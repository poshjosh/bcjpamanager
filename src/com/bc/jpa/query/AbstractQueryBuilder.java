package com.bc.jpa.query;

import com.bc.jpa.DatabaseFormat;
import static com.bc.jpa.query.QueryBuilder.AND;
import static com.bc.jpa.query.QueryBuilder.LIKE;
import static com.bc.jpa.query.QueryBuilder.OR;
import com.bc.util.XLogger;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.QueryHints;

/**
 * @param <T>
 * @author Josh
 */
public abstract class AbstractQueryBuilder<T> implements QueryBuilder<T> {
    
    private final EntityManager em;
    
    private final Class<T> resultType;
    
    private final DatabaseFormat databaseFormat;
        
    private boolean commited;
    
    private Class activeEntityType;
    
    public AbstractQueryBuilder(EntityManager em) {
        this(em, null, null);
    }
    
    public AbstractQueryBuilder(EntityManager em, Class<T> resultType) {
        this(em, resultType, null);
    }
    
    public AbstractQueryBuilder(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        this.em = Objects.requireNonNull(em);
        this.resultType = resultType;
        this.databaseFormat = databaseFormat;
    }
    
    protected abstract QueryBuilder doSelect(Class fromEntityType, Collection<String> cols);
    
    protected abstract QueryBuilder doWhere(Class entityType, String key, 
            QueryBuilder.ComparisonOperator comparisonOperator, Object val, QueryBuilder.Connector connector);

    protected abstract QueryBuilder doConnect(Class entityType, QueryBuilder.Connector connector);
    
    protected abstract QueryBuilder doJoin(Class fromType, String joinColumn, JoinType joinType, Class toType);
    
    protected abstract QueryBuilder doOrderBy(Class entityType, String col, String order);
    
    protected abstract CriteriaQuery<T> doBuild();

    @Override
    public TypedQuery<T> build() {
        
        this.throwExceptionIfCommited();
        
        try{
            
            this.commited = true;

    final XLogger log = XLogger.getInstance();
    final Level level = Level.FINER;
    final Class cls = this.getClass();
    if(log.isLoggable(level, cls)) {
        log.log(level, "#build.\n{0}", cls, this);
    }
            final CriteriaQuery<T> cq = this.doBuild();

            final TypedQuery<T> tq = em.createQuery(cq);
            
            return format(tq);
            
        }finally{
            
            this.clear();
        }
    }
    
    public TypedQuery<T> format(TypedQuery<T> tq) {
        
// http://java-persistence-performance.blogspot.com/2010/08/batch-fetching-optimizing-object-graph.html
// http://java-persistence-performance.blogspot.com/2011/06/how-to-improve-jpa-performance-by-1825.html
//                
        tq.setHint("eclipselink.read-only", "true");
        
// http://vard-lokkur.blogspot.com/2011/05/eclipselink-jpa-queries-optimization.html 

        Set<Class> entityTypes = this.getEntityTypes();
        
        boolean added = false;
        for(Class entityType:entityTypes) {
            
            final String ch = Character.toString(entityType.getSimpleName().charAt(0)).toLowerCase();
            
            Field [] fields = entityType.getDeclaredFields();
            
            for(Field field:fields) {
                
                if(accept(entityTypes, field)) {
                    
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);

                    if(oneToMany != null) {
                        final String HINT = ch + '.' + field.getName();
XLogger.getInstance().log(Level.FINER, "Entity type: {0}, Hint: {1}", 
        this.getClass(), entityType.getName(), HINT);
                        try{
                            tq.setHint(QueryHints.BATCH, HINT);
                            added = true;
                        }catch(IllegalArgumentException ignore) {
                            XLogger.getInstance().log(Level.WARNING, "{0}", this.getClass(), ignore.toString());
                        }
                    }
                }
            }
        }
        if(added) {
            tq.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
        }
        return tq;
    }
    
    public boolean accept(Set<Class> classes, Field field) {
        boolean accepted;
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if(oneToMany == null) {
            accepted = false;
        }else{
            accepted = false;
            final Type type = field.getGenericType();
// Format:  java.util.XXX<Collection-Element-Type> e.g:  java.util.List<com.looseboxes.pu.entities.Productvariant>
            final String sval = type.toString();
            for(Class cls:classes) {
                if(sval.contains( "<" + cls.getName() + ">")) {
XLogger.getInstance().log(Level.FINER, "Accepted: true, Type: {0}, Class: {1}",
        this.getClass(), sval, cls.getName());
                    accepted = true;
                    break;
                }
            }
        }
        return accepted;
    }

    @Override
    public boolean isOpen() {
        return this.em.isOpen();
    }
    
    @Override
    public void close() {
        this.em.close();
    }
    
    @Override
    public void clear() {
        this.activeEntityType = null;
    }
    
    @Override
    public QueryBuilder forType(Class entityType) {
        this.throwExceptionIfCommited();
        this.activeEntityType = entityType;
        return this;
    }

    @Override
    public QueryBuilder and() {
        this.throwExceptionIfNullActiveEntityType();
        return this.and(activeEntityType);
    }
    
    @Override
    public QueryBuilder and(Class entityType) {
        this.throwExceptionIfCommited();
        return this.connect(entityType, AND);
    }

    @Override
    public QueryBuilder or() {
        this.throwExceptionIfNullActiveEntityType();
        return this.or(activeEntityType);
    }
    
    @Override
    public QueryBuilder or(Class entityType) {
        this.throwExceptionIfCommited();
        return this.connect(entityType, OR);
    }
    
    public QueryBuilder connect(Class entityType, QueryBuilder.Connector connector) {
        
        this.throwExceptionIfCommited();
        
        if(connector == null) {
            throw new NullPointerException();
        }
        
        this.activeEntityType = entityType;
        
        return this.doConnect(entityType, connector);
    }
    
    @Override
    public QueryBuilder search(String query, Collection<String> cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.search(activeEntityType, query, cols);
    }
    
    @Override
    public QueryBuilder search(Class entityType, String query, Collection<String> cols) {
        
        return search(entityType, query, cols.toArray(new String[0]));
    }

    @Override
    public QueryBuilder search(String query, String... cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.search(activeEntityType, query, cols);
    }
    
    @Override
    public QueryBuilder search(Class entityType, String query, String... cols) {
        
        this.throwExceptionIfCommited();
        
        if(query == null || cols == null) {
            throw new NullPointerException();
        }
        
        this.activeEntityType = entityType;
        
        if(cols.length == 0) {
            throw new UnsupportedOperationException("Attempting to search through an empty list of columns");
        }
        
        final String TEXT_TO_FIND = '%' + query + '%';
        
        for(String col:cols) {
            
            this.where(entityType, col, LIKE, TEXT_TO_FIND, OR);
        }
        
        return this;
    }
    
    @Override
    public QueryBuilder where(Map parameters) {
        this.throwExceptionIfNullActiveEntityType();
        return where(activeEntityType, parameters);
    }
    
    @Override
    public QueryBuilder where(Class entityType, Map parameters) {
        this.throwExceptionIfCommited();
        Set keys = parameters.keySet();
        for(Object key:keys) {
            this.where(entityType, key.toString(), parameters.get(key));
        }
        return this;
    }

    @Override
    public QueryBuilder where(String key, Object val) {
        this.throwExceptionIfNullActiveEntityType();
        return where(activeEntityType, key, val);
    }
    
    @Override
    public QueryBuilder where(Class entityType, String key, Object val) {
        this.throwExceptionIfCommited();
        return this.where(entityType, key, QueryBuilder.ComparisonOperator.EQUALS, val);
    }

    @Override
    public QueryBuilder where(String key, 
            QueryBuilder.ComparisonOperator comparisonOperator, Object val) {
        this.throwExceptionIfNullActiveEntityType();
        return this.where(activeEntityType, key, comparisonOperator, val);
    }
    
    @Override
    public QueryBuilder where(Class entityType, String key, 
            QueryBuilder.ComparisonOperator comparisonOperator, Object val) {
        this.throwExceptionIfCommited();
        return this.where(entityType, key, comparisonOperator, val, QueryBuilder.Connector.AND);
    }

    @Override
    public QueryBuilder where(String key, 
            QueryBuilder.ComparisonOperator comparisonOperator, Object val, QueryBuilder.Connector connector) {
        this.throwExceptionIfNullActiveEntityType();
        return this.where(activeEntityType, key, comparisonOperator, val, connector);
    }
    
    @Override
    public QueryBuilder where(Class entityType, String key, 
            QueryBuilder.ComparisonOperator comparisonOperator, Object val, QueryBuilder.Connector connector) {
        
        this.throwExceptionIfCommited();
        
        if(key == null || comparisonOperator == null) {
            throw new NullPointerException();
        }
        
        this.activeEntityType = entityType;
        
        return this.doWhere(entityType, key, comparisonOperator, val, connector);
    }
    
    @Override
    public QueryBuilder select(String... cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.select(activeEntityType, cols);
    }
    
    @Override
    public QueryBuilder select(Class fromEntityType, String... cols) {
        this.throwExceptionIfCommited();
        return this.select(fromEntityType, Arrays.asList(cols));
    }

    @Override
    public QueryBuilder select(Collection<String> cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.select(activeEntityType, cols);
    }
    
    @Override
    public QueryBuilder select(Class fromEntityType, Collection<String> cols) {
        
        this.throwExceptionIfCommited();
        
        if(fromEntityType == null || cols == null) {
            throw new NullPointerException();
        }
        
        if(cols.isEmpty()) {
            throw new UnsupportedOperationException("Attempting to select an empty list of columns");
        }
        
        this.activeEntityType = fromEntityType;
        
        return this.doSelect(fromEntityType, cols);
    }

    @Override
    public QueryBuilder join(String joinColumn, Class toType) {
        this.throwExceptionIfNullActiveEntityType();
        return this.join(activeEntityType, joinColumn, toType);
    }
    
    @Override
    public QueryBuilder join(Class fromType, String joinColumn, Class toType) {
        this.throwExceptionIfCommited();
        return this.join(fromType, joinColumn, JoinType.INNER, toType);
    }

    @Override
    public QueryBuilder joins(JoinType joinType, Map<String, Class> joins) {
        this.throwExceptionIfNullActiveEntityType();
        return this.joins(activeEntityType, joinType, joins);
    }
    
    @Override
    public QueryBuilder joins(Class fromType, JoinType joinType, Map<String, Class> joins) {
        
        this.throwExceptionIfCommited();
        
        Set<String> keys = joins.keySet();
        for(String key:keys) {
            Class joinEntityType = joins.get(key);
            this.join(fromType, key, joinType, joinEntityType);
        }
        return this;
    }

    @Override
    public QueryBuilder join(String joinColumn, JoinType joinType, Class toType) {
        this.throwExceptionIfNullActiveEntityType();
        return this.join(activeEntityType, joinColumn, joinType, toType);
    }
    
    @Override
    public QueryBuilder join(Class fromType, String joinColumn, JoinType joinType, Class toType) {
        
        this.throwExceptionIfCommited();
        
        if(joinColumn == null || joinType == null || toType == null) {
            throw new NullPointerException();
        }
        
        this.activeEntityType = fromType;
        
        return this.doJoin(fromType, joinColumn, joinType, toType);
    }

    @Override
    public QueryBuilder descOrder(String col) {
        this.throwExceptionIfNullActiveEntityType();
        return this.descOrder(activeEntityType, col);
    }
    
    @Override
    public QueryBuilder descOrder(Class entityType, String col) {
        this.throwExceptionIfCommited();
        return this.orders(entityType, "DESC", col);
    }

    @Override
    public QueryBuilder descOrders(String... cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.descOrders(activeEntityType, cols);
    }
    
    @Override
    public QueryBuilder descOrders(Class entityType, String... cols) {
        this.throwExceptionIfCommited();
        return this.orders(entityType, "DESC", cols);
    }

    @Override
    public QueryBuilder descOrders(Collection<String> cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.descOrders(activeEntityType, cols);
    }
    
    @Override
    public QueryBuilder descOrders(Class entityType, Collection<String> cols) {
        this.throwExceptionIfCommited();
        return this.orders(entityType, "DESC", cols.toArray(new String[0]));
    }
    @Override
    public QueryBuilder ascOrder(String col) {
        this.throwExceptionIfNullActiveEntityType();
        return this.ascOrder(activeEntityType, col);
    }

    @Override
    public QueryBuilder ascOrder(Class entityType, String col) {
        this.throwExceptionIfCommited();
        return this.orders(entityType, "ASC", col);
    }

    @Override
    public QueryBuilder ascOrders(String... cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.ascOrders(activeEntityType, cols);
    }
    
    @Override
    public QueryBuilder ascOrders(Class entityType, String... cols) {
        this.throwExceptionIfCommited();
        return this.orders(entityType, "ASC", cols);
    }

    @Override
    public QueryBuilder ascOrders(Collection<String> cols) {
        this.throwExceptionIfNullActiveEntityType();
        return this.ascOrders(activeEntityType, cols);
    }
    
    @Override
    public QueryBuilder ascOrders(Class entityType, Collection<String> cols) {
        this.throwExceptionIfCommited();
        return this.orders(entityType, "ASC", cols.toArray(new String[0]));
    }
    
    public QueryBuilder orders(Class entityType, String order, String... cols) {
        
        this.throwExceptionIfCommited();
        
        Map orderMappings = new LinkedHashMap(cols.length, 1.0f);
        for(String col:cols) {
            orderMappings.put(col, order);
        }
        return this.orderBy(entityType, orderMappings);
    }

    @Override
    public QueryBuilder orderBy(Map<String, String> orders) {
        this.throwExceptionIfNullActiveEntityType();
        return this.orderBy(activeEntityType, orders);
    }
    
    @Override
    public QueryBuilder orderBy(Class entityType, Map<String, String> orders) {
        this.throwExceptionIfCommited();
        Set<String> keys = orders.keySet();
        for(String column:keys) {
            this.orderBy(entityType, column, orders.get(column));
        }
        return this;
    }

    @Override
    public QueryBuilder orderBy(String col, String order) {
        this.throwExceptionIfNullActiveEntityType();
        return this.orderBy(activeEntityType, col, order);
    }
    
    @Override
    public QueryBuilder orderBy(Class entityType, String col, String order) {

        this.throwExceptionIfCommited();
        
        if(col == null || order == null) {
            throw new NullPointerException();
        }
        
        this.activeEntityType = entityType;
        
        return this.doOrderBy(entityType, col, order);
    }

    public Predicate buildPredicate(
            CriteriaBuilder cb, Class entityType, From from, String col, QueryBuilder.ComparisonOperator comparisonOperator, List list) {
         
        return this.buildPredicate(cb, entityType, from, col, comparisonOperator, list.toArray(new Object[0]));
    }
    
    public Predicate buildPredicate(
            CriteriaBuilder cb, Class entityType, From from, String col, QueryBuilder.ComparisonOperator comparisonOperator, Object [] arr) {
            
        Predicate [] predicates = new Predicate[arr.length];

        int j = 0;

        for(Object e:arr) {

            Predicate predicate = this.buildPredicate(cb, entityType, from, col, comparisonOperator, e);

            // We know this has only one element
            predicates[j++] = predicate;
        }

        // Has to be OR
        // 
        return this.buildPredicate(cb, QueryBuilder.Connector.OR, predicates);
    }
    
    public Predicate buildPredicate(CriteriaBuilder cb, Class entityType, From from, 
            String col, QueryBuilder.ComparisonOperator comparisonOperator, Object val) {
        
XLogger.getInstance().log(Level.FINER, "#buildPredicate. Column: {0}, comparisonOperator: {1}",
this.getClass(), col, comparisonOperator);

        if(this.databaseFormat != null) {
            val = this.databaseFormat.getDatabaseValue(entityType, col, val, val);
        }
        
        Predicate predicate;
        
        if(val == null) {
            
            predicate = cb.isNull(from.get(col));
            
        }else{
            
            switch(comparisonOperator) {
                case EQUALS:
                    predicate = cb.equal(from.get(col), val); 
                    break;
                case LIKE:    
                    predicate = cb.like(from.get(col), val.toString()); 
                    break;
                case GREATER_OR_EQUALS:
                    predicate = cb.greaterThanOrEqualTo(from.get(col), (Comparable)val); 
                    break;
                case GREATER_THAN:
                    predicate = cb.greaterThan(from.get(col), (Comparable)val); 
                    break;
                case LESS_OR_EQUALS:
                    predicate = cb.lessThanOrEqualTo(from.get(col), (Comparable)val); 
                    break;
                case LESS_THAN:
                    predicate = cb.lessThan(from.get(col), (Comparable)val); 
                    break;
                default:    
                    throw new UnsupportedOperationException("Unexpected query connector: '"+comparisonOperator+"' Only '=' and 'LIKE' are currently supported");                        
            }
        }
        
        return predicate;
    }
        
    public Predicate buildPredicate(CriteriaBuilder cb, Predicate p0, QueryBuilder.Connector connector, Predicate p1) {
        
        Predicate predicate;
        
        switch(connector) {
            case OR:
                predicate = cb.or(p0, p1); 
                break; 
            case AND:
                predicate = cb.and(p0, p1); 
                break; 
            default:
                throw new UnsupportedOperationException("Unexpected query connector: '"+connector+"' Only 'OR' and 'AND' are currently supported");
        }        

        return predicate;
    }
    
    public Predicate buildPredicate(CriteriaBuilder cb, QueryBuilder.Connector connector, Predicate ...predicates) {
        
        Predicate predicate;
        
        if(predicates != null && predicates.length != 0) {
        
            switch(connector) {
                case OR:
                    predicate = cb.or(predicates); 
                    break; 
                case AND:
                    predicate = cb.and(predicates); 
                    break; 
                default:
                    throw new UnsupportedOperationException("Unexpected query connector: '"+connector+"' Only 'OR' and 'AND' are currently supported");
            }
        }else{
            
            predicate = null;
        }
        
        return predicate;
    }
    
    private void throwExceptionIfCommited() {
        if(this.isCommited()) {
            throw new IllegalStateException("Operation not allowed if "+QueryBuilder.class.getSimpleName()+" is committed");
        }
    }
    
    private void throwExceptionIfNullActiveEntityType() {
        this.throwExceptionIfNull(activeEntityType, "activeEntityType == null");
    }
    
    private void throwExceptionIfNull(Object o, String msg) {
        if(o == null) {
            throw new NullPointerException(msg);
        }
    }

    protected final EntityManager getEntityManager() {
        return em;
    }

    protected final DatabaseFormat getDatabaseFormat() {
        return databaseFormat;
    }
    
    @Override
    public final boolean isCommited() {
        return commited;
    }

    @Override
    public final Class getResultType() {
        return resultType;
    }
}

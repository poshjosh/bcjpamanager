package com.bc.jpa.query;

import com.bc.jpa.DatabaseFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 * @param <T>
 * @author Josh
 */
public class QueryBuilderImpl2<T> extends AbstractQueryBuilder<T> {
    
    private final CriteriaBuilder cb;
        
    private final CriteriaQuery cq;
    
    private final Map<Class, Root> roots = new LinkedHashMap();
    
    private Predicate where;
    
    private Connector nextConnector;
    
    private Map<Class, Join> joins;
    
    public QueryBuilderImpl2(EntityManager em) {
        this(em, null, null);
    }
    
    public QueryBuilderImpl2(EntityManager em, Class<T> resultType) {
        this(em, resultType, null);
    }
    
    public QueryBuilderImpl2(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
        this.cb = em.getCriteriaBuilder();
        if(resultType == null) {
            this.cq = cb.createQuery();
        }else{
            this.cq = cb.createQuery(resultType);
        }        
    }
    
    @Override
    public void clear() {
        super.clear();
        this.roots.clear();
        this.where = null;
        this.nextConnector = null;
        this.joins = null;
    }
    
    @Override
    protected QueryBuilder doConnect(Class entityType, QueryBuilder.Connector connector) {
        
        this.nextConnector = connector;
        
        return this;
    }

    @Override
    protected QueryBuilder doWhere(Class entityType, String key, 
            QueryBuilder.ComparisonOperator comparisonOperator, Object val, QueryBuilder.Connector connector) {
        
        Root root = this.getRoot(entityType, true);
        
        Predicate predicate = this.buildPredicate(cb, entityType, root, key, comparisonOperator, val);
        
        if(where == null) {
            where = predicate;
        }else{
            where = this.buildPredicate(cb, where, nextConnector, predicate);
        }

        nextConnector = connector;
        
        return this;
    }
    
    @Override
    protected QueryBuilder doSelect(Class fromEntityType, Collection<String> cols) {
        
        Root root = this.getRoot(fromEntityType, true);
        
        if(cols.size() == 1) {
            final String column = cols.iterator().next();
            final Selection selection = root.get(column);
            if(selection == null) {
                throw new IllegalStateException("Column in columns list: "+column+", not found int type: "+fromEntityType.getName());
            }
            cq.select(selection);
        }else{
            final Selection [] selections = new Selection[cols.size()];
            final Iterator<String> iter = cols.iterator();
            for(int i=0; iter.hasNext(); i++) {
                String column = iter.next();
                Selection selection = root.get(column);
                if(selection == null) {
                    throw new IllegalStateException("Column in columns list: "+column+", not found int type: "+fromEntityType.getName());
                }
                selections[i] = selection;
            }
            cq.multiselect(selections);
        }
        
        return this;
    }

    @Override
    protected QueryBuilder doJoin(Class fromType, String joinColumn, JoinType joinType, Class toType) {
        
        Root root = this.getRoot(fromType, true);
        
        Join join = root.join(joinColumn, joinType);
        
        if(this.joins == null) {
            this.joins = new LinkedHashMap();
        }
        
        this.joins.put(toType, join);
        
        return this;
    }

    @Override
    protected QueryBuilder doOrderBy(Class entityType, String col, final String orderString) {

        Root root = this.getRoot(entityType, true);
        
        Order order;
        if(orderString.equalsIgnoreCase("DESC")) {
            order = cb.desc(root.get(col));
        }else if(orderString.equalsIgnoreCase("ASC")) {
            order = cb.asc(root.get(col));
        }else{
            throw new UnsupportedOperationException("Unexpected order value: "+orderString+", only values: DESC and ASC are supported");
        }
        
        cq.orderBy(order);
        
        return this;
    }

    @Override
    protected CriteriaQuery<T> doBuild() {
        
        if(where != null) {

            cq.where(where);
        }
        
        return cq;
    }

    public Root getRoot(Class entityClass, boolean create) {
        
        Root root = roots.get(entityClass);
        
        if(root == null) {
            if(create) {
                
                root = cq.from(entityClass);
                roots.put(entityClass, root);
                
            }else{
                
                throw new IllegalStateException("No selections were made for type: "+entityClass.getName());
            }
        }
        
        return root;
    }
    
    @Override
    public final Set<Class> getEntityTypes() {
        return new HashSet(roots.keySet());
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() +  "{\n  commited=" + this.isCommited() + ", resultType=" + this.getResultType() + ", where=" + where + ", nextConnector=" + nextConnector + "\n  roots=" + roots + "\n  joins=" + joins + '}';
    }
}

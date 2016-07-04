package com.bc.jpa.query;

import com.bc.jpa.DatabaseFormat;
import com.bc.util.XLogger;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
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
public class QueryBuilderImpl<T> extends AbstractQueryBuilder<T> {
    
    private Map<Class, List<String>> selectColumns;
    
    private Map<Class, List<WherePart>> whereParts;
    
    private Map<Class, List<JoinData>> joinData;
            
    private Map<Class, Map<String, String>> orders;  
    
    public static interface WherePart {
        String getColumn();
        ComparisonOperator getComparisonOperator();
        Object getValue();
        Connector getConnector();
    }
    
    public static interface ConnectedPredicate{
        Predicate getPredicate();
        Connector getConnector();
    }
    
    public static interface JoinData {
        String getColumn();
        JoinType getJoinType();
        Class getEntityType();
    }

    public QueryBuilderImpl(EntityManager em) {
        super(em);
    }
    
    public QueryBuilderImpl(EntityManager em, Class<T> resultType) {
        super(em, resultType);
    }
    
    public QueryBuilderImpl(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
    }
    
    @Override
    public void clear() {
        super.clear();
        this.joinData = null;
        this.orders = null;
        this.selectColumns = null;
        this.whereParts = null;
    }
    
    @Override
    protected QueryBuilder doConnect(Class entityType, Connector connector) {
        
        List<WherePart> entityWhereParts = this.get(this.whereParts, entityType, false);
        
        if(entityWhereParts != null && !entityWhereParts.isEmpty()) {
            
            final int index = entityWhereParts.size() - 1;
                    
            WherePart wherePart = entityWhereParts.get(index);
            
            WherePart withConnectorUpdated = new WherePartImpl(
                    wherePart.getColumn(), wherePart.getComparisonOperator(), 
                    wherePart.getValue(), connector);
            
            entityWhereParts.set(index, withConnectorUpdated);
            
        }else{
            
            throw new IllegalStateException("A where clause needs to be defined before adding a connector");
        }
        
        return this;
    }

    @Override
    protected QueryBuilder doWhere(Class entityType, String key, 
            ComparisonOperator comparisonOperator, Object val, Connector connector) {
        
        WherePart wherePart = new WherePartImpl(key, comparisonOperator, val, connector);
        
        this.whereParts = this.add(this.whereParts, entityType, true, wherePart);
        
        return this;
    }
    
    @Override
    protected QueryBuilder doSelect(Class fromEntityType, Collection<String> cols) {
        
        this.selectColumns = this.addAll(this.selectColumns, fromEntityType, true, cols);
        
        return this;
    }

    @Override
    public QueryBuilder doJoin(Class fromType, String joinColumn, JoinType joinType, Class toType) {
        
        JoinData singleJoinData = new JoinDataImpl(joinColumn, joinType, toType);
        
        this.joinData = this.add(this.joinData, fromType, true, singleJoinData);
        
        return this;
    }

    @Override
    protected QueryBuilder doOrderBy(Class entityType, String col, String order) {
        
        if(this.orders == null) {
            this.orders = new LinkedHashMap();
        }
        
        Map<String, String> entityOrders = this.orders.get(entityType);
        if(entityOrders == null) {
            entityOrders = new LinkedHashMap();
            this.orders.put(entityType, entityOrders);
        }
        entityOrders.put(col, order);
        
        return this;
    }

    @Override
    protected CriteriaQuery<T> doBuild() {
        
        final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

        final CriteriaQuery cq;
        
        final Class<T> resultType = this.getResultType();

        if(resultType == null) {

            cq = cb.createQuery();

        }else{

            cq = cb.createQuery(resultType);
        }

        final Map<Class, From> roots = Collections.unmodifiableMap(this.buildRoots(cq));

        this.buildSelections(cq, roots, this.selectColumns);

        final ConnectedPredicate rootWhere = this.buildPredicate(cb, roots, this.whereParts);

        final Map<Class, From> joinMappings = this.buildJoins(roots, this.joinData);

        final ConnectedPredicate joinWhere = this.buildPredicate(cb, joinMappings, this.whereParts);

        final Predicate combinedWhere;
        if(rootWhere == null && joinWhere != null) {
            combinedWhere = joinWhere.getPredicate();
        }else if(rootWhere != null && joinWhere == null) {
            combinedWhere = rootWhere.getPredicate();
        }else if(rootWhere != null && joinWhere != null) {
            combinedWhere = this.buildPredicate(cb, rootWhere.getPredicate(), rootWhere.getConnector(), joinWhere.getPredicate());
        }else{
            combinedWhere = null;
        }

        if(combinedWhere != null) {

            cq.where(combinedWhere);
        }

        List<Order> ordersBuffer = new LinkedList();

        this.buildOrderBy(cb, roots, orders, ordersBuffer);

        this.buildOrderBy(cb, joinMappings, orders, ordersBuffer);

        if(!ordersBuffer.isEmpty()) {

            cq.orderBy(ordersBuffer.toArray(new Order[0]));
        }
        
        return cq;
    }

    public Map<Class, From> buildRoots(CriteriaQuery cq) {
        
        final Set<Class> entityTypes = this.getEntityTypes();
        
        if(entityTypes == null || entityTypes.isEmpty()) {
            throw new IllegalStateException("No selections were made");
        }
        
        final Map<Class, From> buildTo = new LinkedHashMap();
        
        for(Class selectionEntity:entityTypes) {

            if(!buildTo.containsKey(selectionEntity)) {
                
                Root entityRoot = cq.from(selectionEntity);

                buildTo.put(selectionEntity, entityRoot);
            }
        }
        
        return buildTo;
    }
    
    public CriteriaQuery buildSelections(
            CriteriaQuery cq, Map<Class, From> allRoots, Map<Class, List<String>> allSelectColumns) {
        
        if(allSelectColumns != null && !allSelectColumns.isEmpty()) {
            
            Set<Class> entityTypes = allSelectColumns.keySet();

            for(Class selectionEntity:entityTypes) {

                From entityRoot = allRoots.get(selectionEntity);
                
                List<String> entitySelectCols = allSelectColumns.get(selectionEntity);

                cq = this.buildSelections(cq, entityRoot, entitySelectCols);
            }
        }
        
        return cq;
    }
    
    public CriteriaQuery buildSelections(CriteriaQuery cq, From root, List<String> selectColumns) {
        int selectColsSize;
        if(selectColumns != null && (selectColsSize = selectColumns.size()) > 0) {
            if(selectColsSize == 1) {
                Selection selection = root.get(selectColumns.get(0));
                cq = cq.select(selection);
            }else if(selectColsSize > 1) {
                List<Selection> selections = new LinkedList<>();
                for(String selectColumn:selectColumns) {
                    selections.add(root.get(selectColumn));
                }
                cq = cq.multiselect(selections);   
            }
        }
        return cq;
    }
    
    public Map<Class, From> buildJoins(Map<Class, From> allRoots, Map<Class, List<JoinData>> allJoins) {
        
        Map<Class, From> joinMappings;
        
        if(allJoins != null && !allJoins.isEmpty()) {
            
            joinMappings = new HashMap(allJoins.size(), 1.0f);
            
            Set<Class> keys = allRoots.keySet();
        
            for(Class rootEntityType:keys) {

                List<JoinData> entityJoins = allJoins.get(rootEntityType);

                if(entityJoins != null && !entityJoins.isEmpty()) {

                    From root = allRoots.get(rootEntityType);

                    this.buildJoins(root, entityJoins, joinMappings);
                }
            }
        }else{
            
            joinMappings = Collections.EMPTY_MAP;
        }
        
        return joinMappings;
    }
    
    public void buildJoins(From root, List<JoinData> allJoins, Map<Class, From> buildTo) {
        
        if(allJoins != null && !allJoins.isEmpty()) {
            
            for(JoinData singleJoinData:allJoins) {

                String joinColumn = singleJoinData.getColumn();
                JoinType joinType = singleJoinData.getJoinType();
                Class joinEntityType = singleJoinData.getEntityType();
                
XLogger.getInstance().log(Level.FINER, "{0} {1} ON {2}", 
this.getClass(), joinType, joinColumn, joinEntityType.getName());

                Join join = root.join(singleJoinData.getColumn(), singleJoinData.getJoinType());
                
                buildTo.put(joinEntityType, join);
            }
        }
    }
    
    public void buildOrderBy(CriteriaBuilder cb, Map<Class, From> froms, 
            Map<Class, Map<String, String>> orders, List<Order> buildTo) {
        
        if(froms != null && !froms.isEmpty() &&
                orders != null && !orders.isEmpty()) {
            
            Set<Class> keys = froms.keySet();

            for(Class fromEntityType:keys) {

                From from = froms.get(fromEntityType);

                Map<String, String> entityOrders = orders.get(fromEntityType);

                this.buildOrderBy(cb, from, entityOrders, buildTo);
            }
        }
    }
    
    public void buildOrderBy(CriteriaBuilder cb, From entityRoot, 
            Map<String, String> entityOrders, List<Order> buildTo) {
        
        if(entityOrders != null && !entityOrders.isEmpty()) {
            
            Set<String> keys = entityOrders.keySet();

            for(String key:keys) {
                String val = entityOrders.get(key);
                Order order;
                if(val.equalsIgnoreCase("DESC")) {
                    order = cb.desc(entityRoot.get(key)); 
                }else if(val.equalsIgnoreCase("ASC")) {
                    order = cb.asc(entityRoot.get(key)); 
                }else{
                    throw new UnsupportedOperationException("Unexpected order value: "+val+", only values: DESC and ASC are supported");
                }
                buildTo.add(order);
            }
        }
    }
    
    public ConnectedPredicate buildPredicate(
            CriteriaBuilder cb, Map<Class, From> froms, Map<Class, List<WherePart>> allWhereParts) {
        
        Predicate cummulative = null;

        Connector connector = null;

        if(allWhereParts != null && !allWhereParts.isEmpty() &&
                froms != null && !froms.isEmpty()) {
            
            Set<Class> keys = froms.keySet();

            for(Class rootEntityType:keys) {

                From entityRoot = froms.get(rootEntityType);

                List<WherePart> entityWhereParts = allWhereParts.get(rootEntityType);
    XLogger.getInstance().log(Level.FINER, "Building predicate for {0}", this.getClass(), rootEntityType.getName());

                Predicate current = this.buildPredicate(cb, rootEntityType, entityRoot, entityWhereParts);

                if(current != null) {

                    connector = this.getLastConnector(entityWhereParts);

                    if(cummulative == null) {

                        cummulative = current;

                    }else{

                        cummulative = this.buildPredicate(cb, cummulative, connector, current);
                    }
                }
            }
        }
        
        return cummulative == null || connector == null ? null :
                new ConnectedPredicateImpl(cummulative, connector);
    }
    
    public Connector getLastConnector(List<WherePart> whereParts) {
        
        if(whereParts == null || whereParts.isEmpty()) {
            
            return Connector.AND;
            
        }else{
            
            return whereParts.get(whereParts.size() - 1).getConnector();
        }
    }

    public Predicate buildPredicate(
            CriteriaBuilder cb, Class entityType, From from, List<WherePart> wherePartList) {
        
        Predicate cummulative = null;
        
        if(wherePartList != null && !wherePartList.isEmpty()) {
        
            WherePart previousWherePart = null;
            
            for(WherePart wherePart:wherePartList) {
                
                String col = wherePart.getColumn();
                ComparisonOperator comparisonOperator = wherePart.getComparisonOperator();
                Object val = wherePart.getValue();
                
                Predicate predicate = this.buildPredicate(cb, entityType, from, col, comparisonOperator, val);
                
                if(cummulative != null && previousWherePart != null) {
                    
                    final Connector connector = previousWherePart.getConnector();
                    
                    switch(connector) {
                        
                        case AND:
                            cummulative = cb.and(cummulative, predicate); break;
                        case OR:
                            cummulative = cb.or(cummulative, predicate); break;
                        default:
                            throw new UnsupportedOperationException(
                            "Unexpected query connector: '" + connector + 
                            "', permitted values: "+Arrays.toString(Connector.values()));
                    }
                }else{
                    
                    cummulative = predicate;
                }
                
                previousWherePart = wherePart;
            }
        }
        
        return cummulative;
    }
    
    private <V> Map<Class, List<V>> addAll(
            Map<Class, List<V>> allValues, Class entityType, 
            boolean createIfNone, Collection<V> toAdd) {
        
        if(allValues == null && createIfNone) {
            allValues = new LinkedHashMap();
        }
        
        List<V> entityValues = this.get(allValues, entityType, createIfNone);
        
        if(entityValues != null) {
            
            entityValues.addAll(toAdd);
        }
        
        return allValues;
    }
    
    private <V> Map<Class, List<V>> add(
            Map<Class, List<V>> allValues, Class entityType, 
            boolean createIfNone, V toAdd) {
        
        if(allValues == null && createIfNone) {
            allValues = new LinkedHashMap();
        }
        
        List<V> entityValues = this.get(allValues, entityType, createIfNone);
        
        if(entityValues != null) {
            
            entityValues.add(toAdd);
        }
        
        return allValues;
    }
    
    private <V> List<V> get(Map<Class, List<V>> allValues, Class entityType, boolean createIfNone) {
        List<V> entityValues = allValues == null || allValues.isEmpty() ? null : allValues.get(entityType);
        if(entityValues == null && createIfNone) {
            entityValues = new LinkedList();
            allValues.put(entityType, entityValues);
        }
        return entityValues;
    }
    
    @Override
    public final Set<Class> getEntityTypes() {
        Set<Class> entityTypes = new LinkedHashSet();
        this.addEntityTypes(this.selectColumns, entityTypes);
        this.addEntityTypes(this.whereParts, entityTypes);
        this.addEntityTypes(this.joinData, entityTypes);
        this.addEntityTypes(this.orders, entityTypes);
        return entityTypes;
    }
    
    private void addEntityTypes(Map<Class, ?> map, Set<Class> entityTypes) {
        if(map != null && !map.isEmpty()) {
            entityTypes.addAll(map.keySet());
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{\n  commited=" + this.isCommited() + ", resultType=" + this.getResultType() + "\n  selectColumns=" + selectColumns + "\n  whereParts=" + whereParts + "\n  joinData=" + joinData + "\n  orders=" + orders + '}';
    }

    private static class WherePartImpl implements WherePart, Serializable {
        private final String column;
        private final ComparisonOperator comparisonOperator;
        private final Object value;
        private final Connector connector;
        public WherePartImpl(String column, Object value) {
            this(column, QueryBuilder.ComparisonOperator.EQUALS, value, QueryBuilder.Connector.AND);
        }
        public WherePartImpl(String column, ComparisonOperator comparisonOperator, Object value, Connector connector) {
            this.column = column;
            this.comparisonOperator = comparisonOperator;
            this.value = value;
            this.connector = connector;
        }
        @Override
        public final String getColumn() {
            return column;
        }
        @Override
        public final ComparisonOperator getComparisonOperator() {
            return comparisonOperator;
        }
        @Override
        public final Object getValue() {
            return value;
        }
        @Override
        public final Connector getConnector() {
            return connector;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.column);
            hash = 71 * hash + Objects.hashCode(this.comparisonOperator);
            hash = 71 * hash + Objects.hashCode(this.value);
            hash = 71 * hash + Objects.hashCode(this.connector);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final WherePartImpl other = (WherePartImpl) obj;
            if (!Objects.equals(this.column, other.column)) {
                return false;
            }
            if (this.comparisonOperator != other.comparisonOperator) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            if (this.connector != other.connector) {
                return false;
            }
            return true;
        }
        @Override
        public String toString() {
            return "WherePartImpl{" + column + ' ' + comparisonOperator + ' ' + truncate(value, 50) + ' ' + connector + '}';
        }
        private Object truncate(Object value, int maxLen) {
            if(value == null){
                return value;
            }
            try{
                String sval = ((String)value);
                if(sval.length() > maxLen-3) {
                    value = sval.substring(0, maxLen-3) + "...";
                }
            }catch(ClassCastException e) { }
            return value;
        }
    }
    
    private static class ConnectedPredicateImpl implements ConnectedPredicate, Serializable {
        private final Predicate predicate;
        private final Connector connector;
        public ConnectedPredicateImpl(Predicate predicate, Connector connector) {
            this.predicate = predicate;
            this.connector = connector;
        }
        @Override
        public final Predicate getPredicate() {
            return predicate;
        }
        @Override
        public final Connector getConnector() {
            return connector;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 19 * hash + Objects.hashCode(this.predicate);
            hash = 19 * hash + Objects.hashCode(this.connector);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ConnectedPredicateImpl other = (ConnectedPredicateImpl) obj;
            if (!Objects.equals(this.predicate, other.predicate)) {
                return false;
            }
            if (this.connector != other.connector) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ConnectedPredicateImpl{" + "predicate=" + predicate + ", connector=" + connector + '}';
        }
    }

    private static class JoinDataImpl implements JoinData, Serializable {
        private final String column;
        private final JoinType joinType;
        private final Class entityType;
        public JoinDataImpl(String column, Class entityType) {
            this(column, JoinType.INNER, entityType);
        }
        public JoinDataImpl(String column, JoinType joinType, Class entityType) {
            this.column = column;
            this.joinType = joinType;
            this.entityType = entityType;
        }
        @Override
        public String getColumn() {
            return column;
        }
        @Override
        public JoinType getJoinType() {
            return joinType;
        }
        @Override
        public Class getEntityType() {
            return entityType;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.column);
            hash = 97 * hash + Objects.hashCode(this.joinType);
            hash = 97 * hash + Objects.hashCode(this.entityType);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final JoinDataImpl other = (JoinDataImpl) obj;
            if (!Objects.equals(this.column, other.column)) {
                return false;
            }
            if (this.joinType != other.joinType) {
                return false;
            }
            if (!Objects.equals(this.entityType, other.entityType)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "JoinDataImpl{" + "column=" + column + ", joinType=" + joinType + ", entityType=" + entityType + '}';
        }
    }
}

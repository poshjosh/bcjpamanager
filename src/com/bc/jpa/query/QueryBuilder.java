package com.bc.jpa.query;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.JoinType;

/**
 * @param <T>
 * @author Josh
 */
public interface QueryBuilder<T> extends AutoCloseable {
    
    public static enum ComparisonOperator{
        EQUALS, LIKE, GREATER_THAN, GREATER_OR_EQUALS, LESS_THAN, LESS_OR_EQUALS
    };
    public static enum Connector{AND, OR};
    
    ComparisonOperator EQUALS = ComparisonOperator.EQUALS;
    ComparisonOperator LIKE = ComparisonOperator.LIKE;
    ComparisonOperator GREATER_THAN = ComparisonOperator.GREATER_THAN;
    ComparisonOperator GREATER_OR_EQUALS = ComparisonOperator.GREATER_OR_EQUALS;
    ComparisonOperator LESS_THAN = ComparisonOperator.LESS_THAN;
    ComparisonOperator LESS_OR_EQUALS = ComparisonOperator.LESS_OR_EQUALS;
    Connector AND = Connector.AND;
    Connector OR = Connector.OR;
    
    boolean isOpen();
    
    @Override
    void close();
    
    boolean isCommited();
    
    void clear();
    
    Set<Class> getEntityTypes();
    
    Class<T> getResultType();
    
    QueryBuilder<T> forType(Class entityType);
    
    /**
     * @see #and(java.lang.Class) 
     * @return This instance
     */
    QueryBuilder<T> and();
    
    /**
     * Switches the last connector for the specified entity type to AND
     * @param entityType The target entity type reference
     * @return This instance
     */
    QueryBuilder<T> and(Class entityType);

    /**
     * @see #or(java.lang.Class) 
     * @return This instance
     */
    QueryBuilder<T> or();
    
    /**
     * Switches the last connector for the specified entity type to OR
     * @param entityType The target entity type reference
     * @return This instance
     */
    QueryBuilder<T> or(Class entityType);
    
    QueryBuilder<T> search(String query, Collection<String> cols);
    
    QueryBuilder<T> search(Class entityType, String query, Collection<String> cols);
    
    QueryBuilder<T> search(String query, String... cols);    
    
    QueryBuilder<T> search(Class entityType, String query, String... cols);

    QueryBuilder<T> where(Map parameters);
    
    QueryBuilder<T> where(Class entityType, Map parameters);

    QueryBuilder<T> where(String col, Object val);
    
    QueryBuilder<T> where(Class entityType, String col, Object val);

    QueryBuilder<T> where(String key, 
            ComparisonOperator comparisonOperator, Object val);
    
    QueryBuilder<T> where(Class entityType, String key, 
            ComparisonOperator comparisonOperator, Object val);
    
    QueryBuilder<T> where(String key, 
            ComparisonOperator comparisonOperator, Object val, Connector connector);
    
    QueryBuilder<T> where(Class entityType, String key, 
            ComparisonOperator comparisonOperator, Object val, Connector connector);

    QueryBuilder<T> select(String... cols);
    
    QueryBuilder<T> select(Class fromType, String... cols);

    QueryBuilder<T> select(Collection<String> cols);
    
    QueryBuilder<T> select(Class fromType, Collection<String> cols);
    
    QueryBuilder<T> join(String joinColumn, Class toType);
    
    QueryBuilder<T> join(Class fromType, String joinColumn, Class toType);

    QueryBuilder<T> join(String joinColumn, JoinType joinType, Class toType);
    
    QueryBuilder<T> join(Class fromType, String joinColumn, JoinType joinType, Class toType);

    QueryBuilder<T> joins(JoinType joinType, Map<String, Class> joins);

    QueryBuilder<T> joins(Class fromType, JoinType joinType, Map<String, Class> joins);
    
    QueryBuilder<T> orderBy(String col, String order);
    
    QueryBuilder<T> orderBy(Class entityType, String col, String order);

    QueryBuilder<T> orderBy(Map<String, String> orders);
    
    QueryBuilder<T> orderBy(Class entityType, Map<String, String> orders);
    
    QueryBuilder<T> descOrder(String col);
    
    QueryBuilder<T> descOrder(Class entityType, String col);

    QueryBuilder<T> descOrders(String... cols);
    
    QueryBuilder<T> descOrders(Class entityType, String... cols);

    QueryBuilder<T> descOrders(Collection<String> cols);
    
    QueryBuilder<T> descOrders(Class entityType, Collection<String> cols);
    
    QueryBuilder<T> ascOrder(String col);
    
    QueryBuilder<T> ascOrder(Class entityType, String col);
    
    QueryBuilder<T> ascOrders(String... cols);
    
    QueryBuilder<T> ascOrders(Class entityType, String... cols);

    QueryBuilder<T> ascOrders(Collection<String> cols);
    
    QueryBuilder<T> ascOrders(Class entityType, Collection<String> cols);

    TypedQuery<T> build();
}

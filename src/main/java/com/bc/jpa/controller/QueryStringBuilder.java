package com.bc.jpa.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * @(#)JPQL.java   27-Jun-2014 18:13:33
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @param <E>
 */
public interface QueryStringBuilder<E> {
    
    String getTableAlias();
    
    void appendColumns(Collection columns, Collection values, String tableAlias, StringBuilder appendTo);

    boolean isUseColumnNamesToSelectAll();
    
    void setUseColumnNamesToSelectAll(boolean b);

    EntityManagerFactory getEntityManagerFactory();

//    List getResultList(Collection selectColumns, Map where, String connector, Map orderBy, int offset, int limit, boolean convertCrossReferences);
    
//    List<E> getResultList(Map where, String connector, Map orderBy, int offset, int limit, boolean convertCrossReferences);
    
//    List getResultList(Collection selectColumns, String column, Object [] values, Map orderBy, int offset, int limit, boolean convertCrossReferences);
    
//    List<E> getResultList(String column, Object [] values, Map orderBy, int offset, int limit, boolean convertCrossReferences);

//    Object getSingleResult(String selectColumn, Map where, String connector, Map orderBy, boolean convertCrossReferences);
    
//    E getSingleResult(Map where, String connector, Map orderBy, boolean convertCrossReferences);
    
//    Object getSingleResult(String selectColumn, String column, Object [] values, Map orderBy, boolean convertCrossReferences);
    
//    E getSingleResult(String column, Object [] values, Map orderBy, boolean convertCrossReferences);
    
    String appendSelectQuery(Collection selectCols, StringBuilder appendTo);

    void appendOrderBy(Map orderBy, String tableAlias, StringBuilder appendTo);

    void appendWhereClause(String WHERE, Set whereColumnNames, String tableAlias, String connector, StringBuilder appendTo);

    void appendWhereClause(String WHERE, String column, Object[] values, String tableAlias, String connector, StringBuilder appendTo);

    void appendWherePair(String col, String tableAlias, StringBuilder appendTo);

    void appendWherePair(String columnName, int columnIndex, String tableAlias, StringBuilder appendTo);
    
    String getDatabaseName();

    Class<E> getEntityClass();

    String getSelectQuery(Collection selectCols, Set whereColumnNames, String connector, Map orderBy);

    String getSelectQuery(Collection selectCols, String column, Object[] values, Map orderBy);
    
    /**
     * Updates the query with the parameters contained in the <tt>whereClauseParameters</tt>
     * @param em Optional. May be null, in which case a new instance is obtained via {@link #getEntityManager()}
     * @param q The query whose parameters will be updated
     * @param whereClauseParameters The parameter_name=parameter_value pairs to use in updating the query
     * @param convertCrossReferences If true cross references will be converted to their entity values 
     */
    void updateQuery(EntityManager em, Query q, Map whereClauseParameters, boolean convertCrossReferences);

    /**
     * Updates the query with the values for the column specified
     * @param em Optional. May be null, in which case a new instance is obtained via {@link #getEntityManager()}
     * @param q The query whose parameters will be updated
     * @param col The column whose values will be updated
     * @param values The column values to be updated
     * @param convertCrossReferences If true cross references will be converted to their entity values 
     */
    void updateQuery(EntityManager em, Query q, String col, Object [] values, boolean convertCrossReferences);
    
    String getTableName();

    String getComparisonOperator();

    void setComparisonOperator(String comparison);
    
    /**
     * @param entityClass
     * @deprecated
     */
    @Deprecated
    void setEntityClass(Class<E> entityClass);

    /**
     * Determines if <tt>null</tt> parameters will be considered in where clauses<br/>
     * If this method returns <tt>true</tt> null parameters will be considered
     * in where clauses, otherwise null parameters will not be considered.<br/>
     * By default, in MySQL NULL != NULL. so a query having <tt>where street = null</tt>
     * will return 0 results even if a column where street is null was found.
     * @return <tt>true</tt> if nulls should be searched <tt>false</tt> otherwise
     */
    boolean isSearchNulls();

    /**
     * @param searchNulls If <tt>true</tt> nulls should be searched <tt>false</tt> otherwise
     * @see #isSearchNulls() 
     */
    void setSearchNulls(boolean searchNulls);
}

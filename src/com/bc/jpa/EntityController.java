package com.bc.jpa;

import com.bc.jpa.query.JPQL;
import com.bc.jpa.exceptions.EntityInstantiationException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @(#)EntityController.java   30-Nov-2013 13:33:59
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface EntityController<E, e> extends EntityControllerBase<E, e> {
    
    Map formatWhereParameters(Map params);

    Collection formatColumnNames(Collection columnNames);

    Map formatOrderBy(Map orderBy);
    
    JPQL getJpql();

    void setJpql(JPQL jpql);
    
    Object getResultType();

    void setResultType(Object resultType);
    
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
    
    E persist(Map params);
    
    List<E> select(Map parameters, String connector);
    
    List<E> select(Map parameters, Map orderByParameters, 
            String connector, int maxResults, int firstResult);

    long count(Map parameters);
    
    E toEntity(Map row, boolean convertCrossReferences) 
            throws EntityInstantiationException;
    
    int updateEntity(E entity, Map row, boolean convertCrossReferences) 
            throws EntityInstantiationException;
    
    int deleteById(e id);

    E selectById(e id);

    E selectFirst(String columnName, Object columnValue);

    E selectFirst(Map params);

    Object selectValue(String selectCol, String column, Object value);

    Object selectId(String column, Object value);

    int delete(String column, Object value);

    int delete(Map params);

    List<E> edit(String searchCol, Object searchVal, String updateCol, Object updateVal);

    List<E> edit(String connector, Map<String, Object> where, Map<String, Object> update);

    E editById(e id, String col, Object val);
    
    /**
     * @deprecated 
     * @return The keys generated by the last transaction
     */
    @Deprecated
    int [] getGeneratedKeys();

    int insert(Map params);

    List<E> select(String column, Object value, int offset, int limit);

    List<E> select(Map params, Map orderBy, int offset, int limit);

    List<E> select(String column, Object[] values);

    /**
     * SELECT * FROM table WHERE column = elem1 OR column = elem 2 OR column = elem 3 ...
     * Values elem1,elem2... etc are gotten from the input Array
     * @throws SQLException
     */
    List<E> select(String column, Object[] values, Map orderBy, int offset, int limit);

    List selectColumn(String selectCol, String column, Object value, int offset, int limit);

    List selectColumn(String selectCol, Map params, Map orderBy, int offset, int limit);

    List selectColumns(Collection<String> selectCols, Map params, Map orderBy, int offset, int limit);

    /**
     * @deprecated Rather use {@link #toList(java.util.List, java.lang.String, int)}
     */
    List toList(List<E> results, int columnIndex, int limit);

    /**
     * Use this when only one column is expected in the resultSet.
     * Or if you want to retrieve only one column
     */
    List toList(List<E> results, String column, int limit);
    
    List toList(E entity);
    
    Map toMap(E entity);

    Map toMap(E entity, boolean nullsAllowed);
    
    void toMap(E entity, Map buffer, boolean nullsAllowed);
    
    List<Map<String, ?>> toMapList(List<E> results, int limit);

    List<Map<String, ?>> toMapList(List<E> results, Collection<String> columnNames, int limit);
    
    int update(String searchCol, Object searchVal, String updateCol, Object updateVal);

    int update(String connector, Map where, Map update);
    
    int update(Map where, Map update);

    int updateById(Object id, String col, Object val);
    
    /**
     * Lets say we have a reference table named <tt>role</tt> as shown below: 
     * <pre>
     * ----------------
     *  roleid | role
     * ----------------
     *  1      | admin
     * ----------------
     *  2      | user
     * ----------------
     *  3      | guest
     * ----------------
     * </pre>
     * <br/>And given the referencing table named <tt>userroles</tt> with definition below:
     * <pre>
     * create table userroles(
     *   userroleid INTEGER(8) AUTO_INCREMENT not null primary key,
     *   role SHORT(2) not null,
     *   FOREIGN KEY (role) REFERENCES role(roleid)
     * )ENGINE=INNODB;
     * </pre>
     * Calling this method with arguments <tt>role</tt> and <tt>2</tt> respectively 
     * will return the <tt>Role</tt> entity with the specified id.
     * <br/><br/>
     * This method will return null if no reference is applicable.
     */
    Object getReference(String col, Object val);

///////////////////
//    void close();

//    void addBatch(String query) throws SQLException;        
            
//    int [] executeBatch() throws SQLException;
    
//    int getAutoGeneratedKeys();

// ??? ??? ???    
//    int[] getColumnIndices();

    /**
     * @return The last connection returned by {@linkplain #newConnection()}
     */
//    Connection getConnection();

//    int getResultSetConcurrency();

//    int getResultSetType();

//    Statement getStatement();

    /**
     * Implementations of this method should make the created connection 
     * available via the {@linkplain #getConnection()} method.
     * @return a newly created {@link java.sql.Connection}
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
//    Connection newConnection() throws ClassNotFoundException, SQLException;

//    void reset();

//    void setAutoGeneratedKeys(int autoGeneratedKeys);
    
//    void setColumnIndices(int[] columnIndices);

//    void setResultSetConcurrency(int resultSetConcurrency);

//    void setResultSetType(int resultSetType);
}
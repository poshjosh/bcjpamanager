package com.bc.jpa.controller;

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;

/**
 * @(#)EntityController.java   30-Nov-2013 13:33:59
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @param <E> The type of entity objects of this class may operate on
 * @param <e> The type of Id of the entity objects of this class may operate on
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface EntityController<E, e> extends EntityUpdater<E, e> {
    
    EntityManager getEntityManager();
    
    Map formatWhereParameters(Map params);

    Collection formatColumnNames(Collection columnNames);

    Map formatOrderBy(Map orderBy);
    
    /**
     * @return
     * @deprecated
     */
    @Deprecated
    Object getResultType();

    /**
     * @param resultType
     * @deprecated
     */
    @Deprecated
    void setResultType(Object resultType);
    
    List<E> select(Map parameters, String connector);
    
    List<E> select(Map parameters, Map orderByParameters, 
            String connector, int maxResults, int firstResult);

    long count(Map parameters);
    
    E selectById(e id);

    E selectFirst(String columnName, Object columnValue);

    E selectFirst(Map params);

    Object selectValue(String selectCol, String column, Object value);

    Object selectId(String column, Object value);

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
    @Deprecated
    List toList(List<E> results, int columnIndex, int limit);

    /**
     * Use this when only one column is expected in the resultSet.
     * Or if you want to retrieve only one column
     */
    List toList(List<E> results, String column, int limit);
    
    Map toMap(E entity);

    Map toMap(E entity, boolean nullsAllowed);
    
    void toMap(E entity, Map buffer, boolean nullsAllowed);
    
    List<Map<String, ?>> toMapList(List<E> results, int limit);

    List<Map<String, ?>> toMapList(List<E> results, Collection<String> columnNames, int limit);
    
    E persist(Map params);
    
    int deleteById(e id);

    int delete(String column, Object value);

    int delete(Map params);

    /**
     * <b>Note: Make sure you close the EntityManagr after using this method</b>
     * @param em
     * @param column
     * @param value
     * @return 
     */
    int delete(EntityManager em, String column, Object value);

    List<E> edit(String searchCol, Object searchVal, String updateCol, Object updateVal);
    
    List<E> edit(String connector, Map<String, Object> where, Map<String, Object> update);

    E editById(e id, String col, Object val);
    
    int insert(Map params);
    
    int update(String searchCol, Object searchVal, String updateCol, Object updateVal);

    int update(String whereColumn, Set whereValues, String updateColumn, Object updateValue);  
    
    /**
     * <b>NOTE: Make sure you close the EntityManager after using this method</b>
     * @param em
     * @param oldColumn
     * @param oldValue
     * @param newColumn
     * @param newValue
     * @return
     */
    int update(EntityManager em, String oldColumn, Object oldValue, String newColumn, Object newValue);
    
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
     * <p>And given the referencing table named <tt>userroles</tt> with definition below:</p>
     * <pre>
     * create table userroles(
     *   userroleid INTEGER(8) AUTO_INCREMENT not null primary key,
     *   role SHORT(2) not null,
     *   FOREIGN KEY (role) REFERENCES role(roleid)
     * )ENGINE=INNODB;
     * </pre>
     * <p>
     * Calling this method with arguments <tt>role</tt> and <tt>2</tt> respectively 
     * will return the <tt>Role</tt> entity with the specified id.
     * </p>
     * This method will return null if no reference is applicable.
     * @param col
     * @param val
     * @return The reference, referenced by the supplied column and value
     */
    Object getReference(String col, Object val);

    E create(Map values, boolean convertCrossReferences) 
            throws EntityInstantiationException;
    
    int update(E entity, Map values, boolean convertCrossReferences) 
            throws EntityInstantiationException;
    
//////////////////////////////////////////////////////////////////////////
///////////// Methods from previous class EntityControllerBase////////////  
    
    
    long count();
    
    void create(E entity) throws PreexistingEntityException, Exception;
    
    int create(Collection<E> entities); 

    int destroy(Collection<e> ids);
    
    void destroy(e id) throws IllegalOrphanException, NonexistentEntityException;
    
    int edit(Collection<E> entities);
    
    void edit(E entity) throws NonexistentEntityException, Exception;

    int executeUpdate(String query);
    
    List executeQuery(String query);

    List executeQuery(String query, String hintKey, Object hintValue);

    E find(e id);

    List<E> find();

    List<E> find(int maxResults, int firstResult);
    
    String getDatabaseName();
    
    String getTableName();
    
    String getIdColumnName();
    
 
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

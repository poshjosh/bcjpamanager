package com.bc.jpa;

import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @(#)EntityControllerBase.java   10-May-2014 14:53:48
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
public interface EntityControllerBase<E, e> {
    
    long count();
    
    void create(E entity) throws PreexistingEntityException, Exception;
    
    int create(Collection<E> entities); 

    int destroy(Collection<e> ids);
    
    void destroy(e id) throws IllegalOrphanException, NonexistentEntityException;
    
    int edit(Collection<E> entities);
    
    void edit(E entity) throws NonexistentEntityException, Exception;

    /**
     * Simply throws UnsupportedOperationException.<br/>
     * Rather use {@link #executeQuery(java.lang.String)} or
     * {@link #executeUpdate(java.lang.String)}
     * @deprecated 
     * @see #executeQuery(java.lang.String) 
     * @see #executeUpdate(java.lang.String) 
     */
    boolean execute(String query);

    int executeUpdate(String query);
    
    List executeQuery(String query);

    List executeQuery(String query, String hintKey, Object hintValue);

    E find(e id);

    List<E> find();

    List<E> find(int maxResults, int firstResult);
    
    String getDatabaseName();

    Class<E> getEntityClass();
    
    EntityManager getEntityManager();
    
    EntityManagerFactory getEntityManagerFactory();
    
    PersistenceMetaData getMetaData();

    /**
     * @param entity Entity whose Id is to be returned
     * @return The id of the specified entity
     * @throws IllegalArgumentException If no method matching the  
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    e getId(E entity); 
    
    String getTableName();
    
    String getIdColumnName();

    /**
     * @param entity Entity whose value is to be returned
     * @param columnName The columnName matching the field whose value is 
     * to be returned
     * @return The value of the field whose name matches the specified columnName
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    Object getValue(E entity, String columnName) 
            throws IllegalArgumentException, UnsupportedOperationException;

    /**
     * @param databaseName
     * @deprecated 
     */
    @Deprecated
    void setDatabaseName(String databaseName);

    void setEntityClass(Class<E> aClass);
    
    /**
     * @param entity Entity whose Id is to be updated with a new value
     * @param id The new id
     * @throws IllegalArgumentException If no method matching the specified
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    void setId(E entity, e id);
    
    void setTableName(String tableName);

    /**
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    void setValue(E entity, String columnName, Object columnValue)
            throws IllegalArgumentException, UnsupportedOperationException;
}

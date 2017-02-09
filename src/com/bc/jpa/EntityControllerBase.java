package com.bc.jpa;

import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.util.Collection;
import java.util.List;

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
public interface EntityControllerBase<E, e> extends EntityUpdater<E, e> {
    
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
}

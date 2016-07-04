package com.bc.jpa;

import com.bc.jpa.query.JPQL;
import com.bc.jpa.query.QueryBuilder;
import com.bc.jpa.fk.EnumReferences;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * @(#)ControllerFactory.java   20-Mar-2014 18:09:39
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
public interface JpaContext {

    <T> QueryBuilder<T> getQueryBuilder(Class<T> entityAndResultType);
    
    <T> QueryBuilder<T> getQueryBuilder(Class entityType, Class<T> resultType);
    
    Object getReference(Class referencingClass, String col, Object val);
    
    <E> JPQL<E> getJpql(Class<E> entityClass);

    DatabaseFormat getDatabaseFormat();
    
    Map getDatabaseParameters(Class entityClass, Map params);
    
    EntityController getEntityController(String database, String table);
    
    <E> EntityController<E, Object> getEntityController(Class<E> entityClass);

    <E, e> EntityController<E, e> getEntityController(Class<E> entityClass, Class<e> idClass);
    
    EntityManager getEntityManager(String database);

    EntityManager getEntityManager(Class entityClass);
    
    PersistenceMetaData getMetaData();
    
    EnumReferences getEnumReferences();
}

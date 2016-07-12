package com.bc.jpa;

import com.bc.jpa.query.JPQL;
import com.bc.jpa.query.QueryBuilder;
import com.bc.jpa.fk.EnumReferences;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;

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
    
    boolean isOpen();
    
    void close();
    
    /**
     * @return The path to the persistence configuration file usually <tt>META-INF/persistence.xml</tt>
     */
    URI getPersistenceConfigURI();

    EntityManagerFactory getEntityManagerFactory(Class entityClass);
    
    EntityManagerFactory getEntityManagerFactory(String persistenceUnit);    
        
    <T> QueryBuilder<T> getQueryBuilder(Class<T> entityAndResultType);
    
    <T> QueryBuilder<T> getQueryBuilder(Class entityType, Class<T> resultType);
    
    Object getReference(Class referencingClass, String col, Object val);
    
    Object getReference(
            EntityManager em, Class referencingType, 
            Map<JoinColumn, Field> joinCols, String col, Object val);
    
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

package com.bc.jpa;

import com.bc.jpa.dao.BuilderForDelete;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.fk.EnumReferences;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.dao.BuilderForUpdate;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.search.TextSearch;

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
public interface JpaContext extends AutoCloseable {
    
    boolean isOpen();
    
    @Override
    void close();
    
    /**
     * @return The path to the persistence configuration file usually <tt>META-INF/persistence.xml</tt>
     */
    URI getPersistenceConfigURI();

    EntityManagerFactory getEntityManagerFactory(Class entityClass);
    
    EntityManagerFactory getEntityManagerFactory(String persistenceUnit);    
        
    EntityManagerFactory removeEntityManagerFactory(String persistenceUnit, boolean close);

    Dao getDao(Class entityType);
    
    <T> BuilderForSelect<T> getBuilderForSelect(Class<T> entityAndResultType);
    
    <T> BuilderForSelect<T> getBuilderForSelect(Class entityType, Class<T> resultType);
    
    <T> BuilderForDelete<T> getBuilderForDelete(Class<T> entityType);

    <T> BuilderForUpdate<T> getBuilderForUpdate(Class<T> entityType);
    
    TextSearch getTextSearch();
    
    /**
     * @see #getReference(javax.persistence.EntityManager, java.lang.Class, java.util.Map, java.lang.String, java.lang.Object) 
     * @param referencingClass
     * @param col
     * @param val
     * @return 
     */
    Object getReference(Class referencingClass, String col, Object val);
    
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
     *   userid INTEGER(8) not null UNIQUE,
     *   role SHORT(2) not null,
     *   FOREIGN KEY (role) REFERENCES role(roleid)
     * )ENGINE=INNODB;
     * </pre>
     * <p>
     * Calling this method with arguments <tt>role</tt> and <tt>2</tt> respectively 
     * will return the <tt>Role</tt> entity with the specified id.
     * </p>
     * The method returns null if the arguments have no matching reference.
     * @param em
     * @param referencingType
     * @param joinCols
     * @param col
     * @param val
     * @return 
     */
    Object getReference(
            EntityManager em, Class referencingType, 
            Map<JoinColumn, Field> joinCols, String col, Object val);
    
    DatabaseFormat getDatabaseFormat();
    
    Map getDatabaseParameters(Class entityClass, Map params);
    
    EntityController getEntityController(String database, String table);
    
    <E> EntityController<E, Object> getEntityController(Class<E> entityClass);

    <E, e> EntityController<E, e> getEntityController(Class<E> entityClass, Class<e> idClass);
    
    EntityManager getEntityManager(String database);

    EntityManager getEntityManager(Class entityClass);
    
    JpaMetaData getMetaData();
    
    EnumReferences getEnumReferences();
    
    <E> EntityUpdater<E, Object> getEntityUpdater(Class<E> entityClass);
}

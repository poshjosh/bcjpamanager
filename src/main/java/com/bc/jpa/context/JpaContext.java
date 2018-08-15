package com.bc.jpa.context;

import com.bc.jpa.controller.EntityController;
import com.bc.jpa.metadata.JpaMetaData;
import com.bc.jpa.fk.EnumReferences;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.Select;

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
 * @deprecated 
 */
@Deprecated
public interface JpaContext extends PersistenceContext, PersistenceUnitContext {
    
    /**
     * @param entityClass
     * @return 
     * @deprecated
     */        
    @Deprecated
    EntityManagerFactory getEntityManagerFactory(Class entityClass);
    
    /**
     * @param entityType
     * @return 
     * @deprecated
     */        
    @Deprecated
    Dao getDao(Class entityType);
    
    /**
     * @param <T>
     * @param entityType
     * @param resultType
     * @return 
     * @deprecated
     */        
    @Deprecated
    <T> Select<T> getDaoForSelect(Class entityType, Class<T> resultType);
    
    /**
     * @param catalog
     * @param schema
     * @param table
     * @return 
     * @deprecated
     */        
    @Deprecated
    EntityController getEntityController(String catalog, String schema, String table);
    
    /**
     * @param <E>
     * @param entityClass
     * @return 
     * @deprecated
     */        
    @Deprecated
    <E> EntityController<E, Object> getEntityController(Class<E> entityClass);

    /**
     * @param <E>
     * @param <e>
     * @param entityClass
     * @param idClass
     * @return 
     * @deprecated
     */        
    @Deprecated
    <E, e> EntityController<E, e> getEntityController(Class<E> entityClass, Class<e> idClass);
    
    /**
     * @param entityClass
     * @return 
     * @deprecated
     */        
    @Deprecated
    EntityManager getEntityManager(Class entityClass);
    
    /**
     * @return 
     */        
    @Override
    default boolean isMetaDataLoaded() {
        return this.getMetaData(false).isBuilt();
    }
    
    /**
     * @deprecated
     */        
    @Deprecated
    @Override
    JpaMetaData loadMetaData();
    
    /**
     * @return 
     * @deprecated
     */        
    @Deprecated
    @Override
    default JpaMetaData getMetaData() {
        return this.getMetaData(true);
    }
    
    /**
     * @param loadIfNotLoaded
     * @return 
     * @deprecated
     */        
    @Deprecated
    @Override
    JpaMetaData getMetaData(boolean loadIfNotLoaded);

    /**
     * @return 
     * @deprecated
     */        
    @Deprecated
    EnumReferences getEnumReferences();
}

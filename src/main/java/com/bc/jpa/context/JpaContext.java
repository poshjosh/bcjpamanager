package com.bc.jpa.context;

import com.bc.jpa.controller.EntityController;
import com.bc.jpa.metadata.JpaMetaData;
import com.bc.jpa.fk.EnumReferences;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.Update;
import com.bc.jpa.dao.Delete;

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
     * @deprecated
     */        
    @Deprecated
    EntityManagerFactory getEntityManagerFactory(Class entityClass);
    
    /**
     * @deprecated
     */        
    @Deprecated
    Dao getDao(Class entityType);
    
    /**
     * @deprecated
     */        
    @Deprecated
    <T> Select<T> getDaoForSelect(Class<T> resultType);
    
    /**
     * @deprecated
     */        
    @Deprecated
    <T> Select<T> getDaoForSelect(Class entityType, Class<T> resultType);
    
    /**
     * @deprecated
     */        
    @Deprecated
    <T> Delete<T> getDaoForDelete(Class<T> entityType);

    /**
     * @deprecated
     */        
    @Deprecated
    <T> Update<T> getDaoForUpdate(Class<T> entityType);
    
    /**
     * @deprecated
     */        
    @Deprecated
    EntityController getEntityController(String catalog, String schema, String table);
    
    /**
     * @deprecated
     */        
    @Deprecated
    <E> EntityController<E, Object> getEntityController(Class<E> entityClass);

    /**
     * @deprecated
     */        
    @Deprecated
    <E, e> EntityController<E, e> getEntityController(Class<E> entityClass, Class<e> idClass);
    
    /**
     * @deprecated
     */        
    @Deprecated
    EntityManager getEntityManager(Class entityClass);
    
    /**
     * @deprecated
     */        
    @Deprecated
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
     * @deprecated
     */        
    @Deprecated
    @Override
    default JpaMetaData getMetaData() {
        return this.getMetaData(true);
    }
    
    /**
     * @deprecated
     */        
    @Deprecated
    @Override
    JpaMetaData getMetaData(boolean loadIfNotLoaded);

    /**
     * @deprecated
     */        
    @Deprecated
    EnumReferences getEnumReferences();
}

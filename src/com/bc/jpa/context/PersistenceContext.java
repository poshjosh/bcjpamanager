/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa.context;

import com.bc.jpa.EntityReference;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.metadata.PersistenceMetaData;
import com.bc.jpa.search.TextSearch;
import java.net.URI;
import java.sql.SQLException;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 10:52:41 AM
 */
public interface PersistenceContext extends AutoCloseable, Function<String, EntityManagerFactory> {
    
    @Override
    default EntityManagerFactory apply(String persisenceUnit) {
        return this.getEntityManagerFactory(persisenceUnit);
    }
    
    boolean isOpen();
    
    @Override
    void close();
    
    /**
     * @return The path to the persistence configuration file usually <tt>META-INF/persistence.xml</tt>
     */
    URI getPersistenceConfigURI();
    
    PersistenceUnitContext getContext(String persistenceUnit);
    
    default <R> R executeTransaction(String persistenceUnit, Function<EntityManager, R> transaction) {
        final EntityManager em = this.getEntityManager(persistenceUnit);
        return this.executeTransaction(em, transaction);
    }
    
    <R> R executeTransaction(EntityManager em, Function<EntityManager, R> transaction);

    EntityManagerFactory getEntityManagerFactory(String persistenceUnit);    
        
    EntityManagerFactory removeEntityManagerFactory(String persistenceUnit, boolean close);

    Dao getDao(String persistenceUnit);
    
    TextSearch getTextSearch(String persistenceUnit);
    
    DatabaseFormat getDatabaseFormat(String persistenceUnit);
    
    EntityManager getEntityManager(String persistenceUnit);
    
    default boolean isMetaDataLoaded() {
        return this.getMetaData(false).isBuilt();
    }
    
    /**
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @return
     * @throws SQLException 
     */
    default PersistenceMetaData loadMetaData() throws SQLException {
        final PersistenceMetaData metaData = this.getMetaData(false);
        metaData.build(this);
        return metaData;
    }
    
    default PersistenceMetaData getMetaData() {
        return this.getMetaData(true);
    }

    PersistenceMetaData getMetaData(boolean loadIfNotLoaded);
    
    EntityReference getEntityReference();
    
    <E> EntityUpdater<E, Object> getEntityUpdater(String persistenceUnit, Class<E> entityClass);
}

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

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import com.bc.jpa.search.TextSearch;
import java.sql.SQLException;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 12:13:58 PM
 */
public interface PersistenceUnitContext extends AutoCloseable {
    
    boolean isOpen();
    
    @Override
    void close();
    
    default String getName() {
        return this.getPersistenceUnitName();
    }
    
    String getPersistenceUnitName();
    
    <R> R executeTransaction(Function<EntityManager, R> transaction);

    EntityManagerFactory getEntityManagerFactory();    
        
    EntityManagerFactory removeEntityManagerFactory(boolean close);

    Dao getDao();
    
    TextSearch getTextSearch();
    
    DatabaseFormat getDatabaseFormat();
    
    EntityManager getEntityManager();
    
    <E> EntityUpdater<E, Object> getEntityUpdater(Class<E> entityClass);

    default boolean isMetaDataLoaded() {
        return this.getMetaData(false).isBuilt();
    }
    
    /**
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @return
     * @throws SQLException 
     */
    default PersistenceUnitMetaData loadMetaData() throws SQLException {
        final PersistenceUnitMetaData puMetaData = this.getMetaData(false);
        final PersistenceContext persistenceContext = this.getPersistenceContext();
        puMetaData.build(persistenceContext.getMetaData(false).getNode(), persistenceContext);
        return puMetaData;
    }
    
    default PersistenceUnitMetaData getMetaData() {
        return this.getMetaData(true);
    }
    
    PersistenceUnitMetaData getMetaData(boolean loadIfNotLoaded);

    PersistenceContext getPersistenceContext();
}

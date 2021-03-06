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

import com.bc.jpa.EntityUpdaterImpl;
import com.bc.jpa.DatabaseFormatImpl;
import com.bc.jpa.EntityManagerFactoryCreator;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DaoImpl;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import com.bc.jpa.search.TextSearch;
import com.bc.jpa.search.TextSearchImpl;
import com.bc.sql.SQLDateTimePatterns;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 12:21:14 PM
 */
public class PersistenceUnitContextImpl implements PersistenceUnitContext, Serializable {

    private transient static final Logger logger = Logger.getLogger(PersistenceUnitContextImpl.class.getName());

    private final PersistenceContext persistenceContext;
    
    private final String persistenceUnitName;
    
    private final EntityManagerFactoryCreator emfCreator;
    
    private final SQLDateTimePatterns sqlDateTimePatterns;
    
    private final PersistenceUnitMetaData metaData;
    
    private transient EntityManagerFactory factory;
    
    public PersistenceUnitContextImpl(
            PersistenceContext persistenceContext, 
            String persistenceUnit,
            PersistenceUnitMetaData metaData,
            EntityManagerFactoryCreator emfCreator,
            SQLDateTimePatterns sqlDateTimePatterns) { 
        
        this.persistenceContext = Objects.requireNonNull(persistenceContext);
        
        this.persistenceUnitName = Objects.requireNonNull(persistenceUnit);
        
        this.emfCreator = Objects.requireNonNull(emfCreator);
        
        this.sqlDateTimePatterns = Objects.requireNonNull(sqlDateTimePatterns);
        
        this.metaData = Objects.requireNonNull(metaData);
    }
    
    @Override
    public boolean isOpen() {
        return this.factory != null && this.factory.isOpen();
    }

    @Override
    public void close() {
        if(this.factory != null && this.isOpen()) {
            this.factory.close();
        }
    }

    @Override
    public <R> R executeTransaction(Function<EntityManager, R> transaction) {
        return this.persistenceContext.executeTransaction(this.getEntityManager(), transaction);
    }
    
    @Override
    public <E> EntityUpdater<E, Object> getEntityUpdater(Class<E> entityClass) {
        return new EntityUpdaterImpl(this, entityClass);
    }

    @Override
    public Dao getDao() {
        return new DaoImpl(this.getEntityManager(), this.getDatabaseFormat());
    }

    @Override
    public TextSearch getTextSearch() {
        return new TextSearchImpl(this);
    }

    @Override
    public DatabaseFormat getDatabaseFormat() {
        return new DatabaseFormatImpl(this, this.sqlDateTimePatterns);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        if(this.factory == null) {
            this.factory = this.emfCreator.newInstance(this.persistenceUnitName);
        }
        return this.factory;
    }
    
    @Override
    public EntityManagerFactory removeEntityManagerFactory(boolean close) {
        if(close) {
            this.close();
        }
        final EntityManagerFactory output = this.factory;
        this.factory = null;
        return output;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    @Override
    public final PersistenceUnitMetaData getMetaData(boolean loadIfNotLoaded) {
        if(!this.metaData.isBuilt() && loadIfNotLoaded) {
            try{
                this.loadMetaData();
            }catch(SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return this.metaData;
    }
}

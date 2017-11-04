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

import com.bc.jpa.EntityManagerFactoryCreator;
import com.bc.jpa.EntityManagerFactoryCreatorImpl;
import com.bc.jpa.EntityReference;
import com.bc.jpa.EntityReferenceImpl;
import com.bc.jpa.EntityUpdater;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.dao.functions.CommitEntityTransaction;
import com.bc.jpa.functions.GetClassLoaderForPersistenceUri;
import com.bc.jpa.metadata.PersistenceMetaData;
import com.bc.jpa.metadata.PersistenceMetaDataImpl;
import com.bc.jpa.search.TextSearch;
import com.bc.sql.SQLDateTimePatterns;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 10:30:01 PM
 */
public class PersistenceContextImpl implements PersistenceContext, Serializable {

    private transient final Map<String, PersistenceUnitContext> persistenceUnitContexts = new HashMap<>();
    
    private final PersistenceMetaData metaData;
    
    private final EntityManagerFactoryCreator emfCreator;
            
    private final SQLDateTimePatterns dateTimePatterns;

    private boolean closed;
    
    public PersistenceContextImpl(
            URI persistenceConfigUri, 
            SQLDateTimePatterns dateTimePatterns) { 
        
        this(persistenceConfigUri,
                new EntityManagerFactoryCreatorImpl(
                        new GetClassLoaderForPersistenceUri().apply(persistenceConfigUri.toString()),
                        (persistenceUnit) -> new Properties()
                ), 
                dateTimePatterns
        );
    }
    
    public PersistenceContextImpl(
            URI persistenceConfigUri, 
            EntityManagerFactoryCreator emfCreator,
            SQLDateTimePatterns dateTimePatterns) { 
        
        this(
                new PersistenceMetaDataImpl(persistenceConfigUri),
                emfCreator, dateTimePatterns);
    }
    
    public PersistenceContextImpl(
            PersistenceMetaData metaData, 
            EntityManagerFactoryCreator emfCreator,
            SQLDateTimePatterns dateTimePatterns) { 

        this.metaData = Objects.requireNonNull(metaData);
        
        this.emfCreator = Objects.requireNonNull(emfCreator);
        
        this.dateTimePatterns = Objects.requireNonNull(dateTimePatterns);
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public void close() {
        if(!this.isOpen()) {
            return;
        }
        closed = true;
        Collection<PersistenceUnitContext> puContexts = persistenceUnitContexts.values();
        for(PersistenceUnitContext puContext : puContexts) {
            if(puContext.isOpen()) {
                puContext.close();
            }
        }
    }

    @Override
    public EntityReference getEntityReference() {
        return new EntityReferenceImpl();
    }
    
    @Override
    public <R> R executeTransaction(EntityManager em, Function<EntityManager, R> transaction) {
        
        R result;
        final EntityTransaction t = em.getTransaction();
        try{
            
            t.begin();
            
            result = transaction.apply(em);
            
        }finally{
            try{
                new CommitEntityTransaction().apply(t);
            }finally{
                if(em.isOpen()) {
                    em.close();
                }
            }
        }
        return result;
    }
    
    @Override
    public <E> EntityUpdater<E, Object> getEntityUpdater(String persistenceUnit, Class<E> entityClass) {
        return this.getContext(persistenceUnit).getEntityUpdater(entityClass);
    }

    @Override
    public Dao getDao(String persistenceUnit) {
        return this.getContext(persistenceUnit).getDao();
    }

    @Override
    public TextSearch getTextSearch(String persistenceUnit) {
        return this.getContext(persistenceUnit).getTextSearch();
    }

    @Override
    public DatabaseFormat getDatabaseFormat(String persistenceUnit) {
        return this.getContext(persistenceUnit).getDatabaseFormat();
    }

    @Override
    public PersistenceUnitContext getContext(String persistenceUnit) {
        return this.getContext(persistenceUnit, true);
    }
    
    @Override
    public EntityManager getEntityManager(String persistenceUnit) {
        return this.getContext(persistenceUnit).getEntityManager();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {

        return this.getEntityManagerFactory(persistenceUnit, true);
    }
    
    @Override
    public EntityManagerFactory removeEntityManagerFactory(String persistenceUnit, boolean close) {
        
        final PersistenceUnitContext puContext = this.getContext(persistenceUnit, false);
        
        return puContext == null ? null : puContext.removeEntityManagerFactory(close);
    }
    
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit, boolean createIfNone) {

        final PersistenceUnitContext puContext = this.getContext(persistenceUnit, createIfNone);
        
        return puContext == null ? null : puContext.getEntityManagerFactory();
    }

    public PersistenceUnitContext getContext(String persistenceUnit, boolean createIfNone) {

        PersistenceUnitContext puContext = persistenceUnitContexts.get(persistenceUnit);
        
        if(puContext == null && createIfNone) {
            
            puContext = this.createPersistenceUnitContext(
                    persistenceUnit, emfCreator, dateTimePatterns);

            persistenceUnitContexts.put(persistenceUnit, puContext);
        }

        return puContext;
    }
    
    public PersistenceUnitContext createPersistenceUnitContext(
            String persistenceUnit,
            EntityManagerFactoryCreator emfCreator,
            SQLDateTimePatterns sqlDateTimePatterns) {
        
            return new PersistenceUnitContextImpl(
                    this, persistenceUnit, 
                    this.metaData.getMetaData(persistenceUnit), 
                    emfCreator, sqlDateTimePatterns);
    }
    
    @Override
    public final URI getPersistenceConfigURI() {
        return this.metaData.getURI();
    }
    
    @Override
    public PersistenceMetaData getMetaData(boolean loadIfNotLoaded) {
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

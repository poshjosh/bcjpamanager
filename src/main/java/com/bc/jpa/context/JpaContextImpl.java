package com.bc.jpa.context;

import com.bc.jpa.context.eclipselink.PersistenceContextEclipselinkOptimized;
import com.bc.jpa.EntityManagerFactoryCreatorImpl;
import com.bc.jpa.EntityReference;
import com.bc.jpa.controller.EntityController;
import com.bc.jpa.controller.EntityControllerImpl;
import com.bc.jpa.controller.ReferenceEntityController;
import com.bc.jpa.controller.ReferencingEntityController;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.metadata.JpaMetaDataImpl;
import com.bc.jpa.metadata.JpaMetaData;
import com.bc.jpa.util.PersistenceURISelector;
import com.bc.jpa.fk.EnumReferences;
import com.bc.jpa.fk.EnumReferencesImpl;
import com.bc.sql.MySQLDateTimePatterns;
import com.bc.sql.SQLDateTimePatterns;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import javax.persistence.EntityManager;
import java.net.URISyntaxException;
import java.util.Properties;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.Update;
import com.bc.jpa.dao.Delete;
import com.bc.xml.PersistenceXmlDomImpl;
import com.bc.jpa.search.TextSearch;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;
import com.bc.jpa.EntityMemberAccess;
import com.bc.jpa.metadata.EntityMetaDataAccess;

/**
 * @(#)DefaultControllerFactory.java   28-Jun-2014 18:47:01
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
public class JpaContextImpl implements JpaContext, Serializable {
    
    private final String puName;
    
    private final URI persistenceURI;
    
    private final SQLDateTimePatterns dateTimePatterns;

    private final JpaMetaData metaData;
    
    private final EnumReferences enumReferences;

    private PersistenceContext persistenceContext;
    
    public JpaContextImpl() throws URISyntaxException {
        this(Thread.currentThread().getContextClassLoader().getResource("META-INF/persistence.xml").toURI(), null);
    }
    
    public JpaContextImpl(
            String persistenceFile, PersistenceURISelector.URIFilter uriFilter, 
            SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) 
            throws IOException { 
        
        this(new PersistenceURISelector().selectURI(persistenceFile, uriFilter), dateTimePatterns, enumRefClasses);
    }

    public JpaContextImpl(URI persistenceURI, Class [] enumRefClasses) { 
        
        this(persistenceURI, new MySQLDateTimePatterns(), enumRefClasses);
    }
    
    public JpaContextImpl(
            File persistenceFile, SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) 
            throws IOException { 
        
        this(persistenceFile.toURI(), dateTimePatterns, enumRefClasses);
    }
    
    public JpaContextImpl(
            URI persistenceURI, SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) { 

        this.puName = new PersistenceXmlDomImpl(persistenceURI).getPersistenceUnitNames().get(0);
        Objects.requireNonNull(puName);

        this.persistenceURI = Objects.requireNonNull(persistenceURI);

        this.dateTimePatterns = Objects.requireNonNull(dateTimePatterns);

        this.metaData = new JpaMetaDataImpl(this);        

        if(enumRefClasses != null) {
            this.enumReferences = new EnumReferencesImpl(this, enumRefClasses);
        }else{
            this.enumReferences = null;
        }
    }
    
    @Override
    public Dao getDao(Class entityType) {
        return this.getPersistenceContext().getContext(puName).getDao();
    }
    
    @Override
    public EntityManager getEntityManager(Class entityClass) {
        final EntityManagerFactory emf = this.getEntityManagerFactory(entityClass);
        return emf.createEntityManager();
    }
    
    @Override
    public EntityManagerFactory getEntityManagerFactory(Class entityClass) { 
        return this.getEntityManagerFactory(this.puName);
    }

    @Override
    public <T> Select<T> getDaoForSelect(Class<T> resultType) {
        return this.getPersistenceContext().getContext(puName).getDaoForSelect(resultType);
    }
    
    @Override
    public <T> Select<T> getDaoForSelect(Class entityType, Class<T> resultType) {
        return this.getPersistenceContext().getContext(puName).getDaoForSelect(resultType).from(entityType);
    }

    @Override
    public <T> Update<T> getDaoForUpdate(Class<T> entityType) {
        return this.getPersistenceContext().getContext(puName).getDaoForUpdate(entityType);
    }

    @Override
    public <T> Delete<T> getDaoForDelete(Class<T> entityType) {
        return this.getPersistenceContext().getContext(puName).getDaoForDelete(entityType);
    }
    
    @Override
    public EntityController getEntityController(String database, String schema, String table) {
        return this.getEntityController(this.metaData.getEntityClass(database, schema, table));
    }
    
    @Override
    public <E> EntityController<E, Object> getEntityController(Class<E> entityClass) {
        return getEntityController(entityClass, null);
    }

    @Override
    public <E, e> EntityController<E, e> getEntityController(Class<E> entityClass, Class<e> idClass) {

        EntityController<E, e> controller;

        boolean isReference = metaData.getReferencingClasses(entityClass) != null;
        boolean isReferencing = metaData.getReferenceClasses(entityClass) != null;
        
//System.out.println(entityClass.getName()+" is reference: "+isReference+", is referencing: "+isReferencing);
        
        if(!isReference && !isReferencing) {
            controller = new EntityControllerImpl<>(this, entityClass);
        }else if(isReference) {        
            controller = new ReferenceEntityController<>(this, entityClass);
        }else if(isReferencing) {        
            controller = new ReferencingEntityController<>(this, entityClass);
        }else {     
//            controller = new RelatedEntityController(metaData, entityClass);
                    throw new UnsupportedOperationException(
                            "This implementation currently does not support entity classes which are both parent/reference and child/referencing instances");
        }
        
        return controller;
    }

    public Properties getPersistenceUnitProperties(String persistenceUnit) {
        return new Properties();
    }

    @Override
    public JpaMetaData loadMetaData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ///////////////////// DELEGATING TO PersistenceContext /////////////////////
    @Override
    public JpaMetaData getMetaData(boolean loadIfNotLoaded) {
        return this.metaData;
    }

    @Override
    public boolean isOpen() {
        return getPersistenceContext().isOpen();
    }

    @Override
    public void close() {
        getPersistenceContext().close();
    }

    @Override
    public URI getPersistenceConfigURI() {
        return getPersistenceContext().getPersistenceConfigURI();
    }

    @Override
    public PersistenceUnitContext getContext(String persistenceUnit) {
        return getPersistenceContext().getContext(persistenceUnit);
    }

    @Override
    public <R> R executeTransaction(String persistenceUnit, Function<EntityManager, R> transaction) {
        return getPersistenceContext().executeTransaction(persistenceUnit, transaction);
    }

    @Override
    public <R> R executeTransaction(EntityManager em, Function<EntityManager, R> transaction) {
        return getPersistenceContext().executeTransaction(em, transaction);
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {
        return getPersistenceContext().getEntityManagerFactory(persistenceUnit);
    }

    @Override
    public EntityManagerFactory removeEntityManagerFactory(String persistenceUnit, boolean close) {
        return getPersistenceContext().removeEntityManagerFactory(persistenceUnit, close);
    }

    @Override
    public Dao getDao(String persistenceUnit) {
        return getPersistenceContext().getDao(persistenceUnit);
    }

    @Override
    public TextSearch getTextSearch(String persistenceUnit) {
        return getPersistenceContext().getTextSearch(persistenceUnit);
    }

    @Override
    public DatabaseFormat getDatabaseFormat(String persistenceUnit) {
        return getPersistenceContext().getDatabaseFormat(persistenceUnit);
    }

    @Override
    public EntityManager getEntityManager(String persistenceUnit) {
        return getPersistenceContext().getEntityManager(persistenceUnit);
    }

    @Override
    public EntityReference getEntityReference(String persistenceUnit) {
        return getPersistenceContext().getEntityReference(persistenceUnit);
    }

    @Override
    public <E> EntityMemberAccess<E, Object> getEntityMemberAccess(String persistenceUnit, Class<E> entityClass) {
        return getPersistenceContext().getEntityMemberAccess(persistenceUnit, entityClass);
    }

    @Override
    public EntityMetaDataAccess getMetaDataAccess(String persistenceUnitName) {
        return getPersistenceContext().getMetaDataAccess(persistenceUnitName);
    }

    /////////////////// DELEGATING TO PersistenceUnitContext ///////////////////

    @Override
    public String getName() {
        return getPersistenceContext().getContext(puName).getName();
    }

    @Override
    public String getPersistenceUnitName() {
        return getPersistenceContext().getContext(puName).getPersistenceUnitName();
    }

    @Override
    public <R> R executeTransaction(Function<EntityManager, R> transaction) {
        return getPersistenceContext().getContext(puName).executeTransaction(transaction);
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getPersistenceContext().getContext(puName).getEntityManagerFactory();
    }

    @Override
    public EntityManagerFactory removeEntityManagerFactory(boolean close) {
        return getPersistenceContext().getContext(puName).removeEntityManagerFactory(close);
    }

    @Override
    public Dao getDao() {
        return getPersistenceContext().getContext(puName).getDao();
    }

    @Override
    public EntityReference getEntityReference() {
        return getPersistenceContext().getContext(puName).getEntityReference();
    }

    @Override
    public TextSearch getTextSearch() {
        return getPersistenceContext().getContext(puName).getTextSearch();
    }

    @Override
    public DatabaseFormat getDatabaseFormat() {
        return getPersistenceContext().getContext(puName).getDatabaseFormat();
    }

    @Override
    public EntityManager getEntityManager() {
        return getPersistenceContext().getContext(puName).getEntityManager();
    }

    @Override
    public <E> EntityMemberAccess<E, Object> getEntityMemberAccess(Class<E> entityClass) {
        return getPersistenceContext().getContext(puName).getEntityMemberAccess(entityClass);
    }

    @Override
    public EntityMetaDataAccess getMetaDataAccess() {
        return getPersistenceContext().getContext(puName).getMetaDataAccess();
    }
    
    @Override
    public PersistenceContext getPersistenceContext() {
        if(persistenceContext == null) {
            try{
                this.persistenceContext = new PersistenceContextEclipselinkOptimized(
                        this.persistenceURI, 
                        new EntityManagerFactoryCreatorImpl(
                                persistenceURI,
                                (persistenceUnit) -> JpaContextImpl.this.getPersistenceUnitProperties(persistenceUnit)
                        ), 
                        this.dateTimePatterns);
                this.persistenceContext.loadMetaData();
            }catch(SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return persistenceContext;
    }
    
    ///////////////////// Getters /////////////////////    

    @Override
    public final EnumReferences getEnumReferences() {
        return this.enumReferences;
    }
}

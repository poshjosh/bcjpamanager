package com.bc.jpa;

import com.bc.jpa.query.JPQL;
import com.bc.jpa.query.JPQLImpl;
import com.bc.jpa.query.QueryBuilder;
import com.bc.jpa.query.QueryBuilderImpl;
import com.bc.jpa.fk.EnumReferences;
import com.bc.jpa.fk.EnumReferencesImpl;
import com.bc.sql.MySQLDateTimePatterns;
import com.bc.sql.SQLDateTimePatterns;
import com.bc.util.XLogger;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;

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
 */
public class JpaContextImpl implements JpaContext, Serializable {
    
    private final PersistenceMetaData metaData;
    
    private final SQLDateTimePatterns dateTimePatterns;
    
    private final EnumReferences enumReferences;
    
    public JpaContextImpl(SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) throws IOException {
        
        this("META-INF/persistence.xml", null, dateTimePatterns, enumRefClasses);
    }

    public JpaContextImpl(
            String persistenceFile, PersistenceLoader.URIFilter uriFilter, 
            SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) 
            throws IOException { 
        
        this(new PersistenceLoader().selectURI(persistenceFile, uriFilter), dateTimePatterns, enumRefClasses);
    }

    public JpaContextImpl(URI persistenceFile, Class [] enumRefClasses) throws IOException { 
        
        this(persistenceFile, new MySQLDateTimePatterns(), enumRefClasses);
    }
    
    public JpaContextImpl(
            URI persistenceFile, SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) 
            throws IOException { 
        
        this(new PersistenceMetaDataImpl(persistenceFile), dateTimePatterns, enumRefClasses);
    }

    public JpaContextImpl(
            File persistenceFile, SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) 
            throws IOException { 
        
        this(new PersistenceMetaDataImpl(persistenceFile), dateTimePatterns, enumRefClasses);
    }
    
    public JpaContextImpl(
            PersistenceMetaData metaData, SQLDateTimePatterns dateTimePatterns, Class [] enumRefClasses) { 
        
        if(metaData == null) {
            throw new NullPointerException();
        }
        if(dateTimePatterns == null) {
            throw new NullPointerException();
        }
        
        this.metaData = metaData;
        this.dateTimePatterns = dateTimePatterns;
        if(enumRefClasses != null) {
            this.enumReferences = new EnumReferencesImpl(this, enumRefClasses);
        }else{
            this.enumReferences = null;
        }
    }
    
    /**
     * Subclasses should override this method to as it simply throws
     * {@link java.lang.UnsupportedOperationException}
     * @return Foreignkey references that have been declared in corresponding
     * enums.
     */
    @Override
    public final EnumReferences getEnumReferences() {
        return this.enumReferences;
    }

    @Override
    public <T> QueryBuilder<T> getQueryBuilder(Class<T> resultType) {
        return this.getQueryBuilder(resultType, resultType);
    }
    
    @Override
    public <T> QueryBuilder<T> getQueryBuilder(Class entityType, Class<T> resultType) {
        final EntityManager em = this.getEntityManager(entityType);
        return new QueryBuilderImpl(em, resultType, this.getDatabaseFormat());
    }
    
    @Override
    public <E> JPQL<E> getJpql(Class<E> entityClass) {
        JPQLImpl<E> jpql = new JPQLImpl();
        jpql.setEntityClass(entityClass);
        jpql.setMetaData(this.metaData);
        return jpql;
    }
    
    @Override
    public DatabaseFormat getDatabaseFormat() {
        DatabaseFormat databaseFormat = new DatabaseFormatImpl(this, this.dateTimePatterns);
        return databaseFormat;
    }
    
    @Override
    public Map getDatabaseParameters(final Class entityClass, Map params) {
        Map output;
        if(params == null) {
            output = null;
        }else{
            DatabaseFormat databaseFormat = this.getDatabaseFormat();
            final Object NO_VALUE = new Object();
            output = new HashMap(params.size()+1, 1.0f);
            Set keys = params.keySet();
            for(Object key:keys) {
                Object val = databaseFormat.getDatabaseValue(entityClass, key, params.get(key), NO_VALUE);
                if(val != NO_VALUE) {
                    output.put(key, val);
                }
            }
        }
XLogger.getInstance().log(Level.FINE, "   Params: {0}\nDb params: {1}", this.getClass(), params, output);
        return output;
    }

    @Override
    public Object getReference(Class referencingClass, String col, Object val) {
        
        Map<JoinColumn, Field> joinColumns = this.getMetaData().getJoinColumns(referencingClass);
        
        EntityManager em = this.getEntityManager(referencingClass);
        
        Object ref;
        if(joinColumns == null) {
            ref = null;
        }else{
            ref = JpaUtil.getReference(em, referencingClass, joinColumns, col, val);
        }
        
        return ref;
    }
    
    @Override
    public EntityManager getEntityManager(String database) {
        return JpaUtil.getEntityManagerFactory(
                metaData,
                database).createEntityManager();
    }

    @Override
    public EntityManager getEntityManager(Class entityClass) {
        String database = metaData.getDatabaseName(entityClass);
        return this.getEntityManager(database);
    }
    
    @Override
    public EntityController getEntityController(String database, String table) {
        
        return this.getEntityController(this.metaData.getEntityClass(database, table));
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
            controller = new DefaultEntityController<>(metaData, entityClass);
        }else if(isReference) {        
            controller = new ReferenceEntityController<>(metaData, entityClass);
        }else if(isReferencing) {        
            controller = new ReferencingEntityController<>(metaData, entityClass);
        }else {     
//            controller = new RelatedEntityController(metaData, entityClass);
                    throw new UnsupportedOperationException(
                            "This implementation currently does not support entity classes which are both parent/reference and child/referencing instances");
        }
        
        return controller;
    }

    @Override
    public PersistenceMetaData getMetaData() {
        return metaData;
    }
}

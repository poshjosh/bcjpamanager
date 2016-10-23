package com.bc.jpa;

import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.util.PersistenceURISelector;
import com.bc.jpa.fk.EnumReferences;
import com.bc.jpa.fk.EnumReferencesImpl;
import com.bc.jpa.classloaders.AlternativePersistenceClassLoader;
import com.bc.jpa.classloaders.AlternativePersistenceURLClassLoader;
import com.bc.jpa.dao.BuilderForDelete;
import com.bc.jpa.dao.BuilderForDeleteImpl;
import com.bc.sql.MySQLDateTimePatterns;
import com.bc.sql.SQLDateTimePatterns;
import com.bc.util.XLogger;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.Persistence;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.dao.BuilderForUpdate;
import com.bc.jpa.dao.BuilderForUpdateImpl;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.DaoImpl;

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
    
    private boolean closed;
    
    private transient final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();
    
    private final URI persistenceConfigURI;

    private final JpaMetaData metaData;
    
    private final SQLDateTimePatterns dateTimePatterns;
    
    private final EnumReferences enumReferences;
    
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
        
        this.persistenceConfigURI = Objects.requireNonNull(persistenceURI);
        
        this.dateTimePatterns = Objects.requireNonNull(dateTimePatterns);
        
        this.metaData = new JpaMetaDataImpl(this);        
        
        if(enumRefClasses != null) {
            this.enumReferences = new EnumReferencesImpl(this, enumRefClasses);
        }else{
            this.enumReferences = null;
        }
    }
    
    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public void close() {
        closed = true;
        Collection<EntityManagerFactory> factories = entityManagerFactories.values();
        for(EntityManagerFactory factory:factories) {
            if(factory.isOpen()) {
                factory.close();
            }
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
    public Dao getDao(Class entityType) {
        return new DaoImpl(this.getEntityManager(entityType), this.getDatabaseFormat());
    }

    @Override
    public <T> BuilderForSelect<T> getBuilderForSelect(Class<T> entityAndResultType) {
        return this.getBuilderForSelect(entityAndResultType, entityAndResultType);
    }
    
    @Override
    public <T> BuilderForSelect<T> getBuilderForSelect(Class entityType, Class<T> resultType) {
        final EntityManager em = this.getEntityManager(entityType);
        BuilderForSelect<T> select = new BuilderForSelectEclipselinkOptimized(em, resultType, this.getDatabaseFormat());
        select.from(entityType);
        return select;
    }

    @Override
    public <T> BuilderForUpdate<T> getBuilderForUpdate(Class<T> entityType) {
        final EntityManager em = this.getEntityManager(entityType);
        BuilderForUpdate<T> update = new BuilderForUpdateImpl(em, entityType, this.getDatabaseFormat());
        update.from(entityType);
        return update;
    }

    @Override
    public <T> BuilderForDelete<T> getBuilderForDelete(Class<T> entityType) {
        final EntityManager em = this.getEntityManager(entityType);
        BuilderForDelete<T> delete = new BuilderForDeleteImpl(em, entityType, this.getDatabaseFormat());
        delete.from(entityType);
        return delete;
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
                Object val = databaseFormat.toDatabaseFormat(entityClass, key, params.get(key), NO_VALUE);
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
        
        Object ref;
        if(joinColumns == null) {
            ref = null;
        }else{
            ref = this.getReference(referencingClass, joinColumns, col, val);
        }
        
        return ref;
    }
    
    public Object getReference(
            Class referencingType, Map<JoinColumn, Field> joinCols, String col, Object val) {
     
        EntityManager em = this.getEntityManager(referencingType);
        
        return this.getReference(em, referencingType, joinCols, col, val);
    }
    
    @Override
    public Object getReference(
            EntityManager em, Class referencingType, 
            Map<JoinColumn, Field> joinCols, String col, Object val) {
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINER;
Class cls = this.getClass();

        if(val == null) {
            return null;
        }

        if(joinCols == null) {
            return null;
        }
        
        JoinColumn joinCol = null;
        Class refType = null;
        for(Map.Entry<JoinColumn, Field> entry:joinCols.entrySet()) {
            if(entry.getKey().name().equals(col)) {
                joinCol = entry.getKey();
                refType = entry.getValue().getType();
                break;
            }
        }
        
logger.log(level, "Entity type: {0}, column: {1}, reference type: {2}", 
cls, referencingType, col, refType);        
        
        if(refType == null) {
            return null;
        }
        
        if(refType.equals(val.getClass())) {
logger.log(level, "No need to convert: {0} to type: {1}", cls, val, refType);        
            return null;
        }
        
        String crossRefColumn = joinCol.referencedColumnName();

logger.log(level, "Reference type: {0}, Referencing type: {1}, reference column: {2}", 
        cls, refType, referencingType, crossRefColumn);        
        
        Object ref;
        
        if(crossRefColumn != null) {

logger.log(level, "Reference type: {0}, Raw type: {1}, raw: {2}", cls, refType, val.getClass(), val);        
            
            ref = em.getReference(refType, val);
            
logger.log(level, "Raw type: {0}, raw: {1}, Reference type: {2}, reference: {3}", 
cls, val.getClass(), val, refType, ref);        
            
            if(ref == null) {
                throw new NullPointerException("Reference cannot be null for: "+
                        refType.getName()+" of entity: "+referencingType.getName()+
                        ", column: "+col+", value: "+val);
            }

        }else{

            ref = null;
        }
        
        return ref;
    }
    
    @Override
    public EntityManager getEntityManager(String database) {
        final String persistenceUnit = this.metaData.getPersistenceUnitName(database);
        return this.getEntityManagerFactory(persistenceUnit).createEntityManager();
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
            controller = new DefaultEntityController<>(this, entityClass);
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

    @Override
    public EntityManagerFactory getEntityManagerFactory(Class entityClass) { 
        final String persistenceUnit = this.metaData.getPersistenceUnitName(entityClass);
        return this.getEntityManagerFactory(persistenceUnit);
    }
    
    @Override
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {

        return this.getEntityManagerFactory(persistenceUnit, true);
    }
    
    public EntityManagerFactory getEntityManagerFactory(String persistenceUnit, boolean createIfNone) {

        EntityManagerFactory factory = entityManagerFactories.get(persistenceUnit);
        
        if(factory == null && createIfNone) {
            
            factory = this.createEntityManagerFactory(persistenceUnit);
            
            if(factory != null) {
                entityManagerFactories.put(persistenceUnit, factory);
            }else{
                throw new NullPointerException();
            }
        }

        return factory;
    }
    
    protected EntityManagerFactory createEntityManagerFactory(String persistenceUnit) {
        
        Properties props = null;
        try{

XLogger.getInstance().log(Level.INFO, 
    "======================== Creating EntityManagerFactory =========================\n"+
    "PersistenceUnit: {0}, URI: {1}",
    this.getClass(), persistenceUnit, persistenceConfigURI);

            props = metaData.getProperties(persistenceUnit);

XLogger.getInstance().log(Level.FINE, "Properties: {0}", this.getClass(), props);

        }catch(IOException e) {

            XLogger.getInstance().log(Level.WARNING, "Exception loading properties for unit: "+persistenceUnit, this.getClass(), e);
        }

        final ClassLoader originalClassLoader = this.getContextClassLoader();

        ClassLoader alternativeClassLoader = null;

        try{

            if(persistenceConfigURI != null && !persistenceConfigURI.toString().equals("META-INF/persistence.xml")) {
                
                alternativeClassLoader = this.getAlternativeClassLoader(originalClassLoader);
                
                this.setContextClassLoader(alternativeClassLoader);
            }

            if(props != null && !props.isEmpty()) {

                return Persistence.createEntityManagerFactory(
                        persistenceUnit, props);
            }else{

                return Persistence.createEntityManagerFactory(
                        persistenceUnit);
            }
        }catch(MalformedURLException e) {

            throw new RuntimeException("Exception compiling URL from URI: "+persistenceConfigURI, e);

        }finally{

            if(alternativeClassLoader != null) {

                this.setContextClassLoader(originalClassLoader);
            }
        }
    }
    
    protected ClassLoader getAlternativeClassLoader(ClassLoader toReplace) 
            throws MalformedURLException {
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINE;
Class cls = this.getClass();
        
logger.log(level, "ClassLoader to replace. Type: {0}, instance: {1}", cls, toReplace.getClass().getName(), toReplace);

        ClassLoader replacement;
        if(toReplace instanceof URLClassLoader) {
            replacement = new AlternativePersistenceURLClassLoader(
                    persistenceConfigURI.toURL(), (URLClassLoader)toReplace);
        }else{
            replacement = new AlternativePersistenceClassLoader(
                    persistenceConfigURI.toURL(), (URLClassLoader)toReplace);
        }
        
logger.log(level, "Using classloader: {0} to load persistence.xml file", cls, replacement.getClass().getName());

        return replacement;
    }
    
    /**
     * Wraps <code>Thread.currentThread().setContextClassLoader(ClassLoader)</code> 
     * into a doPrivileged block if security manager is present
     */
    private void setContextClassLoader(final ClassLoader classLoader) {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }else {
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        @Override
                        public java.lang.Object run() {
                            Thread.currentThread().setContextClassLoader(classLoader);
                            return classLoader;
                        }
                    }
            );
        }
    }

    /**
     * Wraps <code>Thread.currentThread().getContextClassLoader()</code> 
     * into a doPrivileged block if security manager is present
     */
    private ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }else {
            return  (ClassLoader) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        @Override
                        public java.lang.Object run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    }
            );
        }
    }
    
    @Override
    public final URI getPersistenceConfigURI() {
        return persistenceConfigURI;
    }
    
    @Override
    public final JpaMetaData getMetaData() {
        return metaData;
    }
}

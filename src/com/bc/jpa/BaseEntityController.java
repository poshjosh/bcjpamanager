package com.bc.jpa;

import com.bc.util.XLogger;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @(#)BaseEntityController.java   05-Dec-2013 19:58:23
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
public class BaseEntityController<E, e> implements EntityControllerBase<E, e> {

    private Method [] accessViaGetter_methods;
    
    private Class entityClass;
    
    private final JpaContext jpaContext;
    
    public BaseEntityController(
            JpaContext ctx, 
            Class<E> entityClass) {
    
        this.jpaContext = ctx;
        
        this.entityClass = entityClass;
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return this.jpaContext.getEntityManagerFactory(entityClass);
    }

    @Override
    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Simply throws UnsupportedOperationException
     * @deprecated 
     */
    @Override
    public boolean execute(String query) {
        throw new UnsupportedOperationException("Use #executeUpdate or #executeQuery");
    }

    /**
     * Simply throws UnsupportedOperationException
     * @deprecated 
     */
    @Override
    public void setDatabaseName(String databaseName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public int executeUpdate(String query) {
        int updateCount = 0;
        EntityManager em = this.getEntityManager();
        try{
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                Query q = em.createNativeQuery(query);
                updateCount = q.executeUpdate();
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    updateCount = 0;
                }
            }
        }finally{
            em.close();
        }
        return updateCount;
    }
    
    @Override
    public List executeQuery(String query) {
        return executeQuery(query, null, null);
    }

    @Override
    public List executeQuery(String query, String hintKey, Object hintValue) {
        EntityManager em = this.getEntityManager();
        try{
            
            Query q = em.createNativeQuery(query);
            
            if(hintKey != null) {
                q.setHint(hintKey, hintValue);
            }
            
            return q.getResultList();
            
        }finally{
            em.close();
        }
    }

    @Override
    public int create(Collection<E> entities) {
        int created = 0;
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                for(E entity:entities) {
                    try{
                        em.persist(entity); 
                        ++created;
                    }catch(Exception e) {
                        XLogger.getInstance().log(Level.WARNING, "Exception creating entity of type: "+entity.getClass().getName(), this.getClass(), e);
                        break;
                    }
                }
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    created = 0;
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return created;
    }

    @Override
    public void create(E entity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                em.persist(entity);
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public int destroy(Collection<e> ids) {
        int destroyed = 0;
        EntityManager em = getEntityManager();
        try {
            EntityTransaction t = em.getTransaction();
            try{
                
                t.begin();
                
                for(e id:ids){
                    E entity;
                    try {
                        entity = (E)em.getReference(this.getEntityClass(), id);
                    } catch (EntityNotFoundException enfe) {
                        XLogger.getInstance().log(Level.WARNING, "The "+this.getEntityClass()+" with id " + id + " no longer exists.", this.getClass(), enfe);
                        break;
                    }
                    em.remove(entity);
                    ++destroyed;
                }
                
                t.commit();
                
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    destroyed = 0;
                }
            }
        } finally {
            em.close();
        }
        return destroyed;
    }
    
    @Override
    public void destroy(e id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                
                E entity;
                try {
                    entity = (E)em.getReference(this.getEntityClass(), id);
                } catch (EntityNotFoundException enfe) {
                    throw new NonexistentEntityException(
                    "The "+this.getEntityClass()+" with id " + id + " no longer exists.", enfe);
                }
                
                em.remove(entity);
                
                t.commit();
                
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public int edit(Collection<E> entities) {
        int updated = 0;
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                for(E entity:entities) {
                    try{
                        entity = em.merge(entity);
                        ++updated;
                    } catch (Exception ex) {

                        String msg = ex.getLocalizedMessage();

                        if (msg == null || msg.length() == 0) {

                            try{

                                e id = this.getId(entity);

                                if(id != null) {
                                    if (find(id) == null) {
                                        XLogger.getInstance().log(Level.WARNING,
                                        "The "+this.getEntityClass()+" with id " + id + " no longer exists.",
                                        this.getClass(), ex);
                                    }
                                }

                            }catch(UnsupportedOperationException e) {
                                XLogger.getInstance().log(Level.WARNING, null, this.getClass(), e);
                            }
                        }

                        break;
                    }    
                }
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    updated = 0;
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return updated;
    }
    
    @Override
    public void edit(E entity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                entity = em.merge(entity); 
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } catch (Exception ex) {
            
            String msg = ex.getLocalizedMessage();
            
            if (msg == null || msg.length() == 0) {
                
                try{
                    
                    e id = this.getId(entity);
            
                    if(id != null) {
                        if (find(id) == null) {
                            throw new NonexistentEntityException(
                            "The "+this.getEntityClass()+" with id " + id + " no longer exists.");
                        }
                    }
                    
                }catch(UnsupportedOperationException e) {
                    XLogger.getInstance().log(Level.WARNING, null, this.getClass(), e);
                }
            }
            
            throw ex;
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     */
    @Override
    public E find(e id) {
        EntityManager em = getEntityManager();
        try {
            return (E)em.find(this.getEntityClass(), id);
        } finally {
            em.close();
        }
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     */
    @Override
    public List<E> find() {
        return find(true, -1, -1);
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     */
    @Override
    public List<E> find(int maxResults, int firstResult) {
        return find(false, maxResults, firstResult);
    }

    private List<E> find(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(this.getEntityClass()));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<E> rt = cq.from(this.getEntityClass());
            cq.select(cb.count(rt));
            Query q = em.createQuery(cq);
            return ((Long)q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    /**
     * @param entity Entity whose Id is to be returned
     * @return The id of the specified entity
     * @throws IllegalArgumentException If no method matching the  
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public e getId(E entity) 
            throws IllegalArgumentException, UnsupportedOperationException {
        return (e)this.getValue(entity, this.getMetaData().getIdColumnName(this.entityClass));
    }

    /**
     * @param entity Entity whose value is to be returned
     * @param columnName The columnName matching the field whose value is 
     * to be returned
     * @return The value of the field whose name matches the specified columnName
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public Object getValue(E entity, String columnName) 
            throws IllegalArgumentException, UnsupportedOperationException {

XLogger.getInstance().log(Level.FINER, "Entity class. From controller: {0}, From entity: {1}", 
        this.getClass(), this.getEntityClass(), entity.getClass());

        if(columnName == null) {
            throw new NullPointerException();
        }
        if(entity == null) {
            throw new NullPointerException();
        }
        
        Method method = null;
        try{
            
            method = JpaUtil.getMethod(
                    false, this.getMethods(), columnName);
            
XLogger.getInstance().log(Level.FINER, "Entity: {0}, Column name: {1}, Getter method: {2}",
this.getClass(), entity, columnName, (method==null?null:method.getName()));

            if(method != null) {
                return method.invoke(entity);
            }
            
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            
            StringBuilder builder = new StringBuilder("Error getting entity value.");
            builder.append(" Entity: ").append(entity);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(columnName);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
        
        if(method == null) {
            throw new IllegalArgumentException(
                    "Could not find matching getter method for field: "+
                    columnName+" in class: "+entity.getClass());
        }
        
        return null;
    }

    /**
     * @param entity Entity whose Id is to be updated with a new value
     * @param id The new id
     * @throws IllegalArgumentException If no method matching the specified
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public void setId(E entity, e id) 
            throws IllegalArgumentException, UnsupportedOperationException {
        
        this.setValue(entity, this.getMetaData().getIdColumnName(this.entityClass), id);
    }

    /**
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public void setValue(E entity, String columnName, Object columnValue) 
            throws IllegalArgumentException, UnsupportedOperationException {        
        
XLogger.getInstance().log(Level.FINER, "Entity class. From controller: {0}, From entity: {1}", 
this.getClass(), this.getEntityClass(), entity.getClass());
        
        if(columnName == null) {
            throw new NullPointerException();
        }
        if(entity == null) {
            throw new NullPointerException();
        }
        Method method = null;
        try{
            
            method = JpaUtil.getMethod(
                    true, this.getMethods(), columnName);
            
XLogger.getInstance().log(Level.FINER, "Entity: {0}, Column name: {1}, Column value: {2}, Setter method: {3}", 
this.getClass(), entity, columnName, columnValue, (method==null?null:method.getName()));

            if(method != null) {
                
                // We do this because MySQL returns Byte for tinyint where as 
                // jpa designates the columns as shorts
                //
                // Only one parameter expected
                if(columnValue != null) {
                    columnValue = this.convertPrimitive(method.getParameterTypes()[0], columnValue);
                }

                method.invoke(entity, columnValue);
            }
        }catch(Exception e) {
            
            StringBuilder builder = new StringBuilder("Error setting entity value.");
            builder.append(" Entity: ").append(entity);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(columnName);
            builder.append(", Value: ").append(columnValue);
            builder.append(", Value type: ").append(columnValue==null?null:columnValue.getClass());
            builder.append(", Expected type: ").append(method==null?null:method.getParameterTypes()[0]);

            throw new UnsupportedOperationException(builder.toString(), e);
        }

        if(method == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+this.getEntityClass());
        }
    }

    private Object convertPrimitive(Class type, Object value) {
        if(value == null) {
            return value;
        }
        if((type == short.class || type == Short.class) && !(value instanceof Short)) {
            value = Short.valueOf(value.toString());
        }else if((type == int.class || type == Integer.class) && !(value instanceof Integer)) {
            value = Integer.valueOf(value.toString());
        }else if((type == long.class || type == Long.class) && !(value instanceof Long)) {
            value = Long.valueOf(value.toString());
        }else if((type == float.class || type == Float.class) && !(value instanceof Float)) {
            value = Float.valueOf(value.toString());
        }else if((type == double.class || type == Double.class) && !(value instanceof Double)) {
            value = Double.valueOf(value.toString());
        }else if((type == boolean.class || type == Boolean.class) && !(value instanceof Boolean)) {
            value = Boolean.valueOf(value.toString());
        }
        return value;
    }
    
    public Method[] getMethods() {
        if(accessViaGetter_methods == null) {
            accessViaGetter_methods = this.entityClass.getMethods();
        }
        return accessViaGetter_methods;
    }

    @Override
    public String getDatabaseName() {
        return this.getMetaData().getDatabaseName(this.entityClass);
    }

    @Override
    public String getTableName() {
        return this.getMetaData().getTableName(this.entityClass);
    }
    
    @Override
    public void setTableName(String tableName) {
        final PersistenceMetaData metaData = this.getMetaData();
        String databaseName = metaData.getDatabaseName(entityClass);
        Class aClass = metaData.getEntityClass(databaseName, tableName);
        // Use this method as it performs important logic
        this.setEntityClass(aClass);
    }

    @Override
    public String getIdColumnName() {
        return this.getMetaData().getIdColumnName(this.entityClass);
    }
    
    @Override
    public Class<E> getEntityClass() {
        return this.entityClass;
    }
    
    @Override
    public void setEntityClass(Class<E> aClass) {
        if(this.entityClass != aClass) {
            this.checkRelationship(aClass);
            this.accessViaGetter_methods = null;
        }
        this.entityClass = aClass;
    }
    
    private void checkRelationship(Class anEntityClass) {
        
        if(this.entityClass == null || anEntityClass == null) {
            return;
        }
        
        final PersistenceMetaData metaData = this.getMetaData();
        
        boolean aIsRef = metaData.getReferencingClasses(entityClass) != null;
        boolean bIsRef = metaData.getReferencingClasses(anEntityClass) != null;
        
        boolean match;
        if(aIsRef == bIsRef) {
            boolean aIsRefing = metaData.getReferenceClasses(entityClass) != null;
            boolean bIsRefing = metaData.getReferenceClasses(anEntityClass) != null;
            if(aIsRefing == bIsRefing) {
                match = true;
            }else{
                match = false;
            }
        }else{
            match = false;
        }
        
        if(!match) {
            StringBuilder msg = new StringBuilder();
            msg.append("Tried to replace the current entity class: ");
            msg.append(this.entityClass);
            msg.append(" with an incompatible class: ");
            msg.append(anEntityClass);
            throw new IllegalArgumentException(msg.toString());
        }
    }
    
    public final JpaContext getJpaContext() {
        return this.jpaContext;
    }

    @Override
    public final PersistenceMetaData getMetaData() {
        return this.jpaContext.getMetaData();
    }
}

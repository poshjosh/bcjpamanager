package com.bc.jpa;

import com.bc.util.XLogger;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
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
public class BaseEntityController<E, e> extends EntityUpdaterImpl<E, e> implements EntityControllerBase<E, e> {

    public BaseEntityController(JpaContext jpaContext, Class<E> entityClass) {
        
        super(jpaContext, entityClass);
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
     * @param id
     * @return 
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
     * @return 
     */
    @Override
    public List<E> find() {
        return find(true, -1, -1);
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     * @param maxResults
     * @param firstResult
     * @return 
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
    
    @Override
    public String getDatabaseName() {
        return this.getMetaData().getDatabaseName(this.getEntityClass());
    }

    @Override
    public String getTableName() {
        return this.getMetaData().getTableName(this.getEntityClass());
    }
    
    @Override
    public String getIdColumnName() {
        return this.getMetaData().getIdColumnName(this.getEntityClass());
    }
}
/**
 * 
    
    private void checkRelationship(Class anEntityClass) {
        
        final Class entityClass = this.getEntityClass();
        
        if(entityClass == null || anEntityClass == null) {
            return;
        }
        
        final JpaMetaData metaData = this.getMetaData();
        
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
            msg.append(entityClass);
            msg.append(" with an incompatible class: ");
            msg.append(anEntityClass);
            throw new IllegalArgumentException(msg.toString());
        }
    }
 * 
 */

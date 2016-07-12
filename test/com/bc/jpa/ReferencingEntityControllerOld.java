package com.bc.jpa;

import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

/**
 * @(#)ReferencingEntityControllerOld.java   19-Aug-2014 20:30:12
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
public class ReferencingEntityControllerOld<K, k, E> 
        extends ReferenceTypeEntityController<K, k, E, K>{
    
    public ReferencingEntityControllerOld(JpaContext jpaContext, Class referencingClass) {
    
        super(jpaContext, referencingClass);
    }
    
    private Class getReferenceClass() {
//        return this.getMetaData().getReferenceClasses(this.getEntityClass());
        throw new UnsupportedOperationException();
    }
    
    private String getReferenceIdColumn() {
        return this.getMetaData().getIdColumnName(this.getReferenceClass());
    }
    
    private Object getReferenceIdValue(E reference) {
        return getReferenceValue(reference, this.getReferenceIdColumn());
    }
    
    private Object getReferenceValue(E reference, String columnName) {
        return JpaUtil.getValue(reference.getClass(), 
                reference, null, columnName);
    }

    private void setReferenceValue(E reference, String columnName, Object columnValue) {
        JpaUtil.setValue(reference.getClass(), 
                reference, null, columnName, columnValue);
    }
    
    @Override
    public void create(K referencing) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
//            E reference = (E)this.getReference(referencing);
            E reference = null;
            
            if (reference != null) {
                
                reference = (E)em.getReference(
                        this.getReferenceClass(), 
                        this.getReferenceIdValue(reference));
                
//                this.setReference(referencing, reference);
            }
            
            em.persist(referencing);
            
            if (reference != null) {
                
                Collection refingList = this.getReferencing(reference, (Class<K>)referencing.getClass());
                
                if(refingList != null) {
                    refingList.add(referencing);
                }
                
                reference = em.merge(reference);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception ex) {
            k id = this.getId(referencing); 
            if(id != null) {
                if (find(id) != null) {
                    throw new PreexistingEntityException(
                    this.getEntityClass().getSimpleName()+
                    " " + referencing + " already exists.", ex);
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(K refing) throws NonexistentEntityException, Exception {
        
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            em.getTransaction().begin(); 
            
            K persistentRefing = (K)em.find(
                    this.getEntityClass(), 
                    this.getId(refing));
            
            PersistenceMetaData metaData = this.getMetaData();
            
//            Class refClass = metaData.getReferenceClasses(this.getEntityClass());
            Class refClass = null;
            
            String crossRefColumn = metaData.getReferenceColumn(
                    refClass, this.getEntityClass());
            
            E crossrefIdOld = (E)this.getValue(persistentRefing, crossRefColumn);
            E crossrefIdNew = (E)this.getValue(refing, crossRefColumn);
            
            if (crossrefIdNew != null) {

                crossrefIdNew = (E)em.getReference(this.getReferenceClass(), 
                        this.getReferenceIdValue(crossrefIdNew));

                this.setValue(refing, crossRefColumn, crossrefIdNew);
            }
            
            refing = em.merge(refing);
            
            if (crossrefIdOld != null && !crossrefIdOld.equals(crossrefIdNew)) {
                
                Collection refingList = this.getReferencing(crossrefIdOld, this.getEntityClass());
                if(refingList != null) {
                    refingList.remove(refing);
                }
                
                crossrefIdOld = em.merge(crossrefIdOld);
            }
            
            if (crossrefIdNew != null && !crossrefIdNew.equals(crossrefIdOld)) {
                
                Collection refingList = this.getReferencing(crossrefIdNew, this.getEntityClass());
                if(refingList != null) {
                    refingList.add(refing);
                }
                
                crossrefIdNew = em.merge(crossrefIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                k id = (k)this.getId(refing);
                if (find(id) == null) {
                    throw new NonexistentEntityException("The siteurl with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void destroy(k id) throws NonexistentEntityException {
        
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            K refing;
            try {
                refing = (K)em.getReference(
                        this.getEntityClass(), id);
//                refingIdCol.getValue(refing);
//                refing.getSiteurlid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The siteurl with id " + id + " no longer exists.", enfe);
            }
            
//            Class refClass = this.getMetaData().getReferenceClasses(this.getEntityClass());
            Class refClass = null;
            
            String crossRefColumn = this.getMetaData().getReferenceColumn(
                    refClass, this.getEntityClass());
           
            E ref = (E)this.getValue(refing, crossRefColumn);
            if (ref != null) {
                Collection refingList = this.getReferencing(ref, this.getEntityClass());
                if(refingList != null) {
                    refingList.remove(refing);
                }
                ref = em.merge(ref);
            }
            em.remove(refing);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}

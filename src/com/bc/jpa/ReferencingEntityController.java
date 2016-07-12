package com.bc.jpa;

import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

/**
 * @(#)ReferencingEntityController.java   08-Dec-2013 04:07:47
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
public class ReferencingEntityController<K, k, E> 
        extends ReferenceTypeEntityController<K, k, E, K>{
    
    public ReferencingEntityController(JpaContext jpaContext, Class referencingClass) {
    
        super(jpaContext, referencingClass);
    }
    
    private String getReferenceIdColumn(Class referenceClass) {
        return this.getMetaData().getIdColumnName(referenceClass);
    }
    
    private Object getReferenceIdValue(E reference, Class referenceClass) {
        return getReferenceValue(reference, this.getReferenceIdColumn(referenceClass));
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
            
            Map<String, String> refs = 
                    this.getMetaData().getReferences(
                    this.getEntityClass());

            Field [] fields = this.getEntityClass().getDeclaredFields();
            
            for(String refingColumn:refs.keySet()) {
                
                E reference = (E)JpaUtil.getValue(this.getEntityClass(), 
                    referencing, fields, refingColumn);
                
                Class refClass = this.getMetaData().getReferenceClass(
                        this.getEntityClass(), refingColumn);
                
                if (reference != null) {

                    reference = (E)em.getReference(
                            refClass, 
                            this.getReferenceIdValue(reference, refClass));

                    JpaUtil.setValue(this.getEntityClass(), 
                            referencing, fields, refingColumn, reference);
                    
//                    this.setReference(referencing, reference, refClass);
                }

                if (reference != null) {

                    Collection refingList = this.getReferencing(reference, (Class<K>)referencing.getClass());

                    if(refingList != null) {
                        refingList.add(referencing);
                    }

                    reference = em.merge(reference);
                }
            }
            
            em.persist(referencing);
            
            em.getTransaction().commit();
            
        } catch (Exception ex) {
            k id = this.getId(referencing); 
            if(id != null) {
                if (find(id) != null) {
                    throw new PreexistingEntityException(
                    this.getEntityClass().getName()+
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
            
            k refId = this.getId(refing);
            
            K persistentRefing;
            if(refId == null) {
                throw new UnsupportedOperationException("Cannot edit an entity without an id value: "+refing);
            }else{
                persistentRefing = (K)em.find(this.getEntityClass(), refId);
            }
            
            final Class refingClass = this.getEntityClass();
            
            Map<String, String> refs = this.getMetaData().getReferences(refingClass);
            
            for(String refingColumn:refs.keySet()) {

                E crossrefIdOld = (E)this.getValue(persistentRefing, refingColumn);
                E crossrefIdNew = (E)this.getValue(refing, refingColumn);

                if (crossrefIdNew != null) {

                    Class refClass = this.getMetaData().getReferenceClass(
                            refingClass, refingColumn);

                    crossrefIdNew = (E)em.getReference(refClass, 
                            this.getReferenceIdValue(crossrefIdNew, refClass));

                    this.setValue(refing, refingColumn, crossrefIdNew);
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
            }
            
            em.getTransaction().commit();
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                k id = (k)this.getId(refing);
                if (find(id) == null) {
                    throw new NonexistentEntityException("The "+this.getEntityClass().getName()+" with id " + id + " no longer exists.");
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
                throw new NonexistentEntityException(
                        "The "+this.getEntityClass().getSimpleName()+
                        " with id " + id + " no longer exists.", enfe);
            }
            
            final Class refingClass = this.getEntityClass();
            
            Map<String, String> refs = this.getMetaData().getReferences(refingClass);
            
            for(String refingColumn:refs.keySet()) {
                
                E ref = (E)this.getValue(refing, refingColumn);
                if (ref != null) {
                    Collection refingList = this.getReferencing(ref, this.getEntityClass());
                    if(refingList != null) {
                        refingList.remove(refing);
                    }
                    ref = em.merge(ref);
                }
            }
            
            em.remove(refing);
            
            em.getTransaction().commit();
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void destroyOld(k id) throws NonexistentEntityException {
        
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
                throw new NonexistentEntityException(
                        "The "+this.getEntityClass().getSimpleName()+
                        " with id " + id + " no longer exists.", enfe);
            }
            
            Class [] refClasses = this.getMetaData().getReferenceClasses(this.getEntityClass());
            
            for(Class refClass:refClasses) {
                
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

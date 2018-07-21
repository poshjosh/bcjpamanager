package com.bc.jpa.controller;

import com.bc.jpa.EntityReference;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;

/**
 * @(#)ReferenceEntityController.java   08-Dec-2013 02:41:35
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
public class ReferenceEntityController<E, e, K> 
        extends ReferenceTypeEntityController<E, e, E, K>{

    private transient static final Logger LOG = Logger.getLogger(ReferenceEntityController.class.getName());
    
    public ReferenceEntityController(
            PersistenceUnitContext persistenceUnitContext, Class referenceClass) {
    
        super(persistenceUnitContext, referenceClass);
    }
    
    private Object getReferencingValue(K referencing, String columnName) {
        return this.getValue(referencing.getClass(), 
                referencing, null, columnName);
    }

    private void setReferencingValue(K referencing, String columnName, Object columnValue) {
        this.setValue(referencing.getClass(), 
                referencing, null, columnName, columnValue);
    }
    
    @Override
    public void persist(E reference) {
        
        final EntityReference er = this.getPersistenceUnitContext().getEntityReference();
        
        Class [] refingClasses = er.getReferencingClasses(this.getEntityClass());
        
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            
            em.getTransaction().begin();
            
            this.updateReferencing(em, reference);
            
            em.persist(reference);

            for(Class refingClass:refingClasses) {

                Collection<K> refingList = (Collection<K>)
                        this.getReferencing(reference, refingClass);
                
                if(refingList == null) {
//                    this.setReferencing(reference, new ArrayList<K>(), refingClass);
                    continue;
                }else if(refingList.isEmpty()) {
                    continue;
                }

                for (K referencing : refingList) {

                    Map<String, String> refs = er.getReferences(refingClass);

                    Field [] fields = refingClass.getDeclaredFields();

                    for(String refingColumn:refs.keySet()) {

                        E refOldId = (E)this.getValue(refingClass, 
                            referencing, fields, refingColumn);

                        this.setValue(refingClass, 
                                referencing, fields, refingColumn, reference);
                        
//                        this.setReference(referencing, reference, this.getEntityClass());

                        referencing = em.merge(referencing);

                        if(refOldId != null) {

                            Collection c = this.getReferencing(refOldId, refingClass);

                            if(c != null) {
                                c.remove(referencing);
                            }

                            refOldId = em.merge(refOldId);
                        }
                    }    
                }
            }
            
            em.getTransaction().commit();
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    @Override
    public E merge(E reference) throws IllegalOrphanException, NonexistentEntityException, Exception {

        Objects.requireNonNull(reference);
        
        EntityReference er = this.getPersistenceUnitContext().getEntityReference();

        Class [] refingClasses = er.getReferencingClasses(this.getEntityClass());
        
        PersistenceUnitMetaData metaData = this.getPersistenceUnitContext().getMetaData();
        
        LOG.fine(() -> "Entity class: "+this.getEntityClass()+", referencing classes: " +
                (refingClasses==null?null:Arrays.toString(refingClasses)));
        
        EntityManager em = this.getEntityManager();
        
        e refId = null;
        
        try {
            
            EntityTransaction t = em.getTransaction();
            
            try{
                
                t.begin();
                
                refId = this.getId(reference);

                E referenceFromDb;
                if(refId == null) {
                    throw new UnsupportedOperationException("Cannot edit an entity without an id value. Entity of type: "+reference.getClass().getName());
                }else{
                    referenceFromDb = (E)em.find(this.getEntityClass(), refId);
                }
                
                Collection [] arrOld = new Collection[refingClasses.length];
                Collection [] arrNew = new Collection[refingClasses.length];

                Map<Class, String> crossRefColumns = null;

                for(int i=0; i<refingClasses.length; i++) {

                    Class refingClass = refingClasses[i];

                    Collection<K> refingListFromDb = this.getReferencing(referenceFromDb, refingClass);

this.printReferencingList(
        "Printing referencing list of reference entity from database", 
        referenceFromDb, refingClass, refingListFromDb);

                    if(refingListFromDb == null || refingListFromDb.isEmpty()) {
                        arrOld[i] = null;
                        arrNew[i] = null;
                        continue;
                    }

                    Collection<K> refingListFromInput = this.getReferencing(reference, refingClass);

this.printReferencingList(
        "Printing referencing list of reference entity from input", 
        referenceFromDb, refingClass, refingListFromInput);

                    if(refingListFromInput == null || refingListFromInput.isEmpty()) {
                        arrOld[i] = null;
                        arrNew[i] = null;
                        continue;
                    }

                    String refingIdColumn = metaData.getIdColumnName(refingClass);

                    String crossRefColumn = er.getReferenceColumn(this.getEntityClass(), refingClass);

                    if(LOG.isLoggable(Level.FINER)) {                    
                        LOG.log(Level.FINER, "Refing class: {0}, id column: {1}, cross ref column: {2}",
                        new Object[]{refingClass, refingIdColumn, crossRefColumn});
                    }
                    
                    if(crossRefColumns == null) {
                        crossRefColumns = new HashMap(refingClasses.length, 1.0f);
                    }
                    crossRefColumns.put(refingClass, crossRefColumn);

                    this.validateReferencingList(refingClass, crossRefColumn, 
                                    refingListFromDb, refingListFromInput);

                    refingListFromInput = 
                            this.updateReferencingList(
                            em, reference, refingClass, 
                            refingIdColumn, refingListFromInput);

                    arrOld[i] = refingListFromDb;
                    arrNew[i] = refingListFromInput;
                }

                reference = em.merge(reference); 

                for(int i=0; i<refingClasses.length; i++) {
                    
                    if(crossRefColumns == null) {
                        break;
                    }

                    if(arrOld[i] == null || arrOld[i].isEmpty() ||
                            arrNew[i] == null || arrNew[i].isEmpty()) {
                        continue;
                    }

                    String crossRefColumn = crossRefColumns.get(refingClasses[i]);

                    this.afterEdit(em, reference, refingClasses[i], 
                            crossRefColumn, arrOld[i], arrNew[i]);
                }
             
                t.commit();
                
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } catch (IllegalArgumentException | UnsupportedOperationException | IllegalOrphanException ex) {
            
            String msg = ex.getLocalizedMessage();
            
            if (msg == null || msg.length() == 0) {

                if(refId != null) {
                    
                    if (find(refId) == null) {

                        throw new NonexistentEntityException("The "+this.getEntityClass().getName()+" with id " + refId + " no longer exists.");
                    }
                }
            }
            
            throw ex;
            
        } finally {
            em.close();
        }
        
        return reference;
    }

    @Override
    public void remove(e id) throws IllegalOrphanException, NonexistentEntityException {
        
        EntityManager em = null;
        try {
            
            em = getEntityManager();
            
            em.getTransaction().begin();
            
            E reference;
            try {
                
                reference = (E)em.getReference(
                        this.getEntityClass(), id);
                
                this.getId(reference); // This ensures the entity actually exists
                
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException(
                        "The "+this.getEntityClass().getName()+
                        " with id " + id + " no longer exists.", enfe);
            }
            
            List<String> illegalOrphanMessages = null;

            StringBuilder msgBuffer = null;

            EntityReference er = this.getPersistenceUnitContext().getEntityReference();
            
            Class [] refingClasses = er.getReferencingClasses(this.getEntityClass());

            for(Class refingClass:refingClasses) {
                
                if(illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                
                if(msgBuffer == null) {
                    msgBuffer = new StringBuilder();
                }

                Collection<K> referencingListOrphanCheck =
                        this.getReferencing(reference, refingClass);

                String crossRefColumn = er.getReferenceColumn(
                        this.getEntityClass(), refingClass);
            
                
                if(referencingListOrphanCheck != null && !referencingListOrphanCheck.isEmpty()) {
                    this.appendErrorMessage(referencingListOrphanCheck, refingClass, 
                        reference, crossRefColumn, illegalOrphanMessages, msgBuffer);
                }
            }
            
            if (illegalOrphanMessages != null && !illegalOrphanMessages.isEmpty()) {
                
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            
            em.remove(reference);
            
            em.getTransaction().commit();
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    private boolean updateReferencing(EntityManager em, E reference) {
        
        final e ID = this.getId(reference);
        
        if(ID == null) {
            
            return false;
            
        }else{
            
            Class [] refingClasses = this.getPersistenceUnitContext().getEntityReference()
                    .getReferencingClasses(this.getEntityClass());

            ArrayList<K> attached = new ArrayList<>();

            for(Class refingClass:refingClasses) {

                Collection<K> refingList = (Collection<K>)
                        this.getReferencing(reference, refingClass);

                if(refingList == null || refingList.isEmpty()) {
                    continue;
                }

                for (K referencing : refingList) {

                    referencing = (K)em.getReference(refingClass, ID);

                    attached.add(referencing);
                }

                this.setReferencing(reference, attached, refingClass);
            }
            
            return true;
        }
    }

    private void validateReferencingList(
            Class refingClass, String crossRefColumn,
            Collection<K> refingListFromDb, 
            Collection<K> refingListFromInput) 
            throws IllegalOrphanException {
        
        StringBuilder msgBuffer = null;

        List<String> illegalOrphanMessages = null;

        for (K refingEntityFromDb : refingListFromDb) {

            if (!refingListFromInput.contains(refingEntityFromDb)) {
                
                if(msgBuffer == null) {
                    msgBuffer = new StringBuilder();
                }else{
                    msgBuffer.setLength(0);
                }
                
                msgBuffer.append("You must retain ").append(refingClass.getSimpleName());
                msgBuffer.append(' ').append(refingEntityFromDb).append(" since its ");
                msgBuffer.append(crossRefColumn).append(" field is not nullable.");

                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                
                illegalOrphanMessages.add(msgBuffer.toString());
            }
        }

        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
    }
    
    private Collection<K> updateReferencingList(
            EntityManager em, E reference, 
            Class refingClass, String refingIdColumn,             
            Collection<K> refingListFromInput) 
            throws IllegalOrphanException {

        List<K> refingListFromInput_updatedFromDb = new ArrayList<>(refingListFromInput.size());

        for (K refingEntityFromInput : refingListFromInput) {

            Object refingValue = this.getReferencingValue(refingEntityFromInput, refingIdColumn);
            
            K refingEntityFromInput_updatedFromDb = (K)em.getReference(
                    refingEntityFromInput.getClass(), refingValue);

            refingListFromInput_updatedFromDb.add(refingEntityFromInput_updatedFromDb);
        }

this.printReferencingList(
        "Printing referencing list updated from database for input reference entity", 
        reference, refingClass, refingListFromInput_updatedFromDb);

        this.setReferencing(reference, refingListFromInput_updatedFromDb, refingClass);            
        
        return refingListFromInput_updatedFromDb;
    }
    
    private void afterEdit(
            EntityManager em, E reference, 
            Class refingClass, String crossRefColumn,
            Collection<K> refingListFromDb, 
            Collection<K> refingListFromInput) {
        
        Field [] fields = this.getEntityClass().getDeclaredFields();
        
        for (K refingFromInput : refingListFromInput) {

            if (!refingListFromDb.contains(refingFromInput)) {

                E referenceFromDb = (E)this.getReferencingValue(refingFromInput, crossRefColumn);

                if(LOG.isLoggable(Level.FINER)) {                
                    LOG.log(Level.FINER, "Entity type: {0}, entity: {1}, column: {2}, value: {3}",
                    new Object[]{this.getEntityClass().getName(), refingFromInput, crossRefColumn, reference});
                }        
                
                this.setValue(this.getEntityClass(), 
                        refingFromInput, fields, crossRefColumn, reference);
                
//                this.setReference(referencingNew, reference, this.getEntityClass());

                refingFromInput = em.merge(refingFromInput);

                if (referenceFromDb != null && !referenceFromDb.equals(reference)) {

                    Collection<K> refingList = this.getReferencing(referenceFromDb, refingClass);
                    
                    if(refingList != null) {
                        if(LOG.isLoggable(Level.FINER)) {                        
                            LOG.log(Level.FINER, "Removing referencing entity: {0} form list in reference from database: {1}", 
                            new Object[]{refingFromInput, referenceFromDb});
                        }
                        refingList.remove(refingFromInput);
                    }

                    referenceFromDb = em.merge(referenceFromDb);
                }
            }
        }
    }
    
    private void printReferencingList(String prefix, Object ref, Class refingClass, Collection c) {
        if(!LOG.isLoggable(Level.FINER)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(prefix).append('\n');
        if(c == null) {
            builder.append("null");
        }else{
            for(Object o:c) {
                builder.append(o).append('\n');
            }
        }
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Reference: {0}, referencing class: {1}, referencing list:\n{2}",
            new Object[]{ref, refingClass == null ? null : refingClass.getName(), builder});
        }
    }
    
    private void appendErrorMessage(Collection<K> referencingListOrphanCheck, 
            Class referencingType, E reference, String crossReferenceColumn,
            List illegalOrphanMessages, StringBuilder msgBuff) {
        
        for (K entity_referencingListOrphanCheck : referencingListOrphanCheck) {

            msgBuff.setLength(0);
            msgBuff.append("This ").append(this.getEntityClass().getName());  
            msgBuff.append(" (").append(reference).append(") cannot be destroyed since the ");
            msgBuff.append(referencingType.getName());
            msgBuff.append(" ").append(entity_referencingListOrphanCheck);
            msgBuff.append(" in its ").append("referencing List");
            msgBuff.append(" field has a non-nullable ").append(
                    crossReferenceColumn).append(" field.");

            illegalOrphanMessages.add(msgBuff.toString());
        }
    }
}

package com.bc.jpa;

import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @(#)AbstractRelatedEntityController.java   17-Jun-2014 19:20:04
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
public abstract class ReferenceTypeEntityController<E, e, X, Y> 
        extends DefaultEntityController<E, e>
        implements EntityController<E, e> {
    
    public ReferenceTypeEntityController() { }
    
    public ReferenceTypeEntityController(
            PersistenceMetaData puMeta, 
            Class<E> entityClass) {
    
        super(puMeta, entityClass);
    }
    
    @Override
    public abstract void create(E entity) throws PreexistingEntityException, Exception;

    @Override
    public abstract void destroy(e id) throws IllegalOrphanException, NonexistentEntityException;
    
    @Override
    public abstract void edit(E entity) throws NonexistentEntityException, Exception;
    
    public <Y> Collection<Y> getReferencing(X reference, Class<Y> referencingClass) 
            throws UnsupportedOperationException {
        
        Method method = JpaUtil.getMethod(false, 
                this.getEntityClass(), referencingClass);
        
        if(method == null) {
            return null;
        }
        
        try{
            return (Collection<Y>)method.invoke(reference);
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    } 

    public void setReferencing(X reference, Collection<Y> referencing, Class<Y> referencingClass) 
            throws UnsupportedOperationException {
        
        Method method = JpaUtil.getMethod(true, 
                this.getEntityClass(), referencingClass);
        
//        if(method == null) {
//            return;
//        }
        
        try{
            method.invoke(reference, referencing);
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    } 
}

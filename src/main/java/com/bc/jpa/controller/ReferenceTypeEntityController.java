package com.bc.jpa.controller;

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.util.JpaUtil;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.lang.reflect.Field;
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
        extends EntityControllerImpl<E, e>
        implements EntityController<E, e> {
    
    public ReferenceTypeEntityController(
            PersistenceUnitContext persistenceUnitContext, Class<E> entityClass) {
    
        super(persistenceUnitContext, entityClass);
    }
    
    @Override
    public abstract void persist(E entity) throws PreexistingEntityException, Exception;

    @Override
    public abstract void remove(e id) throws IllegalOrphanException, NonexistentEntityException;
    
    @Override
    public abstract E merge(E entity) throws NonexistentEntityException, Exception;
    
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

    public Object getValue(Class aClass, 
            Object entity, Field [] fields, String columnName) {
        
        if(fields == null) {
            fields = aClass.getDeclaredFields();
        }
        
        String methodName = JpaUtil.getMethodName(false, fields, columnName);

        if(methodName == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
        
        Method method = null;
        try{
            
            method = aClass.getMethod(methodName);
            
            if(method != null) {
                return method.invoke(entity);
            }
            
        }catch(Exception e) {
            
            StringBuilder builder = new StringBuilder("Error getting entity value.");
            builder.append(" Entity: ").append(entity);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(columnName);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
        
        if(method == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
        
        return null;
    }

    public void setValue(Class aClass, 
            Object entity, Field [] fields, 
            String columnName, Object columnValue) {
        
        if(fields == null) {
            fields = aClass.getDeclaredFields();
        }
        
        String methodName = JpaUtil.getMethodName(true, fields, columnName);
        
        if(methodName == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
        
        Method method = null;
        try{
            
            method = aClass.getMethod(methodName, columnValue.getClass());
            
            if(method != null) {
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
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
    }
}

package com.bc.jpa;

import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;

/**
 * @(#)RelatedEntityController.java   22-Aug-2014 12:45:28
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
public class RelatedEntityController extends DefaultEntityController {

    public RelatedEntityController(JpaContext jpaContext, Class referenceClass) {
    
        super(jpaContext, referenceClass);
    }
    
    @Override
    public void create(Object entity) throws PreexistingEntityException, Exception {
        
        // We give precedence to referencing here
        //
        if (this.isReferencing()) {
           this.getReferencingController().create(entity); 
        }else if(this.isReference()){
            this.getReferenceController().create(entity);
        }else{
            super.create(entity);
        }
    }

    @Override
    public void destroy(Object id) throws IllegalOrphanException, NonexistentEntityException {

        // We give precedence to reference here
        //
        if(this.isReference()) {
            this.getReferenceController().destroy(id);
        }else if(this.isReferencing()){
            this.getReferencingController().destroy(id);
        }else{    
            super.destroy(id);
        }
    }

    @Override
    public void edit(Object entity) throws NonexistentEntityException, Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private boolean isReference() {
        return false;
    }

    private boolean isReferencing() {
        return false;
    }
    
    private EntityController getReferenceController() {
        return null;
    }

    private EntityController getReferencingController() {
        return null;
    }
    
    private EntityController getDefaultController() {
        return null;
    }
    
    private EntityController getDelegate() {
        EntityController controller;
        Class entityClass = this.getEntityClass();
        PersistenceMetaData metaData = this.getMetaData();
        boolean isReference = metaData.getReferencingClasses(
                entityClass) != null;
        boolean isReferencing = metaData.getReferenceClasses(
                entityClass) != null;
        if(isReferencing) {
            controller = new ReferencingEntityController(this.getJpaContext(), entityClass);
        }else if(isReference) {        
            controller = new ReferenceEntityController(this.getJpaContext(), entityClass);
        }else {        
            throw new UnsupportedOperationException();
        }
        return controller;
    }
}

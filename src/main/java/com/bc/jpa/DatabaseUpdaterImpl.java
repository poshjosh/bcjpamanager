/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa;

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 30, 2017 10:05:02 PM
 */
public class DatabaseUpdaterImpl implements DatabaseUpdater, Serializable {
    
    private transient static final Logger logger = Logger.getLogger(DatabaseUpdaterImpl.class.getName());
    
    private final PersistenceUnitContext puContext;
    
    private final Predicate filter;
    
    private final Function formatter;

    public DatabaseUpdaterImpl(PersistenceUnitContext puContext) {
        this(puContext, (e) -> true, (e) -> e);
    }
    
    public DatabaseUpdaterImpl(
            PersistenceUnitContext puContext, Predicate filter, Function formatter) {
        this.puContext = Objects.requireNonNull(puContext);
        this.filter = Objects.requireNonNull(filter);
        this.formatter = Objects.requireNonNull(formatter);
    }
    
    public int persist(Collection entities) {
        int created = 0;
        final EntityManager em = this.getEntityManager();
        try {
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                for(Object entity:entities) {
                    try{
                        em.persist(entity); 
                        ++created;
                    }catch(Exception e) {
                        logger.log(Level.WARNING, "Exception creating entity of type: "+entity.getClass().getName(), e);
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

    public void persist(Object entity) throws PreexistingEntityException, Exception {
        final EntityManager em = this.getEntityManager();
        try {
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

    public <E> int remove(Class<E> entityClass, Collection ids) {
        int destroyed = 0;
        EntityManager em = getEntityManager();
        try {
            EntityTransaction t = em.getTransaction();
            try{
                
                t.begin();
                
                for(Object id:ids){
                    E entity;
                    try {
                        entity = em.getReference(entityClass, id);
                    } catch (EntityNotFoundException enfe) {
                        logger.log(Level.WARNING, "The "+entityClass+" with id " + id + " no longer exists.", enfe);
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
    
    public <E> void remove(Class<E> entityClass, Object id) throws IllegalOrphanException, NonexistentEntityException {
        final EntityManager em = this.getEntityManager();
        try {
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                
                E entity;
                try {
                    entity = em.getReference(entityClass, id);
                } catch (EntityNotFoundException enfe) {
                    throw new NonexistentEntityException(
                    "The "+entityClass+" with id " + id + " no longer exists.", enfe);
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

    public <E> int merge(Class<E> entityClass, Collection<E> entities) {
        int updated = 0;
        final EntityManager em = this.getEntityManager();
        try {
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

                                final Object id = this.getId(entity);

                                if(id != null) {
                                    if (em.find(entityClass, id) == null) {
                                        logger.log(Level.WARNING,
                                        "The "+entityClass+" with id " + id + " no longer exists.", ex);
                                    }
                                }

                            }catch(UnsupportedOperationException e) {
                                logger.log(Level.WARNING, null, e);
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
    
    public <E> void merge(Class<E> entityClass, E entity) throws NonexistentEntityException, Exception {
        final EntityManager em = this.getEntityManager();
        try {
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
                    
                    final Object id = this.getId(entity);
            
                    if(id != null) {
                        if (em.find(entityClass, id) == null) {
                            throw new NonexistentEntityException(
                            "The "+entityClass+" with id " + id + " no longer exists.");
                        }
                    }
                    
                }catch(UnsupportedOperationException e) {
                    logger.log(Level.WARNING, null, e);
                }
            }
            
            throw ex;
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public EntityManager getEntityManager() {
        return this.puContext.getEntityManager();
    }
    
    @Override
    public final Optional mergeIfFound(Object o) {
        if(!this.mayMerge(o) || !filter.test(o)) {
            return Optional.empty();
        }else{
            return this.puContext.executeTransaction((em) -> {
                Object output;
                try{
                    final Object entity = formatter.apply(o);
                    final EntityMemberAccess updater = puContext.getEntityMemberAccess(entity.getClass());
                    final Map properties = Collections.singletonMap(
                            QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
                    final Object found = em.find(entity.getClass(), updater.getId(entity), properties);
                    if(found != null) {
                        logger.fine(() -> "Merging entity: " + entity);
                        output = em.merge(entity); 
                    }else{
                        output = null;
                    }
                }catch(RuntimeException e){
                    logger.log(Level.WARNING, "Exception pesisteing entity: " + o, e);
                    output = null;
                }    
                return Optional.ofNullable(output);
            });
        }
    }
    
    public boolean mayMerge(Object o) {
        return true;
    }
    
    @Override
    public final boolean persistIfNotFound(Object o) {
        if(!this.mayPersist(o) || !filter.test(o)) {
            return false;
        }else{
            return this.puContext.executeTransaction((em) -> {
                try{
                    final Object entity = formatter.apply(o);
                    final EntityMemberAccess updater = puContext.getEntityMemberAccess(entity.getClass());
                    final Map properties = Collections.singletonMap(
                            QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
                    final Object found = em.find(entity.getClass(), updater.getId(entity), properties);
                    if(found == null) {
                        logger.fine(() -> "Persisting entity: " + entity);
//                        logger.fine(() -> "??????? Persisting entity: " + new MapBuilderForEntity().maxDepth(1).maxCollectionSize(0).source(entity).build());
                        em.persist(entity); 
                        return true;
                    }else{
                        return false;
                    }
                }catch(RuntimeException e){
                    logger.log(Level.WARNING, "Exception pesisting entity: " + o, e);
                    return false;
                }    
            });
        }
    }
    
    public boolean mayPersist(Object o) {
        return true;
    }

    @Override
    public final boolean removeIfFound(Object o) {
        if(!this.mayRemove(o) || !filter.test(o)) {
            return false;
        }else{
            return this.puContext.executeTransaction((em) -> {
                try{
                    final Object entity = formatter.apply(o);
                    final Map properties = Collections.singletonMap(
                            QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
                    final Object found = em.find(entity.getClass(), getId(entity), properties);
                    if(found != null) {
                        logger.fine(() -> "Removing entity: " + entity);
                        em.remove(entity); 
                        return true;
                    }else{
                        return false;
                    }
                }catch(RuntimeException e){
                    logger.log(Level.WARNING, "Exception removing entity: " + o, e);
                    return false;
                }    
            });
        }
    }
    
    public boolean mayRemove(Object o) {
        return true;
    }
    
    @Override
    public final Optional updateIfFound(Object o) {
        return this.update(o, false);
    }

    @Override
    public Optional updateOrPersistIfNotFound(Object o) {
        return this.update(o, true);
    }
    
    public Optional update(Object o, boolean persistIfNotFound) {
        if(!this.mayUpdate(o) || !filter.test(o)) {
            return Optional.empty();
        }else{
            return this.puContext.executeTransaction((em) -> {
                Object output;
                try{
                    final Object entity = formatter.apply(o);
                    final EntityMemberAccess updater = puContext.getEntityMemberAccess(entity.getClass());
                    final Map properties = Collections.singletonMap(
                            QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
                    final Object found = em.find(entity.getClass(), updater.getId(entity), properties);
                    if(found != null) {
                        logger.fine(() -> "Updating entity: " + entity);
                        updater.update(entity, found, false);
                        output = found; 
                    }else{
                        if(persistIfNotFound) {
                            logger.fine(() -> "Persisting entity: " + entity);
                            em.persist(o);
                            output = o;
                        }else{
                            output = null;
                        }
                    }
                }catch(RuntimeException e){
                    logger.log(Level.WARNING, "Exception updating entity: " + o, e);
                    output = null;
                }    
                return Optional.ofNullable(output);
            });
        }
    }

    public boolean mayUpdate(Object o) {
        return true;
    }

    private Object getId(Object entity) {
        final EntityMemberAccess updater = puContext.getEntityMemberAccess(entity.getClass());
        return updater.getId(entity);
    }
}

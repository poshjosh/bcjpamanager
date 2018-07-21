/*
 * Copyright 2018 NUROX Ltd.
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

package com.bc.jpa.functions;

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 16, 2018 6:33:03 PM
 */
public class FetchItemsByIds<E> implements BiFunction<Class<E>, List, List<E>>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(FetchItemsByIds.class.getName());

    private final PersistenceUnitContext puContext;
    
    public FetchItemsByIds(PersistenceUnitContext puContext) { 
        this.puContext = Objects.requireNonNull(puContext);
    }

    @Override
    public List<E> apply(Class<E> entityClass, List ids) {
    
        final EntityManager em = puContext.getEntityManager();
        
        final List<E> entityList = new ArrayList<>(ids.size());
        
        try{
            
            for(Object _id : ids) {
                
                final Object id = this.convertToIdType(entityClass, _id);

                final E entity = em.find(entityClass, id);

                LOG.finer(() -> "For: {0}"+id+", found: "+entity);
                
                entityList.add(entity);
            }
        }finally{
            em.close();
        }
        
        return entityList;
    }
    
    public Object convertToIdType(Class entityClass, Object id) {
        
        final Object output;
        
        if(id == null) {
            output = id;
        }else{
            final PersistenceUnitMetaData metaData = puContext.getMetaData();

            final String columnName = metaData.getIdColumnName(entityClass);

            final int columnIndex = metaData.getColumnIndex(entityClass, columnName);

            final Class columnClass = metaData.getColumnClass(entityClass, columnIndex);

            if(columnClass.isAssignableFrom(id.getClass())) {
                output = id;
            }else{
                if(columnClass.equals(String.class)) {
                    output = id.toString();
                }else if(CharSequence.class.isAssignableFrom(columnClass)) {
                    output = id.toString();
                }else {
                    output = this.construct(id);
                }
            }
        }
        
        return output;
    }
    
    public Object construct(Object id) {
        try{
            final Constructor cinit = id.getClass().getConstructor(String.class);
            if(cinit == null) {
                return id;
            }else{
                return cinit.newInstance(id.toString());
            }   
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
            return id;
        }
    }
}

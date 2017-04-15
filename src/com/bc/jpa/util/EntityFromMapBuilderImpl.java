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

package com.bc.jpa.util;

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 11, 2017 8:38:42 PM
 */
public class EntityFromMapBuilderImpl implements EntityFromMapBuilder {

    private static final Logger logger = Logger.getLogger(EntityFromMapBuilderImpl.class.getName());
    
    private final JpaContext jpaContext;
    
    private final Map<Class, Set<String>> entityColumnNames;

    private Map source;
    
    private ResultHandler resultHandler;
    
    public EntityFromMapBuilderImpl(JpaContext jpaContext, Set<String> puNames) {
        this.jpaContext = jpaContext;
        final JpaMetaData metaData = jpaContext.getMetaData();
        this.entityColumnNames = new HashMap<>(32, 0.75f);
        for(String puName : puNames) {
            final Class [] puClasses = metaData.getEntityClasses(puName);
            for(Class puClass : puClasses) {
                final Set<String> columnNames = new HashSet(Arrays.asList(metaData.getColumnNames(puClass)));
                this.entityColumnNames.put(puClass, columnNames);
            }
        }
    }
    
    @Override
    public Object build() {
        
        if(resultHandler == null) {
            resultHandler = ResultHandler.NO_OP;
        }
        
        return this.build(source);
    }
    
    public Object build(Map src) {
        
        Objects.requireNonNull(src);
        
        if(src.isEmpty()) {
            throw new IllegalArgumentException("Empty 'java.util.Map' not allowed as source");
        }
        
        final Set keys = src.keySet();
       
        final Object tgt = this.createEntityFor(src.keySet());
        
        final EntityUpdater updater = this.jpaContext.getEntityUpdater(tgt.getClass());
        
        for(Object key : keys) {
            
            Object val = src.get(key);
            
            if(val instanceof Map) {
                
                val = this.build((Map)val);
            }
            
            updater.setValue(tgt, key.toString(), val);
        }
        
        this.resultHandler.handleResult(src, tgt);
        
        return tgt;
    }
    
    public Object createEntityFor(Set names) {
        final Class entityType = this.getEntityType(names);
        return this.createEntityFor(entityType);
    }

    public Object createEntityFor(Class entityType) {
        try{
            return entityType.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Class getEntityType(Set columnNames) {
        for(Class entityType : this.entityColumnNames.keySet()) {
            if(this.entityColumnNames.get(entityType).containsAll(columnNames)) {
                if(logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Found entity type: {0} for columns: {1}", 
                            new Object[]{entityType.getName(), columnNames});
                }
                return entityType;
            }
        }
        throw new IllegalArgumentException("Could not determine the entity type for this set of column names: "+columnNames);
    }

    @Override
    public EntityFromMapBuilder source(Map source) {
        this.source = source;
        return this;
    }

    @Override
    public EntityFromMapBuilder resultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }
}

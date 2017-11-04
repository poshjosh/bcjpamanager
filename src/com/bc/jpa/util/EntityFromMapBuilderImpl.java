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
import com.bc.jpa.context.PersistenceContext;
import com.bc.jpa.metadata.PersistenceMetaData;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import com.bc.util.JsonFormat;
import com.bc.util.ReflectionUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
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
    
    private final Set<String> puNames;
    
    private final PersistenceContext jpaContext;
    
    private final Map<Class, Set<String>> entityColumnNames;
    
    private boolean lenient;

    private Map source;
    
    private Object targetEntity;
    
    private Map<Map, Object> resultBuffer;
    
    private Formatter formatter;
    
    private ResultHandler resultHandler;
    
    public EntityFromMapBuilderImpl(PersistenceContext jpaContext, Set<String> puNames) {
        this.jpaContext = jpaContext;
        this.puNames = Objects.requireNonNull(puNames);
        final PersistenceMetaData metaData = jpaContext.getMetaData();
        this.entityColumnNames = new HashMap<>(32, 0.75f);
        for(String puName : puNames) {
            final Set<Class> puClasses = metaData.getEntityClasses(puName);
            final PersistenceUnitMetaData puMeta = metaData.getMetaData(puName);
            for(Class puClass : puClasses) {
                final Set<String> columnNames = new HashSet(Arrays.asList(puMeta.getColumnNames(puClass)));
                this.entityColumnNames.put(puClass, columnNames);
            }
        }
        this.formatter = Formatter.NO_OP;
        this.resultHandler = ResultHandler.NO_OP;
    }
    
    public EntityUpdater getEntityUpdater(Class entityType) {
        for(String puName : puNames) {
            if(this.jpaContext.getMetaData().getMetaData(puName).getEntityClasses().contains(entityType)) {
                return this.jpaContext.getContext(puName).getEntityUpdater(entityType);
            }
        }
        throw new IllegalArgumentException("Unexpected entity type: " + entityType.getName() + 
                "Does not belong to any of the follow persistence units: " + puNames);
    }
    
    @Override
    public Object build() {
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Building: {0}, from data:\n{1}", 
                    new Object[]{this.targetEntity, this.source == null ? null : new JsonFormat(true, true, "  ").toJSONString(this.source)});
        }
        
        return this.build(this.source, this.targetEntity);
    }
    
    public Object build(Map src, Object tgt) {
//System.out.println("Building "+tgt+". from: "+src+". @"+this.getClass());
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Building: {0}, from data: {1}", 
                    new Object[]{tgt, src});
        }
        
        Objects.requireNonNull(src);
        Objects.requireNonNull(tgt);
        
        if(src.isEmpty()) {
            throw new IllegalArgumentException("Empty 'java.util.Map' not allowed as source");
        }
        
        final Set keys = src.keySet();
       
        final EntityUpdater updater = this.getEntityUpdater(tgt.getClass());
        
        final ReflectionUtil reflection = new ReflectionUtil();
        
        for(Object key : keys) {
            
            final String col = key.toString();
            
            Object val = src.get(key);
                                           
            final boolean update = !lenient || updater.getMethod(true, col) != null;
            
            if(logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "For update: {0}, {1}#{2} = {3}", 
                        new Object[]{update, tgt.getClass().getName(), col, val});
            }
//System.out.println("For update: "+update+". "+tgt.getClass().getName()+"#"+col+" = "+val+". @"+this.getClass());            
            if(!update) {
                continue;
            }
            
            final Method getter = updater.getMethod(false, col);
            
             Objects.requireNonNull(getter, "Getter method for " + tgt.getClass().getName() + '#' + col + " is NULL");
                
            val = this.formatter.format(tgt, col, val);
             
            final Object updatedVal;
            
            if(val instanceof Collection) {
            
//System.out.println("- - - - - - - Collection type. "+tgt.getClass().getName()+"#"+col+" = "+val+". @"+this.getClass());                            

                final Collection collection = (Collection)val;
                
                if(collection.isEmpty()) {
                 
                    updatedVal = val;
                    
                }else{    
                    
                    final Collection entityCollection = (Collection)reflection.newInstanceForCollectionType(collection.getClass());
                    final Class dataType = (Class)reflection.getGenericReturnTypeArguments(getter)[0];

                    for(Object e : collection) {

                        final Map dataMap = (Map)e;

//if("courseattendedList".equals(col)) {
//    System.out.println("x x x x x x x Name: "+col+", element type: "+dataType.getName()+", data:\n"+new JsonFormat(true, true, "  ").toJSONString(dataMap));
//}
                        final Object dataEntity = this.build(dataMap, this.newInstance(dataType));
                        
                        entityCollection.add(dataEntity);
                    }
                    
                    updatedVal = entityCollection;
                }
            }else if(val instanceof Map) {
                
//System.out.println("- - - - - - - Map type. "+tgt.getClass().getName()+"#"+col+" = "+val+". @"+this.getClass());                            

                final Class valType = getter.getReturnType();

                if(logger.isLoggable(Level.FINER)) {
                    logger.log(Level.FINER, "Value type is: {0}, for {1}#{2} = {3}", 
                            new Object[]{valType.getName(), tgt.getClass().getName(), col, val});
                }

                final Object builtVal = this.build((Map)val, this.newInstance(valType));

                if(logger.isLoggable(Level.FINER)) {
                    logger.log(Level.FINER, "Built: {0}, from {1}#{2} = {3}", 
                            new Object[]{builtVal, tgt.getClass().getName(), col, val});
                }

                updatedVal = builtVal;
                
            }else{
                
                updatedVal = val;
            }
            
            final Level LEVEL = Objects.equals(val, updatedVal) ? Level.FINER : Level.FINE;
            if(logger.isLoggable(LEVEL)) {
                logger.log(LEVEL, "Updated to: {0}, {1}#{2} = {3}", 
                        new Object[]{updatedVal, tgt.getClass().getName(), col, val});
            }
            
            updater.setValue(tgt, col, updatedVal);
        }
        
        if(this.resultBuffer != null) {
            this.resultBuffer.put(src, tgt);
        }
        
        this.resultHandler.handleResult(src, tgt);
        
        logger.log(Level.FINER, "Built entity: {0}", new MapBuilderForEntity().maxDepth(1).nullsAllowed(true).source(tgt).build());
        
        return tgt;
    }
    
    @Override
    public EntityFromMapBuilder source(boolean lenient) {
        this.lenient = lenient;
        return this;
    }
    
    @Override
    public EntityFromMapBuilder source(Map source) {
        this.source = source;
        return this;
    }

    @Override
    public EntityFromMapBuilder target(Object target) {
        if(target instanceof Class) {
            this.targetEntity = this.newInstance((Class)target);
        }else{
            this.targetEntity = target;
        }
        return this;
    }

    @Override
    public EntityFromMapBuilder resultBuffer(Map<Map, Object> buffer) {
        this.resultBuffer = buffer;
        return this;
    }

    @Override
    public EntityFromMapBuilder formatter(Formatter formatter) {
        this.formatter = formatter;
        return this;
    }
    
    @Override
    public EntityFromMapBuilder resultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    public <T> T newInstance(Class<T> entityType) {
        try{
            return entityType.getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

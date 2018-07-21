/*
 * Copyright 2016 NUROX Ltd.
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

package com.bc.jpa.controller;

import com.bc.jpa.EntityReference;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.util.JpaUtil;
import com.bc.jpa.metadata.JpaMetaData;
import com.bc.jpa.exceptions.EntityInstantiationException;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import com.bc.jpa.EntityMemberAccess;
import com.bc.reflection.ReflectionUtil;

/**
 * @author Chinomso Bassey Ikwuagwu on Dec 12, 2016 12:27:05 PM
 * @param <E> The type of the entity
 * @param <e> The type of the entity ID
 * @deprecated 
 */
@Deprecated
public class EntityUpdaterDeprecated<E, e> implements EntityMemberAccess<E, e> {
    private transient static final Logger LOG = Logger.getLogger(EntityUpdaterDeprecated.class.getName());
    
    private final Class<E> entityClass;
    
    private final Method [] methods;
    
    private final JpaContext jpaContext;
    
    private final ReflectionUtil reflection;

    public EntityUpdaterDeprecated(JpaContext jpaContext, Class<E> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass);
        this.methods = this.entityClass.getMethods(); 
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.reflection = new ReflectionUtil();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.jpaContext.getEntityManagerFactory(entityClass);
    }

    @Override
    public E create(Map values, boolean convertCrossReferences) throws EntityInstantiationException {
        try{
            
            final E entity = this.entityClass.getConstructor().newInstance();
            
            this.update(entity, values, convertCrossReferences);
            
            return entity;
            
        }catch(NoSuchMethodException | SecurityException | 
                InstantiationException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException e) {
            throw new EntityInstantiationException(e);    
        }
    }
    
    @Override
    public int update(E src, E target, boolean all) {
        
        int updateCount = 0;
        
        final String [] targetNames = this.getJpaContext().getMetaData().getColumnNames(target.getClass());
        
        for(String targetName : targetNames) {
            
            final Object srcValue = this.getValue(src, targetName);
            
            if(all || !Objects.equals(srcValue, this.getValue(target, targetName))) {
                
                this.setValue(target, targetName, srcValue);
                
                ++updateCount;
            }
        }
        
        return updateCount;
    }
    
    @Override
    public int update(E entity, Map row, boolean convertCrossReferences) 
            throws EntityInstantiationException {
        
        int updateCount = 0;
        
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Updating entity: {0} with values {1}",new Object[]{ entity,  row});
}
        
        try{
            
            Map<JoinColumn, Field> joinColumns;
            if(convertCrossReferences && !row.isEmpty()) {
                joinColumns = jpaContext.getMetaData().getJoinColumns(entityClass);
            }else{
                joinColumns = null;
            }
            
            final EntityReference entityReference = jpaContext.getEntityReference();
            
            for(Map.Entry entry:(Set<Map.Entry>)row.entrySet()) {
                String col = entry.getKey().toString();
                Object val = entry.getValue();
                if(convertCrossReferences && (joinColumns != null && !joinColumns.isEmpty())) {
                    Object ref = entityReference.getReferenceOptional(entityClass, joinColumns, col, val).orElse(null);
                    if(ref != null) {
                        val = ref;
                    }
                }
                try{
                    this.setValue(entity, col, val);
                    ++updateCount;
                }catch(IllegalArgumentException ignored) { }
            }
        }catch(Exception e) {
            throw new EntityInstantiationException(e);
        }
        return updateCount;
    }

    /**
     * @param entity Entity whose Id is to be returned
     * @return The id of the specified entity
     * @throws IllegalArgumentException If no method matching the  
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public e getId(E entity) 
            throws IllegalArgumentException, UnsupportedOperationException {
        return (e)this.getValue(entity, this.jpaContext.getMetaData().getIdColumnName(this.entityClass));
    }

    /**
     * @param entity Entity whose value is to be returned
     * @param columnName The columnName matching the field whose value is 
     * to be returned
     * @return The value of the field whose name matches the specified columnName
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
ó     * throws an exception
     */
    @Override
    public Object getValue(E entity, String columnName) 
            throws IllegalArgumentException, UnsupportedOperationException {

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Entity class. From controller: {0}, From entity: {1}", 
new Object[]{ this.entityClass,  entity.getClass()});
}

        if(columnName == null) {
            throw new NullPointerException();
        }
        if(entity == null) {
            throw new NullPointerException();
        }
        
        Method method = null;
        try{
            
            method = this.reflection.getMethodAlphaNumeric(false, this.methods, columnName);
            
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Entity: {0}, Column name: {1}, Getter method: {2}",
new Object[]{ entity,  columnName,  (method==null?null:method.getName())});
}

            if(method != null) {
                return method.invoke(entity);
            }
            
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            
            StringBuilder builder = new StringBuilder("Error getting entity value.");
            builder.append(" Entity: ").append(entity);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(columnName);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
        
        if(method == null) {
            throw new IllegalArgumentException(
                    "Could not find matching getter method for field: "+
                    columnName+" in class: "+entity.getClass());
        }
        
        return null;
    }

    /**
     * @param entity Entity whose Id is to be updated with a new value
     * @param id The new id
     * @throws IllegalArgumentException If no method matching the specified
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public void setId(E entity, e id) 
            throws IllegalArgumentException, UnsupportedOperationException {
        
        this.setValue(entity, this.jpaContext.getMetaData().getIdColumnName(this.entityClass), id);
    }

    /**
     * @param entity
     * @param columnName
     * @param columnValue
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    @Override
    public void setValue(E entity, String columnName, Object columnValue) 
            throws IllegalArgumentException, UnsupportedOperationException {        
        
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Entity class. From controller: {0}, From entity: {1}", 
new Object[]{ entityClass,  entity.getClass()});
}
        
        if(columnName == null) {
            throw new NullPointerException();
        }
        if(entity == null) {
            throw new NullPointerException();
        }
        Method method = null; 
        try{
            
            method = this.reflection.getMethodAlphaNumeric(true, this.methods, columnName);
            
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Entity: {0}, Column name: {1}, Column value: {2}, Setter method: {3}", 
new Object[]{ entity,  columnName,  columnValue,  (method==null?null:method.getName())});
}

            if(method != null) {
                
                // We do this because MySQL returns Byte for tinyint where as 
                // jpa designates the columns as shorts
                //
                // Only one parameter expected
                if(columnValue != null) {
                    columnValue = this.convertToNumberOrBooleanType(
                            columnValue, method.getParameterTypes()[0], columnValue);
                }

                method.invoke(entity, columnValue);
//                if(entity.getClass().getName().endsWith("Productvariant")) {
//                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXx Set value to: " + columnValue);
//                }
            }
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            
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
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+entityClass);
        }
    }

    public Object convertToNumberOrBooleanType(Object value, Class toType, Object alternateOutput) {
        Objects.requireNonNull(value);
        Object output;
        if((toType == short.class || toType == Short.class) && !(value instanceof Short)) {
            output = Short.valueOf(value.toString());
        }else if((toType == int.class || toType == Integer.class) && !(value instanceof Integer)) {
            output = Integer.valueOf(value.toString());
        }else if((toType == long.class || toType == Long.class) && !(value instanceof Long)) {
            output = Long.valueOf(value.toString());
        }else if((toType == float.class || toType == Float.class) && !(value instanceof Float)) {
            output = Float.valueOf(value.toString());
        }else if((toType == double.class || toType == Double.class) && !(value instanceof Double)) {
            output = Double.valueOf(value.toString());
        }else if((toType == boolean.class || toType == Boolean.class) && !(value instanceof Boolean)) {
            output = Boolean.valueOf(value.toString());
        }else if((toType == BigDecimal.class) && !(value instanceof BigDecimal)) {
            output = new BigDecimal(value.toString());
        }else{
            output = alternateOutput;
        }
        return output;
    }

    @Override
    public Method getMethod(boolean setter, Class referenceEntityClass, Class referencingEntityClass) {
        return JpaUtil.getMethod(setter, referenceEntityClass, referencingEntityClass);
    }

    @Override
    public Method getMethod(boolean setter, String columnName) {
        return this.reflection.getMethodAlphaNumeric(setter, methods, columnName);
    }

    @Override
    public Method[] getMethods() {
        return methods;
    }

    @Override
    public final Class<E> getEntityClass() {
        return entityClass;
    }

    public final JpaContext getJpaContext() {
        return jpaContext;
    }

    public final JpaMetaData getMetaData() {
        return this.jpaContext.getMetaData();
    }
}

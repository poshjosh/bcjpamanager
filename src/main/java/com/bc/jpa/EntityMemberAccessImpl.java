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
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.jpa.functions.ConvertToNumber;
import com.bc.jpa.util.JpaUtil;
import com.bc.reflection.ReflectionUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.JoinColumn;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 12:46:14 PM
 */
public class EntityMemberAccessImpl<E, e> implements EntityMemberAccess<E, e> {

    private transient static final Logger LOG = Logger.getLogger(EntityMemberAccessImpl.class.getName());
    
    private final Class<E> entityClass;
    
    private final Method [] methods;
    
    private final PersistenceUnitContext persistenceUnitContext;
    
    private final EntityReference entityReference;
    
    private final BiFunction<Object, Class, Object> toNumber;
    
    private final ReflectionUtil reflection;

    public EntityMemberAccessImpl(PersistenceUnitContext puContext, Class<E> entityClass) {
        this.entityClass = Objects.requireNonNull(entityClass);
        this.methods = this.entityClass.getMethods(); 
        this.persistenceUnitContext = Objects.requireNonNull(puContext);
        this.entityReference = puContext.getEntityReference();
        this.toNumber = new ConvertToNumber();
        this.reflection = new ReflectionUtil();
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
        
//        System.out.println("\nUpdating: " + target + ", with: " + src + " @"+this.getClass());
        
        int updateCount = 0;
        
        final String [] targetNames = this.persistenceUnitContext.getMetaData().getColumnNames(target.getClass());
        
        for(String targetName : targetNames) {
            
            final Object srcValue = this.getValue(src, targetName);
            
            final Object tgtValue = this.getValue(target, targetName);

//            System.out.println(targetName + '=' + srcValue + ", old value: " + tgtValue);
            
            if(all || !Objects.equals(srcValue, tgtValue)) {
                
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
        
        LOG.finer(() -> "Updating entity: "+entity+" with values: " + row);
        
        try{
            
            Map<JoinColumn, Field> joinColumns;
            if(convertCrossReferences && !row.isEmpty()) {
                joinColumns = this.entityReference.getJoinColumns(entityClass);
            }else{
                joinColumns = null;
            }
            
            for(Map.Entry entry:(Set<Map.Entry>)row.entrySet()) {
                String col = entry.getKey().toString();
                Object val = entry.getValue();
                if(convertCrossReferences && (joinColumns != null && !joinColumns.isEmpty())) {
                    Object ref = this.entityReference.getReferenceOptional(entityClass, joinColumns, col, val).orElse(null);
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
        return (e)this.getValue(entity, this.persistenceUnitContext.getMetaData().getIdColumnName(this.entityClass));
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

        LOG.finer(() -> "Entity class. From controller: "+entityClass+", From entity: "+entity.getClass());

        if(columnName == null) {
            throw new NullPointerException();
        }
        if(entity == null) {
            throw new NullPointerException();
        }
        
        Method method = null;
        try{
            
            method = this.reflection.getMethodAlphaNumeric(false, this.methods, columnName);
            
            if(LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Entity: {0}, Column name: {1}, Getter method: {2}",
                new Object[]{entity, columnName, (method==null?null:method.getName())});
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
        
        this.setValue(entity, this.persistenceUnitContext.getMetaData().getIdColumnName(this.entityClass), id);
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
        
        LOG.finer(() -> "Entity class. From controller: "+entityClass+", From entity: "+entity.getClass());
        
        if(columnName == null) {
            throw new NullPointerException();
        }
        if(entity == null) {
            throw new NullPointerException();
        }
        Method method = null; 
        try{
            
            method = this.reflection.getMethodAlphaNumeric(true, this.methods, columnName);
            
            if(LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Entity: {0}, Column name: {1}, Column value: {2}, Getter method: {3}",
                new Object[]{entity, columnName, columnValue, (method==null?null:method.getName())});
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
        if((toType == boolean.class || toType == Boolean.class) && !(value instanceof Boolean)) {
            output = Boolean.valueOf(value.toString());
        }else{
            output = toNumber.apply(value, toType);
        }
        return output == null ? alternateOutput : output;
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

    public PersistenceUnitContext getPersistenceUnitContext() {
        return persistenceUnitContext;
    }
}


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

import com.bc.jpa.exceptions.EntityInstantiationException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 29, 2017 8:28:30 PM
 */
public interface EntityMemberAccess<E, e> {

    E create(Map values, boolean convertCrossReferences) throws EntityInstantiationException;

    Class<E> getEntityClass();

    /**
     * @param entity Entity whose Id is to be returned
     * @return The id of the specified entity
     * @throws IllegalArgumentException If no method matching the
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    e getId(E entity) throws IllegalArgumentException, UnsupportedOperationException;

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
    Object getValue(E entity, String columnName) throws IllegalArgumentException, UnsupportedOperationException;

    /**
     * @param entity Entity whose Id is to be updated with a new value
     * @param id The new id
     * @throws IllegalArgumentException If no method matching the specified
     * {@link #getIdColumnName() idColumnName} was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    void setId(E entity, e id) throws IllegalArgumentException, UnsupportedOperationException;

    /**
     * @param entity
     * @param columnName
     * @param columnValue
     * @throws IllegalArgumentException If no method matching the specified
     * columnName was found
     * @throws UnsupportedOperationException if {@link java.lang.reflect.Method#invoke(java.lang.Object, java.lang.Object[])}
     * throws an exception
     */
    void setValue(E entity, String columnName, Object columnValue) throws IllegalArgumentException, UnsupportedOperationException;

    int update(E src, E target, boolean all);
    
    int update(E entity, Map values, boolean convertCrossReferences) throws EntityInstantiationException;

    Method [] getMethods();
    
    /**
     * @param setter The type of method to return. Either setter or getter
     * @param referenceEntityClass
     * @param referencingEntityClass
     * @return For a referer of Car and a reference of Engine i.e Car#getEngineList()
     * returns either method getEngineList or setEngineList(List)
     */
    Method getMethod(boolean setter, 
    Class referenceEntityClass, Class referencingEntityClass);
    
    /**
     * Methods of the {@link java.lang.Object} class are not considered
     * @param setter boolean, if true only setter methods are considered
     * @param columnName The column name for which a method with a matching
     * name is to be returned.
     * @return A method whose name matches the input columnName or null if none was found
     */
    Method getMethod(boolean setter, String columnName);
}

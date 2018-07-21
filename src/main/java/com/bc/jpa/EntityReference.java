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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import javax.persistence.JoinColumn;
import javax.persistence.TemporalType;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 1:10:19 PM
 */
public interface EntityReference {
    
    <E> E find(Class<E> entityType, String name);

    /**
     * @param referencingClass
     * @param col Column name
     * @param val Column value
     * @return The entity referenced by the supplied column name and value
     * @see #getReference(javax.persistence.EntityManager, java.lang.Class, java.util.Map, java.lang.String, java.lang.Object) 
     */
    default Optional getReferenceOptional(Class referencingClass, String col, Object val) {
        
        Map<JoinColumn, Field> joinColumns = this.getJoinColumns(referencingClass);
        
        Optional optional;
        if(joinColumns == null || joinColumns.isEmpty()) {
            optional = Optional.empty();
        }else{
            optional = this.getReferenceOptional(referencingClass, joinColumns, col, val);
        }
        
        return optional;
    }

    /**
     * Lets say we have a reference table named <tt>role</tt> as shown below: 
     * <pre>
     * ----------------
     *  roleid | role
     * ----------------
     *  1      | admin
     * ----------------
     *  2      | user
     * ----------------
     *  3      | guest
     * ----------------
     * </pre>
     * <p>And given the referencing table named <tt>userroles</tt> with definition below:</p>
     * <pre>
     * create table userroles(
     *   userroleid INTEGER(8) AUTO_INCREMENT not null primary key,
     *   userid INTEGER(8) not null UNIQUE,
     *   role SHORT(2) not null,
     *   FOREIGN KEY (role) REFERENCES role(roleid)
     * )ENGINE=INNODB;
     * </pre>
     * <p>
     * Calling this method with arguments <tt>role</tt> and <tt>2</tt> respectively 
     * will return the <tt>Role</tt> entity with the specified id.
     * </p>
     * The method returns null if the arguments have no matching reference.
     * @param referencingClass
     * @param joinCols
     * @param col Column name
     * @param val Column value
     * @return The entity referenced by the supplied column name and value
     */
    Optional getReferenceOptional(Class referencingClass, Map<JoinColumn, Field> joinCols, String col, Object val);

    TemporalType getTemporalType(Class entityClass, String column);
    
    Map<JoinColumn, Field> getJoinColumns(Class referencingClass);
    
    Map<Class, String> getReferencing(Class referenceClass);
    
    Class [] getReferencingClasses(Class reference);

    String [] getReferencingColumns(Class referenceClass);
    
    Class [] getReferenceClasses(Class referencing);
    
    Class getReferenceClass(Class referencing, String referencingColumn);
    
    /**
     * @param referencingClass The referencing class for which all references
     * are to be returned.
     * @return A Map whose key/value pairs are mapped in the format:
     * ReferencingColumnName=ReferenceColumnName
     */
    Map<String, String> getReferences(Class referencingClass);
    
    Map<Class, String> getReferenceTypes(Class referencingClass);
    
    String getReferenceColumn(Class reference, Class referencing);

    String [] getReferencingColumns(Class referencing, String referenceColumn);
}

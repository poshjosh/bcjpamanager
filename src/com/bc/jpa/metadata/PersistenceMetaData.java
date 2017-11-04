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

package com.bc.jpa.metadata;

import com.bc.node.Node;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 7:15:18 PM
 */
public interface PersistenceMetaData {


    /**
     * @return The URI to the persistence.xml data
     */
    URI getURI();
    
    /**
     * @return A node structure containing names in the following heirarchy: 
     * <b>persistence -> persistence-unit -> catalog -> schema -> table -> column</b> 
     */
    Node<String> getNode();
    
    /**
     * 
     * @param persistenceUnitName
     * @return Properties containing the jdbc properties for specified persistence unit
     * @throws java.io.IOException
     */
    Properties getProperties(String persistenceUnitName) throws IOException;
    
    /**
     * If persistence unit name is null then all persistence units will be 
     * searched for the specified entity type
     * @param persistenceUnitName The persistence unit name to search may be null
     * @param entityType The entity type to find
     * @return <code>true</code> if found, otherwise <code>false</code>
     */
    boolean isListedEntityType(String persistenceUnitName, Class entityType);
    
    Set<String> getPersistenceUnitNames();
    
    default Set<Class> getEntityClasses(String persistenceUnitName) {
        return this.getEntityClasses(Collections.singleton(persistenceUnitName));
    }// EDITED
    
    Set<Class> getEntityClasses(Set<String> persistenceUnitNames);
    
    default Map<String, Set<Class>> getPersistenceUnitClasses() {
        return this.getPersistenceUnitClasses(this.getPersistenceUnitNames().toArray(new String[0]));
    }
    
    Map<String, Set<Class>> getPersistenceUnitClasses(String... persistenceUnitNames); // ADDED
    
//    String getPersistenceUnitNames(Class entityClass);

    boolean isAnyListedTableExisting(String persistenceUnit); // throws SQLException;
    
    boolean isAnyTableExisting(String persistenceUnit); //throws SQLException;

    /**
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @param emfProvider
     * @return 
     * @throws SQLException
     */
    Node<String> build(Function<String, EntityManagerFactory> emfProvider) throws SQLException;

    PersistenceUnitMetaData getMetaData(String persistenceUnit);

    boolean isBuilt();
}

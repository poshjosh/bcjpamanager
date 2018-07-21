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
import java.util.List;
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
     * @param persistenceUnitName The persistence unit name to search.
     * @param entityType The entity type to find
     * @return <code>true</code> if the entity type is listed in the persistence configuration file, otherwise <code>false</code>
     */
    boolean isListedEntityType(String persistenceUnitName, Class entityType);
    
    /**
     * @param persistenceUnitName The persistence unit name to search.
     * @param entityTypes The entity type to find
     * @return <code>true</code> if the entity types are listed in the persistence configuration file, otherwise <code>false</code>
     */
    boolean isListedEntityTypes(String persistenceUnitName, List<Class> entityTypes);
    
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
     * Build the node structure returned by {@link #getNode()}.
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @param emfProvider
     * @return The node structure which will subsequently be returned by the 
     * method {@link #getNode()}
     * @throws SQLException 
     */
    Node<String> build(Function<String, EntityManagerFactory> emfProvider) throws SQLException;

    PersistenceUnitMetaData getMetaData(String persistenceUnit);

    boolean isBuilt();
}

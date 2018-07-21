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
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 7:30:20 PM
 */
public interface PersistenceUnitMetaData {
    
    Node<String> getNode();
    
    Set<Class> getEntityClasses();

    Class getEntityClass(
            String catalog, String schema, String tableName);
    
    String getCatalogName(Class entityClass);
    
    default String getDatabaseName(Class entityClass) {
        return this.getCatalogName(entityClass);
    }
    
    String getSchemaName(Class entityClass);
    
    Class getEntityClass(String tableName);

    String getTableName(Class entityClass);
    
    String getIdColumnName(Class entityClass);
    
    String [] getColumnNames(Class entityClass);

    int getColumnIndex(Class entityClass, String column);

    /**
     * Build the node structure returned by {@link #getNode()}.
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @param persistenceNode
     * @param emfProvider
     * @return The node structure which will subsequently be returned by the 
     * method {@link #getNode()}
     * @throws SQLException 
     */
    Node<String> build(Node<String> persistenceNode, 
            Function<String, EntityManagerFactory> emfProvider) throws SQLException;
    
    Class getColumnClass(Class entityClass, int columnIndex);
    
    int[] getColumnDataTypes(Class entityClass);

    int[] getColumnDisplaySizes(Class entityClass);

    int[] getColumnNullables(Class entityClass);

    boolean isBuilt();
    
    List<String> fetchExistingListedTables(
            Function<String, EntityManagerFactory> emfProvider) 
            throws SQLException;

    boolean isAnyListedTableExisting();

    boolean isAnyTableExisting();

    /**
     * @param entityType The entity type to find
     * @return <code>true</code> if the entity type is listed in the persistence configuration file, otherwise <code>false</code>
     */
    boolean isListedEntityType(Class entityType);
    
    /**
     * @param entityTypes The entity types to find
     * @return <code>true</code> if the entity types are listed in the persistence configuration file, otherwise <code>false</code>
     */
    boolean isListedEntityTypes(List<Class> entityTypes);
}

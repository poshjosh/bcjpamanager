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

import static com.bc.jpa.metadata.PersistenceNodeBuilder.NODE_NAME_CATALOG;
import static com.bc.jpa.metadata.PersistenceNodeBuilder.NODE_NAME_COLUMN;
import static com.bc.jpa.metadata.PersistenceNodeBuilder.NODE_NAME_PERSISTENCE_UNIT;
import static com.bc.jpa.metadata.PersistenceNodeBuilder.NODE_NAME_SCHEMA;
import static com.bc.jpa.metadata.PersistenceNodeBuilder.NODE_NAME_TABLE;
import com.bc.node.Node;
import com.bc.node.NodeFormat;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 1, 2017 12:12:42 PM
 */
public class PersistenceUnitNodeBuilderImpl 
        implements PersistenceUnitNodeBuilder, Serializable {

    private static final Logger logger = Logger.getLogger(PersistenceUnitNodeBuilderImpl.class.getName());
    
    private final MetaDataAccess metaDataSource;
    
    private final Node<String> parent;
    
    private boolean buildAttempted;

    public PersistenceUnitNodeBuilderImpl(
            Node<String> parent, Function<String, EntityManagerFactory> emfProvider){
        this(parent, new MetaDataAccessImpl(emfProvider));
    }

    public PersistenceUnitNodeBuilderImpl(Node<String> parent, MetaDataAccess metaDataSource) {
        this.metaDataSource = Objects.requireNonNull(metaDataSource);
        this.parent = Objects.requireNonNull(parent);
    }
    
    @Override
    public Node<String> build(String persistenceUnitName) throws SQLException {
        
        this.ensureBuildNotAttempted();
        
        final Node<String> puNode = Node.of(NODE_NAME_PERSISTENCE_UNIT, persistenceUnitName, parent);

        final Set<String> catalogNames = this.getCatalogNames(persistenceUnitName);

        logger.fine(() -> "Persisence unit: " +persistenceUnitName + ", catalogs: " + catalogNames);

        for(String catalogName : catalogNames) {

            final Node<String> catalogNode = Node.of(NODE_NAME_CATALOG, catalogName, puNode);

            final String catalogSearch = catalogNode.getValueOrDefault(null);

            final Set<String> schemaNames = this.getSchemaNames(persistenceUnitName, catalogSearch);

            logger.fine(() -> "Catalog: " +catalogName + ", schemas: " + schemaNames);

            for(String schemaName : schemaNames) {

                final Node<String> schemaNode = Node.of(NODE_NAME_SCHEMA, schemaName, catalogNode);

                final String schemaSearch = schemaNode.getValueOrDefault(null);

                final Set<String> tableNames = new LinkedHashSet(
                        this.metaDataSource.fetchStringMetaData(persistenceUnitName, 
                        catalogSearch, schemaSearch, null, null, MetaDataAccess.TABLE_NAME)
                );

                logger.fine(() -> "Schema: " +schemaName + ", tables: " + tableNames);

                for(String tableName : tableNames) {

                    final Node<String> tableNode = Node.of(NODE_NAME_TABLE, tableName, schemaNode);

                    final Set<String> columnNames = new LinkedHashSet(
                            this.metaDataSource.fetchStringMetaData(
                            persistenceUnitName, catalogSearch, schemaSearch, tableName, null, MetaDataAccess.COLUMN_NAME)
                    );

                    logger.finer(() -> "Table: " +tableName + ", columns: " + columnNames);

                    for(String columnName : columnNames) {

                        Node.of(NODE_NAME_COLUMN, columnName, tableNode);
                    }
                }
            }
        }
        
        logger.finer(() -> new NodeFormat().format(puNode));
        
        return puNode;
    }
    
    public Set<String> getCatalogNames(String puName) throws SQLException {
        final Set<String> set = this.metaDataSource.fetchCatalogNames(puName);
        return this.returnSingletonWithNullElementIfEmpty(set);
    }
    
    public Set<String> getSchemaNames(String puName, String catalogName) throws SQLException {
        final List<String> schemaNames = this.metaDataSource.fetchStringMetaData(
                puName, catalogName, null, null, null, MetaDataAccess.TABLE_SCHEMA);
        return this.returnSingletonWithNullElementIfEmpty(new LinkedHashSet(schemaNames));
    }
    
    private Set<String> returnSingletonWithNullElementIfEmpty(Set<String> set) {
        return set.isEmpty() ? Collections.singleton(null) : set;
    }

    private void ensureBuildNotAttempted() {
        if(buildAttempted) {
            throw new IllegalStateException("build method may only be called once");
        }
        this.buildAttempted = true;
    }
}


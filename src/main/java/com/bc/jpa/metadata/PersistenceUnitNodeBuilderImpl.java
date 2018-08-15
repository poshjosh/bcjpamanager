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

    private static final Logger LOG = Logger.getLogger(PersistenceUnitNodeBuilderImpl.class.getName());
    
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
    public PersistenceUnitNode build(String persistenceUnitName) throws SQLException {
        
        this.ensureBuildNotAttempted();
        
        final boolean caseInsensitiveNames = true;
        
        final PersistenceUnitNode puNode = new PersistenceUnitNodeImpl(
                PersistenceNode.persistence_unit.getTagName(), 
                persistenceUnitName, 
                parent, 
                caseInsensitiveNames);

        final Set<String> catalogNames = this.getCatalogNames(persistenceUnitName);

        LOG.fine(() -> "Persisence unit: " +persistenceUnitName + ", catalogs: " + catalogNames);

        for(String catalogName : catalogNames) {

            final Node<String> catalogNode = Node.of(PersistenceNode.catalog.getTagName(), catalogName, puNode);

            final String catalogSearch = catalogNode.getValueOrDefault(null);

            final Set<String> schemaNames = this.getSchemaNames(persistenceUnitName, catalogSearch);

            LOG.fine(() -> "Catalog: " +catalogName + ", schemas: " + schemaNames);

            for(String schemaName : schemaNames) {

                final Node<String> schemaNode = Node.of(PersistenceNode.schema.getTagName(), schemaName, catalogNode);

                final String schemaSearch = schemaNode.getValueOrDefault(null);

                final Set<String> tableNames = new LinkedHashSet(
                        this.metaDataSource.fetchStringMetaData(persistenceUnitName, 
                        catalogSearch, schemaSearch, null, null, MetaDataAccess.TABLE_NAME)
                );

                LOG.fine(() -> "Schema: " +schemaName + ", tables: " + tableNames);

                for(String tableName : tableNames) {

                    final Node<String> tableNode = Node.of(PersistenceNode.table.getTagName(), tableName, schemaNode);

                    final Set<String> columnNames = new LinkedHashSet(
                            this.metaDataSource.fetchStringMetaData(
                            persistenceUnitName, catalogSearch, schemaSearch, tableName, null, MetaDataAccess.COLUMN_NAME)
                    );

                    LOG.finer(() -> "Table: " +tableName + ", columns: " + columnNames);

                    for(String columnName : columnNames) {

                        Node.of(PersistenceNode.column.getTagName(), columnName, tableNode);
                    }
                }
            }
        }
        
        LOG.finer(() -> new NodeFormat().format(puNode));
        
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


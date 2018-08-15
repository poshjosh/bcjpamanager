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

package com.bc.jpa.metadata.wip;

import com.bc.jpa.metadata.MetaDataAccess;
import com.bc.jpa.metadata.MetaDataAccessImpl;
import com.bc.jpa.metadata.PersistenceNode;
import com.bc.jpa.metadata.PersistenceUnitNodeBuilderImpl;
import com.bc.node.Node;
import com.bc.node.NodeFormat;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 9:52:11 PM
 */
public class PersistenceNodeBuilderImpl implements PersistenceNodeBuilder {

    private static final Logger LOG = Logger.getLogger(PersistenceNodeBuilderImpl.class.getName());
    
    private final MetaDataAccess metaDataSource;
    
    private final Collection<String> persistenceUnitNames;
    
    private boolean buildAttempted;

    public PersistenceNodeBuilderImpl(
            Function<String, EntityManagerFactory> emfProvider,
            Collection<String> persistenceUnitNames){
        this(new MetaDataAccessImpl(emfProvider), persistenceUnitNames);
    }

    public PersistenceNodeBuilderImpl(MetaDataAccess metaDataSource, Collection<String> persistenceUnitNames) {
        this.metaDataSource = Objects.requireNonNull(metaDataSource);
        this.persistenceUnitNames = Objects.requireNonNull(persistenceUnitNames);
    }
    
    @Override
    public Node<String> build(String rootNodeValue) throws SQLException {
        
        this.ensureBuildNotAttempted();
        
        final Node<String> persistenceNode = Node.of(PersistenceNode.persistence.getTagName(), rootNodeValue, null);
        
        for(String puName : this.persistenceUnitNames) {
            
            new PersistenceUnitNodeBuilderImpl(persistenceNode, this.metaDataSource).build(puName);
        }
        
        LOG.finer(() -> new NodeFormat().format(persistenceNode));
        
        return persistenceNode;
    }
    
    @Override
    public Node<String> buildUnit(String puName, Node<String> parent) throws SQLException {
        
        final Node<String> puNode = Node.of(PersistenceNode.persistence_unit.getTagName(), puName, parent);

        final Set<String> catalogNames = this.getCatalogNames(puName);

        LOG.fine(() -> "Persisence unit: " +puName + ", catalogs: " + catalogNames);

        for(String catalogName : catalogNames) {

            final Node<String> catalogNode = Node.of(PersistenceNode.catalog.getTagName(), catalogName, puNode);

            final String catalogSearch = catalogNode.getValueOrDefault(null);

            final Set<String> schemaNames = this.getSchemaNames(puName, catalogSearch);

            LOG.fine(() -> "Catalog: " +catalogName + ", schemas: " + schemaNames);

            for(String schemaName : schemaNames) {

                final Node<String> schemaNode = Node.of(PersistenceNode.schema.getTagName(), schemaName, catalogNode);

                final String schemaSearch = schemaNode.getValueOrDefault(null);

                final Set<String> tableNames = new LinkedHashSet(
                        this.metaDataSource.fetchStringMetaData(puName, 
                        catalogSearch, schemaSearch, null, null, MetaDataAccess.TABLE_NAME)
                );

                LOG.fine(() -> "Schema: " +schemaName + ", tables: " + tableNames);

                for(String tableName : tableNames) {

                    final Node<String> tableNode = Node.of(PersistenceNode.table.getTagName(), tableName, schemaNode);

                    final Set<String> columnNames = new LinkedHashSet(
                            this.metaDataSource.fetchStringMetaData(
                            puName, catalogSearch, schemaSearch, tableName, null, MetaDataAccess.COLUMN_NAME)
                    );

                    LOG.finer(() -> "Table: " +tableName + ", columns: " + columnNames);

                    for(String columnName : columnNames) {

                        Node.of(PersistenceNode.column.getTagName(), columnName, tableNode);
                    }
                }
            }
        }
        
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

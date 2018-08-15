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

import com.bc.jpa.functions.GetTableNameFromAnnotation;
import com.bc.node.Node;
import com.bc.node.NodeFormat;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 10:42:32 PM
 */
public class PersistenceUnitMetaDataImpl implements PersistenceUnitMetaData, Serializable {

    private transient static final Logger LOG = 
            Logger.getLogger(PersistenceUnitMetaDataImpl.class.getName());

    private final String persistenceUnit;
    
    private final List<Class> persistenceUnitClasses;
    
    private boolean built;
    
    private PersistenceUnitNode puNode;
    
    private List<int[]> columnDisplaySizes;
    
    private List<int[]> columnNullables;
    
    private List<int[]> columnDataTypes;

    public PersistenceUnitMetaDataImpl(String persistenceUnit, Collection<Class> puClasses) {
        this.persistenceUnit = Objects.requireNonNull(persistenceUnit);
        this.persistenceUnitClasses = Collections.unmodifiableList(new ArrayList(puClasses));
    }
    
    @Override
    public boolean isBuilt() {
        return this.built;
    }
    
    /**
     * Build the node structure returned by {@link #getNode()}.
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @param persistenceNode
     * @param emfProvider
     * @throws SQLException 
     */
    @Override
    public PersistenceUnitNode build(Node<String> persistenceNode, Function<String, EntityManagerFactory> emfProvider) throws SQLException{

        final MetaDataAccess metaDataAccess = new MetaDataAccessImpl(emfProvider);
        
        this.puNode = new PersistenceUnitNodeBuilderImpl(
                persistenceNode, metaDataAccess).build(persistenceUnit);

        LOG.finer(() -> new NodeFormat().format(this.puNode));
        
        this.columnDisplaySizes = new ArrayList(this.persistenceUnitClasses.size());
        this.columnNullables = new ArrayList(this.persistenceUnitClasses.size());
        this.columnDataTypes = new ArrayList(this.persistenceUnitClasses.size());
        
        LOG.finer(() -> "Persistence unit name: " + persistenceUnit + ", node: " + this.puNode);
        
        for(Class puClass : this.persistenceUnitClasses) {
            
            final String tableName = this.getTableName(puClass);
            
            LOG.fine(() -> "Type: " + puClass.getName() + ", Table: " + tableName);
            
            this.columnDisplaySizes.add(metaDataAccess.fetchColumnDisplaySizes(persistenceUnit, tableName));
            this.columnNullables.add(metaDataAccess.fetchColumnNullables(persistenceUnit, tableName));
            this.columnDataTypes.add(metaDataAccess.fetchColumnDataTypes(persistenceUnit, tableName));
        }
        
        this.built = true;
        
        return this.puNode;
    }

    @Override
    public Collection<Class> getEntityClasses() {
        return this.persistenceUnitClasses;
    }

    @Override
    public int [] getColumnDisplaySizes(Class entityClass) {
        return this.get(columnDisplaySizes, entityClass);
    }

    @Override
    public int [] getColumnNullables(Class entityClass) {
        return this.get(columnNullables, entityClass);
    }

    @Override
    public Class getColumnClass(Class entityClass, int columnIndex) {
        final int [] dataTypes = this.get(columnDataTypes, entityClass);
        final Class columnClass = com.bc.sql.SQLUtils.getClass(dataTypes[columnIndex], null);
        return Objects.requireNonNull(columnClass);
    }

    @Override
    public int [] getColumnDataTypes(Class entityClass) {
        return this.get(columnDataTypes, entityClass);
    }
    
    public int [] get(List<int[]> source, Class entityClass) {
        return source.get(this.persistenceUnitClasses.indexOf(entityClass));
    }

    @Override
    public Class getEntityClass(String tableName) {
        for(Class cls : this.persistenceUnitClasses) {
            if(this.getTableName(cls).equals(tableName)) {
                return cls;
            }
        }
        throw new IllegalArgumentException("Unexpected table name: " + tableName);
    }

    @Override
    public String getCatalogName(Class entityClass) {
        return this.puNode.getCatalogName(entityClass);
    }
    
    @Override
    public String getSchemaName(Class entityClass) {
        return this.puNode.getSchemaName(entityClass);
    }

    @Override
    public String getTableName(Class entityClass) {
        return this.puNode.getTableName(entityClass);
    }
    
    @Override
    public Class getEntityClass(String database, String schema, String table) {
        if(!this.puNode.isExisting(database, schema, table)) {
            throw new IllegalArgumentException(
                    "Unexpected catalog.schema.table: " + database + '.' + schema + '.' + table);
        }
        Class entityClass = null;
        for(Class dbClass:persistenceUnitClasses) {
            if(this.getTableName(dbClass).equals(table)) {
                entityClass = dbClass;
                break;
            }
        }
//Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
//        "Database: {0}, table: {1}, generated class: {2}", 
//        new Object[]{database, table, entityClass});        
        return entityClass;
    }
    
    @Override
    public int getColumnIndex(Class entityClass, String column) {
        return this.puNode.getColumnIndex(entityClass, column);
    }

    @Override
    public String getIdColumnName(Class aClass) {
        
        String idColumnName = null;
        
        final Field [] fields = aClass.getDeclaredFields();
        for(Field field:fields) {
            Id id = field.getAnnotation(Id.class);
            if(id != null) {
                Column column = field.getAnnotation(Column.class);
                idColumnName = column.name();
                break;
            }
        }
//Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
//        "Class: {0}, id field name: {1}, id column name: {2}", 
//        new Object[]{aClass, idFieldName, idColumnName});        
        return idColumnName;
    }
    
    @Override
    public String [] getColumnNames(Class entityClass) {
        return this.puNode.getColumnNames(entityClass);
    }

    /**
     * @param entityType The entity type to find
     * @return <code>true</code> if the entity type is listed in the persistence configuration file, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityType(Class entityType) {
        return this.persistenceUnitClasses.contains(entityType);
    }
    
    /**
     * @param entityTypes The entity types to find
     * @return <code>true</code> if the entity types are listed in the persistence configuration file, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityTypes(List<Class> entityTypes) {
        return this.persistenceUnitClasses.containsAll(entityTypes);
    }

    @Override
    public List<String> fetchExistingListedTables(
            Function<String, EntityManagerFactory> emfProvider) 
            throws SQLException {
        
        final List<String> tableNames = new MetaDataAccessImpl(emfProvider).fetchStringMetaData(
                this.persistenceUnit, null, null, null, null, MetaDataAccess.TABLE_NAME);
        
        final Function<Class, String> getTableName = new GetTableNameFromAnnotation();
        
        final List<String> existingListedTableNames = this.getEntityClasses().stream()
                .map((cls) -> getTableName.apply(cls))
                .filter((tableName) -> tableNames.contains(tableName))
                .collect(Collectors.toList());
        
        return existingListedTableNames;
    }
    
    @Override
    public boolean isAnyListedTableExisting() {
        boolean output = false;
        if(this.puNode != null) {
            final Collection<Class> classes = this.getEntityClasses();
            final Function<Class, String> getTableName = new GetTableNameFromAnnotation();
            for(Class cls : classes) {
                final String tableNameFromAnnotation = getTableName.apply(cls);
                Objects.requireNonNull(tableNameFromAnnotation);
                if(this.puNode.findFirstNode(this.puNode, 
                        PersistenceNode.table.getLevel(), tableNameFromAnnotation).isPresent()) {
                    output = true;
                    break;
                }
            }
        }
        return output;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //  Delegated methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isAnyTableExisting() {
        return this.puNode.isAnyTableExisting();
    }
    
    @Override
    public Node<String> findFirstTableNodeOrException(Class entityClass) {
        return this.puNode.findFirstTableNodeOrException(entityClass);
    }

    @Override
    public boolean isExisting(String database, String schema, String table) {
        return this.puNode.isExisting(database, schema, table);
    }
    
    @Override
    public Optional<Node<String>> findFirstNode(Node<String> offset, int nodeLevel, String nodeValue) {
        return puNode.findFirstNode(offset, nodeLevel, nodeValue);
    }

    @Override
    public Node<String> findFirstNodeOrException(Node<String> offset, int nodeLevel, String nodeValue) {
        return puNode.findFirstNodeOrException(offset, nodeLevel, nodeValue);
    }

    @Override
    public boolean isRoot() {
        return puNode.isRoot();
    }

    @Override
    public boolean isLeaf() {
        return puNode.isLeaf();
    }

    @Override
    public int getLevel() {
        return puNode.getLevel();
    }

    @Override
    public Node<String> getRoot() {
        return puNode.getRoot();
    }

    @Override
    public Optional<Node<String>> findFirstChild(String... path) {
        return puNode.findFirstChild(path);
    }

    @Override
    public Optional<Node<String>> findFirst(Node<String> offset, String... path) {
        return puNode.findFirst(offset, path);
    }

    @Override
    public Optional<Node<String>> findFirstChild(Predicate<Node<String>> nodeTest) {
        return puNode.findFirstChild(nodeTest);
    }

    @Override
    public Optional<Node<String>> findFirst(Node<String> offset, Predicate<Node<String>> nodeTest) {
        return puNode.findFirst(offset, nodeTest);
    }

    @Override
    public boolean addChild(Node<String> child) {
        return puNode.addChild(child);
    }

    @Override
    public List<Node<String>> getChildren() {
        return puNode.getChildren();
    }

    @Override
    public String getName() {
        return this.persistenceUnit;
    }

    @Override
    public Optional<String> getValue() {
        return puNode.getValue();
    }

    @Override
    public String getValueOrDefault(String outpufIfNone) {
        return puNode.getValueOrDefault(outpufIfNone);
    }

    @Override
    public Optional<Node<String>> getParent() {
        return puNode.getParent();
    }

    @Override
    public Node<String> getParentOrDefault(Node<String> outputIfNone) {
        return puNode.getParentOrDefault(outputIfNone);
    }
}

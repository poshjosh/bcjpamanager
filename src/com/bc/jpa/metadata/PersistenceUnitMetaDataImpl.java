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
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 10:42:32 PM
 */
public class PersistenceUnitMetaDataImpl implements PersistenceUnitMetaData, Serializable {

    private transient static final Logger logger = 
            Logger.getLogger(PersistenceUnitMetaDataImpl.class.getName());

    private final String persistenceUnit;
    
    private final Set<Class> persistenceUnitClasses;
    
    private boolean built;
    
    private Node<String> node;
    
    private Map<Class, int[]> columnDisplaySizes;
    
    private Map<Class, int[]> columnNullables;
    
    private Map<Class, int[]> columnDataTypes;

    public PersistenceUnitMetaDataImpl(String persistenceUnit, Set<Class> puClasses) {
        this.persistenceUnit = Objects.requireNonNull(persistenceUnit);
        this.persistenceUnitClasses = Collections.unmodifiableSet(puClasses);
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
    public Node<String> build(Node<String> persistenceNode, Function<String, EntityManagerFactory> emfProvider) throws SQLException{

        final MetaDataAccess metaDataAccess = new MetaDataAccessImpl(emfProvider);
        
        this.node = new PersistenceUnitNodeBuilderImpl(persistenceNode, metaDataAccess).build(persistenceUnit);

        this.columnDisplaySizes = new LinkedHashMap();
        this.columnNullables = new LinkedHashMap();
        this.columnDataTypes = new LinkedHashMap();
        
        for(Class puClass : this.persistenceUnitClasses) {
            
            final String tableName = this.getTableName(puClass);
            
            this.columnDisplaySizes.put(puClass, 
                    metaDataAccess.fetchColumnDisplaySizes(persistenceUnit, tableName));
            this.columnNullables.put(puClass, 
                    metaDataAccess.fetchColumnNullables(persistenceUnit, tableName));
            this.columnDataTypes.put(puClass, 
                    metaDataAccess.fetchColumnDataTypes(persistenceUnit, tableName));
        }
        
        this.built = true;
        
        return this.node;
    }

    @Override
    public Node<String> getNode() {
        return node;
    }

    @Override
    public Set<Class> getEntityClasses() {
        return this.persistenceUnitClasses;
    }

    @Override
    public int [] getColumnDisplaySizes(Class entityClass) {
        return columnDisplaySizes.get(entityClass);
    }

    @Override
    public int [] getColumnNullables(Class entityClass) {
        return columnNullables.get(entityClass);
    }

    @Override
    public int [] getColumnDataTypes(Class entityClass) {
        return columnDataTypes.get(entityClass);
    }

    @Override
    public String getCatalogName(Class entityClass) {
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Node<String> schemaNode = Objects.requireNonNull(tableNode.getParentOrDefault(null));
        final Node<String> catalogNode = Objects.requireNonNull(schemaNode.getParentOrDefault(null));
        return catalogNode.getValueOrDefault(null);
    }
    
    @Override
    public String getSchemaName(Class entityClass) {
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Node<String> schemaNode = Objects.requireNonNull(tableNode.getParentOrDefault(null));
        return schemaNode.getValueOrDefault(null);
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
    public String getTableName(Class entityClass) {
        final String tableName = this.getNodeSearch().findFirstNodeOrException(
                this.node, PersistenceNodeBuilder.NODE_LEVEL_TABLE, 
                this.getTableNameFromAnnotation(entityClass)).getValue().get();
        logger.finer(() -> "Entity class: "+entityClass+", table name: " + tableName);
        return tableName;
    }
    
    @Override
    public Class getEntityClass(String database, String schema, String table) {
        final Node<String> dbNode = this.getNodeSearch().findFirstNodeOrException(
                node, PersistenceNodeBuilder.NODE_LEVEL_CATALOG, database);
        dbNode.findFirstChild(schema, table).orElseThrow(
                () -> new IllegalArgumentException(
                        "Unexpected catalog.schema.table: " + database + '.' + schema + '.' + table
                )
        );
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
        int columnIndex = -1;
        final String [] columns = this.getColumnNames(entityClass);
        for(int i=0; i<columns.length; i++) {
            if(columns[i].equals(column)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
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
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Function<Node<String>, String> nodeToNodeValue = (node) -> node.getValueOrDefault(null);
        final List<String> columnNames = tableNode.getChildren().stream().map(nodeToNodeValue).collect(Collectors.toList());
        return columnNames.isEmpty() ? new String[0] : columnNames.toArray(new String[0]);
    }

    /**
     * If persistence unit name is null then all persistence units will be 
     * searched for the specified entity type
     * @param entityType The entity type to find
     * @return <code>true</code> if found, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityType(Class entityType) {
        final Collection<Set<Class>> source = Collections.singleton(this.getEntityClasses());
        for(Set<Class> puClasses : source) {
            if(puClasses.contains(entityType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<String> fetchExistingListedTables(
            Function<String, EntityManagerFactory> emfProvider) 
            throws SQLException {
        
        final List<String> tableNames = new MetaDataAccessImpl(emfProvider).fetchStringMetaData(
                this.persistenceUnit, null, null, null, null, MetaDataAccess.TABLE_NAME);
        
        final List<String> names = this.getEntityClasses().stream()
                .map((cls) -> this.getTableNameFromAnnotation(cls))
                .collect(Collectors.toList());
        
        final List<String> output = new ArrayList(names.size());
        
        for(String name : names) {
            
            if(tableNames.contains(name)) {
                
                output.add(name);
            }
        }
        
        return output;
    }
    
    @Override
    public boolean isAnyListedTableExisting() {
        
        boolean output = false;
        final Node<String> puNode = this.getNode();
        if(puNode != null) {
            final Set<Class> classes = this.getEntityClasses();
            final NodeSearch nodeSearch = this.getNodeSearch();
            for(Class cls : classes) {
                final String tableNameFromAnnotation = this.getTableNameFromAnnotation(cls);
                Objects.requireNonNull(tableNameFromAnnotation);
                if(nodeSearch.findFirstNode(puNode, 
                        PersistenceNodeBuilder.NODE_LEVEL_TABLE, tableNameFromAnnotation).isPresent()) {
                    output = true;
                    break;
                }
            }
        }
        return output;
    }
    
    @Override
    public boolean isAnyTableExisting() {
        boolean output = false;
        final Node<String> root = this.getNode();
        final List<Node<String>> catalogNodes = root.getChildren();
        for(Node<String> catalogNode : catalogNodes) {
            final List<Node<String>> schemaNodes = catalogNode.getChildren();
            for(Node<String> schemaNode : schemaNodes) {
                if(!schemaNode.getChildren().isEmpty()) {
                    output = true;
                    break;
                }
            }
        }
        return output;
    }
    
    public Node<String> findFirstTableNodeOrException(Class entityClass) {
        
        final String tableNameFromAnnotation = this.getTableNameFromAnnotation(entityClass);
        
//System.out.println("\n@"+this.getClass()+", entity type: "+entityClass.getName()+"\nPersistence unit node: " + puNode);
        final Node<String> tableNode = this.getNodeSearch().findFirstNodeOrException(node, 
                PersistenceNodeBuilder.NODE_LEVEL_TABLE, tableNameFromAnnotation);
        
//System.out.println("Table node: " + tableNode + "\nPersistence unit of table: " +tableNode.getParentOrDefault(null).getParentOrDefault(null).getParentOrDefault(null).getValueOrDefault(null));
        return tableNode;
    }

    /**
     * @param aClass The class whose table name is to be returned
     * @return The table name as extracted from the supplied entity type's annotation
     */
    public String getTableNameFromAnnotation(Class aClass) {
        Table table = (Table)aClass.getAnnotation(Table.class);
        logger.finer(() -> "Entity class: "+aClass.getName()+", table annotation: " + table);
        return table.name();
    }

    public NodeSearch getNodeSearch() {
        return new NodeSearchImpl();
    }
}

/*
 * Copyright 2018 NUROX Ltd.
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
import com.bc.node.NodeImpl;
import com.bc.node.NodeValueTest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 9, 2018 10:29:14 PM
 */
public class PersistenceUnitNodeImpl extends NodeImpl<String> implements PersistenceUnitNode {

    private transient static final Logger LOG = 
            Logger.getLogger(PersistenceUnitMetaDataImpl.class.getName());

    private final boolean caseInsensitiveNames;

    public PersistenceUnitNodeImpl(Node<String> persistenceNode, String persistenceUnitName) {
        this(PersistenceNode.persistence_unit.getTagName(), persistenceUnitName, persistenceNode, false);
    }
    
    public PersistenceUnitNodeImpl(String name, String value, Node<String> parent, boolean caseInsensitiveNames) {
        super(name, value, parent);
        if(this.getLevel() != PersistenceNode.persistence_unit.getLevel()) {
            throw new IllegalArgumentException();
        }
        this.caseInsensitiveNames = caseInsensitiveNames;
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
    public String getTableName(Class entityClass) {
        final String tableName = this.findFirstNodeOrException(
                this, PersistenceNode.table.getLevel(), 
                new GetTableNameFromAnnotation().apply(entityClass)).getValue().get();
        LOG.finer(() -> "Entity class: "+entityClass+", table name: " + tableName);
        return tableName;
    }
    
    @Override
    public boolean isExisting(String database, String schema, String table) {
        final Node<String> dbNode = this.findFirstNodeOrException(
                this, PersistenceNode.catalog.getLevel(), database);
        return dbNode.findFirstChild(schema, table).isPresent();
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
    public String [] getColumnNames(Class entityClass) {
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Function<Node<String>, String> nodeToNodeValue = (n) -> n.getValueOrDefault(null);
        final List<String> columnNames = tableNode.getChildren().stream().map(nodeToNodeValue).collect(Collectors.toList());
        return columnNames.isEmpty() ? new String[0] : columnNames.toArray(new String[0]);
    }

    @Override
    public boolean isAnyTableExisting() {
        boolean output = false;
        final List<Node<String>> catalogNodes = this.getChildren();
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
    
    @Override
    public Node<String> findFirstTableNodeOrException(Class entityClass) {
        
        final String tableNameFromAnnotation = new GetTableNameFromAnnotation().apply(entityClass);
        
//System.out.println("\n@"+this.getClass()+", entity type: "+entityClass.getName()+"\nPersistence unit node: " + puNode);
        final Node<String> tableNode = this.findFirstNodeOrException(this, 
                PersistenceNode.table.getLevel(), tableNameFromAnnotation);
        
//System.out.println("Table node: " + tableNode + "\nPersistence unit of table: " +tableNode.getParentOrDefault(null).getParentOrDefault(null).getParentOrDefault(null).getValueOrDefault(null));
        return tableNode;
    }

    @Override
    public Node<String> findFirstNodeOrException(Node<String> offset, int nodeLevel, String nodeValue) {
        final Node<String> node = this.findFirstNode(offset, nodeLevel, nodeValue).orElseThrow(
                () -> getIllegalArgumentException(nodeLevel, nodeValue)
        );
        return node;
    }
    
    @Override
    public Optional<Node<String>> findFirstNode(Node<String> offset, int nodeLevel, String nodeValue) {
        final Predicate<Node<String>> nodeTest = this.getNodeTest(nodeLevel, nodeValue);
        final Optional<Node<String>> optionalNode = offset.findFirstChild(nodeTest);
        return optionalNode;
    }

    private Predicate<Node<String>> getNodeTest(int level, String value) {
        return new NodeValueTest(level, value, this.caseInsensitiveNames);
    }
    
    private RuntimeException getIllegalArgumentException(int nodeLevel, String nodeValue) {
        return new IllegalArgumentException("Unexpected node level: " + 
                this.getLevelName(nodeLevel, String.valueOf(nodeLevel)) + " name: " + nodeValue);
    }

    private String getLevelName(int offset, String outputIfNone) {
        final PersistenceNode [] values = PersistenceNode.values();
        for(PersistenceNode value : values) {
            if(value.getLevel() == offset) {
                return value.getTagName();
            }
        }
        return outputIfNone;
    }
}

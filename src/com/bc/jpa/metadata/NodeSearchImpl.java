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
import com.bc.node.NodeValueTest;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 10:53:03 PM
 */
public class NodeSearchImpl implements NodeSearch {

    private final boolean caseInsensitiveNames;

    public NodeSearchImpl() {
        this(false);
    }

    public NodeSearchImpl(boolean caseInsensitiveNames) {
        this.caseInsensitiveNames = caseInsensitiveNames;
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
        return new IllegalArgumentException("Unexpected " + this.getLevelName(nodeLevel) + " name: " + nodeValue);
    }

    private String getLevelName(int offset) {
        switch(offset) {
            case PersistenceNodeBuilder.NODE_LEVEL_PERSISTENCE: return "persistence";
            case PersistenceNodeBuilder.NODE_LEVEL_PERSISTENCE_UNIT: return "persistence-unit";
            case PersistenceNodeBuilder.NODE_LEVEL_CATALOG: return "catalog";
            case PersistenceNodeBuilder.NODE_LEVEL_SCHEMA: return "schema";
            case PersistenceNodeBuilder.NODE_LEVEL_TABLE: return "table";
            case PersistenceNodeBuilder.NODE_LEVEL_COLUMN: return "column";
            default: throw new IllegalArgumentException("Unexpected persistence offset. Possible values: 0=persisence,1=persistenceUnit,2=catalog/database,3=schema,4=table,5=column");
        }
    }
}

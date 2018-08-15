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

import com.bc.node.Node;
import java.util.Optional;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 9, 2018 10:37:31 PM
 */
public interface PersistenceUnitNode extends Node<String> {

    Node<String> findFirstTableNodeOrException(Class entityClass);

    Optional<Node<String>> findFirstNode(Node<String> offset, int nodeLevel, String nodeValue);

    Node<String> findFirstNodeOrException(Node<String> offset, int nodeLevel, String nodeValue);
    
    String getCatalogName(Class entityClass);

    int getColumnIndex(Class entityClass, String column);

    String[] getColumnNames(Class entityClass);

    String getSchemaName(Class entityClass);

    String getTableName(Class entityClass);

    boolean isAnyTableExisting();

    boolean isExisting(String database, String schema, String table);
}

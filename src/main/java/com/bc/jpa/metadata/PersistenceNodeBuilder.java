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

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 10:25:20 PM
 */
public interface PersistenceNodeBuilder {
    
    int NODE_LEVEL_PERSISTENCE = 0;
    int NODE_LEVEL_PERSISTENCE_UNIT = 1;
    int NODE_LEVEL_CATALOG = 2;
    int NODE_LEVEL_SCHEMA = 3;
    int NODE_LEVEL_TABLE = 4;
    int NODE_LEVEL_COLUMN = 5;
    
    String NODE_NAME_PERSISTENCE = "persistence";
    String NODE_NAME_PERSISTENCE_UNIT = "persistence-unit";
    String NODE_NAME_CATALOG = "catalog";
    String NODE_NAME_SCHEMA = "schema";
    String NODE_NAME_TABLE = "table";
    String NODE_NAME_COLUMN = "column";

    default Node<String> build() throws SQLException {
        return this.build(null);
    }

    Node<String> build(String rootNodeValue) throws SQLException;
    
    
    Node<String> buildUnit(String persistenceUnitName, Node<String> parent) throws SQLException;
}

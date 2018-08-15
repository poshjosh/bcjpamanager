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

import com.bc.node.Node;
import java.sql.SQLException;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 10:25:20 PM
 */
public interface PersistenceNodeBuilder {
    
    default Node<String> build() throws SQLException {
        return this.build(null);
    }

    Node<String> build(String rootNodeValue) throws SQLException;
    
    
    Node<String> buildUnit(String persistenceUnitName, Node<String> parent) throws SQLException;
}

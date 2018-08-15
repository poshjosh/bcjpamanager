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

import java.io.Serializable;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 10, 2018 1:24:10 AM
 */
public enum PersistenceNode implements Serializable {
    
    persistence,
    persistence_unit("persistence-unit"),
    catalog,
    schema,
    table,
    column;
    
    private final String tagName;
    
    private PersistenceNode() {
        this(null);
    }
    private PersistenceNode(String tagName) {
        this.tagName = tagName == null ? this.name() : tagName;
    }
    
    public final String getTagName() {
        return this.tagName;
    }
    
    public final int getLevel() {
        return this.ordinal();
    }
}

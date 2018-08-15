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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 9, 2018 9:53:44 PM
 */
public interface EntityMetaDataAccess {

    Class fetchColumnClass(Class entityClass, String columName) throws SQLException;

    Class fetchColumnClass(Class entityClass, int columnIndex) throws SQLException;

    int [] fetchColumnDisplaySizes(Class entityClass) throws SQLException;

    int [] fetchColumnDataTypes(Class entityClass) throws SQLException;
    
    int [] fetchColumnNullables(Class entityClass) throws SQLException;
    
    List<Integer> fetchIntMetaData(Class entityClass, String columnNamePattern, int resultSetDataIndex) throws SQLException;

    List<String> fetchStringMetaData(Class entityClass, String columnNamePattern, int resultSetDataIndex) throws SQLException;

    int [] fetchColumnDisplaySizes(String tableName) throws SQLException;

    int [] fetchColumnDataTypes(String tableName) throws SQLException;
    
    int [] fetchColumnNullables(String tableName) throws SQLException;
    
    Set<String> fetchCatalogNames() throws SQLException;

    List<Integer> fetchIntMetaData(String catalog, String schemaNamePattern, 
            String tableNamePattern, String columnNamePattern, int resultSetDataIndex) throws SQLException;

    Set<String> fetchSchemaNames() throws SQLException;

    Map<String, String> fetchSchemaToCatalogNameMap() throws SQLException;

    List<String> fetchStringMetaData(String catalog, String schemaNamePattern, String tableNamePattern, String columnNamePattern, int resultSetDataIndex) throws SQLException;

    boolean isAnyTableExisting() throws SQLException;
    
    Map<String, List<String>> fetchCatalogToTableNameMap() throws SQLException;
}

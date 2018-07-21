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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 9:50:06 PM
 */
public interface MetaDataAccess {

    public static final int TABLE_CATALOG = 1;
    public static final int TABLE_SCHEMA = 2;
    public static final int TABLE_NAME = 3;
    public static final int COLUMN_NAME = 4;
    public static final int COLUMN_DATA_TYPE = 5;
    public static final int COLUMN_TYPE_NAME = 6;
    public static final int COLUMN_SIZE = 7;
    public static final int COLUMN_NULLABLE = 11;
    public static final int COLUMN_DEFAULT = 13;
    
    int [] fetchColumnDisplaySizes(String puName, String tableName) throws SQLException;

    int [] fetchColumnDataTypes(String puName, String tableName) throws SQLException;
    
    int [] fetchColumnNullables(String puName, String tableName) throws SQLException;
    
    Set<String> fetchCatalogNames(String puName) throws SQLException;

    List<Integer> fetchIntMetaData(
            String puName, String catalog, String schemaNamePattern, 
            String tableNamePattern, String columnNamePattern, int resultSetDataIndex) throws SQLException;

    Set<String> fetchSchemaNames(String puName) throws SQLException;

    Map<String, String> fetchSchemaToCatalogNameMap(String puName) throws SQLException;

    List<String> fetchStringMetaData(String puName, String catalog, String schemaNamePattern, String tableNamePattern, String columnNamePattern, int resultSetDataIndex) throws SQLException;

    boolean isAnyTableExisting(String persistenceUnit) throws SQLException;
    
    Map<String, List<String>> fetchCatalogToTableNameMap(String persistenceUnit) throws SQLException;
}

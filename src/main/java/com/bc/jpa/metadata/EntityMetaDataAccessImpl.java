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
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 9, 2018 9:58:37 PM
 */
public class EntityMetaDataAccessImpl extends MetaDataAccessImpl implements EntityMetaDataAccess{

    private final PersistenceUnitNode puNode;

    public EntityMetaDataAccessImpl(
            Function<String, EntityManagerFactory> emfProvider, 
            PersistenceUnitNode persistenceUnitNode) {
        super(emfProvider);
        this.puNode = Objects.requireNonNull(persistenceUnitNode);
    }

    @Override
    public int[] fetchColumnDisplaySizes(Class entityClass) throws SQLException {
        final String tableName = this.puNode.getTableName(entityClass);
        return this.fetchColumnDisplaySizes(puNode.getName(), tableName);
    }

    @Override
    public Class fetchColumnClass(Class entityClass, String columName) throws SQLException {
        final int columnIndex = this.puNode.getColumnIndex(entityClass, columName);
        return this.fetchColumnClass(entityClass, columnIndex);
    }
    
    @Override
    public Class fetchColumnClass(Class entityClass, int columnIndex) throws SQLException {
        final int [] dataTypes = fetchColumnDataTypes(entityClass);
        final Class columnClass = com.bc.sql.SQLUtils.getClass(dataTypes[columnIndex], null);
        return Objects.requireNonNull(columnClass);
    }

    @Override
    public int[] fetchColumnDataTypes(Class entityClass) throws SQLException {
        final String tableName = this.puNode.getTableName(entityClass);
        return this.fetchColumnDataTypes(puNode.getName(), tableName);
    }

    @Override
    public int[] fetchColumnNullables(Class entityClass) throws SQLException {
        final String tableName = this.puNode.getTableName(entityClass);
        return this.fetchColumnNullables(puNode.getName(), tableName);
    }

    @Override
    public List<Integer> fetchIntMetaData(Class entityClass, String columnNamePattern, int resultSetDataIndex) throws SQLException {
        final String tableName = entityClass == null ? null : this.puNode.getTableName(entityClass);
        return this.fetchIntMetaData(puNode.getName(), null, null, tableName, columnNamePattern, resultSetDataIndex);
    }

    @Override
    public List<String> fetchStringMetaData(Class entityClass, String columnNamePattern, int resultSetDataIndex) throws SQLException {
        final String tableName = entityClass == null ? null : this.puNode.getTableName(entityClass);
        return this.fetchStringMetaData(puNode.getName(), null, null, tableName, columnNamePattern, resultSetDataIndex);
    }

    @Override
    public int[] fetchColumnDisplaySizes(String tableName) throws SQLException {
        return this.fetchColumnDisplaySizes(this.puNode.getName(), tableName);
    }

    @Override
    public int[] fetchColumnDataTypes(String tableName) throws SQLException {
        return this.fetchColumnDataTypes(this.puNode.getName(), tableName);
    }

    @Override
    public int[] fetchColumnNullables(String tableName) throws SQLException {
        return this.fetchColumnNullables(this.puNode.getName(), tableName);
    }

    @Override
    public Set<String> fetchCatalogNames() throws SQLException {
        return this.fetchCatalogNames(this.puNode.getName());
    }

    @Override
    public List<Integer> fetchIntMetaData(String catalog, String schemaNamePattern, String tableNamePattern, String columnNamePattern, int resultSetDataIndex) throws SQLException {
        return this.fetchIntMetaData(this.puNode.getName(), catalog, schemaNamePattern, tableNamePattern, columnNamePattern, resultSetDataIndex);
    }

    @Override
    public Set<String> fetchSchemaNames() throws SQLException {
        return this.fetchSchemaNames(this.puNode.getName());
    }

    @Override
    public Map<String, String> fetchSchemaToCatalogNameMap() throws SQLException {
        return this.fetchSchemaToCatalogNameMap(this.puNode.getName());
    }

    @Override
    public List<String> fetchStringMetaData(String catalog, String schemaNamePattern, String tableNamePattern, String columnNamePattern, int resultSetDataIndex) throws SQLException {
        return this.fetchStringMetaData(this.puNode.getName(), catalog, schemaNamePattern, tableNamePattern, columnNamePattern, resultSetDataIndex);
    }

    @Override
    public boolean isAnyTableExisting() throws SQLException {
        return this.isAnyTableExisting(this.puNode.getName());
    }

    @Override
    public Map<String, List<String>> fetchCatalogToTableNameMap() throws SQLException {
        return this.fetchCatalogToTableNameMap(this.puNode.getName());
    }
}

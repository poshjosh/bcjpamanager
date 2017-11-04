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

import com.bc.jpa.functions.ExtractColumnData;
import com.bc.jpa.functions.ExtractResultIntegerList;
import com.bc.jpa.functions.ExtractResultStringList;
import com.bc.jpa.functions.ExtractSchemaNames;
import com.bc.jpa.functions.ExtractSchemaToCatalogMappings;
import com.bc.jpa.functions.FetchMetaDataImpl;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;
import com.bc.jpa.functions.FetchMetaData;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 13, 2017 9:42:12 PM
 */
public class MetaDataAccessImpl implements MetaDataAccess, Serializable {

    private final Function<String, EntityManagerFactory> emfProvider;
    
    public MetaDataAccessImpl(Function<String, EntityManagerFactory> emfProvider) {
        this.emfProvider = Objects.requireNonNull(emfProvider);
    }

    @Override
    public int [] fetchColumnDisplaySizes(String puName, String tableName) throws SQLException {
        return this.fetchIntMetaData(puName, tableName, MetaDataAccess.COLUMN_SIZE);
    }
    
    @Override
    public int [] fetchColumnDataTypes(String puName, String tableName) throws SQLException {
        return this.fetchIntMetaData(puName, tableName, MetaDataAccess.COLUMN_DATA_TYPE);
    }
    
    @Override
    public int [] fetchColumnNullables(String puName, String tableName) throws SQLException {
        return this.fetchIntMetaData(puName, tableName, MetaDataAccess.COLUMN_NULLABLE);
    }

    public int [] fetchIntMetaData(String puName, String tableName, int resultSetDataIndex) throws SQLException {
        
        final List<Integer> fetched = this.fetchIntMetaData(
                puName, null, null, tableName, 
                null, resultSetDataIndex);
        
        final int [] output = new int[fetched.size()];
        
        int i = 0;
        for(Integer integer : fetched) {
            output[i++] = integer;
        }
        
        return output;
    }

    @Override
    public Set<String> fetchSchemaNames(String puName) throws SQLException {
        
        final FetchMetaData<Set<String>> fetchMetaData =
                new FetchMetaDataImpl(emfProvider, puName);
                
        final Set<String> output = fetchMetaData.apply(new ExtractSchemaNames());

        return output;
    }

    @Override
    public Set<String> fetchCatalogNames(String puName) throws SQLException {
        return new LinkedHashSet(this.fetchSchemaToCatalogNameMap(puName).values());
    }
    
    @Override
    public Map<String, String> fetchSchemaToCatalogNameMap(String puName) throws SQLException {
        
        final FetchMetaData<Map<String, String>> fetchMetaData =
                new FetchMetaDataImpl(emfProvider, puName);
                
        final Map<String, String> output = fetchMetaData.apply(new ExtractSchemaToCatalogMappings());

        return output;
    }

    @Override
    public List<Integer> fetchIntMetaData(
            String puName, String catalog, String schemaNamePattern, 
            String tableNamePattern, String columnNamePattern, 
            int resultSetDataIndex) throws SQLException {
        
//        System.out.println("\nPersistence unit: " + puName + ", catalog: " + catalog + 
//                ", schema: " + schemaNamePattern + ", table: " + tableNamePattern + ". @" + this.getClass());
        
        final FetchMetaData<List<Integer>> fetchMetaData =
                new FetchMetaDataImpl(emfProvider, puName);

        final List<Integer> output = fetchMetaData.apply(new ExtractColumnData(
                catalog, schemaNamePattern, tableNamePattern, 
                columnNamePattern, resultSetDataIndex,
                new ExtractResultIntegerList()
        ));
        
        return output;
    }

    @Override
    public List<String> fetchStringMetaData(
            String puName, String catalog, String schemaNamePattern, 
            String tableNamePattern, String columnNamePattern, 
            int resultSetDataIndex) throws SQLException {
        
//        System.out.println("Persistence unit: " + puName + ", catalog: " + catalog + 
//                ", schema: " + schemaNamePattern + ", table: " + tableNamePattern + ". @" + this.getClass());
        
        final FetchMetaData<List<String>> fetchMetaData =
                new FetchMetaDataImpl(emfProvider, puName);

        final List<String> output = fetchMetaData.apply(new ExtractColumnData(
                catalog, schemaNamePattern, tableNamePattern, 
                columnNamePattern, resultSetDataIndex,
                new ExtractResultStringList()
        ));

        return output;
    }

    @Override
    public boolean isAnyTableExisting(String persistenceUnit) throws SQLException {
        boolean output = false;
        final Set<String> catalogNames = this.fetchCatalogNames(persistenceUnit);
        for(String catalogName : catalogNames) {
            final List<String> tableNames = this.fetchStringMetaData(
                    persistenceUnit, catalogName, null, null, null, TABLE_NAME);
            if(!tableNames.isEmpty()) {
                output = true;
                break;
            }
        }
        return output;
    }
    
    @Override
    public Map<String, List<String>> fetchCatalogToTableNameMap(String persistenceUnit) throws SQLException {
        final Map<String, List<String>> output = new HashMap();
        final Set<String> catalogNames = this.fetchCatalogNames(persistenceUnit);
        for(String catalogName : catalogNames) {
            final List<String> tableNames = this.fetchStringMetaData(
                    persistenceUnit, catalogName, null, null, null, TABLE_NAME);
            output.put(catalogName, tableNames);
        }
        return Collections.unmodifiableMap(output);
    }
}

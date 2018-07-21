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

package com.bc.jpa.functions;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 14, 2017 10:28:07 PM
 */
public class ExtractColumnData<T> implements MetaDataParser<List<T>> {

    private final String catalog; 
    
    private final String schemaNamePattern;
    
    private final String tableNamePattern; 
    
    private final String columnNamePattern;
    
    private final int resultSetDataIndex;
    
    private final BiFunction<ResultSet, Integer, List<T>> extractor;

    public ExtractColumnData(String catalog, String schemaNamePattern, 
            String tableNamePattern, String columnNamePattern, 
            int resultSetDataIndex, BiFunction<ResultSet, Integer, List<T>> extractor) {
        this.catalog = catalog;
        this.schemaNamePattern = schemaNamePattern;
        this.tableNamePattern = tableNamePattern;
        this.columnNamePattern = columnNamePattern;
        this.resultSetDataIndex = resultSetDataIndex;
        this.extractor = Objects.requireNonNull(extractor);
    }
            
    @Override
    public List<T> apply(DatabaseMetaData dbMetaData) {
        
        final List<T> output;
        
        try{
        
            final ResultSet resultSet = dbMetaData.getColumns(
                    catalog, schemaNamePattern, tableNamePattern, columnNamePattern);

            output = extractor.apply(resultSet, resultSetDataIndex);
            
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        
        return output.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(output);
    }
}

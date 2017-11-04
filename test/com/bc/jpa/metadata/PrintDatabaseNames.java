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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 16, 2017 4:59:04 PM
 */
public class PrintDatabaseNames implements Consumer<String> {

    private final Function<String, EntityManagerFactory> jpaContext;

    public PrintDatabaseNames(Function<String, EntityManagerFactory> jpaContext) {
        this.jpaContext = Objects.requireNonNull(jpaContext);
    }

    @Override
    public void accept(String persistenceUnit) {
        
        final MetaDataAccess metaDataSource = new MetaDataAccessImpl(jpaContext);
        
        try{
            
            System.out.println("\nPrinting catalog/table/column data for persistence unit: " + persistenceUnit);

            final Map<String, List<String>> names = metaDataSource
                    .fetchCatalogToTableNameMap(persistenceUnit);
            
            Collection<String> catNames = names.keySet();
            
            if(names.isEmpty()) {
                
                catNames = metaDataSource.fetchCatalogNames(persistenceUnit);
                
                if(catNames.isEmpty()) {
                    
                    catNames = metaDataSource.fetchStringMetaData(persistenceUnit, null, null, null, null, MetaDataAccess.TABLE_CATALOG);
                }
            }

            for(String catalog : catNames) {

                final Set<String> tables = 
                        metaDataSource.fetchStringMetaData(persistenceUnit, catalog, null, null, null, MetaDataAccess.TABLE_NAME)
                                .stream().collect(Collectors.toSet());

                for(String table : tables) {

                    final List<String> columns = metaDataSource.fetchStringMetaData(persistenceUnit, catalog, null, table, null, MetaDataAccess.COLUMN_NAME);

                    System.out.println(catalog + "." + table + '=' + columns);                
                }
            }
        }catch(SQLException e) {
            
            e.printStackTrace();
        }
    }
}

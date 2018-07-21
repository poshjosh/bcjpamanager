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

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 15, 2017 4:05:43 PM
 */
public class GetCatalogSeparatorForPersistenceUnit implements Function<String, String> {

    private final Function<String, EntityManagerFactory> emfProvider;
    
    public GetCatalogSeparatorForPersistenceUnit(            
            Function<String, EntityManagerFactory> emfProvider) {
        this.emfProvider = Objects.requireNonNull(emfProvider);
    }

    @Override
    public String apply(String persistenceUnit) {
        final FetchMetaData<String> fetchMetaData =
                new FetchMetaDataImpl(emfProvider, persistenceUnit);
        final MetaDataParser<String> getCatalogSeprator = (dbmd) -> {
            try{
                return dbmd.getCatalogSeparator();
            }catch(SQLException e) { throw new RuntimeException(e); }
        };
        return fetchMetaData.apply(getCatalogSeprator);
    }
}

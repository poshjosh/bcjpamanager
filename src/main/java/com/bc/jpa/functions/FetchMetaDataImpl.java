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
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 14, 2017 9:45:01 PM
 */
public class FetchMetaDataImpl<T> implements FetchMetaData<T> {

    private final Function<String, EntityManagerFactory> emfProvider; 
    
    private final String persistenceUnit;

    public FetchMetaDataImpl(Function<String, EntityManagerFactory> emfProvider, String persistenceUnit) {
        this.emfProvider = Objects.requireNonNull(emfProvider);
        this.persistenceUnit = Objects.requireNonNull(persistenceUnit);
    }
    
    @Override
    public T apply(MetaDataParser<T> action) {
        
        final T output;
        
        final EntityManager em = this.emfProvider.apply(persistenceUnit).createEntityManager();
        
        try{
            em.getTransaction().begin();
            
            final DatabaseMetaData metaData = this.getDatabaseMetaData(em);
            
            output = action.apply(metaData);
            
            em.getTransaction().commit();
            
        }catch(SQLException e) {
            
            throw new RuntimeException(e);
        }
        
        return output;
    }

    private DatabaseMetaData getDatabaseMetaData(EntityManager entityManager) throws SQLException {
        
        final java.sql.Connection connection = entityManager.unwrap(java.sql.Connection.class);

        final DatabaseMetaData dbMetaData = connection.getMetaData();

        return dbMetaData;
    }
}

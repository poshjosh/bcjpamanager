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

package com.bc.jpa.context;

import com.bc.jpa.EntityManagerFactoryCreator;
import com.bc.jpa.dao.Dao;
import com.bc.jpa.dao.eclipselink.DaoEclipselinkOptimized;
import com.bc.jpa.metadata.PersistenceMetaData;
import com.bc.sql.SQLDateTimePatterns;
import java.net.URI;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 11:39:54 AM
 */
public class PersistenceContextEclipselinkOptimized extends PersistenceContextImpl {

    public PersistenceContextEclipselinkOptimized(
            URI persistenceConfigUri, SQLDateTimePatterns dateTimePatterns) {
        super(persistenceConfigUri, dateTimePatterns);
    }

    public PersistenceContextEclipselinkOptimized(
            URI persistenceConfigUri, 
            EntityManagerFactoryCreator emfCreator, 
            SQLDateTimePatterns dateTimePatterns) {
        super(persistenceConfigUri, emfCreator, dateTimePatterns);
    }

    public PersistenceContextEclipselinkOptimized(
            PersistenceMetaData metaData, 
            EntityManagerFactoryCreator emfCreator, 
            SQLDateTimePatterns dateTimePatterns) {
        super(metaData, emfCreator, dateTimePatterns);
    }

    @Override
    public Dao getDao(String persistenceUnit) {
        
        return new DaoEclipselinkOptimized(
                this.getEntityManager(persistenceUnit), 
                this.getDatabaseFormat(persistenceUnit)
        );
    }
}

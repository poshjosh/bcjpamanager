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

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.metadata.PersistenceUnitMetaData;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 2:01:57 PM
 */
public class GetColumnNamesOfType implements BiFunction<Class, Class, Collection<String>> {

    private final PersistenceUnitContext persistenceUnitContext;

    public GetColumnNamesOfType(PersistenceUnitContext jpaContext) {
        this.persistenceUnitContext = Objects.requireNonNull(jpaContext);
    }
    
    @Override
    public Collection<String> apply(Class entityType, Class columnType) {
        
        final Set<String> output = new LinkedHashSet();
        
        final PersistenceUnitMetaData metaData = this.persistenceUnitContext.getMetaData();
        final String [] columnNames = metaData.getColumnNames(entityType);
        final EntityUpdater updater = this.persistenceUnitContext.getEntityUpdater(entityType);
        
        for(String columnName : columnNames) {
            final Method method = updater.getMethod(false, columnName);
            if(method.getReturnType().equals(columnType)) {
                output.add(columnName);
            }
        }
        
        return output;
    }
}

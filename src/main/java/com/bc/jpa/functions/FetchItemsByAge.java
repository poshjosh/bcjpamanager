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

package com.bc.jpa.functions;

import com.bc.functions.GetDateOfAge;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.Select;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 5, 2018 2:08:47 PM
 * @param <E>
 */
public class FetchItemsByAge<E> implements BiFunction<String, Long, List<E>>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(FetchItemsByAge.class.getName());

    private final PersistenceUnitContext puContext;
    
    private final Class<E> entityClass;
    
    private final BiFunction<Integer, TimeUnit, Date> getDateOfAge;
    
    private final UnaryOperator<Select<E>> formatter;
    
    private final int first;
    
    private final int limit;

    public FetchItemsByAge(
            PersistenceUnitContext puContext, Class<E> entityClass, int max) { 
        this(puContext, entityClass, (selector) -> selector, 0, max);
    }
    
    public FetchItemsByAge(
            PersistenceUnitContext puContext, Class<E> entityClass, 
            UnaryOperator<Select<E>> formatter, int first, int max) { 
        this.puContext = Objects.requireNonNull(puContext);
        this.entityClass = Objects.requireNonNull(entityClass);
        this.getDateOfAge = new GetDateOfAge();
        this.formatter = Objects.requireNonNull(formatter);
        this.first = first;
        this.limit = max;
    }

    @Override
    public List<E> apply(String dateColumnName, Long ageMillis) {
    
        final Date date = this.getDateOfAge.apply((int)TimeUnit.MILLISECONDS.toDays(ageMillis), TimeUnit.DAYS);
    
        LOG.finer(() -> "Date: " + date);
        
        final List<E> entityList;
        
        try(Select<E> select = puContext.getDaoForSelect(entityClass)) {

            select.from(entityClass);
            
            entityList = this.formatter.apply(select)
                    .where(entityClass, dateColumnName, Select.GT, date)
                    .createQuery()
                    .setFirstResult(first < 0 ? 0 : first)
                    .setMaxResults(limit < 1 ? Integer.MAX_VALUE : limit)
                    .getResultList();
        }
        
        return entityList;
    }
}

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

package com.bc.jpa.util;

import com.bc.jpa.context.JpaContext;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 16, 2017 3:23:17 PM
 */
public abstract class SelectDaoBuilderAbstraction<T> implements SelectDaoBuilder<T> {

    private JpaContext jpaContext;

    private Class<T> resultType;
    
    private String textToFind;
    
    private Collection<Class> typesToSearch;
    
    protected SelectDaoBuilderAbstraction() { 
        this.typesToSearch = Collections.EMPTY_SET;
    }

    @Override
    public SelectDaoBuilder<T> jpaContext(JpaContext jpaContext) {
        this.jpaContext = jpaContext;
        return this;
    }

    @Override
    public SelectDaoBuilder<T> resultType(Class<T> resultType) {
        this.resultType = resultType;
        return this;
    }
    
    @Override
    public SelectDaoBuilder<T> textToFind(String text) {
        this.textToFind = text;
        return this;
    }

    @Override
    public SelectDaoBuilder<T> typesToSearch(Collection<Class> types) {
        this.typesToSearch = types;
        return this;
    }

    public JpaContext getJpaContext() {
        return jpaContext;
    }
    
    public Class<T> getResultType() {
        return resultType;
    }

    public String getTextToFind() {
        return textToFind;
    }

    public Collection<Class> getTypesToSearch() {
        return typesToSearch;
    }
}


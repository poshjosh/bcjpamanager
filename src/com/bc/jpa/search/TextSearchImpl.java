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

package com.bc.jpa.search;

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.dao.Criteria;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Chinomso Bassey Ikwuagwu on May 20, 2017 4:18:37 AM
 */
public class TextSearchImpl implements TextSearch {
    
    private final JpaContext jpaContext;
    
    private final Map<String, Method[]> methodsCache;
    
    public TextSearchImpl(JpaContext jpaContext) {
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.methodsCache = new WeakHashMap<>();
    }
    
    /**
     * <p>Part count of the input value is reduced by the supplied factor before
     * being used as search text</p>
     * <table>
     *   <tr>
     *     <th>Value</th><th>Minimum parts</th><th>Factor</th><th>Actual text to find</th>
     *   </tr>
     *   <tr>
     *     <td>SOME FAKE VALUE</td><td>2</td><td>0.67</td><td>SOME FAKE</td>
     *   </tr>
     *   <tr>
     *     <td>SOME FAKE VALUE</td><td>3</td><td>0.67</td><td>SOME FAKE VALUE</td>
     *   </tr>
     *   <tr>
     *     <td>SOME FAKE VALUE I COOKED UP</td><td>4</td><td>0.67</td><td>SOME FAKE VALUE I</td>
     *   </tr>
     * </table>
     * @param <T> The generic type of the entity 
     * @param entityType The type of the entity
     * @param value The value to be resolved into the text to be searched for
     * @param minimumParts The minimum parts the input value must have to be eligible for reduction
     * @param factor The factor by which part count of the input value is reduced.
     * @return The list of entities matching the text that was searched
     * @see #getSearchText(java.lang.Object, float) 
     * @see #search(java.lang.Class, java.lang.String) 
     */
    @Override
    public <T> List<T> search(Class<T> entityType, Object value, int minimumParts, float factor) {
        
        final String text = this.getParts(value).size() > minimumParts 
                ? this.getSearchText(value, factor) : value.toString();
        
        return this.search(entityType, text);
    }

    /**
     * Search all <code>text type</code> columns of the specified entity type
     * for the specified text.
     * @param <T> The generic type of the entity 
     * @param entityType The type of the entity
     * @param text The text to search for
     * @param c The SQL comparison operator e.g 
     * {@link com.bc.jpa.dao.Criteria.ComparisonOperator#EQUALS EQUALS}
     * OR {@link com.bc.jpa.dao.Criteria.ComparisonOperator#LIKE LIKE} etc
     * @return The list of entities matching the text that was searched
     */
    @Override
    public <T> List<T> search(Class<T> entityType, String text, Criteria.ComparisonOperator c) {
        
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(text);
        Objects.requireNonNull(c);
        
        final List foundList;
        
        try(final BuilderForSelect<?> dao = this.jpaContext.getBuilderForSelect(entityType)) {

            final Collection<String> columnsToSearch = this.getColumnNamesToSearch(entityType);

            final Map<String, String> params = new HashMap<>();
            for(String column : columnsToSearch) {
                params.put(column, text);
            }
            
            foundList = dao
                    .where(entityType, c, Criteria.OR, params)
                    .createQuery()
                    .getResultList();
        }    
        
        return foundList;
    }
    
    /**
     * Search all <code>text type</code> columns of the specified entity type
     * for the specified text using the SQL <code>LIKE</code> keyword.
     * @param <T> The generic type of the entity 
     * @param entityType The type of the entity
     * @param text The text to search for
     * @return The list of entities matching the text that was searched
     */
    @Override
    public <T> List<T> search(Class<T> entityType, String text) {
        
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(text);
        
        final List foundList;
        
        try(final BuilderForSelect<?> dao = this.jpaContext.getBuilderForSelect(entityType)) {

            final Collection<String> columnsToSearch = this.getColumnNamesToSearch(entityType);

            foundList = dao
                    .search(entityType, text.trim(), columnsToSearch)
                    .createQuery()
                    .getResultList();
        }    
        
        return foundList;
    }

    @Override
    public boolean search(Object searchIn, Object searchFor, boolean textTypesOnly) {
        
        Objects.requireNonNull(searchIn);
        
        boolean found = false;
        
        final Method [] methods = this.getMethods(searchIn.getClass());
        
        for(Method method : methods) {
            if(method.getName().startsWith("get")) {
                if(!textTypesOnly || method.getReturnType() == String.class) {
                    
                    try{
                        
                        final Object methodValue = method.invoke(searchIn);

                        if(Objects.equals(methodValue, searchFor)) {
                            found = true;
                            break;
                        }
                    }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            } 
        }
        
        return found;
    }
    
    public Method [] getMethods(Class cls) {
        Method [] methods = this.methodsCache.get(cls.getName());
        if(methods == null) {
            methods = cls.getMethods();
            this.methodsCache.put(cls.getName(), methods);
        }
        return methods;
    }
    
    @Override
    public Collection<String> getColumnNamesToSearch(Class entityType) {
        
        final Set<String> output = new LinkedHashSet();
        
        final JpaMetaData metaData = this.jpaContext.getMetaData();
        final String [] columnNames = metaData.getColumnNames(entityType);
        final EntityUpdater updater = this.jpaContext.getEntityUpdater(entityType);
        
        for(String columnName : columnNames) {
            final Method method = updater.getMethod(false, columnName);
            if(method.getReturnType() == String.class) {
                output.add(columnName);
            }
        }
        
        return output;
    }

    @Override
    public String getSearchText(Object value, float factor) {
        
        return reconstitute(value, factor);
    }
    
    public String reconstitute(Object value, float factor) {
        
        if(factor > 1.0f) {
            throw new IllegalArgumentException();
        }
        
        String sval = value.toString();
        
        if(factor < 1.0f) {
            
            final List<String> parts = this.getParts(sval);
            
            if(parts.size() > 1) {
                
                final int n = (int)(parts.size() * factor); 

                if(n >= 0 && n < parts.size()) {
                    final String target = parts.get(n - 1);

                    final int off = sval.indexOf(target);

                    if(off != -1) {
                        final int end = off + target.length();
                        sval = sval.substring(0, end).trim();
                    }
                }
            }
        }
        
        return sval;
    }
    
    @Override
    public List<String> getParts(Object value) {
        
        final String sval = value.toString();
        
        final List<String> parts = new ArrayList(Arrays.asList(sval.split("\\s{1,}")));
//System.out.println("BEFORE Parts: "+parts+". @"+TextSearchImpl.class);            
        final Iterator<String> iter = parts.iterator();
        
        while(iter.hasNext()) {
            
            final String part = iter.next();
            
            if(part == null || part.trim().isEmpty()) {
                
                iter.remove();
            }
        }
//System.out.println(" AFTER Parts: "+parts+". @"+TextSearchImpl.class);                                
        return Collections.unmodifiableList(parts);
    }    
}

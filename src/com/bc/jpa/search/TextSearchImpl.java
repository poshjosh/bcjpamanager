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

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.Criteria;
import com.bc.jpa.dao.Select;
import com.bc.jpa.functions.GetColumnNamesOfType;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 1:58:39 PM
 */
public class TextSearchImpl implements TextSearch, Serializable {
    
    private final PersistenceUnitContext persistenceUnitContext;
    
    private final Map<String, Method[]> methodsCache;
    
    public TextSearchImpl(PersistenceUnitContext persistenceUnitContext) {
        this.persistenceUnitContext = Objects.requireNonNull(persistenceUnitContext);
        this.methodsCache = new WeakHashMap<>();
    }
    
    @Override
    public <T> List<T> search(Class<T> entityType, Object value, int minimumParts, 
            float factor, Function<Query, Query> queryFormatter) {
        
        final String text = this.getParts(value).size() > minimumParts 
                ? this.getSearchText(value, factor) : value.toString();
        
        return this.search(entityType, text, queryFormatter);
    }

    @Override
    public <T> List<T> search(Class<T> entityType, String text, 
            Criteria.ComparisonOperator c, Function<Query, Query> queryFormatter) {
        
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(text);
        Objects.requireNonNull(c);
        
        final List<T> foundList;
        
        try(final Select<T> dao = this.persistenceUnitContext.getDao().forSelect(entityType)) {

            final Collection<String> columnsToSearch = 
                    new GetColumnNamesOfType(this.persistenceUnitContext).apply(entityType, String.class);

            final Map<String, String> params;
            if(columnsToSearch.isEmpty()) {
                params = Collections.EMPTY_MAP;
            }else if(columnsToSearch.size() == 1) {
                params = Collections.singletonMap(columnsToSearch.toArray(new String[0])[0], text);
            }else{
                params = new HashMap<>();
                for(String column : columnsToSearch) {
                    params.put(column, text);
                }
            }
            
            final TypedQuery<T> query = dao
                    .from(entityType).where(c, Criteria.OR, params)
                    .createQuery();
                    
            foundList = queryFormatter.apply(query).getResultList();
        }    
        
        return foundList;
    }
    
    @Override
    public <T> List<T> search(Class<T> entityType, String text, 
            Function<Query, Query> queryFormatter) {
        
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(text);
        
        final List<T> foundList;
        
        try(final Select<T> dao = this.persistenceUnitContext.getDao().forSelect(entityType)) {

            final Collection<String> columnsToSearch = 
                    new GetColumnNamesOfType(this.persistenceUnitContext).apply(entityType, String.class);

            final TypedQuery<T> query = dao
                    .from(entityType).search(text.trim(), columnsToSearch)
                    .createQuery();
                    
            foundList = queryFormatter.apply(query).getResultList();
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
                if(!textTypesOnly || method.getReturnType().equals(String.class)) {
                    
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
//System.out.println("BEFORE Parts: "+parts+". @"+TextSearchDeprecated.class);            
        final Iterator<String> iter = parts.iterator();
        
        while(iter.hasNext()) {
            
            final String part = iter.next();
            
            if(part == null || part.trim().isEmpty()) {
                
                iter.remove();
            }
        }
//System.out.println(" AFTER Parts: "+parts+". @"+TextSearchDeprecated.class);                                
        return Collections.unmodifiableList(parts);
    }    
}

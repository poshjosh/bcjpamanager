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

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.search.TextSearch;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 29, 2017 9:22:19 AM
 */
public class FindEntity<T> implements BiFunction<Class<T>, String, T> {

    private final TextSearch textSearch;

    public FindEntity(PersistenceUnitContext context) {
        this(context.getTextSearch());
    }
    
    public FindEntity(TextSearch textSearch) {
        this.textSearch = Objects.requireNonNull(textSearch);
    }
    
    @Override
    public T apply(Class<T> entityType, String text) {
        final List<T> found = textSearch.search(entityType, text);
        if(found.isEmpty()) {
            throw new IllegalArgumentException("Entity not found. Entity matching text: " + text);
        }else if(found.size() > 1) {
            throw new IllegalArgumentException("More than One Entity found. Entity matching text: " + text);
        }else{
            return found.get(0);
        }
    }
}

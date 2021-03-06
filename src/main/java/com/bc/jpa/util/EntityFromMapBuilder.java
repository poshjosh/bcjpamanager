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

import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 11, 2017 8:35:41 PM
 */
public interface EntityFromMapBuilder {
    
    interface Formatter{
        
        Formatter NO_OP = new Formatter() {
            @Override
            public Object format(Object entity, String column, Object value) { return value; }
        };
        
        Object format(Object entity, String column, Object value);
    }
    
    interface ResultHandler{
        
        ResultHandler NO_OP = new ResultHandler() {
            @Override
            public void handleResult(Map src, Object result) { }
        };
        
        void handleResult(Map src, Object result);
    }

    EntityFromMapBuilder source(boolean lenient);
    
    EntityFromMapBuilder source(Map source);
    
    EntityFromMapBuilder target(Object target);
    
    EntityFromMapBuilder resultBuffer(Map<Map, Object> buffer);
    
    EntityFromMapBuilder formatter(Formatter resultHandler);
    
    EntityFromMapBuilder resultHandler(ResultHandler resultHandler);
    
    Object build();
}

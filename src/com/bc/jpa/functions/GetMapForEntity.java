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

import com.bc.jpa.util.MapBuilderForEntity;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 29, 2017 6:10:26 PM
 */
public class GetMapForEntity implements Function<Object, Map> {

    private final boolean nullsAllowed;

    public GetMapForEntity() {
        this(true);
    }
    
    public GetMapForEntity(boolean nullsAllowed) {
        this.nullsAllowed = nullsAllowed;
    }
    
    @Override
    public Map apply(Object entity) {
        final HashMap output = new LinkedHashMap();
        new MapBuilderForEntity()
                .nullsAllowed(nullsAllowed)
                .maxDepth(1)
                .maxCollectionSize(10)
                .sourceType(entity.getClass())
                .source(entity)
                .target(output)
                .build();
        return output;
    }
}

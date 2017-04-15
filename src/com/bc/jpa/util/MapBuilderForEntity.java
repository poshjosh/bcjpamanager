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

import com.bc.util.MapBuilderImpl;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 5, 2017 1:46:52 PM
 */
public class MapBuilderForEntity extends MapBuilderImpl {

    public MapBuilderForEntity() { }

    @Override
    public Map build() {
        
        if(this.getRecursionFilter() == null) {
            this.recursionFilter(new EntityRecursionFilter());
        }
        
        return super.build();
    }
}
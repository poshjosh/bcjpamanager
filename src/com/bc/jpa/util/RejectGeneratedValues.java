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

import com.bc.util.MapBuilder;
import java.lang.reflect.Method;
import javax.persistence.GeneratedValue;

/**
 * @author Chinomso Bassey Ikwuagwu on May 25, 2017 10:57:57 PM
 */
public class RejectGeneratedValues implements MapBuilder.MethodFilter{

    @Override
    public boolean test(Class type, Object object, Method method, String columnName) {
        try{
            return type.getDeclaredField(columnName).getAnnotation(GeneratedValue.class) == null;
        }catch(NoSuchFieldException e) {
            return false;
        }
    }
}

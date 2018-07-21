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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 7, 2018 10:41:38 PM
 */
public class ConvertToNumber<T extends Number> implements BiFunction<Object, Class<T>, T> {

    private transient static final Logger LOG = Logger.getLogger(ConvertToNumber.class.getName());

    @Override
    public T apply(Object value, Class<T> numberType) {
        return this.apply(value, numberType, null);
    }

    public T apply(Object value, Class<T> numberType, T outputIfNone) {
        
        Objects.requireNonNull(value);
        
        final Object output;
        
        if(numberType.isAssignableFrom(value.getClass())) {
            output = value;
        }else if((numberType == short.class || numberType == Short.class)){// && !(value instanceof Short)) {
            output = Short.valueOf(value.toString());
        }else if((numberType == int.class || numberType == Integer.class)){// && !(value instanceof Integer)) {
            output = Integer.valueOf(value.toString());
        }else if((numberType == long.class || numberType == Long.class)){// && !(value instanceof Long)) {
            output = Long.valueOf(value.toString());
        }else if((numberType == float.class || numberType == Float.class)){// && !(value instanceof Float)) {
            output = Float.valueOf(value.toString());
        }else if((numberType == double.class || numberType == Double.class)){// && !(value instanceof Double)) {
            output = Double.valueOf(value.toString());
        }else if((numberType == BigDecimal.class)){// && !(value instanceof BigDecimal)) {
            output = new BigDecimal(value.toString());
        }else{
            output = null;
        }
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "{0} {1} converted to {2} {3}",
                    new Object[]{value.getClass().getName(), value, numberType.getName(), output});
        }
        
        return output == null ? outputIfNone : (T)output;
    }
}

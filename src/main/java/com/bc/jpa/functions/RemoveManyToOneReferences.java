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

import com.bc.jpa.EntityReference;
import com.bc.jpa.util.JpaUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 11, 2017 5:29:25 PM
 */
public class RemoveManyToOneReferences implements Serializable, Function {

    private transient static final Logger logger = Logger.getLogger(RemoveManyToOneReferences.class.getName());
    
    private final EntityReference entityReference;

    public RemoveManyToOneReferences(EntityReference master) {
        this.entityReference = Objects.requireNonNull(master);
    }

    @Override
    public Object apply(Object entity) {
        
        final Class refClass = entity.getClass();

        final Class [] refingClasses = entityReference.getReferencingClasses(refClass);
        
        if(refingClasses != null && refingClasses.length != 0) {
            for(Class refingClass : refingClasses) {
                final Method setter = JpaUtil.getMethod(true, refClass, refingClass);
                if(setter == null) {
                    continue;
                }
                try{
                    logger.finer(() -> "Removing " + setter.getName());                    
                    setter.invoke(entity, new Object[]{null});
                }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.log(Level.WARNING, "Error invoking "+setter.getName()+" with argument: null", e);
                }
            }
        }
        return entity;
    }
}

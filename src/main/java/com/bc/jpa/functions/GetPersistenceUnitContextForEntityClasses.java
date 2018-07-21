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

import com.bc.jpa.context.PersistenceContext;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.metadata.PersistenceMetaData;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2018 1:50:58 AM
 */
public class GetPersistenceUnitContextForEntityClasses 
        implements Serializable, BiFunction<PersistenceContext, List<Class>, PersistenceUnitContext> {

    private transient static final Logger LOG = Logger.getLogger(GetPersistenceUnitContextForEntityClasses.class.getName());
    
    public GetPersistenceUnitContextForEntityClasses() { }
    
    @Override
    public final PersistenceUnitContext apply(PersistenceContext ctx, List<Class> entityClasses) {
        return this.getOrException(ctx, entityClasses);
    }
    
    public final PersistenceUnitContext getOrException(PersistenceContext ctx, List<Class> entityClasses) {
        final PersistenceUnitContext output = this.getOrDefault(ctx, entityClasses, null);
        return Objects.requireNonNull(output);
    }
    
    public PersistenceUnitContext getOrDefault(PersistenceContext ctx, List<Class> entityClasses, PersistenceUnitContext outputIfNone) {
        
        PersistenceUnitContext output = null;
        
        final PersistenceMetaData metaData = ctx.getMetaData();
        
        final Set<String> puNames = metaData.getPersistenceUnitNames();
        
        for(String puName : puNames) {
            
            final Set<Class> puClasses = metaData.getEntityClasses(puName);
            
            if(puClasses.containsAll(entityClasses)) {
                
                output = ctx.getContext(puName);

                LOG.fine(() -> "Selected persistence unit: " + puName + " for classes: " + entityClasses);
                
                break;
                
            }else{
                
                LOG.fine(() -> "Selected persistence unit: " + puName + " for classes: " + entityClasses);
            }
        }
        
        return output == null ? outputIfNone : output;
    }
}

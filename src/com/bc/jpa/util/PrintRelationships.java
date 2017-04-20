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

import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;
import com.bc.jpa.JpaUtil;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 16, 2017 7:18:19 PM
 */
public class PrintRelationships {
    
    private final JpaContext jpaContext;

    public PrintRelationships(JpaContext jpaContext) {
        this.jpaContext = Objects.requireNonNull(jpaContext);
    }

    public void execute(Object entity) {
        try{
            this.execute(entity, System.out);
        }catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(Object entity, Appendable tgt) throws IOException {
        
        final Class refClass = entity.getClass();
tgt.append("\nType: "+refClass);
tgt.append("\n------------------------------------------------------------");
        final JpaMetaData metaData = jpaContext.getMetaData();
        
        final Class [] refingClasses = metaData.getReferencingClasses(refClass);
tgt.append("\nJpaMetaData#getReferencingClasses("+refClass.getSimpleName()+") = "+Arrays.asList(refingClasses)); 

        final String [] refingCols = metaData.getReferencingColumns(refClass);
tgt.append("\nJpaMetaData#getReferencingColumns("+refClass.getSimpleName()+") = "+Arrays.asList(refingCols)); 

        final Map<Class, String> refing = metaData.getReferencing(refClass); 
tgt.append("\nJpaMetaData#getReferencing("+refClass.getSimpleName()+") = "+refing); 
        
        if(refingClasses != null && refingClasses.length != 0) {
            for(Class refingClass : refingClasses) {
                
                final Method getter = JpaUtil.getMethod(false, refClass, refingClass);
tgt.append("\nJpaUtil#getMethod(false, "+refClass.getSimpleName()+", "+refingClass.getSimpleName()+") = "+getter);                

                final Class [] refClasses = metaData.getReferenceClasses(refingClass);
tgt.append("\nJpaMetaData#getReferenceClasses("+refingClass.getSimpleName()+") = "+Arrays.asList(refClasses)); 
                
                final String refCol = metaData.getReferenceColumn(refClass, refingClass);
tgt.append("\nJpaMetaData#getReferenceColumn("+refClass.getSimpleName()+", "+refingClass.getSimpleName()+") = "+refCol);                                

                final String [] refingClassRefingCols = metaData.getReferencingColumns(refingClass, refCol);
tgt.append("\nJpaMetaData#getReferencingColumns("+refingClass.getSimpleName()+", "+refCol+") = "+Arrays.asList(refingClassRefingCols)); 

                final Map<String, String> refs = metaData.getReferences(refingClass);
tgt.append("\nJpaMetaData#getReferences("+refingClass.getSimpleName()+") = "+refs); 
                
                for(String refingCol : refingClassRefingCols) {
                    
                    final Class refingClassRefClass = metaData.getReferenceClass(refingClass, refingCol);
tgt.append("\nJpaMetaData#getReferenceClass("+refingClass.getSimpleName()+", "+refingCol+") = "+refingClassRefClass);                     
                }
            }
        }
    }
}

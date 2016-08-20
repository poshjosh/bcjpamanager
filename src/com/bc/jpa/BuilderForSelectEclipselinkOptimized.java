/*
 * Copyright 2016 NUROX Ltd.
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

package com.bc.jpa;

import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.dao.BuilderForSelectImpl;
import com.bc.util.XLogger;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.QueryHints;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 18, 2016 2:01:52 AM
 * @param <T>
 */
public class BuilderForSelectEclipselinkOptimized<T> extends BuilderForSelectImpl<T> {

    public BuilderForSelectEclipselinkOptimized(EntityManager em) {
        super(em);
    }

    public BuilderForSelectEclipselinkOptimized(EntityManager em, Class<T> resultType) {
        super(em, resultType);
    }

    public BuilderForSelectEclipselinkOptimized(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
    }

    @Override
    public TypedQuery<T> format(TypedQuery<T> tq) {
        
// http://java-persistence-performance.blogspot.com/2010/08/batch-fetching-optimizing-object-graph.html
// http://java-persistence-performance.blogspot.com/2011/06/how-to-improve-jpa-performance-by-1825.html
//                
        tq.setHint("eclipselink.read-only", "true");
        
// http://vard-lokkur.blogspot.com/2011/05/eclipselink-jpa-queries-optimization.html 
        
        Set<Class> entityTypes = this.getEntityTypes();
        
        boolean added = false;
        for(Class entityType:entityTypes) {
            
            final String entityName = entityType.getSimpleName();
            
            final String ch = Character.toString(entityName.charAt(0)).toLowerCase();
            
            Field [] fields = entityType.getDeclaredFields();
            
            for(Field field:fields) {
                
                if(accept(entityTypes, field)) {
                    
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);

                    if(oneToMany != null) {
                        final String HINT = ch + '.' + field.getName();
XLogger.getInstance().log(Level.FINER, "Entity type: {0}, Hint: {1}", 
        this.getClass(), entityType.getName(), HINT);
                        try{
                            tq.setHint(QueryHints.BATCH, HINT);
                            added = true;
                        }catch(IllegalArgumentException ignore) {
                            XLogger.getInstance().log(Level.WARNING, 
                            "While setting {0} = {1} on {2}, encountered: {3}", 
                            this.getClass(), QueryHints.BATCH, HINT, entityName, ignore);
                        }
                    }
                }
            }
        }
        if(added) {
            tq.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
        }
        return tq;
    }

    public boolean accept(Set<Class> classes, Field field) {
        boolean accepted;
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if(oneToMany == null) {
            accepted = false;
        }else{
            accepted = false;
            final Type type = field.getGenericType();
// Format:  java.util.XXX<Collection-Element-Type> e.g:  java.util.List<com.looseboxes.pu.entities.Productvariant>
            final String sval = type.toString();
            for(Class cls:classes) {
                if(sval.contains( "<" + cls.getName() + ">")) {
XLogger.getInstance().log(Level.FINER, "Accepted: true, Type: {0}, Class: {1}",
        this.getClass(), sval, cls.getName());
                    accepted = true;
                    break;
                }
            }
        }
        return accepted;
    }
}

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

package com.bc.jpa;

import com.bc.functions.GetSingle;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.Criteria.ComparisonOperator;
import com.bc.jpa.search.TextSearch;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 1:06:33 PM
 */
public class EntityReferenceImpl implements EntityReference, Serializable {

    private transient static final Logger LOG = Logger.getLogger(EntityReferenceImpl.class.getName());

    private final PersistenceUnitContext puContext;
    
    private final Function<List, Object> getSingle;
    
    private final TextSearch textSearch;
    
    private final ConvertToEntityFieldNumberType toNumber;
    
    public EntityReferenceImpl(PersistenceUnitContext puContext) {
        
        this.puContext = Objects.requireNonNull(puContext);
        
        this.getSingle = new GetSingle();
        
        this.textSearch = this.puContext.getTextSearch();
        
        this.toNumber = new ConvertToEntityFieldNumberType();
        
        // This must come last
        //
//        final Set<Class> puClasses = puContext.getMetaData().getEntityClasses();
//        for(Class puClass:puClasses) {
//            this.validate(puClass);
//        }
    }
    
    private void validate(Class entityClass) {
        boolean inputIsRefing = this.getReferenceClasses(entityClass) != null;
        boolean inputIsRef = this.getReferencingClasses(entityClass) != null;
        if(inputIsRefing && inputIsRef) {
            final Field [] fields = entityClass.getDeclaredFields();
            if(fields != null) {
                for(Field field:fields) {
                    if(field.getAnnotation(OneToMany.class) != null || 
                            field.getAnnotation(ManyToOne.class) != null ||
                            field.getAnnotation(ManyToMany.class) != null) {
                        throw new UnsupportedOperationException(
                                "This implementation currently only supports relationship "+
                                OneToOne.class.getName()+" in entity classes which are both parent/reference and child/referencing instances");
                    }
                }
            }
        }
        
        final Field [] fields = entityClass.getDeclaredFields();
        if(fields != null) {
            for(Field field:fields) {
                if(//field.getAnnotation(OneToOne.class) != null || 
                        field.getAnnotation(ManyToMany.class) != null) {
                    throw new UnsupportedOperationException("Annotations javax.peristence.ManyToMany not supported");
                }
            }
        }
    }
    
    @Override
    public <E> E find(Class<E> entityType, String name) {
        
        final Function<Query, Query> queryFormatter = (query) -> query.setHint(
                QueryHints.CACHE_USAGE, CacheUsage.CheckCacheThenDatabase);
        
        
        final List<E> resultList = this.textSearch.search(
                entityType, 
                name,
                queryFormatter);
        
        final E output = (E)this.getSingle.apply(resultList);       
        
        return output;
    }

    @Override
    public Optional getReferenceOptional(
            Class referencingType, Map<JoinColumn, Field> joinCols, String col, Object val) {
        
        if(val == null) {
            return Optional.empty();
        }

        if(joinCols == null) {
            return Optional.empty();
        }
        
        JoinColumn joinCol = null;
        Class refType = null;
        for(Map.Entry<JoinColumn, Field> entry:joinCols.entrySet()) {
            if(entry.getKey().name().equals(col)) {
                joinCol = entry.getKey();
                refType = entry.getValue().getType();
                break;
            }
        }
        
        final Level level = Level.FINER;

        if(LOG.isLoggable(level)) {
            LOG.log(level, "Entity type: {0}, column: {1}, reference type: {2}", 
                    new Object[]{referencingType, col, refType});  
        }
        
        if(refType == null) {
            return Optional.empty();
        }
        
        if(refType.equals(val.getClass())) {
            if(LOG.isLoggable(level)) {
                LOG.log(level, "No need to convert: {0} to type: {1}", new Object[]{val, refType});        
            }
            return Optional.empty();
        }
        
        Object ref = null;

        if(joinCol != null) {
            
            final String crossRefColumn = joinCol.referencedColumnName();

            if(LOG.isLoggable(level)) {
                LOG.log(level, "Reference type: {0}, Referencing type: {1}, reference column: {2}", 
                        new Object[]{refType, referencingType, crossRefColumn});        
            }

            if(crossRefColumn != null) {

                if(LOG.isLoggable(level)) {
                    LOG.log(level, "Reference type: {0}, Raw type: {1}, raw: {2}", 
                            new Object[]{refType, val.getClass(), val});        
                }

                try{
                    final Object number = this.toNumber.apply(refType, col, val);
                    if(number != null) {
                        ref = this.puContext.getDao().findAndClose(refType, number);
                    }
                }catch(RuntimeException e) {
                    final List refList = this.textSearch.search(refType, val.toString(), ComparisonOperator.EQUALS);
                    if(!refList.isEmpty()) {
                        ref = this.getSingle.apply(refList);
                    }else{
                        LOG.warning(MessageFormat.format(
                                "Reference cannot be null for: {0} of entity: {1}, column: {2}, value: {3}", 
                                refType.getName(), referencingType.getName(), col, val));
                        throw e;
                    }
                }

                if(LOG.isLoggable(level)) {
                    LOG.log(level, "Raw type: {0}, raw: {1}, Reference type: {2}, reference: {3}", 
                            new Object[]{val.getClass(), val, refType, ref});        
                }
            }
        }
        
        return Optional.ofNullable(ref);
    }

    @Override
    public String [] getReferencingColumns(Class referencing, String crossReferenceColumn) {
        
        Map<String, String> refs = this.getReferences(referencing);
        
        List<String> output = null;
        
        for(String key:refs.keySet()) {
            String val = refs.get(key);
            if(val.equals(crossReferenceColumn)) {
                if(output == null) {
                    output = new ArrayList<>();
                }
                output.add(key);
            }
        }
        
        return output == null ? new String[0] : output.toArray(new String[output.size()]);
    }

    @Override
    public Map<Class, String> getReferencing(Class referenceClass) {
        
        Field [] fields = referenceClass.getDeclaredFields();
        
        Map<Class, String> referencing = new HashMap<>();
        
        for(Field field:fields) {
            
            this.addReferencing(referenceClass, field, referencing);
        }

        return referencing.isEmpty() ? Collections.EMPTY_MAP : referencing;
    }
    
    private boolean addReferencing(Class ref, Field field, Map<Class, String> buff) {
    
        if(buff == null) {
            throw new NullPointerException();
        }
        
        Annotation ann;
        OneToMany one2many = field.getAnnotation(OneToMany.class);

        final Class referencingClass;
        
        final String mappedBy;
        
        if(one2many == null) {
            
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            
            if(one2one == null) {
                ann = null;
                mappedBy = null;
                
                referencingClass = null;
                
            }else{
                ann = one2one;
                mappedBy = one2one.mappedBy();
                
                referencingClass = field.getType();
            }
        }else{
            ann = one2many;
            mappedBy = one2many.mappedBy();

            // For field:         List<String> fieldname;
            // Type arguments is: class java.lang.String
            //
            Type [] typeArgs = this.getCollectionTypeArgs(field);

            referencingClass = typeArgs == null ? null : (Class)typeArgs[0];
        }
        
        if(referencingClass != null && mappedBy != null && !mappedBy.isEmpty()) {
            
//System.out.println(ref.getSimpleName()+"#"+field.getName()+" has "+ann+" relationship mapped by "+mappedBy+" on "+(referencingClass==null?null:referencingClass.getSimpleName()));            
        
            buff.put(referencingClass, mappedBy);
            
            return true;
            
        }else{
            
            return false;
        }
    }
    
    @Override
    public Class[] getReferencingClasses(Class reference) {
        
        Field [] fields = reference.getDeclaredFields();
        
        List<Class> refingClasses = null;
        
        for(Field field:fields) {
            
            Class refingClass = this.getReferencingClass(field);
            
//System.out.println("Field: "+field.getName()+", refing class: "+refingClass);            

            if(refingClass == null) {
                continue;
            }
            
            if(refingClasses == null) {
                refingClasses = new ArrayList<>();
            }
            
            refingClasses.add(refingClass);
        }
        
        return refingClasses == null ? new Class[0] : refingClasses.toArray(new Class[refingClasses.size()]);
    }

    @Override
    public String[] getReferencingColumns(Class referenceClass) {
        Field [] fields = referenceClass.getDeclaredFields();
        List<String> referencedColumns = null;
        for(Field field:fields) {
            String referencedColumn = this.getReferencingColumn(field);
            if(referencedColumn == null) {
                continue;
            }
            if(referencedColumns == null) {
                referencedColumns = new ArrayList<>();
            }
            if(!referencedColumns.contains(referencedColumn)) {
                referencedColumns.add(referencedColumn);
            }
        }
        return referencedColumns == null ? new String[0] : 
                referencedColumns.toArray(new String[referencedColumns.size()]);
    }
    
    @Override
    public Map<JoinColumn, Field> getJoinColumns(Class referencingClass) {
        
        Field [] fields = referencingClass.getDeclaredFields();
        
        Map<JoinColumn, Field> output = null;
        
        for(Field field:fields) {
            
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            
            if(joinColumn == null) {
                continue;
            }
            
            if(output == null) {
                output = new HashMap<>();
            }
            
            output.put(joinColumn, field);
        }
        
        return output == null ? Collections.EMPTY_MAP : output;
    }
    
    /**
     * @param referencingClass The referencing class for which all references
     * are to be returned.
     * @return A Map whose key/value pairs are mapped in the format:
     * ReferencingColumnName=ReferenceColumnName
     */
    @Override
    public Map<String, String> getReferences(Class referencingClass) {
        Field [] fields = referencingClass.getDeclaredFields();
        Map<String, String> references = null;
        for(Field field:fields) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                continue;
            }
            if(references == null) {
                references = new HashMap<>();
            }
            references.put(joinColumn.name(), joinColumn.referencedColumnName());
        }
        return references == null ? Collections.EMPTY_MAP: references;
    }

    @Override
    public Map<Class, String> getReferenceTypes(Class referencingClass) {
        
        final Map<Class, String> output;
        
        final Map<String, String> refsData = this.getReferences(referencingClass);
        
        if(refsData == null || refsData.isEmpty()) {
            
            output = Collections.EMPTY_MAP;
            
        }else{
            
            output = new LinkedHashMap();
            
            final Set<String> referencingCols = refsData.keySet();
            
            for(String referencingCol : referencingCols) {
                
                final Class referenceClass = this.getReferenceClass(referencingClass, referencingCol);
               
                output.put(referenceClass, refsData.get(referencingCol));
            }
        }
        
        return output;
    }
    
    @Override
    public String getReferenceColumn(Class reference, Class referencing) {
        Field [] fields = referencing.getDeclaredFields();
        String crossrefColumn = null;
        for(Field field:fields) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                continue;
            }
            if(field.getType() == reference) {
                crossrefColumn = joinColumn.referencedColumnName();
                break;
            }
        }
        return crossrefColumn;
        
    }

    @Override
    public Class getReferenceClass(Class refingClass, String refingColumn) {
        Field [] fields = refingClass.getDeclaredFields();
        Class refClass = null;
        for(Field field:fields) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                continue;
            }
            if(joinColumn.name().equals(refingColumn)) {
                refClass = field.getType();
                break;
            }
        }
        return refClass;
    }

    @Override
    public Class[] getReferenceClasses(Class refingClass) {
        Field [] fields = refingClass.getDeclaredFields();
        List<Class> refClasses = null;
        for(Field field:fields) {
            Class refClass = this.getReferenceClass(field);
            if(refClass == null) {
                continue;
            }
            if(refClasses == null) {
                refClasses = new ArrayList<>();
            }
            refClasses.add(refClass);
        }
        return refClasses == null ? new Class[0] : refClasses.toArray(new Class[refClasses.size()]);
    }
    @Override
    public TemporalType getTemporalType(Class entityClass, String column) {
        TemporalType output = null;
        Field [] fields = entityClass.getDeclaredFields();
        for(Field field:fields) {
            if(!field.getName().equals(column)) {
                continue;
            }
            Temporal temporalAnn = field.getAnnotation(Temporal.class);
            if(temporalAnn == null) {
                output = null;
            }else{
                output = temporalAnn.value();
            }
            break;
        }
        return output;
    }
    
    private Class getReferencingClass(Field field) {
        return this.getReferencingClass(field, null);
    }
    
    private Class getReferencingClass(Field field, String referenceColumn) {
    
        OneToMany one2many = field.getAnnotation(OneToMany.class);

        Class output;
        
        if(one2many == null) {
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            if(one2one == null) {
                output = null;
            }else{
                if(referenceColumn != null && 
                        !referenceColumn.equals(one2one.mappedBy())) {
                    output = null;
                }else{
                    output = field.getType();
                }
            }
        }else{
            if(referenceColumn != null && 
                    !referenceColumn.equals(one2many.mappedBy())) {
                output = null;
            }else{

                // For field:         List<String> fieldname;
                // Type arguments is: class java.lang.String
                //
                Type [] typeArgs = this.getCollectionTypeArgs(field);

                output = typeArgs == null ? null : (Class)typeArgs[0];
            }
        }

        return output;
    }
    
    /**
     * For field:         List<String> fieldname;
     * Type arguments is: class java.lang.String
     */
    private Type [] getCollectionTypeArgs(Field field) {
        
        Type [] output;
        
        Class type = field.getType();
        Type genType = field.getGenericType();

        if(this.isCollectionType(type) &&
            genType instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType)genType;
            
            output = pType.getActualTypeArguments();
            
        }else{
            
            output = null;
        }
        
        return output == null ? new Type[0] : output;
    }

    private String getReferencingColumn(Field field) {
        String output;
        OneToMany one2many = field.getAnnotation(OneToMany.class);
        if(one2many != null) {
            output = one2many.mappedBy();
        }else{
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            if(one2one != null) {
                output = one2one.mappedBy();
            }else{
                output = null;
            }
        }
        return output == null || output.isEmpty() ? null : output;
    }
    
    private Class getReferenceClass(Field field) {
        Class output;
        ManyToOne many2One = field.getAnnotation(ManyToOne.class);
        if(many2One != null) {
            output = field.getType();
        }else{
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            if(one2one != null) {
                output = field.getType();
            }else{
                output = null;
            }
        }
        return output;
    }

    private boolean isCollectionType(Class type) {
        return type == java.util.Collection.class ||
                type == java.util.Set.class ||
                type == java.util.List.class;
    }
}

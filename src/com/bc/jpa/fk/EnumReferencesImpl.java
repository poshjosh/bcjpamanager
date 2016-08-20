package com.bc.jpa.fk;

import com.bc.jpa.EntityController;
import com.bc.util.XLogger;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.jpa.PersistenceMetaData;
import com.bc.jpa.JpaContext;

/**
 * @(#)AbstractEnumReferences.java   15-May-2015 08:27:29
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author Josh
 * @param <E> 
 */
public class EnumReferencesImpl<E> implements Serializable, EnumReferences<E> {

    private static transient final Logger logger =
            Logger.getLogger(EnumReferencesImpl.class.getName());
    
    private static boolean validationAttempted;
    
    private final Class [] enumTypes;
    
    private transient final JpaContext jpaContext;
    
    public EnumReferencesImpl(JpaContext jpaContext, Class [] enumTypes) { 
        this.jpaContext = jpaContext;
        this.enumTypes = enumTypes;
        if(!validationAttempted) {
            validationAttempted = true;
            EnumReferencesImpl.this.validate();
            // Ensure these are well configured
            for(Class enumType:enumTypes) {
                EnumReferencesImpl.this.getMappings(enumType);
            }
        }
    }
    
    @Override
    public boolean isReference(String column) {
        
        Class type = this.getEnumType(column);
        
        return type != null;
    }
        
    @Override
    public EntityController<E, ?> getEntityController(String tablename) {
        PersistenceMetaData metaData = jpaContext.getMetaData();
        Class entityClass = metaData.findEntityClass(tablename);
        if(entityClass == null) {
            throw new IllegalArgumentException("For table: "+tablename+", no entity class was specified in persistence config: "+this.jpaContext.getPersistenceConfigURI());
        }
        
        return jpaContext.getEntityController(entityClass);
    }
    
    @Override
    public void validate() {
    
        if(enumTypes != null) {
            for(Class aClass:enumTypes) {

                validate(aClass);
            }
        }
Level level = enumTypes == null ? Level.WARNING : Level.FINE;        
if(XLogger.getInstance().isLoggable(level, this.getClass())) {
    XLogger.getInstance().log(level, "#validate(), enum types: {0}", 
    this.getClass(), enumTypes == null ? null : Arrays.toString(enumTypes));
}
    }

    @Override
    public E getEntity(String key, Object val) {
        
        Enum en = this.getEnum(key, val);
        
        if(en == null) {
            return null;
        }
        
        return this.getEntity(en);
    }

    @Override
    public Class getEnumType(String column) {

XLogger xlog = XLogger.getInstance();

        Class matchingType = null;
        
        PersistenceMetaData metaData = this.jpaContext.getMetaData();
        
        outer:
        for(Class enumType:enumTypes) {
            
            String tableName = this.getTableName(enumType);
            
            Class referenceType = metaData.findEntityClass(tableName);
            
            final String [] refingCols = metaData.getReferencingColumns(referenceType);

if(xlog.isLoggable(Level.FINER, this.getClass()))            
xlog.log(Level.FINER, "Enum type: {0}, referencing columns: {1}", this.getClass(), 
enumType, refingCols == null ? null : Arrays.toString(refingCols));

            if(refingCols == null || refingCols.length == 0) {
                continue;
            }
            
            synchronized(refingCols) {
                
                for(String refingCol:refingCols) {
                    
                    if(refingCol.equals(column)) {
                        
                        matchingType = enumType;
                        
                        break outer;
                    }
                }
            }
        }
xlog.log(Level.FINER, "Referencing column: {0}, enum type: {1}", this.getClass(), column, matchingType);
        return matchingType;
    }
    
    @Override
    public Enum getEnum(String key, Object val) {
        try{
            return this.getEnum(key, Integer.parseInt(val.toString().trim()));
        }catch(NumberFormatException ignored) { 
            return this.getNonIntEnum(key, val);
        }
    }
    
    private Enum getNonIntEnum(String key, Object val) {
        
        Class matchingType = this.getEnumType(key);
        
        Enum enumConstant = null;
        
        if(matchingType != null) {
            Object [] enumConstants = matchingType.getEnumConstants();
            for(Object ec:enumConstants) {
                Enum eval = (Enum)ec;
                if(this.matches(eval.name(), val)) {
                    enumConstant = eval;
                    break;
                }
            }
        }
        
        return enumConstant;
    }

    @Override
    public Enum getEnum(String key, int val) {

        Class matchingType = this.getEnumType(key);
        
//System.out.println(this.getClass().getName()+". "+key+"="+val+", enum type: "+matchingType);        
        Enum enumConstant = null;
        
        if(matchingType != null) {
            
            Object [] enumConstants = matchingType.getEnumConstants();
            
            Enum first = (Enum)enumConstants[0];
            
            String table = this.getTableName(first);
            String column = this.getIdColumnName(first);

            EntityController<E, ?> ec = this.getEntityController(table);
            
            List<E> found = ec.find();
            
            for(Object oval:enumConstants) {
                
                Enum eval = (Enum)oval;
                
                E entity = this.getEntity(ec, found, eval);

                Number idValue = (Number)ec.getValue(entity, column);

//System.out.println(this.getClass().getName()+". Enum: "+oval+", idValue: "+idValue);        
                
                if(idValue.intValue() == val) {
                    
//System.out.println(this.getClass().getName()+". Selected enum: "+eval);        
                    
                    enumConstant = eval;
                    
                    break;
                }
            }
        }
        
        return enumConstant;
    }

    /**
     * @param enumclassname The name of the enum class whose mappings will be returned
     * @return The Mappings of the enum class
     * @see #getMappings(java.lang.Enum[]) 
     */
    @Override
    public Map getMappings(String enumclassname) {
        
        Enum [] arr = this.getValues(enumclassname);
        
        return this.getMappings(arr);
    }
    
    @Override
    public Enum [] getValues(String enumclassname) {
        
        if(enumclassname == null) {
            throw new NullPointerException();
        }
        
        for(Class type:enumTypes) {
            if(type.getName().equals(enumclassname)) {
                return (Enum [])type.getEnumConstants();
            }
        }
        
        return null;
    }

    /**
     * @param enumclass The enum class whose mappings will be returned
     * @return The Mappings of the enum class
     * @see #getMappings(java.lang.Enum[]) 
     */
    @Override
    public Map getMappings(Class enumclass) {
        
        Enum [] arr = this.getValues(enumclass);
        
        return this.getMappings(arr);
    }
    
    @Override
    public Enum [] getValues(Class enumclass) {
        
        if(enumclass == null) {
            throw new NullPointerException();
        }
        
        for(Class type:enumTypes) {
            if(type == enumclass) {
                return (Enum [])type.getEnumConstants();
            }
        }
        
        return null;
    }
    
    /**
     * Returns the actual values of each row of a declared reference.
     * E.g of a declared: 
     * <code><pre>
     *     public static enum productstatus{new,classic,refurbished,used};
     * </pre></code>
     * @param arr The enum values of a declared reference. 
     * @return Map with each entry pair of format {'new' = 1, 'classic' = 2, 'refurbished' = 3, 'used' = 4}
     */
    @Override
    public Map getMappings(Enum [] arr) {
        
        if(arr.length == 0) {
            return Collections.emptyMap();
        }
        
        final String tablename = this.getTableName(arr[0]);
        final String column = this.getIdColumnName(arr[0]);
    
        Class declaringClass = arr[0].getDeclaringClass();
        
if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, 
"Enum type: {0}, table: {1}, ID column: {2}",
new Object[]{declaringClass.getName(), tablename, column});
        
        EntityController<E, ?> ec = this.getEntityController(tablename);
        
        List<E> found = ec.find();
        
if(found.size() != arr.length) {
    String msg = MessageFormat.format("Expected {0} entities but found {1} for enum type: {2}, table: {3}, ID column: {4}, enum values: {5}", 
            arr.length, found.size(), declaringClass.getName(), tablename, column, Arrays.toString(arr));
    throw new UnsupportedOperationException(msg);
}        
        
        EnumMap map = new EnumMap(declaringClass);
        
        for(int i=0; i<found.size(); i++) {

            E entity = found.get(i);
            
            Object value = ec.getValue(entity, column);
            
            map.put(arr[i], value);
        }

        return map;
    }

    @Override
    public E getEntity(Enum en) {
        
        if(en == null) {
            return null;
        }
        
        final String tablename = this.getTableName(en);
        final String dataColumnName = this.getDataColumnName(en);

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, 
"Enum type: {0}, constant: {1}, table: {2}, data column: {3}",
new Object[]{en.getDeclaringClass().getName(), en, tablename, dataColumnName});
        
        EntityController<E, ?> ec = this.getEntityController(tablename);
        
        E match = null;
        
        List<E> found = ec.find();
        
        for(E entity:found) {
            
            Object value = ec.getValue(entity, dataColumnName);
            
            if(this.matches(value, en.name())) {
            
                match = entity;
                
                break;
            }
        }

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, 
"Enum type: {0}, constant: {1}, Entity type: {2}, instance: {3}",
new Object[]{en.getDeclaringClass().getName(), en, match==null?null:match.getClass().getName(), match});

        if(match == null) {
            throw new UnsupportedOperationException(en.name()+" is not a valid reference for "+tablename);
        }

        return match;
    }

    @Override
    public List<E> getEntities(Enum en) {
        
        final String tablename = this.getTableName(en);

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, constant: {1}, table: {2}",
new Object[]{en.getDeclaringClass().getName(), en, tablename});
        
        EntityController<E, ?> ec = this.getEntityController(tablename);
        
        List<E> output = ec.find();
        
        if(output == null) {
            throw new UnsupportedOperationException(en+" is not valid reference for "+tablename);
        }

        return output;
    }
    
    @Override
    public Object getId(Enum en) {
        
        final String tablename = this.getTableName(en);
        final String column = this.getDataColumnName(en);

final Level level = Level.FINER;

if(logger.isLoggable(level))        
logger.log(level, 
"Enum type: {0}, constant: {1}, table: {2}, data column: {3}",
new Object[]{en.getDeclaringClass().getName(), en, tablename, column});
        
        EntityController<E, ?> ec = this.getEntityController(tablename);

        Object id = null;
        
        List<E> found = ec.find();
        
if(logger.isLoggable(level))        
logger.log(level, 
"Enum type: {0}, constant: {1}, found: {2}",
new Object[]{en.getDeclaringClass().getName(), en, found});
        
        for(E entity:found) {
            
            Object value = ec.getValue(entity, column);

if(logger.isLoggable(level))        
logger.log(level, 
"Enum type: {0}, constant: {1}, {2}={3}",
new Object[]{en.getDeclaringClass().getName(), en, column, value});
            
            if(this.matches(value, en.name())) {
            
                id = ec.getId(entity);
                
                break;
            }
        }
        
if(logger.isLoggable(level))        
logger.log(level, 
"Enum type: {0}, constant: {1}, Id type: {2}, id value: {3}",
new Object[]{en.getDeclaringClass().getName(), en, id==null?null:id.getClass().getName(), id});

        if(id == null) {
            throw new UnsupportedOperationException(en.name()+" is not a valid id reference for "+tablename+"."+column);
        }

        return id;
    }

    /**
     * Sample reference table:
     * <code><pre>
     * tablename: availability
     * --------------------------
     * | id_column | data_column |
     * ---------------------------
     * |    1      |   InStock   |
     * |    2      |   SoldOut   |
     * ---------------------------
     * </pre></code>
     * Sample Enum reference:
     * <code><pre>
 public class References extends EnumReferencesImpl{
     public static enum availability{InStock, SoldOut};
 }
 </pre></code>
     * @param enumType
     * @return 
     */
    @Override
    public Map<Enum, E> getEntities(Class<E> enumType) {

        String tableName = this.getTableName(enumType);
        
        EntityController<E, ?> ec = this.getEntityController(tableName);

        List<E> entities = ec.find();
        
        Map<Enum, E> output = new EnumMap(enumType);
        
        Enum [] enumValues = this.getValues(enumType);
        
if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, found: {1}", new Object[]{enumType, entities});

        for(E entity:entities) {
            
            for(Enum en:enumValues) {
                
                if(!output.containsKey(en)) {
                    
                    // Data column name e.g 'availability'
                    String dataCol = this.getDataColumnName(en);
                    
                    // Data column value e.g 'InStock'
                    Object dataColValue = ec.getValue(entity, dataCol);
                    
if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, constant: {1}, {2}={3}",
new Object[]{enumType, en, dataCol, dataColValue});
                    
                    if(this.matches(dataColValue, en.name())) {
                        
                        output.put(en, entity);
                        
                        break;
                    }
                }
            }
        }

        return output;
    }

    /**
     * Sample reference table:
     * <code><pre>
     * tablename: availability
     * --------------------------
     * | id_column | data_column |
     * ---------------------------
     * |    1      |   InStock   |
     * |    2      |   SoldOut   |
     * ---------------------------
     * </pre></code>
     * Sample Enum reference:
     * <code><pre>
 public class References extends EnumReferencesImpl{
     public static enum availability{InStock, SoldOut};
 }
 </pre></code>
     * @param enumType The type of the enum whose id mappings is to be returned.
     * @param keysNotValues If true, the values of the output Map will be
     * reference <b>id column values</b>, otherwise the values will be reference
     * <b>data column values</b>
     * @return Map with id mappings of the input enum type.
     */
    @Override
    public Map<Enum, Object> getIds(Class<E> enumType, boolean keysNotValues) {
        
        String tableName = this.getTableName(enumType);
        
        EntityController<E, ?> ec = this.getEntityController(tableName);

        List<E> entities = ec.find();
        
        Map<Enum, Object> output = new EnumMap(enumType);
        
        Enum [] enumValues = this.getValues(enumType);
        
if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, found: {1}", new Object[]{enumType, entities});

        for(E entity:entities) {
            
            for(Enum en:enumValues) {
                
                if(!output.containsKey(en)) {

                    String dataColName = this.getDataColumnName(en);

                    Object dataColValue = ec.getValue(entity, dataColName);

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, constant: {1}, {2}={3}",
new Object[]{enumType, en, dataColName, dataColValue});

                    if(this.matches(dataColValue, en.name())) {
                        
                        if(keysNotValues) {
                            String idColName = this.getIdColumnName(en);
                            Object idColValue = ec.getValue(entity, idColName);
                            output.put(en, idColValue);
                        }else{
                            output.put(en, dataColValue);
                        }

                        break;
                    }
                }
            }
        }
        
if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, id value mappings: {1}", new Object[]{enumType, output});

        return output;
    }

    private Enum getEnum(EntityController<E, ?> ec, Enum [] enumValues, E entity) {
        
        Enum output = null;
        
        for(Enum en:enumValues) {
            
            String dataColName = this.getDataColumnName(en);

            Object dataColValue = ec.getValue(entity, dataColName);

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, constant: {1}, {2}={3}",
new Object[]{en.getDeclaringClass().getName(), en, dataColName, dataColValue});

            if(this.matches(dataColValue, en.name())) {

                output = en;
                
                break;
            }
        }
        
        return output;
    }
    
    private E getEntity(EntityController<E, ?> ec, List<E> entities, Enum en) {
        
        E output = null;
        
        for(E entity:entities) {
            
            String dataColName = this.getDataColumnName(en);

            Object dataColValue = ec.getValue(entity, dataColName);

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Enum type: {0}, constant: {1}, {2}={3}",
new Object[]{en.getDeclaringClass().getName(), en, dataColName, dataColValue});

            if(this.matches(dataColValue, en.name())) {

                output = entity;
               
                break;
            }
        }
        
        return output;
    }
    
    protected boolean matches(Object a, Object b) {
        return format(a).equals(format(b));
    }
    
    private Object format(Object a) {
        // remove all non word characters
        return a.toString().toLowerCase().replaceAll("\\W", "");
    }
    
    @Override
    public void validate(Class aClass) {

        Object [] enumConstants = aClass.getEnumConstants();
    
XLogger.getInstance().log(Level.FINE, "Class: {0}, enum constants: {1}", this.getClass(), 
aClass.getName(), (enumConstants==null?null:Arrays.toString(enumConstants)));

        if(enumConstants == null || enumConstants.length == 0) {
            return;
        }
        
        for(Object obj:enumConstants) {
            
            Enum en = (Enum)obj;
            
            Object id = this.getId(en);
            
            if(id == null) {
                
                StringBuilder builder = new StringBuilder();
                builder.append("Constants for enum class ");
                builder.append(aClass.getName()).append(": ");
                builder.append(Arrays.toString(enumConstants));
                builder.append(" does not match the values in the database table with a matching name, table: ");
                builder.append(this.getTableName(en));
                
                throw new UnsupportedOperationException(builder.toString());
            }
        }
    }

    @Override
    public String getTableName(Class enumclass) {
        return enumclass.getSimpleName();
    }
    
    @Override
    public String getTableName(Enum en) {
        return en.getDeclaringClass().getSimpleName();
    }
    
    @Override
    public String getIdColumnName(Enum en) {
        return this.getDataColumnName(en) + "id";
    }
    
    @Override
    public String getDataColumnName(Enum en) {
        return this.getTableName(en);
    }
    
    @Override
    public final Class [] getEnumTypes() {
        return this.enumTypes;
    }

    public final JpaContext getJpaContext() {
        return this.jpaContext;
    }
}

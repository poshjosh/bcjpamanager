package com.bc.jpa.controller;

import com.bc.jpa.EntityReference;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.fk.EnumReferences;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.sql.SQLDateTimePatterns;
import com.bc.sql.SQLUtils;
import com.bc.util.XLogger;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.logging.Level;
import javax.persistence.JoinColumn;


/**
 * @(#)DatabaseValue.java   18-Apr-2015 04:49:47
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * This map implementation formats data that is added via the 
 * {@link #put(java.lang.Object, java.lang.Object) put(Object,Object)} method so that
 * <b>what is introduced via the {@link #put(java.lang.Object, java.lang.Object) put(Object,Object)} 
 * method may not be actually added to the map</b>. If an entry pair was not added
 * the {@link #put(java.lang.Object, java.lang.Object) put(Object,Object)} method returns 
 * {@link #NO_OP NO_OP}. So if you want to check if entry pairs where actually 
 * added, check if the returned value is equal to {@link #NO_OP NO_OP}. Also, it
 * does not have the Map argument constructor which takes in a Map i.e 
 * {@link java.util.HashMap#HashMap(java.util.Map)}. This is to ensure the 
 * additional logic of our <tt>put</tt> method here is not bypassed by the
 * putForCreate method in the superclass <tt>HashMap</tt> constructor.
 * @see ${@link java.util.HashMap}
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @deprecated Rather use {@link com.bc.jpa.context.DatabaseFormatImpl}
 */
@Deprecated
public class DatabaseFormatDeprecated implements DatabaseFormat {

    private final SQLDateTimePatterns dateTimePatterns;
    
    private final JpaContext jpaContext;
    
    private final EntityReference entityReference;

    public DatabaseFormatDeprecated(
            JpaContext jpaContext, SQLDateTimePatterns dateTimePatterns) { 
        this.jpaContext = jpaContext;
        this.dateTimePatterns = dateTimePatterns;
        this.entityReference = jpaContext.getEntityReference();
    }

    public final SQLDateTimePatterns getDateTimePatterns() {
        return dateTimePatterns;
    }

    @Override
    public Object toDatabaseFormat(Class entityType, Object key, Object value, Object outputIfNone) {
        
        Object output;

        if(!this.isDatabaseColumn(entityType, key)) {
            
            output = outputIfNone;
            
        }else if(value == null) {

//@ todo This may not be suitable for creating queries, in which case 'NULL' may be suitable
            output = null; 
            
        }else if(value.equals("")) {
            
            output = value;
            
        }else{

            Object sqlVal = this.getSQLValue(entityType, key, value);
            
            output = sqlVal != null ? sqlVal : value;
            
            // Replace the value with the reference if possible
            //
            Object ref = this.getReference(entityType, key, output);

            if(ref != null) {

                output = ref;
            }
        }
        
XLogger.getInstance().log(Level.FINER, "Column: {0}, value: {1}, database value: {2}", 
this.getClass(), key, value, output);
        
        return output;
    }
    
    public Object getSQLValue(Class entityType, Object key, Object value) {
        Object sqlObj = null;
        try{
long mb4 = com.bc.util.Util.availableMemory();
long tb4 = System.currentTimeMillis();
            Field field = entityType.getDeclaredField(key.toString());
            if(field != null) {
                Class fieldType = field.getType();
                int [] sqlTypes = SQLUtils.getTypes(fieldType);
                if(sqlTypes != null && sqlTypes.length > 0) {
                    
XLogger.getInstance().log(Level.FINER, "Key: {0}, value type: {1}, value: {2}",
this.getClass(), key, value==null?null:value.getClass().getName(), value);
                    
                    int sqlType = sqlTypes[0];
                    boolean stringType = sqlType == Types.CHAR || sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR || sqlType == Types.LONGNVARCHAR;
                    if(!stringType) {
                        
                        boolean dateOrTimeType = sqlType == Types.DATE || sqlType == Types.TIME || sqlType == Types.TIMESTAMP;
                        
                        if(dateOrTimeType) {
                            sqlObj = SQLUtils.toSQLType(dateTimePatterns, sqlType, value);
                        }
                        
XLogger.getInstance().log(Level.FINER, "Spent:: memory: {0}, time: {1}, converting: {2} to SQL value: {3}", 
this.getClass(), mb4-com.bc.util.Util.usedMemory(mb4), System.currentTimeMillis()-tb4, value, sqlObj);
                    }
                }else{
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    if(joinColumn != null) {
XLogger.getInstance().log(Level.FINER, "Entity class: {0}, column: {1} join column: {2}", 
this.getClass(), entityType, key, joinColumn);
                        try{
                            sqlObj = Integer.parseInt(value.toString());
XLogger.getInstance().log(Level.FINER, "Spent:: memory: {0}, time: {1}, converting: {2} to SQL value: {3}", 
this.getClass(), mb4-com.bc.util.Util.usedMemory(mb4), System.currentTimeMillis()-tb4, value, sqlObj);
                        }catch(NumberFormatException ignored) { }
                    }
                }
            }
        }catch(NoSuchFieldException | RuntimeException e) {
            XLogger.getInstance().log(Level.WARNING, "Error converting '"+key+"' to sql type", this.getClass(), e);
        }
        return sqlObj;
    }
    
    @Override
    public boolean isDatabaseColumn(Class entityType, Object key) {
        boolean output = false;
        final String [] cols = this.jpaContext.getMetaData().getColumnNames(entityType);
        synchronized(cols) {
            String sval = key.toString();
            for(String col:cols) {
                if(col.equals(sval)) {
                    output = true;
                    break;
                }
            }
        }
XLogger.getInstance().log(Level.FINER, "Column: {0}, is database column: {1}", 
this.getClass(), key, output);
        return output;
    }
    
    public Object getReference(Class entityType, Object key, Object value) {
XLogger.getInstance().log(Level.FINER, "Column: {0}, value: {1}", this.getClass(), key, value);
        String col = key.toString();
        
        EnumReferences enumReferences = this.jpaContext.getEnumReferences();
        
        // May be an enum reference
        Object reference = enumReferences == null ? null : enumReferences.getEntity(col, value);
        
        if(reference == null) {
            // Or an ordinary reference
            reference = entityReference.getReference(
                    this.jpaContext.getEntityManager(entityType), 
                    entityType, col, value);
        }
XLogger.getInstance().log(Level.FINER, "Column: {0}, value: {1}, entity: {2}", this.getClass(), key, value, reference);
        return reference;
    }
}

package com.bc.jpa;

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.sql.SQLDateTimePatterns;
import com.bc.sql.SQLUtils;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.JoinColumn;

/**
 * @(#)DatabaseValue.java   18-Apr-2015 04:49:47
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class DatabaseFormatImpl implements DatabaseFormat {

    private transient static final Logger LOG = Logger.getLogger(DatabaseFormatImpl.class.getName());

    private final SQLDateTimePatterns dateTimePatterns;
    
    private final PersistenceUnitContext persistenceUnitContext;
    
    private final EntityReference entityReference;
    
    private final ConvertToEntityFieldNumberType toNumber;
    
    public DatabaseFormatImpl(
            PersistenceUnitContext persistenceUnitContext, SQLDateTimePatterns dateTimePatterns) { 
        this.persistenceUnitContext = Objects.requireNonNull(persistenceUnitContext);
        this.dateTimePatterns = Objects.requireNonNull(dateTimePatterns);
        this.entityReference = persistenceUnitContext.getEntityReference();
        this.toNumber = new ConvertToEntityFieldNumberType();
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
            Optional optionalRef = this.getReference(entityType, key, output);

            if(optionalRef.isPresent()) {

                output = optionalRef.get();
            }
        }
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "For: {0}#{1}, converted: {2} to {3}", 
                    new Object[]{entityType==null?null:entityType.getName(), key, value, output});
        }
        return output;
    }
    
    public Object getSQLValue(Class entityType, Object key, Object value) {
        Object sqlObj = null;
        try{

            final long mb4 = com.bc.util.Util.availableMemory();
            final long tb4 = System.currentTimeMillis();

            final Field field = entityType.getDeclaredField(key.toString());
            if(field != null) {
                final Class fieldType = field.getType();
                int [] sqlTypes = SQLUtils.getTypes(fieldType);
                if(sqlTypes != null && sqlTypes.length > 0) {

                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Key: {0}, value type: {1}, value: {2}", 
                                new Object[]{key, value==null?null:value.getClass().getName(), value});
                    }
                    
                    final int sqlType = sqlTypes[0];
    
                    if(SQLUtils.isDateOrTimeType(sqlType)) {
                        sqlObj = SQLUtils.toSQLType(dateTimePatterns, sqlType, value);
                    }

                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Spent:: memory: {0}, time: {1}, converting: {2} to SQL value: {3}", 
                                new Object[]{mb4-com.bc.util.Util.usedMemory(mb4), System.currentTimeMillis()-tb4, value, sqlObj});
                    }
                }
                
                final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                if(joinColumn != null) {
                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Entity class: {0}, column: {1} join column: {2}", 
                                new Object[]{entityType, key, joinColumn});
                    }
                    
                    final Object number = this.toNumber.apply(entityType, key.toString(), value);
                    if(number != null) {
                        sqlObj = number;
                        if(LOG.isLoggable(Level.FINER)) {
                            LOG.log(Level.FINER, "Spent:: memory: {0}, time: {1}, converting: {2} to SQL value: {3} of type: {4}", 
                                    new Object[]{
                                        mb4-com.bc.util.Util.usedMemory(mb4), 
                                        System.currentTimeMillis()-tb4, 
                                        value, 
                                        sqlObj, 
                                        sqlObj==null?null:sqlObj.getClass()
                                    }
                            );
                        }
                    }
                }
            }
        }catch(NoSuchFieldException | RuntimeException e) {
            LOG.log(Level.WARNING, "Error converting '"+key+"' to sql type", e);
        }
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "For: {0}#{1}, converted: {2} to {3} of type: {4}", 
                    new Object[]{entityType==null?null:entityType.getName(), key, value, sqlObj, sqlObj==null?null:sqlObj.getClass()});
        }
        return sqlObj;
    }
    
    @Override
    public boolean isDatabaseColumn(Class entityType, Object key) {
        boolean output = false;
        final String [] cols = this.persistenceUnitContext.getMetaData().getColumnNames(entityType);
        synchronized(cols) {
            String sval = key.toString();
            for(String col:cols) {
                if(col.equals(sval)) {
                    output = true;
                    break;
                }
            }
        }
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Column: {0}, is database column: {1}", 
                    new Object[]{key, output});
        }
        return output;
    }
    
    public Optional getReference(Class entityType, Object key, Object value) {
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Column: {0}, value: {1}", 
                    new Object[]{key, value});
        }

        final String col = key.toString();
        
        final Optional optionalRef = entityReference.getReferenceOptional(entityType, col, value);

        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Column: {0}, value: {1}, entity: {2}", 
                    new Object[]{key, value, optionalRef.orElse(null)});
        }
        return optionalRef;
    }
}

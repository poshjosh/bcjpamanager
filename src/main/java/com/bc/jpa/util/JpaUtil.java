package com.bc.jpa.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

/**
 * @(#)DatabaseObjects.java   14-May-2014 23:10:25
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
public class JpaUtil {

    private transient static final Logger LOG = Logger.getLogger(JpaUtil.class.getName());

    public static Method getMethod(boolean setter, 
    Class referenceEntityClass, Class referencingEntityClass) {
        
        String prefix = setter ? "set" : "get";
        
        // Formats: getXXXList, setXXXList, getXXXCollection, 
        // setXXXCollection, getXXXSet, setXXXSet
        //
        Class [] collectionTypes = {List.class, Collection.class, Set.class};
        
        Method output = null;
        
        StringBuilder buff = new StringBuilder();
        
        for(Class collectionType:collectionTypes) {
            
            Class [] parameterTypes = setter ? new Class[]{collectionType} : (Class[])null;
            
            try{

                // E.g setSiteurlList, getSiteurlList, setSiteurlSet etc
                //
                buff.setLength(0);
                buff.append(prefix);
                buff.append(referencingEntityClass.getSimpleName());
                buff.append(collectionType.getSimpleName());
                
                if(LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, "Reference Entity Class: {0}, Method name: {1}, ParameterTypes: {2}",
                    new Object[]{referenceEntityClass, buff, parameterTypes});
                }
                
                output = referenceEntityClass.getMethod(buff.toString(), parameterTypes);

                if(output != null) {
                    break;
                }
                
            }catch(NoSuchMethodException ignored) {
                
            }
        }
        
//        if(output == null) {
//            StringBuilder builder = new StringBuilder();
//            builder.append("Failed to find ").append(prefix).append("ter method for ");
//            builder.append(referencingEntityClass.getSimpleName()).append(" in entity ");
//            builder.append(referenceEntityClass.getSimpleName());
//            throw new IllegalArgumentException(builder.toString());
//        }
        if(LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Setter: {0}, reference: {1}, referencing: {2}, method: {3}", 
                new Object[]{setter, referenceEntityClass, referencingEntityClass, output});
        }
        
        return output;
    }

    public static String getMethodName(boolean setter, Field [] fields, String columnName) {
        String methodName = null;
        for(Field field:fields) {
            Column column = field.getAnnotation(Column.class);
            String name;
            if(column == null) {
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                if(joinColumn == null) {
                    continue;
                }else{
                    name = joinColumn.name();
                }
            }else{
                name = column.name();
            }
            if(name.equals(columnName)) {
                StringBuilder builder = new StringBuilder();
                builder.append(setter?"set":"get");
                String fieldName = field.getName();
                builder.append(Character.toTitleCase(fieldName.charAt(0)));
                builder.append(fieldName.substring(1));
                methodName = builder.toString();
                break;
            } 
        }
        return methodName;
    }
}

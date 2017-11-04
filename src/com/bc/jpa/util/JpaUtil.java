package com.bc.jpa.util;

import com.bc.util.XLogger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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
                
XLogger.getInstance().log(Level.FINER, "Reference Entity Class: {0}, Method name: {1}, ParameterTypes: {2}",
JpaUtil.class, referenceEntityClass, buff, parameterTypes);
                
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
XLogger.getInstance().log(Level.FINER, "Setter: {0}, reference: {1}, referencing: {2}, method: {3}", 
JpaUtil.class, setter, referenceEntityClass, referencingEntityClass, output);
        
        return output;
    }

    public static Object getValue(Class aClass, 
            Object entity, Field [] fields, String columnName) {
        
        if(fields == null) {
            fields = aClass.getDeclaredFields();
        }
        
        String methodName = getMethodName(false, fields, columnName);

        if(methodName == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
        
        Method method = null;
        try{
            
            method = aClass.getMethod(methodName);
            
            if(method != null) {
                return method.invoke(entity);
            }
            
        }catch(Exception e) {
            
            StringBuilder builder = new StringBuilder("Error getting entity value.");
            builder.append(" Entity: ").append(entity);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(columnName);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
        
        if(method == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
        
        return null;
    }

    public static void setValue(Class aClass, 
            Object entity, Field [] fields, 
            String columnName, Object columnValue) {
        
        if(fields == null) {
            fields = aClass.getDeclaredFields();
        }
        
        String methodName = getMethodName(true, fields, columnName);
        
        if(methodName == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
        
        Method method = null;
        try{
            
            method = aClass.getMethod(methodName, columnValue.getClass());
            
            if(method != null) {
                method.invoke(entity, columnValue);
            }
            
        }catch(Exception e) {
            
            StringBuilder builder = new StringBuilder("Error setting entity value.");
            builder.append(" Entity: ").append(entity);
            builder.append(", Method: ").append(method==null?null:method.getName());
            builder.append(", Column: ").append(columnName);
            builder.append(", Value: ").append(columnValue);
            builder.append(", Value type: ").append(columnValue==null?null:columnValue.getClass());
            builder.append(", Expected type: ").append(method==null?null:method.getParameterTypes()[0]);

            throw new UnsupportedOperationException(builder.toString(), e);
        }
        
        if(method == null) {
            throw new IllegalArgumentException("Could not find matching method for: "+columnName+" in class: "+aClass);
        }
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
    
    /**
     * Methods of the {@link java.lang.Object} class are not considered
     * @param setter boolean, if true only setter methods are considered
     * @param methods The array of methods within which to search for a 
     * method matching the specified column name.
     * @param columnName The column name for which a method with a matching
     * name is to be returned.
     * @return A method whose name matches the input columnName or null if none was found
     */
    public static Method getMethod(boolean setter, Method [] methods, String columnName) {
        Method method = null;
        final String prefix = setter ? "set" : "get";
        // remove all _
        //
        String normalizedColName = removeAll(columnName, '_').toString();
        for(Method m:methods) {
            if(m.getDeclaringClass() == Object.class) {
                continue;
            }
            String methodName = m.getName();
            if(!methodName.startsWith(prefix)) {
                continue;
            }
            // remove get or set
            //
            String normalizedMethodName = methodName.substring(prefix.length());
            if(normalizedColName.equalsIgnoreCase(normalizedMethodName)) {
                method = m;
                break;
            }
        }
        return method;
    }
    
    private static StringBuilder removeAll(String input, char toRemove) {
        StringBuilder builder = new StringBuilder(input.length());
        for(int i=0; i<input.length(); i++) {
            char ch = input.charAt(i);
            if(ch == toRemove) {
                continue;
            }
            builder.append(ch);
        }
        return builder;
    }
}

package com.bc.jpa;

import com.bc.util.XLogger;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.Persistence;

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

    private static transient final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();

    public static EntityManagerFactory getEntityManagerFactory(
            PersistenceMetaData metaData, 
            String databaseName) {

        String persistenceUnit = metaData.getPersistenceUnitName(databaseName);
        
        EntityManagerFactory factory = entityManagerFactories.get(persistenceUnit);
        
        Properties props = null;
        
        URL url = null;
        
        if(factory == null) {
            
            final String persistenceURI = metaData.getURI();

            try{

XLogger.getInstance().log(Level.INFO, 
        "======================== Creating EntityManagerFactory =========================\n"+
        "Database: {0}, persistenceUnit: {1}, URI: {2}",
        JpaUtil.class, databaseName, persistenceUnit, persistenceURI);

XLogger.getInstance().log(Level.INFO, "Properties: {0}", JpaUtil.class, props);

                props = metaData.getProperties(persistenceUnit);

                url = new URI(persistenceURI).toURL();

            }catch(IOException e) {

                XLogger.getInstance().log(Level.WARNING, "Exception loading properties for unit: "+persistenceUnit, JpaUtil.class, e);
                
            }catch(URISyntaxException e) {
                
                XLogger.getInstance().log(Level.WARNING, "Exception creating URI from: "+persistenceURI, JpaUtil.class, e);
            }
            
            final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            
            try{
                
                if(url != null) {
                    
                    final ClassLoader alternativeClassLoader = new AlternativePersistenceClassLoader(url);

                    Thread.currentThread().setContextClassLoader(alternativeClassLoader);
                }
                
                if(props != null && !props.isEmpty()) {
                    factory = Persistence.createEntityManagerFactory(
                            persistenceUnit, props);
                }else{
                    factory = Persistence.createEntityManagerFactory(
                            persistenceUnit);
                }
            }finally{
                
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }

            entityManagerFactories.put(persistenceUnit, factory);
        }

        return factory;
    }

    public static Object getReference(
            EntityManager em, Class referencingType, Map<JoinColumn, Field> joinCols, String col, Object val) {
        
        if(val == null) {
            return null;
        }

        if(joinCols == null) {
            return null;
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
        
XLogger.getInstance().log(Level.FINER, "Entity type: {0}, column: {1}, reference type: {2}", 
JpaUtil.class, referencingType, col, refType);        
        
        if(refType == null) {
            return null;
        }
        
        if(refType.equals(val.getClass())) {
XLogger.getInstance().log(Level.FINER, "No need to convert: {0} to type: {1}", JpaUtil.class, val, refType);        
            return null;
        }
        
        String crossRefColumn = joinCol.referencedColumnName();

XLogger.getInstance().log(Level.FINER, "Reference type: {0}, Referencing type: {1}, reference column: {2}", 
        JpaUtil.class, refType, referencingType, crossRefColumn);        
        
        Object ref;
        
        if(crossRefColumn != null) {

XLogger.getInstance().log(Level.FINER, "Reference type: {0}, Raw type: {1}, raw: {2}", JpaUtil.class, refType, val.getClass(), val);        
            
            ref = em.getReference(refType, val);
            
XLogger.getInstance().log(Level.FINER, "Raw type: {0}, raw: {1}, Reference type: {2}, reference: {3}", 
JpaUtil.class, val.getClass(), val, refType, ref);        
            
            if(ref == null) {
                throw new NullPointerException("Reference cannot be null for: "+
                        refType.getName()+" of entity: "+referencingType.getName()+
                        ", column: "+col+", value: "+val);
            }

        }else{

            ref = null;
        }
        
        return ref;
    }
    
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

    /**
     * Methods of the {@link java.lang.Object} class are not considered
     * @param setter boolean, if true only setter methods are considered
     * @param method Method. The method for which a column with a name 
     * matching the method name will be returned/
     * @return A column whose name matches the input Method name, or null if
     * no such column could be inferred.
     */
    public static void appendColumnName(boolean setter, Method method, StringBuilder buff) {

        final String prefix = setter ? "set" : "get";
        
        String methodName = method.getName();
        
        if(method.getDeclaringClass() == Object.class || prefix != null && 
                !methodName.startsWith(prefix)) {
        
            return;
            
        }else{
        
            final int prefixLen = prefix == null ? 0 : prefix.length();
            final int len = methodName.length();

            boolean doneFirst = false;
            for(int i=0; i<len; i++) {

                if(i < prefixLen) {
                    continue;
                }

                char ch = methodName.charAt(i);

                if(!doneFirst) {
                    doneFirst = true;
                    ch = Character.toLowerCase(ch);
                }

                buff.append(ch);
            }
        }
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

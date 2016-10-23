package com.bc.jpa.util;

import com.bc.jpa.JpaUtil;
import com.bc.util.XLogger;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.Entity;

/**
 * @author Josh
 */
public class EntityMapBuilderImpl implements EntityMapBuilder {
    
    private final boolean nullsAllowed;
    
    private final int maxDepth;
    
    private final int maxCollectionSize;
    
    private final Set<Class> builtTypes;
    
    private final Set<Class> typesToAccept;
    
    private final Set<Class> typesToIgnore;
    
    private final Map<Class, Method []> entityToMethods;
    
    public EntityMapBuilderImpl() {
        
        this(null, null);
    }

    public EntityMapBuilderImpl(Set<Class> typesToAccept, Set<Class> typesToIgnore) {
        
        this(false, 3, 100, typesToAccept, typesToIgnore);
    }

    public EntityMapBuilderImpl(
            boolean nullsAllowed, int maxDepth, int maxCollectionSize) {
        
        this(nullsAllowed, maxDepth, maxCollectionSize, null, null);
    }
    
    public EntityMapBuilderImpl(
            boolean nullsAllowed, int maxDepth, int maxCollectionSize, 
            Set<Class> typesToAccept, Set<Class> typesToIgnore) {
        this.nullsAllowed = nullsAllowed;
        this.maxDepth = maxDepth;
        this.maxCollectionSize = maxCollectionSize;
        this.builtTypes = new HashSet();
        this.typesToAccept = typesToAccept == null || typesToAccept.isEmpty() ? 
                Collections.EMPTY_SET : Collections.unmodifiableSet(typesToAccept);
        this.typesToIgnore = typesToIgnore == null || typesToIgnore.isEmpty() ? 
                Collections.EMPTY_SET : Collections.unmodifiableSet(typesToIgnore);
        this.entityToMethods = new HashMap<>();
    }
    
    @Override
    public Map build(Object entity) {
        
        return this.build(entity.getClass(), entity);
    }

    @Override
    public Map build(Class entityType, Object entity) {
        
        return this.build(entityType, entity, Transformer.NO_OPERATION);
    }
    
    @Override
    public <E> Map build(Class<E> entityType, E entity, Transformer<E> transformer) {
        
        Map appendTo = new LinkedHashMap();
        
        this.build(entityType, entity, appendTo, transformer);
        
        return appendTo;
    }

    @Override
    public void build(Object entity, Map appendTo) {
     
        this.build(entity.getClass(), entity, appendTo);
    }

    @Override
    public void build(Class entityType, Object entity, Map appendTo) {
        
        build(entityType, entity, appendTo, Transformer.NO_OPERATION);
    }
    
    @Override
    public <E> void build(Class<E> entityType, E entity, Map appendTo, Transformer<E> transformer) {
        
        this.builtTypes.clear();
        
        this.build(entityType, entity, appendTo, transformer, 0);
    }

    private <E> void build(Class<E> entityType, E entity, Map appendTo, Transformer<E> transformer, int depth) {
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINER;
Class cls = this.getClass();

logger.log(level, "Building Map for entity: {0}", cls, entity);

        Objects.requireNonNull(entity);

        builtTypes.add(entityType);
        
        Method [] methods = this.getMethods(entityType);
        
        StringBuilder buff = new StringBuilder();
        
        for(Method method:methods) {
            
            buff.setLength(0);
            JpaUtil.appendColumnName(false, method, buff);
            String key = buff.length() == 0 ? null : buff.toString();
            
            boolean foundGetterMethod = key != null;
            
            if(!foundGetterMethod) {
logger.log(level, "Not a getter method: {0}", cls, method.getName());                
                continue;
            }
            
            if(!this.mayRecurse(logger, level, method, depth)) {
                continue;
            }
            
            Object value;
            if(this.maxCollectionSize < 1 && 
                    this.isSubclassOf(method.getReturnType(), Collection.class)) {
//System.out.println("-------------------------------------------- key: "+key+", return type: "+method.getReturnType());                
                value = null;
                
            }else{
                
                try{

                    value = method.invoke(entity);

                }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

                    this.logMethodError(logger, cls, e, entity, method, key);

                    continue;
                }
            }

            if(transformer != null) {
                final String oldKey = key;
                key = transformer.transformKey(entity, key);
                value = transformer.transformValue(entity, oldKey, key, value);
            }

            if(!nullsAllowed && value == null) {
                continue;
            }
            
            if(maxDepth > 0) {

                if(value instanceof Collection) {

                    Collection collection = (Collection)value;

                    if(collection.size() > maxCollectionSize) {

                        collection = this.truncate(collection, maxCollectionSize);

                        value = collection;
                    }

                    if(collection.isEmpty()) {
                        
logger.log(level, "{0} is an empty collection", cls, key);

                        value = null;
                        
                    }else{
                    
                        final Class collectionValueType = this.getTypeOfGenericReturnTypesArgument(method);

logger.log(level, "{0} has generic type {1}", cls, key, collectionValueType);                      
                        
                        List list = new ArrayList();

logger.log(level, "Recursing collection: {0} and {1} values", cls, key, collection.size());

                        Iterator iter = collection.iterator();

                        ++depth;

                        while(iter.hasNext()) {

                            Object subValue = iter.next();

                            Map subMap = new LinkedHashMap();

                            build(collectionValueType, subValue, subMap, Transformer.NO_OPERATION, depth);

                            list.add(subMap);
                        }

                        --depth;

                        value = list;
                    }
                }else if(value != null){

                    final Class valueType = value.getClass();

                    Annotation entityAnnotation = valueType.getAnnotation(Entity.class);

logger.log(level, "Key: {0}, value type: {1}, entity annotation: {2}", 
    cls, key, valueType, entityAnnotation); 

                    if(entityAnnotation != null) {
                        
logger.log(level, "Recursing value with key: {0}", cls, key);  

                        ++depth;

                        Map valueMap = new LinkedHashMap();
                        
                        build(valueType, value, valueMap, Transformer.NO_OPERATION, depth);

                        --depth;

                        value = valueMap;
                    }
                }
            }

            if(nullsAllowed || value != null) {

                this.append(entityType, entity, key, value, appendTo);
            }
        }
if(logger.isLoggable(level, cls))        
logger.log(level, "Extracted: {0}", cls, appendTo.keySet());     
    }
    
    protected <E> void append(Class<E> entityType, E entity, String key, Object value, Map appendTo) {
        
        appendTo.put(key, value);
    }
    
    Collection truncate(Collection collection, int maxSize) {
        
        Collection output;
        
        if(maxSize < 1) {
            
            output = Collections.EMPTY_SET;
            
        }else{
            
            output = new ArrayList(maxSize);
            
            int i = 0;

            for(Object object:collection) {

                output.add(object);

                if(++i >= maxSize) {
                    break;
                }
            }
        }    
        
        return output;
    }
    
    boolean mayRecurse(XLogger log, Level level, Method method, int depth) {
        
        final Class returnType = method.getReturnType();
        
        final boolean collectionReturnType = this.isSubclassOf(returnType, Collection.class);
        
        final Class type;
        if(!collectionReturnType) {
            type = returnType;
        }else{
            type = this.getTypeOfGenericReturnTypesArgument(method);
        }
        
        return this.mayRecurse(log, level, type, depth);
    }
    
    Class getTypeOfGenericReturnTypesArgument(Method method) {
        final Type genericReturnType = method.getGenericReturnType();
        ParameterizedType parameterizedType = (ParameterizedType)genericReturnType;
        Type [] typeArg = parameterizedType.getActualTypeArguments();
        return (Class)typeArg[0];
    }
    
    boolean mayRecurse(XLogger log, Level level, Class type, int depth) {
        
if(log.isLoggable(level, this.getClass()))
log.log(level, "{0}, depth < maxDepth: {1}, !typesToIgnore.contains(type): {2}, !builtTypes.contains(type): {3}", 
this.getClass(), type.getName(), (depth<maxDepth), !typesToIgnore.contains(type), !builtTypes.contains(type));

        return depth < maxDepth  && !builtTypes.contains(type) 
                && (typesToAccept.contains(type) || (typesToAccept.isEmpty() && !typesToIgnore.contains(type)));
    }
    
    boolean isSubclassOf(Class tgt, Class cls) {
        try{
            tgt.asSubclass(cls);
            return true;
        }catch(ClassCastException ignored) {
            return false;
        }
    }
    
    Method [] getMethods(Class entityType) {
        Method [] output = entityToMethods.get(entityType);
        if(output == null) {
            output = entityType.getMethods();
            entityToMethods.put(entityType, output);
        }
        return output;
    }
    
    void logMethodError(XLogger logger, Class cls, Exception e, Object entity, Method method, String key) {
        StringBuilder msg = new StringBuilder();
        msg.append("Entity: ").append(entity);
        msg.append(", Method: ").append(method.getName());
        msg.append(", key: ").append(key);
        logger.log(Level.WARNING, msg.toString(), cls, e);
    }

    public final boolean isNullsAllowed() {
        return nullsAllowed;
    }

    public final int getMaxDepth() {
        return maxDepth;
    }

    public final int getMaxCollectionSize() {
        return maxCollectionSize;
    }

    public final Set<Class> getBuiltTypes() {
        return builtTypes;
    }

    public final Set<Class> getTypesToAccept() {
        return typesToAccept;
    }

    public final Set<Class> getTypesToIgnore() {
        return typesToIgnore;
    }
}

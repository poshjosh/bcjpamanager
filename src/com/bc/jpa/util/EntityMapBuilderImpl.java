package com.bc.jpa.util;

import com.bc.jpa.JpaUtil;
import com.bc.util.XLogger;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.Entity;

/**
 * @author Josh
 */
public class EntityMapBuilderImpl implements EntityMapBuilder {
    
    private final boolean nullsAllowed;
    
    private final int maxDepth;
    
    private final int maxCollectionSize;
    
    private final Collection<Class> builtTypes;
    
    private final Collection<Class> typesToAccept;
    
    private final Collection<Class> typesToIgnore;
    
    private int depth = 0;

    public EntityMapBuilderImpl() {
        
        this(null, null);
    }

    public EntityMapBuilderImpl(Collection<Class> typesToAccept, Collection<Class> typesToIgnore) {
        
        this(false, 3, 100, typesToAccept, typesToIgnore);
    }

    public EntityMapBuilderImpl(
            boolean nullsAllowed, int maxDepth, int maxCollectionSize, 
            Collection<Class> typesToAccept, Collection<Class> typesToIgnore) {
        this.nullsAllowed = nullsAllowed;
        this.maxDepth = maxDepth;
        this.maxCollectionSize = maxCollectionSize;
        this.builtTypes = new ArrayList();
        this.typesToAccept = typesToAccept == null ? Collections.EMPTY_LIST : typesToAccept;
        this.typesToIgnore = typesToIgnore == null ? Collections.EMPTY_LIST : typesToIgnore;
    }

    @Override
    public void reset() {
        this.builtTypes.clear();
        this.depth = 0;
    }

    @Override
    public Map build(Object entity) {
        
        return this.build(entity, entity.getClass());
    }
    
    @Override
    public Map build(Object entity, Class entityType) {
        
        Map appendTo = new LinkedHashMap();
        
        this.build(entity, entityType, appendTo);
        
        return appendTo;
    }

    @Override
    public void build(Object entity, Map appendTo) {
     
        this.build(entity, entity.getClass(), appendTo);
    }

    @Override
    public void build(Object entity, Class entityType, Map appendTo) {
        
        build(true, entity, entityType, appendTo);
    }

    private void build(boolean log, Object entity, Class entityType, Map appendTo) {
        
        if(log) {
            
            builtTypes.add(entityType);
        }
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINER;
Class cls = this.getClass();

logger.log(level, "toMap. Entity: {0}", cls, entity);
        
        if(entity==null) {
            throw new NullPointerException();
        }

        Method [] methods = entityType.getMethods();
        
        StringBuilder buff = new StringBuilder();
        
        for(Method method:methods) {
            
            buff.setLength(0);
            
            JpaUtil.appendColumnName(false, method, buff);
            String columnName = buff.length() == 0 ? null : buff.toString();
            if(columnName == null) {
                continue;
            }
            
            try{
                
                Object columnValue = method.invoke(entity);
                if(!nullsAllowed && columnValue == null) {
                    continue;
                }
                
                if(columnValue != null && maxDepth > 0) {
                    
                    if(columnValue instanceof Collection) {
                        
                        Collection collection = (Collection)columnValue;
                        
                        if(collection.size() > maxCollectionSize) {
                            
                            collection = this.truncate(collection, maxCollectionSize);
                            
                            columnValue = collection;
                        }
                        
                        final Class subType = this.getEntityType(collection);
                        
if(logger.isLoggable(level, cls))                        
logger.log(level, "{0} has generic type {1}", cls, columnName, subType==null?null:subType.getName());

                        if(subType != null) {
                            
                            if(this.mayRecurse(logger, subType)) {
                                
                                List list = new ArrayList();

logger.log(level, "Recursing list with columnName: {0} and {1} values", 
        cls, columnName, collection.size());

                                Iterator iter = collection.iterator();
                                
                                ++depth;
                                
                                while(iter.hasNext()) {
                                    
                                    Object subValue = iter.next();

                                    Map subMap = new LinkedHashMap();
                                    
                                    build(!iter.hasNext(), subValue, subType, subMap);

                                    list.add(subMap);
                                }
                                
                                --depth;

                                columnValue = list;
                                
                            }else{
                                
                                columnValue = Collections.EMPTY_LIST;
                            }
                        }
                    }else{
                        
                        Annotation entityAnnotation = this.getEntityAnnotation(columnValue);

                        if(entityAnnotation != null) {

logger.log(level, "Recursing value with columnName: {0}", cls, columnName);  

                            Class columnType = columnValue.getClass();
                            
                            if(this.mayRecurse(logger, columnType)) {

                                ++depth;
                                
                                Map columnMap = build(columnValue, columnType);
                                
                                --depth;
                                
                                columnValue = columnMap;
                                
                            }else{
                                
                                columnValue = null;
                            }
                        }
                    }
                }
                
                if(columnValue != null) {
                
                    appendTo.put(columnName, columnValue);
                }
                
            }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Entity: ").append(entity);
                msg.append(", Method: ").append(method.getName());
                msg.append(", Column: ").append(columnName);
                logger.log(Level.WARNING, msg.toString(), cls, e);
            }
        }
if(logger.isLoggable(Level.FINER, cls))        
logger.log(Level.FINER, "Extracted: {0}", cls, appendTo.keySet());     
    }
    
    private Collection truncate(Collection collection, int maxSize) {
        
        Collection output = new ArrayList(maxSize);
        
        int i = 0;
        
        for(Object object:collection) {
         
            output.add(object);
            
            if(++i >= maxSize) {
                break;
            }
        }
        
        return output;
    }
    
    private Class getEntityType(Collection collection) {
        
        Iterator iter = collection.iterator();

        Class output = null;
        
        if(iter.hasNext()) {
            Object subValue = iter.next();
            Annotation entityAnnotation = this.getEntityAnnotation(subValue);
            output = entityAnnotation == null ? null : subValue.getClass();
        }

        return output;
    }
    
    private boolean mayRecurse(XLogger log, Class type) {
        
if(log.isLoggable(Level.FINER, this.getClass()))
log.log(Level.FINER, "{0}, depth < maxDepth: {1}, !typesToIgnore.contains(type): {2}, !builtTypes.contains(type): {3}", 
this.getClass(), type.getName(), (depth<maxDepth), !typesToIgnore.contains(type), !builtTypes.contains(type));

        return depth < maxDepth  && !builtTypes.contains(type) 
                && (typesToAccept.contains(type) || (typesToAccept.isEmpty() && !typesToIgnore.contains(type)));
    }
    
    private Annotation getEntityAnnotation(Object object) {
        
        Annotation entityAnn;
        
        if(object == null) {
            
            entityAnn = null;
            
        }else{
            
            entityAnn = object.getClass().getAnnotation(Entity.class);
        }
        
        return entityAnn;
    }
}

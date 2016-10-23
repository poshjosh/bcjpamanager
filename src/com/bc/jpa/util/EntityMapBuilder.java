package com.bc.jpa.util;

import java.util.Map;

/**
 * @author Josh
 */
public interface EntityMapBuilder {

    interface Transformer<E> {
        
        Transformer NO_OPERATION = new Transformer() {
            @Override
            public String transformKey(Object entity, String key) {
                return key;
            }
            @Override
            public Object transformValue(Object entity, String oldKey, String newKey, Object value) {
                return value;
            }
        };
        
        String transformKey(E entity, String key);
        
        Object transformValue(E entity, String oldKey, String newKey, Object value);
    }
    
    Map build(Object entity);

    <E> Map build(Class<E> entityType, E entity);
    
    <E> Map build(Class<E> entityType, E entity, Transformer<E> transformer);

    void build(Object entity, Map appendTo);

    <E> void build(Class<E> entityType, E entity, Map appendTo);
    
    <E> void build(Class<E> entityType, E entity, Map appendTo, Transformer<E> tranformer);
}

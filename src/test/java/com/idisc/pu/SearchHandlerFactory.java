package com.idisc.pu;

import com.bc.jpa.search.SearchResults;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Josh
 */
public interface SearchHandlerFactory extends AutoCloseable {

    Iterator<Class> getEntityTypes();
    
    Iterator<String> getKeys(Class entityType);
    
    <E> SearchResults<E> get(String sessionId, Class<E> entityType, boolean createNew);
    
    <E> SearchResults<E> get(String sessionId, Class<E> entityType, Map<String, Object> parameters, boolean createNew);

    int removeAll(boolean close);
    
    int removeAll(String sessionId, boolean close);
    
    <E> SearchResults<E> remove(String sessionId, Class<E> entityType, boolean close);
}

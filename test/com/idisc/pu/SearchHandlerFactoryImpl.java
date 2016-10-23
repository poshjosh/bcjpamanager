package com.idisc.pu;

import com.bc.jpa.search.BaseSearchResults;
import com.bc.jpa.search.SearchResults;
import com.idisc.pu.entities.Comment;
import com.idisc.pu.entities.Feed;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.util.XLogger;
import java.util.logging.Level;

/**
 * @author Josh
 */
public class SearchHandlerFactoryImpl implements SearchHandlerFactory {
    
    // we use the same cacheSize for both type cache and session cache
    private final int cacheSize;
    
    private final Lock lock;
    
    private final JpaContext cf;
    
    private final Map<Class, Map<String, SearchResults>> typeCache;
    
    public SearchHandlerFactoryImpl(JpaContext cf) {
        this.cacheSize = 8;
        this.lock = new ReentrantLock();
        this.cf = cf;
        this.typeCache = Collections.synchronizedMap(new HashMapNoNulls(cacheSize, 0.75f));
    }
    
    @Override
    public void close() {
        this.removeAll(true);
    }
    
    @Override
    public int removeAll(boolean close) {
        int removed = 0;
        try{
            lock.lock();
            Set<Class> entityTypes = typeCache.keySet();
            for(Class entityType:entityTypes) {
                Map<String, SearchResults> sessionCache = typeCache.get(entityType);
                if(sessionCache != null) {
                    Set<String> sessionIds = sessionCache.keySet();
                    for(String sessionId:sessionIds) {
                        Object o = this.remove(sessionId, entityType, close);
                        if(o != null) {
                            ++removed;
                        }
                    }
                }
            }
        }finally{
            lock.unlock();
        }
        return removed;
    }
    
    @Override
    public int removeAll(String sessionId, boolean close) {
        int removed = 0;
        try{
            lock.lock();
            Set<Class> entityTypes = typeCache.keySet();
            for(Class entityType:entityTypes) {
                Object o = this.remove(sessionId, entityType, close);
                if(o != null) {
                    ++removed;
                }
            }
        }finally{
            lock.unlock();
        }
        return removed;
    }
    
    @Override
    public <E> SearchResults<E> remove(String sessionId, Class<E> entityType, boolean close) {
        try{
            lock.lock();
            Map<String, SearchResults> sessionCache = typeCache.get(entityType);
            SearchResults<E> output = sessionCache == null ? null : sessionCache.remove(sessionId);
            if(output != null) {
                this.close(output);
            }
            return output;
        }finally{
            lock.unlock();
        }
    }
    
    @Override
    public <E> SearchResults<E> get(String sessionId, Class<E> entityType, boolean createNew) {
        
        return this.get(sessionId, entityType, Collections.EMPTY_MAP, createNew);
    }

    @Override
    public <E> SearchResults<E> get(String sessionId, Class<E> entityType, 
            Map<String, Object> parameters, boolean createNew) {

        SearchResults output = null;

        try{
            
            lock.lock();

            Map<String, SearchResults> sessionCache = typeCache.get(entityType);

            if(sessionCache != null) {

                output = sessionCache.get(sessionId);
            }

            if(createNew) {

                if(output != null) {

                    this.close(output);
                }

                output = this.create(entityType, parameters);

                if(sessionCache == null) {

                    sessionCache = new HashMapNoNulls(cacheSize, 0.75f);

                    typeCache.put(entityType, sessionCache);
                }

                sessionCache.put(sessionId, output);
            }
        }finally{
        
            lock.unlock();
        }
        
        return output;
    }
    
    @Override
    public Iterator<Class> getEntityTypes() {
        try{
            lock.lock();
            Set<Class> typeKeys = typeCache.keySet();
            return typeKeys.iterator();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public Iterator<String> getKeys(Class entityType) {
        try{
            lock.lock();
            Map<String, SearchResults> sessionCache = typeCache.get(entityType);
            return sessionCache.keySet().iterator();
        }finally{
            lock.unlock();
        }
    }

    protected <E> SearchResults<E> create(Class<E> entityType, Map<String, Object> parameters) {
        
        String query = parameters == null ? null : (String)parameters.get("query");
        Integer limit = parameters == null || parameters.get("limit") == null ? 20 : (Integer)parameters.get("limit");
        Date after = parameters == null ? null : (Date)parameters.get("after");
        
        BuilderForSelect queryBuilder = this.createQueryBuilder(entityType, query, after, 0, limit);
        
        return new BaseSearchResults(queryBuilder, limit, true);
    } 
    
    private void close(SearchResults sr) {
        if(sr instanceof AutoCloseable) {
            try{
                ((AutoCloseable)sr).close();
            }catch(Exception e) {
                XLogger.getInstance().log(Level.WARNING, "Exception closing "+sr.getClass().getName(), this.getClass(), e);
            }
        }
    }

    protected <E> BuilderForSelect<E> createQueryBuilder(Class<E> resultType, String query, Date after, int offset, int limit) {
        BuilderForSelect queryBuilder;
        final String dateColumn;
        if(resultType == Feed.class) {
            queryBuilder = new FeedDao(cf, offset, limit, query);
            dateColumn = "feeddate";
        }else if(resultType == Comment.class) {
            queryBuilder = new CommentDao(cf, offset, limit, query);
            dateColumn = "datecreated";
        }else{
            queryBuilder = new SearchDao(cf, resultType, offset, limit, query);
            dateColumn = null;
        }
        
        if(after != null && dateColumn != null) {
            queryBuilder.where(resultType, dateColumn, BuilderForSelect.GREATER_THAN, after);
        }
        return queryBuilder;
    }

    private class HashMapNoNulls<K, V> extends HashMap<K, V> {

        public HashMapNoNulls(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        public HashMapNoNulls(int initialCapacity) {
            super(initialCapacity);
        }

        public HashMapNoNulls() { }

        @Override
        public V put(K key, V value) {
            if(key == null || value == null) {
                throw new NullPointerException();
            }
            return super.put(key, value); //To change body of generated methods, choose Tools | Templates.
        }
    }
}

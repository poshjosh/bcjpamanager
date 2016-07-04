package com.bc.jpa.search;

import java.io.Serializable;
import java.util.List;
import javax.persistence.TypedQuery;
import com.bc.jpa.paging.PaginatedList;
import com.bc.jpa.paging.SimplePagingList;

/**
 * @(#)AbstractSearchResults.java   11-Apr-2015 08:28:48
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * <p>
 * Example class that wraps the execution of a {@link javax.persistence.TypedQuery} 
 * calculating the current size and then paging the results using the provided 
 * page size.
 * </p>
 * <b>Notes:</b>
 * <ul>
 * <li>The query should contain an ORDER BY</li> 
 * <li>The following methods must not have been called on the query:<br/>
 * {@link javax.persistence.TypedQuery#setFirstResult(int)}<br/> 
 * {@link javax.persistence.TypedQuery#setMaxResults(int)}
 * </li>
 * <li>The usage of this may produce incorrect results if the matching data set 
 * changes on the database while the results are being paged.</li>
 * </ul>
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class QuerySearchResults<T> 
        extends SimplePagingList<T>
        implements Serializable, SearchResultList<T> {

    private int pageNumber;
    
    private final boolean useCache;
    
    public QuerySearchResults(TypedQuery typedQuery) { 
        super(typedQuery);
        this.useCache = true;
    }

    public QuerySearchResults(TypedQuery typedQuery, int batchSize) {
        super(typedQuery, batchSize);
        this.useCache = true;
    }
    
    public QuerySearchResults(TypedQuery typedQuery, int batchSize, boolean useCache) {
        super(typedQuery, batchSize);
        this.useCache = useCache;
    }

    @Override
    public void reset() {
        super.reset(); 
        this.pageNumber = 0;
    }
    
    /**
     * Releases valuable resources. Overriding methods must call super.close()
     */
    @Override
    public void close() {
        super.close();
        this.pageNumber = 0;
    }

    @Override
    public PaginatedList<T> getAllResults() {
        return this;
    }
    
    @Override
    public int getSize() {
        return this.size();
    }

    @Override
    public final boolean isUseCache() {
        return this.useCache;
    }

    @Override
    public List<T> getBatch() {
        return getCurrentBatch();
    }
    
    @Override
    public List<T> getCurrentBatch() {
        return getBatch(pageNumber);
    }

    public List<T> getPage() {
        return getCurrentPage();
    }
    
    public List<T> getCurrentPage() {
        return getBatch(pageNumber);
    }
    
    public List<T> getPage(int pageNumber) {
        return this.getBatch(pageNumber);
    }
    
    @Override
    public int getBatchIndex() {
        return pageNumber;
    }

    @Override
    public void setBatchIndex(int pageNumber) {
        if(pageNumber > this.getBatchCount()-1) {
            throw new ArrayIndexOutOfBoundsException(pageNumber+" > "+this.getBatchCount());
        }
        this.pageNumber = pageNumber;
    }
}

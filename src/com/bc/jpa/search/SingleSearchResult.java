package com.bc.jpa.search;

import com.bc.jpa.paging.ListPager;
import com.bc.jpa.paging.PaginatedList;
import java.util.Collections;
import java.util.List;

/**
 * @(#)SingleSearchResult.java   28-May-2015 23:43:19
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * Contains <tt>zero</tt> or <tt>one</tt> record depending on if the
 * Entity referenced by the input id exists.
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class SingleSearchResult<T> implements SearchResults<T> {

    private final List<T> results;
    
    public SingleSearchResult(T result) {
        this.results = Collections.singletonList(result);
    }
    
    @Override
    public void reset() { }
    
    @Override
    public void close() { }

    @Override
    public PaginatedList<T> getAllResults() {
        return new ListPager(this.results, this.results.size());
    }

    @Override
    public List getCurrentBatch() {
        return this.getBatch();
    }

    @Override
    public List getBatch() {
        return this.getBatch(this.getBatchIndex());
    }

    @Override
    public int getBatchIndex() {
        return 0;
    }

    @Override
    public int getSize() {
        return results == null ? 0 : results.size();
    }

    @Override
    public void setBatchIndex(int pageNumber) {
        if(pageNumber != 0) {
            throw new IndexOutOfBoundsException("Only one page of elements available. Index out of bounds. Page: "+pageNumber);
        }
    }

    @Override
    public List getBatch(int pageNum) {
        if(pageNum != 0) {
            throw new IndexOutOfBoundsException("Only one element in collection. Index out of bounds. Index: "+pageNum);
        }else{
            return results;
        }
    }

    @Override
    public int getBatchCount() {
        return results == null ? 0 : 1;
    }

    @Override
    public int getBatchSize() {
        return 20;
    }
}

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
    public T get(int index) {
        return results.get(index);
    }

    @Override
    public PaginatedList<T> getAllResults() {
        return new ListPager(this.results, this.results.size());
    }

    @Override
    public List getCurrentPage() {
        return this.getPage();
    }

    @Override
    public List getPage() {
        return this.getPage(this.getPageNumber());
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getSize() {
        return results == null ? 0 : results.size();
    }

    @Override
    public void setPageNumber(int pageNumber) {
        if(pageNumber != 0) {
            throw new IndexOutOfBoundsException("Only one page of elements available. Index out of bounds. Page: "+pageNumber);
        }
    }

    @Override
    public List getPage(int pageNum) {
        if(pageNum != 0) {
            throw new IndexOutOfBoundsException("Only one element in collection. Index out of bounds. Index: "+pageNum);
        }else{
            return results;
        }
    }

    @Override
    public int getPageCount() {
        return results == null ? 0 : 1;
    }

    @Override
    public int getPageSize() {
        return 20;
    }
}

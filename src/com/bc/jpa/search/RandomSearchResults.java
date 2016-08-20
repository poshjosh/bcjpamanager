package com.bc.jpa.search;

import com.bc.jpa.paging.ListPager;
import com.bc.util.Util;
import com.bc.util.XLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.bc.jpa.paging.PaginatedList;


/**
 * @(#)RandomSearchResults.java   31-May-2015 00:26:59
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class RandomSearchResults<T> implements SearchResults<T> {

    private final List<T> results;
    
    public RandomSearchResults(List<T> results) {
        this.results = results;
XLogger.getInstance().log(Level.FINE, "Random Search Results: {0}", this.getClass(), results.size());
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
        return new ListPager(results, results.size());
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
    public void setPageNumber(int pageNumber) { }

    @Override
    public List getPage(int pageNum) {
        final int totalSize = this.getSize();
        if(totalSize == 0 || totalSize <= this.getPageSize()) {
            return results;
        }else{
            final int batchSize = totalSize < this.getPageSize() ? totalSize : this.getPageSize();
            List<T> batch = new ArrayList<>(batchSize);
            while(batch.size() < batchSize) {
                int index = Util.randomInt(totalSize);
                T t = results.get(index);
                boolean added = batch.contains(t);
                if(!added) {
                    batch.add(t);
                }
            }
XLogger.getInstance().log(Level.FINER, "{0} random results:: {1}", this.getClass(), batch.size(), batch);
            return batch;
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

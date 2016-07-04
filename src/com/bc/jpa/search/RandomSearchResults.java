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
    public PaginatedList<T> getAllResults() {
        return new ListPager(results, results.size());
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
    public void setBatchIndex(int pageNumber) { }

    @Override
    public List getBatch(int pageNum) {
        final int totalSize = this.getSize();
        if(totalSize == 0 || totalSize <= this.getBatchSize()) {
            return results;
        }else{
            final int batchSize = totalSize < this.getBatchSize() ? totalSize : this.getBatchSize();
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
    public int getBatchCount() {
        return results == null ? 0 : 1;
    }

    @Override
    public int getBatchSize() {
        return 20;
    }
}

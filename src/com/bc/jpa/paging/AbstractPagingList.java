package com.bc.jpa.paging;

import com.bc.util.BatchUtils;
import com.bc.util.XLogger;
import java.util.AbstractList;
import java.util.List;
import java.util.logging.Level;

/**
 * @(#)AbstractPagingList.java   27-Jun-2014 13:36:14
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * Example class that wraps the execution of a {@link javax.persistence.TypedQuery} 
 * and paging the results using the provided {@linkplain #pageSize}
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class AbstractPagingList<T> 
        extends AbstractList<T> implements PaginatedList<T> {

    private int previousPageNum = -1;

    /**
     * Cached page results. This approach holds all retrieved pages. if the
     * total volume of results is large this caching should be changed to only
     * hold a limited set of pages or rely on garbage collection to clear out
     * unused pages.
     */
    private List<T>[] batches;

    public AbstractPagingList() {  }

    protected abstract List<T> loadBatch(int pageNum);
    
    protected abstract List<T> [] initPagesBuffer();
    
    @Override
    public void reset() {
        previousPageNum = -1;
        batches = null;
    }
    
    @Override
    public T get(int index) {
        int batchSize = this.getBatchSize();
        int batchIndex = BatchUtils.getBatch(index, batchSize);
        int indexInBatch = BatchUtils.getIndexInBatch(index, batchSize);
XLogger.getInstance().log(Level.FINER, "Retreiving index {0}, batctCount: {1}, batchIndex: {2}, indexInBatch: {3}",
        this.getClass(), index, this.getBatchCount(), batchIndex, indexInBatch);
        return getBatch(batchIndex).get(indexInBatch);
    }

    @Override
    public int getBatchSize() {
        return 20;
    }
    
    @Override
    public int getBatchCount() {
        return getBatches().length;
    }

    public List<T>[] getBatches() {
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        if(batches == null) {
            
            if(this.getBatchSize() < 1) {
                throw new UnsupportedOperationException("Page size "+this.getBatchSize()+" < 1");
            }
            batches = this.initPagesBuffer();
XLogger.getInstance().log(Level.FINE, 
"Initialized batches array. Total Size: {0}, batch size: {1}, number of batches: {2}", 
this.getClass(), this.size(), this.getBatchSize(), this.getBatchCount());        
        }
        return batches;
    }
    
    @Override
    public List<T> getBatch(int pageNum) {
        
        if(pageNum < 0 || pageNum >= this.getBatchCount()) {
            throw new IndexOutOfBoundsException("Page number: "+pageNum+", number of pages: "+this.getBatchCount());
        }
        
        List<T> page = getBatches()[pageNum];

        if (page == null) {
            
            page = this.loadBatch(pageNum);
XLogger.getInstance().log(Level.FINE, "Loaded from database. Batch {0}, size of batch: {1}",
        this.getClass(), pageNum, page == null ? null : page.size());
            
            getBatches()[pageNum] = page;
            
        }else{
            
XLogger.getInstance().log(Level.FINER, "Loaded from cache. Batch {0}, size of batch: {1}",
        this.getClass(), pageNum, page.size());
        }
        
        if(!this.isUseCache() && previousPageNum > -1 && previousPageNum < this.getBatchCount()) {
            
XLogger.getInstance().log(Level.FINER, "Clearing batch at {0}", this.getClass(), previousPageNum);

            this.getBatches()[previousPageNum] = null;
            
            previousPageNum = pageNum;
        }

        return page;
    }
    
    public final int computeNumberOfPages(int size, int pageSize) {
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        return BatchUtils.getBatch(size, pageSize) + (BatchUtils.getIndexInBatch(size, pageSize) > 0 ? 1 : 0);
    }

    public boolean isUseCache() {
        return true;
    }

    public int getPreviousPage() {
        return previousPageNum;
    }
}

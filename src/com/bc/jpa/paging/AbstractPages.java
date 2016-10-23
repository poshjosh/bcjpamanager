package com.bc.jpa.paging;

import com.bc.util.BatchUtils;
import com.bc.util.XLogger;
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
public abstract class AbstractPages<T> implements Paginated<T> {

    /**
     * The size determined by {@link #calculateSize()}
     */
    private int $_size = -1;
    
    private final int batchSize;
    
    private int previousPageNum = -1;

    /**
     * Cached page results. This approach holds all retrieved pages. if the
     * total volume of results is large this caching should be changed to only
     * hold a limited set of pages or rely on garbage collection to clear out
     * unused pages.
     */
    private List<T>[] batches;

    public AbstractPages(int pageSize) {  
        if(pageSize < 1) {
            throw new IllegalArgumentException("For page size, expected value > 0. found: "+pageSize);
        }
        this.batchSize = pageSize;
    }
    
    protected abstract List<T> loadBatch(int pageNum);
    
    protected abstract int calculateSize();
    
    @Override
    public void reset() {
        this.$_size = -1;
        this.previousPageNum = -1;
        this.batches = null;
    }
    
    @Override
    public int getSize() {
        if(this.$_size == -1) {
            this.$_size = calculateSize();
        }
        return this.$_size;
    }
    
    @Override
    public T get(int index) {
        int batchIndex = BatchUtils.getBatch(index, batchSize);
        int indexInBatch = BatchUtils.getIndexInBatch(index, batchSize);
XLogger.getInstance().log(Level.FINER, "Retreiving index {0}, batctCount: {1}, batchIndex: {2}, indexInBatch: {3}",
        this.getClass(), index, this.getPageCount(), batchIndex, indexInBatch);
        return getPage(batchIndex).get(indexInBatch);
    }

    protected List<T> [] initPagesBuffer() {
        
        if(this.getPageSize() < 1) {
            throw new UnsupportedOperationException("Page size "+this.getPageSize()+"< 1");
        }
        
        final int numPages = this.computeNumberOfPages();
        
XLogger.getInstance().log(Level.FINE, "Size: {0}, number of pages: {1}", this.getClass(), this.getSize(), numPages);
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        List<T> [] pages = new List[numPages];

        return pages;
    }
    
    @Override
    public final int getPageSize() {
        return this.batchSize;
    }
    
    @Override
    public int getPageCount() {
        return getBatches().length;
    }

    public List<T>[] getBatches() {
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        if(batches == null) {
            
            if(this.getPageSize() < 1) {
                throw new UnsupportedOperationException("Page size "+this.getPageSize()+" < 1");
            }
            
            batches = this.initPagesBuffer();
XLogger.getInstance().log(Level.FINE, 
"Initialized batches array. Total Size: {0}, batch size: {1}, number of batches: {2}", 
this.getClass(), this.getSize(), this.getPageSize(), this.getPageCount());        
        }
        return batches;
    }
    
    @Override
    public List<T> getPage(int pageNum) {
        
        if(pageNum < 0 || pageNum >= this.getPageCount()) {
            throw new IndexOutOfBoundsException("Page number: "+pageNum+", number of pages: "+this.getPageCount());
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
        
        if(!this.isUseCache() && previousPageNum > -1 && previousPageNum < this.getPageCount()) {
            
XLogger.getInstance().log(Level.FINER, "Clearing batch at {0}", this.getClass(), previousPageNum);

            this.getBatches()[previousPageNum] = null;
            
            previousPageNum = pageNum;
        }

        return page;
    }
    
    protected final int computeNumberOfPages() {
        
        final int size = this.getSize();
        final int pageSize = this.getPageSize();
        
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

package com.bc.jpa.search;

import java.util.List;
import com.bc.jpa.paging.Paginated;
import com.bc.jpa.paging.PaginatedList;


/**
 * @(#)SearchResults.java   14-Apr-2015 21:47:30
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
public interface SearchResults<T> extends Paginated<T>, java.lang.AutoCloseable {
    
    SearchResults EMPTY_INSTANCE = new EmptySearchResults();
    
    @Override
    void close();
    
    PaginatedList<T> getAllResults();
    
    List<T> getCurrentBatch();

    List<T> getBatch();

    int getBatchIndex();

    int getSize();

    void setBatchIndex(int pageNumber);
    
    class EmptySearchResults<T> implements SearchResults<T> {
        
        private EmptySearchResults(){}

        @Override
        public void reset() { }

        @Override
        public void close() { }

        @Override
        public PaginatedList<T> getAllResults() {
            return PaginatedList.EMPTY_PAGINATED_LIST;
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
            return PaginatedList.EMPTY_PAGINATED_LIST.size();
        }

        @Override
        public void setBatchIndex(int pageNumber) {
            throw new IndexOutOfBoundsException("0 elements available. Index out of bounds. Page: "+pageNumber);
        }

        @Override
        public List getBatch(int pageNum) {
            return PaginatedList.EMPTY_PAGINATED_LIST.getBatch(pageNum);
        }

        @Override
        public int getBatchCount() {
            return PaginatedList.EMPTY_PAGINATED_LIST.getBatchCount();
        }

        @Override
        public int getBatchSize() {
            return PaginatedList.EMPTY_PAGINATED_LIST.getBatchSize();
        }
    }
}

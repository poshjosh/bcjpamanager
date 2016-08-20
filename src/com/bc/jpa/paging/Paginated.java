package com.bc.jpa.paging;

import java.util.List;

/**
 * @(#)PagingListIx.java   11-May-2014 03:17:52
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
public interface Paginated<T> {

    Paginated EMPTY_PAGES = new EmptyPages();
    
    /**
     * This sets the current results to null and eventually causes a fresh set 
     * of results to be re-loaded from the database
     */
    void reset();
    
    int getSize();
    
    T get(int index);
    
    List<T> getPage(int pageNum);

    int getPageCount();

    int getPageSize();
    
    class EmptyPages<T> implements Paginated<T> {
        private EmptyPages() { }
        @Override
        public void reset() { }
        @Override
        public T get(int index) {
            throw new IndexOutOfBoundsException("index: "+index);
        }
        @Override
        public int getSize() {
            return 0;
        }
        @Override
        public List<T> getPage(int pageNum) {
            throw new IndexOutOfBoundsException("page: "+pageNum);
        }
        @Override
        public int getPageCount() {
            return 0;
        }
        @Override
        public int getPageSize() {
            return 20;
        }
    }
}

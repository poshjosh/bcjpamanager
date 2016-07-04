package com.bc.jpa.paging;

import java.util.AbstractList;
import java.util.List;


/**
 * @(#)PagingList.java   14-Apr-2015 21:41:22
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
public interface PaginatedList<T> extends List<T>, Paginated<T> {

    PaginatedList EMPTY_PAGINATED_LIST = new EmptyPaginatedList();
            
    class EmptyPaginatedList extends AbstractList implements PaginatedList {
        private EmptyPaginatedList(){}
        @Override
        public Object get(int index) {
            throw new IndexOutOfBoundsException("index <= size");
        }
        @Override
        public int size() {
            return 0;
        }
        @Override
        public void reset() { }
        @Override
        public List getBatch(int pageNum) {
            return Paginated.EMPTY_PAGES.getBatch(pageNum);
        }
        @Override
        public int getBatchCount() {
            return Paginated.EMPTY_PAGES.getBatchCount();
        }
        @Override
        public int getBatchSize() {
            return Paginated.EMPTY_PAGES.getBatchSize();
        }
    }
}

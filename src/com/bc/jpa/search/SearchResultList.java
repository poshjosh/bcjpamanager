package com.bc.jpa.search;

import com.bc.jpa.paging.PaginatedList;

/**
 * @author Josh
 * @param <T>
 */
public interface SearchResultList<T> extends PaginatedList<T>, SearchResults<T> {
    
    
}

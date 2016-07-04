package com.bc.jpa.search;

import com.bc.jpa.query.QueryBuilder;

/**
 * @(#)ParamSearchResults.java   25-Apr-2015 22:45:02
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * This class must be closed after use via the {@link #close()} method
 * @param <R> The type of the result to return
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class BaseSearchResults<R> extends QuerySearchResults<R> {
    
    private transient final QueryBuilder<R> queryBuilder;
    
    public BaseSearchResults(QueryBuilder queryBuilder) {
        super(queryBuilder.build());
        this.queryBuilder = queryBuilder;
    }
    
    public BaseSearchResults(
            QueryBuilder<R> queryBuilder, int batchSize, boolean useCache) { 
        super(queryBuilder.build(), batchSize, useCache);
        this.queryBuilder = queryBuilder;
    }
    
    @Override
    public void close() {
        super.close();
        if(this.queryBuilder != null && this.queryBuilder.isOpen()) {
            this.queryBuilder.close();
        }
    }
}

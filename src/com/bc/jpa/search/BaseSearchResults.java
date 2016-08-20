package com.bc.jpa.search;

import com.bc.jpa.dao.BuilderForSelect;

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
    
    private transient final BuilderForSelect<R> select;
    
    public BaseSearchResults(BuilderForSelect<R> select) {
        super(select.createQuery());
        this.select = select;
    }
    
    public BaseSearchResults(
            BuilderForSelect<R> select, int batchSize, boolean useCache) { 
        super(select.createQuery(), batchSize, useCache);
        this.select = select;
        
    }
    
    @Override
    public void close() {
        super.close();
        if(this.select != null && this.select.isOpen()) {
            this.select.close();
        }
    }

    public final BuilderForSelect<R> getSelect() {
        return select;
    }
}

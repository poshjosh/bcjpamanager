package com.idisc.pu;

import com.idisc.pu.entities.Feed;
import com.bc.jpa.JpaContext;

/**
 * @author Josh
 */
public class FeedQuery extends Query<Feed> {

    public FeedQuery(JpaContext cf, String query) {
        this(cf, -1, -1, query);
    }
    
    public FeedQuery(JpaContext cf, int offset, int limit, String query) {
        
        super(cf, Feed.class, offset, limit, query, "title", "keywords", "description", "content");
    }
}

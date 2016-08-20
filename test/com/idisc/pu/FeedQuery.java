package com.idisc.pu;

import com.idisc.pu.entities.Feed;
import com.bc.jpa.JpaContext;

/**
 * @author Josh
 */
public class FeedQuery extends SearchQueryBuilder<Feed> {

    public FeedQuery(JpaContext jpaContext, String query) {
        this(jpaContext, -1, -1, query);
    }
    
    public FeedQuery(JpaContext jpaContext, int offset, int limit, String query) {
        
        super(jpaContext, Feed.class, offset, limit, query, "title", "keywords", "description", "content");
    }
}

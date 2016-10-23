package com.idisc.pu;

import com.idisc.pu.entities.Comment;
import com.bc.jpa.JpaContext;

/**
 * @author Josh
 */
public class CommentDao extends SearchDao<Comment> {
    
    public CommentDao(JpaContext cf, String query) {
        this(cf, -1, -1, query);
    }
    
    public CommentDao(JpaContext cf, int offset, int limit, String query) {
        
        super(cf, Comment.class, offset, limit, query, "commentSubject", "commentText");
    }
}

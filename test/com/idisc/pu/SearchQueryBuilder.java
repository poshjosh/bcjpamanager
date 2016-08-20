/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idisc.pu;

import com.bc.jpa.dao.BuilderForSelectImpl;
import javax.persistence.TypedQuery;
import com.bc.jpa.JpaContext;

/**
 *
 * @author Josh
 */
public class SearchQueryBuilder<R> extends BuilderForSelectImpl<R> {

    private final int offset;
    
    private final int limit;

    public SearchQueryBuilder(JpaContext cf, Class<R> resultType, String query, String... cols) {
        this(cf, resultType, -1, -1, query, cols);
    }
    
    public SearchQueryBuilder(JpaContext cf, Class<R> resultType, int offset, int limit, String query, String... cols) {
        
        super(cf.getEntityManager(resultType), resultType, cf.getDatabaseFormat());
        
        SearchQueryBuilder.this.from(resultType);
        
        SearchQueryBuilder.this.descOrder(cf.getMetaData().getIdColumnName(resultType));
        
        if(query != null) {
        
            SearchQueryBuilder.this.search(query, cols);
        }
        
        this.offset = offset;
        
        this.limit = limit;
    }

    @Override
    public TypedQuery<R> format(TypedQuery<R> tq) {
        tq = super.format(tq); 
        if(this.offset >= 0) {
            tq.setFirstResult(offset);
        }
        if(this.limit > 0) {
            tq.setMaxResults(limit);
        }
        return tq;
    }
}

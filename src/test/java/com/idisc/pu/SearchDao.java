/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idisc.pu;

import com.bc.jpa.dao.SelectImpl;
import javax.persistence.TypedQuery;
import com.bc.jpa.context.JpaContext;

/**
 *
 * @author Josh
 */
public class SearchDao<R> extends SelectImpl<R> {

    private final int offset;
    
    private final int limit;

    public SearchDao(JpaContext jpaContext, Class<R> resultType, String query, String... cols) {
        this(jpaContext, resultType, -1, -1, query, cols);
    }
    
    public SearchDao(JpaContext jpaContext, Class<R> resultType, int offset, int limit, String query, String... cols) {
        
        super(jpaContext.getEntityManager(resultType), resultType, jpaContext.getDatabaseFormat());
        
        SearchDao.this.from(resultType);
        
        SearchDao.this.descOrder(jpaContext.getMetaData().getIdColumnName(resultType));
        
        if(query != null) {
        
            SearchDao.this.search(query, cols);
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

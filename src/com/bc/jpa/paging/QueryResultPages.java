package com.bc.jpa.paging;

/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke - Bug 370875 - Paging List Example
 ******************************************************************************/
import com.bc.util.XLogger;
import java.util.List;
import java.util.Objects;

import java.util.Set;
import java.util.logging.Level;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * <p>
 * Example class that wraps the execution of a {@link javax.persistence.TypedQuery} 
 * calculating the current size and then paging the results using the provided 
 * page size.
 * </p>
 * <b>Notes:</b>
 * <ul>
 * <li>The query should contain an ORDER BY</li> 
 * <li>The following methods must not have been called on the query:<br/>
 * {@link javax.persistence.TypedQuery#setFirstResult(int)}<br/> 
 * {@link javax.persistence.TypedQuery#setMaxResults(int)}
 * </li>
 * <li>The usage of this may produce incorrect results if the matching data set 
 * changes on the database while the results are being paged.</li>
 * </ul>
 * @param <T>
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class QueryResultPages<T> extends AbstractPages<T> implements AutoCloseable {

    private transient TypedQuery typedQuery;

    public QueryResultPages(TypedQuery<T> typedQuery) {  
        this(typedQuery, 20);
    }
    
    public QueryResultPages(TypedQuery<T> typedQuery, int pageSize) {  
        super(pageSize);
        this.typedQuery = Objects.requireNonNull(typedQuery);
    }
    
    @Override
    public void close() {
        this.typedQuery = null;
    }

    @Override
    protected List<T> loadBatch(int pageNum) {
        final int batchSize = this.getPageSize();
        typedQuery.setFirstResult(batchSize * pageNum);
        typedQuery.setMaxResults(batchSize);
        return typedQuery.getResultList();
    }
    
    /**
     * <p>
     * Using the provided {@link TypedQuery} to calculate the size. The query is
     * copied to create a new query which just retrieves the count.
     * </p>
     * <b>Notes:</b>
     * <ul>
     * <li>The query should contain an ORDER BY</li> 
     * <li>The following methods must not have been called on the query:<br/>
     * {@link javax.persistence.TypedQuery#setFirstResult(int)}<br/> 
     * {@link javax.persistence.TypedQuery#setMaxResults(int)}
     * </li>
     * </ul>
     * @return 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected int calculateSize() {
        
        if(typedQuery == null) {
            throw new NullPointerException();
        }
        
        JpaQuery<T> queryImpl = (JpaQuery<T>)typedQuery;
        ReadAllQuery raq = JpaHelper.getReadAllQuery(typedQuery);

        ReportQuery rq;

        if (raq.isReportQuery()) {
            rq = (ReportQuery) raq.clone();
            rq.getItems().clear();
            rq.addCount();
            rq.getGroupByExpressions().clear();
            rq.getOrderByExpressions().clear();
        } else {
            rq = new ReportQuery();
            rq.setReferenceClass(raq.getReferenceClass());
            rq.addCount();
            rq.setShouldReturnSingleValue(true);
            rq.setSelectionCriteria(raq.getSelectionCriteria());
        }
        // Wrap new report query as JPA query for execution with parameters
        TypedQuery<Number> countQuery = (TypedQuery<Number>) JpaHelper.createQuery(rq, queryImpl.getEntityManager());

        try{
// Temporary solution = Rather than use Query of type: 'SELECT p FROM User p', explicitly name the columns of the entity this way: 'SELECT p.name, p.age, p.height...etc FROM Person p'
            
//This first line is used in the bug title below... all changes be reflected below            
//@bug QueryResultPages#calculateSize(TypedQuery) TypedQuery.getParameters throws NullpointerException            
//query.getParameters() often throws the below exception
//java.lang.NullPointerException
//	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.getParameters(EJBQueryImpl.java:1442)
//	at com.loosedb.pu.jpa.QueryResultPages.calculateSize(QueryResultPages.java:149)
//	at com.loosedb.pu.jpa.QueryResultPages.<init>(QueryResultPages.java:72)
//	at com.loosedb.pu.jpa.PagingListTest.testAll(PagingListTest.java:89)
            if(typedQuery.getParameters() != null) {
                
                // Copy parameters
                Set<Parameter<?>>  params = typedQuery.getParameters();
                
XLogger.getInstance().log(Level.FINER, "Query parameters: {0}", this.getClass(), params);

                for (Parameter param : params) {
                    countQuery.setParameter(param, typedQuery.getParameterValue(param));
                }
            }
        }catch(RuntimeException bug) { 
            StringBuilder builder = new StringBuilder();
            builder.append("This is a bug. Search for '@bug SimplePagingList' to locate.\n");
            builder.append("Temporary solution = Rather than use Query of type: 'SELECT p FROM User p', explicitly name the columns of the entity this way: 'SELECT p.name, p.age, p.height...etc FROM Person p'");
            XLogger.getInstance().log(Level.WARNING, builder.toString(), this.getClass(), bug);
        }

        return countQuery.getSingleResult().intValue();
    }
}

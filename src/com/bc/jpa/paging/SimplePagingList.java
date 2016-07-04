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
public class SimplePagingList<T> extends AbstractPagingList<T> implements AutoCloseable {

    /**
     * The size determined by {@link #calculateSize(javax.persistence.TypedQuery)}
     */
    private int size = -1;
    
    private final int batchSize;
    
    private transient TypedQuery typedQuery;

    public SimplePagingList(TypedQuery<T> typedQuery) {  
        this(typedQuery, 20);
    }
    
    public SimplePagingList(TypedQuery<T> typedQuery, int pageSize) {  
        this.batchSize = pageSize;
        this.typedQuery = typedQuery;
    }

    @Override
    public void reset() {
        super.reset(); 
        this.size = -1;
    }
    
    @Override
    public void close() {
        this.typedQuery = null;
    }

    @Override
    public final int getBatchSize() {
        return batchSize;
    }
    
    @Override
    public int size() {
        if(this.size == -1) {
            // This causes size to be initialized
            this.getBatches();
        }
        return this.size;
    }

    @Override
    protected List<T> [] initPagesBuffer() {
        if(typedQuery == null) {
            throw new NullPointerException();
        }
        if(this.getBatchSize() < 1) {
            throw new UnsupportedOperationException("Page size "+this.getBatchSize()+"< 1");
        }
        this.size = calculateSize(typedQuery);
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        List<T> [] pages = new List[this.computeNumberOfPages(size, batchSize)];

        return pages;
    }
    
    @Override
    protected List<T> loadBatch(int pageNum) {
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
     * @param query
     * @return 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final int calculateSize(TypedQuery<T> query) {
        
        if(query == null) {
            throw new NullPointerException();
        }
        
        JpaQuery<T> queryImpl = (JpaQuery<T>)query;
        ReadAllQuery raq = JpaHelper.getReadAllQuery(query);

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
            
//This first line is used int the bug title below... all changes be reflected below            
//@bug SimplePagingList#calculateSize(TypedQuery) TypedQuery.getParameters throws NullpointerException            
//query.getParameters() often throws the below exception
//java.lang.NullPointerException
//	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.getParameters(EJBQueryImpl.java:1442)
//	at com.loosedb.pu.jpa.SimplePagingList.calculateSize(SimplePagingList.java:149)
//	at com.loosedb.pu.jpa.SimplePagingList.<init>(SimplePagingList.java:72)
//	at com.loosedb.pu.jpa.PagingListTest.testAll(PagingListTest.java:89)
            if(query.getParameters() != null) {
                
                // Copy parameters
                Set<Parameter<?>>  params = query.getParameters();
                
XLogger.getInstance().log(Level.FINER, "Query parameters: {0}", this.getClass(), params);

                for (Parameter param : params) {
                    countQuery.setParameter(param, query.getParameterValue(param));
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

package com.bc.jpa.paging;

import java.util.List;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.queries.ScrollableCursor;

/**
 * @(#)ScrollablePagingList.java   11-May-2014 03:22:25
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * <p>
 * This class uses a {@link org.eclipse.persistence.queries.ScrollableCursor}
 * The drawback is that the ScrollableCursor represents a live cursor and 
 * connection with database, so will normally not live across web page
 * requests.
 * </p>
 * <b>Note:</b><br/>
 * Make sure you call the close method {@link org.eclipse.persistence.queries.ScrollableCursor#close()} 
 * on the scrollable cursor returned by {@linkplain #getScrollableCursor()} 
 * after using this class
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class EclipselinkScrollablePagingList<T> extends AbstractPagingList<T> {

    private final int batchSize;
    
    private final TypedQuery<T> typedQuery;
    
    private ScrollableCursor scrollableCursor;
    
    public EclipselinkScrollablePagingList(TypedQuery<T> typedQuery, int batchSize) { 
        if(typedQuery == null) {
            throw new NullPointerException();
        }
        if(batchSize < 1) {
            throw new IllegalArgumentException();
        }
        this.batchSize = batchSize;
        this.typedQuery = typedQuery;
    }

    /**
     * Original query. This query is closed for size calculation and re-used
     * with different first/max values for each page result.
     * @return 
     */
    public final TypedQuery<T> getQuery() {
        return typedQuery;
    }
    
    @Override
    public final int getBatchSize() {
        return batchSize;
    }
    
    @Override
    public int size() {
        if(this.scrollableCursor == null) {
            // Initializes the scrollableCursor
            this.getBatches();
        }
        return this.scrollableCursor.size();
    }

    @Override
    protected List<T> [] initPagesBuffer() {
        TypedQuery<T> query = this.getQuery();
        if(query == null) {
            throw new NullPointerException();
        }
        if(this.getBatchSize() < 1) {
            throw new UnsupportedOperationException("Page size < 1");
        }
        query.setHint("eclipselink.cursor.scrollable", true);
        query.setHint("eclipselink.cursor.page-size", batchSize);
        this.scrollableCursor = (ScrollableCursor)query.getSingleResult();
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        List<T> [] pages = new List[this.computeNumberOfPages(this.scrollableCursor.size(), batchSize)];
        
        return pages;
    }

    @Override
    protected List<T> loadBatch(int pageNum) {
        
        final int offset = pageNum * batchSize;
        
        if(scrollableCursor.absolute(offset)) {
            
            final int size = this.size();

            final int pageCount = this.computeNumberOfPages(size, batchSize);

            int currentPageSize;

            if(pageNum < pageCount - 1) {
                currentPageSize = batchSize;
            }else{
                currentPageSize = size - offset;
            }

            return (List<T>)scrollableCursor.next(currentPageSize);

        }else{

            throw new IndexOutOfBoundsException("Number of pages: "+this.getBatchCount()+
                    ", page number: "+pageNum+" starting at offset: "+offset);
        }
    }

    public ScrollableCursor getScrollableCursor() {
        return scrollableCursor;
    }
}

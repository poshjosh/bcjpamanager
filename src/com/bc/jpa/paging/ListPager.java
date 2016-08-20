package com.bc.jpa.paging;

import com.bc.util.BatchUtils;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * @author Josh
 * @param <T>
 */
public class ListPager<T> extends AbstractList<T> implements PaginatedList<T> {
    
    private final int batchSize;
    
    private final List<T> list;

    public ListPager(List<T> list, int batchSize) {
        this.list = Objects.requireNonNull(list);
        if(batchSize > list.size()) {
            throw new IllegalArgumentException();
        }
        this.batchSize = batchSize;
    }

    @Override
    public void reset() { }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int getSize() {
        return this.size();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List<T> getPage(int pageNum) {
        int start = BatchUtils.getStart(pageNum, batchSize, size());
        int end = BatchUtils.getEnd(pageNum, batchSize, size());
        return list.subList(start, end);
     }

    @Override
    public final int getPageCount() {
        return BatchUtils.getBatchCount(this.getPageSize(), size());
    }

    @Override
    public final int getPageSize() {
        return batchSize;
    }
}

package com.bc.jpa.paging;

import com.bc.util.BatchUtils;
import java.util.AbstractList;
import java.util.List;

/**
 * @author Josh
 */
public class ListPager<T> extends AbstractList<T> implements PaginatedList<T> {
    
    private final int batchSize;
    
    private final List<T> list;

    public ListPager(List<T> list, int batchSize) {
        if(list == null) {
            throw new NullPointerException();
        }
        if(batchSize > list.size()) {
            throw new IllegalArgumentException();
        }
        this.list = list;
        this.batchSize = batchSize;
    }

    @Override
    public void reset() { }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List<T> getBatch(int pageNum) {
        int start = BatchUtils.getStart(pageNum, batchSize, size());
        int end = BatchUtils.getEnd(pageNum, batchSize, size());
        return list.subList(start, end);
     }

    @Override
    public final int getBatchCount() {
        return BatchUtils.getBatchCount(this.getBatchSize(), size());
    }

    @Override
    public final int getBatchSize() {
        return batchSize;
    }
}

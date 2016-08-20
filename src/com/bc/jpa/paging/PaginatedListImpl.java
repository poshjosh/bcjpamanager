/*
 * Copyright 2016 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa.paging;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 26, 2016 3:32:57 PM
 * @param <T>
 */
public class PaginatedListImpl<T> extends AbstractList<T> implements PaginatedList<T> {

    private final Paginated<T> paginated;
    
    public PaginatedListImpl(Paginated<T> paginated) {
        this.paginated = Objects.requireNonNull(paginated);
    }

    @Override
    public T get(int index) {
        return paginated.get(index);
    }

    @Override
    public int size() {
        return paginated.getSize();
    }

    @Override
    public void reset() {
        paginated.reset();
    }

    @Override
    public int getSize() {
        return paginated.getSize();
    }

    @Override
    public List<T> getPage(int pageNum) {
        return paginated.getPage(pageNum);
    }

    @Override
    public int getPageCount() {
        return paginated.getPageCount();
    }

    @Override
    public int getPageSize() {
        return paginated.getPageSize();
    }
}

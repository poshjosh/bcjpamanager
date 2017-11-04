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

package com.bc.jpa.query;

import com.looseboxes.pu.entities.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.bc.jpa.dao.Select;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 26, 2016 6:09:26 PM
 */
public class QueryBuilderTestMisc extends TestBaseForJpaQuery {

    public QueryBuilderTestMisc(String testName) {
        super(testName);
    }

    @Test
    public void testAll() {
        
        Map where = new HashMap();
        where.put("productcategoryid", "fashion");
        where.put("fakecolumn_0", "fakevalue_0");
        where.put("fakecolumn_1", null);
        
        try(Select<Integer> qb = this.createSelect(Integer.class)) {
            
            List<Integer> found = qb.select(Product.class, "productid")
            .where(where).createQuery().setMaxResults(10).getResultList();
            
System.out.println("Found: " + found);
        }
    }
}

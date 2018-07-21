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

import static com.bc.jpa.query.TestBaseForJpaQuery.ENTITY_TYPE;
import static com.bc.jpa.query.TestBaseForJpaQuery.SELECTED_PRODUCTID;
import com.looseboxes.pu.entities.Product;
import java.util.List;
import org.junit.Test;
import com.bc.jpa.dao.Select;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 18, 2016 7:30:49 PM
 */
public class TestSelectReset extends TestBaseForJpaQuery {

    public TestSelectReset(String testName) {
        super(testName);
    }

    @Test
    public void test() {
        
        final String ID_COL = "productid";
        final String query = "dress";
        final String [] colsToSelect = new String[]{"productid", "productName", "price"};
        final String [] colsToSearch = new String[]{"productName", "description", "keywords", "model"};

        
        try(Select<Object[]> instance = createSelect(Object[].class)) {
        
            List<Object[]> results = instance
                    .search(ENTITY_TYPE, query, colsToSearch)
                    .and()
                    .where(ID_COL, Select.GREATER_OR_EQUALS, SELECTED_PRODUCTID)
                    .select(colsToSelect)
                    .descOrder(ID_COL)
                    .createQuery().getResultList();
            
this.printResults(results, true);

            Product product = instance.getEntityManager().find(Product.class, SELECTED_PRODUCTID);
            
System.out.println("Product: "+product); 

            instance.reset();

            List<Object[]> results2 = instance
                    .select(ENTITY_TYPE, colsToSelect)
                    .where(ID_COL, Select.EQUALS, SELECTED_PRODUCTID)
                    .createQuery().getResultList();
            
this.printResults(results2, true);
        }
    }
}

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

package com.bc.jpa.search;

import com.bc.jpa.TestApp;
import com.looseboxes.pu.entities.Product;
import java.util.List;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 26, 2016 8:36:12 PM
 */
public class SearchResultsSizeTest extends SearchResultsTestBase {

    public SearchResultsSizeTest(String testName) {
        super(testName);
    }
    
    @Test
    public void testSize() {
        
        
        
        try(SearchResults<Product> searchresults = 
                this.createInstance(TestApp.getInstance().getLbJpaContext(), 
                Product.class, Product.class, "1 year", 100, true)) {

System.out.println("==================================Size: "+searchresults.getAllResults().size());             
System.out.println("==================================Size: "+searchresults.getSize()); 
            List<Product> found = searchresults.getAllResults();
            for(Product product:found) {
System.out.println(product.getProductid()+"="+product.getProductName());                
            }
        }
    }
}

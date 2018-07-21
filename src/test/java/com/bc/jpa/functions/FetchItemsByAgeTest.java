/*
 * Copyright 2018 NUROX Ltd.
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
package com.bc.jpa.functions;

import com.bc.jpa.TestApp;
import com.bc.jpa.context.JpaContext;
import com.looseboxes.pu.entities.Product;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class FetchItemsByAgeTest {
    
    public FetchItemsByAgeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class FetchItemsByAge.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        final String dateColumnName = "timemodified";
        final long ageMillis = TimeUnit.DAYS.toMillis(90);
        System.out.println("Age: " + TimeUnit.MILLISECONDS.toDays(ageMillis) + " days.");
        final JpaContext jpa = TestApp.getInstance().getLbJpaContext();
        final FetchItemsByAge<Product> instance = new FetchItemsByAge<>(jpa, Product.class, 10);
        System.out.println("Fetching results");
        final List<Product> result = instance.apply(dateColumnName, ageMillis);
        System.out.println("Found: " + result.size() + " results");
        for(Product product : result) {
            System.out.println("ID: " + product.getProductid()+", views: " + product.getViews() + 
                    ", timemodified: "+product.getTimemodified() + ", Name: "+ product.getProductName());
        }
    }
    
}

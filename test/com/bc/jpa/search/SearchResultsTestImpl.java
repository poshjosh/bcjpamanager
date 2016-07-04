package com.bc.jpa.search;

import com.bc.jpa.TestApp;
import com.looseboxes.pu.entities.Product;
import org.junit.Test;

/**
 * @author Josh
 */
public class SearchResultsTestImpl extends SearchResultsTest {
    
    public SearchResultsTestImpl(String testName) {
        super(testName);
    }

    @Test
    public void testAll() {
//        this.execute(TestApp.getInstance().getIdiscJpaContext(), 
//                Feed.class, Feed.class, "keshi", 5, true);
        this.execute(TestApp.getInstance().getLbJpaContext(), 
                Product.class, Product.class, "girls dress", 5, true);
    }
}

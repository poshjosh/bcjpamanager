package com.bc.jpa.search;

import com.bc.jpa.TestApp;
import com.idisc.pu.entities.Feed;
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
        this.execute(TestApp.getInstance().getIdiscJpaContext(), 
                Feed.class, Feed.class, "stephen keshi", 5, true);
//        this.execute(TestApp.getInstance().getLbJpaContext(), 
//                Product.class, Product.class, "girls dress", 5, true);
    }
}

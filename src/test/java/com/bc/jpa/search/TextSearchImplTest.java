/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bc.jpa.search;

import com.bc.jpa.TestApp;
import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.Select;
import com.looseboxes.pu.entities.Availability;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productsubcategory;
import com.looseboxes.pu.entities.Region;
import com.looseboxes.pu.entities.Userstatus;
import com.looseboxes.pu.query.SelectProduct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Josh
 */
public class TextSearchImplTest {
    
    private static PersistenceUnitContext jpa;
    private static TextSearchImpl instance;
    
    public TextSearchImplTest() { }
    
    @BeforeClass
    public static void setUpClass() {
        jpa = TestApp.getInstance().getLbJpaContext();
        instance = new TextSearchImpl(jpa);
    }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    @Test
    public void testSearch() {
        System.out.println("\ntestSearch(Class, String)");
        System.out.println("---------------------------------");

        Object result;
        List resultList;

        resultList = instance.search(Region.class, "Abia");
        result = resultList.stream().findFirst().orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Region);

        resultList = instance.search(Userstatus.class, "Unactivated");
        result = resultList.stream().findFirst().orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Userstatus);
        
        resultList = instance.search(Productsubcategory.class, "Kid's Clothing");
        result = resultList.stream().findFirst().orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Productsubcategory);
        
        resultList = instance.search(Availability.class, "In Stock");
        result = resultList.stream().findFirst().orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Availability);
    }

    public void testProductSearch() {
        
        final String query = "boy";
        
        final Map whereParams = Collections.singletonMap("productsubcategoryid", "Kid's Clothing");
        
        final Select<Product> select = new SelectProduct(query, jpa, Product.class);
        
        select.where(Product.class, whereParams);
        
        final SearchResults<Product> searchResults = new BaseSearchResults(select);
        
        System.out.println("Search Results size: " + searchResults.getSize());
        
        System.out.println("Search Results:\n" + searchResults);
    }
}

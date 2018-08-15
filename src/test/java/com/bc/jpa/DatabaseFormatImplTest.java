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
package com.bc.jpa;

import com.bc.sql.MySQLDateTimePatterns;
import com.bc.sql.SQLDateTimePatterns;
import com.looseboxes.pu.entities.Availability;
import com.looseboxes.pu.entities.Country;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productcategory;
import com.looseboxes.pu.entities.Productsubcategory;
import com.looseboxes.pu.entities.Region;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class DatabaseFormatImplTest {
    
    private static DatabaseFormatImpl instance;
    
    public DatabaseFormatImplTest() { }
    
    @BeforeClass
    public static void setUpClass() {
        try{
            instance = new DatabaseFormatImpl(
                    TestApp.getInstance().getLbJpaContext(), new MySQLDateTimePatterns()
            );
        }catch(Throwable t) {
            t.printStackTrace();
        }
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
     * Test of toDatabaseFormat method, of class DatabaseFormatImpl.
     */
    @Test
    public void testToDatabaseFormat() {
        try{
            this.doTestToDatabaseFormat();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doTestToDatabaseFormat() {
        System.out.println("toDatabaseFormat");
        Object result;
        result = instance.toDatabaseFormat(Product.class, "productcategoryid", "fashion", null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Productcategory);
        
        result = instance.toDatabaseFormat(Product.class, "productsubcategoryid", "Kid's Clothing", null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Productsubcategory);

        result = instance.toDatabaseFormat(Product.class, "availabilityid", "In Stock", null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Availability);
         
        result = instance.toDatabaseFormat(Region.class, "countryid", 566, null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Country);

        result = instance.toDatabaseFormat(Country.class, "regionid", 19, null);
        System.out.println("Result: " + result);
    }

    /**
     * Test of getDateTimePatterns method, of class DatabaseFormatImpl.
     */
//    @Test
    public void testGetDateTimePatterns() {
        System.out.println("getDateTimePatterns");
        SQLDateTimePatterns expResult = null;
        SQLDateTimePatterns result = instance.getDateTimePatterns();
    }

    /**
     * Test of getSQLValue method, of class DatabaseFormatImpl.
     */
//    @Test
    public void testGetSQLValue() {
        System.out.println("getSQLValue");
        Class entityType = null;
        Object key = null;
        Object value = null;
        Object expResult = null;
        Object result = instance.getSQLValue(entityType, key, value);
    }

    /**
     * Test of isDatabaseColumn method, of class DatabaseFormatImpl.
     */
//    @Test
    public void testIsDatabaseColumn() {
        System.out.println("isDatabaseColumn");
        Class entityType = null;
        Object key = null;
        boolean expResult = false;
        boolean result = instance.isDatabaseColumn(entityType, key);
    }

    /**
     * Test of getReference method, of class DatabaseFormatImpl.
     */
//    @Test
    public void testGetReference() {
        System.out.println("getReference");
        Class entityType = null;
        Object key = null;
        Object value = null;
        Optional expResult = null;
        Optional result = instance.getReference(entityType, key, value);
    }
}

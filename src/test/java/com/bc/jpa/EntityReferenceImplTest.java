/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bc.jpa;

import com.bc.util.JsonFormat;
import com.looseboxes.pu.References;
import com.looseboxes.pu.entities.Availability;
import com.looseboxes.pu.entities.Country;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productcategory;
import com.looseboxes.pu.entities.Productsubcategory;
import com.looseboxes.pu.entities.Region;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.JoinColumn;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class EntityReferenceImplTest {
    
    private static EntityReferenceImpl instance;
    
    public EntityReferenceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = new EntityReferenceImpl(TestApp.getInstance().getLbJpaContext());
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
     * Test of find method, of class EntityReferenceImpl.
     */
    @Test
    public void testGetReferenceOptional() {
        System.out.println("\ngetReferenceOptional(Class, String, Object)");
        System.out.println("------------------------------");
        
        Object result;
        result = instance.getReferenceOptional(Product.class, "productcategoryid", "fashion").orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Productcategory);
        
        result = instance.getReferenceOptional(Product.class, "productsubcategoryid", "Kid's Clothing").orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Productsubcategory);

        result = instance.getReferenceOptional(Product.class, "availabilityid", "In Stock").orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Availability);
         
        result = instance.getReferenceOptional(Region.class, "countryid", 566).orElse(null);
        System.out.println("Result: " + result);
        assertTrue(result instanceof Country);

        result = instance.getReferenceOptional(Country.class, "regionid", 19).orElse(null);
        System.out.println("Result: " + result);
    }
    
    /**
     * Test of find method, of class EntityReferenceImpl.
     */
    @Test
    public void testFind() {
        System.out.println("\nfind(Class, String)");
        System.out.println("------------------------------");
        Object result = instance.find(Productcategory.class, "fashion");
        System.out.println("Result: " + result);
        assertTrue(result instanceof Productcategory);

        final String [] names = {
            References.availability.InStock.getLabel(),
            References.availability.LimitedAvailability.getLabel()
        };
            
        for(String name : names) {

            final Availability found = instance.find(Availability.class, name);
            
            System.out.println("For: " + name + ", found: " + (found == null ? null : found.getAvailability()));
        }
    }

    /**
     * Test of getReferencingColumns method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferencingColumns_Class_String() {
        System.out.println("\ngetReferencingColumns");
        System.out.println("------------------------------");
//        Class referencing = Product.class;
//        String crossReferenceColumn = "productcategoryid";
//        String[] expResult = null;
//        String[] result = instance.getReferencingColumns(referencing, crossReferenceColumn);
//        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getReferencing method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferencing() {
        System.out.println("\ngetReferencing");
        System.out.println("------------------------------");
//        Class referenceClass = null;
//        EntityReferenceImpl instance = null;
//        Map<Class, String> expResult = null;
//        Map<Class, String> result = instance.getReferencing(referenceClass);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getReferencingClasses method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferencingClasses() {
        System.out.println("\ngetReferencingClasses");
        System.out.println("------------------------------");
//        Class reference = null;
//        EntityReferenceImpl instance = null;
//        Class[] expResult = null;
//        Class[] result = instance.getReferencingClasses(reference);
//        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getReferencingColumns method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferencingColumns_Class() {
        System.out.println("\ngetReferencingColumns");
        System.out.println("------------------------------");
//        Class referenceClass = null;
//        EntityReferenceImpl instance = null;
//        String[] expResult = null;
//        String[] result = instance.getReferencingColumns(referenceClass);
//        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getJoinColumns method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetJoinColumns() {
        System.out.println("\ngetJoinColumns(Class)");
        System.out.println("------------------------------");
        Class referencingClass = Product.class;
        Map<JoinColumn, Field> result = instance.getJoinColumns(referencingClass);
        log(result);
    }

    /**
     * Test of getReferences method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferences() {
        System.out.println("\ngetReferences");
        System.out.println("------------------------------");
        Class referencingClass = Product.class;
        Map<String, String> result = instance.getReferences(referencingClass);
        log(result);
    }

    /**
     * Test of getReferenceTypes method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferenceTypes() {
        System.out.println("getReferenceTypes");
        System.out.println("------------------------------");
        Class referencingClass = Product.class;
        Map<Class, String> result = instance.getReferenceTypes(referencingClass);
        log(result);
    }

    /**
     * Test of getReferenceColumn method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferenceColumn() {
        System.out.println("\ngetReferenceColumn");
        System.out.println("------------------------------");
//        Class reference = null;
//        Class referencing = null;
//        String expResult = "";
//        String result = instance.getReferenceColumn(reference, referencing);
//        System.out.println("Result: " + result);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getReferenceClass method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferenceClass() {
        System.out.println("\ngetReferenceClass");
        System.out.println("------------------------------");
//        Class refingClass = null;
//        String refingColumn = "";
//        Class expResult = null;
//        Class result = instance.getReferenceClass(refingClass, refingColumn);
//        System.out.println("Result: " + result);
//        assertEquals(expResult, result);
    }

    /**
     * Test of getReferenceClasses method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetReferenceClasses() {
        System.out.println("\ngetReferenceClasses");
        System.out.println("------------------------------");
//        Class refingClass = null;
//        EntityReferenceImpl instance = null;
//        Class[] expResult = null;
//        Class[] result = instance.getReferenceClasses(refingClass);
//        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getTemporalType method, of class EntityReferenceImpl.
     */
//    @Test
    public void testGetTemporalType() {
        System.out.println("\ngetTemporalType");
        System.out.println("------------------------------");
//        Class entityClass = null;
//        String column = "";
//        EntityReferenceImpl instance = null;
//        TemporalType expResult = null;
//        TemporalType result = instance.getTemporalType(entityClass, column);
//        System.out.println("Result: " + result);
//        assertEquals(expResult, result);
    }
    
    public void log(Map result) {
        System.out.println(LocalDateTime.now() + ". " + new JsonFormat(true, true, " ").toJSONString(result));
    }
}

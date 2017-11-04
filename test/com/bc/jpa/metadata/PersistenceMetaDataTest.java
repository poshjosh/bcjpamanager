/*
 * Copyright 2017 NUROX Ltd.
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

package com.bc.jpa.metadata;

import com.bc.jpa.TestApp;
import com.bc.jpa.context.PersistenceContext;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 5:05:27 PM
 */
public class PersistenceMetaDataTest extends TestCase {
    
    public PersistenceMetaDataTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void test() throws SQLException {
       
        final String filename = System.getProperty("user.home") + 
                "/Documents/NetBeansProjects/idiscpu/test/META-INF/persistence.xml";
        
        final PersistenceContext context = TestApp.loadPersistenceContext(filename);
        
        final PersistenceMetaData metaData = context.getMetaData();

        final Set<String> puNames = metaData.getPersistenceUnitNames();

        final URI uri = metaData.getURI();
System.out.println("URI: "+uri+"\nPersistence units: "+puNames);        
        
        for(String pu : puNames) {
            
            this.test(metaData, pu);
        }
    } 
    
    private void test(PersistenceMetaData jpaMetaData, String pu) {
        
System.out.println("Persistence unit: "+pu);
        try{
            Properties puProps = jpaMetaData.getProperties(pu);
System.out.println("Properties: "+puProps);                
        }catch(IOException e) {
            e.printStackTrace();
        }

        Set<Class> puEntityClasses = jpaMetaData.getEntityClasses(pu);
System.out.println("Classes: "+puEntityClasses);

        for(Class puEntityClass : puEntityClasses) {

            this.test(jpaMetaData, pu, puEntityClass);
        }
    }
    
    private void test(PersistenceMetaData metaData, String puName, Class entityClass) {
        
        final PersistenceUnitMetaData unitMetaData = metaData.getMetaData(puName);
        
        final String [] columnNames = unitMetaData.getColumnNames(entityClass);
System.out.println(" Column names: "+Arrays.toString(columnNames));

        
        int [] intArr = unitMetaData.getColumnDataTypes(entityClass);
        test("   Data types", columnNames, intArr);  

        intArr = unitMetaData.getColumnDisplaySizes(entityClass);
        test("Display sizes", columnNames, intArr);  

        intArr = unitMetaData.getColumnNullables(entityClass);
        test("    Nullables", columnNames, intArr); 
        
        final String database = unitMetaData.getDatabaseName(entityClass);
        
        final String tableName = unitMetaData.getTableName(entityClass);
        
        Class expectedClass = unitMetaData.getEntityClass(database, null, tableName);
        
        assertEquals(entityClass, expectedClass);
        
        unitMetaData.getIdColumnName(entityClass);
        
//        final String entityPuName = unitMetaData.getPersistenceUnitName(entityClass);
        
//        assertEquals(puName, entityPuName);
        
//        final String databasePuName = unitMetaData.getPersistenceUnitName(database);
        
//        assertEquals(puName, databasePuName);
    }
    
    private void test(String key, String [] columnNames, int [] intArr) {
System.out.println(key + ": " + Arrays.toString(intArr));  
        assertEquals(columnNames.length, intArr.length);
        for(int i=0; i<columnNames.length; i++) {
System.out.println(columnNames[i] + '=' + intArr[i]);            
        }
    }
}

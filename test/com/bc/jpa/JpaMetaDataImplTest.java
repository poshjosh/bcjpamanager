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
package com.bc.jpa;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class JpaMetaDataImplTest extends TestCase {
    
    public JpaMetaDataImplTest(String testName) {
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
       
        final JpaContext jpaContext = TestApp.getInstance().getIdiscJpaContext();
        
        final JpaMetaData jpaMetaData = jpaContext.getMetaData();

        final String [] puNames = jpaMetaData.getPersistenceUnitNames();

        final URI uri = jpaMetaData.getURI();
System.out.println("URI: "+uri+"\nPersistence units: "+Arrays.toString(puNames));        
        
        for(String pu : puNames) {
            
            this.test(jpaMetaData, pu);
        }
    } 
    
    private void test(JpaMetaData jpaMetaData, String pu) {
        
System.out.println("Persistence unit: "+pu);
        try{
            Properties puProps = jpaMetaData.getProperties(pu);
System.out.println("Properties: "+puProps);                
        }catch(IOException e) {
            e.printStackTrace();
        }

        Class [] puEntityClasses = jpaMetaData.getEntityClasses(pu);
System.out.println("Classes: "+Arrays.toString(puEntityClasses));

        for(Class puEntityClass : puEntityClasses) {

            this.test(jpaMetaData, pu, puEntityClass);
        }
    }
    
    private void test(JpaMetaData jpaMetaData, String puName, Class entityClass) {
        
        final String [] columnNames = jpaMetaData.getColumnNames(entityClass);
System.out.println(" Column names: "+Arrays.toString(columnNames));

        int [] intArr = jpaMetaData.getColumnDataTypes(entityClass);
        test("   Data types", columnNames, intArr);  

        intArr = jpaMetaData.getColumnDisplaySizes(entityClass);
        test("Display sizes", columnNames, intArr);  

        intArr = jpaMetaData.getColumnNullables(entityClass);
        test("    Nullables", columnNames, intArr); 
        
        final String database = jpaMetaData.getDatabaseName(entityClass);
        
        final String tableName = jpaMetaData.getTableName(entityClass);
        
        Class expectedClass = jpaMetaData.getEntityClass(database, tableName);
        
        assertEquals(entityClass, expectedClass);
        
        jpaMetaData.getIdColumnName(entityClass);
        
        final String entityPuName = jpaMetaData.getPersistenceUnitName(entityClass);
        
        assertEquals(puName, entityPuName);
        
        final String databasePuName = jpaMetaData.getPersistenceUnitName(database);
        
        assertEquals(puName, databasePuName);
    }
    
    private void test(String key, String [] columnNames, int [] intArr) {
System.out.println(key + ": " + Arrays.toString(intArr));  
        assertEquals(columnNames.length, intArr.length);
        for(int i=0; i<columnNames.length; i++) {
System.out.println(columnNames[i] + '=' + intArr[i]);            
        }
    }
}

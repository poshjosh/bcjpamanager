package com.bc.jpa;

import com.bc.sql.MySQLDateTimePatterns;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 *
 * @author Josh
 */
public class TestApp {
    
    private JpaContext lbJpaContext;
    
    private JpaContext idiscJpaContext;
    
    private static TestApp instance;

    private TestApp() {
        
System.out.println(TestApp.class.getName()+"<init>");   

        try{
            
            String filename = System.getProperty("user.home") + "/Documents/NetBeansProjects/looseboxespu/test/META-INF/persistence.xml";
            
            lbJpaContext = this.loadJpaContext(filename, com.looseboxes.pu.References.ENUM_TYPES);

            filename = System.getProperty("user.home") + "/Documents/NetBeansProjects/idiscpu/test/META-INF/persistence.xml";
            
            idiscJpaContext = this.loadJpaContext(filename, com.idisc.pu.References.ENUM_TYPES);
            
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private JpaContext loadJpaContext(String filename, Class[] enumRefClasses) {
        
        try{
            
            File persistenceFile = new File(filename);
            if(!persistenceFile.exists()) {
                throw new FileNotFoundException(filename);
            }
            
            final JpaContext jpaContext = new JpaContextImpl(
                    persistenceFile, new MySQLDateTimePatterns(), enumRefClasses);
            
            PersistenceMetaData metaData = jpaContext.getMetaData();
            String [] puNames = metaData.getPersistenceUnitNames();
System.out.println(TestCase.class.getName()+" "+Arrays.toString(puNames));
            for(String puName:puNames) {
System.out.println(puName+" = "+Arrays.toString(metaData.getEntityClasses(puName))); 
            }
            
            return jpaContext;
            
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static TestApp getInstance() {
        if(instance == null) {
            instance = new TestApp();
        }
        return instance;
    }
    
    public JpaContext getIdiscJpaContext() {
        return idiscJpaContext;
    }

    public JpaContext getLbJpaContext() {
        return lbJpaContext;
    }
}

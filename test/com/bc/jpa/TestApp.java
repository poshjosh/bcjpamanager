package com.bc.jpa;

import com.bc.jpa.context.JpaContext;
import com.bc.jpa.context.JpaContextImpl;
import com.bc.jpa.context.PersistenceContext;
import com.bc.jpa.context.PersistenceContextImpl;
import com.bc.jpa.metadata.JpaMetaData;
import com.bc.jpa.metadata.PersistenceMetaData;
import com.bc.sql.MySQLDateTimePatterns;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Set;
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
    }
    
    public static JpaContext loadJpaContext(String filename, Class[] enumRefClasses) {
        
        try{
            
            File persistenceFile = new File(filename);
            if(!persistenceFile.exists()) {
                throw new FileNotFoundException(filename);
            }
            
            final JpaContext jpaContext = new JpaContextImpl(
                    persistenceFile, new MySQLDateTimePatterns(), enumRefClasses);
            
            JpaMetaData metaData = jpaContext.getMetaData();
            
            Set<String> puNames = metaData.getPersistenceUnitNames();
            
System.out.println(TestCase.class.getName()+" "+puNames);
            for(String puName:puNames) {
System.out.println(puName+" = "+metaData.getEntityClasses(puName)); 
            }
            
            return jpaContext;
            
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static PersistenceContext loadPersistenceContext(String filename) {
        
        try{
            
            File persistenceFile = new File(filename);
            if(!persistenceFile.exists()) {
                throw new FileNotFoundException(filename);
            }
            
            final PersistenceContext persistenceContext;
            try{
                persistenceContext = new PersistenceContextImpl(
                        persistenceFile.toURI(), new MySQLDateTimePatterns());
                persistenceContext.loadMetaData();
            }catch(SQLException e) {
                throw new RuntimeException(e);
            }
            
            PersistenceMetaData metaData = persistenceContext.getMetaData();
            
            Set<String> puNames = metaData.getPersistenceUnitNames();
            
System.out.println(TestCase.class.getName()+" "+puNames);
            for(String puName:puNames) {
System.out.println(puName+" = "+metaData.getEntityClasses(puName)); 
            }
            
            return persistenceContext;
            
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
        if(idiscJpaContext == null) {
            final String filename = System.getProperty("user.home") + 
                    "/Documents/NetBeansProjects/idiscpu/test/META-INF/persistence.xml";
            idiscJpaContext = loadJpaContext(filename, com.idisc.pu.References.ENUM_TYPES);
        }
        return idiscJpaContext;
    }

    public JpaContext getLbJpaContext() {
        if(lbJpaContext == null) {
            final String filename = System.getProperty("user.home") + ""
                    + "/Documents/NetBeansProjects/looseboxespu/test/META-INF/persistence.xml";
            lbJpaContext = loadJpaContext(filename, com.looseboxes.pu.References.ENUM_TYPES);
        }
        return lbJpaContext;
    }
}

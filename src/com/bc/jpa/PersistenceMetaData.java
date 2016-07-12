package com.bc.jpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import javax.persistence.JoinColumn;
import javax.persistence.TemporalType;

/**
 * @(#)PersistenceMetaData.java   20-Mar-2014 18:42:52
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface PersistenceMetaData {

    /**
     * @return The URI to the persistence.xml data
     */
    URI getURI();
    
    /**
     * 
     * @param persistenceUnitName
     * @return Properties containing the jdbc properties for specified persistence unit
     * @throws java.io.IOException
     */
    Properties getProperties(String persistenceUnitName) throws IOException;
    
    String [] getPersistenceUnitNames();
    
    Class [] getEntityClasses(String persistenceUnitName);
    
    String getPersistenceUnitName(Class aClass);
    
    Class getEntityClass(String database, String tableName);

    Class findEntityClass(String tableName);
    
    String getPersistenceUnitName(String database);
    
    String getDatabaseName(Class aClass);
    
    String getTableName(Class aClass);
    
    String getIdColumnName(Class aClass);
    
    String [] getColumnNames(Class entityClass);
    
    int [] getColumnDisplaySizes(Class entityClass);
    
    int [] getNullables(Class entityClass);
    
    int getColumnIndex(Class entityClass, String column);
        
    TemporalType getTemporalType(Class entityClass, String column);
    
    Map<JoinColumn, Field> getJoinColumns(Class referencingClass);
    
    Map<Class, String> getReferencing(Class referenceClass);
    
    Class [] getReferencingClasses(Class reference);

    String [] getReferencingColumns(Class referenceClass);
    
    Class [] getReferenceClasses(Class referencing);
    
    Class getReferenceClass(Class referencing, String referencingColumn);
    
    /**
     * @param referencingClass The referencing class for which all references
     * are to be returned.
     * @return A Map whose key/value pairs are mapped in the format:
     * ReferencingColumnName=ReferenceColumnName
     */
    Map<String, String> getReferences(Class referencingClass);
        
    String getReferenceColumn(Class reference, Class referencing);

    String [] getReferencingColumns(Class referencing, String referenceColumn);
}

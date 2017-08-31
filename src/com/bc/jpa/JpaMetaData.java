package com.bc.jpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
 * <code>
 * <pre>
 * @Entity
 * @Table(name = "person")
 * public class Person implements Serializable {
 *   @Id
 *   @Basic(optional = false)
 *   @Column(name = "personid") 
 *   private Integer personid;
 * 
 *   @JoinColumn(name = "nationalidcard", referencedColumnName = "nationalidcardid")
 *   @OneToOne(optional = false, fetch = FetchType.LAZY)
 *   private Nationalidcard nationalidcard;
 * 
 *   @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.LAZY)
 *   private List&lt;Vehicle&gt; vehicleList;
 * }
 * @Entity
 * @Table(name = "vehicle")
 * public class Vehicle implements Serializable {
 *   @Id
 *   @Basic(optional = false)
 *   @Column(name = "vehicleid") 
 *   private Integer vehicleid;
 * 
 *   @JoinColumn(name = "owner", referencedColumnName = "personid")
 *   @ManyToOne(optional = false, fetch = FetchType.LAZY)
 *   private Person owner;
 * }
 * @Entity
 * @Table(name = "nationalidcard")
 * public class Nationalidcard implements Serializable {
 *   @Id
 *   @Basic(optional = false)
 *   @Column(name = "nationalidcardid") 
 *   private Integer nationalidcardid;
 * 
 *   @OneToOne(cascade = CascadeType.ALL, mappedBy = "nationalidcard", fetch = FetchType.LAZY)
 *   private Person person;
 * }
 * 
 *  JpaMetaData metaData = jpaContext.getMetaData();
 *  System.out.println("Ref col: "+metaData.getReferenceColumn(Person.class, Nationalidcard.class));
 *  System.out.println("Ref class: "+metaData.getReferenceClass(Person.class, nationalidcard));
 * </pre>
 * </code>
 * <p>
 *    <b>Output</b><br/>
 *    Ref col: null;<br/>
 *    Ref class: Nationalidcard.class<br/>
 * </p>
 * 
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface JpaMetaData {

    public static final int TABLE_CATALOG = 1;
    public static final int TABLE_SCHEMA = 2;
    public static final int TABLE_NAME = 3;
    public static final int COLUMN_NAME = 4;
    public static final int COLUMN_DATA_TYPE = 5;
    public static final int COLUMN_TYPE_NAME = 6;
    public static final int COLUMN_SIZE = 7;
    public static final int COLUMN_NULLABLE = 11;
    public static final int COLUMN_DEFAULT = 13;

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
    
    /**
     * If persistence unit name is null then all persistence units will be 
     * searched for the specified entity type
     * @param persistenceUnitName The persistence unit name to search may be null
     * @param entityType The entity type to find
     * @return <code>true</code> if found, otherwise <code>false</code>
     */
    boolean isListedEntityType(String persistenceUnitName, Class entityType);
    
    String [] getPersistenceUnitNames();
    
    Class [] getEntityClasses(String persistenceUnitName);
    
    Set<Class> getEntityClasses(Set<String> persistenceUnitNames);
    
    String getPersistenceUnitName(Class aClass);
    
    Class getEntityClass(String database, String tableName);

    Class findEntityClass(String tableName);
    
    String getPersistenceUnitName(String database);
    
    String getDatabaseName(Class aClass);
    
    boolean isAnyTableExisting(String persistenceUnit) throws SQLException;
    
    List<String> getExistingTables(String persistenceUnit) throws SQLException;
    
    boolean isExistingTable(Class entityType) throws SQLException;
        
    String getTableName(Class aClass);
    
    String getIdColumnName(Class aClass);
    
    String [] getColumnNames(Class entityClass);
    
    int [] getColumnDisplaySizes(Class entityClass);

    int [] getColumnDataTypes(Class entityClass);
    
    int [] getColumnNullables(Class entityClass);
    
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
    
    Map<Class, String> getReferenceTypes(Class referencingClass);
    
    String getReferenceColumn(Class reference, Class referencing);

    String [] getReferencingColumns(Class referencing, String referenceColumn);
    
    int [] fetchIntMetaData(Class entityClass, int resultSetDataIndex) throws SQLException;
    
    String [] fetchStringMetaData(Class entityClass, int resultSetDataIndex) throws SQLException;
}

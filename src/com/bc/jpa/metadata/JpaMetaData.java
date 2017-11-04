package com.bc.jpa.metadata;

import com.bc.jpa.EntityReference;
import com.bc.node.Node;
import java.sql.SQLException;

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
public interface JpaMetaData extends PersistenceMetaData, PersistenceUnitMetaData, EntityReference {

    /**
     * Build the node structure returned by {@link #getPersistenceNode()}. Call
     * this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @return The node structure which will subsequently be returned by the 
     * method {@link #getPersistenceNode()}
     * @throws SQLException 
     */
    Node<String> buildPersistenceUnitNode() throws SQLException;

    /**
     * @param tableName The table name whose corresponding entity class will be returned
     * @return The entity class for the table name argument
     * @deprecated Rather use: {@link com.bc.jpa.metadata.PersistenceUnitMetaData#getEntityClass(java.lang.String, java.lang.String, java.lang.String)} 
     */
    @Deprecated
    Class findEntityClass(String tableName);
    
    /**
     * @param entityClass
     * @return
     * @deprecated
     */
    @Deprecated
    String getPersistenceUnitName(Class entityClass);
    
    /**
     * @param database The name of the database whose persistence unit will be returned
     * @return The persistence unit for the database name argument
     * @deprecated REASON: Database name should not be hard-coded into an 
     * application i.e One shouldn't be working with database names directly
     */
    @Deprecated
    String getPersistenceUnitName(String database);
    
    int [] getColumnDisplaySizes(Class entityClass);

    int [] getColumnDataTypes(Class entityClass);
    
    int [] getColumnNullables(Class entityClass);
}

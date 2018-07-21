package com.bc.jpa.metadata;

import com.bc.jpa.EntityReference;
import com.bc.jpa.EntityReferenceImpl;
import com.bc.jpa.context.JpaContext;
import com.bc.node.Node;
import com.bc.node.NodeValueTest;
import com.bc.xml.PersistenceXmlDomImpl;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TemporalType;

/**
 * @(#)PersistenceMetaDataImpl.java   16-Aug-2014 15:13:38
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
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class JpaMetaDataImpl implements JpaMetaData, Serializable {

    private static final Logger logger = Logger.getLogger(JpaMetaDataImpl.class.getName());
    
    private final Map<String, Set<Class>> persistenceUnitClasses;
    
    private final EntityReference entityReference;
    
    private final PersistenceMetaData metaData;
    
    private final PersistenceUnitMetaData unitMetaData;
    
    public JpaMetaDataImpl(JpaContext jpaContext) {

        final URI persistenceUri = Objects.requireNonNull(jpaContext.getPersistenceConfigURI());
        
        this.metaData = new PersistenceMetaDataImpl(persistenceUri);
        try{
            this.metaData.build(jpaContext);
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
        
        this.entityReference = new EntityReferenceImpl(jpaContext);
        
        final PersistenceXmlDomImpl pudom = new PersistenceXmlDomImpl(persistenceUri);
        
        final List<String> puNames = pudom.getPersistenceUnitNames();
        
        final String name = puNames.stream().findFirst().orElseThrow(() -> 
                new IllegalStateException("File does not specify any persistence unit(s): " + persistenceUri));
        
        this.unitMetaData = metaData.getMetaData(name);
        
        final Map<String, Set<Class>> pu2cls = new LinkedHashMap<>(puNames.size(), 1.0f);
        
        for(String puName : puNames) {

            final List<String> puClsNames = pudom.getClassNames(puName);
            
            final Set<Class> puClasses = new LinkedHashSet<>(puClsNames.size());
            
            for(String puClsName:puClsNames) {
                
                try {
                    
                    puClasses.add(Class.forName(puClsName));
                    
                }catch(ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            
            pu2cls.put(puName, puClasses);
        }
        
        this.persistenceUnitClasses = Collections.unmodifiableMap(pu2cls);
        
        logger.log(Level.FINE, "{0}", this.persistenceUnitClasses);
        
        // This must come last
        //
//        for(List<Class> puClasses:this.classes) {
//            for(Class puClass:puClasses) {
//                this.validate(puClass);
//           }
//        }
    }
    
    private void validate(Class entityClass) {
        boolean inputIsRefing = this.getReferenceClasses(entityClass) != null;
        boolean inputIsRef = this.getReferencingClasses(entityClass) != null;
        if(inputIsRefing && inputIsRef) {
            Field [] fields = entityClass.getDeclaredFields();
            for(Field field:fields) {
                if(field.getAnnotation(OneToMany.class) != null || 
                        field.getAnnotation(ManyToOne.class) != null ||
                        field.getAnnotation(ManyToMany.class) != null) {
                    throw new UnsupportedOperationException(
                            "This implementation currently only supports relationship "+
                            OneToOne.class.getName()+" in entity classes which are both parent/reference and child/referencing instances");
                }
            }
        }
        
        Field [] fields = entityClass.getDeclaredFields();
        for(Field field:fields) {
            if(//field.getAnnotation(OneToOne.class) != null || 
                    field.getAnnotation(ManyToMany.class) != null) {
                throw new UnsupportedOperationException("Annotations javax.peristence.ManyToMany not supported");
            }
        }
    }

    @Override
    public Class findEntityClass(String tablename) {
        if(tablename == null) {
            throw new NullPointerException();
        }
        Class entityClass = null;
        for(String puName : this.persistenceUnitClasses.keySet()) {
            Set<Class> puClasses = this.getEntityClasses(puName);
//System.out.println("PU: "+puName+", classes: "+(Arrays.toString(puClasses)));            
            for(Class puClass:puClasses) {
                if(this.getTableName(puClass).equals(tablename)) {
                    entityClass = puClass;
                    break;
                }
            }
            
        }
        return entityClass;
    }
    
    @Override
    public String getPersistenceUnitName(Class aClass) {
        
        for(String puName : persistenceUnitClasses.keySet()) {
            
            if(persistenceUnitClasses.get(puName).contains(aClass)) {
                
                return puName;
            }
        }
        
        return null;
    }

    @Override
    public String getPersistenceUnitName(String database) {
        final Node<String> dbNode = this.findFirstNodeOrException(this.getNode(), 2, database);
        final Node<String> puNode = Objects.requireNonNull(
                dbNode.getParentOrDefault(null), () -> "Orphan database node: " + dbNode
        );
        return Objects.requireNonNull(
                puNode.getValueOrDefault(null), () -> "Missing value for node: " + puNode
        );
    }

    public Node<String> findFirstNodeOrException(Node<String> offset, int nodeLevel, String nodeValue) {
        final Predicate<Node<String>> nodeTest = this.getNodeTest(nodeLevel, nodeValue);
        final Node<String> node = offset.findFirstChild(nodeTest).orElseThrow(
                () -> getIllegalArgumentException(nodeLevel, nodeValue)
        );
        return node;
    }
    
    private RuntimeException getIllegalArgumentException(int nodeLevel, String nodeValue) {
        return new IllegalArgumentException("Unexpected " + this.getLevelName(nodeLevel) + " name: " + nodeValue);
    }

    public final String getLevelName(int offset) {
        switch(offset) {
            case PersistenceNodeBuilder.NODE_LEVEL_PERSISTENCE: return "persistence";
            case PersistenceNodeBuilder.NODE_LEVEL_PERSISTENCE_UNIT: return "persistence-unit";
            case PersistenceNodeBuilder.NODE_LEVEL_CATALOG: return "catalog";
            case PersistenceNodeBuilder.NODE_LEVEL_SCHEMA: return "schema";
            case PersistenceNodeBuilder.NODE_LEVEL_TABLE: return "table";
            case PersistenceNodeBuilder.NODE_LEVEL_COLUMN: return "column";
            default: throw new IllegalArgumentException("Unexpected persistence offset. Possible values: 0=persisence,1=persistenceUnit,2=catalog/database,3=schema,4=table,5=column");
        }
    }

    private Predicate<Node<String>> getNodeTest(int level, String value) {
        return new NodeValueTest(level, value, false);
    }
    
    @Override
    public URI getURI() {
        return metaData.getURI();
    }

    @Override
    public Node<String> getNode() {
        return metaData.getNode();
    }

    @Override
    public Properties getProperties(String persistenceUnitName) throws IOException {
        return metaData.getProperties(persistenceUnitName);
    }

    @Override
    public boolean isListedEntityType(String persistenceUnitName, Class entityType) {
        return metaData.isListedEntityType(persistenceUnitName, entityType);
    }

    @Override
    public boolean isListedEntityTypes(String persistenceUnitName, List<Class> entityTypes) {
        return metaData.isListedEntityTypes(persistenceUnitName, entityTypes);
    }

    @Override
    public boolean isListedEntityTypes(List<Class> entityTypes) {
        return unitMetaData.isListedEntityTypes(entityTypes);
    }

    @Override
    public Set<String> getPersistenceUnitNames() {
        return metaData.getPersistenceUnitNames();
    }

    @Override
    public Set<Class> getEntityClasses(String persistenceUnitName) {
        return metaData.getEntityClasses(persistenceUnitName);
    }

    @Override
    public Set<Class> getEntityClasses(Set<String> persistenceUnitNames) {
        return metaData.getEntityClasses(persistenceUnitNames);
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses() {
        return metaData.getPersistenceUnitClasses();
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses(String... persistenceUnitNames) {
        return metaData.getPersistenceUnitClasses(persistenceUnitNames);
    }

    @Override
    public boolean isAnyListedTableExisting(String persistenceUnit) {
        return metaData.isAnyListedTableExisting(persistenceUnit);
    }

    @Override
    public boolean isAnyTableExisting(String persistenceUnit) {
        return metaData.isAnyTableExisting(persistenceUnit);
    }

    @Override
    public Node<String> build(Function<String, EntityManagerFactory> emfProvider) throws SQLException {
        return metaData.build(emfProvider);
    }

    @Override
    public PersistenceUnitMetaData getMetaData(String persistenceUnit) {
        return metaData.getMetaData(persistenceUnit);
    }

    @Override
    public boolean isBuilt() {
        return metaData.isBuilt();
    }

    @Override
    public Set<Class> getEntityClasses() {
        return unitMetaData.getEntityClasses();
    }

    @Override
    public Class getEntityClass(String catalog, String schema, String tableName) {
        return unitMetaData.getEntityClass(catalog, schema, tableName);
    }

    @Override
    public String getCatalogName(Class entityClass) {
        return unitMetaData.getCatalogName(entityClass);
    }

    @Override
    public String getDatabaseName(Class entityClass) {
        return unitMetaData.getDatabaseName(entityClass);
    }

    @Override
    public String getSchemaName(Class entityClass) {
        return unitMetaData.getSchemaName(entityClass);
    }

    @Override
    public Class getEntityClass(String tableName) {
        return unitMetaData.getEntityClass(tableName);
    }

    @Override
    public String getTableName(Class entityClass) {
        return unitMetaData.getTableName(entityClass);
    }

    @Override
    public String getIdColumnName(Class entityClass) {
        return unitMetaData.getIdColumnName(entityClass);
    }

    @Override
    public String[] getColumnNames(Class entityClass) {
        return unitMetaData.getColumnNames(entityClass);
    }

    @Override
    public int getColumnIndex(Class entityClass, String column) {
        return unitMetaData.getColumnIndex(entityClass, column);
    }

    @Override
    public Node<String> build(Node<String> persistenceNode, Function<String, EntityManagerFactory> emfProvider) throws SQLException {
        return unitMetaData.build(persistenceNode, emfProvider);
    }

    @Override
    public Class getColumnClass(Class entityClass, int columnIndex) {
        return unitMetaData.getColumnClass(entityClass, columnIndex);
    }

    @Override
    public int[] getColumnDataTypes(Class entityClass) {
        return unitMetaData.getColumnDataTypes(entityClass);
    }

    @Override
    public int[] getColumnDisplaySizes(Class entityClass) {
        return unitMetaData.getColumnDisplaySizes(entityClass);
    }

    @Override
    public int[] getColumnNullables(Class entityClass) {
        return unitMetaData.getColumnNullables(entityClass);
    }

    @Override
    public List<String> fetchExistingListedTables(Function<String, EntityManagerFactory> emfProvider) throws SQLException {
        return unitMetaData.fetchExistingListedTables(emfProvider);
    }

    @Override
    public boolean isAnyListedTableExisting() {
        return unitMetaData.isAnyListedTableExisting();
    }

    @Override
    public boolean isAnyTableExisting() {
        return unitMetaData.isAnyTableExisting();
    }

    @Override
    public boolean isListedEntityType(Class entityType) {
        return unitMetaData.isListedEntityType(entityType);
    }

    @Override
    public <E> E find(Class<E> entityType, String name) {
        return entityReference.find(entityType, name);
    }

    @Override
    public Optional getReferenceOptional(Class referencingClass, String col, Object val) {
        return entityReference.getReferenceOptional(referencingClass, col, val);
    }

    @Override
    public Optional getReferenceOptional(Class referencingClass, Map<JoinColumn, Field> joinCols, String col, Object val) {
        return entityReference.getReferenceOptional(referencingClass, joinCols, col, val);
    }

    @Override
    public TemporalType getTemporalType(Class entityClass, String column) {
        return entityReference.getTemporalType(entityClass, column);
    }

    @Override
    public Map<JoinColumn, Field> getJoinColumns(Class referencingClass) {
        return entityReference.getJoinColumns(referencingClass);
    }

    @Override
    public Map<Class, String> getReferencing(Class referenceClass) {
        return entityReference.getReferencing(referenceClass);
    }

    @Override
    public Class[] getReferencingClasses(Class reference) {
        return entityReference.getReferencingClasses(reference);
    }

    @Override
    public String[] getReferencingColumns(Class referenceClass) {
        return entityReference.getReferencingColumns(referenceClass);
    }

    @Override
    public Class[] getReferenceClasses(Class referencing) {
        return entityReference.getReferenceClasses(referencing);
    }

    @Override
    public Class getReferenceClass(Class referencing, String referencingColumn) {
        return entityReference.getReferenceClass(referencing, referencingColumn);
    }

    @Override
    public Map<String, String> getReferences(Class referencingClass) {
        return entityReference.getReferences(referencingClass);
    }

    @Override
    public Map<Class, String> getReferenceTypes(Class referencingClass) {
        return entityReference.getReferenceTypes(referencingClass);
    }

    @Override
    public String getReferenceColumn(Class reference, Class referencing) {
        return entityReference.getReferenceColumn(reference, referencing);
    }

    @Override
    public String[] getReferencingColumns(Class referencing, String referenceColumn) {
        return entityReference.getReferencingColumns(referencing, referenceColumn);
    }
}
/**
 * 

    private String getDatabaseNameFromUrl(String jdbcUrl) {
        // Extract dbname from jdbc:mysql://localhost:3306/dbname
        //
        final int offset = jdbcUrl.lastIndexOf('/') + 1;
        return jdbcUrl.substring(offset);
    }
    
    private int [] fetchIntMetaDataX_old(Class entityClass, int resultSetDataIndex) throws SQLException {

        final String [] columnNames = this.getColumnNames(entityClass);
        
        final int [] output = new int[columnNames.length];
        
        EntityManagerFactory factory = jpaContext.getEntityManagerFactory(entityClass);
        
        EntityManager entityManager = factory.createEntityManager();
        
        try{
            
            entityManager.getTransaction().begin();
        
            final java.sql.Connection connection = entityManager.unwrap(java.sql.Connection.class);

            final DatabaseMetaData dbMetaData = connection.getMetaData();

            final String databaseName = this.getDatabaseName(entityClass);
        
            final String tableName = this.getTableName(entityClass);
          
            final ResultSet dbData = dbMetaData.getColumns(null, databaseName, tableName, null);

            while(dbData.next()) {

                String columnName = dbData.getString(COLUMN_NAME);

                int data = dbData.getInt(resultSetDataIndex);
                
                for(int i=0; i<columnNames.length; i++) {
                    if(columnName.equals(columnNames[i])) {
                        output[i] = data;
                        break;
                    }
                }
            }

            entityManager.getTransaction().commit();  
            
            return output;
            
        }finally{
            
            entityManager.close();
        }
    }

    private Class[] getReferencingClassesOld(Class reference) {
        Field [] fields = reference.getDeclaredFields();
        List<Class> refingClasses = null;
        for(Field field:fields) {
            Class type = field.getType();
            Type genType = field.getGenericType();
            if(this.isCollectionType(type) &&
                genType instanceof ParameterizedType) {
                OneToMany one2many = field.getAnnotation(OneToMany.class);
                if(one2many == null) {
                    continue;
                }
                Class refingClass = one2many.targetEntity();
                if(refingClasses == null) {
                    
                }
                refingClasses.add(refingClass);
                
//                ParameterizedType pType = (ParameterizedType)genType;
//                Type [] typeArgs = pType.getActualTypeArguments();
//                refingClasses = this.addAll(refingClasses, typeArgs);
            }
        }
        return refingClasses == null ? null : refingClasses.toArray(new Class[refingClasses.size()]);
    }
    
    private List<Class> addAll(List<Class> list, Type [] types) {
        if(types == null || types.length == 0) {
            return list;
        }
        if(list == null) {
            list = new ArrayList<Class>();
        }
        for(Type type:types) {
            list.add(type.getClass());
        }
        return list;
    }
    
    private boolean containsType(Type [] types, Type toFind) {
        boolean contains = false;
        for(Type type:types) {
            if(type == toFind) {
                contains = true;
                break;
            }
        }
        return contains;
    }
    
    private boolean isCollectionType(Class type) {
        return type == java.util.Collection.class ||
                type == java.util.Set.class ||
                type == java.util.List.class;
    }

 * 
 */


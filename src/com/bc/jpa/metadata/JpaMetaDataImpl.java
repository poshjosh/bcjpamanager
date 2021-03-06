package com.bc.jpa.metadata;

import com.bc.jpa.EntityReference;
import com.bc.jpa.EntityReferenceImpl;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.dom.PersistenceDOM;
import com.bc.jpa.dom.PersistenceDOMImpl;
import com.bc.node.Node;
import com.bc.node.NodeValueTest;
import com.bc.util.XLogger;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
    
    private final boolean caseInsensitiveNames = false;
    
    private final Map<String, Set<Class>> persistenceUnitClasses;
    
    private final URI persistenceConfigURI;
    
    private final MetaDataAccess metaDataAccess;
    
    private final EntityReference entityReference;

    public JpaMetaDataImpl(JpaContext jpaContext) {
        this(new MetaDataAccessImpl(jpaContext), jpaContext.getPersistenceConfigURI());
    }
    
    public JpaMetaDataImpl(MetaDataAccess metaDataSource, URI persistenceConfigURI) { 
        
        this.persistenceConfigURI = Objects.requireNonNull(persistenceConfigURI);
        
        this.metaDataAccess = Objects.requireNonNull(metaDataSource);
        
        this.entityReference = new EntityReferenceImpl();
        
        final PersistenceDOMImpl pudom = new PersistenceDOMImpl(persistenceConfigURI);
        
        final List<String> puNames = pudom.getPersistenceUnitNames();
        
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
    public Node<String> build(Function<String, EntityManagerFactory> emfProvider) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node<String> build(Node<String> persistenceNode, Function<String, EntityManagerFactory> emfProvider) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> fetchExistingListedTables(Function<String, EntityManagerFactory> emfProvider) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAnyListedTableExisting() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAnyTableExisting() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isListedEntityType(Class entityType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class getEntityClass(String tableName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PersistenceUnitMetaData getMetaData(String persistenceUnit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isBuilt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getReference(EntityManager em, Class referencingClass, String col, Object val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getReference(EntityManager em, Class referencingClass, Map<JoinColumn, Field> joinCols, String col, Object val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public URI getURI() {
        return persistenceConfigURI;
    }
    
    @Override
    public Properties getProperties(String persistenceUnitName) throws IOException {
        final PersistenceDOM pudom = new PersistenceDOMImpl(persistenceConfigURI);
        return pudom.getProperties(persistenceUnitName);
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses(String... persistenceUnitNames) {
        final Map<String, Set<Class>> output = new LinkedHashMap<>();
        for(String puName : persistenceUnitNames) {
            final Set<Class> puClasses = this.persistenceUnitClasses.get(puName);
            output.put(puName, puClasses);
        }
        return Collections.unmodifiableMap(output);
    }

    @Override
    public Set<Class> getEntityClasses() {
        return this.getEntityClasses(this.persistenceUnitClasses.keySet());
    }
    
    /**
     * If persistence unit name is null then all persistence units will be 
     * searched for the specified entity type
     * @param persistenceUnitName The persistence unit name to search may be null
     * @param entityType The entity type to find
     * @return <code>true</code> if found, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityType(String persistenceUnitName, Class entityType) {
        final Collection<Set<Class>> source;
        if(persistenceUnitName != null) {
            source = Collections.singleton(persistenceUnitClasses.get(persistenceUnitName));
        }else{
            source = persistenceUnitClasses.values();
        }
        for(Set<Class> puClasses : source) {
            if(puClasses.contains(entityType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getPersistenceUnitNames() {
        return Collections.unmodifiableSet(this.persistenceUnitClasses.keySet());
    }
    
    @Override
    public Set<Class> getEntityClasses(Set<String> persistenceUnitNames) {
    
        final Set<Class> entityClasses = new LinkedHashSet();
        
        for(String puName : persistenceUnitNames) {
            
            entityClasses.addAll(this.persistenceUnitClasses.get(puName));
        }
        
        return Collections.unmodifiableSet(entityClasses);
    }
    
    @Override
    public Set<Class> getEntityClasses(String persistenceUnitName) {
        
        final Set<Class> puClasses = persistenceUnitClasses.get(persistenceUnitName);
        
        return Collections.unmodifiableSet(puClasses);
    }
    
    @Override
    public Class getEntityClass(String database, String schema, String table) {
        final Node<String> dbNode = this.findFirstNodeOrException(this.getNode(), 2, database);
        final Node<String> puNode = Objects.requireNonNull(
                dbNode.getParentOrDefault(null), () -> "Orphan database node: " + dbNode
        );
        final String puName = Objects.requireNonNull(
                puNode.getValueOrDefault(null), () -> "Missing value for node: " + puNode
        );
        dbNode.findFirstChild(schema, table).orElseThrow(
                () -> new IllegalArgumentException(
                        "Unexpected catalog.schema.table: " + database + '.' + schema + '.' + table
                )
        );
        final Set<Class> dbClasses = this.persistenceUnitClasses.get(puName);
        Class entityClass = null;
        for(Class dbClass:dbClasses) {
            if(this.getTableName(dbClass).equals(table)) {
                entityClass = dbClass;
                break;
            }
        }
//Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
//        "Database: {0}, table: {1}, generated class: {2}", 
//        new Object[]{database, table, entityClass});        
        return entityClass;
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

    @Override
    public boolean isAnyListedTableExisting(String persistenceUnit) {
        boolean output = false;
        final Node<String> root = this.getNode();
        final Node<String> puNode = root.findFirstChild(persistenceUnit).orElse(null);
        if(puNode != null) {
            final Set<Class> classes = this.getEntityClasses(persistenceUnit);
            for(Class cls : classes) {
                final String tableNameFromAnnotation = this.getTableNameFromAnnotation(cls);
                Objects.requireNonNull(tableNameFromAnnotation);
                final Predicate<Node<String>> nodeValueTest = this.getNodeTest(
                        PersistenceNodeBuilder.NODE_LEVEL_TABLE, tableNameFromAnnotation);
                if(puNode.findFirstChild(nodeValueTest).isPresent()) {
                    output = true;
                    break;
                }
            }
        }
        return output;
    }
    
    @Override
    public boolean isAnyTableExisting(String persistenceUnit) {
        boolean output = false;
        final Node<String> root = this.getNode();
        final List<Node<String>> catalogNodes = root.getChildren();
        for(Node<String> catalogNode : catalogNodes) {
            final List<Node<String>> schemaNodes = catalogNode.getChildren();
            for(Node<String> schemaNode : schemaNodes) {
                if(!schemaNode.getChildren().isEmpty()) {
                    output = true;
                    break;
                }
            }
        }
        return output;
    }
    
    @Override
    public String getTableName(Class aClass) {
        final String tableName = this.findFirstNodeOrException(
                this.getNode(), PersistenceNodeBuilder.NODE_LEVEL_TABLE, this.getTableNameFromAnnotation(aClass)).getValue().get();
XLogger.getInstance().log(Level.FINER, "Entity class: {0}, table name: {1}", this.getClass(), aClass, tableName);
        return tableName;
    }
    
    /**
     * @param aClass The class whose table name is to be returned
     * @return The table name as extracted from the supplied entity type's annotation
     */
    public String getTableNameFromAnnotation(Class aClass) {
        Table table = (Table)aClass.getAnnotation(Table.class);
XLogger.getInstance().log(Level.FINER, "Entity class: {0}, table annotation: {1}", this.getClass(), aClass, table);
        return table.name();
    }
    
    @Override
    public String getIdColumnName(Class aClass) {
        
        String idColumnName = null;
        
        final Field [] fields = aClass.getDeclaredFields();
        for(Field field:fields) {
            Id id = field.getAnnotation(Id.class);
            if(id != null) {
                Column column = field.getAnnotation(Column.class);
                idColumnName = column.name();
                break;
            }
        }
//Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
//        "Class: {0}, id field name: {1}, id column name: {2}", 
//        new Object[]{aClass, idFieldName, idColumnName});        
        return idColumnName;
    }
    
    @Override
    public int getColumnIndex(Class entityClass, String column) {
        int columnIndex = -1;
        final String [] columns = this.getColumnNames(entityClass);
        for(int i=0; i<columns.length; i++) {
            if(columns[i].equals(column)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    @Override
    public String [] getColumnNames(Class entityClass) {
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Function<Node<String>, String> nodeToNodeValue = (node) -> node.getValueOrDefault(null);
        final List<String> columnNames = tableNode.getChildren().stream().map(nodeToNodeValue).collect(Collectors.toList());
        return columnNames.isEmpty() ? new String[0] : columnNames.toArray(new String[0]);
    }

    @Override
    public int [] getColumnDisplaySizes(Class entityClass) {
        try{
            return this.fetchIntMetaData(entityClass, MetaDataAccess.COLUMN_SIZE);
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
            return new int[0];
        }
    }
    
    @Override
    public int [] getColumnDataTypes(Class entityClass) {
        try{
            return this.fetchIntMetaData(entityClass, MetaDataAccess.COLUMN_DATA_TYPE);
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
            return new int[0];
        }
    }
    
    @Override
    public int [] getColumnNullables(Class entityClass) {
        try{
            return this.fetchIntMetaData(entityClass, MetaDataAccess.COLUMN_NULLABLE);
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
            return new int[0];
        }
    }
    
    public int [] fetchIntMetaData(Class entityClass, int resultSetDataIndex) throws SQLException {
        
        if(resultSetDataIndex < PersistenceNodeBuilder.NODE_LEVEL_TABLE) {
            throw new IllegalArgumentException();
        }
        
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Node<String> schemaNode = Objects.requireNonNull(tableNode.getParentOrDefault(null));
        final Node<String> catalogNode = Objects.requireNonNull(schemaNode.getParentOrDefault(null));
        final Node<String> puNode = Objects.requireNonNull(catalogNode.getParentOrDefault(null));
        
        final List<Integer> fetched = metaDataAccess.fetchIntMetaData(
                puNode.getValue().get(), catalogNode.getValueOrDefault(null),
                schemaNode.getValueOrDefault(null), tableNode.getValue().get(), 
                null, resultSetDataIndex);
        
        final int [] output = new int[fetched.size()];
        
        int i = 0;
        for(Integer integer : fetched) {
            output[i++] = integer;
        }
        
        return output;
    }

    public List<String> fetchStringMetaData(Class entityClass, int resultSetDataIndex) throws SQLException{
        
        if(resultSetDataIndex < PersistenceNodeBuilder.NODE_LEVEL_TABLE) {
            throw new IllegalArgumentException();
        }
        
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
//        System.out.println("xxxxxxxxxxxxxxxxxx\nEntity type: " + entityClass.getName() + ", table node: " + tableNode);
        final Node<String> schemaNode = Objects.requireNonNull(tableNode.getParentOrDefault(null));
        final Node<String> catalogNode = Objects.requireNonNull(schemaNode.getParentOrDefault(null));
        final Node<String> puNode = Objects.requireNonNull(catalogNode.getParentOrDefault(null));
        
        final List<String> set = metaDataAccess.fetchStringMetaData(
                puNode.getValue().get(), catalogNode.getValueOrDefault(null),
                schemaNode.getValueOrDefault(null), tableNode.getValue().get(), 
                null, resultSetDataIndex);
        
        return set;
    }
        
    @Override
    public String getDatabaseName(Class entityClass) {
        return this.getCatalogName(entityClass);
    }
    
    @Override
    public String getCatalogName(Class entityClass) {
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Node<String> schemaNode = Objects.requireNonNull(tableNode.getParentOrDefault(null));
        final Node<String> catalogNode = Objects.requireNonNull(schemaNode.getParentOrDefault(null));
        return catalogNode.getValueOrDefault(null);
    }
    
    @Override
    public String getSchemaName(Class entityClass) {
        final Node<String> tableNode = this.findFirstTableNodeOrException(entityClass);
        final Node<String> schemaNode = Objects.requireNonNull(tableNode.getParentOrDefault(null));
        return schemaNode.getValueOrDefault(null);
    }
    
    public Node<String> findFirstTableNodeOrException(Class entityClass) {
        
        final Node<String> persistenceNode = this.getNode();
//System.out.println("Persistence node: " + persistenceNode + "\nChildren: " + persistenceNode.getChildren());        
        final String puName = this.getPersistenceUnitName(entityClass);
//System.out.println("Persistence unit name: " + puName);        
        final Node<String> puNode = this.findFirstNodeOrException(persistenceNode, 
                PersistenceNodeBuilder.NODE_LEVEL_PERSISTENCE_UNIT, puName);
        
        final String tableNameFromAnnotation = this.getTableNameFromAnnotation(entityClass);
        
//System.out.println("\n@"+this.getClass()+", entity type: "+entityClass.getName()+"\nPersistence unit node: " + puNode);
        final Node<String> tableNode = this.findFirstNodeOrException(puNode, 
                PersistenceNodeBuilder.NODE_LEVEL_TABLE, tableNameFromAnnotation);
        
//System.out.println("Table node: " + tableNode + "\nPersistence unit of table: " +tableNode.getParentOrDefault(null).getParentOrDefault(null).getParentOrDefault(null).getValueOrDefault(null));
        return tableNode;
    }

    public Node<String> findFirstNodeOrException(Node<String> offset, int nodeLevel, String nodeValue) {
        final Predicate<Node<String>> nodeTest = this.getNodeTest(nodeLevel, nodeValue);
        final Node<String> node = offset.findFirstChild(nodeTest).orElseThrow(
                () -> getIllegalArgumentException(nodeLevel, nodeValue)
        );
        return node;
    }
    
    private Predicate<Node<String>> getNodeTest(int level, String value) {
        return new NodeValueTest(level, value, this.caseInsensitiveNames);
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
    
    private Node<String> _pn;
    @Override
    public Node<String> getNode() {
        if(_pn == null) {
            try{
                this._pn = this.buildPersistenceUnitNode();
            }catch(SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return _pn;
    }

    @Override
    public Node<String> buildPersistenceUnitNode() throws SQLException{
        this._pn = new PersistenceNodeBuilderImpl(
                metaDataAccess, new LinkedHashSet(new PersistenceDOMImpl(this.persistenceConfigURI).getPersistenceUnitNames())).build();
        return this._pn;
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

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses() {
        return persistenceUnitClasses;
    }

    public MetaDataAccess getMetaDataAccess() {
        return metaDataAccess;
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


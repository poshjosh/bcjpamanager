package com.bc.jpa;

import com.bc.jpa.dom.PersistenceDOMImpl;
import com.bc.util.IntegerArray;
import com.bc.util.XLogger;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
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
    
    private Map<String, List<Class>> puToClassesMap;
    
    private transient final JpaContext jpaContext;

    public JpaMetaDataImpl(JpaContext jpaContext) { 
XLogger logger = XLogger.getInstance();
Level level = Level.FINE;
Class cls = this.getClass();

        this.jpaContext = Objects.requireNonNull(jpaContext);
        
        final PersistenceDOMImpl pudom = new PersistenceDOMImpl(jpaContext.getPersistenceConfigURI());

        final List<String> puNames = pudom.getPersistenceUnitNames();
        
        this.puToClassesMap = new LinkedHashMap<>(puNames.size(), 1.0f);
        
        for(String puName : puNames) {

            List<String> puClsNames = pudom.getClassNames(puName);
            
            List<Class> puClasses = new ArrayList<>(puClsNames.size());
            
            for(String puClsName:puClsNames) {
                
                try {
                    
                    puClasses.add(Class.forName(puClsName));
                    
                }catch(ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            
            this.puToClassesMap.put(puName, puClasses);
        }
        
        logger.log(level, "{0}", cls, this.puToClassesMap);

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
    public URI getURI() {
        return this.jpaContext.getPersistenceConfigURI();
    }
    
    @Override
    public Properties getProperties(String persistenceUnitName) throws IOException {
        PersistenceDOMImpl pudom = new PersistenceDOMImpl(this.jpaContext.getPersistenceConfigURI());
        return pudom.getProperties(persistenceUnitName);
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
        final Collection<List<Class>> source;
        if(persistenceUnitName != null) {
            source = Collections.singleton(Arrays.asList(this.getEntityClasses(persistenceUnitName)));
        }else{
            source = puToClassesMap.values();
        }
        for(List<Class> puClasses : source) {
            if(puClasses.contains(entityType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String [] getReferencingColumns(Class referencing, String crossReferenceColumn) {
        
        Map<String, String> refs = this.getReferences(referencing);
        
        List<String> output = null;
        
        for(String key:refs.keySet()) {
            String val = refs.get(key);
            if(val.equals(crossReferenceColumn)) {
                if(output == null) {
                    output = new ArrayList<>();
                }
                output.add(key);
            }
        }
        
        return output == null ? new String[0] : output.toArray(new String[output.size()]);
    }

    @Override
    public Map<Class, String> getReferencing(Class referenceClass) {
        
        Field [] fields = referenceClass.getDeclaredFields();
        
        Map<Class, String> referencing = new HashMap<>();
        
        for(Field field:fields) {
            
            this.addReferencing(referenceClass, field, referencing);
        }

        return referencing.isEmpty() ? Collections.EMPTY_MAP : referencing;
    }
    
    private boolean addReferencing(Class ref, Field field, Map<Class, String> buff) {
    
        if(buff == null) {
            throw new NullPointerException();
        }
        
        Annotation ann;
        OneToMany one2many = field.getAnnotation(OneToMany.class);

        final Class referencingClass;
        
        final String mappedBy;
        
        if(one2many == null) {
            
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            
            if(one2one == null) {
                ann = null;
                mappedBy = null;
                
                referencingClass = null;
                
            }else{
                ann = one2one;
                mappedBy = one2one.mappedBy();
                
                referencingClass = field.getType();
            }
        }else{
            ann = one2many;
            mappedBy = one2many.mappedBy();

            // For field:         List<String> fieldname;
            // Type arguments is: class java.lang.String
            //
            Type [] typeArgs = this.getCollectionTypeArgs(field);

            referencingClass = typeArgs == null ? null : (Class)typeArgs[0];
        }
        
        if(referencingClass != null && mappedBy != null && !mappedBy.isEmpty()) {
            
//System.out.println(ref.getSimpleName()+"#"+field.getName()+" has "+ann+" relationship mapped by "+mappedBy+" on "+(referencingClass==null?null:referencingClass.getSimpleName()));            
        
            buff.put(referencingClass, mappedBy);
            
            return true;
            
        }else{
            
            return false;
        }
    }
    
    @Override
    public Class[] getReferencingClasses(Class reference) {
        
        Field [] fields = reference.getDeclaredFields();
        
        List<Class> refingClasses = null;
        
        for(Field field:fields) {
            
            Class refingClass = this.getReferencingClass(field);
            
//System.out.println("Field: "+field.getName()+", refing class: "+refingClass);            

            if(refingClass == null) {
                continue;
            }
            
            if(refingClasses == null) {
                refingClasses = new ArrayList<>();
            }
            
            refingClasses.add(refingClass);
        }
        
        return refingClasses == null ? new Class[0] : refingClasses.toArray(new Class[refingClasses.size()]);
    }

    @Override
    public String[] getReferencingColumns(Class referenceClass) {
        Field [] fields = referenceClass.getDeclaredFields();
        List<String> referencedColumns = null;
        for(Field field:fields) {
            String referencedColumn = this.getReferencingColumn(field);
            if(referencedColumn == null) {
                continue;
            }
            if(referencedColumns == null) {
                referencedColumns = new ArrayList<>();
            }
            if(!referencedColumns.contains(referencedColumn)) {
                referencedColumns.add(referencedColumn);
            }
        }
        return referencedColumns == null ? new String[0] : 
                referencedColumns.toArray(new String[referencedColumns.size()]);
    }
    
    @Override
    public Map<JoinColumn, Field> getJoinColumns(Class referencingClass) {
        
        Field [] fields = referencingClass.getDeclaredFields();
        
        Map<JoinColumn, Field> output = null;
        
        for(Field field:fields) {
            
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            
            if(joinColumn == null) {
                continue;
            }
            
            if(output == null) {
                output = new HashMap<>();
            }
            
            output.put(joinColumn, field);
        }
        
        return output == null ? Collections.EMPTY_MAP : output;
    }
    
    /**
     * @param referencingClass The referencing class for which all references
     * are to be returned.
     * @return A Map whose key/value pairs are mapped in the format:
     * ReferencingColumnName=ReferenceColumnName
     */
    @Override
    public Map<String, String> getReferences(Class referencingClass) {
        Field [] fields = referencingClass.getDeclaredFields();
        Map<String, String> references = null;
        for(Field field:fields) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                continue;
            }
            if(references == null) {
                references = new HashMap<>();
            }
            references.put(joinColumn.name(), joinColumn.referencedColumnName());
        }
        return references == null ? Collections.EMPTY_MAP: references;
    }

    @Override
    public Map<Class, String> getReferenceTypes(Class referencingClass) {
        
        final Map<Class, String> output;
        
        final Map<String, String> refsData = this.getReferences(referencingClass);
        
        if(refsData == null || refsData.isEmpty()) {
            
            output = Collections.EMPTY_MAP;
            
        }else{
            
            output = new LinkedHashMap();
            
            final Set<String> referencingCols = refsData.keySet();
            
            for(String referencingCol : referencingCols) {
                
                final Class referenceClass = this.getReferenceClass(referencingClass, referencingCol);
               
                output.put(referenceClass, refsData.get(referencingCol));
            }
        }
        
        return output;
    }
    
    @Override
    public String getReferenceColumn(Class reference, Class referencing) {
        Field [] fields = referencing.getDeclaredFields();
        String crossrefColumn = null;
        for(Field field:fields) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                continue;
            }
            if(field.getType() == reference) {
                crossrefColumn = joinColumn.referencedColumnName();
                break;
            }
        }
        return crossrefColumn;
        
    }

    @Override
    public Class getReferenceClass(Class refingClass, String refingColumn) {
        Field [] fields = refingClass.getDeclaredFields();
        Class refClass = null;
        for(Field field:fields) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn == null) {
                continue;
            }
            if(joinColumn.name().equals(refingColumn)) {
                refClass = field.getType();
                break;
            }
        }
        return refClass;
    }

    @Override
    public Class[] getReferenceClasses(Class refingClass) {
        Field [] fields = refingClass.getDeclaredFields();
        List<Class> refClasses = null;
        for(Field field:fields) {
            Class refClass = this.getReferenceClass(field);
            if(refClass == null) {
                continue;
            }
            if(refClasses == null) {
                refClasses = new ArrayList<>();
            }
            refClasses.add(refClass);
        }
        return refClasses == null ? new Class[0] : refClasses.toArray(new Class[refClasses.size()]);
    }
    
    @Override
    public String [] getPersistenceUnitNames() {
        return this.puToClassesMap.keySet().toArray(new String[0]);
    }
    
    @Override
    public Set<Class> getEntityClasses(Set<String> persistenceUnitNames) {
    
        final Set<Class> entityClasses = new LinkedHashSet();
        
        for(String puName : persistenceUnitNames) {
            
            entityClasses.addAll(Arrays.asList(this.getEntityClasses(puName)));
        }
        
        return Collections.unmodifiableSet(entityClasses);
    }
    
    @Override
    public Class [] getEntityClasses(String persistenceUnitName) {
        
        final List<Class> puClasses = puToClassesMap.get(persistenceUnitName);
        
        return puClasses.toArray(new Class[0]);
    }
    
    @Override
    public Class getEntityClass(String database, String table) {
        final String puName = this.getPersistenceUnitName(database);
        final List<Class> dbClasses = this.puToClassesMap.get(puName);
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
        for(String puName : this.puToClassesMap.keySet()) {
            Class [] puClasses = this.getEntityClasses(puName);
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
    public String getDatabaseName(Class aClass) {
        return this.getDatabaseName(this.getPersistenceUnitName(aClass));
    }
    
    @Override
    public boolean isExistingTable(Class entityType) throws SQLException {
        boolean output = false;
        final String tableName = this.getTableName(entityType);
        if(tableName != null) {
            final String [] tableNames = this.fetchStringMetaData(entityType, TABLE_NAME);
            if(tableNames != null && tableNames.length != 0) {
                for(String dbTable : tableNames) {
                    output = tableName.equalsIgnoreCase(dbTable);
//System.out.println("Existing: "+output+", table: "+tableName+" @"+this.getClass());                    
                    if(output) {
                        break;
                    }
                }
            }
        }        
        return output;
    }
    
    @Override
    public boolean isAnyTableExisting(String persistenceUnit) throws SQLException {
        boolean output = false;
        final Class [] entityTypes = this.getEntityClasses(persistenceUnit);
        if(entityTypes != null && entityTypes.length != 0) {
            for(Class entityType : entityTypes) {
                output = this.isExistingTable(entityType);
                if(output) {
                    break;
                }
            }
        }
        return output;
    }
    
    @Override
    public List<String> getExistingTables(String persistenceUnit) throws SQLException {
        final List<String> output;
        final Class [] entityTypes = this.getEntityClasses(persistenceUnit);
        if(entityTypes == null || entityTypes.length == 0) {
            output = Collections.EMPTY_LIST;
        }else{
            final Set<String> temp = new HashSet();
            for(Class entityType : entityTypes) {
                final String [] tableNames = this.fetchStringMetaData(entityType, TABLE_NAME);
                if(tableNames == null || tableNames.length == 0) {
                    continue;
                }
                temp.addAll(Arrays.asList(tableNames));
            }
            output = Collections.unmodifiableList(new ArrayList(temp));
        }
        return output;
    }

    @Override
    public String getTableName(Class aClass) {
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

    private transient WeakReference<Map<Class, String[]>> _$columnNamesMap;
    @Override
    public String [] getColumnNames(Class entityClass) {
        
        Map<Class, String[]> columnNamesMap;
        
        if(_$columnNamesMap == null || _$columnNamesMap.get() == null) {
            columnNamesMap = new HashMap<>();
            _$columnNamesMap = new WeakReference(columnNamesMap);
        }else{
            columnNamesMap = _$columnNamesMap.get();
        }
        
        String [] output = columnNamesMap.get(entityClass);
        
        if(output == null) {
            
            try{
                output = this.fetchStringMetaData(entityClass, COLUMN_NAME);
            }catch(SQLException e) {
                XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
                output = new String[0];
            }
            
            columnNamesMap.put(entityClass, output);
        }
        
        return output;
    }

    @Override
    public int [] getColumnDisplaySizes(Class entityClass) {
        try{
            return this.fetchIntMetaData(entityClass, COLUMN_SIZE);
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
            return new int[0];
        }
    }
    
    @Override
    public int [] getColumnDataTypes(Class entityClass) {
        try{
            return this.fetchIntMetaData(entityClass, COLUMN_DATA_TYPE);
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
            return new int[0];
        }
    }
    
    @Override
    public int [] getColumnNullables(Class entityClass) {
        try{
            return this.fetchIntMetaData(entityClass, COLUMN_NULLABLE);
        }catch(SQLException e) {
            XLogger.getInstance().log(Level.WARNING, "Unexpected exception", this.getClass(), e);
            return new int[0];
        }
    }

    @Override
    public TemporalType getTemporalType(Class entityClass, String column) {
        TemporalType output = null;
        Field [] fields = entityClass.getDeclaredFields();
        for(Field field:fields) {
            if(!field.getName().equals(column)) {
                continue;
            }
            Temporal temporalAnn = field.getAnnotation(Temporal.class);
            if(temporalAnn == null) {
                output = null;
            }else{
                output = temporalAnn.value();
            }
            break;
        }
        return output;
    }
    
    @Override
    public String getPersistenceUnitName(Class aClass) {
        
        for(String puName : puToClassesMap.keySet()) {
            
            if(puToClassesMap.get(puName).contains(aClass)) {
                
                return puName;
            }
        }
        
        return null;
    }

    @Override
    public String getPersistenceUnitName(String database) {
        
        for(String puName : this.puToClassesMap.keySet()) {
            
            if(database.equals(this.getDatabaseName(puName))) {
                
                return puName;
            }
        }
        
        return null;
    }

    public String getDatabaseName(String persistenceUnitName) {
        final Class anyClass = this.puToClassesMap.get(persistenceUnitName).get(0);
        return this.fetchDatabaseName(persistenceUnitName, anyClass);
    }

    public String fetchDatabaseName(String puName, Class aClass) {
        try{
            String [] arr = this.fetchStringMetaData(null, aClass, TABLE_CATALOG);
            if(arr == null || arr.length == 0) {
                arr = this.fetchStringMetaData(null, aClass, TABLE_SCHEMA);
            }
            return arr == null || arr.length == 0 ? null : arr[0];
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Class getReferencingClass(Field field) {
        return this.getReferencingClass(field, null);
    }
    
    private Class getReferencingClass(Field field, String referenceColumn) {
    
        OneToMany one2many = field.getAnnotation(OneToMany.class);

        Class output;
        
        if(one2many == null) {
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            if(one2one == null) {
                output = null;
            }else{
                if(referenceColumn != null && 
                        !referenceColumn.equals(one2one.mappedBy())) {
                    output = null;
                }else{
                    output = field.getType();
                }
            }
        }else{
            if(referenceColumn != null && 
                    !referenceColumn.equals(one2many.mappedBy())) {
                output = null;
            }else{

                // For field:         List<String> fieldname;
                // Type arguments is: class java.lang.String
                //
                Type [] typeArgs = this.getCollectionTypeArgs(field);

                output = typeArgs == null ? null : (Class)typeArgs[0];
            }
        }

        return output;
    }
    
    /**
     * For field:         List<String> fieldname;
     * Type arguments is: class java.lang.String
     */
    private Type [] getCollectionTypeArgs(Field field) {
        
        Type [] output;
        
        Class type = field.getType();
        Type genType = field.getGenericType();

        if(this.isCollectionType(type) &&
            genType instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType)genType;
            
            output = pType.getActualTypeArguments();
            
        }else{
            
            output = null;
        }
        
        return output == null ? new Type[0] : output;
    }

    private String getReferencingColumn(Field field) {
        String output;
        OneToMany one2many = field.getAnnotation(OneToMany.class);
        if(one2many != null) {
            output = one2many.mappedBy();
        }else{
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            if(one2one != null) {
                output = one2one.mappedBy();
            }else{
                output = null;
            }
        }
        return output == null || output.isEmpty() ? null : output;
    }
    
    private Class getReferenceClass(Field field) {
        Class output;
        ManyToOne many2One = field.getAnnotation(ManyToOne.class);
        if(many2One != null) {
            output = field.getType();
        }else{
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            if(one2one != null) {
                output = field.getType();
            }else{
                output = null;
            }
        }
        return output;
    }

    private boolean isCollectionType(Class type) {
        return type == java.util.Collection.class ||
                type == java.util.Set.class ||
                type == java.util.List.class;
    }
    
    @Override
    public int [] fetchIntMetaData(Class entityClass, int resultSetDataIndex) throws SQLException {
        final String dbName = this.getDatabaseName(entityClass);
        return this.fetchIntMetaData(dbName, entityClass, resultSetDataIndex);
    }

    private int [] fetchIntMetaData(String databaseName, Class entityClass, int resultSetDataIndex) throws SQLException {
        
        final IntegerArray intArray = new IntegerArray(30);
        
        final EntityManager entityManager = jpaContext.getEntityManager(entityClass);
        
        try{
            
            entityManager.getTransaction().begin();
        
            final java.sql.Connection connection = entityManager.unwrap(java.sql.Connection.class);

            final DatabaseMetaData dbMetaData = connection.getMetaData();

            final String tableName = this.getTableName(entityClass);
          
            final ResultSet dbData = dbMetaData.getColumns(null, databaseName, tableName, null);

            while(dbData.next()) {

                int data = dbData.getInt(resultSetDataIndex);

                intArray.add(data);
            }

            entityManager.getTransaction().commit();  
            
            return intArray.toArray();
            
        }finally{
            
            entityManager.close();
        }
    }

    @Override
    public String [] fetchStringMetaData(Class entityClass, int resultSetDataIndex) throws SQLException {
        final String dbName = this.getDatabaseName(entityClass);
        return this.fetchStringMetaData(dbName, entityClass, resultSetDataIndex);
    }
        
    private String [] fetchStringMetaData(
            String databaseName, Class entityClass, int resultSetDataIndex) throws SQLException {
        
        final List<String> list = new LinkedList<>();
        
        final EntityManager em = jpaContext.getEntityManager(entityClass);
        
        try{
        
            em.getTransaction().begin();

            final java.sql.Connection connection = em.unwrap(java.sql.Connection.class);

            final DatabaseMetaData dbMetaData = connection.getMetaData();

            final String tableName = this.getTableName(entityClass);

            final ResultSet dbData = dbMetaData.getColumns(null, databaseName, tableName, null);

            while(dbData.next()) {

                String data = dbData.getString(resultSetDataIndex);

                list.add(data);
            }

            em.getTransaction().commit();  

            return list.isEmpty() ? new String[0] : list.toArray(new String[0]);
            
        }finally{
            em.close();
        }
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
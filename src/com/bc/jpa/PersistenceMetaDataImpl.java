package com.bc.jpa;

import com.bc.jpa.dom.PersistenceDOM;
import com.bc.util.XLogger;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.Basic;
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
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class PersistenceMetaDataImpl 
        implements PersistenceMetaData, Serializable {
    
    private List<String> puNames;
    
    private List<String> jdbcUrls;

    /**
     * Format:
     * {
     *     {com.database1.Entity0.class, com.database1.Entity1.class},
     *     {com.database2.Entity0.class, com.database2.Entity1.class},
     * }
     */
    private List<List<Class>> classes;
    
    private List<List<String>> idFieldNames;
    
    private List<List<String>> idColumnNames;
    
    private transient final JpaContext jpaContext;

    public PersistenceMetaDataImpl(JpaContext jpaContext) { 
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINE;
Class cls = this.getClass();

        this.jpaContext = jpaContext;
        
long mb4 = Runtime.getRuntime().freeMemory();
long tb4 = System.currentTimeMillis();

        PersistenceDOM pudom = new PersistenceDOM(jpaContext.getPersistenceConfigURI());

if(logger.isLoggable(level, cls)) {        
    logger.log(level, "Loaded {0}, Used time: {1}, memory: {2}", cls, 
    PersistenceDOM.class.getName(), (System.currentTimeMillis()-tb4), (mb4-Runtime.getRuntime().freeMemory()));        
}

        this.puNames = pudom.getPersistenceUnitNames();
        
        this.jdbcUrls = new ArrayList<>(this.puNames.size());
        
        this.classes = new ArrayList<>(this.puNames.size());
        
        for(String puName:this.puNames) {

            Properties props = pudom.getProperties(puName);
            String jdbcUrl = props.getProperty("javax.persistence.jdbc.url");
            this.jdbcUrls.add(jdbcUrl);
            
            List<String> puClsNames = pudom.getClassNames(puName);
            
            List<Class> puClasses = new ArrayList<>(puClsNames.size());
            
            for(String puClsName:puClsNames) {
                
                try {
                    
                    puClasses.add(Class.forName(puClsName));
                    
                }catch(ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            
            this.classes.add(puClasses);
        }

if(logger.isLoggable(level, cls)) {        
    for(int i=0; i<puNames.size(); i++) {        
    logger.log(level, 
    "Persistence unit: "+puNames.get(i)+", jdbc url: {0}\nclasses: {1}\nidFieldNames: {2}\nidColumnNames: {3}",
    cls, jdbcUrls.get(i), classes.get(i), 
        idFieldNames==null?null:idFieldNames.get(i), 
        idColumnNames==null?null:idColumnNames.get(i));        
    } 
}
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
        PersistenceDOM pudom = new PersistenceDOM(this.jpaContext.getPersistenceConfigURI());
        return pudom.getProperties(persistenceUnitName);
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
        
        return output == null ? null : output.toArray(new String[output.size()]);
    }

    @Override
    public Map<Class, String> getReferencing(Class referenceClass) {
        
        Field [] fields = referenceClass.getDeclaredFields();
        
        Map<Class, String> referencing = new HashMap<>();
        
        for(Field field:fields) {
            
            this.addReferencing(field, referencing);
        }

        return referencing.isEmpty() ? null : referencing;
    }
    
    private boolean addReferencing(Field field, Map<Class, String> buff) {
    
        if(buff == null) {
            throw new NullPointerException();
        }
        
        OneToMany one2many = field.getAnnotation(OneToMany.class);

        final Class referencingClass;
        
        final String mappedBy;
        
        if(one2many == null) {
            
            OneToOne one2one = field.getAnnotation(OneToOne.class);
            
            if(one2one == null) {
                
                mappedBy = null;
                
                referencingClass = null;
                
            }else{
                
                mappedBy = one2one.mappedBy();
                
                referencingClass = field.getType();
            }
        }else{
            
            mappedBy = one2many.mappedBy();

            // For field:         List<String> fieldname;
            // Type arguments is: class java.lang.String
            //
            Type [] typeArgs = this.getCollectionTypeArgs(field);

            referencingClass = typeArgs == null ? null : (Class)typeArgs[0];
        }
        
        if(referencingClass != null && mappedBy != null) {
            
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
        
        return refingClasses == null ? null : refingClasses.toArray(new Class[refingClasses.size()]);
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
        return referencedColumns == null ? null : 
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
        
        return output;
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
        return references;
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
        return refClasses == null ? null : refClasses.toArray(new Class[refClasses.size()]);
    }
    
    @Override
    public String [] getPersistenceUnitNames() {
        return this.puNames.toArray(new String[0]);
    }
    
    @Override
    public Class [] getEntityClasses(String persistenceUnitName) {
        
        int index = this.puNames.indexOf(persistenceUnitName);
        
        if(index == -1) {
            throw new IllegalArgumentException("Unexpected persistence unit: "+persistenceUnitName);
        }
        
        List<Class> puClasses = this.classes.get(index);
        
        return puClasses.toArray(new Class[0]);
    }
    
    @Override
    public Class getEntityClass(String database, String table) {
        Class entityClass = null;
        int index = this.indexOf(database);
        if(index != -1) {
            List<Class> dbClasses = this.classes.get(index);
            for(Class dbClass:dbClasses) {
                if(this.getTableName(dbClass).equals(table)) {
                    entityClass = dbClass;
                    break;
                }
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
        for(String puName:puNames) {
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
        
        int []  posOf = this.positionOf(aClass);
        
        if(posOf == null) {
            
            return null;
            
        }else{
            
            int index = posOf[0];

            if(index > -1) {
                return this.getDatabaseNameFromUrl(this.jdbcUrls.get(index));
            }else{
                return null;
            }
        }
    }
    
    @Override
    public String getTableName(Class aClass) {
        Table table = (Table)aClass.getAnnotation(Table.class);
XLogger.getInstance().log(Level.FINER, "Entity class: {0}, table annotation: {1}", this.getClass(), aClass, table);
        return table.name();
    }
    
    @Override
    public String getIdColumnName(Class aClass) {
        
        final int [] pos = this.positionOf(aClass);
        
        final int x = pos[0]; final int y = pos[1];
        
        String idColumnName = null;
        
        if(this.idColumnNames != null && x < this.idColumnNames.size()) {
            List<String> dbColNames = this.idColumnNames.get(x);
            if(dbColNames != null && y < dbColNames.size()) {
                idColumnName = dbColNames.get(y);
            }
        }

        if(idColumnName == null) {
            idColumnName = this.loadId(aClass, x, y);
        }
        
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
        ArrayList<String> cols = new ArrayList<>();
        Field [] fields = entityClass.getDeclaredFields();
        for(Field field:fields) {
            String col = null;
            Column colAnn = field.getAnnotation(Column.class);
            if(colAnn != null) {
                col = colAnn.name();
            }else{
                JoinColumn joinColAnn = field.getAnnotation(JoinColumn.class);
                if(joinColAnn != null) {
                    col = joinColAnn.name();
                }
            }
            if(col != null) {
                cols.add(col);
            }
        }
        return cols.toArray(new String[0]);
    }

    @Override
    public int [] getColumnDisplaySizes(Class entityClass) {
        try{
            // column_name=4, data_type=5, type_name=6, size=7, nullable=11  
            return this.fetchIntMetaData(entityClass, 7);
        }catch(SQLException e) {
            return null;
        }
    }
    
    @Override
    public int [] getNullables(Class entityClass) {
        
        final Field [] fields = entityClass.getDeclaredFields();
        
        final String [] columnNames = this.getColumnNames(entityClass);
        
        final int [] nullables = new int[columnNames.length];
        
        for(int i=0; i<nullables.length; i++) {
            
            Field field = this.getField(fields, columnNames[i]);
            
            nullables[i] = this.getNullable(entityClass, field);
        }
     
        return nullables;
    }
    
    private Field getField(Field [] fields, String columnName) {
        
        Field output = null;
        
        for(Field field:fields) {

            if(field.getName().equals(columnName)) {

                output = field;

                break;
            }
        }
        
        return output;
    }
        
    protected int getNullable(Class entityClass, Field field) {
        if(entityClass == null) {
            throw new NullPointerException();
        }
        int nullable;
        if(field != null) {
            Basic basic = field.getAnnotation(Basic.class);
            if(basic != null) {
                nullable = basic.optional() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
            }else{
                // Only OneToOne and ManyToOne may be optional (has the optional() method)
                OneToOne one2one = field.getAnnotation(OneToOne.class);
                if(one2one != null) {
                    nullable = one2one.optional() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
                }else{
                    ManyToOne many2one = field.getAnnotation(ManyToOne.class);
                    if(many2one != null) {
                        nullable = many2one.optional() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
                    }else{
                        nullable = ResultSetMetaData.columnNullableUnknown;
                    }
                }
            }
        }else{
            nullable = ResultSetMetaData.columnNullableUnknown;
        }
        return nullable;
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

        int [] pos = this.positionOf(aClass);
        
        if(pos == null || pos.length == 0) {
            throw new IllegalArgumentException("Unexpected class: "+aClass.getName());
        }

//System.out.println(this.getClass().getName()+"#getPersistenceUnitName(java.lang.Class). Class: "+aClass.getName()+", pos: "+(pos==null?null:Arrays.toString(pos)));
        
        int index = pos[0];
        if(index > -1) {
            return this.puNames.get(index);
        }else{
            return null;
        }
    }

    @Override
    public String getPersistenceUnitName(String database) {
        int index = this.indexOf(database);
        if(index > -1) {
            return this.puNames.get(index);
        }else{
            return null;
        }
    }

    public String getDatabaseName(String persistenceUnitName) {
        int index = puNames.indexOf(persistenceUnitName);
        if(index > -1) {
            return this.getDatabaseNameFromUrl(this.jdbcUrls.get(index));
        }else{
            return null;
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
                        !one2one.mappedBy().equals(referenceColumn)) {
                    output = null;
                }else{
                    output = field.getType();
                }
            }
        }else{
            if(referenceColumn != null && 
                    !one2many.mappedBy().equals(referenceColumn)) {
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
        
        return output;
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
        return output;
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

    private String loadId(Class aClass, final int x, final int y) {
        
        String idColumnName = null;
        
        Field [] fields = aClass.getDeclaredFields();
        String idFieldName = null;
        for(Field field:fields) {
            Id id = field.getAnnotation(Id.class);
            if(id != null) {
                Column column = field.getAnnotation(Column.class);
                idColumnName = column.name();
                idFieldName = field.getName();
                break;
            }
        }
//Logger.getLogger(this.getClass().getName()).log(Level.FINER, 
//        "Class: {0}, id field name: {1}, id column name: {2}", 
//        new Object[]{aClass, idFieldName, idColumnName});        
        if(idColumnName != null) {
            if(x != -1 && y != -1) {
                this.idFieldNames = this.update(x, y, this.idFieldNames, idFieldName);
                this.idColumnNames = this.update(x, y, this.idColumnNames, idColumnName);
            }
        }
        
        assert idColumnName != null;
        
        return idColumnName;
    }
    
    private List<List<String>> update(int x, int y, List<List<String>> toUpdate, String value) {
        if(toUpdate == null) {
            toUpdate = new ArrayList<>();
        }
        List<String> list = null;
        if(x < toUpdate.size()) {
            list = toUpdate.get(x);
        }
        if(list == null) {
            list = new ArrayList<>();
            this.fillAndSet(toUpdate, x, list);
        }
        this.fillAndSet(list, y, value);
        return toUpdate;
    }
    
    private void fillAndSet(List list, int index, Object e) {
        final int size = list.size();
        if(index >= size) {
            int n = (index - size) + 1;
            for(int i=0; i<n; i++) {
                list.add(null);
            }
        }
        list.set(index, e);
    }
    
    private int indexOf(String database) {
        int i = -1;
        for(String jdbcUrl:jdbcUrls) {
            ++i;
            if(this.getDatabaseNameFromUrl(jdbcUrl).equals(database)) {
                break;
            }
        }
        return i;
    }
    
    private String getDatabaseNameFromUrl(String jdbcUrl) {
        // Extract dbname from jdbc:mysql://localhost:3306/dbname
        //
        final int offset = jdbcUrl.lastIndexOf('/') + 1;
        return jdbcUrl.substring(offset);
    }
    
    private int[] positionOf(Class aClass) {
        int [] pos = {-1, -1};
        for(List<Class> list:this.classes) {
            ++pos[0];
            int y = list.indexOf(aClass);
            if(y != -1) {
                pos[1] = y;
                break;
            }
        }
        return pos[1] == -1 ? null : pos;
    }
    
    private boolean isCollectionType(Class type) {
        return type == java.util.Collection.class ||
                type == java.util.Set.class ||
                type == java.util.List.class;
    }

    private int [] fetchIntMetaData(Class entityClass, int resultSetDataIndex) throws SQLException {

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
            
            ResultSet dbColumns = dbMetaData.getColumns(null, databaseName, tableName, null);

            while(dbColumns.next()) {

                String columnName = dbColumns.getString(4); // 4 == COLUMN_NAME

                int data = dbColumns.getInt(resultSetDataIndex);

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
}
/**
 * 
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
package com.bc.jpa;

import com.bc.jpa.query.JPQLImpl;
import com.bc.jpa.query.JPQL;
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.JoinColumn;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;

/**
 * @(#)DefaultEntityController.java   08-Dec-2013 02:14:22
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @param <E> The type of the entity
 * @param <e> The type of the entity's key
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class DefaultEntityController<E, e> 
        extends BaseEntityController<E, e> 
        implements EntityController<E, e>, Serializable {
    
    private transient static final Logger logger = 
            Logger.getLogger(DefaultEntityController.class.getName());
    
    private Object resultType;
    
    private JPQL jpql;
    
    public DefaultEntityController() { 
        this.jpql = new JPQLImpl();
    }
    
    public DefaultEntityController(
            PersistenceMetaData puMeta, Class<E> entityClass) {
    
        super(puMeta, entityClass);
        
        this.jpql = new JPQLImpl(puMeta, entityClass);
    }
    
    @Override
    public Collection formatColumnNames(Collection columnNames) {
        if(columnNames != null && !columnNames.isEmpty()) {
            final String [] dbCols = this.getMetaData().getColumnNames(this.getEntityClass());
            final List output = new ArrayList(columnNames);
            output.retainAll(Arrays.asList(dbCols));
            columnNames = output;
        }
        return columnNames;
    }

    @Override
    public Map formatWhereParameters(Map where) {
        if(where != null && !where.isEmpty()) {
            final Map output = new LinkedHashMap(where);
            final String [] dbCols = this.getMetaData().getColumnNames(this.getEntityClass());
            output.keySet().retainAll(Arrays.asList(dbCols));
            where = output;
        }
        return where;
    }
    
    @Override
    public Map formatOrderBy(Map orderBy) {
        if(orderBy != null && !orderBy.isEmpty()) {
            final Map output = new LinkedHashMap(orderBy);
            final String [] dbCols = this.getMetaData().getColumnNames(this.getEntityClass());
            output.keySet().retainAll(Arrays.asList(dbCols));
            orderBy = output;
        }
        return orderBy;
    }
    
    /**
     * No longer supported, now throws UnsupportedOperationException
     * @deprecated 
     */
    @Override
    @Deprecated
    public int[] getGeneratedKeys() {
        throw new UnsupportedOperationException("Not supported");
    }
    /**
     * No longer supported, now throws UnsupportedOperationException
     * @deprecated 
     */
    @Override
    @Deprecated
    public void setDatabaseName(String databaseName) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    /**
     * @deprecated Rather use {@link #toList(java.util.List, java.lang.String, int)}
     */
    @Override
    @Deprecated
    public List toList(List<E> results, int columnIndex, int limit) {
        if(columnIndex < 0) {
            throw new IndexOutOfBoundsException(""+columnIndex);
        }
        if(results == null) {
            throw new NullPointerException();
        }
        if(limit < 0 || limit > results.size()) {
            limit = results.size();
        }
        List output = new ArrayList(limit);
        for(int row=0; row<limit; row++) {
            E entity = results.get(row);
            Map m = new LinkedHashMap(this.toMap(entity));
            if(columnIndex >= m.size()) {
                throw new IndexOutOfBoundsException("Index: "+columnIndex+", size: "+m.size());
            }
            Iterator iter = m.keySet().iterator();
            Object val = null;
            for(int col=0; iter.hasNext(); col++) {
                Object key = iter.next();
                if(col == columnIndex) {
                    val = m.get(key);
                    break;
                }
            }
            output.add(val);
        }
        return output;
    }

    @Override
    public List toList(List<E> results, String column, int limit) {
        if(results == null) {
            throw new NullPointerException();
        }
        if(limit < 0 || limit > results.size()) {
            limit = results.size();
        }
        List output = new ArrayList(limit);
        for(int row=0; row<limit; row++) {
            E entity = results.get(row);
            try{
                // Throws UnsupportedOperationException
                Object retVal = this.getValue(entity, column);
                output.add(retVal);
            }catch(IllegalArgumentException | UnsupportedOperationException e) {
                logger.log(Level.WARNING, null, e);
                break;
            }
        }
        return output;
    }

    @Override
    public List<Map<String, ?>> toMapList(List<E> results, int limit) {
        return toMapList(results, null, limit);
    }

    @Override
    public List<Map<String, ?>> toMapList(List<E> results, Collection<String> columnNames, int limit) {
        if(results == null) {
            throw new NullPointerException();
        }
        if(limit < 0 || limit > results.size()) {
            limit = results.size();
        }
        List<Map<String, ?>> output = new ArrayList<>(limit);
        for(int i=0; i<limit; i++) {
            E entity = results.get(i);
            Map m = this.toMap(entity);
            if(columnNames != null) {
                m.keySet().retainAll(columnNames);
            }
            output.add(m);
        }
        return output;
    }
    
    public List<E> toEntitiesList(List<Map> results, int limit) 
            throws EntityInstantiationException {
        if(results == null) {
            throw new NullPointerException();
        }
        if(limit < 0 || limit > results.size()) {
            limit = results.size();
        }
        ArrayList<E> output = new ArrayList<>(results.size());
        for(int i=0; i<limit; i++) {
            Map map = results.get(i);
            E entity = this.toEntity(map, true);
            output.add(entity);
        }
        return output;
    }
    
    @Override
    public int delete(String column, Object value) {
        Map params = Collections.singletonMap(column, value);
        return delete(params);
    }

    @Override
    public int delete(Map params) {
        
        int updateCount = 0;
        
        try{
// Not OK. we should be able to delete even if idColumn is not in parameters
//            this.destroy((e)params.get(this.getIdColumn()));
            
            // In this case, if parameters is null, all the records
            // in the database will be deleted
            if(params == null) {
                throw new NullPointerException();
            }
            
            StringBuilder queryBuff = new StringBuilder("DELETE FROM ");
            queryBuff.append(this.getEntityClass().getSimpleName()).append(" s");
            
            // This must come first
            params = this.formatWhereParameters(params);
            
            jpql.appendWhereClause("WHERE", params.keySet(), "s", "AND", queryBuff);
            
logger.log(Level.FINER, " Query: {0}", queryBuff);            
            EntityManager em = this.getEntityManager();

            try {

                TypedQuery<E> q = em.createQuery(queryBuff.toString(), this.getEntityClass());

                jpql.updateQuery(em, q, params, true);

                EntityTransaction t = em.getTransaction();
                
                try{
                    t.begin();

                    updateCount = q.executeUpdate();

                    t.commit();

                    return updateCount;
                    
                }finally{
                    if(t.isActive()) {
                        t.rollback();
                        updateCount = 0;
                    }
                }

            } finally {
                em.close();
            }
        }catch(Exception e) {
            logger.log(Level.WARNING, null, e);
        }
        
        return updateCount;
    }

    @Override
    public int deleteById(e id) {
        return delete(this.getIdColumn(), id);
    }

    @Override
    public int insert(Map params) {
        E entity = this.persist(params);
        return entity == null ? -1 : 1;
    }

    @Override
    public E persist(Map params) {
        try{
            E entity = this.toEntity(params, true);
            this.create(entity);
            return entity;
        }catch(Exception e) {
            logger.log(Level.WARNING, null, e);
            return null;
        }
    }
    
    @Override
    public E selectById(e id) {
        List<E> found = select(this.getIdColumnName(), id, -1, 1);
        return found == null || found.isEmpty() ? null : found.get(0);
    }
    
    @Override
    public List<E> select(String column, Object value, int offset, int limit) {
        try{
            Map parameters = Collections.singletonMap(column, value);
            return this.select(parameters, (Map)null, null, limit, offset);
        }catch(Exception e) {
            logger.log(Level.WARNING, null, e);
            return null;
        }
    }

    @Override
    public List<E> select(Map params, Map orderBy, int offset, int limit) {
        return selectColumns(null, params, orderBy, offset, limit);
    }

    @Override
    public List<E> select(String column, Object[] values) {
        return select(column, values, null, -1, -1);
    }

    @Override
    public List<E> select(String column, Object[] values, Map orderBy, int offset, int limit) {
    
        // This must come first
        orderBy = this.formatOrderBy(orderBy);
        
        final String selectQuery = jpql.getSelectQuery(null, column, values, orderBy);
logger.log(Level.FINER, "Query: {0}", selectQuery);        
        EntityManager em = this.getEntityManager();
        
        try {
            
            Query q = this.createQuery(em, selectQuery, false, true);

            jpql.updateQuery(em, q, column, values, true);
            
            if (offset > 0) {
                q.setFirstResult(offset);
            }

            if (limit > 0) {
                q.setMaxResults(limit);
            }
            
            List result = q.getResultList();
            
logger.log(Level.FINEST, "Before converting to result type, results: {0}", result==null?null:result.size());            
            result = this.convertToResultType(null, result);
logger.log(Level.FINEST, "After converting to result type, results: {0}", result==null?null:result.size());                        
            return result;
            
        } finally {
            em.close();
        }
    }

    @Override
    public List selectColumn(String selectCol, String column, Object value, int offset, int limit) {
        Map params = Collections.singletonMap(column, value);
        return this.selectColumn(selectCol, params, null, offset, limit);
    }

    @Override
    public List selectColumn(String selectCol, Map params, Map orderBy, int offset, int limit) {
        
        if(selectCol == null) {
            throw new NullPointerException();
        }
        
        List columns = new ArrayList(1);
        
        columns.add(selectCol);
        
        return this.selectColumns(columns, params, orderBy, offset, limit);
    }

    @Override
    public List selectColumns(
            Collection selectCols, Map whereClauseParameters, 
            Map orderBy, int offset, int limit) {
        
        return this.selectColumns(selectCols, whereClauseParameters, 
                "AND", orderBy, offset, limit);
    }
    
    private List selectColumns(
            Collection selectCols, Map whereClauseParameters, 
            String connector, Map orderBy, int offset, int limit) {
        
        // This must come first
        selectCols = this.formatColumnNames(selectCols);
        whereClauseParameters = this.formatWhereParameters(whereClauseParameters);
        orderBy = this.formatOrderBy(orderBy);
        
        String selectQuery = jpql.getSelectQuery(selectCols, whereClauseParameters == null ? null : whereClauseParameters.keySet(), connector, orderBy);
        
logger.log(Level.FINER, "Query: {0}", selectQuery);            
        
        EntityManager em = this.getEntityManager();
        
        try {
            
            boolean hasSelectCols = selectCols != null && !selectCols.isEmpty();
            
            Query q = this.createQuery(em, selectQuery, false, !hasSelectCols);
            
            jpql.updateQuery(em, q, whereClauseParameters, true);
            
            if (offset > 0) {
                q.setFirstResult(offset);
            }

            if (limit > 0) {
                q.setMaxResults(limit);
            }
            
            List result = q.getResultList(); 
            
            result = this.convertToResultType(selectCols, result);
            
            return result;
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public E selectFirst(String columnName, Object columnValue) {
        Map params = Collections.singletonMap(columnName, columnValue);
        return selectFirst(params);
    }

    @Override
    public E selectFirst(Map params) {
        List<E> records = this.select(params, null, -1, 1);
        if(records == null || records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }

    @Override
    public Object selectValue(String selectCol, String column, Object value) {
        List found = this.selectColumn(selectCol, column, value, -1, 1);
        if(found == null || found.isEmpty()) {
            return null;
        }else{
            return found.get(0); // Only 1 col and 1 row expected
        }
    }

    @Override
    public Object selectId(String column, Object value) {
        return selectValue(this.getIdColumn(), column, value);
    }

    @Override
    public int update(String searchCol, Object searchVal, String updateCol, Object updateVal) {
        List<E> edited = edit(searchCol, searchVal, updateCol, updateVal);
        return edited == null ? -1 : edited.size();
    }
    
    @Override
    public List<E> edit(String searchCol, Object searchVal, String updateCol, Object updateVal) {
        try{
            
            Map search = Collections.singletonMap(searchCol, searchVal);
            
            Map update = Collections.singletonMap(updateCol, updateVal);
            
            return this.edit("AND", search, update);
            
        }catch(Exception e) {
            logger.log(Level.WARNING, null, e);
            return null;
        }
    }

    @Override
    public int updateById(Object id, String col, Object val) {
        E entity = this.editById(id, col, val);
        return entity == null ? -1 : 1;
    }
    
    
    @Override
    public E editById(Object id, String col, Object val) {
        List<E> updated = edit(this.getIdColumn(), id, col, val);
        return updated == null || updated.isEmpty() ? null : updated.get(0);
    }

    @Override
    public long count(Map params) {
        
        StringBuilder queryBuff = new StringBuilder("SELECT COUNT(s) FROM ");
        queryBuff.append(this.getEntityClass().getSimpleName()).append(" s");

        // This must come first
        params = this.formatWhereParameters(params);
        
        jpql.appendWhereClause("WHERE", params.keySet(), "s", "AND", queryBuff);
        
logger.log(Level.FINER, "Query: {0}", queryBuff);            
        
        EntityManager em = this.getEntityManager();
        
        try {
            
            TypedQuery<E> q = em.createQuery(queryBuff.toString(), this.getEntityClass());
            
            jpql.updateQuery(em, q, params, true);
                
            return ((Long) q.getSingleResult()).intValue();
            
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<E> select(Map parameters, String connector) {

        return this.select(parameters, (Map)null, connector, -1, -1);
    }
    
    @Override
    public List<E> select(Map params, Map orderBy, String connector, 
            int maxResults, int firstResult) {

        return this.selectColumns(null, params, connector, orderBy, firstResult, maxResults);
    }

    @Override
    public int update(Map where, Map update) {
        List<E> edited = this.edit("AND", where, update);
        return edited == null ? -1 : edited.size();
    }
    
    @Override
    public int update(String connector, Map where, Map update) {
        List<E> edited = this.edit(connector, where, update);
        return edited == null ? -1 : edited.size();
    }
    
    @Override
    public List<E> edit(String connector, Map where, Map update) {
        try{
            
            List<E> found = this.select(where, null, connector, -1, -1);

            if(found == null || found.isEmpty()) {
                throw new NonexistentEntityException("Search used parameters: "+where);
            }
            
            List<E> output = null;
            for(E oldEntity:found) {
                // oldEntity is not managed
                E newEntity;
                if(true) {
//System.out.println("BEFORE Update. Row: "+new TreeMap(this.toMap(oldEntity)));
                    this.updateEntity(oldEntity, update, true);
//System.out.println(" AFTER Update. Row: "+new TreeMap(this.toMap(oldEntity)));
                    this.edit(oldEntity);
//System.out.println(" AFTER DbEdit. Row: "+new TreeMap(this.toMap(oldEntity)));
                    newEntity = oldEntity;
                }else{
                    // Due to the create, this logic should consume more memory
                    newEntity = this.toEntity(update, true);
                    this.setId(newEntity, this.getId(oldEntity));
                    this.edit(newEntity); 
                }
                if(output == null) {
                    output = new ArrayList<>();
                }
                output.add(newEntity);
            }
            
            return output;
            
        }catch(Exception e) {
            logger.log(Level.WARNING, null, e);
            return null;
        }
    }
    
    @Override
    public E toEntity(Map row, boolean convertCrossReferences) 
            throws EntityInstantiationException {
        
        E entity = this.newEntity();

        if(row != null) {
            this.updateEntity(entity, row, convertCrossReferences);
        }
        
        return entity;
    }
    
    @Override
    public Map toMap(E entity) {
    
        return toMap(entity, true);
    }
    
    @Override
    public Map toMap(E entity, boolean nullsAllowed) {    
        Method [] methods = this.getMethods();
        HashMap output = new HashMap(methods.length, 1.0f);
        toMap(entity, output, nullsAllowed);
        return output;
    }

    @Override
    public void toMap(E entity, Map map, boolean nullsAllowed) {
        
        if(entity==null) {
            throw new NullPointerException();
        }

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Entity class. From controller: {0}, From entity: {1}", 
        new Object[]{this.getEntityClass(), entity.getClass()});
        
        Method [] methods = this.getMethods();
        StringBuilder buff = new StringBuilder();
        for(Method method:methods) {
            buff.setLength(0);
            JpaUtil.appendColumnName(false, method, buff);
            String columnName = buff.length() == 0 ? null : buff.toString();
            if(columnName == null) {
                continue;
            }
            try{
                Object columnValue = method.invoke(entity);
                if(!nullsAllowed && columnValue == null) {
                    continue;
                }
                map.put(columnName, columnValue);
            }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Entity: ").append(entity);
                msg.append(", Method: ").append(method.getName());
                msg.append(", Column: ").append(columnName);
                logger.log(Level.WARNING, msg.toString(), e);
            }
        }
logger.log(Level.FINER, "Extracted: {0}", map.keySet());        
    }

    @Override
    public List toList(E entity) {
        
        if(entity == null) {
            throw new NullPointerException();
        }
        
if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Entity class. From controller: {0}, From entity: {1}", 
        new Object[]{this.getEntityClass(), entity.getClass()});
        
        Method [] methods = this.getMethods();
        
        List output = null;
        StringBuilder buff = new StringBuilder();
        for(Method method:methods) {
            buff.setLength(0);
            // We have to use this method to check that the columnName is accepted
            JpaUtil.appendColumnName(false, method, buff);
            String columnName = buff.length() == 0 ? null : buff.toString();
            if(columnName == null) {
                continue;
            }
            try{
                Object columnValue = method.invoke(entity);
                output = new ArrayList(methods.length);
                output.add(columnValue);
            }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                StringBuilder msg = new StringBuilder();
                msg.append("Entity: ").append(entity);
                msg.append(", Method: ").append(method.getName());
                msg.append(", Column: ").append(columnName);
                logger.log(Level.WARNING, msg.toString(), e);
                
            }
        }
logger.log(Level.FINER, "Extracted: {0}", output==null?null:output.size());        
        return output;
    }
    
    /**
     * @return The number of fields that were successfully updated
     */
    @Override
    public int updateEntity(E entity, Map row, boolean convertCrossReferences) 
            throws EntityInstantiationException {
        
        int updateCount = 0;

if(logger.isLoggable(Level.FINER))        
logger.log(Level.FINER, "Updating entity: {0} with values {1}", new Object[]{entity, row});                
        
        try{
            
            Map<JoinColumn, Field> joinColumns;
            if(convertCrossReferences && !row.isEmpty()) {
                joinColumns = this.getMetaData().getJoinColumns(this.getEntityClass());
            }else{
                joinColumns = null;
            }
            
            for(Entry entry:(Set<Entry>)row.entrySet()) {
                String col = entry.getKey().toString();
                Object val = entry.getValue();
                if(convertCrossReferences && (joinColumns != null && !joinColumns.isEmpty())) {
                    Object ref = JpaUtil.getReference(this.getEntityManager(), this.getEntityClass(), joinColumns, col, val);
                    if(ref != null) {
                        val = ref;
                    }
                }
                try{
                    this.setValue(entity, col, val);
                    ++updateCount;
                }catch(IllegalArgumentException ignored) { }
            }
        }catch(Exception e) {
            throw new EntityInstantiationException(e);
        }
        return updateCount;
    }

    protected Query createQuery(EntityManager em, String queryString, 
            boolean nativeQuery, boolean all) {
        
        Query q;
        if(this.resultType == null && all) {
            if(nativeQuery) {
                q = em.createNativeQuery(queryString, this.getEntityClass());
            }else{
                q = em.createQuery(queryString, this.getEntityClass());
            }
        }else{
            if(nativeQuery) {
                q = em.createNativeQuery(queryString);
            }else{
                q = em.createQuery(queryString);
            }
        }
        
        if(this.resultType != null && nativeQuery) {
            q.setHint(QueryHints.RESULT_TYPE, this.resultType);
        }
      
        return q;
    }

    /**
     * Lets say we have a reference table named <tt>role</tt> as shown below: 
     * <pre>
     * ----------------
     *  roleid | role
     * ----------------
     *  1      | admin
     * ----------------
     *  2      | user
     * ----------------
     *  3      | guest
     * ----------------
     * </pre>
     * <br/>And given the referencing table named <tt>userroles</tt> with definition below:
     * <pre>
     * create table userroles(
     *   userroleid INTEGER(8) AUTO_INCREMENT not null primary key,
     *   userid INTEGER(8) not null UNIQUE,
     *   role SHORT(2) not null,
     *   FOREIGN KEY (role) REFERENCES role(roleid)
     * )ENGINE=INNODB;
     * </pre>
     * Calling this method with arguments <tt>role</tt> and <tt>2</tt> respectively 
     * will return the <tt>Role</tt> entity with the specified id.
     * <br/><br/>
     * The method returns null if the arguments have no matching reference.
     */
    @Override
    public Object getReference(String col, Object val) {
        
        Class referencingClass = this.getEntityClass();
        
        Map<JoinColumn, Field> joinColumns = this.getMetaData().getJoinColumns(referencingClass);
        
        EntityManager em = this.getEntityManager();
        
        Object ref;
        if(joinColumns == null) {
            ref = null;
        }else{
            ref = JpaUtil.getReference(em, referencingClass, joinColumns, col, val);
        }
        
        return ref;
    }
    
    private E newEntity() throws EntityInstantiationException {
        E entity;
        try{
            entity = this.getEntityClass().getConstructor().newInstance();
        }catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new EntityInstantiationException(e);
        }
        return entity;
    }

    private List convertToResultType(Collection columns, List input) {
        if(input == null || input.isEmpty() || this.getResultType() == null) {
            return input;
        }
        ArrayList output = new ArrayList(input.size());
        if(ResultType.Map.equals(this.getResultType())) {
            final boolean hasColumns = columns != null && !columns.isEmpty();
            boolean doneIsArray = false;
            boolean isArray = false;
            for(Object oval:input) {
                Map row;
                if(hasColumns) {
                    row = new HashMap(columns.size(), 1.0f);
                    if(!doneIsArray) {
                        doneIsArray = true;
                        isArray = oval instanceof Object[];
                    }
                    if(isArray) {
                        Object [] arr = (Object[])oval;
                        assert columns.size() == arr.length : 
                        ("Size mismatch. Length of query results "+arr.length+
                        " does not match length of columns specified in query: "+columns.size());
                        Iterator iter = columns.iterator();
                        for(int i=0; iter.hasNext(); i++) {
                            row.put(iter.next(), arr[i]);
                        }
                    }else{
                        assert columns.size() == 1 : 
                        ("Size mismatch. Length of query results 1"+
                        " does not match length of columns specified in query: "+columns.size());
                        row.put(columns.iterator().next(), oval);
                    }
                }else {
                    row = this.toMap((E)oval);
                }
                output.add(row);
            }
        }else if(ResultType.DEFAULT.equals(this.getResultType()) || ResultType.Array.equals(this.getResultType())) {    
            final boolean hasColumns = columns != null && !columns.isEmpty();
            boolean doneIsArray = false;
            boolean isArray = false;
            for(Object oval:input) {
                Object [] row;
                if(hasColumns) {
                    row = new Object[columns.size()];
                    if(!doneIsArray) {
                        doneIsArray = true;
                        isArray = oval instanceof Object[];
                    }
                    if(isArray) {
                        row = (Object[])oval;
                    }else{
                        assert columns.size() == 1 : 
                        ("Size mismatch. Length of query results 1"+
                        " does not match length of columns specified in query: "+columns.size());
                        row[0] = oval;
                    }
                }else {
                    List list = this.toList((E)oval);
                    row = list.toArray();
                }
                output.add(row);
            }
        }else{
            throw new UnsupportedOperationException("Unsupported "+
            org.eclipse.persistence.config.ResultType.class.getName()
                    +": "+this.getResultType());
        }
        return output;
    }
    
    private String getIdColumn() {
        return this.getMetaData().getIdColumnName(this.getEntityClass());
    }

    @Override
    public JPQL getJpql() {
        return jpql;
    }

    @Override
    public void setJpql(JPQL jpql) {
        this.jpql = jpql;
    }

    @Override
    public void setEntityClass(Class<E> aClass) {
        super.setEntityClass(aClass);
        jpql.setEntityClass(aClass);
    }
    
    @Override
    public void setMetaData(PersistenceMetaData entityMetaData) {
        super.setMetaData(entityMetaData);
        jpql.setMetaData(entityMetaData);
    }
    
    @Override
    public boolean isSearchNulls() {
        return jpql.isSearchNulls();
    }

    @Override
    public void setSearchNulls(boolean searchNulls) {
        this.jpql.setSearchNulls(searchNulls);
    }

    @Override
    public Object getResultType() {
        return resultType;
    }

    @Override
    public void setResultType(Object resultType) {
        this.resultType = resultType;
    }
}
/**
 * 
    private Object convertCrossReferencesOld(String col, Object val) {
        
        if(val == null) {
            return val;
        }
        
        String crossRefColumn = metaData.getReferenceColumn(this.getEntityClass());
        
//System.out.println("Type: "+this.getEntityClass().getName()+", cross reference column: "+crossRefColumn);
        if(col.equals(crossRefColumn)) {
            
            Class refType = metaData.getReferenceClasses(this.getEntityClass());
            
//System.out.println("Reference class for "+this.getTableName()+"."+col+" of "+val+": "+refType);                

            if(!val.getClass().equals(refType)) {

                if(refType != null) {
                    Object ref = this.getEntityManager().getReference(refType, val);
//System.out.println("Reference object for "+this.getTableName()+"."+col+" of "+val+": "+ref);                
                    val = ref;
                }else{
//System.out.println("Reference object for "+this.getTableName()+"."+col+" of "+val+" is null");                
                }
            }
        }
        
        return val;
    }

 * 
 */
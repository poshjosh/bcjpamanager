package com.bc.jpa.controller;

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.exceptions.EntityInstantiationException;
import com.bc.jpa.exceptions.NonexistentEntityException;
import java.io.Serializable;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;
import com.bc.util.XLogger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.bc.jpa.exceptions.IllegalOrphanException;
import com.bc.jpa.exceptions.PreexistingEntityException;
import com.bc.jpa.util.MapBuilderForEntity;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import com.bc.jpa.dao.Select;

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
public class EntityControllerImpl<E, e> 
        extends EntityUpdaterDeprecated<E, e> 
        implements EntityController<E, e>, Serializable {
    
    private transient static final Logger logger = 
            Logger.getLogger(EntityControllerImpl.class.getName());
    
    private Object resultType;
    
    private final QueryStringBuilder qsb;
    
    public EntityControllerImpl(
            JpaContext jpaContext, Class<E> entityClass) {
    
        super(jpaContext, entityClass);
        
        this.qsb = new QueryStringBuilderImpl(jpaContext, entityClass);
    }
    
    @Override
    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
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
            E entity = this.create(map, true);
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
    public int delete(EntityManager em, String column, Object value) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        Class<E> entityClass = this.getEntityClass();

        CriteriaDelete<E> delete = cb.createCriteriaDelete(entityClass);

        Root<E> entity = delete.from(entityClass);

        Predicate where = cb.equal(entity.get(column), value); 

        delete.where(where);

        Query query = em.createQuery(delete); 

        return query.executeUpdate();
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
            
            qsb.appendWhereClause("WHERE", params.keySet(), "s", "AND", queryBuff);
            
logger.log(Level.FINER, " Query: {0}", queryBuff);            
            EntityManager em = this.getEntityManager();

            try {

                TypedQuery<E> q = em.createQuery(queryBuff.toString(), this.getEntityClass());

                qsb.updateQuery(em, q, params, true);

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
            E entity = this.create(params, true);
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
        
        try(Select<E> qb = this.getJpaContext().getDaoForSelect(this.getEntityClass())) {
            
            qb.from(this.getEntityClass()).where(column, values);
            
            if(orderBy != null && !orderBy.isEmpty()) {
                
                qb.orderBy(orderBy);
            }
            
            TypedQuery<E> tq = qb.createQuery();
            
            if(offset > -1) {
                
                tq.setFirstResult(offset);
            }
            
            if(limit > -1) {
                
                tq.setMaxResults(limit);
            }
            
            List result = tq.getResultList();
            
logger.log(Level.FINEST, "Before converting to result type, results: {0}", result==null?null:result.size());            
            result = this.convertToResultType(null, result);
logger.log(Level.FINEST, "After converting to result type, results: {0}", result==null?null:result.size());                        
            return result;
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
            Collection selectCols, Map where, 
            String connector, Map orderBy, int offset, int limit) {
        
        // This must come first
        selectCols = this.formatColumnNames(selectCols);
        where = this.formatWhereParameters(where);
        orderBy = this.formatOrderBy(orderBy);
        
        try(Select<E> qb = this.getJpaContext().getDaoForSelect(this.getEntityClass())) {
            
            qb.from(this.getEntityClass());
            
            if(selectCols != null && !selectCols.isEmpty()) {
             
                qb.select(selectCols);
            }
            
            if(where != null && !where.isEmpty()) {
                
                Iterator iter = where.keySet().iterator();
                
                while(iter.hasNext()) {
                    Object key = iter.next();
                    Object val = where.get(key);
                    if(iter.hasNext()) {
                        if("AND".equalsIgnoreCase(connector)) {
                            qb.where(key.toString(), Select.EQ, val, Select.AND);
                        }else if("OR".equalsIgnoreCase(connector)) {
                            qb.where(key.toString(), Select.EQ, val, Select.OR);
                        }else{
                            throw new UnsupportedOperationException("Expected 'OR' or 'AND', found: "+connector);
                        }
                    }else{
                        qb.where(key.toString(), Select.EQ, val);
                    }
                }
            }
            
            if(orderBy != null && !orderBy.isEmpty()) {
                
                qb.orderBy(orderBy);
            }
            
            TypedQuery<E> tq = qb.createQuery(); 
            
            if (offset > 0) {
                tq.setFirstResult(offset);
            }

            if (limit > 0) {
                tq.setMaxResults(limit);
            }
            
            List result = tq.getResultList(); 
            
            result = this.convertToResultType(selectCols, result);
            
            return result;
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
        
        int updateCount = -1;
        
        EntityManager em = this.getJpaContext().getEntityManager(this.getEntityClass());
        
        try{
            
            try{

                em.getTransaction().begin();

                updateCount = this.update(em, searchCol, searchVal, updateCol, updateVal);

                em.getTransaction().commit();

            }finally{

                if(em.getTransaction().isActive()) {

                    em.getTransaction().rollback();
                }
            }
        }finally{
            em.close();
        }
        
        return updateCount;
    }
    
    @Override
    public int update(String whereColumn, Set whereValues, String updateColumn, Object updateValue) {
         
        int updateCount = 0;
        
        EntityManager em = this.getJpaContext().getEntityManager(this.getEntityClass());
        
        try{
            try{
                
                em.getTransaction().begin();
                
                for(Object oldValue:whereValues) { 
                    
                    updateCount += this.update(em, 
                            whereColumn, oldValue, 
                            updateColumn, updateValue);
                }
                
                em.getTransaction().commit();
                
            }finally{
                if(em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }finally{
            em.close();
        }
        
XLogger.getInstance().log(Level.FINE, "{0} / {1} success rate for edit operation", 
        this.getClass(), updateCount, whereValues.size());

        return updateCount;
    }
    
    @Override
    public int update(EntityManager em, String oldColumn, Object oldValue, String newColumn, Object newValue) {
        
        Class<E> entityClass = this.getEntityClass();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaUpdate<E> update = cb.createCriteriaUpdate(entityClass);

        Root<E> entity = update.from(entityClass);

        update.set(newColumn, newValue);

        Predicate where = cb.equal(entity.get(oldColumn), oldValue); 

        update.where(where);

        Query query = em.createQuery(update); 

        return query.executeUpdate();
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
        
        qsb.appendWhereClause("WHERE", params.keySet(), "s", "AND", queryBuff);
        
logger.log(Level.FINER, "Query: {0}", queryBuff);            
        
        EntityManager em = this.getEntityManager();
        
        try {
            
            TypedQuery<E> q = em.createQuery(queryBuff.toString(), this.getEntityClass());
            
            qsb.updateQuery(em, q, params, true);
                
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
            
            final EntityUpdater entityUpdater = new EntityUpdaterDeprecated(this.getJpaContext(), this.getEntityClass());
            
            List<E> output = null;
            for(E oldEntity:found) {
                // oldEntity is not managed
                E newEntity;
                if(true) {
//System.out.println("BEFORE Update. Row: "+new TreeMap(this.toMap(oldEntity)));
                    entityUpdater.update(oldEntity, update, true);
//System.out.println(" AFTER Update. Row: "+new TreeMap(this.toMap(oldEntity)));
                    this.edit(oldEntity);
//System.out.println(" AFTER DbEdit. Row: "+new TreeMap(this.toMap(oldEntity)));
                    newEntity = oldEntity;
                    
                }else{
                    
                    // Due to the create, this logic should consume more memory
                    newEntity = this.create(update, true);
                    
                    final e ID = Objects.requireNonNull(this.getId(oldEntity),
                            "ID is required but found null value for entity of type: "+oldEntity.getClass().getName()+", retrieved from database using parameters: "+where);
                    
                    this.setId(newEntity, ID);
                    
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
        new MapBuilderForEntity()
                .nullsAllowed(nullsAllowed)
                .maxDepth(1)
                .maxCollectionSize(10)
                .sourceType(this.getEntityClass())
                .source(entity)
                .target(map)
                .build();
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
     * @see com.bc.jpa.JpaContext#getReference(java.lang.Class, java.lang.String, java.lang.Object) 
     * @param col
     * @param val
     * @return 
     */
    @Override
    public Object getReference(String col, Object val) {
        return this.getJpaContext().getEntityReference().getReference(
                this.getEntityManager(), this.getEntityClass(), col, val);
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
                    Map map = this.toMap((E)oval, true);
                    row = new ArrayList(map.values()).toArray(new Object[0]);
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
    public Object getResultType() {
        return resultType;
    }

    @Override
    public void setResultType(Object resultType) {
        this.resultType = resultType;
    }

//////////////////////////////////////////////////////////////////////////
///////////// Methods from previous class EntityControllerBase////////////  
    
    @Override
    public int executeUpdate(String query) {
        int updateCount = 0;
        EntityManager em = this.getEntityManager();
        try{
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                Query q = em.createNativeQuery(query);
                updateCount = q.executeUpdate();
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    updateCount = 0;
                }
            }
        }finally{
            em.close();
        }
        return updateCount;
    }
    
    @Override
    public List executeQuery(String query) {
        return executeQuery(query, null, null);
    }

    @Override
    public List executeQuery(String query, String hintKey, Object hintValue) {
        EntityManager em = this.getEntityManager();
        try{
            
            Query q = em.createNativeQuery(query);
            
            if(hintKey != null) {
                q.setHint(hintKey, hintValue);
            }
            
            return q.getResultList();
            
        }finally{
            em.close();
        }
    }

    @Override
    public int create(Collection<E> entities) {
        int created = 0;
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                for(E entity:entities) {
                    try{
                        em.persist(entity); 
                        ++created;
                    }catch(Exception e) {
                        XLogger.getInstance().log(Level.WARNING, "Exception creating entity of type: "+entity.getClass().getName(), this.getClass(), e);
                        break;
                    }
                }
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    created = 0;
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return created;
    }

    @Override
    public void create(E entity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                em.persist(entity);
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public int destroy(Collection<e> ids) {
        int destroyed = 0;
        EntityManager em = getEntityManager();
        try {
            EntityTransaction t = em.getTransaction();
            try{
                
                t.begin();
                
                for(e id:ids){
                    E entity;
                    try {
                        entity = (E)em.getReference(this.getEntityClass(), id);
                    } catch (EntityNotFoundException enfe) {
                        XLogger.getInstance().log(Level.WARNING, "The "+this.getEntityClass()+" with id " + id + " no longer exists.", this.getClass(), enfe);
                        break;
                    }
                    em.remove(entity);
                    ++destroyed;
                }
                
                t.commit();
                
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    destroyed = 0;
                }
            }
        } finally {
            em.close();
        }
        return destroyed;
    }
    
    @Override
    public void destroy(e id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                
                E entity;
                try {
                    entity = (E)em.getReference(this.getEntityClass(), id);
                } catch (EntityNotFoundException enfe) {
                    throw new NonexistentEntityException(
                    "The "+this.getEntityClass()+" with id " + id + " no longer exists.", enfe);
                }
                
                em.remove(entity);
                
                t.commit();
                
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public int edit(Collection<E> entities) {
        int updated = 0;
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                for(E entity:entities) {
                    try{
                        entity = em.merge(entity);
                        ++updated;
                    } catch (Exception ex) {

                        String msg = ex.getLocalizedMessage();

                        if (msg == null || msg.length() == 0) {

                            try{

                                e id = this.getId(entity);

                                if(id != null) {
                                    if (find(id) == null) {
                                        XLogger.getInstance().log(Level.WARNING,
                                        "The "+this.getEntityClass()+" with id " + id + " no longer exists.",
                                        this.getClass(), ex);
                                    }
                                }

                            }catch(UnsupportedOperationException e) {
                                XLogger.getInstance().log(Level.WARNING, null, this.getClass(), e);
                            }
                        }

                        break;
                    }    
                }
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                    updated = 0;
                }
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return updated;
    }
    
    @Override
    public void edit(E entity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            EntityTransaction t = em.getTransaction();
            try{
                t.begin();
                entity = em.merge(entity); 
                t.commit();
            }finally{
                if(t.isActive()) {
                    t.rollback();
                }
            }
        } catch (Exception ex) {
            
            String msg = ex.getLocalizedMessage();
            
            if (msg == null || msg.length() == 0) {
                
                try{
                    
                    e id = this.getId(entity);
            
                    if(id != null) {
                        if (find(id) == null) {
                            throw new NonexistentEntityException(
                            "The "+this.getEntityClass()+" with id " + id + " no longer exists.");
                        }
                    }
                    
                }catch(UnsupportedOperationException e) {
                    XLogger.getInstance().log(Level.WARNING, null, this.getClass(), e);
                }
            }
            
            throw ex;
            
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     * @param id
     * @return 
     */
    @Override
    public E find(e id) {
        EntityManager em = getEntityManager();
        try {
            return (E)em.find(this.getEntityClass(), id);
        } finally {
            em.close();
        }
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     * @return 
     */
    @Override
    public List<E> find() {
        return find(true, -1, -1);
    }

    /**
     * The value of the {@link #resultType} variable has no effect on 
     * this method's logic.
     * @param maxResults
     * @param firstResult
     * @return 
     */
    @Override
    public List<E> find(int maxResults, int firstResult) {
        return find(false, maxResults, firstResult);
    }

    private List<E> find(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(this.getEntityClass()));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<E> rt = cq.from(this.getEntityClass());
            cq.select(cb.count(rt));
            Query q = em.createQuery(cq);
            return ((Long)q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    @Override
    public String getDatabaseName() {
        return this.getMetaData().getDatabaseName(this.getEntityClass());
    }

    @Override
    public String getTableName() {
        return this.getMetaData().getTableName(this.getEntityClass());
    }
    
    @Override
    public String getIdColumnName() {
        return this.getMetaData().getIdColumnName(this.getEntityClass());
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
package com.bc.jpa;

import com.bc.jpa.JpaContext;
import com.bc.jpa.PersistenceMetaData;
import com.bc.util.XLogger;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.Query;

/**
 * @(#)JPQLImpl.java   27-Jun-2014 17:18:14
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 * @param <E>
 */
public class QueryStringBuilderImpl<E> implements QueryStringBuilder<E>, Serializable {
    
    private boolean searchNulls = false;
    
    private boolean useColumnNamesToSelectAll;
    
    private String comparisonOperator = "=";
    
    private final JpaContext jpaContext;
    
    private Class<E> entityClass;
    
    public QueryStringBuilderImpl(JpaContext jpaContext, Class<E> entityClass) {
        this.jpaContext = jpaContext;
        this.entityClass = entityClass;
    }
    
    /**
     * Updates the query with the parameters contained in the <tt>whereClauseParameters</tt>
     * @param em The EntityManager
     * @param q The query whose parameters will be updated
     * @param whereClauseParameters The parameter_name=parameter_value pairs to use in updating the query
     * @param convertCrossReferences If true cross references will be converted to their entity values 
     */
    @Override
    public void updateQuery(EntityManager em, Query q, Map whereClauseParameters, boolean convertCrossReferences) {

XLogger.getInstance().log(Level.FINER, "Updating Query with: {0}", this.getClass(), whereClauseParameters);        

        if(whereClauseParameters == null || whereClauseParameters.isEmpty()) {
            return;
        }

        Map<JoinColumn, Field> joinColumns;
        if(convertCrossReferences && !whereClauseParameters.isEmpty()) {
            joinColumns = this.getMetaData().getJoinColumns(this.getEntityClass());
        }else{
            joinColumns = null;
        }
        
        final boolean convertPossible = (convertCrossReferences && (joinColumns != null && !joinColumns.isEmpty()));
        
        for(Object key:whereClauseParameters.keySet()) {
            
            String col = key.toString();
            
            Object val = whereClauseParameters.get(key); 

            if(convertPossible) {
            
                Object ref = jpaContext.getReference(em, this.entityClass, joinColumns, col, val);

                if(ref != null) {
                    val = ref;
                }
            }

            q.setParameter(col, val);
        }
    }
    
    /**
     * Updates the query with the values for the column specified
     * @param em The EntityManager
     * @param q The query whose parameters will be updated
     * @param col The column whose values will be updated
     * @param values The column values to be updated
     * @param convertCrossReferences If true cross references will be converted to their entity values 
     */
    @Override
    public void updateQuery(EntityManager em, Query q, String col, 
            Object [] values, boolean convertCrossReferences) {

if(XLogger.getInstance().isLoggable(Level.FINER, this.getClass()))
XLogger.getInstance().log(Level.FINER, "Updating column: {0} with {1}", 
this.getClass(), col, (values==null?null:Arrays.toString(values)));                
    
        if(values == null) {
            throw new NullPointerException();
        }
        
        if(values.length == 0) {
            return;
        }

        Map<JoinColumn, Field> joinColumns;
        if(convertCrossReferences && values.length != 0) {
            joinColumns = this.getMetaData().getJoinColumns(this.getEntityClass());
        }else{
            joinColumns = null;
        }
        
        // @related Query Parameter Index
        // Since there is only one column with multiple values
        // We use      Query.setParameter(int, Object)
        // Rather than Query.setParameter(String, Object)
        //
        // Parameter starts at 1
        //
        for(int i=0; i<values.length; i++) {
            
            Object val = values[i];
            
            if(convertCrossReferences && (joinColumns != null && !joinColumns.isEmpty())) {

                Object ref = this.jpaContext.getReference(em, this.entityClass, joinColumns, col, val);

                if(ref != null) {
                    val = ref;
                }
            }
XLogger.getInstance().log(Level.FINER, "At {0} setting {1} to {2}", this.getClass(), i+1, col, val);            

            q.setParameter(i + 1, val);
        }
    }
    
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return jpaContext.getEntityManagerFactory(entityClass);
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
    public String getSelectQuery(
            Collection selectCols, Set whereColumnNames, 
            String connector, Map orderBy) {
        
        StringBuilder selectQueryBuff = new StringBuilder();
        
        String tableAlias = this.appendSelectQuery(selectCols, selectQueryBuff);

        this.appendWhereClause("WHERE", whereColumnNames, 
                tableAlias, connector, selectQueryBuff);
        
        this.appendOrderBy(orderBy, tableAlias, selectQueryBuff);

XLogger.getInstance().log(Level.FINE, "Query: {0}", this.getClass(), selectQueryBuff);

        return selectQueryBuff.toString();
    }
    
    @Override
    public String getSelectQuery(Collection selectColumns, String column, Object [] values, Map orderBy) {
        
        if(column == null || values == null) {
            throw new NullPointerException();
        }
        
        StringBuilder selectQueryBuff = new StringBuilder();
        
        String tableAlias = this.appendSelectQuery(selectColumns, selectQueryBuff);
        
        this.appendWhereClause("WHERE", column, values, tableAlias, "OR", selectQueryBuff);
        
        this.appendOrderBy(orderBy, tableAlias, selectQueryBuff);
        
XLogger.getInstance().log(Level.FINE, "Query: {0}", this.getClass(), selectQueryBuff);

        return selectQueryBuff.toString();
    }
    
    @Override
    public String getTableAlias() {
        final String simpleName = this.getEntityClass().getSimpleName();
        final String tableAlias = Character.toString(Character.toLowerCase(simpleName.charAt(0)));
        return tableAlias;
    }

    /**
     * @param selectCols
     * @param appendTo
     * @return The table alias used in the select query
     */
    @Override
    public String appendSelectQuery(Collection selectCols, StringBuilder appendTo) {
        
        appendTo.append("SELECT ");
        
        boolean hasSelectCols = selectCols != null && !selectCols.isEmpty();
        
        final String simpleName = this.getEntityClass().getSimpleName();
        final String tableAlias = Character.toString(Character.toLowerCase(simpleName.charAt(0)));
        
        if(!hasSelectCols) {
            if(this.isUseColumnNamesToSelectAll()) {
                String [] colsArr = this.getMetaData().getColumnNames(this.getEntityClass());
                Collection<String> columnNames = Arrays.asList(colsArr);
                this.appendColumns(columnNames, null, tableAlias, appendTo);
            }else{
                appendTo.append(tableAlias);
            }
        }else{
            this.appendColumns(selectCols, null, tableAlias, appendTo);
        }
        appendTo.append(" FROM ").append(simpleName).append(' ').append(tableAlias);
        
        return tableAlias;
    }
    
    @Override
    public void appendWhereClause(String WHERE, Set whereColumnNames, 
            String tableAlias, String connector, StringBuilder appendTo){
    
        if(whereColumnNames == null || whereColumnNames.isEmpty()) {
            return;
        }

        if(WHERE != null) {
            appendTo.append(' ').append(WHERE).append(' ');
        }    
        
        // Input Map with only one entry does not need a connector
        // Connector may be null at this point
        //
        if(connector != null) {
            connector = connector.toUpperCase();
        }
        
        Iterator iter = whereColumnNames.iterator();
        while(iter.hasNext()) {
            
            String col = iter.next().toString();
            
            // Format  s.id = : id;
            this.appendWherePair(col, tableAlias, appendTo);
            
            if(iter.hasNext()) {
                
                // Connector may not be null at this point
                if(connector == null) {
                    throw new NullPointerException();
                }
                
                appendTo.append(' ').append(connector).append(' ');
            }
        }
    }
    
    @Override
    public void appendWhereClause(String WHERE, String column, Object[] values, 
            String tableAlias, String connector, StringBuilder appendTo){
        
        if(values.length > 0) {
        if(WHERE != null) {
            appendTo.append(' ').append(WHERE).append(' ');
        }    
        }else{
            return;
        }
        
        // Input Map with only one entry does not need a connector
        // Connector may be null at this point
        //
        if(connector != null) {
            connector = connector.toUpperCase();
        }
        
        for(int i=0; i<values.length; i++) {
            
            // @related Query Parameter Index
            // Since there is only one column with multiple values
            // we use column index rather than column name
            //
            // Parameter starts at 1
            //
            this.appendWherePair(column, i+1, tableAlias, appendTo);
            
            if(i < values.length-1) {
                
                // Connector may not be null at this point
                if(connector == null) {
                    throw new NullPointerException();
                }
                
                appendTo.append(' ').append(connector).append(' ');
            }
        }
    }

    @Override
    public void appendWherePair(String col, String tableAlias, StringBuilder appendTo){

        if(this.searchNulls) {
            appendTo.append('(');
        }
        
        // FORMAT
        // In this case, s = table alias
        //
        // Where searchNulls is not true
        // s.id = :id
        //
        // Where searchNulls is true
        // (s.id = :id OR s.id IS NULL)
        //
        appendTo.append(tableAlias).append('.').append(col).append(' ').append(comparisonOperator).append(" :").append(col);
        
        if(this.searchNulls) {
            appendTo.append(" OR ").append(tableAlias).append('.').append(col).append(" IS NULL)");
        }
    }

    @Override
    public void appendWherePair(String columnName, int columnIndex, String tableAlias, StringBuilder appendTo){

        if(this.searchNulls) {
            appendTo.append('(');
        }
        
        // FORMAT
        // In this case, s = table alias
        //
        // Where searchNulls is not true
        // s.columnName = ?columnIndex
        //
        // Where searchNulls is true
        // (s.columnName = ?columnIndex OR s.id IS NULL)
        //
        appendTo.append(tableAlias).append('.').append(columnName).append(' ').append(comparisonOperator).append(" ?").append(columnIndex);
        
        if(this.searchNulls) {
            appendTo.append(" OR ").append(tableAlias).append('.').append(columnName).append(" IS NULL)");
        }
    }
    
    @Override
    public void appendOrderBy(Map orderBy, String tableAlias, StringBuilder appendTo) {
        
        if(orderBy == null || orderBy.isEmpty()) {
            return;
        }
        
        appendTo.append(" ORDER BY ");
        
        this.appendColumns(orderBy.keySet(), orderBy.values(), tableAlias, appendTo);
    }

    @Override
    public void appendColumns(Collection columns, Collection values, String tableAlias, StringBuilder appendTo) {

        if(columns == null || columns.isEmpty()) {
            return;
        }
        
        char SPACE = ' ';
        
        // tableAlias.[column_0] [value_0], tableAlias.[column_1] [value_1], tableAlias.[column_2] [value_2
        // the values (i.e value_0, value_1 etc) are optional
        //
        Iterator colsIter = columns.iterator();
        Iterator valsIter = values == null ? null : values.iterator();
        
        while(colsIter.hasNext()) {
            
            Object col = colsIter.next();
            
            if(tableAlias != null) {
                appendTo.append(tableAlias).append('.');
            }
            
            appendTo.append(col);
            
            if(valsIter != null && valsIter.hasNext()) {
                Object val = valsIter.next();
                appendTo.append(SPACE).append(val);
            }
            if(colsIter.hasNext()) {
                appendTo.append(',').append(SPACE);
            }
        }
    }

    @Override
    public boolean isUseColumnNamesToSelectAll() {
        return useColumnNamesToSelectAll;
    }

    @Override
    public void setUseColumnNamesToSelectAll(boolean useColumnNamesToSelectAll) {
        this.useColumnNamesToSelectAll = useColumnNamesToSelectAll;
    }

    @Override
    public boolean isSearchNulls() {
        return searchNulls;
    }

    @Override
    public void setSearchNulls(boolean searchNulls) {
        this.searchNulls = searchNulls;
    }

    @Override
    public String getComparisonOperator() {
        return comparisonOperator;
    }

    @Override
    public void setComparisonOperator(String comparison) {
        this.comparisonOperator = comparison;
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public final PersistenceMetaData getMetaData() {
        return jpaContext.getMetaData();
    }
}
/**
 * 
 * 
//    @Override
    
    public List getResultList(
        Collection selection, Map where, 
        String connector, Map orderBy,
        int offset, int limit,
        boolean convertCrossReferences) {
        String query = this.getSelectQuery(selection, where == null ? null : where.keySet(), connector, orderBy);
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            Query tq = em.createQuery(query);
            this.updateQuery(em, tq, where, convertCrossReferences);
            if(offset > -1) {
                tq.setFirstResult(offset);
            }
            if(limit > -1) {
                tq.setMaxResults(limit);
            }
            return tq.getResultList();
        }finally{
            em.close();
        }
    }

//    @Override
    public List<E> getResultList(
        Map where, 
        String connector, Map orderBy,
        int offset, int limit,
        boolean convertCrossReferences) {
        String query = this.getSelectQuery(null, where==null?null:where.keySet(), connector, orderBy);
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            TypedQuery<E> tq = em.createQuery(query, this.getEntityClass());
            this.updateQuery(em, tq, where, convertCrossReferences);
            if(offset > -1) {
                tq.setFirstResult(offset);
            }
            if(limit > -1) {
                tq.setMaxResults(limit);
            }
            return tq.getResultList();
        }finally{
            em.close();
        }
    }
    
//    @Override
    public List getResultList(
        Collection selection,
        String column, Object [] values,
        Map orderBy, 
        int offset, int limit,
        boolean convertCrossReferences) {
        
        String query = this.getSelectQuery(selection, column, values, orderBy);
        
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            Query tq = em.createQuery(query);
            this.updateQuery(em, tq, column, values, convertCrossReferences);
            if(offset > -1) {
                tq.setFirstResult(offset);
            }
            if(limit > -1) {
                tq.setMaxResults(limit);
            }
            return tq.getResultList();
        }finally{
            em.close();
        }
    }

//    @Override
    public List<E> getResultList(
        String column, Object [] values,
        Map orderBy, 
        int offset, int limit,
        boolean convertCrossReferences) {
        
        String query = this.getSelectQuery(null, column, values, orderBy);
        
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            TypedQuery<E> tq = em.createQuery(query, this.getEntityClass());
            this.updateQuery(em, tq, column, values, convertCrossReferences);
            if(offset > -1) {
                tq.setFirstResult(offset);
            }
            if(limit > -1) {
                tq.setMaxResults(limit);
            }
            return tq.getResultList();
        }finally{
            em.close();
        }
    }

//    @Override
    public E getSingleResult(
        Map where, 
        String connector, Map orderBy, 
        boolean convertCrossReferences) {
        String query = this.getSelectQuery(null, where == null ? null : where.keySet(), connector, orderBy);
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            TypedQuery<E> tq = em.createQuery(query, this.getEntityClass());
            this.updateQuery(em, tq, where, convertCrossReferences);
            try{
                return tq.getSingleResult();
            }catch(NoResultException ignored) {
                return null;
            }
        }finally{
            em.close();
        }
    }
    
//    @Override
    public Object getSingleResult(
        String selectColumn, Map where, 
        String connector, Map orderBy, 
        boolean convertCrossReferences) {
        String query = this.getSelectQuery(Collections.singleton(selectColumn), where==null?null:where.keySet(), connector, orderBy);
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            Query tq = em.createQuery(query);
            this.updateQuery(em, tq, where, convertCrossReferences);
            try{
                return tq.getSingleResult();
            }catch(NoResultException ignored) {
                return null;
            }
        }finally{
            em.close();
        }
    }

//    @Override
    public E getSingleResult(
        String column, Object [] values,
        Map orderBy, boolean convertCrossReferences) {
        String query = this.getSelectQuery(null, column, values, orderBy);
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            TypedQuery<E> tq = em.createQuery(query, this.getEntityClass());
            this.updateQuery(em, tq, column, values, convertCrossReferences);
            try{
                return tq.getSingleResult();
            }catch(NoResultException ignored) {
                return null;
            }
        }finally{
            em.close();
        }
    }

//    @Override
    public Object getSingleResult(
        String selectColumn,
        String column, Object [] values,
        Map orderBy, boolean convertCrossReferences) {
        String query = this.getSelectQuery(Collections.singleton(selectColumn), column, values, orderBy);
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            Query tq = em.createQuery(query);
            this.updateQuery(em, tq, column, values, convertCrossReferences);
            try{
                return tq.getSingleResult();
            }catch(NoResultException ignored) {
                return null;
            }
        }finally{
            em.close();
        }
    }


    @Override
    public Number getSum(String columnToSelect, Map whereParameters) {
        
        return this.getSum(columnToSelect, whereParameters, Number.class);
    }
    
    @Override
    public <R extends Number> R getSum(
            String columnToSelect, Map whereParameters, Class<R> resultType) {
        
        EntityManager em = this.getEntityManagerFactory().createEntityManager();
        try{
            
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<R> cq = cb.createQuery(resultType);

            Root<E> root = cq.from(this.entityClass);

            Expression<R> sum = cb.sum(root.<R>get(columnToSelect));

            cq = cq.select(sum); 
            
            if(whereParameters != null && !whereParameters.isEmpty()) {

                List<Predicate> predicates = new LinkedList<>();

                for(Object entryObj:whereParameters.entrySet()) {

                    Map.Entry entry = (Map.Entry)entryObj;
                    
                    predicates.add(cb.equal(root.get(entry.getKey().toString()), entry.getValue())); 
                }
                
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            TypedQuery<R> tq = em.createQuery(cq); 

            return tq.getSingleResult();
            
        }finally{
            
            em.close();
        }
    }

 * 
 */
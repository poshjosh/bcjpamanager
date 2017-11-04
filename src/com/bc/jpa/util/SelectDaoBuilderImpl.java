/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa.util;

import com.bc.jpa.EntityUpdater;
import com.bc.jpa.metadata.JpaMetaData;
import com.bc.jpa.dao.SelectImpl;
import com.bc.jpa.dao.SelectDao;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.bc.jpa.dao.Select;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 16, 2017 3:28:12 PM
 */
public class SelectDaoBuilderImpl<T> extends SelectDaoBuilderAbstraction<T> {

    private static final Logger logger = Logger.getLogger(SelectDaoBuilderImpl.class.getName());

    private static final class SimpleMapEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private final V value;
        public SimpleMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public K getKey() { return this.key; }
        @Override
        public V getValue() { return this.value; }
        @Override
        public V setValue(V value) { throw new UnsupportedOperationException(); }
    }
    
    private final Set<Class> searchedTypes;
    
    private final List<Predicate> where;
        
    public SelectDaoBuilderImpl() { 
        searchedTypes = new HashSet();
        where = new ArrayList<>();
    }
    
    @Override
    public SelectDao<T> build() {
        
        this.checkBuildAttempted();
        
        final Class<T> resultType = this.getResultType();
        final String textToFind = this.getTextToFind();
        
        final boolean hasTextToFind = textToFind != null && !textToFind.isEmpty();
        
        final String query = !hasTextToFind ? null : '%'+textToFind+'%';
        
        final EntityManager em = this.getJpaContext().getEntityManager(resultType);

        final Select<T> dao = resultType == null ? 
                new SelectImpl(em) : new SelectImpl(em, resultType);
        
        final CriteriaBuilder cb = dao.getCriteriaBuilder();
        
        final CriteriaQuery<T> cq = dao.getCriteriaQuery();
        
        cq.distinct(true);
        
        final List<Predicate> likes = !hasTextToFind ? null : new ArrayList();
        
        final Root<T> root = cq.from(resultType); 
        
        this.search(cb, cq, resultType, root, likes, query);

        if(likes != null) {
            where.add(cb.or(likes.toArray(new Predicate[0])));
        }
        
        if(!where.isEmpty()) {
            cq.where( cb.and(where.toArray(new Predicate[0])) );
        }

        this.orderBy(cb, cq, resultType, root);

        return dao;
    }

    public void search(CriteriaBuilder cb, CriteriaQuery<T> cq, Class<T> type, 
            From<?, T> from, List<Predicate> likes, String query) {
        
        final Collection<Class> typesToSearch = this.getTypesToSearch();
        
        for(Class typeToSearch : typesToSearch) {

            if(!this.isEntityType(typeToSearch)) {
                continue;
            }
            
            final From x;
            
            if(typeToSearch.equals(type)) {
                
                x = from;
                
            }else{
                
                x = this.getJoin(type, from, typeToSearch, null);
            }
            
            this.doSearch(cb, cq, x.getJavaType(), x, likes, query);
        }
    }

    public void doSearch(
            CriteriaBuilder cb, CriteriaQuery<T> cq, Class<T> type, 
            From<?, T> from, List<Predicate> likes, String query) {
      
        if(searchedTypes.contains(type)) {
            return;
        }
        
        logger.log(Level.FINE, "Searching: {0}", type);

        searchedTypes.add(type);
        
        this.where(cb, cq, type, from, where);
        
        if(likes != null) {
            
            final JpaMetaData metaData = this.getJpaContext().getMetaData();
            
            final EntityUpdater updater = this.getJpaContext().getEntityUpdater(type);
            
            final String [] colNames = metaData.getColumnNames(type);
            
            for(String colName : colNames) {
                
                final Class colType = updater.getMethod(false, colName).getReturnType();
                
                if(this.isToSearch(colName, colType)) {
                    
                    final boolean isEntity = this.isEntityType(colType);
                    
                    if(!isEntity) {
                        
                        if(logger.isLoggable(Level.FINER)) {
                            logger.log(Level.FINER, "Searching: {0}#{1} {2} LIKE {3}",
                                    new Object[]{type, colType.getSimpleName(), colName, query});
                        }

                        final Path path = from.get(colName);  

                        final Predicate like = cb.like(path, query); 

                        likes.add(like);
                    }
                }
            }
        }
    }
    
    public Join getJoin(Class<T> type, From<?, T> from, Class colType, Join outputIfNone) {        
        
        Join join = null;

        Map.Entry<String, JoinType> joinEntry = this.getJoin(type, colType, null);

        if(joinEntry != null) {

            try{
                join = from.join(joinEntry.getKey(), joinEntry.getValue());
            }catch(IllegalArgumentException e) {
                joinEntry = this.getJoin(colType, type, null);
                join = from.join(joinEntry.getKey(), joinEntry.getValue());
            }
        }
        
        return join == null ? outputIfNone : join;
    }
    
    public Map.Entry<String, JoinType> getJoin(Class entityType, Class colType, 
            Map.Entry<String, JoinType> outputIfNone) {

        final Level level = Level.FINER;
        if(logger.isLoggable(level)) {
            logger.log(level, "{0} -> {1}", new Object[]{entityType.getName(), colType.getName()});
        }

        final JpaMetaData metaData = this.getJpaContext().getMetaData();
        
        final Map<Class, String> colRefings = metaData.getReferencing(colType);
        
        if(logger.isLoggable(level)) {
            logger.log(level, "Column type: {0}, referencings: {1}",
                    new Object[]{colType, colRefings});
        }
        
        final String innerConn = colRefings.get(entityType);
        
        final Class type;
        final String joinCol;
        final JoinType joinType;
        if(innerConn != null && Arrays.asList(metaData.getColumnNames(entityType)).contains(innerConn)) {
            type = entityType;
            joinCol = innerConn;
            joinType = JoinType.INNER;
        }else{

            final Map<Class, String> entityRefings = metaData.getReferencing(entityType);
            if(logger.isLoggable(level)) {
                logger.log(level, "Entity type: {0}, referencings: {1}",
                        new Object[]{entityType, entityRefings});
            }
            
            final String leftConn = entityRefings.get(colType);

            if(leftConn != null && Arrays.asList(metaData.getColumnNames(colType)).contains(leftConn)) {
                type = colType;
//                joinCol = leftConn;
                joinCol = colType.getSimpleName().toLowerCase() + "List";
                joinType = JoinType.LEFT;
            }else{
                type = null;
                joinCol = null;
                joinType = null;
            }
        }
        
        if(logger.isLoggable(level)) {
            logger.log(level, "{0}#{1}, {1}",
                    new Object[]{type==null?null:type.getName(), joinCol, joinType});
        }

        return joinCol == null || joinType == null ? outputIfNone : 
                new SelectDaoBuilderImpl.SimpleMapEntry<>(joinCol, joinType);
    }
    
    public boolean isEntityType(Class type) {
        return type.getAnnotation(Entity.class) != null;
    }

    public boolean isToSearch(String columnName, Class columnType) {
        final boolean maySearch = this.getTypesToSearch().contains(columnType);
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "May search: {0}, {1} {2}", 
                    new Object[]{maySearch, columnType.getSimpleName(), columnName});
        }
        return maySearch;
    }
    
    public void where(CriteriaBuilder cb, CriteriaQuery<T> cq, Class<T> type, From<?, T> from, List<Predicate> where) {
        final String key = null;
        final Object val = null;
        if(key != null && val != null) {
            where.add(cb.equal(from.get(key), val));
        }
    }
    
    public void orderBy(CriteriaBuilder cb, CriteriaQuery<T> cq, Class<T> type, From<?, T> from) {
        
        final String idCol = this.getJpaContext().getMetaData().getIdColumnName(type);
        
        cq.orderBy(cb.desc(from.get(idCol))); 
    }
    
    private boolean buildAttempted;
    protected void checkBuildAttempted() {
        if(this.buildAttempted) {
            throw new java.lang.IllegalStateException("build() method may only be called once");
        }
        this.buildAttempted = true;
    }
}

/**
 * @(#)Listings.java   08-Apr-2010 12:37:54
 *
 * Copyright 2009 BC Enterprise, Inc. All rights reserved.
 * BCE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.looseboxes.pu;

import java.util.logging.Logger;
import com.bc.jpa.fk.EnumReferences;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.bc.jpa.context.JpaContext;

/**
 * keys are columnNames and values are <tt>Maps</tt> whose keys are gotten
 * from {@link org.lb.children.tables.ProductCategories#keySet()}<br/>
 * <b><u>Some Examples</u>:</b><br/>
 * <code>
 * {offerType={sales=5000,lease=1000,letting=0}}
 * {productCategory={new=1000,used=3000,refurbished=2000,classic=0}}
 * {status={sold=500,available=5500}}
 * {propertyType={building-commercial=3000,building-residential=2000,land-commerical=1000}}
 * </code>
 * @param <T>
 * @author  chinomso bassey ikwuagwu
 * @version 1.0
 * @since   1.0
 */
public abstract class AbstractListings<T> implements Listings, Serializable {
    private transient static final Logger LOG = Logger.getLogger(AbstractListings.class.getName());

    private Map<String, Map<Enum, Integer>> values;
    
//    private Map<String, Class> referenceTableTypes;
    
    public AbstractListings() { 
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "<init>", this.getClass());
} 
        init();
    }
    
    public abstract Class<T> getEntityClass();

    public abstract JpaContext getControllerFactory();
    
    private void init() {

long mb4 = com.bc.util.Util.availableMemory();
long tb4 = System.currentTimeMillis();

        EntityManager em = null;

        try{
            
            JpaContext cf = this.getControllerFactory();
            
            EnumReferences refs = cf.getEnumReferences();
            
            Class [] enumTypes = refs.getEnumTypes();
            
            values = new HashMap<String, Map<Enum, Integer>>(enumTypes.length, 1.0f){
                @Override
                public Map<Enum, Integer> put(String key, Map<Enum, Integer> value) {
                    if(key == null || value == null) throw new NullPointerException();
                    return super.put(key, value);
                }
            };
            
//            this.referenceTableTypes = new HashMap(enumTypes.length, 1.0f);
            
            Map<JoinColumn, Field> joins = cf.getMetaData().getJoinColumns(this.getEntityClass());

            em = cf.getEntityManager(this.getEntityClass());

            em.getTransaction().begin();

            for(Class enumType:enumTypes) {

                this.init(em, refs, enumType, joins);
            }
            
            em.getTransaction().commit();
            
        }finally{
            
if(LOG.isLoggable(Level.FINE))            
LOG.log(Level.FINE, 
"Expended memory: {0}, time: {1} to load listings for entity type: {2}, listings:\n{3}",
new Object[]{mb4-com.bc.util.Util.usedMemory(mb4), 
System.currentTimeMillis()-tb4,  this.getEntityClass(), this.values});
            
            if(em != null) {
                em.close();
            }
        }
    }
    
    private void init(EntityManager em, EnumReferences refs, Class enumType, Map<JoinColumn, Field> joins) {
        
        Enum [] en_arr = refs.getValues(enumType);

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Entity type: {0}, reference type: {1}, Listing names: {2}",
new Object[]{ this.getEntityClass().getName(),  enumType,  Arrays.toString(en_arr)});
}

        Map<Enum, Integer> tableMap = null;

        Map<Enum, Object> entities = refs.getEntities(enumType);

        String joinColName = null;

        for(Enum en:en_arr) {

            // All the enums have the same joinColumn
            //
            if(joinColName == null) {

                String idColumn = refs.getIdColumnName(en);

                for(JoinColumn jc:joins.keySet()) {
                    if(jc.referencedColumnName().equals(idColumn)) {
                        joinColName = jc.name();
                        break;
                    }
                }
            }

            Object entity = entities.get(en);

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Reference type: {0}, enum: {1}, entity: {2}, join column: {3}",
new Object[]{ enumType,  en,  entity,  joinColName});
}

            if(joinColName == null) {
                break;
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(this.getEntityClass());
            cq.select(cb.count(root)); 
            cq.where(cb.equal(root.get(joinColName), entity));

            long count = em.createQuery(cq).getSingleResult();                    

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Adding count {0} = {1}",new Object[]{ en,  count});
}

            if(tableMap == null) {
                tableMap = new EnumMap(enumType);
            }

            tableMap.put(en, (int)count);
        }

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Entity type: {0}, reference type: {1}, listings: {2}",
new Object[]{ this.getEntityClass(),  enumType,  tableMap});
}

        if(tableMap != null && !tableMap.isEmpty()) {
            this.values.put(joinColName, tableMap);
//                    this.referenceTableTypes.put(joinColName, enumType);
        }
    }
    
//    public Class getReferenceType(String joinColumnName) {
//        return this.referenceTableTypes.get(joinColumnName);
//    }
    
    /**
     * @param joinColName The join ccolumn name (e.g <tt>availabilityid</tt> or <tt>productstatusid</tt>)
     * @param joinColValue Example for a join column name of <tt>availabilityid</tt> would be <tt>InStock</tt> or <tt>1</tt>
     * @return The updated values
     */
    @Override
    public Map<Enum, Integer> increment(String joinColName, Object joinColValue) {
        return this.update(true, joinColName, joinColValue);
    }
    
    @Override
    public Map<String, Map<Enum, Integer>> increment(Map params) {
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "BEFORE increment, Params: {0}\nListings: {1}",new Object[]{ params,  values});
}        
        update(true, params);
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "AFTER increment, Params: {0}\nListings: {1}",new Object[]{ params,  values});
}        
        return this.values;
    }

    /**
     * @param joinColName The join ccolumn name (e.g <tt>availabilityid</tt> or <tt>productstatusid</tt>)
     * @param joinColValue Example for a join column name of <tt>availabilityid</tt> would be <tt>InStock</tt> or <tt>1</tt>
     * @return The updated values
     */
    @Override
    public Map<Enum, Integer> decrement(String joinColName, Object joinColValue) {
        return this.update(false, joinColName, joinColValue);
    }
    
    @Override
    public Map<String, Map<Enum, Integer>> decrement(Map params) {
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "BEFORE decrement, Params: {0}\nListings: {1}",new Object[]{ params,  values});
}        
        update(false, params);
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "AFTER decrement, Params: {0}\nListings: {1}",new Object[]{ params,  values});
}        
        return this.values;
    }

    private void update(boolean increment, Map params) {

        Iterator<String> iter = values.keySet().iterator();

        while (iter.hasNext()) {

            String joinColName = iter.next();

            Object joinColValue = params.get(joinColName);

            if (joinColValue == null) continue;

            this.update(increment, joinColName, joinColValue);
        }
    }

    private synchronized Map<Enum, Integer> update( 
            boolean increment, String joinColName, Object joinColValue) {

        // Each key-value pair would be something like
        // New=20 OR Used=300 etc
        //
        final Map<Enum, Integer> pairs  =  values.get(joinColName);
        
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Pairs: {0}", pairs);
}

        Enum target;
        if(joinColValue instanceof Enum) {
            target = (Enum)joinColValue;
        }else{
            
            EnumReferences refs = this.getControllerFactory().getEnumReferences();
            
            if(joinColValue instanceof Number) {
                target = refs.getEnum(joinColName, ((Number)joinColValue).intValue());
            }else{
                target = refs.getEnum(joinColName, joinColValue);
            }
        }
        
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "To increment: {0}", target);
}

        return this.update(increment, pairs, target);
    }
    
    private Map<Enum, Integer> update(boolean increment, Map<Enum, Integer> pairs, Enum target) {
        
        Integer intObj = pairs.get(target);
        
if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, 
"BEFORE. increment: {0}, name: {1}, count: {2}",
new Object[]{ increment,  target,  intObj});
}

        if(intObj == null) {
            pairs.put(target, 0);
        }else{

            if(increment) {
                pairs.put(target, ++intObj);
            }else{
                pairs.put(target, --intObj);
            }
        }

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, 
"AFTER. increment: {0}, name: {1}, count: {2}",
new Object[]{ increment,  target,  intObj});
}

        return pairs;
    }

    @Override
    public Map<String, Map<Enum, Integer>> getValues() {
        return values;
    }
}//~END

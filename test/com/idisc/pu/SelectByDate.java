package com.idisc.pu;

import com.bc.jpa.controller.EntityController;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.dao.Criteria.ComparisonOperator;
import com.bc.util.XLogger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.bc.jpa.dao.Select;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 8:52:24 AM
 * @param <E>
 * @param <I>
 */
public class SelectByDate<E, I> {
    
    private final JpaContext jpaContext;
    
    private final EntityController<E, I> ec;
    
    public SelectByDate(JpaContext jpaContext, Class<E> entityClass, Class<I> idClass) {
    
        this.jpaContext = jpaContext;
        
        ec = this.jpaContext.getEntityController(entityClass, idClass);
    }

    public List<E> sort(List<E> resultList, Comparator<E> comparator, int maxOutputSize) {
        
        Collections.sort(resultList, comparator);

        final int resultsCount = resultList.size();

        final int finalMetricsSize = resultsCount < maxOutputSize ? resultsCount : maxOutputSize;

        List<E> outputList = finalMetricsSize >= resultsCount ? resultList : resultList.subList(0, finalMetricsSize);

        return outputList;
    }

    public List<E> getResultList(String dateColumnName, ComparisonOperator comparison, int maxAge, TimeUnit timeUnit, int batchSize) {
        
        return this.getResultList(dateColumnName, comparison, toAge(maxAge, timeUnit), batchSize);
    }
    
    public List<E> getResultList(String dateColumnName, ComparisonOperator comparison, Date maxAge, int batchSize) {
        
        return this.getResultList(dateColumnName, comparison, maxAge, -1, batchSize);
    }
        
    public List<E> getResultList(String dateColumnName, ComparisonOperator comparison, int maxAge, TimeUnit timeUnit, int maxSpread, int batchSize) {
        
        return this.getResultList(dateColumnName, comparison, toAge(maxAge, timeUnit), maxSpread, batchSize);
    }
    
    public List<E> getResultList(String dateColumnName, ComparisonOperator comparison, Date date, int maxSpread, int batchSize) {
        
        int offset = 0;

        List<E> resultList = maxSpread < 1 ? new LinkedList() : new ArrayList<>(maxSpread);

        Class<E> entityType = ec.getEntityClass();
        
        do{

            List<E> batch;
            
            try(Select<E> qb = jpaContext.getDaoForSelect(entityType)) {
                
                if(date == null) {
                    batch = qb.from(entityType).createQuery().setFirstResult(offset).setMaxResults(batchSize).getResultList();
                }else{
                    batch = qb.where(dateColumnName, comparison, date)
                    .createQuery().setFirstResult(offset).setMaxResults(batchSize)
                    .getResultList();
                }    
            }

            final int currentBatchSize = this.sizeOf(batch);

XLogger.getInstance().log(Level.FINE, "Batch size: {0}, offset: {1}, results: {2}", 
        this.getClass(), batchSize, offset, currentBatchSize);   

            if(currentBatchSize < 1) {
                break;
            }

            offset += currentBatchSize;

            resultList.addAll(batch);

        }while(maxSpread < 1 || offset < maxSpread);
        
        return resultList;
    }    
    
    private Date toAge(int maxAge, TimeUnit timeUnit) {
        Calendar cal = Calendar.getInstance();
        cal.add(getField(timeUnit), -maxAge);
        final Date maxAgeTime = cal.getTime();
        return maxAgeTime;
    }
    
    private int getField(TimeUnit timeUnit) {
        switch(timeUnit) {
            case DAYS: return Calendar.DAY_OF_YEAR;
            case HOURS: return Calendar.HOUR_OF_DAY;
            case MINUTES: return Calendar.MINUTE;
            case SECONDS: return Calendar.SECOND;
            case MILLISECONDS: return Calendar.MILLISECOND;
            default: throw new UnsupportedOperationException("TimeUnit: "+timeUnit);
        }
    }
    
    private int sizeOf(List list) {
        return list == null ? 0 : list.size();
    }
}

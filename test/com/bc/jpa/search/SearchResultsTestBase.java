package com.bc.jpa.search;

import com.bc.jpa.EntityController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bc.jpa.paging.PaginatedList;
import com.idisc.pu.entities.Feed;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productvariant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import junit.framework.TestCase;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.BuilderForSelectImpl;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import com.bc.jpa.dao.BuilderForSelect;

/**
 * @author Josh
 */
public class SearchResultsTestBase extends TestCase {
    
    public SearchResultsTestBase(String testName) {
        super(testName);
    }
    
    public <R> void execute(JpaContext jpaContext, Class entityType, Class<R> resultType, String query, int batchSize, boolean useCache) {
        
System.out.println("======================= testing all ======================");
        
        try(SearchResults<R> instance = 
                this.createInstance(jpaContext, entityType, resultType, query, batchSize, useCache)
        )  {

            final String TYPE = instance.getClass().getSuperclass().getName();
            
            Map<Integer, R> resultsCache = new HashMap<>();
            
            R firstResult = null;
            
            final int maxBatches = 3;
            final int batchesToRead = instance.getPageCount() > maxBatches ? maxBatches : instance.getPageCount();
            
            final int numberToRead = instance.getPageSize() * batchesToRead;
            
            PaginatedList<R> resultsList = instance.getAllResults();
            
System.out.println("Number of results: " + (resultsList==null?null:resultsList.size()));
this.printList(jpaContext, entityType, resultsList);

            for(int i=0; i<numberToRead; i++) {
                
                R result = resultsList.get(i);
                
                resultsCache.put(i, result);
                
                if(i == 0) {
                    
                    firstResult = result;
                }
            }
            
System.out.println("First result: " + firstResult);            

            if(firstResult == null) {
                throw new NullPointerException();
            }
            
            int count = 0;
            for(int batch=0; batch<batchesToRead; batch++) {
                
                List<R> batchResults = instance.getPage(batch);
                
                for(R result:batchResults) {
                    
                    R cached = resultsCache.get(count++);
                    
                    assertEquals(cached, result);
                    
//XLogger.getInstance().log(Level.INFO, "{0} = {1}", this.getClass(), count, result);
                }
            }
            
            try{
System.out.println("======================= testing add(E) ======================");
                resultsList.add(firstResult);
                fail(TYPE+" should not be modifiable");
            }catch(UnsupportedOperationException ignored) { }
            
            try{
System.out.println("======================= testing clear() ======================");
                resultsList.clear();
                fail(TYPE+" should not be modifiable");
            }catch(UnsupportedOperationException ignored) { }
            
System.out.println("======================= testing contains(Object) ======================");
            boolean contains = resultsList.contains(firstResult);
            assertTrue("SearchResults should, but does not contain: "+firstResult, contains);
            
//            boolean containsAll = instance.containsAll(resultsCache.values());
//            assertTrue(containsAll);
            
            try{
System.out.println("======================= testing remove(Object) ======================");
                resultsList.remove(firstResult);
                fail(TYPE+" should not be modifiable");
            }catch(UnsupportedOperationException ignored) { }
            
            try{
System.out.println("======================= testing remove(int) ======================");
                resultsList.remove(0);
                fail(TYPE+" should not be modifiable");
            }catch(UnsupportedOperationException ignored) { }
            
System.out.println("======================= testing reset() ======================");
            instance.reset();
            assertFalse(TYPE+" should NOT be empty after reset()", resultsList.isEmpty());

System.out.println("======================= AFTER reset() reading results ======================");

            resultsList = instance.getAllResults();
            
System.out.println("Number of results: " + (resultsList==null?null:resultsList.size()));
this.printList(jpaContext, entityType, resultsList);

            count = 0;
            for(int batch=0; batch<batchesToRead; batch++) {
                
                List<R> batchResults = instance.getPage(batch);
                
                for(R result:batchResults) {
                    
                    R cached = resultsCache.get(count++);
                    
                    assertEquals(cached, result);
                    
//XLogger.getInstance().log(Level.INFO, "{0} = {1}", this.getClass(), count, result);
                }
            }
        }
System.out.println("======================= testing all finished ======================");
    }
    
    private void printList(JpaContext jpaContext, Class<Object> entityType, List list) {
        EntityController<Object, Integer> ec = jpaContext.getEntityController(entityType, Integer.class);
        StringBuilder b = new StringBuilder();
        for(Object entity:list) {
            Integer ID = ec.getId(entity);
            b.append(ID).append(',').append(' ');
        }
System.out.println(b);        
    }

    public <R> SearchResults createInstance(
            JpaContext jpaContext, Class entityType, Class<R> resultType, String query) {
        return new BaseSearchResults(this.createQueryBuilder(jpaContext, entityType, resultType, query));
    }

    public <R> SearchResults createInstance(
            JpaContext jpaContext, Class entityType, Class<R> resultType, 
            String query, int batchSize, boolean useCache) {
        return new BaseSearchResults(
                this.createQueryBuilder(jpaContext, entityType, resultType, query), 
                batchSize, useCache);
    }
    
    protected <R> BuilderForSelect<R> createQueryBuilder(
            JpaContext jpaContext, Class entityType, Class<R> resultType, String query) {

//            BuilderForSelect queryBuilder = cf.getBuilderForSelect(resultType);
        BuilderForSelect queryBuilder = new BuilderForSelectImpl(
                jpaContext.getEntityManager(entityType), resultType, null);

        if(entityType == Product.class){
            queryBuilder.join(entityType, "productvariantList", Productvariant.class);
        }

        String [] colsToSearch;
        if(entityType == Feed.class) {
            colsToSearch = new String[]{"title", "keywords", "description", "content"};                
        }else if(entityType == Product.class) {
            colsToSearch = new String[]{"productName", "keywords", "model", "description"};
        }else if(entityType == Productvariant.class){
            colsToSearch = new String[]{"color", "productSize", "weight"};
        }else{
            colsToSearch = null;
        }
System.out.println("Query: "+query+", columns to search: "+(colsToSearch==null?null:Arrays.toString(colsToSearch)));            
        if(colsToSearch != null) {
            queryBuilder.search(entityType, query, colsToSearch);
        }

        if(entityType == Feed.class) {
            queryBuilder.descOrder("feeddate");
        } else if(entityType == Product.class) {
            Map orderBy = new LinkedHashMap(); // important LinkedHashMap
            orderBy.put("availabilityid", "ASC");
            orderBy.put("ratingPercent", "DESC");
            orderBy.put("price", "ASC");
            queryBuilder.orderBy(entityType, orderBy);
    // 'views' is misleading... a recently uploaded item may not have much views 
    // compared with an earlier uploaded item        
    //        orderBy.put(Product_.views.getName(), "DESC");
    // 'productid' is implicit at this point
    //        orderBy.put(Product_.productid.getName(), "DESC");
        }


        return queryBuilder;
    }
}

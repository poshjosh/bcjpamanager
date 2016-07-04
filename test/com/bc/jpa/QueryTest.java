/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bc.jpa;

import com.idisc.pu.entities.Feed;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author Josh
 */
public class QueryTest extends TestCase {

    public QueryTest(String testName) {
        super(testName);
    }
    
    @Test
    public void test() {
        
        JpaContext jpaContext = TestApp.getInstance().getIdiscJpaContext();
        
        List<Feed> foundFeedList = jpaContext.getEntityController(Feed.class).find(10, 100);

System.out.println("Found: "+foundFeedList.size()+" feeds");

        Feed foundFeed = foundFeedList.get(foundFeedList.size()-1);

        EntityManager em = jpaContext.getEntityManager(Feed.class);

        try{

            final Class entityClass = Feed.class;
            final Class resultClass = String.class;
            final String columnToSelect = "url";
            final String columnToFind = columnToSelect;
            final String valueToFind = foundFeed.getUrl();

System.out.println("URL to find: "+valueToFind);                
            TypedQuery<String> tq = this.buildQueryFromJPQl(
                    em, entityClass, resultClass, columnToSelect, columnToFind, valueToFind);

            tq.setFirstResult(0);
            tq.setMaxResults(1);

            String found = tq.getSingleResult();

System.out.println("Named query URL found: "+found);                                
            assertEquals(valueToFind, found);

            tq = this.buildQueryFromCriteriaBuilder(
                    em, entityClass, resultClass, columnToSelect, columnToFind, valueToFind);

            tq.setFirstResult(0);
            tq.setMaxResults(1);

            found = tq.getSingleResult();

System.out.println("CriteriBuilder query URL found: "+found);  

            assertEquals(valueToFind, found);

        }finally{

            em.close();
        }

    }
    
  private <E, R> TypedQuery<R> buildQueryFromJPQl(
          EntityManager em, Class entityClass, Class<R> resultClass, 
          String columnToSelect, String columnToFind, E valueToFind) {
    TypedQuery<R> query = em.createQuery("SELECT e."+columnToSelect+" FROM "+entityClass.getSimpleName()+" e WHERE e."+columnToFind+" = :"+columnToFind, resultClass);
    query.setParameter(columnToFind, valueToFind);
    return query;
  }

  private <E, R> TypedQuery<R> buildQueryFromCriteriaBuilder(
          EntityManager em, Class<E> entityClass, Class<R> resultClass, 
          String columnToSelect, String columnToFind, Object valueToFind) {
      
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<R> cq = cb.createQuery(resultClass); 
    Root<E> entity = cq.from(entityClass); 
    
    Path<R> selection = entity.<R>get(columnToSelect); 
    cq.select(selection);
    
    Path selector = entity.get(columnToFind);
    cq.where( cb.equal(selector, valueToFind) );
    
    TypedQuery<R> query = em.createQuery(cq);
    
    return query;
  }
}

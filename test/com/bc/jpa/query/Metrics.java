/*
 * Copyright 2016 NUROX Ltd.
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

package com.bc.jpa.query;

import com.bc.jpa.dao.BuilderForSelectImpl;
import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.EntityController;
import com.bc.jpa.JpaContext;
import com.bc.jpa.dao.SelectDao;
import com.looseboxes.pu.entities.Product;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 18, 2016 8:41:04 AM
 */
public class Metrics extends TestBaseForJpaQuery {

    public Metrics(String testName) {
        super(testName);
    }
    
    @Test
    public void test() {
        
        final int n = 5;
        int [] multiples = {1, 10, 100};
        final Integer [] productIds = {512,648,110,539,598,521,644,651,609,611,607,240,639};
        
        for(int multiple:multiples) {
            final int maxResults = n * multiple;
            this.testEc(maxResults, productIds);
        }
        this.sleep(60_000);
        for(int multiple:multiples) {
            final int maxResults = n * multiple;
            this.testSelect(maxResults, productIds);
        }
        this.sleep(60_000);
        for(int multiple:multiples) {
            final int maxResults = n * multiple;
            this.testEm(maxResults, productIds);
        }
    }

    public void testEc(int maxResults, Object [] productids) {
        
final long mb4 = Runtime.getRuntime().freeMemory(); final long tb4 = System.currentTimeMillis();

        EntityController<Product, ?> instance = this.getLbJpaContext().getEntityController(Product.class);
System.out.println("================================: "+instance.getClass().getSimpleName());        

        List<Product> found = instance.find(maxResults, 0); 
System.out.println("Found: "+(found==null?null:found.size()));

        List<Product> found2 = instance.select("productid", productids); 
System.out.println("Found2: "+(found2==null?null:found2.size()));        

        long count = instance.count();
System.out.println("Count: "+count); 

System.out.println("Spent. time: "+(System.currentTimeMillis()-tb4)+", memory: "+(mb4-Runtime.getRuntime().freeMemory()));
    }

    public void testSelect(int maxResults, Object [] productids) {
        
final long mb4 = Runtime.getRuntime().freeMemory(); final long tb4 = System.currentTimeMillis();

        try(BuilderForSelect<Product> instance = this.createSelect(Product.class)) {
System.out.println("================================: "+BuilderForSelectImpl.class.getSimpleName());  

            List<Product> found = instance.from(Product.class)
                    .createQuery().setMaxResults(maxResults).getResultList();
System.out.println("Found: "+(found==null?null:found.size()));

            instance.reset();
            
            List<Product> found2 = instance.from(Product.class)
                    .where("productid", productids)
                    .createQuery().getResultList();
System.out.println("Found2: "+(found2==null?null:found2.size()));            

            SelectDao<Long> selectLong = instance.forSelect(Long.class);
        
            Long count = selectLong.getCriteria()
                    .from(Product.class).count().createQuery().getSingleResult();
System.out.println("Count: "+count);            
        }
        
System.out.println("Spent. time: "+(System.currentTimeMillis()-tb4)+", memory: "+(mb4-Runtime.getRuntime().freeMemory()));
    }
    
    public void testEm(int maxResults, Object [] productids) {
        
final long mb4 = Runtime.getRuntime().freeMemory(); final long tb4 = System.currentTimeMillis();

        final Class<Product> entityClass = Product.class;
        final JpaContext jpaContext = this.getLbJpaContext();
        
        EntityManager em = jpaContext.getEntityManager(entityClass);
System.out.println("================================: "+em.getClass().getSimpleName());  
        
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(entityClass);
            Root<Product> root = cq.from(entityClass); 
            List<Product> found = em.createQuery(cq).setMaxResults(maxResults).getResultList();
System.out.println("Found: "+(found==null?null:found.size()));
//        }finally{
//            em.close();
//        }
        
//        em = jpaContext.getEntityManager(entityClass);
//        try{
            cb = em.getCriteriaBuilder();
            cq = cb.createQuery(entityClass);
            root = cq.from(entityClass); 
            Predicate [] predicates = new Predicate[productids.length];
            for(int i=0; i<productids.length; i++) {
                predicates[i] = cb.equal(root.get("productid"), productids[i]);
            }
            cq = cq.where(cb.or(predicates)); 
            List<Product> found2 = em.createQuery(cq).getResultList();
System.out.println("Found2: "+(found2==null?null:found2.size()));
//        }finally{
//            em.close();
//        }

//        em = jpaContext.getEntityManager(entityClass);
//        try{
            cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cqLong = cb.createQuery(Long.class); 
            Root productLong = cqLong.from(Product.class);
            cqLong.select(cb.count(productLong));
            Long count = em.createQuery(cqLong).getSingleResult();
System.out.println("Count: "+count);                        
        }finally{
            em.close();
        }
        
System.out.println("Spent. time: "+(System.currentTimeMillis()-tb4)+", memory: "+(mb4-Runtime.getRuntime().freeMemory()));
    }
    
    private void sleep(long millis) {
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}

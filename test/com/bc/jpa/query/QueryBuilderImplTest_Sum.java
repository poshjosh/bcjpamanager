package com.bc.jpa.query;

import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.JpaContext;
import com.looseboxes.pu.entities.Orderproduct;
import com.looseboxes.pu.entities.Productorder;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Josh
 */
public class QueryBuilderImplTest_Sum extends TestBaseForJpaQuery {

    public QueryBuilderImplTest_Sum(String testName) {
        super(testName);
    }

    public void testA() {
        
        // SELECT SUM(column_to_sum) FROM table WHERE column = value
        final String columnToSelect = "quantity";
        final String columnToSearch = "productorderid";
        final Integer orderId = 104;
        
        JpaContext jpaContext = this.getLbJpaContext();

System.out.println("MODE 1: QueryBuilder#sum");        
        Number sum0 = null;
        try(BuilderForSelect<Number> qb = jpaContext.getBuilderForSelect(Orderproduct.class, Number.class)) {
            sum0 = qb.from(Orderproduct.class)
            .sum(columnToSelect)
            .where(columnToSearch, orderId)
            .createQuery().getSingleResult();
System.out.println("Sum: "+sum0);            
        }catch(javax.persistence.NoResultException e) {
            e.printStackTrace();
        }

System.out.println("MODE 2: CriteriaBuilder, CriteriaQuery");  
        Number sum1 = null;
        EntityManager em = jpaContext.getEntityManager(Orderproduct.class);
        try{
            
            final Productorder productorder = em.find(Productorder.class, orderId);
            
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Number> cq = cb.createQuery(Number.class);

            Root<Orderproduct> root = cq.from(Orderproduct.class);

            Expression<Number> sumExpr = cb.sum(root.<Number>get(columnToSelect));

            cq = cq.select(sumExpr); 
  
            // Won't work
//            Predicate where = cb.equal(root.get(columnToSearch), orderId);
            Predicate where = cb.equal(root.get(columnToSearch), productorder);
            
            cq.where(where);
            
            TypedQuery<Number> tq = em.createQuery(cq); 

            sum1 = tq.getSingleResult();
System.out.println("Sum: "+sum1);

        }catch(javax.persistence.NoResultException e) {
            e.printStackTrace();
        }finally{
            
            em.close();
        }
        
        assertEquals(sum0, sum1);
    }
}

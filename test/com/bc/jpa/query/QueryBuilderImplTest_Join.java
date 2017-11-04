package com.bc.jpa.query;

import com.bc.jpa.context.JpaContext;
import com.bc.jpa.TestApp;
import static com.bc.jpa.query.TestBaseForJpaQuery.ENTITY_TYPE;
import com.looseboxes.pu.entities.Orderproduct;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productorder;
import com.looseboxes.pu.entities.Productvariant;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.Test;
import com.bc.jpa.dao.Select;

/**
 * @author Josh
 */
public class QueryBuilderImplTest_Join extends TestBaseForJpaQuery {
    
    public QueryBuilderImplTest_Join(String testName) {
        super(testName);
    }
    
    @Test
    public void testJoin_2Modes() {
        
System.out.println("#testJoin_2Modes");

        final JpaContext jpaContext = TestApp.getInstance().getLbJpaContext();
        
        final EntityManager em = jpaContext.getEntityManager(ENTITY_TYPE);
        
        final Product product = (Product)em.find(ENTITY_TYPE, 527);
        
        final String searchCol = "productvariantid";
        final String joinCol = "orderproductList";

        List<Productvariant> variants = product.getProductvariantList();

        for(Productvariant variant:variants) {

            List<Productorder> orders0;
            try(Select<Productorder> instance = createSelect(Productorder.class)) {

                TypedQuery<Productorder> tq = instance
                        .join(Productorder.class, joinCol, JoinType.INNER, Orderproduct.class)
                        .where(Orderproduct.class, searchCol, variant)
                        .createQuery();

                orders0 = tq.getResultList();
            }

            if(orders0 != null && !orders0.isEmpty()) {
System.out.println("MODE 1. Variant: "+variant+"\nOrders: "+orders0);  

                CriteriaBuilder cb = em.getCriteriaBuilder();

                CriteriaQuery<Productorder> cq = cb.createQuery(Productorder.class); 

                Root<Productorder> productOrder = cq.from(Productorder.class);
//                    Root<Orderproduct> orderProduct = cq.from(Orderproduct.class);

                Join<Productorder, Orderproduct> orderProduct = productOrder.join(joinCol, JoinType.INNER);

                Predicate equalProductvariant = cb.equal(orderProduct.<Productvariant>get(searchCol), variant);

                cq.where(equalProductvariant);

                TypedQuery<Productorder> tq = em.createQuery(cq);

                List<Productorder> orders1 = tq.getResultList();
System.out.println("MODE 2. Variant: "+variant+"\nOrders: "+orders1);  

                assertEquals(orders0, orders1);
            }
        }
    }
}

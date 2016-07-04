package com.bc.jpa.query;

import com.bc.jpa.TestApp;
import com.looseboxes.pu.entities.Orderproduct;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productorder;
import com.looseboxes.pu.entities.Productvariant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.JoinType;
import junit.framework.TestCase;
import org.junit.Test;
import com.bc.jpa.JpaContext;

/**
 * @author Josh
 */
public class QueryBuilderImplTest extends TestCase {

    private static final Class entityType = Product.class;
    public JpaContext getJpaContext() {
        return TestApp.getInstance().getLbJpaContext();
    }
    
    private static List<Integer> ids;

    private static Integer ID;

    public QueryBuilderImplTest(String testName) {
        super(testName);
        if(ids == null) {
            ids = selectLastIds(20);
            ID = ids.get(ids.size() - 1);
        }
    }
    
    @Test
    public void testSearchAndWhereGreaterOrEquals() {
        
System.out.println("#testSearchAndWhereGreaterOrEquals");
        
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
        
            TypedQuery<Object[]> tq = instance.forType(entityType)
                    .select("productid", "productName", "price")
                    .search("dress", "productName", "description", "keywords", "model")
                    .and()
                    .where("productid", QueryBuilder.GREATER_OR_EQUALS, ID)
                    .descOrder("productid")
                    .build();

            List<Object[]> results = tq.getResultList();
this.printResults(results, true);
        }
    }
    
    @Test
    public void testSelectAndLikeAndGreaterOrEquals() {
        
System.out.println("#testSelectAndLikeAndGreaterOrEquals");
        
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
            
            TypedQuery<Object[]> tq = instance.forType(entityType)
                    .select("productid", "productName", "price", "description")
                    .where("productName", QueryBuilder.LIKE, "%pepperts%", QueryBuilder.AND)
                    .where("productid", QueryBuilder.GREATER_OR_EQUALS, ID - 100)
                    .ascOrder("productid")
                    .build();

            List<Object[]> results = tq.getResultList();
this.printResults(results, true);
        }
    }
    
    @Test
    public void testJoin() {
        
System.out.println("#testJoin");
        
        final EntityManager em = getJpaContext().getEntityManager(entityType);
        
        List<Product> results = this.selectRange(499, 528);
        
this.printResults(results, true);

        Productvariant productvariant;
        List<Productorder> orders;
        
        outer:
        for(Product product:results) {
            
            List<Productvariant> variants = product.getProductvariantList();
            
            for(Productvariant variant:variants) {
                
                productvariant = variant;
                
                try(QueryBuilder instance = createQueryBuilder(Productorder.class)) {
                    
                    TypedQuery<Productorder> tq = 
                            instance.where(Orderproduct.class, "productvariantid", variant)
                            .join(Productorder.class, "orderproductList", JoinType.INNER, Orderproduct.class)
                            .build();

                    orders = tq.getResultList();
                }
                
                if(orders != null && !orders.isEmpty()) {

System.out.println("Variant: "+productvariant+"\nOrders: "+orders);                    
                    break outer;
                }
            }
        }
    }
    
    @Test
    public void testResetAndCommit() {
        
        try(QueryBuilder instance = createQueryBuilder(entityType)) {
        
            final String COLUMN = "productid";

            TypedQuery<Product> tq = instance.forType(entityType).descOrder(COLUMN).build();

            assertEquals("QueryBuilder should be commited after calling #createQuery, but is not commited", instance.isCommited(), true);

            try{

                instance.and(); 

                fail("QueryBuilder should not allow any further operations except #createQuery operations when commited, but and() method returned successfully");

            }catch(RuntimeException indicatesSuccess) { }

//            instance.reset();

//            tq = instance.forType(entityType).where(COLUMN, QueryBuilder.EQUALS, 519).descOrder(COLUMN).build();

//            Product result = tq.getSingleResult();
        }
    }
    
    @Test
    public void testComplex() {
        
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
        
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -20);

            TypedQuery<Object[]> tq = instance.forType(entityType)
                    .select("productid", "productName", "price")
                    .search("girls dress", "productName", "description", "keywords", "model")
                    .and().where("price", QueryBuilder.GREATER_OR_EQUALS, 2_000)
                    .and().where("datecreated", QueryBuilder.LESS_OR_EQUALS, cal.getTime())
                    .descOrder("productid")
                    .build();

            List<Object[]> results = tq.getResultList();

this.printResults(results, true);
        }
    }
    
    public List<Product> selectRange(Integer start, Integer end) {
        
System.out.println(this.getClass().getName()+"#selectRange("+start+", "+end+")");

        try(QueryBuilder instance = createQueryBuilder(entityType)) {
        
            final String COLUMN = "productid";

            TypedQuery<Product> tq = instance.forType(entityType)
                    .where(COLUMN, QueryBuilder.GREATER_OR_EQUALS, start, QueryBuilder.AND)
                    .where(COLUMN, QueryBuilder.LESS_THAN, end)        
                    .descOrder(COLUMN)
                    .build();

            List<Product> results = tq.getResultList();

            return results;
        }
    }
    
    public List<Integer> selectLastIds(int n) {
        
        try(QueryBuilder<Integer> instance = createQueryBuilder(Integer.class)) {
        
            TypedQuery<Integer> tq = instance
                    .forType(Product.class)
                    .select("productid")
                    .descOrder("productid").build();

            tq.setFirstResult(0);
            tq.setMaxResults(n);

            List<Integer> output = tq.getResultList();

            return output;
        }
    }
    
    public void printResults(List results, boolean deep) {
        System.out.println("Found: "+(results==null?null:results.size())+" results");
        if(deep) {
            for(Object result:results) {
                if(result instanceof Object []) {
System.out.println(Arrays.toString((Object[])result));
                }else{
System.out.println(result);                    
                }
            }
        }
    }
    
    public <R> QueryBuilder<R> createQueryBuilder(Class<R> resultType) {
        return new QueryBuilderImpl(this.getJpaContext().getEntityManager(entityType), resultType){
            @Override
            public TypedQuery<R> build() {

//String output = String.format("#build()\n%s", this);

//System.out.println(this.getClass().getSimpleName() + output);

                return super.build();
            }
        };
    }
}

package com.bc.jpa.query;

import com.looseboxes.pu.entities.Orderproduct;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productorder;
import com.looseboxes.pu.entities.Productvariant;
import java.util.Calendar;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.JoinType;
import org.junit.Test;

/**
 * @author Josh
 */
public class QueryBuilderImplTest extends QueryBuilderTestBase {

    public QueryBuilderImplTest(String testName) {
        super(testName);
    }
    
    @Test
    public void testSearchAndWhereGreaterOrEquals() {
        
System.out.println("#testSearchAndWhereGreaterOrEquals");
        
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
        
            final String ID_COL = "productid";
            final String query = "dress";
            final String queryExpression = "%"+query+"%";
            final String [] colsToSelect = new String[]{"productid", "productName", "price"};
            final String [] colsToSearch = new String[]{"productName", "description", "keywords", "model"};
            
            TypedQuery<Object[]> tq = instance.forType(ENTITY_TYPE)
                    .select(colsToSelect)
                    .where(colsToSearch, QueryBuilder.LIKE, queryExpression, QueryBuilder.OR)
                    .and()
                    .where(ID_COL, QueryBuilder.GREATER_OR_EQUALS, SELECTED_PRODUCTID)
                    .descOrder(ID_COL)
                    .build();
            List<Object[]> results = tq.getResultList();
this.printResults(results, true);
        }
    }
    
    @Test
    public void testSelectAndLikeAndGreaterOrEquals() {
        
System.out.println("#testSelectAndLikeAndGreaterOrEquals");
        
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
            
            TypedQuery<Object[]> tq = instance.forType(ENTITY_TYPE)
                    .select("productid", "productName", "price", "description")
                    .where("productName", QueryBuilder.LIKE, "%pepperts%", QueryBuilder.AND)
                    .where("productid", QueryBuilder.GREATER_OR_EQUALS, SELECTED_PRODUCTID - 100)
                    .ascOrder("productid")
                    .build();

            List<Object[]> results = tq.getResultList();
this.printResults(results, true);
        }
    }
    
    @Test
    public void testJoin() {
        
System.out.println("#testJoin");
        
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
        
        try(QueryBuilder instance = createQueryBuilder(ENTITY_TYPE)) {
        
            final String COLUMN = "productid";

            TypedQuery<Product> tq = instance.forType(ENTITY_TYPE).descOrder(COLUMN).build();

            assertEquals("QueryBuilder should be commited after calling #build, but is not commited", instance.isCommited(), true);

            try{

                instance.and(); 

                fail("QueryBuilder should not allow any further operations except #build operations when commited, but and() method returned successfully");

            }catch(RuntimeException indicatesSuccess) { }

//            instance.reset();

//            tq = instance.forType(ENTITY_TYPE).where(COLUMN, QueryBuilder.EQUALS, 519).descOrder(COLUMN).build();

//            Product result = tq.getSingleResult();
        }
    }
    
    @Test
    public void testComplex() {
        
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
        
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -20);

            TypedQuery<Object[]> tq = instance.forType(ENTITY_TYPE)
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
}

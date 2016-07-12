package com.bc.jpa.query;

import com.bc.jpa.JpaContext;
import com.bc.jpa.TestApp;
import com.looseboxes.pu.entities.Product;
import java.util.Arrays;
import java.util.List;
import javax.persistence.TypedQuery;
import junit.framework.TestCase;

/**
 * @author Josh
 */
public class QueryBuilderTestBase extends TestCase {

    public static final Class ENTITY_TYPE = Product.class;
    
    public JpaContext getJpaContext() {
        return TestApp.getInstance().getLbJpaContext();
    }
    
    private static List<Integer> ids;

    public static Integer SELECTED_PRODUCTID;

    public QueryBuilderTestBase(String testName) {
        super(testName);
        if(ids == null) {
            ids = selectLastIds(20);
            SELECTED_PRODUCTID = ids.get(ids.size() - 1);
        }
    }
    
    public List<Product> selectRange(Integer start, Integer end) {
        
System.out.println(this.getClass().getName()+"#selectRange("+start+", "+end+")");

        try(QueryBuilder instance = createQueryBuilder(ENTITY_TYPE)) {
        
            final String COLUMN = "productid";

            TypedQuery<Product> tq = instance.forType(ENTITY_TYPE)
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
        return new QueryBuilderImpl(this.getJpaContext().getEntityManager(ENTITY_TYPE), resultType){
            @Override
            public TypedQuery<R> build() {

//String output = String.format("#build()\n%s", this);

//System.out.println(this.getClass().getSimpleName() + output);

                return super.build();
            }
        };
    }
}

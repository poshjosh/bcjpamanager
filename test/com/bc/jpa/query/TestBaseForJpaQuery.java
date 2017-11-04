package com.bc.jpa.query;

import com.bc.jpa.dao.SelectImpl;
import com.bc.jpa.dao.UpdateImpl;
import com.bc.jpa.context.JpaContext;
import com.bc.jpa.TestApp;
import com.looseboxes.pu.entities.Product;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import junit.framework.TestCase;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.Update;

/**
 * @author Josh
 */
public class TestBaseForJpaQuery extends TestCase {

    public static final Class ENTITY_TYPE = Product.class;
    
    public JpaContext getLbJpaContext() {
        return TestApp.getInstance().getLbJpaContext();
    }

    
    public JpaContext getIdiscJpaContext() {
        return TestApp.getInstance().getIdiscJpaContext();
    }
    
    private static List<Integer> ids;

    public static Integer SELECTED_PRODUCTID;

    public TestBaseForJpaQuery(String testName) {
        super(testName);
        if(ids == null) {
            ids = selectLastIds(20);
            SELECTED_PRODUCTID = ids.get(ids.size() - 1);
        }
    }
    
    public List<Product> selectRange(Integer start, Integer end) {
        
System.out.println(this.getClass().getName()+"#selectRange("+start+", "+end+")");

        try(Select<Product> instance = createSelect(ENTITY_TYPE)) {
        
            final String COLUMN = "productid";

            TypedQuery<Product> tq = instance.from(ENTITY_TYPE)
                    .where(COLUMN, Select.GREATER_OR_EQUALS, start, Select.AND)
                    .where(COLUMN, Select.LESS_THAN, end)        
                    .descOrder(COLUMN)
                    .createQuery();

            List<Product> results = tq.getResultList();

            return results;
        }
    }
    
    public List<Integer> selectLastIds(int n) {
        
        try(Select<Integer> instance = createSelect(Integer.class)) {
        
            TypedQuery<Integer> tq = instance
                    .from(Product.class)
                    .select("productid")
                    .descOrder("productid").createQuery();

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
    
    public <R> Select<R> createSelect(Class<R> resultType) {
        
        return new SelectImpl<R>(this.getLbJpaContext().getEntityManager(ENTITY_TYPE), resultType, this.getLbJpaContext().getDatabaseFormat()){
            @Override
            public TypedQuery<R> createQuery() {

//String output = String.format("#createQuery()\n%s", this);

//System.out.println(this.getClass().getSimpleName() + output);

                return super.createQuery();
            }
        };
    }

    public Update<Product> createUpdate() {
        
        return new UpdateImpl(this.getLbJpaContext().getEntityManager(ENTITY_TYPE), ENTITY_TYPE, this.getLbJpaContext().getDatabaseFormat()){
            @Override
            public Query createQuery() {

//String output = String.format("#createQuery()\n%s", this);

//System.out.println(this.getClass().getSimpleName() + output);

                return super.createQuery();
            }
        };
    }
}

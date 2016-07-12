package com.bc.jpa.query;

import com.bc.jpa.JpaContext;
import com.bc.jpa.TestApp;
import static com.bc.jpa.query.QueryBuilderTestBase.ENTITY_TYPE;
import static com.bc.jpa.query.QueryBuilderTestBase.SELECTED_PRODUCTID;
import com.looseboxes.pu.entities.Product;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Josh
 */
public class QueryBuilderImplTest_1 extends QueryBuilderTestBase {
    
    public QueryBuilderImplTest_1(String testName) {
        super(testName);
    }
    
    @Test
    public void testSearchAndWhereGreaterOrEquals_3Modes() {
        
System.out.println("#testSearchAndWhereGreaterOrEquals_3 modes");

        final String ID_COL = "productid";
        final Integer ID_VAL = SELECTED_PRODUCTID-30;
        final String query = "dress";
        final String queryExpression = "%"+query+"%";
        final String [] colsToSelect = new String[]{"productid", "productName", "price"};
        final String [] colsToSearch = new String[]{"productName", "description", "keywords", "model"};
        
System.out.println("MODE 1: QueryBuilder#where(String[], LIKE, ?, OR)");            
        List<Object[]> results0;
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
        
            TypedQuery<Object[]> tq = instance.forType(ENTITY_TYPE)
                    .select(colsToSelect)
                    .where(colsToSearch, QueryBuilder.LIKE, queryExpression, QueryBuilder.OR)
                    .and()
                    .where(ID_COL, QueryBuilder.GREATER_OR_EQUALS, ID_VAL)
                    .descOrder(ID_COL)
                    .build();
            
            results0 = tq.getResultList();
this.printResults(results0, true);
        }

System.out.println("MODE 2: QueryBuilder#search(String[], ?)");            
        List<Object[]> results1;
        try(QueryBuilder<Object[]> instance = createQueryBuilder(Object[].class)) {
        
            TypedQuery<Object[]> tq1 = instance.forType(ENTITY_TYPE)
                    .select(colsToSelect)
                    .search(query, colsToSearch)
                    .and()
                    .where(ID_COL, QueryBuilder.GREATER_OR_EQUALS, ID_VAL)
                    .descOrder(ID_COL)
                    .build();
            
            results1 = tq1.getResultList();
this.printResults(results1, true);
        }

        boolean equals = Arrays.deepEquals(results0.toArray(new Object[0]), results1.toArray(new Object[0]));
        assertTrue("Mode 1 Results NOT EQUAL TO Mode 2 Results", equals);
        
System.out.println("MODE 3: CriteriaBuilder and CriteriaQuery");               
        JpaContext jpaContext = TestApp.getInstance().getLbJpaContext();

        EntityManager em = jpaContext.getEntityManager(ENTITY_TYPE);

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<Product> root = cq.from(ENTITY_TYPE);

        int off = 0;
        Selection [] selections = new Selection[colsToSelect.length];
        for(String col:colsToSelect) {
            Selection selection = root.get(col);
            selections[off++] = selection;
        }
        cq = cq.multiselect(selections);

        off = 0;
        Predicate [] predicates = new Predicate[colsToSearch.length];
        for(String col:colsToSearch) {
            Predicate predicate = cb.like(root.<String>get(col), queryExpression);
            predicates[off++] = predicate;
        }

        Predicate likeProductIdNameAndPrice = cb.or(predicates);

        Predicate greaterOrEqualsProductId = cb.greaterThanOrEqualTo(root.<Integer>get(ID_COL), ID_VAL);

        Predicate where = cb.and(likeProductIdNameAndPrice, greaterOrEqualsProductId);

        cq.where(where);

        Order order = cb.desc(root.get(ID_COL));

        cq.orderBy(order);

        TypedQuery<Object[]> tq2 = em.createQuery(cq);

        List<Object[]> results2 = tq2.getResultList();
this.printResults(results2, true);

        equals = Arrays.deepEquals(results0.toArray(new Object[0]), results2.toArray(new Object[0]));
        assertTrue("Mode 1 Results NOT EQUAL TO Mode 3 Results", equals);

    }
}

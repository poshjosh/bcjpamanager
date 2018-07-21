package com.bc.jpa.query;

import com.looseboxes.pu.References;
import com.looseboxes.pu.entities.Availability;
import com.looseboxes.pu.entities.Product;
import com.bc.jpa.dao.Select;

/**
 * @author Josh
 */
public class QueryBuilderImplTest_EnumRefs extends TestBaseForJpaQuery {

    public QueryBuilderImplTest_EnumRefs(String testName) {
        super(testName);
    }
    
    public void testEnumRefs() {
        
        Select<Product> qb = this.createSelect(Product.class);
        
        Product product0 = qb.from(Product.class).createQuery().setMaxResults(1).getSingleResult();
System.out.println("================= Selected 0: "+product0);

        qb = this.createSelect(Product.class);
        
        final Availability reference = product0.getAvailabilityid();
System.out.println("================= Reference: "+reference);

        Product product1 = qb.from(Product.class)
        .where("availabilityid", Select.EQ, reference)
        .createQuery().setMaxResults(1).getSingleResult();
System.out.println("================= Selected 1: "+product1);        

        assertEquals(product0, product1);
        
        qb = this.createSelect(Product.class);
        
        Product product2 = qb.from(Product.class)
        .where("availabilityid", Select.EQ, reference.getAvailabilityid())
        .createQuery().setMaxResults(1).getSingleResult();
System.out.println("================= Selected 2: "+product2);

        assertEquals(product0, product2);

        qb = this.createSelect(Product.class);
        
        final References.availability availabilityReference = References.availability.SoldOut; // 4
System.out.println("================= Availability Reference: "+availabilityReference);

        Product product3 = qb.from(Product.class)
        .where("availabilityid", Select.EQ, availabilityReference)
        .createQuery().setMaxResults(1).getSingleResult();
System.out.println("================= Selected 3: "+product3);

        assertEquals(product0, product3);
        
        qb = this.createSelect(Product.class);
        
        final Enum referenceEnum = this.getLbJpaContext().getEnumReferences().getEnum("availabilityid", 4);
System.out.println("================= Reference Enum: "+referenceEnum);

        Product product4 = qb.from(Product.class)
        .where("availabilityid", Select.EQ, referenceEnum)
        .createQuery().setMaxResults(1).getSingleResult();
System.out.println("================= Selected 4: "+product4);

        assertEquals(product0, product4);
    }
}

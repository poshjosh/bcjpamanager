package com.looseboxes.pu.query;

import com.bc.jpa.context.PersistenceUnitContext;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.dao.SelectImpl;
import com.looseboxes.pu.entities.Product;
import com.looseboxes.pu.entities.Productvariant;
import javax.persistence.EntityManager;

/**
 * @author Josh
 */
public class SelectProduct extends SelectImpl {

    public SelectProduct(PersistenceUnitContext jpaContext) {
        this(null, jpaContext.getEntityManager(), 
                Product.class, jpaContext.getDatabaseFormat());
    }
    
    public SelectProduct(String query, PersistenceUnitContext jpaContext, Class resultType) {
        this(query, jpaContext.getEntityManager(), 
                resultType, jpaContext.getDatabaseFormat());
    }

    public SelectProduct(EntityManager em, DatabaseFormat databaseFormat) {
        this(null, em, Product.class, databaseFormat);
    }
    
    public SelectProduct(String query, EntityManager em, Class resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
        SelectProduct.this.format(query);
    }
    
    protected void format(String query) {

        this.distinct(true);
        
        this.join(Product.class, "productvariantList", Productvariant.class);

        if(query != null) {
            
            this.search(
                    Product.class, query,
                    "productName",
                    "keywords",
                    "model",
                    "description"
            );
            this.search(
                Productvariant.class, query,
                "color",
                "productSize",
                "weight"
            );
        }
        
        this.ascOrder(Product.class, "availabilityid");
        this.descOrder(Product.class, "ratingPercent");
        this.ascOrder(Product.class, "price");
// 'views' is misleading... a recently uploaded item may not have much views 
// compared with an earlier uploaded item        
//        this.descOrder(Product.class, Product_.views.getName());
// 'productid' is implicit at this point
//        this.descOrder(Product.class, Product_.productid.getName());
    }
}

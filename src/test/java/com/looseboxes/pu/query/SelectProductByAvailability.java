package com.looseboxes.pu.query;

import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.context.JpaContext;
import com.looseboxes.pu.References;
import com.looseboxes.pu.entities.Product;
import java.util.Collections;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 * @author Josh
 */
public class SelectProductByAvailability extends SelectProduct {
    
    private final References.availability availability;

    public SelectProductByAvailability(
            References.availability availability, JpaContext jpaContext) {
        super(jpaContext);
        this.availability = this.requireNonNull(availability);
    }

    public SelectProductByAvailability(
            References.availability availability, String query, 
            JpaContext jpaContext, Class resultType) {
        super(query, jpaContext, resultType);
        this.availability = this.requireNonNull(availability);
    }

    public SelectProductByAvailability(
            References.availability availability, 
            EntityManager em, DatabaseFormat databaseFormat) {
        super(em, databaseFormat);
        this.availability = this.requireNonNull(availability);
    }

    public SelectProductByAvailability(
            References.availability availability, String query, 
            EntityManager em, Class resultType, DatabaseFormat databaseFormat) {
        super(query, em, resultType, databaseFormat);
        this.availability = this.requireNonNull(availability);
    }
    
    private References.availability requireNonNull(References.availability availability) {
        return Objects.requireNonNull(availability, "Availability cannot be null");
    }

    @Override
    protected void format(String query) {
        this.where(
                Product.class, 
                Collections.singletonMap("availabilityid", availability));        
    }
}

package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @(#)Availability.java   20-May-2015 15:49:51
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
@Entity
@Table(name = "availability")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Availability.findAll", query = "SELECT a FROM Availability a"),
    @NamedQuery(name = "Availability.findByAvailabilityid", query = "SELECT a FROM Availability a WHERE a.availabilityid = :availabilityid"),
    @NamedQuery(name = "Availability.findByAvailability", query = "SELECT a FROM Availability a WHERE a.availability = :availability")})
public class Availability implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "availabilityid")
    private Short availabilityid;
    @Basic(optional = false)
    @Column(name = "availability")
    private String availability;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "availabilityid")
    private List<Product> productList;

    public Availability() {
    }

    public Availability(Short availabilityid) {
        this.availabilityid = availabilityid;
    }

    public Availability(Short availabilityid, String availability) {
        this.availabilityid = availabilityid;
        this.availability = availability;
    }

    public Short getAvailabilityid() {
        return availabilityid;
    }

    public void setAvailabilityid(Short availabilityid) {
        this.availabilityid = availabilityid;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @XmlTransient
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (availabilityid != null ? availabilityid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Availability)) {
            return false;
        }
        Availability other = (Availability) object;
        if ((this.availabilityid == null && other.availabilityid != null) || (this.availabilityid != null && !this.availabilityid.equals(other.availabilityid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Availability[ availabilityid=" + availabilityid + " ]";
    }

}

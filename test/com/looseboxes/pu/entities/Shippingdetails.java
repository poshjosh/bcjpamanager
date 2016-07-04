package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @(#)Shippingdetails.java   20-May-2015 15:49:52
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
@Table(name = "shippingdetails")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Shippingdetails.findAll", query = "SELECT s FROM Shippingdetails s"),
    @NamedQuery(name = "Shippingdetails.findByShippingdetailsid", query = "SELECT s FROM Shippingdetails s WHERE s.shippingdetailsid = :shippingdetailsid"),
    @NamedQuery(name = "Shippingdetails.findByShippingMethod", query = "SELECT s FROM Shippingdetails s WHERE s.shippingMethod = :shippingMethod"),
    @NamedQuery(name = "Shippingdetails.findByShipDate", query = "SELECT s FROM Shippingdetails s WHERE s.shipDate = :shipDate"),
    @NamedQuery(name = "Shippingdetails.findByEstimatedArrivalDate", query = "SELECT s FROM Shippingdetails s WHERE s.estimatedArrivalDate = :estimatedArrivalDate"),
    @NamedQuery(name = "Shippingdetails.findByDatecreated", query = "SELECT s FROM Shippingdetails s WHERE s.datecreated = :datecreated"),
    @NamedQuery(name = "Shippingdetails.findByTimemodified", query = "SELECT s FROM Shippingdetails s WHERE s.timemodified = :timemodified")})
public class Shippingdetails implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "shippingdetailsid")
    private Integer shippingdetailsid;
    @Column(name = "shippingMethod")
    private String shippingMethod;
    @Column(name = "shipDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date shipDate;
    @Column(name = "estimatedArrivalDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedArrivalDate;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @JoinColumn(name = "deliveryAddress", referencedColumnName = "addressid")
    @ManyToOne(optional = false)
    private Address deliveryAddress;
    @JoinColumn(name = "productorderid", referencedColumnName = "productorderid")
    @OneToOne(optional = false)
    private Productorder productorderid;
    @JoinColumn(name = "shippingstatusid", referencedColumnName = "shippingstatusid")
    @ManyToOne(optional = false)
    private Shippingstatus shippingstatusid;

    public Shippingdetails() {
    }

    public Shippingdetails(Integer shippingdetailsid) {
        this.shippingdetailsid = shippingdetailsid;
    }

    public Shippingdetails(Integer shippingdetailsid, Date datecreated, Date timemodified) {
        this.shippingdetailsid = shippingdetailsid;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getShippingdetailsid() {
        return shippingdetailsid;
    }

    public void setShippingdetailsid(Integer shippingdetailsid) {
        this.shippingdetailsid = shippingdetailsid;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    public Date getEstimatedArrivalDate() {
        return estimatedArrivalDate;
    }

    public void setEstimatedArrivalDate(Date estimatedArrivalDate) {
        this.estimatedArrivalDate = estimatedArrivalDate;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public Date getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(Date timemodified) {
        this.timemodified = timemodified;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Productorder getProductorderid() {
        return productorderid;
    }

    public void setProductorderid(Productorder productorderid) {
        this.productorderid = productorderid;
    }

    public Shippingstatus getShippingstatusid() {
        return shippingstatusid;
    }

    public void setShippingstatusid(Shippingstatus shippingstatusid) {
        this.shippingstatusid = shippingstatusid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (shippingdetailsid != null ? shippingdetailsid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shippingdetails)) {
            return false;
        }
        Shippingdetails other = (Shippingdetails) object;
        if ((this.shippingdetailsid == null && other.shippingdetailsid != null) || (this.shippingdetailsid != null && !this.shippingdetailsid.equals(other.shippingdetailsid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Shippingdetails[ shippingdetailsid=" + shippingdetailsid + " ]";
    }

}

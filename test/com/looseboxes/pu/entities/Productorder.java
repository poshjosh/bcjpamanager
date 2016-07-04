package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @(#)Productorder.java   20-May-2015 15:49:52
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
@Table(name = "productorder")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productorder.findAll", query = "SELECT p FROM Productorder p"),
    @NamedQuery(name = "Productorder.findByProductorderid", query = "SELECT p FROM Productorder p WHERE p.productorderid = :productorderid"),
    @NamedQuery(name = "Productorder.findByOrderDate", query = "SELECT p FROM Productorder p WHERE p.orderDate = :orderDate"),
    @NamedQuery(name = "Productorder.findByRequiredDate", query = "SELECT p FROM Productorder p WHERE p.requiredDate = :requiredDate"),
    @NamedQuery(name = "Productorder.findByDatecreated", query = "SELECT p FROM Productorder p WHERE p.datecreated = :datecreated"),
    @NamedQuery(name = "Productorder.findByTimemodified", query = "SELECT p FROM Productorder p WHERE p.timemodified = :timemodified")})
public class Productorder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "productorderid")
    private Integer productorderid;
    @Column(name = "orderDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    @Column(name = "requiredDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requiredDate;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productorderid")
    private List<Orderproduct> orderproductList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productorderid")
    private Payment payment;
    @JoinColumn(name = "orderstatusid", referencedColumnName = "orderstatusid")
    @ManyToOne(optional = false)
    private Orderstatus orderstatusid;
    @JoinColumn(name = "buyer", referencedColumnName = "siteuserid")
    @ManyToOne(optional = false)
    private Siteuser buyer;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "productorderid")
    private Shippingdetails shippingdetails;

    public Productorder() {
    }

    public Productorder(Integer productorderid) {
        this.productorderid = productorderid;
    }

    public Productorder(Integer productorderid, Date datecreated, Date timemodified) {
        this.productorderid = productorderid;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getProductorderid() {
        return productorderid;
    }

    public void setProductorderid(Integer productorderid) {
        this.productorderid = productorderid;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
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

    @XmlTransient
    public List<Orderproduct> getOrderproductList() {
        return orderproductList;
    }

    public void setOrderproductList(List<Orderproduct> orderproductList) {
        this.orderproductList = orderproductList;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Orderstatus getOrderstatusid() {
        return orderstatusid;
    }

    public void setOrderstatusid(Orderstatus orderstatusid) {
        this.orderstatusid = orderstatusid;
    }

    public Siteuser getBuyer() {
        return buyer;
    }

    public void setBuyer(Siteuser buyer) {
        this.buyer = buyer;
    }

    public Shippingdetails getShippingdetails() {
        return shippingdetails;
    }

    public void setShippingdetails(Shippingdetails shippingdetails) {
        this.shippingdetails = shippingdetails;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productorderid != null ? productorderid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productorder)) {
            return false;
        }
        Productorder other = (Productorder) object;
        if ((this.productorderid == null && other.productorderid != null) || (this.productorderid != null && !this.productorderid.equals(other.productorderid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Productorder[ productorderid=" + productorderid + " ]";
    }

}

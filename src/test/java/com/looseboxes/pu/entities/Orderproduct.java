package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @(#)Orderproduct.java   20-May-2015 15:49:49
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
@Table(name = "orderproduct")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orderproduct.findAll", query = "SELECT o FROM Orderproduct o"),
    @NamedQuery(name = "Orderproduct.findByOrderproductid", query = "SELECT o FROM Orderproduct o WHERE o.orderproductid = :orderproductid"),
    @NamedQuery(name = "Orderproduct.findByPrice", query = "SELECT o FROM Orderproduct o WHERE o.price = :price"),
    @NamedQuery(name = "Orderproduct.findByDiscount", query = "SELECT o FROM Orderproduct o WHERE o.discount = :discount"),
    @NamedQuery(name = "Orderproduct.findByQuantity", query = "SELECT o FROM Orderproduct o WHERE o.quantity = :quantity"),
    @NamedQuery(name = "Orderproduct.findByDatecreated", query = "SELECT o FROM Orderproduct o WHERE o.datecreated = :datecreated"),
    @NamedQuery(name = "Orderproduct.findByTimemodified", query = "SELECT o FROM Orderproduct o WHERE o.timemodified = :timemodified")})
public class Orderproduct implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "orderproductid")
    private Integer orderproductid;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "discount")
    private BigDecimal discount;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @JoinColumn(name = "currencyid", referencedColumnName = "currencyid")
    @ManyToOne(optional = false)
    private Currency currencyid;
    @JoinColumn(name = "productorderid", referencedColumnName = "productorderid")
    @ManyToOne(optional = false)
    private Productorder productorderid;
    @JoinColumn(name = "productvariantid", referencedColumnName = "productvariantid")
    @ManyToOne(optional = false)
    private Productvariant productvariantid;

    public Orderproduct() {
    }

    public Orderproduct(Integer orderproductid) {
        this.orderproductid = orderproductid;
    }

    public Orderproduct(Integer orderproductid, int quantity, Date datecreated, Date timemodified) {
        this.orderproductid = orderproductid;
        this.quantity = quantity;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getOrderproductid() {
        return orderproductid;
    }

    public void setOrderproductid(Integer orderproductid) {
        this.orderproductid = orderproductid;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public Currency getCurrencyid() {
        return currencyid;
    }

    public void setCurrencyid(Currency currencyid) {
        this.currencyid = currencyid;
    }

    public Productorder getProductorderid() {
        return productorderid;
    }

    public void setProductorderid(Productorder productorderid) {
        this.productorderid = productorderid;
    }

    public Productvariant getProductvariantid() {
        return productvariantid;
    }

    public void setProductvariantid(Productvariant productvariantid) {
        this.productvariantid = productvariantid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderproductid != null ? orderproductid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orderproduct)) {
            return false;
        }
        Orderproduct other = (Orderproduct) object;
        if ((this.orderproductid == null && other.orderproductid != null) || (this.orderproductid != null && !this.orderproductid.equals(other.orderproductid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Orderproduct[ orderproductid=" + orderproductid + " ]";
    }

}

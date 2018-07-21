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
 * @(#)Currency.java   20-May-2015 15:49:51
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
@Table(name = "currency")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Currency.findAll", query = "SELECT c FROM Currency c"),
    @NamedQuery(name = "Currency.findByCurrencyid", query = "SELECT c FROM Currency c WHERE c.currencyid = :currencyid"),
    @NamedQuery(name = "Currency.findByCurrency", query = "SELECT c FROM Currency c WHERE c.currency = :currency"),
    @NamedQuery(name = "Currency.findBySymbol", query = "SELECT c FROM Currency c WHERE c.symbol = :symbol"),
    @NamedQuery(name = "Currency.findByDescription", query = "SELECT c FROM Currency c WHERE c.description = :description"),
    @NamedQuery(name = "Currency.findByFractionDigits", query = "SELECT c FROM Currency c WHERE c.fractionDigits = :fractionDigits")})
public class Currency implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "currencyid")
    private Short currencyid;
    @Basic(optional = false)
    @Column(name = "currency")
    private String currency;
    @Basic(optional = false)
    @Column(name = "symbol")
    private String symbol;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "fractionDigits")
    private short fractionDigits;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "currencyid")
    private List<Orderproduct> orderproductList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "currencyid")
    private List<Payment> paymentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "currencyid")
    private List<Siteuser> siteuserList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "currencyid")
    private List<Product> productList;

    public Currency() {
    }

    public Currency(Short currencyid) {
        this.currencyid = currencyid;
    }

    public Currency(Short currencyid, String currency, String symbol, String description, short fractionDigits) {
        this.currencyid = currencyid;
        this.currency = currency;
        this.symbol = symbol;
        this.description = description;
        this.fractionDigits = fractionDigits;
    }

    public Short getCurrencyid() {
        return currencyid;
    }

    public void setCurrencyid(Short currencyid) {
        this.currencyid = currencyid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(short fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    @XmlTransient
    public List<Orderproduct> getOrderproductList() {
        return orderproductList;
    }

    public void setOrderproductList(List<Orderproduct> orderproductList) {
        this.orderproductList = orderproductList;
    }

    @XmlTransient
    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @XmlTransient
    public List<Siteuser> getSiteuserList() {
        return siteuserList;
    }

    public void setSiteuserList(List<Siteuser> siteuserList) {
        this.siteuserList = siteuserList;
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
        hash += (currencyid != null ? currencyid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency) object;
        if ((this.currencyid == null && other.currencyid != null) || (this.currencyid != null && !this.currencyid.equals(other.currencyid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Currency[ currencyid=" + currencyid + " ]";
    }

}

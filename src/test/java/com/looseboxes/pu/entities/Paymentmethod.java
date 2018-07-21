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
 * @(#)Paymentmethod.java   20-May-2015 15:49:52
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
@Table(name = "paymentmethod")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paymentmethod.findAll", query = "SELECT p FROM Paymentmethod p"),
    @NamedQuery(name = "Paymentmethod.findByPaymentmethodid", query = "SELECT p FROM Paymentmethod p WHERE p.paymentmethodid = :paymentmethodid"),
    @NamedQuery(name = "Paymentmethod.findByPaymentmethod", query = "SELECT p FROM Paymentmethod p WHERE p.paymentmethod = :paymentmethod")})
public class Paymentmethod implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "paymentmethodid")
    private Short paymentmethodid;
    @Basic(optional = false)
    @Column(name = "paymentmethod")
    private String paymentmethod;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentmethodid")
    private List<Userpaymentmethod> userpaymentmethodList;

    public Paymentmethod() {
    }

    public Paymentmethod(Short paymentmethodid) {
        this.paymentmethodid = paymentmethodid;
    }

    public Paymentmethod(Short paymentmethodid, String paymentmethod) {
        this.paymentmethodid = paymentmethodid;
        this.paymentmethod = paymentmethod;
    }

    public Short getPaymentmethodid() {
        return paymentmethodid;
    }

    public void setPaymentmethodid(Short paymentmethodid) {
        this.paymentmethodid = paymentmethodid;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }

    @XmlTransient
    public List<Userpaymentmethod> getUserpaymentmethodList() {
        return userpaymentmethodList;
    }

    public void setUserpaymentmethodList(List<Userpaymentmethod> userpaymentmethodList) {
        this.userpaymentmethodList = userpaymentmethodList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (paymentmethodid != null ? paymentmethodid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paymentmethod)) {
            return false;
        }
        Paymentmethod other = (Paymentmethod) object;
        if ((this.paymentmethodid == null && other.paymentmethodid != null) || (this.paymentmethodid != null && !this.paymentmethodid.equals(other.paymentmethodid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Paymentmethod[ paymentmethodid=" + paymentmethodid + " ]";
    }

}

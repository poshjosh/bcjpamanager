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
 * @(#)Paymentstatus.java   20-May-2015 15:49:51
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
@Table(name = "paymentstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paymentstatus.findAll", query = "SELECT p FROM Paymentstatus p"),
    @NamedQuery(name = "Paymentstatus.findByPaymentstatusid", query = "SELECT p FROM Paymentstatus p WHERE p.paymentstatusid = :paymentstatusid"),
    @NamedQuery(name = "Paymentstatus.findByPaymentstatus", query = "SELECT p FROM Paymentstatus p WHERE p.paymentstatus = :paymentstatus")})
public class Paymentstatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "paymentstatusid")
    private Short paymentstatusid;
    @Basic(optional = false)
    @Column(name = "paymentstatus")
    private String paymentstatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentstatusid")
    private List<Payment> paymentList;

    public Paymentstatus() {
    }

    public Paymentstatus(Short paymentstatusid) {
        this.paymentstatusid = paymentstatusid;
    }

    public Paymentstatus(Short paymentstatusid, String paymentstatus) {
        this.paymentstatusid = paymentstatusid;
        this.paymentstatus = paymentstatus;
    }

    public Short getPaymentstatusid() {
        return paymentstatusid;
    }

    public void setPaymentstatusid(Short paymentstatusid) {
        this.paymentstatusid = paymentstatusid;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    @XmlTransient
    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (paymentstatusid != null ? paymentstatusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paymentstatus)) {
            return false;
        }
        Paymentstatus other = (Paymentstatus) object;
        if ((this.paymentstatusid == null && other.paymentstatusid != null) || (this.paymentstatusid != null && !this.paymentstatusid.equals(other.paymentstatusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Paymentstatus[ paymentstatusid=" + paymentstatusid + " ]";
    }

}

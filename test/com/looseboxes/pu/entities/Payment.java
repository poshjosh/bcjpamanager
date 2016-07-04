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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @(#)Payment.java   20-May-2015 15:49:51
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
@Table(name = "payment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Payment.findAll", query = "SELECT p FROM Payment p"),
    @NamedQuery(name = "Payment.findByPaymentid", query = "SELECT p FROM Payment p WHERE p.paymentid = :paymentid"),
    @NamedQuery(name = "Payment.findByPaymentAmount", query = "SELECT p FROM Payment p WHERE p.paymentAmount = :paymentAmount"),
    @NamedQuery(name = "Payment.findByPaymentCode", query = "SELECT p FROM Payment p WHERE p.paymentCode = :paymentCode"),
    @NamedQuery(name = "Payment.findByDatecreated", query = "SELECT p FROM Payment p WHERE p.datecreated = :datecreated"),
    @NamedQuery(name = "Payment.findByTimemodified", query = "SELECT p FROM Payment p WHERE p.timemodified = :timemodified")})
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "paymentid")
    private Integer paymentid;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "paymentAmount")
    private BigDecimal paymentAmount;
    @Basic(optional = false)
    @Column(name = "paymentCode")
    private String paymentCode;
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
    @OneToOne(optional = false)
    private Productorder productorderid;
    @JoinColumn(name = "paymentstatusid", referencedColumnName = "paymentstatusid")
    @ManyToOne(optional = false)
    private Paymentstatus paymentstatusid;
    @JoinColumn(name = "userpaymentmethodid", referencedColumnName = "userpaymentmethodid")
    @ManyToOne(optional = false)
    private Userpaymentmethod userpaymentmethodid;

    public Payment() {
    }

    public Payment(Integer paymentid) {
        this.paymentid = paymentid;
    }

    public Payment(Integer paymentid, BigDecimal paymentAmount, String paymentCode, Date datecreated, Date timemodified) {
        this.paymentid = paymentid;
        this.paymentAmount = paymentAmount;
        this.paymentCode = paymentCode;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getPaymentid() {
        return paymentid;
    }

    public void setPaymentid(Integer paymentid) {
        this.paymentid = paymentid;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
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

    public Paymentstatus getPaymentstatusid() {
        return paymentstatusid;
    }

    public void setPaymentstatusid(Paymentstatus paymentstatusid) {
        this.paymentstatusid = paymentstatusid;
    }

    public Userpaymentmethod getUserpaymentmethodid() {
        return userpaymentmethodid;
    }

    public void setUserpaymentmethodid(Userpaymentmethod userpaymentmethodid) {
        this.userpaymentmethodid = userpaymentmethodid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (paymentid != null ? paymentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) object;
        if ((this.paymentid == null && other.paymentid != null) || (this.paymentid != null && !this.paymentid.equals(other.paymentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Payment[ paymentid=" + paymentid + " ]";
    }

}

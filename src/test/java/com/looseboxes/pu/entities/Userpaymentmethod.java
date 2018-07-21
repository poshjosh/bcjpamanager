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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @(#)Userpaymentmethod.java   20-May-2015 15:49:52
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
@Table(name = "userpaymentmethod")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userpaymentmethod.findAll", query = "SELECT u FROM Userpaymentmethod u"),
    @NamedQuery(name = "Userpaymentmethod.findByUserpaymentmethodid", query = "SELECT u FROM Userpaymentmethod u WHERE u.userpaymentmethodid = :userpaymentmethodid"),
    @NamedQuery(name = "Userpaymentmethod.findByPaymentMethodUsername", query = "SELECT u FROM Userpaymentmethod u WHERE u.paymentMethodUsername = :paymentMethodUsername"),
    @NamedQuery(name = "Userpaymentmethod.findByPaymentMethodNumber", query = "SELECT u FROM Userpaymentmethod u WHERE u.paymentMethodNumber = :paymentMethodNumber"),
    @NamedQuery(name = "Userpaymentmethod.findByCode", query = "SELECT u FROM Userpaymentmethod u WHERE u.code = :code"),
    @NamedQuery(name = "Userpaymentmethod.findByExpiryDate", query = "SELECT u FROM Userpaymentmethod u WHERE u.expiryDate = :expiryDate"),
    @NamedQuery(name = "Userpaymentmethod.findByDatecreated", query = "SELECT u FROM Userpaymentmethod u WHERE u.datecreated = :datecreated"),
    @NamedQuery(name = "Userpaymentmethod.findByTimemodified", query = "SELECT u FROM Userpaymentmethod u WHERE u.timemodified = :timemodified")})
public class Userpaymentmethod implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "userpaymentmethodid")
    private Integer userpaymentmethodid;
    @Column(name = "paymentMethodUsername")
    private String paymentMethodUsername;
    @Column(name = "paymentMethodNumber")
    private String paymentMethodNumber;
    @Column(name = "code")
    private String code;
    @Column(name = "expiryDate")
    @Temporal(TemporalType.DATE)
    private Date expiryDate;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userpaymentmethodid")
    private List<Payment> paymentList;
    @JoinColumn(name = "billingAddress", referencedColumnName = "addressid")
    @ManyToOne
    private Address billingAddress;
    @JoinColumn(name = "paymentmethodid", referencedColumnName = "paymentmethodid")
    @ManyToOne(optional = false)
    private Paymentmethod paymentmethodid;
    @JoinColumn(name = "paymentmethoduser", referencedColumnName = "siteuserid")
    @ManyToOne(optional = false)
    private Siteuser paymentmethoduser;

    public Userpaymentmethod() {
    }

    public Userpaymentmethod(Integer userpaymentmethodid) {
        this.userpaymentmethodid = userpaymentmethodid;
    }

    public Userpaymentmethod(Integer userpaymentmethodid, Date datecreated, Date timemodified) {
        this.userpaymentmethodid = userpaymentmethodid;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getUserpaymentmethodid() {
        return userpaymentmethodid;
    }

    public void setUserpaymentmethodid(Integer userpaymentmethodid) {
        this.userpaymentmethodid = userpaymentmethodid;
    }

    public String getPaymentMethodUsername() {
        return paymentMethodUsername;
    }

    public void setPaymentMethodUsername(String paymentMethodUsername) {
        this.paymentMethodUsername = paymentMethodUsername;
    }

    public String getPaymentMethodNumber() {
        return paymentMethodNumber;
    }

    public void setPaymentMethodNumber(String paymentMethodNumber) {
        this.paymentMethodNumber = paymentMethodNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
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
    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Paymentmethod getPaymentmethodid() {
        return paymentmethodid;
    }

    public void setPaymentmethodid(Paymentmethod paymentmethodid) {
        this.paymentmethodid = paymentmethodid;
    }

    public Siteuser getPaymentmethoduser() {
        return paymentmethoduser;
    }

    public void setPaymentmethoduser(Siteuser paymentmethoduser) {
        this.paymentmethoduser = paymentmethoduser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userpaymentmethodid != null ? userpaymentmethodid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userpaymentmethod)) {
            return false;
        }
        Userpaymentmethod other = (Userpaymentmethod) object;
        if ((this.userpaymentmethodid == null && other.userpaymentmethodid != null) || (this.userpaymentmethodid != null && !this.userpaymentmethodid.equals(other.userpaymentmethodid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Userpaymentmethod[ userpaymentmethodid=" + userpaymentmethodid + " ]";
    }

}

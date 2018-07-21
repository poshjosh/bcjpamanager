package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @(#)Supportticket.java   20-Aug-2015 21:36:10
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
@Table(name = "supportticket")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Supportticket.findAll", query = "SELECT s FROM Supportticket s"),
    @NamedQuery(name = "Supportticket.findBySupportticketid", query = "SELECT s FROM Supportticket s WHERE s.supportticketid = :supportticketid"),
    @NamedQuery(name = "Supportticket.findByEmailAddress", query = "SELECT s FROM Supportticket s WHERE s.emailAddress = :emailAddress"),
    @NamedQuery(name = "Supportticket.findByPhoneNumber", query = "SELECT s FROM Supportticket s WHERE s.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Supportticket.findBySubject", query = "SELECT s FROM Supportticket s WHERE s.subject = :subject"),
    @NamedQuery(name = "Supportticket.findByMessage", query = "SELECT s FROM Supportticket s WHERE s.message = :message"),
    @NamedQuery(name = "Supportticket.findByIsreplyto", query = "SELECT s FROM Supportticket s WHERE s.isreplyto = :isreplyto"),
    @NamedQuery(name = "Supportticket.findByClosed", query = "SELECT s FROM Supportticket s WHERE s.closed = :closed"),
    @NamedQuery(name = "Supportticket.findByDatecreated", query = "SELECT s FROM Supportticket s WHERE s.datecreated = :datecreated")})
public class Supportticket implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "supportticketid")
    private Integer supportticketid;
    @Basic(optional = false)
    @Column(name = "emailAddress")
    private String emailAddress;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "subject")
    private String subject;
    @Basic(optional = false)
    @Column(name = "message")
    private String message;
    @Column(name = "isreplyto")
    private Integer isreplyto;
    @Basic(optional = false)
    @Column(name = "closed")
    private boolean closed;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "supportticket1")
    private Supportticket supportticket;
    @JoinColumn(name = "supportticketid", referencedColumnName = "supportticketid", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Supportticket supportticket1;

    public Supportticket() {
    }

    public Supportticket(Integer supportticketid) {
        this.supportticketid = supportticketid;
    }

    public Supportticket(Integer supportticketid, String emailAddress, String message, boolean closed, Date datecreated) {
        this.supportticketid = supportticketid;
        this.emailAddress = emailAddress;
        this.message = message;
        this.closed = closed;
        this.datecreated = datecreated;
    }

    public Integer getSupportticketid() {
        return supportticketid;
    }

    public void setSupportticketid(Integer supportticketid) {
        this.supportticketid = supportticketid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIsreplyto() {
        return isreplyto;
    }

    public void setIsreplyto(Integer isreplyto) {
        this.isreplyto = isreplyto;
    }

    public boolean getClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public Supportticket getSupportticket() {
        return supportticket;
    }

    public void setSupportticket(Supportticket supportticket) {
        this.supportticket = supportticket;
    }

    public Supportticket getSupportticket1() {
        return supportticket1;
    }

    public void setSupportticket1(Supportticket supportticket1) {
        this.supportticket1 = supportticket1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (supportticketid != null ? supportticketid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Supportticket)) {
            return false;
        }
        Supportticket other = (Supportticket) object;
        if ((this.supportticketid == null && other.supportticketid != null) || (this.supportticketid != null && !this.supportticketid.equals(other.supportticketid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Supportticket[ supportticketid=" + supportticketid + " ]";
    }

}

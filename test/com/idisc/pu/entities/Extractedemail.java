/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.idisc.pu.entities;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 10:53:11 PM
 */
@Entity
@Table(name = "extractedemail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Extractedemail.findAll", query = "SELECT e FROM Extractedemail e"),
    @NamedQuery(name = "Extractedemail.findByExtractedemailid", query = "SELECT e FROM Extractedemail e WHERE e.extractedemailid = :extractedemailid"),
    @NamedQuery(name = "Extractedemail.findByEmailAddress", query = "SELECT e FROM Extractedemail e WHERE e.emailAddress = :emailAddress"),
    @NamedQuery(name = "Extractedemail.findByUsername", query = "SELECT e FROM Extractedemail e WHERE e.username = :username"),
    @NamedQuery(name = "Extractedemail.findByDatecreated", query = "SELECT e FROM Extractedemail e WHERE e.datecreated = :datecreated"),
    @NamedQuery(name = "Extractedemail.findByTimemodified", query = "SELECT e FROM Extractedemail e WHERE e.timemodified = :timemodified"),
    @NamedQuery(name = "Extractedemail.findByExtradetails", query = "SELECT e FROM Extractedemail e WHERE e.extradetails = :extradetails")})
public class Extractedemail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "extractedemailid")
    private Integer extractedemailid;
    @Basic(optional = false)
    @Column(name = "emailAddress")
    private String emailAddress;
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @Column(name = "extradetails")
    private String extradetails;
    @JoinColumn(name = "installationid", referencedColumnName = "installationid")
    @ManyToOne(optional = false)
    private Installation installationid;
    @JoinColumn(name = "emailstatus", referencedColumnName = "emailstatusid")
    @ManyToOne(optional = false)
    private Emailstatus emailstatus;

    public Extractedemail() {
    }

    public Extractedemail(Integer extractedemailid) {
        this.extractedemailid = extractedemailid;
    }

    public Extractedemail(Integer extractedemailid, String emailAddress, Date datecreated, Date timemodified) {
        this.extractedemailid = extractedemailid;
        this.emailAddress = emailAddress;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getExtractedemailid() {
        return extractedemailid;
    }

    public void setExtractedemailid(Integer extractedemailid) {
        this.extractedemailid = extractedemailid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getExtradetails() {
        return extradetails;
    }

    public void setExtradetails(String extradetails) {
        this.extradetails = extradetails;
    }

    public Installation getInstallationid() {
        return installationid;
    }

    public void setInstallationid(Installation installationid) {
        this.installationid = installationid;
    }

    public Emailstatus getEmailstatus() {
        return emailstatus;
    }

    public void setEmailstatus(Emailstatus emailstatus) {
        this.emailstatus = emailstatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (extractedemailid != null ? extractedemailid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Extractedemail)) {
            return false;
        }
        Extractedemail other = (Extractedemail) object;
        if ((this.extractedemailid == null && other.extractedemailid != null) || (this.extractedemailid != null && !this.extractedemailid.equals(other.extractedemailid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Extractedemail[ extractedemailid=" + extractedemailid + " ]";
    }

}

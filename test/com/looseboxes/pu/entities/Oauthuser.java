package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @(#)Oauthuser.java   20-Aug-2015 19:13:52
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
@Table(name = "oauthuser")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Oauthuser.findAll", query = "SELECT o FROM Oauthuser o"),
    @NamedQuery(name = "Oauthuser.findByEmailAddress", query = "SELECT o FROM Oauthuser o WHERE o.emailAddress = :emailAddress"),
    @NamedQuery(name = "Oauthuser.findByAccessToken", query = "SELECT o FROM Oauthuser o WHERE o.accessToken = :accessToken"),
    @NamedQuery(name = "Oauthuser.findBySecret", query = "SELECT o FROM Oauthuser o WHERE o.secret = :secret"),
    @NamedQuery(name = "Oauthuser.findByDatecreated", query = "SELECT o FROM Oauthuser o WHERE o.datecreated = :datecreated")})
public class Oauthuser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "emailAddress")
    private String emailAddress;
    @Basic(optional = false)
    @Column(name = "accessToken")
    private String accessToken;
    @Column(name = "secret")
    private String secret;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;

    public Oauthuser() {
    }

    public Oauthuser(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Oauthuser(String emailAddress, String accessToken, Date datecreated) {
        this.emailAddress = emailAddress;
        this.accessToken = accessToken;
        this.datecreated = datecreated;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (emailAddress != null ? emailAddress.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Oauthuser)) {
            return false;
        }
        Oauthuser other = (Oauthuser) object;
        if ((this.emailAddress == null && other.emailAddress != null) || (this.emailAddress != null && !this.emailAddress.equals(other.emailAddress))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Oauthuser[ emailAddress=" + emailAddress + " ]";
    }

}

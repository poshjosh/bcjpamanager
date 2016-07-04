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
 * @(#)Userstatus.java   20-May-2015 15:49:51
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
@Table(name = "userstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userstatus.findAll", query = "SELECT u FROM Userstatus u"),
    @NamedQuery(name = "Userstatus.findByUserstatusid", query = "SELECT u FROM Userstatus u WHERE u.userstatusid = :userstatusid"),
    @NamedQuery(name = "Userstatus.findByUserstatus", query = "SELECT u FROM Userstatus u WHERE u.userstatus = :userstatus")})
public class Userstatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "userstatusid")
    private Short userstatusid;
    @Basic(optional = false)
    @Column(name = "userstatus")
    private String userstatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userstatusid")
    private List<Siteuser> siteuserList;

    public Userstatus() {
    }

    public Userstatus(Short userstatusid) {
        this.userstatusid = userstatusid;
    }

    public Userstatus(Short userstatusid, String userstatus) {
        this.userstatusid = userstatusid;
        this.userstatus = userstatus;
    }

    public Short getUserstatusid() {
        return userstatusid;
    }

    public void setUserstatusid(Short userstatusid) {
        this.userstatusid = userstatusid;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    @XmlTransient
    public List<Siteuser> getSiteuserList() {
        return siteuserList;
    }

    public void setSiteuserList(List<Siteuser> siteuserList) {
        this.siteuserList = siteuserList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userstatusid != null ? userstatusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userstatus)) {
            return false;
        }
        Userstatus other = (Userstatus) object;
        if ((this.userstatusid == null && other.userstatusid != null) || (this.userstatusid != null && !this.userstatusid.equals(other.userstatusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Userstatus[ userstatusid=" + userstatusid + " ]";
    }

}

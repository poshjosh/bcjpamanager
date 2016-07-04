package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
 * @(#)Howdidyoufindus.java   20-May-2015 15:49:52
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
@Table(name = "howdidyoufindus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Howdidyoufindus.findAll", query = "SELECT h FROM Howdidyoufindus h"),
    @NamedQuery(name = "Howdidyoufindus.findByHowdidyoufindusid", query = "SELECT h FROM Howdidyoufindus h WHERE h.howdidyoufindusid = :howdidyoufindusid"),
    @NamedQuery(name = "Howdidyoufindus.findByHowdidyoufindus", query = "SELECT h FROM Howdidyoufindus h WHERE h.howdidyoufindus = :howdidyoufindus")})
public class Howdidyoufindus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "howdidyoufindusid")
    private Short howdidyoufindusid;
    @Basic(optional = false)
    @Column(name = "howdidyoufindus")
    private String howdidyoufindus;
    @OneToMany(mappedBy = "howdidyoufindusid")
    private List<Siteuser> siteuserList;

    public Howdidyoufindus() {
    }

    public Howdidyoufindus(Short howdidyoufindusid) {
        this.howdidyoufindusid = howdidyoufindusid;
    }

    public Howdidyoufindus(Short howdidyoufindusid, String howdidyoufindus) {
        this.howdidyoufindusid = howdidyoufindusid;
        this.howdidyoufindus = howdidyoufindus;
    }

    public Short getHowdidyoufindusid() {
        return howdidyoufindusid;
    }

    public void setHowdidyoufindusid(Short howdidyoufindusid) {
        this.howdidyoufindusid = howdidyoufindusid;
    }

    public String getHowdidyoufindus() {
        return howdidyoufindus;
    }

    public void setHowdidyoufindus(String howdidyoufindus) {
        this.howdidyoufindus = howdidyoufindus;
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
        hash += (howdidyoufindusid != null ? howdidyoufindusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Howdidyoufindus)) {
            return false;
        }
        Howdidyoufindus other = (Howdidyoufindus) object;
        if ((this.howdidyoufindusid == null && other.howdidyoufindusid != null) || (this.howdidyoufindusid != null && !this.howdidyoufindusid.equals(other.howdidyoufindusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Howdidyoufindus[ howdidyoufindusid=" + howdidyoufindusid + " ]";
    }

}

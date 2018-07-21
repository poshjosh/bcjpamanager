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
 * @(#)Shippingstatus.java   20-May-2015 15:49:51
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
@Table(name = "shippingstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Shippingstatus.findAll", query = "SELECT s FROM Shippingstatus s"),
    @NamedQuery(name = "Shippingstatus.findByShippingstatusid", query = "SELECT s FROM Shippingstatus s WHERE s.shippingstatusid = :shippingstatusid"),
    @NamedQuery(name = "Shippingstatus.findByShippingstatus", query = "SELECT s FROM Shippingstatus s WHERE s.shippingstatus = :shippingstatus")})
public class Shippingstatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "shippingstatusid")
    private Short shippingstatusid;
    @Basic(optional = false)
    @Column(name = "shippingstatus")
    private String shippingstatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shippingstatusid")
    private List<Shippingdetails> shippingdetailsList;

    public Shippingstatus() {
    }

    public Shippingstatus(Short shippingstatusid) {
        this.shippingstatusid = shippingstatusid;
    }

    public Shippingstatus(Short shippingstatusid, String shippingstatus) {
        this.shippingstatusid = shippingstatusid;
        this.shippingstatus = shippingstatus;
    }

    public Short getShippingstatusid() {
        return shippingstatusid;
    }

    public void setShippingstatusid(Short shippingstatusid) {
        this.shippingstatusid = shippingstatusid;
    }

    public String getShippingstatus() {
        return shippingstatus;
    }

    public void setShippingstatus(String shippingstatus) {
        this.shippingstatus = shippingstatus;
    }

    @XmlTransient
    public List<Shippingdetails> getShippingdetailsList() {
        return shippingdetailsList;
    }

    public void setShippingdetailsList(List<Shippingdetails> shippingdetailsList) {
        this.shippingdetailsList = shippingdetailsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (shippingstatusid != null ? shippingstatusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shippingstatus)) {
            return false;
        }
        Shippingstatus other = (Shippingstatus) object;
        if ((this.shippingstatusid == null && other.shippingstatusid != null) || (this.shippingstatusid != null && !this.shippingstatusid.equals(other.shippingstatusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Shippingstatus[ shippingstatusid=" + shippingstatusid + " ]";
    }

}

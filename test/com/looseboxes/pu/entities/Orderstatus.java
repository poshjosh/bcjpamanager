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
 * @(#)Orderstatus.java   20-May-2015 15:49:51
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
@Table(name = "orderstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orderstatus.findAll", query = "SELECT o FROM Orderstatus o"),
    @NamedQuery(name = "Orderstatus.findByOrderstatusid", query = "SELECT o FROM Orderstatus o WHERE o.orderstatusid = :orderstatusid"),
    @NamedQuery(name = "Orderstatus.findByOrderstatus", query = "SELECT o FROM Orderstatus o WHERE o.orderstatus = :orderstatus")})
public class Orderstatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "orderstatusid")
    private Short orderstatusid;
    @Basic(optional = false)
    @Column(name = "orderstatus")
    private String orderstatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderstatusid")
    private List<Productorder> productorderList;

    public Orderstatus() {
    }

    public Orderstatus(Short orderstatusid) {
        this.orderstatusid = orderstatusid;
    }

    public Orderstatus(Short orderstatusid, String orderstatus) {
        this.orderstatusid = orderstatusid;
        this.orderstatus = orderstatus;
    }

    public Short getOrderstatusid() {
        return orderstatusid;
    }

    public void setOrderstatusid(Short orderstatusid) {
        this.orderstatusid = orderstatusid;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    @XmlTransient
    public List<Productorder> getProductorderList() {
        return productorderList;
    }

    public void setProductorderList(List<Productorder> productorderList) {
        this.productorderList = productorderList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderstatusid != null ? orderstatusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orderstatus)) {
            return false;
        }
        Orderstatus other = (Orderstatus) object;
        if ((this.orderstatusid == null && other.orderstatusid != null) || (this.orderstatusid != null && !this.orderstatusid.equals(other.orderstatusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Orderstatus[ orderstatusid=" + orderstatusid + " ]";
    }

}

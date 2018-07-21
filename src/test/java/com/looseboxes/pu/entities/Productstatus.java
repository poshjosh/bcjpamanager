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
 * @(#)Productstatus.java   20-May-2015 15:49:52
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
@Table(name = "productstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productstatus.findAll", query = "SELECT p FROM Productstatus p"),
    @NamedQuery(name = "Productstatus.findByProductstatusid", query = "SELECT p FROM Productstatus p WHERE p.productstatusid = :productstatusid"),
    @NamedQuery(name = "Productstatus.findByProductstatus", query = "SELECT p FROM Productstatus p WHERE p.productstatus = :productstatus")})
public class Productstatus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "productstatusid")
    private Short productstatusid;
    @Basic(optional = false)
    @Column(name = "productstatus")
    private String productstatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productstatusid")
    private List<Product> productList;

    public Productstatus() {
    }

    public Productstatus(Short productstatusid) {
        this.productstatusid = productstatusid;
    }

    public Productstatus(Short productstatusid, String productstatus) {
        this.productstatusid = productstatusid;
        this.productstatus = productstatus;
    }

    public Short getProductstatusid() {
        return productstatusid;
    }

    public void setProductstatusid(Short productstatusid) {
        this.productstatusid = productstatusid;
    }

    public String getProductstatus() {
        return productstatus;
    }

    public void setProductstatus(String productstatus) {
        this.productstatus = productstatus;
    }

    @XmlTransient
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productstatusid != null ? productstatusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productstatus)) {
            return false;
        }
        Productstatus other = (Productstatus) object;
        if ((this.productstatusid == null && other.productstatusid != null) || (this.productstatusid != null && !this.productstatusid.equals(other.productstatusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Productstatus[ productstatusid=" + productstatusid + " ]";
    }

}

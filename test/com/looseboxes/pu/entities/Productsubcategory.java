package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @(#)Productsubcategory.java   20-May-2015 15:49:51
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
@Table(name = "productsubcategory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productsubcategory.findAll", query = "SELECT p FROM Productsubcategory p"),
    @NamedQuery(name = "Productsubcategory.findByProductsubcategoryid", query = "SELECT p FROM Productsubcategory p WHERE p.productsubcategoryid = :productsubcategoryid"),
    @NamedQuery(name = "Productsubcategory.findByProductsubcategory", query = "SELECT p FROM Productsubcategory p WHERE p.productsubcategory = :productsubcategory"),
    @NamedQuery(name = "Productsubcategory.findByProductsubcategorySortorder", query = "SELECT p FROM Productsubcategory p WHERE p.productsubcategorySortorder = :productsubcategorySortorder")})
public class Productsubcategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "productsubcategoryid")
    private Short productsubcategoryid;
    @Basic(optional = false)
    @Column(name = "productsubcategory")
    private String productsubcategory;
    @Column(name = "productsubcategory_sortorder")
    private Short productsubcategorySortorder;
    @JoinColumn(name = "productcategoryid", referencedColumnName = "productcategoryid")
    @ManyToOne(optional = false)
    private Productcategory productcategoryid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productsubcategoryid")
    private List<Product> productList;

    public Productsubcategory() {
    }

    public Productsubcategory(Short productsubcategoryid) {
        this.productsubcategoryid = productsubcategoryid;
    }

    public Productsubcategory(Short productsubcategoryid, String productsubcategory) {
        this.productsubcategoryid = productsubcategoryid;
        this.productsubcategory = productsubcategory;
    }

    public Short getProductsubcategoryid() {
        return productsubcategoryid;
    }

    public void setProductsubcategoryid(Short productsubcategoryid) {
        this.productsubcategoryid = productsubcategoryid;
    }

    public String getProductsubcategory() {
        return productsubcategory;
    }

    public void setProductsubcategory(String productsubcategory) {
        this.productsubcategory = productsubcategory;
    }

    public Short getProductsubcategorySortorder() {
        return productsubcategorySortorder;
    }

    public void setProductsubcategorySortorder(Short productsubcategorySortorder) {
        this.productsubcategorySortorder = productsubcategorySortorder;
    }

    public Productcategory getProductcategoryid() {
        return productcategoryid;
    }

    public void setProductcategoryid(Productcategory productcategoryid) {
        this.productcategoryid = productcategoryid;
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
        hash += (productsubcategoryid != null ? productsubcategoryid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productsubcategory)) {
            return false;
        }
        Productsubcategory other = (Productsubcategory) object;
        if ((this.productsubcategoryid == null && other.productsubcategoryid != null) || (this.productsubcategoryid != null && !this.productsubcategoryid.equals(other.productsubcategoryid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Productsubcategory[ productsubcategoryid=" + productsubcategoryid + " ]";
    }

}

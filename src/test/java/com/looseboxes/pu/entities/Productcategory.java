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
 * @(#)Productcategory.java   20-May-2015 15:49:51
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
@Table(name = "productcategory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productcategory.findAll", query = "SELECT p FROM Productcategory p"),
    @NamedQuery(name = "Productcategory.findByProductcategoryid", query = "SELECT p FROM Productcategory p WHERE p.productcategoryid = :productcategoryid"),
    @NamedQuery(name = "Productcategory.findByProductcategory", query = "SELECT p FROM Productcategory p WHERE p.productcategory = :productcategory"),
    @NamedQuery(name = "Productcategory.findByProductcategorySortorder", query = "SELECT p FROM Productcategory p WHERE p.productcategorySortorder = :productcategorySortorder")})
public class Productcategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "productcategoryid")
    private Short productcategoryid;
    @Basic(optional = false)
    @Column(name = "productcategory")
    private String productcategory;
    @Column(name = "productcategory_sortorder")
    private Short productcategorySortorder;
    @JoinColumn(name = "itemtypeid", referencedColumnName = "itemtypeid")
    @ManyToOne(optional = false)
    private Itemtype itemtypeid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productcategoryid")
    private List<Productsubcategory> productsubcategoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productcategoryid")
    private List<Product> productList;

    public Productcategory() {
    }

    public Productcategory(Short productcategoryid) {
        this.productcategoryid = productcategoryid;
    }

    public Productcategory(Short productcategoryid, String productcategory) {
        this.productcategoryid = productcategoryid;
        this.productcategory = productcategory;
    }

    public Short getProductcategoryid() {
        return productcategoryid;
    }

    public void setProductcategoryid(Short productcategoryid) {
        this.productcategoryid = productcategoryid;
    }

    public String getProductcategory() {
        return productcategory;
    }

    public void setProductcategory(String productcategory) {
        this.productcategory = productcategory;
    }

    public Short getProductcategorySortorder() {
        return productcategorySortorder;
    }

    public void setProductcategorySortorder(Short productcategorySortorder) {
        this.productcategorySortorder = productcategorySortorder;
    }

    public Itemtype getItemtypeid() {
        return itemtypeid;
    }

    public void setItemtypeid(Itemtype itemtypeid) {
        this.itemtypeid = itemtypeid;
    }

    @XmlTransient
    public List<Productsubcategory> getProductsubcategoryList() {
        return productsubcategoryList;
    }

    public void setProductsubcategoryList(List<Productsubcategory> productsubcategoryList) {
        this.productsubcategoryList = productsubcategoryList;
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
        hash += (productcategoryid != null ? productcategoryid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productcategory)) {
            return false;
        }
        Productcategory other = (Productcategory) object;
        if ((this.productcategoryid == null && other.productcategoryid != null) || (this.productcategoryid != null && !this.productcategoryid.equals(other.productcategoryid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Productcategory[ productcategoryid=" + productcategoryid + " ]";
    }

}

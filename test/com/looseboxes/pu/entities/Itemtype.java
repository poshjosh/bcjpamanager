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
 * @(#)Itemtype.java   20-May-2015 15:49:52
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
@Table(name = "itemtype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Itemtype.findAll", query = "SELECT i FROM Itemtype i"),
    @NamedQuery(name = "Itemtype.findByItemtypeid", query = "SELECT i FROM Itemtype i WHERE i.itemtypeid = :itemtypeid"),
    @NamedQuery(name = "Itemtype.findByItemtype", query = "SELECT i FROM Itemtype i WHERE i.itemtype = :itemtype")})
public class Itemtype implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "itemtypeid")
    private Short itemtypeid;
    @Basic(optional = false)
    @Column(name = "itemtype")
    private String itemtype;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemtypeid")
    private List<Productcategory> productcategoryList;
    @OneToMany(mappedBy = "parenttype")
    private List<Itemtype> itemtypeList;
    @JoinColumn(name = "parenttype", referencedColumnName = "itemtypeid")
    @ManyToOne
    private Itemtype parenttype;

    public Itemtype() {
    }

    public Itemtype(Short itemtypeid) {
        this.itemtypeid = itemtypeid;
    }

    public Itemtype(Short itemtypeid, String itemtype) {
        this.itemtypeid = itemtypeid;
        this.itemtype = itemtype;
    }

    public Short getItemtypeid() {
        return itemtypeid;
    }

    public void setItemtypeid(Short itemtypeid) {
        this.itemtypeid = itemtypeid;
    }

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    @XmlTransient
    public List<Productcategory> getProductcategoryList() {
        return productcategoryList;
    }

    public void setProductcategoryList(List<Productcategory> productcategoryList) {
        this.productcategoryList = productcategoryList;
    }

    @XmlTransient
    public List<Itemtype> getItemtypeList() {
        return itemtypeList;
    }

    public void setItemtypeList(List<Itemtype> itemtypeList) {
        this.itemtypeList = itemtypeList;
    }

    public Itemtype getParenttype() {
        return parenttype;
    }

    public void setParenttype(Itemtype parenttype) {
        this.parenttype = parenttype;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (itemtypeid != null ? itemtypeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Itemtype)) {
            return false;
        }
        Itemtype other = (Itemtype) object;
        if ((this.itemtypeid == null && other.itemtypeid != null) || (this.itemtypeid != null && !this.itemtypeid.equals(other.itemtypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Itemtype[ itemtypeid=" + itemtypeid + " ]";
    }

}

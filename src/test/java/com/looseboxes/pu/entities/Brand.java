package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @(#)Brand.java   20-May-2015 15:49:52
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
@Table(name = "brand")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Brand.findAll", query = "SELECT b FROM Brand b"),
    @NamedQuery(name = "Brand.findByBrandid", query = "SELECT b FROM Brand b WHERE b.brandid = :brandid"),
    @NamedQuery(name = "Brand.findByBrandName", query = "SELECT b FROM Brand b WHERE b.brandName = :brandName"),
    @NamedQuery(name = "Brand.findByDescription", query = "SELECT b FROM Brand b WHERE b.description = :description"),
    @NamedQuery(name = "Brand.findByLogo", query = "SELECT b FROM Brand b WHERE b.logo = :logo"),
    @NamedQuery(name = "Brand.findByImage", query = "SELECT b FROM Brand b WHERE b.image = :image"),
    @NamedQuery(name = "Brand.findByUrl", query = "SELECT b FROM Brand b WHERE b.url = :url")})
public class Brand implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "brandid")
    private Short brandid;
    @Basic(optional = false)
    @Column(name = "brandName")
    private String brandName;
    @Column(name = "description")
    private String description;
    @Column(name = "logo")
    private String logo;
    @Column(name = "image")
    private String image;
    @Column(name = "url")
    private String url;
    @OneToMany(mappedBy = "brandid")
    private List<Product> productList;

    public Brand() {
    }

    public Brand(Short brandid) {
        this.brandid = brandid;
    }

    public Brand(Short brandid, String brandName) {
        this.brandid = brandid;
        this.brandName = brandName;
    }

    public Short getBrandid() {
        return brandid;
    }

    public void setBrandid(Short brandid) {
        this.brandid = brandid;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        hash += (brandid != null ? brandid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Brand)) {
            return false;
        }
        Brand other = (Brand) object;
        if ((this.brandid == null && other.brandid != null) || (this.brandid != null && !this.brandid.equals(other.brandid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Brand[ brandid=" + brandid + " ]";
    }

}

package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * @(#)Productvariant.java   20-May-2015 15:49:51
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
@Table(name = "productvariant")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productvariant.findAll", query = "SELECT p FROM Productvariant p"),
    @NamedQuery(name = "Productvariant.findByProductvariantid", query = "SELECT p FROM Productvariant p WHERE p.productvariantid = :productvariantid"),
    @NamedQuery(name = "Productvariant.findByColor", query = "SELECT p FROM Productvariant p WHERE p.color = :color"),
    @NamedQuery(name = "Productvariant.findByProductSize", query = "SELECT p FROM Productvariant p WHERE p.productSize = :productSize"),
    @NamedQuery(name = "Productvariant.findByWeight", query = "SELECT p FROM Productvariant p WHERE p.weight = :weight"),
    @NamedQuery(name = "Productvariant.findByQuantityInStock", query = "SELECT p FROM Productvariant p WHERE p.quantityInStock = :quantityInStock"),
    @NamedQuery(name = "Productvariant.findByImage1", query = "SELECT p FROM Productvariant p WHERE p.image1 = :image1"),
    @NamedQuery(name = "Productvariant.findByImage2", query = "SELECT p FROM Productvariant p WHERE p.image2 = :image2"),
    @NamedQuery(name = "Productvariant.findByImage3", query = "SELECT p FROM Productvariant p WHERE p.image3 = :image3"),
    @NamedQuery(name = "Productvariant.findByImage4", query = "SELECT p FROM Productvariant p WHERE p.image4 = :image4"),
    @NamedQuery(name = "Productvariant.findByImage5", query = "SELECT p FROM Productvariant p WHERE p.image5 = :image5"),
    @NamedQuery(name = "Productvariant.findByImage6", query = "SELECT p FROM Productvariant p WHERE p.image6 = :image6"),
    @NamedQuery(name = "Productvariant.findByImage7", query = "SELECT p FROM Productvariant p WHERE p.image7 = :image7"),
    @NamedQuery(name = "Productvariant.findByDatecreated", query = "SELECT p FROM Productvariant p WHERE p.datecreated = :datecreated"),
    @NamedQuery(name = "Productvariant.findByTimemodified", query = "SELECT p FROM Productvariant p WHERE p.timemodified = :timemodified")})
public class Productvariant implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "productvariantid")
    private Integer productvariantid;
    @Column(name = "color")
    private String color;
    @Column(name = "productSize")
    private String productSize;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "weight")
    private BigDecimal weight;
    @Basic(optional = false)
    @Column(name = "quantityInStock")
    private int quantityInStock;
    @Column(name = "image1")
    private String image1;
    @Column(name = "image2")
    private String image2;
    @Column(name = "image3")
    private String image3;
    @Column(name = "image4")
    private String image4;
    @Column(name = "image5")
    private String image5;
    @Column(name = "image6")
    private String image6;
    @Column(name = "image7")
    private String image7;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productvariantid")
    private List<Orderproduct> orderproductList;
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    @ManyToOne(optional = false)
    private Product productid;

    public Productvariant() {
    }

    public Productvariant(Integer productvariantid) {
        this.productvariantid = productvariantid;
    }

    public Productvariant(Integer productvariantid, int quantityInStock, Date datecreated, Date timemodified) {
        this.productvariantid = productvariantid;
        this.quantityInStock = quantityInStock;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getProductvariantid() {
        return productvariantid;
    }

    public void setProductvariantid(Integer productvariantid) {
        this.productvariantid = productvariantid;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getImage5() {
        return image5;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    public String getImage6() {
        return image6;
    }

    public void setImage6(String image6) {
        this.image6 = image6;
    }

    public String getImage7() {
        return image7;
    }

    public void setImage7(String image7) {
        this.image7 = image7;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public Date getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(Date timemodified) {
        this.timemodified = timemodified;
    }

    @XmlTransient
    public List<Orderproduct> getOrderproductList() {
        return orderproductList;
    }

    public void setOrderproductList(List<Orderproduct> orderproductList) {
        this.orderproductList = orderproductList;
    }

    public Product getProductid() {
        return productid;
    }

    public void setProductid(Product productid) {
        this.productid = productid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productvariantid != null ? productvariantid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productvariant)) {
            return false;
        }
        Productvariant other = (Productvariant) object;
        if ((this.productvariantid == null && other.productvariantid != null) || (this.productvariantid != null && !this.productvariantid.equals(other.productvariantid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Productvariant[ productvariantid=" + productvariantid + " ]";
    }

}

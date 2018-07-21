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
 * @(#)Product.java   20-May-2015 15:49:52
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
@Table(name = "product")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findByProductid", query = "SELECT p FROM Product p WHERE p.productid = :productid"),
    @NamedQuery(name = "Product.findByProductName", query = "SELECT p FROM Product p WHERE p.productName = :productName"),
    @NamedQuery(name = "Product.findByModel", query = "SELECT p FROM Product p WHERE p.model = :model"),
    @NamedQuery(name = "Product.findByKeywords", query = "SELECT p FROM Product p WHERE p.keywords = :keywords"),
    @NamedQuery(name = "Product.findByDescription", query = "SELECT p FROM Product p WHERE p.description = :description"),
    @NamedQuery(name = "Product.findByPrice", query = "SELECT p FROM Product p WHERE p.price = :price"),
    @NamedQuery(name = "Product.findByDiscount", query = "SELECT p FROM Product p WHERE p.discount = :discount"),
    @NamedQuery(name = "Product.findByMinimumOrderQuantity", query = "SELECT p FROM Product p WHERE p.minimumOrderQuantity = :minimumOrderQuantity"),
    @NamedQuery(name = "Product.findByLogo", query = "SELECT p FROM Product p WHERE p.logo = :logo"),
    @NamedQuery(name = "Product.findByUrl", query = "SELECT p FROM Product p WHERE p.url = :url"),
    @NamedQuery(name = "Product.findByDateOfManufacture", query = "SELECT p FROM Product p WHERE p.dateOfManufacture = :dateOfManufacture"),
    @NamedQuery(name = "Product.findByRatingPercent", query = "SELECT p FROM Product p WHERE p.ratingPercent = :ratingPercent"),
    @NamedQuery(name = "Product.findByViews", query = "SELECT p FROM Product p WHERE p.views = :views"),
    @NamedQuery(name = "Product.findByValidFrom", query = "SELECT p FROM Product p WHERE p.validFrom = :validFrom"),
    @NamedQuery(name = "Product.findByValidThrough", query = "SELECT p FROM Product p WHERE p.validThrough = :validThrough"),
    @NamedQuery(name = "Product.findByDatecreated", query = "SELECT p FROM Product p WHERE p.datecreated = :datecreated"),
    @NamedQuery(name = "Product.findByTimemodified", query = "SELECT p FROM Product p WHERE p.timemodified = :timemodified")})
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "productid")
    private Integer productid;
    @Basic(optional = false)
    @Column(name = "productName")
    private String productName;
    @Column(name = "model")
    private String model;
    @Column(name = "keywords")
    private String keywords;
    @Column(name = "description")
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "discount")
    private BigDecimal discount;
    @Basic(optional = false)
    @Column(name = "minimumOrderQuantity")
    private int minimumOrderQuantity;
    @Column(name = "logo")
    private String logo;
    @Column(name = "url")
    private String url;
    @Column(name = "dateOfManufacture")
    @Temporal(TemporalType.DATE)
    private Date dateOfManufacture;
    @Column(name = "ratingPercent")
    private Short ratingPercent;
    @Basic(optional = false)
    @Column(name = "views")
    private int views;
    @Column(name = "validFrom")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;
    @Column(name = "validThrough")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validThrough;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productid")
    private List<Productvariant> productvariantList;
    @JoinColumn(name = "seller", referencedColumnName = "siteuserid")
    @ManyToOne(optional = false)
    private Siteuser seller;
    @JoinColumn(name = "productcategoryid", referencedColumnName = "productcategoryid")
    @ManyToOne(optional = false)
    private Productcategory productcategoryid;
    @JoinColumn(name = "productsubcategoryid", referencedColumnName = "productsubcategoryid")
    @ManyToOne(optional = false)
    private Productsubcategory productsubcategoryid;
    @JoinColumn(name = "productstatusid", referencedColumnName = "productstatusid")
    @ManyToOne(optional = false)
    private Productstatus productstatusid;
    @JoinColumn(name = "brandid", referencedColumnName = "brandid")
    @ManyToOne
    private Brand brandid;
    @OneToMany(mappedBy = "isRelatedTo")
    private List<Product> productList;
    @JoinColumn(name = "isRelatedTo", referencedColumnName = "productid")
    @ManyToOne
    private Product isRelatedTo;
    @JoinColumn(name = "availabilityid", referencedColumnName = "availabilityid")
    @ManyToOne(optional = false)
    private Availability availabilityid;
    @JoinColumn(name = "availableAtOrFrom", referencedColumnName = "addressid")
    @ManyToOne
    private Address availableAtOrFrom;
    @JoinColumn(name = "currencyid", referencedColumnName = "currencyid")
    @ManyToOne(optional = false)
    private Currency currencyid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productid")
    private List<Productcomment> productcommentList;

    public Product() {
    }

    public Product(Integer productid) {
        this.productid = productid;
    }

    public Product(Integer productid, String productName, int minimumOrderQuantity, int views, Date datecreated, Date timemodified) {
        this.productid = productid;
        this.productName = productName;
        this.minimumOrderQuantity = minimumOrderQuantity;
        this.views = views;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getMinimumOrderQuantity() {
        return minimumOrderQuantity;
    }

    public void setMinimumOrderQuantity(int minimumOrderQuantity) {
        this.minimumOrderQuantity = minimumOrderQuantity;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateOfManufacture() {
        return dateOfManufacture;
    }

    public void setDateOfManufacture(Date dateOfManufacture) {
        this.dateOfManufacture = dateOfManufacture;
    }

    public Short getRatingPercent() {
        return ratingPercent;
    }

    public void setRatingPercent(Short ratingPercent) {
        this.ratingPercent = ratingPercent;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidThrough() {
        return validThrough;
    }

    public void setValidThrough(Date validThrough) {
        this.validThrough = validThrough;
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
    public List<Productvariant> getProductvariantList() {
        return productvariantList;
    }

    public void setProductvariantList(List<Productvariant> productvariantList) {
        this.productvariantList = productvariantList;
    }

    public Siteuser getSeller() {
        return seller;
    }

    public void setSeller(Siteuser seller) {
        this.seller = seller;
    }

    public Productcategory getProductcategoryid() {
        return productcategoryid;
    }

    public void setProductcategoryid(Productcategory productcategoryid) {
        this.productcategoryid = productcategoryid;
    }

    public Productsubcategory getProductsubcategoryid() {
        return productsubcategoryid;
    }

    public void setProductsubcategoryid(Productsubcategory productsubcategoryid) {
        this.productsubcategoryid = productsubcategoryid;
    }

    public Productstatus getProductstatusid() {
        return productstatusid;
    }

    public void setProductstatusid(Productstatus productstatusid) {
        this.productstatusid = productstatusid;
    }

    public Brand getBrandid() {
        return brandid;
    }

    public void setBrandid(Brand brandid) {
        this.brandid = brandid;
    }

    @XmlTransient
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Product getIsRelatedTo() {
        return isRelatedTo;
    }

    public void setIsRelatedTo(Product isRelatedTo) {
        this.isRelatedTo = isRelatedTo;
    }

    public Availability getAvailabilityid() {
        return availabilityid;
    }

    public void setAvailabilityid(Availability availabilityid) {
        this.availabilityid = availabilityid;
    }

    public Address getAvailableAtOrFrom() {
        return availableAtOrFrom;
    }

    public void setAvailableAtOrFrom(Address availableAtOrFrom) {
        this.availableAtOrFrom = availableAtOrFrom;
    }

    public Currency getCurrencyid() {
        return currencyid;
    }

    public void setCurrencyid(Currency currencyid) {
        this.currencyid = currencyid;
    }

    @XmlTransient
    public List<Productcomment> getProductcommentList() {
        return productcommentList;
    }

    public void setProductcommentList(List<Productcomment> productcommentList) {
        this.productcommentList = productcommentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productid != null ? productid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.productid == null && other.productid != null) || (this.productid != null && !this.productid.equals(other.productid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Product[ productid=" + productid + " ]";
    }

}

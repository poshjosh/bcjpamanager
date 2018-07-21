package com.looseboxes.pu.entities;

import java.io.Serializable;
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
 * @(#)Address.java   20-May-2015 15:49:52
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
@Table(name = "address")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Address.findAll", query = "SELECT a FROM Address a"),
    @NamedQuery(name = "Address.findByAddressid", query = "SELECT a FROM Address a WHERE a.addressid = :addressid"),
    @NamedQuery(name = "Address.findByCity", query = "SELECT a FROM Address a WHERE a.city = :city"),
    @NamedQuery(name = "Address.findByCounty", query = "SELECT a FROM Address a WHERE a.county = :county"),
    @NamedQuery(name = "Address.findByStreetAddress", query = "SELECT a FROM Address a WHERE a.streetAddress = :streetAddress"),
    @NamedQuery(name = "Address.findByFax", query = "SELECT a FROM Address a WHERE a.fax = :fax"),
    @NamedQuery(name = "Address.findByPostalCode", query = "SELECT a FROM Address a WHERE a.postalCode = :postalCode"),
    @NamedQuery(name = "Address.findByDatecreated", query = "SELECT a FROM Address a WHERE a.datecreated = :datecreated"),
    @NamedQuery(name = "Address.findByTimemodified", query = "SELECT a FROM Address a WHERE a.timemodified = :timemodified")})
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "addressid")
    private Integer addressid;
    @Column(name = "city")
    private String city;
    @Column(name = "county")
    private String county;
    @Column(name = "streetAddress")
    private String streetAddress;
    @Column(name = "fax")
    private String fax;
    @Column(name = "postalCode")
    private String postalCode;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(mappedBy = "addressid")
    private List<Siteuser> siteuserList;
    @OneToMany(mappedBy = "availableAtOrFrom")
    private List<Product> productList;
    @JoinColumn(name = "regionid", referencedColumnName = "regionid")
    @ManyToOne
    private Region regionid;
    @JoinColumn(name = "countryid", referencedColumnName = "countryid")
    @ManyToOne(optional = false)
    private Country countryid;
    @OneToMany(mappedBy = "billingAddress")
    private List<Userpaymentmethod> userpaymentmethodList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deliveryAddress")
    private List<Shippingdetails> shippingdetailsList;

    public Address() {
    }

    public Address(Integer addressid) {
        this.addressid = addressid;
    }

    public Address(Integer addressid, Date datecreated, Date timemodified) {
        this.addressid = addressid;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getAddressid() {
        return addressid;
    }

    public void setAddressid(Integer addressid) {
        this.addressid = addressid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
    public List<Siteuser> getSiteuserList() {
        return siteuserList;
    }

    public void setSiteuserList(List<Siteuser> siteuserList) {
        this.siteuserList = siteuserList;
    }

    @XmlTransient
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Region getRegionid() {
        return regionid;
    }

    public void setRegionid(Region regionid) {
        this.regionid = regionid;
    }

    public Country getCountryid() {
        return countryid;
    }

    public void setCountryid(Country countryid) {
        this.countryid = countryid;
    }

    @XmlTransient
    public List<Userpaymentmethod> getUserpaymentmethodList() {
        return userpaymentmethodList;
    }

    public void setUserpaymentmethodList(List<Userpaymentmethod> userpaymentmethodList) {
        this.userpaymentmethodList = userpaymentmethodList;
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
        hash += (addressid != null ? addressid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        if ((this.addressid == null && other.addressid != null) || (this.addressid != null && !this.addressid.equals(other.addressid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Address[ addressid=" + addressid + " ]";
    }

}

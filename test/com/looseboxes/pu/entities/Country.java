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
 * @(#)Country.java   20-May-2015 15:49:49
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
@Table(name = "country")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Country.findAll", query = "SELECT c FROM Country c"),
    @NamedQuery(name = "Country.findByCountryid", query = "SELECT c FROM Country c WHERE c.countryid = :countryid"),
    @NamedQuery(name = "Country.findByCountry", query = "SELECT c FROM Country c WHERE c.country = :country"),
    @NamedQuery(name = "Country.findByIsocode2", query = "SELECT c FROM Country c WHERE c.isocode2 = :isocode2"),
    @NamedQuery(name = "Country.findByIsocode3", query = "SELECT c FROM Country c WHERE c.isocode3 = :isocode3")})
public class Country implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "countryid")
    private Short countryid;
    @Basic(optional = false)
    @Column(name = "country")
    private String country;
    @Basic(optional = false)
    @Column(name = "isocode2")
    private String isocode2;
    @Basic(optional = false)
    @Column(name = "isocode3")
    private String isocode3;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "countryid")
    private List<Address> addressList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "countryid")
    private List<Region> regionList;

    public Country() {
    }

    public Country(Short countryid) {
        this.countryid = countryid;
    }

    public Country(Short countryid, String country, String isocode2, String isocode3) {
        this.countryid = countryid;
        this.country = country;
        this.isocode2 = isocode2;
        this.isocode3 = isocode3;
    }

    public Short getCountryid() {
        return countryid;
    }

    public void setCountryid(Short countryid) {
        this.countryid = countryid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsocode2() {
        return isocode2;
    }

    public void setIsocode2(String isocode2) {
        this.isocode2 = isocode2;
    }

    public String getIsocode3() {
        return isocode3;
    }

    public void setIsocode3(String isocode3) {
        this.isocode3 = isocode3;
    }

    @XmlTransient
    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    @XmlTransient
    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (countryid != null ? countryid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Country)) {
            return false;
        }
        Country other = (Country) object;
        if ((this.countryid == null && other.countryid != null) || (this.countryid != null && !this.countryid.equals(other.countryid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Country[ countryid=" + countryid + " ]";
    }

}

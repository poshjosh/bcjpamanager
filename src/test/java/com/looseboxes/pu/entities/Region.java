package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
 * @(#)Region.java   20-May-2015 15:49:52
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
@Table(name = "region")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Region.findAll", query = "SELECT r FROM Region r"),
    @NamedQuery(name = "Region.findByRegionid", query = "SELECT r FROM Region r WHERE r.regionid = :regionid"),
    @NamedQuery(name = "Region.findByRegion", query = "SELECT r FROM Region r WHERE r.region = :region"),
    @NamedQuery(name = "Region.findByRegionCode", query = "SELECT r FROM Region r WHERE r.regionCode = :regionCode")})
public class Region implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "regionid")
    private Short regionid;
    @Basic(optional = false)
    @Column(name = "region")
    private String region;
    @Basic(optional = false)
    @Column(name = "regionCode")
    private String regionCode;
    @OneToMany(mappedBy = "regionid")
    private List<Address> addressList;
    @JoinColumn(name = "countryid", referencedColumnName = "countryid")
    @ManyToOne(optional = false)
    private Country countryid;

    public Region() {
    }

    public Region(Short regionid) {
        this.regionid = regionid;
    }

    public Region(Short regionid, String region, String regionCode) {
        this.regionid = regionid;
        this.region = region;
        this.regionCode = regionCode;
    }

    public Short getRegionid() {
        return regionid;
    }

    public void setRegionid(Short regionid) {
        this.regionid = regionid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    @XmlTransient
    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Country getCountryid() {
        return countryid;
    }

    public void setCountryid(Country countryid) {
        this.countryid = countryid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (regionid != null ? regionid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Region)) {
            return false;
        }
        Region other = (Region) object;
        if ((this.regionid == null && other.regionid != null) || (this.regionid != null && !this.regionid.equals(other.regionid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Region[ regionid=" + regionid + " ]";
    }

}

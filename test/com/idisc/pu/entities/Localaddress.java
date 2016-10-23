/*
 * Copyright 2016 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idisc.pu.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
 * @author Chinomso Bassey Ikwuagwu on Oct 3, 2016 5:25:24 PM
 */
@Entity
@Table(name = "localaddress")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Localaddress.findAll", query = "SELECT l FROM Localaddress l"),
    @NamedQuery(name = "Localaddress.findByLocaladdressid", query = "SELECT l FROM Localaddress l WHERE l.localaddressid = :localaddressid"),
    @NamedQuery(name = "Localaddress.findByStateOrRegion", query = "SELECT l FROM Localaddress l WHERE l.stateOrRegion = :stateOrRegion"),
    @NamedQuery(name = "Localaddress.findByCity", query = "SELECT l FROM Localaddress l WHERE l.city = :city"),
    @NamedQuery(name = "Localaddress.findByCounty", query = "SELECT l FROM Localaddress l WHERE l.county = :county"),
    @NamedQuery(name = "Localaddress.findByStreetAddress", query = "SELECT l FROM Localaddress l WHERE l.streetAddress = :streetAddress"),
    @NamedQuery(name = "Localaddress.findByPostalCode", query = "SELECT l FROM Localaddress l WHERE l.postalCode = :postalCode"),
    @NamedQuery(name = "Localaddress.findByDatecreated", query = "SELECT l FROM Localaddress l WHERE l.datecreated = :datecreated"),
    @NamedQuery(name = "Localaddress.findByTimemodified", query = "SELECT l FROM Localaddress l WHERE l.timemodified = :timemodified"),
    @NamedQuery(name = "Localaddress.findByExtradetails", query = "SELECT l FROM Localaddress l WHERE l.extradetails = :extradetails")})
public class Localaddress implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "localaddressid")
    private Integer localaddressid;
    @Column(name = "stateOrRegion")
    private String stateOrRegion;
    @Column(name = "city")
    private String city;
    @Column(name = "county")
    private String county;
    @Column(name = "streetAddress")
    private String streetAddress;
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
    @Column(name = "extradetails")
    private String extradetails;
    @JoinColumn(name = "country", referencedColumnName = "countryid")
    @ManyToOne
    private Country country;
    @OneToMany(mappedBy = "localaddressid")
    private List<Feeduser> feeduserList;

    public Localaddress() {
    }

    public Localaddress(Integer localaddressid) {
        this.localaddressid = localaddressid;
    }

    public Localaddress(Integer localaddressid, Date datecreated, Date timemodified) {
        this.localaddressid = localaddressid;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getLocaladdressid() {
        return localaddressid;
    }

    public void setLocaladdressid(Integer localaddressid) {
        this.localaddressid = localaddressid;
    }

    public String getStateOrRegion() {
        return stateOrRegion;
    }

    public void setStateOrRegion(String stateOrRegion) {
        this.stateOrRegion = stateOrRegion;
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

    public String getExtradetails() {
        return extradetails;
    }

    public void setExtradetails(String extradetails) {
        this.extradetails = extradetails;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @XmlTransient
    public List<Feeduser> getFeeduserList() {
        return feeduserList;
    }

    public void setFeeduserList(List<Feeduser> feeduserList) {
        this.feeduserList = feeduserList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (localaddressid != null ? localaddressid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Localaddress)) {
            return false;
        }
        Localaddress other = (Localaddress) object;
        if ((this.localaddressid == null && other.localaddressid != null) || (this.localaddressid != null && !this.localaddressid.equals(other.localaddressid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Localaddress[ localaddressid=" + localaddressid + " ]";
    }

}

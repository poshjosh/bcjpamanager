/*
 * Copyright 2017 NUROX Ltd.
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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 5, 2017 10:52:02 PM
 */
@Entity
@Table(name = "country")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Country.findAll", query = "SELECT c FROM Country c"),
    @NamedQuery(name = "Country.findByCountryid", query = "SELECT c FROM Country c WHERE c.countryid = :countryid"),
    @NamedQuery(name = "Country.findByCountry", query = "SELECT c FROM Country c WHERE c.country = :country"),
    @NamedQuery(name = "Country.findByCodeIso2", query = "SELECT c FROM Country c WHERE c.codeIso2 = :codeIso2"),
    @NamedQuery(name = "Country.findByCodeIso3", query = "SELECT c FROM Country c WHERE c.codeIso3 = :codeIso3")})
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "countryid")
    private Short countryid;
    @Basic(optional = false)
    @Column(name = "country")
    private String country;
    @Column(name = "codeIso2")
    private String codeIso2;
    @Column(name = "codeIso3")
    private String codeIso3;
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<Localaddress> localaddressList;
    @OneToMany(mappedBy = "countryid", fetch = FetchType.LAZY)
    private List<Site> siteList;
    @OneToMany(mappedBy = "countryid", fetch = FetchType.LAZY)
    private List<Installation> installationList;

    public Country() {
    }

    public Country(Short countryid) {
        this.countryid = countryid;
    }

    public Country(Short countryid, String country) {
        this.countryid = countryid;
        this.country = country;
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

    public String getCodeIso2() {
        return codeIso2;
    }

    public void setCodeIso2(String codeIso2) {
        this.codeIso2 = codeIso2;
    }

    public String getCodeIso3() {
        return codeIso3;
    }

    public void setCodeIso3(String codeIso3) {
        this.codeIso3 = codeIso3;
    }

    @XmlTransient
    public List<Localaddress> getLocaladdressList() {
        return localaddressList;
    }

    public void setLocaladdressList(List<Localaddress> localaddressList) {
        this.localaddressList = localaddressList;
    }

    @XmlTransient
    public List<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

    @XmlTransient
    public List<Installation> getInstallationList() {
        return installationList;
    }

    public void setInstallationList(List<Installation> installationList) {
        this.installationList = installationList;
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
        return "com.idisc.pu.entities.Country[ countryid=" + countryid + " ]";
    }

}

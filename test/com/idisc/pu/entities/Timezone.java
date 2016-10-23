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
 * @author Chinomso Bassey Ikwuagwu on Oct 3, 2016 5:25:24 PM
 */
@Entity
@Table(name = "timezone")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Timezone.findAll", query = "SELECT t FROM Timezone t"),
    @NamedQuery(name = "Timezone.findByTimezoneid", query = "SELECT t FROM Timezone t WHERE t.timezoneid = :timezoneid"),
    @NamedQuery(name = "Timezone.findByAbbreviation", query = "SELECT t FROM Timezone t WHERE t.abbreviation = :abbreviation"),
    @NamedQuery(name = "Timezone.findByTimezonename", query = "SELECT t FROM Timezone t WHERE t.timezonename = :timezonename")})
public class Timezone implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "timezoneid")
    private Short timezoneid;
    @Basic(optional = false)
    @Column(name = "abbreviation")
    private String abbreviation;
    @Basic(optional = false)
    @Column(name = "timezonename")
    private String timezonename;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "timezoneid")
    private List<Site> siteList;

    public Timezone() {
    }

    public Timezone(Short timezoneid) {
        this.timezoneid = timezoneid;
    }

    public Timezone(Short timezoneid, String abbreviation, String timezonename) {
        this.timezoneid = timezoneid;
        this.abbreviation = abbreviation;
        this.timezonename = timezonename;
    }

    public Short getTimezoneid() {
        return timezoneid;
    }

    public void setTimezoneid(Short timezoneid) {
        this.timezoneid = timezoneid;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTimezonename() {
        return timezonename;
    }

    public void setTimezonename(String timezonename) {
        this.timezonename = timezonename;
    }

    @XmlTransient
    public List<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (timezoneid != null ? timezoneid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Timezone)) {
            return false;
        }
        Timezone other = (Timezone) object;
        if ((this.timezoneid == null && other.timezoneid != null) || (this.timezoneid != null && !this.timezoneid.equals(other.timezoneid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Timezone[ timezoneid=" + timezoneid + " ]";
    }

}

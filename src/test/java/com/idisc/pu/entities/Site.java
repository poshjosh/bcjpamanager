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
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * @author Chinomso Bassey Ikwuagwu on Feb 5, 2017 10:52:03 PM
 */
@Entity
@Table(name = "site")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Site.findAll", query = "SELECT s FROM Site s"),
    @NamedQuery(name = "Site.findBySiteid", query = "SELECT s FROM Site s WHERE s.siteid = :siteid"),
    @NamedQuery(name = "Site.findBySite", query = "SELECT s FROM Site s WHERE s.site = :site"),
    @NamedQuery(name = "Site.findByIconurl", query = "SELECT s FROM Site s WHERE s.iconurl = :iconurl"),
    @NamedQuery(name = "Site.findByDatecreated", query = "SELECT s FROM Site s WHERE s.datecreated = :datecreated"),
    @NamedQuery(name = "Site.findByTimemodified", query = "SELECT s FROM Site s WHERE s.timemodified = :timemodified"),
    @NamedQuery(name = "Site.findByExtradetails", query = "SELECT s FROM Site s WHERE s.extradetails = :extradetails")})
public class Site implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "siteid")
    private Integer siteid;
    @Basic(optional = false)
    @Column(name = "site")
    private String site;
    @Column(name = "iconurl")
    private String iconurl;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siteid", fetch = FetchType.LAZY)
    private List<Archivedfeed> archivedfeedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siteid", fetch = FetchType.LAZY)
    private List<Feed> feedList;
    @JoinColumn(name = "sitetypeid", referencedColumnName = "sitetypeid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Sitetype sitetypeid;
    @JoinColumn(name = "countryid", referencedColumnName = "countryid")
    @ManyToOne(fetch = FetchType.LAZY)
    private Country countryid;
    @JoinColumn(name = "timezoneid", referencedColumnName = "timezoneid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Timezone timezoneid;

    public Site() {
    }

    public Site(Integer siteid) {
        this.siteid = siteid;
    }

    public Site(Integer siteid, String site, Date datecreated, Date timemodified) {
        this.siteid = siteid;
        this.site = site;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getSiteid() {
        return siteid;
    }

    public void setSiteid(Integer siteid) {
        this.siteid = siteid;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
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

    @XmlTransient
    public List<Archivedfeed> getArchivedfeedList() {
        return archivedfeedList;
    }

    public void setArchivedfeedList(List<Archivedfeed> archivedfeedList) {
        this.archivedfeedList = archivedfeedList;
    }

    @XmlTransient
    public List<Feed> getFeedList() {
        return feedList;
    }

    public void setFeedList(List<Feed> feedList) {
        this.feedList = feedList;
    }

    public Sitetype getSitetypeid() {
        return sitetypeid;
    }

    public void setSitetypeid(Sitetype sitetypeid) {
        this.sitetypeid = sitetypeid;
    }

    public Country getCountryid() {
        return countryid;
    }

    public void setCountryid(Country countryid) {
        this.countryid = countryid;
    }

    public Timezone getTimezoneid() {
        return timezoneid;
    }

    public void setTimezoneid(Timezone timezoneid) {
        this.timezoneid = timezoneid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (siteid != null ? siteid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Site)) {
            return false;
        }
        Site other = (Site) object;
        if ((this.siteid == null && other.siteid != null) || (this.siteid != null && !this.siteid.equals(other.siteid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Site[ siteid=" + siteid + " ]";
    }

}

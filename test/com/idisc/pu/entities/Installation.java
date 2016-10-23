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
 * @author Chinomso Bassey Ikwuagwu on Oct 3, 2016 5:25:25 PM
 */
@Entity
@Table(name = "installation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Installation.findAll", query = "SELECT i FROM Installation i"),
    @NamedQuery(name = "Installation.findByInstallationid", query = "SELECT i FROM Installation i WHERE i.installationid = :installationid"),
    @NamedQuery(name = "Installation.findByInstallationkey", query = "SELECT i FROM Installation i WHERE i.installationkey = :installationkey"),
    @NamedQuery(name = "Installation.findByScreenname", query = "SELECT i FROM Installation i WHERE i.screenname = :screenname"),
    @NamedQuery(name = "Installation.findByFirstinstallationdate", query = "SELECT i FROM Installation i WHERE i.firstinstallationdate = :firstinstallationdate"),
    @NamedQuery(name = "Installation.findByLastinstallationdate", query = "SELECT i FROM Installation i WHERE i.lastinstallationdate = :lastinstallationdate"),
    @NamedQuery(name = "Installation.findByFirstsubscriptiondate", query = "SELECT i FROM Installation i WHERE i.firstsubscriptiondate = :firstsubscriptiondate"),
    @NamedQuery(name = "Installation.findByLastsubscriptiondate", query = "SELECT i FROM Installation i WHERE i.lastsubscriptiondate = :lastsubscriptiondate"),
    @NamedQuery(name = "Installation.findByDatecreated", query = "SELECT i FROM Installation i WHERE i.datecreated = :datecreated"),
    @NamedQuery(name = "Installation.findByTimemodified", query = "SELECT i FROM Installation i WHERE i.timemodified = :timemodified"),
    @NamedQuery(name = "Installation.findByExtradetails", query = "SELECT i FROM Installation i WHERE i.extradetails = :extradetails")})
public class Installation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "installationid")
    private Integer installationid;
    @Basic(optional = false)
    @Column(name = "installationkey")
    private String installationkey;
    @Basic(optional = false)
    @Column(name = "screenname")
    private String screenname;
    @Basic(optional = false)
    @Column(name = "firstinstallationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstinstallationdate;
    @Basic(optional = false)
    @Column(name = "lastinstallationdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastinstallationdate;
    @Column(name = "firstsubscriptiondate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstsubscriptiondate;
    @Column(name = "lastsubscriptiondate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastsubscriptiondate;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "installationid")
    private List<Bookmarkfeed> bookmarkfeedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "installationid")
    private List<Favoritefeed> favoritefeedList;
    @OneToMany(mappedBy = "installationid")
    private List<Feedhit> feedhitList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "installationid")
    private List<Extractedemail> extractedemailList;
    @OneToMany(mappedBy = "installationid")
    private List<Applaunchlog> applaunchlogList;
    @JoinColumn(name = "feeduserid", referencedColumnName = "feeduserid")
    @ManyToOne
    private Feeduser feeduserid;
    @JoinColumn(name = "countryid", referencedColumnName = "countryid")
    @ManyToOne
    private Country countryid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "installationid")
    private List<Comment> commentList;

    public Installation() {
    }

    public Installation(Integer installationid) {
        this.installationid = installationid;
    }

    public Installation(Integer installationid, String installationkey, String screenname, Date firstinstallationdate, Date lastinstallationdate, Date datecreated, Date timemodified) {
        this.installationid = installationid;
        this.installationkey = installationkey;
        this.screenname = screenname;
        this.firstinstallationdate = firstinstallationdate;
        this.lastinstallationdate = lastinstallationdate;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getInstallationid() {
        return installationid;
    }

    public void setInstallationid(Integer installationid) {
        this.installationid = installationid;
    }

    public String getInstallationkey() {
        return installationkey;
    }

    public void setInstallationkey(String installationkey) {
        this.installationkey = installationkey;
    }

    public String getScreenname() {
        return screenname;
    }

    public void setScreenname(String screenname) {
        this.screenname = screenname;
    }

    public Date getFirstinstallationdate() {
        return firstinstallationdate;
    }

    public void setFirstinstallationdate(Date firstinstallationdate) {
        this.firstinstallationdate = firstinstallationdate;
    }

    public Date getLastinstallationdate() {
        return lastinstallationdate;
    }

    public void setLastinstallationdate(Date lastinstallationdate) {
        this.lastinstallationdate = lastinstallationdate;
    }

    public Date getFirstsubscriptiondate() {
        return firstsubscriptiondate;
    }

    public void setFirstsubscriptiondate(Date firstsubscriptiondate) {
        this.firstsubscriptiondate = firstsubscriptiondate;
    }

    public Date getLastsubscriptiondate() {
        return lastsubscriptiondate;
    }

    public void setLastsubscriptiondate(Date lastsubscriptiondate) {
        this.lastsubscriptiondate = lastsubscriptiondate;
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
    public List<Bookmarkfeed> getBookmarkfeedList() {
        return bookmarkfeedList;
    }

    public void setBookmarkfeedList(List<Bookmarkfeed> bookmarkfeedList) {
        this.bookmarkfeedList = bookmarkfeedList;
    }

    @XmlTransient
    public List<Favoritefeed> getFavoritefeedList() {
        return favoritefeedList;
    }

    public void setFavoritefeedList(List<Favoritefeed> favoritefeedList) {
        this.favoritefeedList = favoritefeedList;
    }

    @XmlTransient
    public List<Feedhit> getFeedhitList() {
        return feedhitList;
    }

    public void setFeedhitList(List<Feedhit> feedhitList) {
        this.feedhitList = feedhitList;
    }

    @XmlTransient
    public List<Extractedemail> getExtractedemailList() {
        return extractedemailList;
    }

    public void setExtractedemailList(List<Extractedemail> extractedemailList) {
        this.extractedemailList = extractedemailList;
    }

    @XmlTransient
    public List<Applaunchlog> getApplaunchlogList() {
        return applaunchlogList;
    }

    public void setApplaunchlogList(List<Applaunchlog> applaunchlogList) {
        this.applaunchlogList = applaunchlogList;
    }

    public Feeduser getFeeduserid() {
        return feeduserid;
    }

    public void setFeeduserid(Feeduser feeduserid) {
        this.feeduserid = feeduserid;
    }

    public Country getCountryid() {
        return countryid;
    }

    public void setCountryid(Country countryid) {
        this.countryid = countryid;
    }

    @XmlTransient
    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (installationid != null ? installationid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Installation)) {
            return false;
        }
        Installation other = (Installation) object;
        if ((this.installationid == null && other.installationid != null) || (this.installationid != null && !this.installationid.equals(other.installationid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Installation[ installationid=" + installationid + " ]";
    }

}

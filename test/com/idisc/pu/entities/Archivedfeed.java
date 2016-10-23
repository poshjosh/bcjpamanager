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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 3, 2016 5:25:19 PM
 */
@Entity
@Table(name = "archivedfeed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Archivedfeed.findAll", query = "SELECT a FROM Archivedfeed a"),
    @NamedQuery(name = "Archivedfeed.findByArchivedfeedid", query = "SELECT a FROM Archivedfeed a WHERE a.archivedfeedid = :archivedfeedid"),
    @NamedQuery(name = "Archivedfeed.findByFeedid", query = "SELECT a FROM Archivedfeed a WHERE a.feedid = :feedid"),
    @NamedQuery(name = "Archivedfeed.findByRawid", query = "SELECT a FROM Archivedfeed a WHERE a.rawid = :rawid"),
    @NamedQuery(name = "Archivedfeed.findByUrl", query = "SELECT a FROM Archivedfeed a WHERE a.url = :url"),
    @NamedQuery(name = "Archivedfeed.findByImageurl", query = "SELECT a FROM Archivedfeed a WHERE a.imageurl = :imageurl"),
    @NamedQuery(name = "Archivedfeed.findByAuthor", query = "SELECT a FROM Archivedfeed a WHERE a.author = :author"),
    @NamedQuery(name = "Archivedfeed.findByTitle", query = "SELECT a FROM Archivedfeed a WHERE a.title = :title"),
    @NamedQuery(name = "Archivedfeed.findByKeywords", query = "SELECT a FROM Archivedfeed a WHERE a.keywords = :keywords"),
    @NamedQuery(name = "Archivedfeed.findByCategories", query = "SELECT a FROM Archivedfeed a WHERE a.categories = :categories"),
    @NamedQuery(name = "Archivedfeed.findByDescription", query = "SELECT a FROM Archivedfeed a WHERE a.description = :description"),
    @NamedQuery(name = "Archivedfeed.findByFeeddate", query = "SELECT a FROM Archivedfeed a WHERE a.feeddate = :feeddate"),
    @NamedQuery(name = "Archivedfeed.findByDatecreated", query = "SELECT a FROM Archivedfeed a WHERE a.datecreated = :datecreated"),
    @NamedQuery(name = "Archivedfeed.findByTimemodified", query = "SELECT a FROM Archivedfeed a WHERE a.timemodified = :timemodified"),
    @NamedQuery(name = "Archivedfeed.findByExtradetails", query = "SELECT a FROM Archivedfeed a WHERE a.extradetails = :extradetails")})
public class Archivedfeed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "archivedfeedid")
    private Integer archivedfeedid;
    @Basic(optional = false)
    @Column(name = "feedid")
    private int feedid;
    @Column(name = "rawid")
    private String rawid;
    @Column(name = "url")
    private String url;
    @Column(name = "imageurl")
    private String imageurl;
    @Column(name = "author")
    private String author;
    @Column(name = "title")
    private String title;
    @Column(name = "keywords")
    private String keywords;
    @Column(name = "categories")
    private String categories;
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Lob
    @Column(name = "content")
    private String content;
    @Basic(optional = false)
    @Column(name = "feeddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date feeddate;
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
    @JoinColumn(name = "siteid", referencedColumnName = "siteid")
    @ManyToOne(optional = false)
    private Site siteid;

    public Archivedfeed() {
    }

    public Archivedfeed(Integer archivedfeedid) {
        this.archivedfeedid = archivedfeedid;
    }

    public Archivedfeed(Integer archivedfeedid, int feedid, String content, Date feeddate, Date datecreated, Date timemodified) {
        this.archivedfeedid = archivedfeedid;
        this.feedid = feedid;
        this.content = content;
        this.feeddate = feeddate;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getArchivedfeedid() {
        return archivedfeedid;
    }

    public void setArchivedfeedid(Integer archivedfeedid) {
        this.archivedfeedid = archivedfeedid;
    }

    public int getFeedid() {
        return feedid;
    }

    public void setFeedid(int feedid) {
        this.feedid = feedid;
    }

    public String getRawid() {
        return rawid;
    }

    public void setRawid(String rawid) {
        this.rawid = rawid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getFeeddate() {
        return feeddate;
    }

    public void setFeeddate(Date feeddate) {
        this.feeddate = feeddate;
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

    public Site getSiteid() {
        return siteid;
    }

    public void setSiteid(Site siteid) {
        this.siteid = siteid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (archivedfeedid != null ? archivedfeedid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Archivedfeed)) {
            return false;
        }
        Archivedfeed other = (Archivedfeed) object;
        if ((this.archivedfeedid == null && other.archivedfeedid != null) || (this.archivedfeedid != null && !this.archivedfeedid.equals(other.archivedfeedid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Archivedfeed[ archivedfeedid=" + archivedfeedid + " ]";
    }

}

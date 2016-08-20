/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.Lob;
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
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 10:53:11 PM
 */
@Entity
@Table(name = "feed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feed.findAll", query = "SELECT f FROM Feed f"),
    @NamedQuery(name = "Feed.findByFeedid", query = "SELECT f FROM Feed f WHERE f.feedid = :feedid"),
    @NamedQuery(name = "Feed.findByRawid", query = "SELECT f FROM Feed f WHERE f.rawid = :rawid"),
    @NamedQuery(name = "Feed.findByUrl", query = "SELECT f FROM Feed f WHERE f.url = :url"),
    @NamedQuery(name = "Feed.findByImageurl", query = "SELECT f FROM Feed f WHERE f.imageurl = :imageurl"),
    @NamedQuery(name = "Feed.findByAuthor", query = "SELECT f FROM Feed f WHERE f.author = :author"),
    @NamedQuery(name = "Feed.findByTitle", query = "SELECT f FROM Feed f WHERE f.title = :title"),
    @NamedQuery(name = "Feed.findByKeywords", query = "SELECT f FROM Feed f WHERE f.keywords = :keywords"),
    @NamedQuery(name = "Feed.findByCategories", query = "SELECT f FROM Feed f WHERE f.categories = :categories"),
    @NamedQuery(name = "Feed.findByDescription", query = "SELECT f FROM Feed f WHERE f.description = :description"),
    @NamedQuery(name = "Feed.findByFeeddate", query = "SELECT f FROM Feed f WHERE f.feeddate = :feeddate"),
    @NamedQuery(name = "Feed.findByDatecreated", query = "SELECT f FROM Feed f WHERE f.datecreated = :datecreated"),
    @NamedQuery(name = "Feed.findByTimemodified", query = "SELECT f FROM Feed f WHERE f.timemodified = :timemodified"),
    @NamedQuery(name = "Feed.findByExtradetails", query = "SELECT f FROM Feed f WHERE f.extradetails = :extradetails")})
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "feedid")
    private Integer feedid;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feedid")
    private List<Bookmarkfeed> bookmarkfeedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feedid")
    private List<Favoritefeed> favoritefeedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feedid")
    private List<Feedhit> feedhitList;
    @JoinColumn(name = "siteid", referencedColumnName = "siteid")
    @ManyToOne(optional = false)
    private Site siteid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feedid")
    private List<Comment> commentList;

    public Feed() {
    }

    public Feed(Integer feedid) {
        this.feedid = feedid;
    }

    public Feed(Integer feedid, String content, Date feeddate, Date datecreated, Date timemodified) {
        this.feedid = feedid;
        this.content = content;
        this.feeddate = feeddate;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getFeedid() {
        return feedid;
    }

    public void setFeedid(Integer feedid) {
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

    public Site getSiteid() {
        return siteid;
    }

    public void setSiteid(Site siteid) {
        this.siteid = siteid;
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
        hash += (feedid != null ? feedid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Feed)) {
            return false;
        }
        Feed other = (Feed) object;
        if ((this.feedid == null && other.feedid != null) || (this.feedid != null && !this.feedid.equals(other.feedid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Feed[ feedid=" + feedid + " ]";
    }

}

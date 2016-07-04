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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
























@Entity
@Table(name="archivedfeed")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Archivedfeed.findAll", query="SELECT a FROM Archivedfeed a"), @javax.persistence.NamedQuery(name="Archivedfeed.findByArchivedfeedid", query="SELECT a FROM Archivedfeed a WHERE a.archivedfeedid = :archivedfeedid"), @javax.persistence.NamedQuery(name="Archivedfeed.findByFeedid", query="SELECT a FROM Archivedfeed a WHERE a.feedid = :feedid"), @javax.persistence.NamedQuery(name="Archivedfeed.findByRawid", query="SELECT a FROM Archivedfeed a WHERE a.rawid = :rawid"), @javax.persistence.NamedQuery(name="Archivedfeed.findByUrl", query="SELECT a FROM Archivedfeed a WHERE a.url = :url"), @javax.persistence.NamedQuery(name="Archivedfeed.findByImageurl", query="SELECT a FROM Archivedfeed a WHERE a.imageurl = :imageurl"), @javax.persistence.NamedQuery(name="Archivedfeed.findByAuthor", query="SELECT a FROM Archivedfeed a WHERE a.author = :author"), @javax.persistence.NamedQuery(name="Archivedfeed.findByTitle", query="SELECT a FROM Archivedfeed a WHERE a.title = :title"), @javax.persistence.NamedQuery(name="Archivedfeed.findByKeywords", query="SELECT a FROM Archivedfeed a WHERE a.keywords = :keywords"), @javax.persistence.NamedQuery(name="Archivedfeed.findByCategories", query="SELECT a FROM Archivedfeed a WHERE a.categories = :categories"), @javax.persistence.NamedQuery(name="Archivedfeed.findByDescription", query="SELECT a FROM Archivedfeed a WHERE a.description = :description"), @javax.persistence.NamedQuery(name="Archivedfeed.findByFeeddate", query="SELECT a FROM Archivedfeed a WHERE a.feeddate = :feeddate"), @javax.persistence.NamedQuery(name="Archivedfeed.findByDatecreated", query="SELECT a FROM Archivedfeed a WHERE a.datecreated = :datecreated"), @javax.persistence.NamedQuery(name="Archivedfeed.findByTimemodified", query="SELECT a FROM Archivedfeed a WHERE a.timemodified = :timemodified"), @javax.persistence.NamedQuery(name="Archivedfeed.findByExtradetails", query="SELECT a FROM Archivedfeed a WHERE a.extradetails = :extradetails")})
public class Archivedfeed
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="archivedfeedid")
  private Integer archivedfeedid;
  @Basic(optional=false)
  @Column(name="feedid")
  private int feedid;
  @Column(name="rawid")
  private String rawid;
  @Column(name="url")
  private String url;
  @Column(name="imageurl")
  private String imageurl;
  @Column(name="author")
  private String author;
  @Column(name="title")
  private String title;
  @Column(name="keywords")
  private String keywords;
  @Column(name="categories")
  private String categories;
  @Column(name="description")
  private String description;
  @Basic(optional=false)
  @Lob
  @Column(name="content")
  private String content;
  @Basic(optional=false)
  @Column(name="feeddate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date feeddate;
  @Basic(optional=false)
  @Column(name="datecreated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datecreated;
  @Basic(optional=false)
  @Column(name="timemodified")
  @Temporal(TemporalType.TIMESTAMP)
  private Date timemodified;
  @Column(name="extradetails")
  private String extradetails;
  @JoinColumn(name="siteid", referencedColumnName="siteid")
  @ManyToOne(optional=false)
  private Site siteid;
  
  public Archivedfeed() {}
  
  public Archivedfeed(Integer archivedfeedid)
  {
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
    return this.archivedfeedid;
  }
  
  public void setArchivedfeedid(Integer archivedfeedid) {
    this.archivedfeedid = archivedfeedid;
  }
  
  public int getFeedid() {
    return this.feedid;
  }
  
  public void setFeedid(int feedid) {
    this.feedid = feedid;
  }
  
  public String getRawid() {
    return this.rawid;
  }
  
  public void setRawid(String rawid) {
    this.rawid = rawid;
  }
  
  public String getUrl() {
    return this.url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public String getImageurl() {
    return this.imageurl;
  }
  
  public void setImageurl(String imageurl) {
    this.imageurl = imageurl;
  }
  
  public String getAuthor() {
    return this.author;
  }
  
  public void setAuthor(String author) {
    this.author = author;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getKeywords() {
    return this.keywords;
  }
  
  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }
  
  public String getCategories() {
    return this.categories;
  }
  
  public void setCategories(String categories) {
    this.categories = categories;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getContent() {
    return this.content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public Date getFeeddate() {
    return this.feeddate;
  }
  
  public void setFeeddate(Date feeddate) {
    this.feeddate = feeddate;
  }
  
  public Date getDatecreated() {
    return this.datecreated;
  }
  
  public void setDatecreated(Date datecreated) {
    this.datecreated = datecreated;
  }
  
  public Date getTimemodified() {
    return this.timemodified;
  }
  
  public void setTimemodified(Date timemodified) {
    this.timemodified = timemodified;
  }
  
  public String getExtradetails() {
    return this.extradetails;
  }
  
  public void setExtradetails(String extradetails) {
    this.extradetails = extradetails;
  }
  
  public Site getSiteid() {
    return this.siteid;
  }
  
  public void setSiteid(Site siteid) {
    this.siteid = siteid;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.archivedfeedid != null ? this.archivedfeedid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Archivedfeed)) {
      return false;
    }
    Archivedfeed other = (Archivedfeed)object;
    if (((this.archivedfeedid == null) && (other.archivedfeedid != null)) || ((this.archivedfeedid != null) && (!this.archivedfeedid.equals(other.archivedfeedid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Archivedfeed[ archivedfeedid=" + this.archivedfeedid + " ]";
  }
}

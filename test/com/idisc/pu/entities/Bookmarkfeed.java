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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;












@Entity
@Table(name="bookmarkfeed")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Bookmarkfeed.findAll", query="SELECT b FROM Bookmarkfeed b"), @javax.persistence.NamedQuery(name="Bookmarkfeed.findByBookmarkfeedid", query="SELECT b FROM Bookmarkfeed b WHERE b.bookmarkfeedid = :bookmarkfeedid"), @javax.persistence.NamedQuery(name="Bookmarkfeed.findByDatecreated", query="SELECT b FROM Bookmarkfeed b WHERE b.datecreated = :datecreated")})
public class Bookmarkfeed
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="bookmarkfeedid")
  private Integer bookmarkfeedid;
  @Basic(optional=false)
  @Column(name="datecreated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datecreated;
  @JoinColumn(name="installationid", referencedColumnName="installationid")
  @ManyToOne(optional=false)
  private Installation installationid;
  @JoinColumn(name="feedid", referencedColumnName="feedid")
  @ManyToOne(optional=false)
  private Feed feedid;
  
  public Bookmarkfeed() {}
  
  public Bookmarkfeed(Integer bookmarkfeedid)
  {
    this.bookmarkfeedid = bookmarkfeedid;
  }
  
  public Bookmarkfeed(Integer bookmarkfeedid, Date datecreated) {
    this.bookmarkfeedid = bookmarkfeedid;
    this.datecreated = datecreated;
  }
  
  public Integer getBookmarkfeedid() {
    return this.bookmarkfeedid;
  }
  
  public void setBookmarkfeedid(Integer bookmarkfeedid) {
    this.bookmarkfeedid = bookmarkfeedid;
  }
  
  public Date getDatecreated() {
    return this.datecreated;
  }
  
  public void setDatecreated(Date datecreated) {
    this.datecreated = datecreated;
  }
  
  public Installation getInstallationid() {
    return this.installationid;
  }
  
  public void setInstallationid(Installation installationid) {
    this.installationid = installationid;
  }
  
  public Feed getFeedid() {
    return this.feedid;
  }
  
  public void setFeedid(Feed feedid) {
    this.feedid = feedid;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.bookmarkfeedid != null ? this.bookmarkfeedid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Bookmarkfeed)) {
      return false;
    }
    Bookmarkfeed other = (Bookmarkfeed)object;
    if (((this.bookmarkfeedid == null) && (other.bookmarkfeedid != null)) || ((this.bookmarkfeedid != null) && (!this.bookmarkfeedid.equals(other.bookmarkfeedid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Bookmarkfeed[ bookmarkfeedid=" + this.bookmarkfeedid + " ]";
  }
}

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

















@Entity
@Table(name="site")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Site.findAll", query="SELECT s FROM Site s"), @javax.persistence.NamedQuery(name="Site.findBySiteid", query="SELECT s FROM Site s WHERE s.siteid = :siteid"), @javax.persistence.NamedQuery(name="Site.findBySite", query="SELECT s FROM Site s WHERE s.site = :site"), @javax.persistence.NamedQuery(name="Site.findByIconurl", query="SELECT s FROM Site s WHERE s.iconurl = :iconurl"), @javax.persistence.NamedQuery(name="Site.findByDatecreated", query="SELECT s FROM Site s WHERE s.datecreated = :datecreated"), @javax.persistence.NamedQuery(name="Site.findByTimemodified", query="SELECT s FROM Site s WHERE s.timemodified = :timemodified"), @javax.persistence.NamedQuery(name="Site.findByExtradetails", query="SELECT s FROM Site s WHERE s.extradetails = :extradetails")})
public class Site
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="siteid")
  private Integer siteid;
  @Basic(optional=false)
  @Column(name="site")
  private String site;
  @Column(name="iconurl")
  private String iconurl;
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
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="siteid")
  private List<Archivedfeed> archivedfeedList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="siteid")
  private List<Feed> feedList;
  @JoinColumn(name="sitetypeid", referencedColumnName="sitetypeid")
  @ManyToOne(optional=false)
  private Sitetype sitetypeid;
  
  public Site() {}
  
  public Site(Integer siteid)
  {
    this.siteid = siteid;
  }
  
  public Site(Integer siteid, String site, Date datecreated, Date timemodified) {
    this.siteid = siteid;
    this.site = site;
    this.datecreated = datecreated;
    this.timemodified = timemodified;
  }
  
  public Integer getSiteid() {
    return this.siteid;
  }
  
  public void setSiteid(Integer siteid) {
    this.siteid = siteid;
  }
  
  public String getSite() {
    return this.site;
  }
  
  public void setSite(String site) {
    this.site = site;
  }
  
  public String getIconurl() {
    return this.iconurl;
  }
  
  public void setIconurl(String iconurl) {
    this.iconurl = iconurl;
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
  
  @XmlTransient
  public List<Archivedfeed> getArchivedfeedList() {
    return this.archivedfeedList;
  }
  
  public void setArchivedfeedList(List<Archivedfeed> archivedfeedList) {
    this.archivedfeedList = archivedfeedList;
  }
  
  @XmlTransient
  public List<Feed> getFeedList() {
    return this.feedList;
  }
  
  public void setFeedList(List<Feed> feedList) {
    this.feedList = feedList;
  }
  
  public Sitetype getSitetypeid() {
    return this.sitetypeid;
  }
  
  public void setSitetypeid(Sitetype sitetypeid) {
    this.sitetypeid = sitetypeid;
  }
  
  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (this.siteid != null ? this.siteid.hashCode() : 0);
    return hash;
  }
  
  @Override
  public boolean equals(Object object)
  {
    if (!(object instanceof Site)) {
      return false;
    }
    Site other = (Site)object;
    if (((this.siteid == null) && (other.siteid != null)) || ((this.siteid != null) && (!this.siteid.equals(other.siteid)))) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString()
  {
    return "com.idisc.pu.entities.Site[ siteid=" + this.siteid + " ]";
  }
}

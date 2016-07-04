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
@Table(name="installation")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Installation.findAll", query="SELECT i FROM Installation i"), @javax.persistence.NamedQuery(name="Installation.findByInstallationid", query="SELECT i FROM Installation i WHERE i.installationid = :installationid"), @javax.persistence.NamedQuery(name="Installation.findByInstallationkey", query="SELECT i FROM Installation i WHERE i.installationkey = :installationkey"), @javax.persistence.NamedQuery(name="Installation.findByScreenname", query="SELECT i FROM Installation i WHERE i.screenname = :screenname"), @javax.persistence.NamedQuery(name="Installation.findByFirstinstallationdate", query="SELECT i FROM Installation i WHERE i.firstinstallationdate = :firstinstallationdate"), @javax.persistence.NamedQuery(name="Installation.findByLastinstallationdate", query="SELECT i FROM Installation i WHERE i.lastinstallationdate = :lastinstallationdate"), @javax.persistence.NamedQuery(name="Installation.findByFirstsubscriptiondate", query="SELECT i FROM Installation i WHERE i.firstsubscriptiondate = :firstsubscriptiondate"), @javax.persistence.NamedQuery(name="Installation.findByLastsubscriptiondate", query="SELECT i FROM Installation i WHERE i.lastsubscriptiondate = :lastsubscriptiondate"), @javax.persistence.NamedQuery(name="Installation.findByDatecreated", query="SELECT i FROM Installation i WHERE i.datecreated = :datecreated"), @javax.persistence.NamedQuery(name="Installation.findByTimemodified", query="SELECT i FROM Installation i WHERE i.timemodified = :timemodified"), @javax.persistence.NamedQuery(name="Installation.findByExtradetails", query="SELECT i FROM Installation i WHERE i.extradetails = :extradetails")})
public class Installation
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="installationid")
  private Integer installationid;
  @Basic(optional=false)
  @Column(name="installationkey")
  private String installationkey;
  @Basic(optional=false)
  @Column(name="screenname")
  private String screenname;
  @Basic(optional=false)
  @Column(name="firstinstallationdate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date firstinstallationdate;
  @Basic(optional=false)
  @Column(name="lastinstallationdate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastinstallationdate;
  @Column(name="firstsubscriptiondate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date firstsubscriptiondate;
  @Column(name="lastsubscriptiondate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastsubscriptiondate;
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
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="installationid")
  private List<Bookmarkfeed> bookmarkfeedList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="installationid")
  private List<Favoritefeed> favoritefeedList;
  @OneToMany(mappedBy="installationid")
  private List<Feedhit> feedhitList;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="installationid")
  private List<Extractedemail> extractedemailList;
  @JoinColumn(name="feeduserid", referencedColumnName="feeduserid")
  @ManyToOne
  private Feeduser feeduserid;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="installationid")
  private List<Comment> commentList;
  
  public Installation() {}
  
  public Installation(Integer installationid)
  {
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
    return this.installationid;
  }
  
  public void setInstallationid(Integer installationid) {
    this.installationid = installationid;
  }
  
  public String getInstallationkey() {
    return this.installationkey;
  }
  
  public void setInstallationkey(String installationkey) {
    this.installationkey = installationkey;
  }
  
  public String getScreenname() {
    return this.screenname;
  }
  
  public void setScreenname(String screenname) {
    this.screenname = screenname;
  }
  
  public Date getFirstinstallationdate() {
    return this.firstinstallationdate;
  }
  
  public void setFirstinstallationdate(Date firstinstallationdate) {
    this.firstinstallationdate = firstinstallationdate;
  }
  
  public Date getLastinstallationdate() {
    return this.lastinstallationdate;
  }
  
  public void setLastinstallationdate(Date lastinstallationdate) {
    this.lastinstallationdate = lastinstallationdate;
  }
  
  public Date getFirstsubscriptiondate() {
    return this.firstsubscriptiondate;
  }
  
  public void setFirstsubscriptiondate(Date firstsubscriptiondate) {
    this.firstsubscriptiondate = firstsubscriptiondate;
  }
  
  public Date getLastsubscriptiondate() {
    return this.lastsubscriptiondate;
  }
  
  public void setLastsubscriptiondate(Date lastsubscriptiondate) {
    this.lastsubscriptiondate = lastsubscriptiondate;
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
  public List<Bookmarkfeed> getBookmarkfeedList() {
    return this.bookmarkfeedList;
  }
  
  public void setBookmarkfeedList(List<Bookmarkfeed> bookmarkfeedList) {
    this.bookmarkfeedList = bookmarkfeedList;
  }
  
  @XmlTransient
  public List<Favoritefeed> getFavoritefeedList() {
    return this.favoritefeedList;
  }
  
  public void setFavoritefeedList(List<Favoritefeed> favoritefeedList) {
    this.favoritefeedList = favoritefeedList;
  }
  
  @XmlTransient
  public List<Feedhit> getFeedhitList() {
    return this.feedhitList;
  }
  
  public void setFeedhitList(List<Feedhit> feedhitList) {
    this.feedhitList = feedhitList;
  }
  
  @XmlTransient
  public List<Extractedemail> getExtractedemailList() {
    return this.extractedemailList;
  }
  
  public void setExtractedemailList(List<Extractedemail> extractedemailList) {
    this.extractedemailList = extractedemailList;
  }
  
  public Feeduser getFeeduserid() {
    return this.feeduserid;
  }
  
  public void setFeeduserid(Feeduser feeduserid) {
    this.feeduserid = feeduserid;
  }
  
  @XmlTransient
  public List<Comment> getCommentList() {
    return this.commentList;
  }
  
  public void setCommentList(List<Comment> commentList) {
    this.commentList = commentList;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.installationid != null ? this.installationid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Installation)) {
      return false;
    }
    Installation other = (Installation)object;
    if (((this.installationid == null) && (other.installationid != null)) || ((this.installationid != null) && (!this.installationid.equals(other.installationid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Installation[ installationid=" + this.installationid + " ]";
  }
}

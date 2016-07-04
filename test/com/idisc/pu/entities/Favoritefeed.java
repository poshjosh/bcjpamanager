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
@Table(name="favoritefeed")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Favoritefeed.findAll", query="SELECT f FROM Favoritefeed f"), @javax.persistence.NamedQuery(name="Favoritefeed.findByFavoritefeedid", query="SELECT f FROM Favoritefeed f WHERE f.favoritefeedid = :favoritefeedid"), @javax.persistence.NamedQuery(name="Favoritefeed.findByDatecreated", query="SELECT f FROM Favoritefeed f WHERE f.datecreated = :datecreated")})
public class Favoritefeed
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="favoritefeedid")
  private Integer favoritefeedid;
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
  
  public Favoritefeed() {}
  
  public Favoritefeed(Integer favoritefeedid)
  {
    this.favoritefeedid = favoritefeedid;
  }
  
  public Favoritefeed(Integer favoritefeedid, Date datecreated) {
    this.favoritefeedid = favoritefeedid;
    this.datecreated = datecreated;
  }
  
  public Integer getFavoritefeedid() {
    return this.favoritefeedid;
  }
  
  public void setFavoritefeedid(Integer favoritefeedid) {
    this.favoritefeedid = favoritefeedid;
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
    hash += (this.favoritefeedid != null ? this.favoritefeedid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Favoritefeed)) {
      return false;
    }
    Favoritefeed other = (Favoritefeed)object;
    if (((this.favoritefeedid == null) && (other.favoritefeedid != null)) || ((this.favoritefeedid != null) && (!this.favoritefeedid.equals(other.favoritefeedid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Favoritefeed[ favoritefeedid=" + this.favoritefeedid + " ]";
  }
}

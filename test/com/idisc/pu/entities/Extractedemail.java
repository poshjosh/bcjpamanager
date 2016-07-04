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
@Table(name="extractedemail")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Extractedemail.findAll", query="SELECT e FROM Extractedemail e"), @javax.persistence.NamedQuery(name="Extractedemail.findByExtractedemailid", query="SELECT e FROM Extractedemail e WHERE e.extractedemailid = :extractedemailid"), @javax.persistence.NamedQuery(name="Extractedemail.findByEmailAddress", query="SELECT e FROM Extractedemail e WHERE e.emailAddress = :emailAddress"), @javax.persistence.NamedQuery(name="Extractedemail.findByUsername", query="SELECT e FROM Extractedemail e WHERE e.username = :username"), @javax.persistence.NamedQuery(name="Extractedemail.findByDatecreated", query="SELECT e FROM Extractedemail e WHERE e.datecreated = :datecreated"), @javax.persistence.NamedQuery(name="Extractedemail.findByTimemodified", query="SELECT e FROM Extractedemail e WHERE e.timemodified = :timemodified"), @javax.persistence.NamedQuery(name="Extractedemail.findByExtradetails", query="SELECT e FROM Extractedemail e WHERE e.extradetails = :extradetails")})
public class Extractedemail
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="extractedemailid")
  private Integer extractedemailid;
  @Basic(optional=false)
  @Column(name="emailAddress")
  private String emailAddress;
  @Column(name="username")
  private String username;
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
  @JoinColumn(name="installationid", referencedColumnName="installationid")
  @ManyToOne(optional=false)
  private Installation installationid;
  @JoinColumn(name="emailstatus", referencedColumnName="emailstatusid")
  @ManyToOne(optional=false)
  private Emailstatus emailstatus;
  
  public Extractedemail() {}
  
  public Extractedemail(Integer extractedemailid)
  {
    this.extractedemailid = extractedemailid;
  }
  
  public Extractedemail(Integer extractedemailid, String emailAddress, Date datecreated, Date timemodified) {
    this.extractedemailid = extractedemailid;
    this.emailAddress = emailAddress;
    this.datecreated = datecreated;
    this.timemodified = timemodified;
  }
  
  public Integer getExtractedemailid() {
    return this.extractedemailid;
  }
  
  public void setExtractedemailid(Integer extractedemailid) {
    this.extractedemailid = extractedemailid;
  }
  
  public String getEmailAddress() {
    return this.emailAddress;
  }
  
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public void setUsername(String username) {
    this.username = username;
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
  
  public Installation getInstallationid() {
    return this.installationid;
  }
  
  public void setInstallationid(Installation installationid) {
    this.installationid = installationid;
  }
  
  public Emailstatus getEmailstatus() {
    return this.emailstatus;
  }
  
  public void setEmailstatus(Emailstatus emailstatus) {
    this.emailstatus = emailstatus;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.extractedemailid != null ? this.extractedemailid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Extractedemail)) {
      return false;
    }
    Extractedemail other = (Extractedemail)object;
    if (((this.extractedemailid == null) && (other.extractedemailid != null)) || ((this.extractedemailid != null) && (!this.extractedemailid.equals(other.extractedemailid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Extractedemail[ extractedemailid=" + this.extractedemailid + " ]";
  }
}

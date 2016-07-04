package com.idisc.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;













@Entity
@Table(name="emailstatus")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Emailstatus.findAll", query="SELECT e FROM Emailstatus e"), @javax.persistence.NamedQuery(name="Emailstatus.findByEmailstatusid", query="SELECT e FROM Emailstatus e WHERE e.emailstatusid = :emailstatusid"), @javax.persistence.NamedQuery(name="Emailstatus.findByEmailstatus", query="SELECT e FROM Emailstatus e WHERE e.emailstatus = :emailstatus")})
public class Emailstatus
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional=false)
  @Column(name="emailstatusid")
  private Short emailstatusid;
  @Basic(optional=false)
  @Column(name="emailstatus")
  private String emailstatus;
  @OneToMany(cascade={javax.persistence.CascadeType.ALL}, mappedBy="emailstatus")
  private List<Extractedemail> extractedemailList;
  
  public Emailstatus() {}
  
  public Emailstatus(Short emailstatusid)
  {
    this.emailstatusid = emailstatusid;
  }
  
  public Emailstatus(Short emailstatusid, String emailstatus) {
    this.emailstatusid = emailstatusid;
    this.emailstatus = emailstatus;
  }
  
  public Short getEmailstatusid() {
    return this.emailstatusid;
  }
  
  public void setEmailstatusid(Short emailstatusid) {
    this.emailstatusid = emailstatusid;
  }
  
  public String getEmailstatus() {
    return this.emailstatus;
  }
  
  public void setEmailstatus(String emailstatus) {
    this.emailstatus = emailstatus;
  }
  
  @XmlTransient
  public List<Extractedemail> getExtractedemailList() {
    return this.extractedemailList;
  }
  
  public void setExtractedemailList(List<Extractedemail> extractedemailList) {
    this.extractedemailList = extractedemailList;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.emailstatusid != null ? this.emailstatusid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Emailstatus)) {
      return false;
    }
    Emailstatus other = (Emailstatus)object;
    if (((this.emailstatusid == null) && (other.emailstatusid != null)) || ((this.emailstatusid != null) && (!this.emailstatusid.equals(other.emailstatusid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Emailstatus[ emailstatusid=" + this.emailstatusid + " ]";
  }
}
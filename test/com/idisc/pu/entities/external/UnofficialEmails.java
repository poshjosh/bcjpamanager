package com.idisc.pu.entities.external;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name="unofficial_emails")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="UnofficialEmails.findAll", query="SELECT u FROM UnofficialEmails u"), @javax.persistence.NamedQuery(name="UnofficialEmails.findByEmailAddress", query="SELECT u FROM UnofficialEmails u WHERE u.emailAddress = :emailAddress"), @javax.persistence.NamedQuery(name="UnofficialEmails.findByLastName", query="SELECT u FROM UnofficialEmails u WHERE u.lastName = :lastName"), @javax.persistence.NamedQuery(name="UnofficialEmails.findByFirstName", query="SELECT u FROM UnofficialEmails u WHERE u.firstName = :firstName"), @javax.persistence.NamedQuery(name="UnofficialEmails.findByDatein", query="SELECT u FROM UnofficialEmails u WHERE u.datein = :datein"), @javax.persistence.NamedQuery(name="UnofficialEmails.findByEmailStatus", query="SELECT u FROM UnofficialEmails u WHERE u.emailStatus = :emailStatus"), @javax.persistence.NamedQuery(name="UnofficialEmails.findByExtraDetails", query="SELECT u FROM UnofficialEmails u WHERE u.extraDetails = :extraDetails")})
public class UnofficialEmails
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional=false)
  @Column(name="emailAddress")
  private String emailAddress;
  @Column(name="lastName")
  private String lastName;
  @Column(name="firstName")
  private String firstName;
  @Basic(optional=false)
  @Column(name="datein")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datein;
  @Column(name="emailStatus")
  private Short emailStatus;
  @Column(name="extraDetails")
  private String extraDetails;
  
  public UnofficialEmails() {}
  
  public UnofficialEmails(String emailAddress)
  {
    this.emailAddress = emailAddress;
  }
  
  public UnofficialEmails(String emailAddress, Date datein) {
    this.emailAddress = emailAddress;
    this.datein = datein;
  }
  
  public String getEmailAddress() {
    return this.emailAddress;
  }
  
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
  
  public String getLastName() {
    return this.lastName;
  }
  
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  
  public String getFirstName() {
    return this.firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public Date getDatein() {
    return this.datein;
  }
  
  public void setDatein(Date datein) {
    this.datein = datein;
  }
  
  public Short getEmailStatus() {
    return this.emailStatus;
  }
  
  public void setEmailStatus(Short emailStatus) {
    this.emailStatus = emailStatus;
  }
  
  public String getExtraDetails() {
    return this.extraDetails;
  }
  
  public void setExtraDetails(String extraDetails) {
    this.extraDetails = extraDetails;
  }
  
  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (this.emailAddress != null ? this.emailAddress.hashCode() : 0);
    return hash;
  }
  

  @Override
  public boolean equals(Object object)
  {
    if (!(object instanceof UnofficialEmails)) {
      return false;
    }
    UnofficialEmails other = (UnofficialEmails)object;
    if (((this.emailAddress == null) && (other.emailAddress != null)) || ((this.emailAddress != null) && (!this.emailAddress.equals(other.emailAddress)))) {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString()
  {
    return "com.idiscweb.UnofficialEmails[ emailAddress=" + this.emailAddress + " ]";
  }
}

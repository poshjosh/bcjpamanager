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
@Table(name="country")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Country.findAll", query="SELECT c FROM Country c"), @javax.persistence.NamedQuery(name="Country.findByCountryid", query="SELECT c FROM Country c WHERE c.countryid = :countryid"), @javax.persistence.NamedQuery(name="Country.findByCountry", query="SELECT c FROM Country c WHERE c.country = :country")})
public class Country
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional=false)
  @Column(name="countryid")
  private Short countryid;
  @Basic(optional=false)
  @Column(name="country")
  private String country;
  @OneToMany(mappedBy="country")
  private List<Feeduser> feeduserList;
  
  public Country() {}
  
  public Country(Short countryid)
  {
    this.countryid = countryid;
  }
  
  public Country(Short countryid, String country) {
    this.countryid = countryid;
    this.country = country;
  }
  
  public Short getCountryid() {
    return this.countryid;
  }
  
  public void setCountryid(Short countryid) {
    this.countryid = countryid;
  }
  
  public String getCountry() {
    return this.country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  @XmlTransient
  public List<Feeduser> getFeeduserList() {
    return this.feeduserList;
  }
  
  public void setFeeduserList(List<Feeduser> feeduserList) {
    this.feeduserList = feeduserList;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.countryid != null ? this.countryid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Country)) {
      return false;
    }
    Country other = (Country)object;
    if (((this.countryid == null) && (other.countryid != null)) || ((this.countryid != null) && (!this.countryid.equals(other.countryid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Country[ countryid=" + this.countryid + " ]";
  }
}

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
@Table(name="gender")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Gender.findAll", query="SELECT g FROM Gender g"), @javax.persistence.NamedQuery(name="Gender.findByGenderid", query="SELECT g FROM Gender g WHERE g.genderid = :genderid"), @javax.persistence.NamedQuery(name="Gender.findByGender", query="SELECT g FROM Gender g WHERE g.gender = :gender")})
public class Gender
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional=false)
  @Column(name="genderid")
  private Short genderid;
  @Basic(optional=false)
  @Column(name="gender")
  private String gender;
  @OneToMany(mappedBy="gender")
  private List<Feeduser> feeduserList;
  
  public Gender() {}
  
  public Gender(Short genderid)
  {
    this.genderid = genderid;
  }
  
  public Gender(Short genderid, String gender) {
    this.genderid = genderid;
    this.gender = gender;
  }
  
  public Short getGenderid() {
    return this.genderid;
  }
  
  public void setGenderid(Short genderid) {
    this.genderid = genderid;
  }
  
  public String getGender() {
    return this.gender;
  }
  
  public void setGender(String gender) {
    this.gender = gender;
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
    hash += (this.genderid != null ? this.genderid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Gender)) {
      return false;
    }
    Gender other = (Gender)object;
    if (((this.genderid == null) && (other.genderid != null)) || ((this.genderid != null) && (!this.genderid.equals(other.genderid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Gender[ genderid=" + this.genderid + " ]";
  }
}

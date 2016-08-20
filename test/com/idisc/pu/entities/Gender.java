/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.idisc.pu.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 10:53:10 PM
 */
@Entity
@Table(name = "gender")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gender.findAll", query = "SELECT g FROM Gender g"),
    @NamedQuery(name = "Gender.findByGenderid", query = "SELECT g FROM Gender g WHERE g.genderid = :genderid"),
    @NamedQuery(name = "Gender.findByGender", query = "SELECT g FROM Gender g WHERE g.gender = :gender")})
public class Gender implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "genderid")
    private Short genderid;
    @Basic(optional = false)
    @Column(name = "gender")
    private String gender;
    @OneToMany(mappedBy = "gender")
    private List<Feeduser> feeduserList;

    public Gender() {
    }

    public Gender(Short genderid) {
        this.genderid = genderid;
    }

    public Gender(Short genderid, String gender) {
        this.genderid = genderid;
        this.gender = gender;
    }

    public Short getGenderid() {
        return genderid;
    }

    public void setGenderid(Short genderid) {
        this.genderid = genderid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @XmlTransient
    public List<Feeduser> getFeeduserList() {
        return feeduserList;
    }

    public void setFeeduserList(List<Feeduser> feeduserList) {
        this.feeduserList = feeduserList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (genderid != null ? genderid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gender)) {
            return false;
        }
        Gender other = (Gender) object;
        if ((this.genderid == null && other.genderid != null) || (this.genderid != null && !this.genderid.equals(other.genderid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Gender[ genderid=" + genderid + " ]";
    }

}

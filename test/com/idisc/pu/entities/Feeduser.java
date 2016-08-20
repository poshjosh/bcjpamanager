/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 10:53:11 PM
 */
@Entity
@Table(name = "feeduser")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feeduser.findAll", query = "SELECT f FROM Feeduser f"),
    @NamedQuery(name = "Feeduser.findByFeeduserid", query = "SELECT f FROM Feeduser f WHERE f.feeduserid = :feeduserid"),
    @NamedQuery(name = "Feeduser.findByEmailAddress", query = "SELECT f FROM Feeduser f WHERE f.emailAddress = :emailAddress"),
    @NamedQuery(name = "Feeduser.findByLastName", query = "SELECT f FROM Feeduser f WHERE f.lastName = :lastName"),
    @NamedQuery(name = "Feeduser.findByFirstName", query = "SELECT f FROM Feeduser f WHERE f.firstName = :firstName"),
    @NamedQuery(name = "Feeduser.findByDateOfBirth", query = "SELECT f FROM Feeduser f WHERE f.dateOfBirth = :dateOfBirth"),
    @NamedQuery(name = "Feeduser.findByPhoneNumber1", query = "SELECT f FROM Feeduser f WHERE f.phoneNumber1 = :phoneNumber1"),
    @NamedQuery(name = "Feeduser.findByPhoneNumber2", query = "SELECT f FROM Feeduser f WHERE f.phoneNumber2 = :phoneNumber2"),
    @NamedQuery(name = "Feeduser.findByFax", query = "SELECT f FROM Feeduser f WHERE f.fax = :fax"),
    @NamedQuery(name = "Feeduser.findByStateOrRegion", query = "SELECT f FROM Feeduser f WHERE f.stateOrRegion = :stateOrRegion"),
    @NamedQuery(name = "Feeduser.findByCity", query = "SELECT f FROM Feeduser f WHERE f.city = :city"),
    @NamedQuery(name = "Feeduser.findByCounty", query = "SELECT f FROM Feeduser f WHERE f.county = :county"),
    @NamedQuery(name = "Feeduser.findByStreetAddress", query = "SELECT f FROM Feeduser f WHERE f.streetAddress = :streetAddress"),
    @NamedQuery(name = "Feeduser.findByPostalCode", query = "SELECT f FROM Feeduser f WHERE f.postalCode = :postalCode"),
    @NamedQuery(name = "Feeduser.findByImage1", query = "SELECT f FROM Feeduser f WHERE f.image1 = :image1"),
    @NamedQuery(name = "Feeduser.findByDatecreated", query = "SELECT f FROM Feeduser f WHERE f.datecreated = :datecreated"),
    @NamedQuery(name = "Feeduser.findByTimemodified", query = "SELECT f FROM Feeduser f WHERE f.timemodified = :timemodified"),
    @NamedQuery(name = "Feeduser.findByExtradetails", query = "SELECT f FROM Feeduser f WHERE f.extradetails = :extradetails")})
public class Feeduser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "feeduserid")
    private Integer feeduserid;
    @Basic(optional = false)
    @Column(name = "emailAddress")
    private String emailAddress;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "dateOfBirth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(name = "phoneNumber1")
    private String phoneNumber1;
    @Column(name = "phoneNumber2")
    private String phoneNumber2;
    @Column(name = "fax")
    private String fax;
    @Column(name = "stateOrRegion")
    private String stateOrRegion;
    @Column(name = "city")
    private String city;
    @Column(name = "county")
    private String county;
    @Column(name = "streetAddress")
    private String streetAddress;
    @Column(name = "postalCode")
    private String postalCode;
    @Column(name = "image1")
    private String image1;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @Column(name = "extradetails")
    private String extradetails;
    @JoinColumn(name = "gender", referencedColumnName = "genderid")
    @ManyToOne
    private Gender gender;
    @JoinColumn(name = "country", referencedColumnName = "countryid")
    @ManyToOne
    private Country country;
    @JoinColumn(name = "howDidYouFindUs", referencedColumnName = "howdidyoufindusid")
    @ManyToOne
    private Howdidyoufindus howDidYouFindUs;
    @OneToMany(mappedBy = "feeduserid")
    private List<Installation> installationList;

    public Feeduser() {
    }

    public Feeduser(Integer feeduserid) {
        this.feeduserid = feeduserid;
    }

    public Feeduser(Integer feeduserid, String emailAddress, Date datecreated, Date timemodified) {
        this.feeduserid = feeduserid;
        this.emailAddress = emailAddress;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getFeeduserid() {
        return feeduserid;
    }

    public void setFeeduserid(Integer feeduserid) {
        this.feeduserid = feeduserid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getStateOrRegion() {
        return stateOrRegion;
    }

    public void setStateOrRegion(String stateOrRegion) {
        this.stateOrRegion = stateOrRegion;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public Date getTimemodified() {
        return timemodified;
    }

    public void setTimemodified(Date timemodified) {
        this.timemodified = timemodified;
    }

    public String getExtradetails() {
        return extradetails;
    }

    public void setExtradetails(String extradetails) {
        this.extradetails = extradetails;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Howdidyoufindus getHowDidYouFindUs() {
        return howDidYouFindUs;
    }

    public void setHowDidYouFindUs(Howdidyoufindus howDidYouFindUs) {
        this.howDidYouFindUs = howDidYouFindUs;
    }

    @XmlTransient
    public List<Installation> getInstallationList() {
        return installationList;
    }

    public void setInstallationList(List<Installation> installationList) {
        this.installationList = installationList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (feeduserid != null ? feeduserid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Feeduser)) {
            return false;
        }
        Feeduser other = (Feeduser) object;
        if ((this.feeduserid == null && other.feeduserid != null) || (this.feeduserid != null && !this.feeduserid.equals(other.feeduserid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Feeduser[ feeduserid=" + feeduserid + " ]";
    }

}

package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
 * @(#)Siteuser.java   20-May-2015 15:49:52
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
@Entity
@Table(name = "siteuser")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Siteuser.findAll", query = "SELECT s FROM Siteuser s"),
    @NamedQuery(name = "Siteuser.findBySiteuserid", query = "SELECT s FROM Siteuser s WHERE s.siteuserid = :siteuserid"),
    @NamedQuery(name = "Siteuser.findByEmailAddress", query = "SELECT s FROM Siteuser s WHERE s.emailAddress = :emailAddress"),
    @NamedQuery(name = "Siteuser.findByUsername", query = "SELECT s FROM Siteuser s WHERE s.username = :username"),
    @NamedQuery(name = "Siteuser.findByLastName", query = "SELECT s FROM Siteuser s WHERE s.lastName = :lastName"),
    @NamedQuery(name = "Siteuser.findByFirstName", query = "SELECT s FROM Siteuser s WHERE s.firstName = :firstName"),
    @NamedQuery(name = "Siteuser.findByDateOfBirth", query = "SELECT s FROM Siteuser s WHERE s.dateOfBirth = :dateOfBirth"),
    @NamedQuery(name = "Siteuser.findByPhoneNumber", query = "SELECT s FROM Siteuser s WHERE s.phoneNumber = :phoneNumber"),
    @NamedQuery(name = "Siteuser.findByMobileNumber", query = "SELECT s FROM Siteuser s WHERE s.mobileNumber = :mobileNumber"),
    @NamedQuery(name = "Siteuser.findByContactDetails", query = "SELECT s FROM Siteuser s WHERE s.contactDetails = :contactDetails"),
    @NamedQuery(name = "Siteuser.findByLogo", query = "SELECT s FROM Siteuser s WHERE s.logo = :logo"),
    @NamedQuery(name = "Siteuser.findByImage1", query = "SELECT s FROM Siteuser s WHERE s.image1 = :image1"),
    @NamedQuery(name = "Siteuser.findByImage2", query = "SELECT s FROM Siteuser s WHERE s.image2 = :image2"),
    @NamedQuery(name = "Siteuser.findByImage3", query = "SELECT s FROM Siteuser s WHERE s.image3 = :image3"),
    @NamedQuery(name = "Siteuser.findByUrl", query = "SELECT s FROM Siteuser s WHERE s.url = :url"),
    @NamedQuery(name = "Siteuser.findByDatecreated", query = "SELECT s FROM Siteuser s WHERE s.datecreated = :datecreated"),
    @NamedQuery(name = "Siteuser.findByTimemodified", query = "SELECT s FROM Siteuser s WHERE s.timemodified = :timemodified")})
public class Siteuser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "siteuserid")
    private Integer siteuserid;
    @Basic(optional = false)
    @Column(name = "emailAddress")
    private String emailAddress;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "dateOfBirth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "mobileNumber")
    private String mobileNumber;
    @Column(name = "contactDetails")
    private String contactDetails;
    @Column(name = "logo")
    private String logo;
    @Column(name = "image1")
    private String image1;
    @Column(name = "image2")
    private String image2;
    @Column(name = "image3")
    private String image3;
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @JoinColumn(name = "howdidyoufindusid", referencedColumnName = "howdidyoufindusid")
    @ManyToOne
    private Howdidyoufindus howdidyoufindusid;
    @JoinColumn(name = "genderid", referencedColumnName = "genderid")
    @ManyToOne
    private Gender genderid;
    @JoinColumn(name = "addressid", referencedColumnName = "addressid")
    @ManyToOne
    private Address addressid;
    @JoinColumn(name = "userstatusid", referencedColumnName = "userstatusid")
    @ManyToOne(optional = false)
    private Userstatus userstatusid;
    @JoinColumn(name = "currencyid", referencedColumnName = "currencyid")
    @ManyToOne(optional = false)
    private Currency currencyid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "seller")
    private List<Product> productList;
    @OneToMany(mappedBy = "author")
    private List<Productcomment> productcommentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buyer")
    private List<Productorder> productorderList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentmethoduser")
    private List<Userpaymentmethod> userpaymentmethodList;

    public Siteuser() {
    }

    public Siteuser(Integer siteuserid) {
        this.siteuserid = siteuserid;
    }

    public Siteuser(Integer siteuserid, String emailAddress, String username, Date datecreated, Date timemodified) {
        this.siteuserid = siteuserid;
        this.emailAddress = emailAddress;
        this.username = username;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getSiteuserid() {
        return siteuserid;
    }

    public void setSiteuserid(Integer siteuserid) {
        this.siteuserid = siteuserid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Howdidyoufindus getHowdidyoufindusid() {
        return howdidyoufindusid;
    }

    public void setHowdidyoufindusid(Howdidyoufindus howdidyoufindusid) {
        this.howdidyoufindusid = howdidyoufindusid;
    }

    public Gender getGenderid() {
        return genderid;
    }

    public void setGenderid(Gender genderid) {
        this.genderid = genderid;
    }

    public Address getAddressid() {
        return addressid;
    }

    public void setAddressid(Address addressid) {
        this.addressid = addressid;
    }

    public Userstatus getUserstatusid() {
        return userstatusid;
    }

    public void setUserstatusid(Userstatus userstatusid) {
        this.userstatusid = userstatusid;
    }

    public Currency getCurrencyid() {
        return currencyid;
    }

    public void setCurrencyid(Currency currencyid) {
        this.currencyid = currencyid;
    }

    @XmlTransient
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @XmlTransient
    public List<Productcomment> getProductcommentList() {
        return productcommentList;
    }

    public void setProductcommentList(List<Productcomment> productcommentList) {
        this.productcommentList = productcommentList;
    }

    @XmlTransient
    public List<Productorder> getProductorderList() {
        return productorderList;
    }

    public void setProductorderList(List<Productorder> productorderList) {
        this.productorderList = productorderList;
    }

    @XmlTransient
    public List<Userpaymentmethod> getUserpaymentmethodList() {
        return userpaymentmethodList;
    }

    public void setUserpaymentmethodList(List<Userpaymentmethod> userpaymentmethodList) {
        this.userpaymentmethodList = userpaymentmethodList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (siteuserid != null ? siteuserid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Siteuser)) {
            return false;
        }
        Siteuser other = (Siteuser) object;
        if ((this.siteuserid == null && other.siteuserid != null) || (this.siteuserid != null && !this.siteuserid.equals(other.siteuserid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Siteuser[ siteuserid=" + siteuserid + " ]";
    }

}

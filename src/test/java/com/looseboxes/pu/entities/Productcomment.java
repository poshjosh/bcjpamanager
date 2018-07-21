package com.looseboxes.pu.entities;

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
 * @(#)Productcomment.java   20-May-2015 15:49:52
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
@Table(name = "productcomment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Productcomment.findAll", query = "SELECT p FROM Productcomment p"),
    @NamedQuery(name = "Productcomment.findByProductcommentid", query = "SELECT p FROM Productcomment p WHERE p.productcommentid = :productcommentid"),
    @NamedQuery(name = "Productcomment.findByHeadline", query = "SELECT p FROM Productcomment p WHERE p.headline = :headline"),
    @NamedQuery(name = "Productcomment.findByCommentText", query = "SELECT p FROM Productcomment p WHERE p.commentText = :commentText"),
    @NamedQuery(name = "Productcomment.findByKeywords", query = "SELECT p FROM Productcomment p WHERE p.keywords = :keywords"),
    @NamedQuery(name = "Productcomment.findByDatecreated", query = "SELECT p FROM Productcomment p WHERE p.datecreated = :datecreated"),
    @NamedQuery(name = "Productcomment.findByTimemodified", query = "SELECT p FROM Productcomment p WHERE p.timemodified = :timemodified")})
public class Productcomment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "productcommentid")
    private Integer productcommentid;
    @Column(name = "headline")
    private String headline;
    @Basic(optional = false)
    @Column(name = "commentText")
    private String commentText;
    @Column(name = "keywords")
    private String keywords;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @OneToMany(mappedBy = "isreplyto")
    private List<Productcomment> productcommentList;
    @JoinColumn(name = "isreplyto", referencedColumnName = "productcommentid")
    @ManyToOne
    private Productcomment isreplyto;
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    @ManyToOne(optional = false)
    private Product productid;
    @JoinColumn(name = "author", referencedColumnName = "siteuserid")
    @ManyToOne
    private Siteuser author;

    public Productcomment() {
    }

    public Productcomment(Integer productcommentid) {
        this.productcommentid = productcommentid;
    }

    public Productcomment(Integer productcommentid, String commentText, Date datecreated, Date timemodified) {
        this.productcommentid = productcommentid;
        this.commentText = commentText;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getProductcommentid() {
        return productcommentid;
    }

    public void setProductcommentid(Integer productcommentid) {
        this.productcommentid = productcommentid;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
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

    @XmlTransient
    public List<Productcomment> getProductcommentList() {
        return productcommentList;
    }

    public void setProductcommentList(List<Productcomment> productcommentList) {
        this.productcommentList = productcommentList;
    }

    public Productcomment getIsreplyto() {
        return isreplyto;
    }

    public void setIsreplyto(Productcomment isreplyto) {
        this.isreplyto = isreplyto;
    }

    public Product getProductid() {
        return productid;
    }

    public void setProductid(Product productid) {
        this.productid = productid;
    }

    public Siteuser getAuthor() {
        return author;
    }

    public void setAuthor(Siteuser author) {
        this.author = author;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productcommentid != null ? productcommentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productcomment)) {
            return false;
        }
        Productcomment other = (Productcomment) object;
        if ((this.productcommentid == null && other.productcommentid != null) || (this.productcommentid != null && !this.productcommentid.equals(other.productcommentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Productcomment[ productcommentid=" + productcommentid + " ]";
    }

}

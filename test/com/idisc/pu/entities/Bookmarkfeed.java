/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 10:53:10 PM
 */
@Entity
@Table(name = "bookmarkfeed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bookmarkfeed.findAll", query = "SELECT b FROM Bookmarkfeed b"),
    @NamedQuery(name = "Bookmarkfeed.findByBookmarkfeedid", query = "SELECT b FROM Bookmarkfeed b WHERE b.bookmarkfeedid = :bookmarkfeedid"),
    @NamedQuery(name = "Bookmarkfeed.findByDatecreated", query = "SELECT b FROM Bookmarkfeed b WHERE b.datecreated = :datecreated")})
public class Bookmarkfeed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "bookmarkfeedid")
    private Integer bookmarkfeedid;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @JoinColumn(name = "installationid", referencedColumnName = "installationid")
    @ManyToOne(optional = false)
    private Installation installationid;
    @JoinColumn(name = "feedid", referencedColumnName = "feedid")
    @ManyToOne(optional = false)
    private Feed feedid;

    public Bookmarkfeed() {
    }

    public Bookmarkfeed(Integer bookmarkfeedid) {
        this.bookmarkfeedid = bookmarkfeedid;
    }

    public Bookmarkfeed(Integer bookmarkfeedid, Date datecreated) {
        this.bookmarkfeedid = bookmarkfeedid;
        this.datecreated = datecreated;
    }

    public Integer getBookmarkfeedid() {
        return bookmarkfeedid;
    }

    public void setBookmarkfeedid(Integer bookmarkfeedid) {
        this.bookmarkfeedid = bookmarkfeedid;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    public Installation getInstallationid() {
        return installationid;
    }

    public void setInstallationid(Installation installationid) {
        this.installationid = installationid;
    }

    public Feed getFeedid() {
        return feedid;
    }

    public void setFeedid(Feed feedid) {
        this.feedid = feedid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookmarkfeedid != null ? bookmarkfeedid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bookmarkfeed)) {
            return false;
        }
        Bookmarkfeed other = (Bookmarkfeed) object;
        if ((this.bookmarkfeedid == null && other.bookmarkfeedid != null) || (this.bookmarkfeedid != null && !this.bookmarkfeedid.equals(other.bookmarkfeedid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Bookmarkfeed[ bookmarkfeedid=" + bookmarkfeedid + " ]";
    }

}

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
@Table(name = "commentreplynotice")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Commentreplynotice.findAll", query = "SELECT c FROM Commentreplynotice c"),
    @NamedQuery(name = "Commentreplynotice.findByCommentreplynoticeid", query = "SELECT c FROM Commentreplynotice c WHERE c.commentreplynoticeid = :commentreplynoticeid"),
    @NamedQuery(name = "Commentreplynotice.findByDateusernotified", query = "SELECT c FROM Commentreplynotice c WHERE c.dateusernotified = :dateusernotified"),
    @NamedQuery(name = "Commentreplynotice.findByDateuserread", query = "SELECT c FROM Commentreplynotice c WHERE c.dateuserread = :dateuserread"),
    @NamedQuery(name = "Commentreplynotice.findByTimemodified", query = "SELECT c FROM Commentreplynotice c WHERE c.timemodified = :timemodified"),
    @NamedQuery(name = "Commentreplynotice.findByExtradetails", query = "SELECT c FROM Commentreplynotice c WHERE c.extradetails = :extradetails")})
public class Commentreplynotice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "commentreplynoticeid")
    private Integer commentreplynoticeid;
    @Basic(optional = false)
    @Column(name = "dateusernotified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateusernotified;
    @Column(name = "dateuserread")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateuserread;
    @Basic(optional = false)
    @Column(name = "timemodified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timemodified;
    @Column(name = "extradetails")
    private String extradetails;
    @JoinColumn(name = "commentid", referencedColumnName = "commentid")
    @ManyToOne(optional = false)
    private Comment commentid;

    public Commentreplynotice() {
    }

    public Commentreplynotice(Integer commentreplynoticeid) {
        this.commentreplynoticeid = commentreplynoticeid;
    }

    public Commentreplynotice(Integer commentreplynoticeid, Date dateusernotified, Date timemodified) {
        this.commentreplynoticeid = commentreplynoticeid;
        this.dateusernotified = dateusernotified;
        this.timemodified = timemodified;
    }

    public Integer getCommentreplynoticeid() {
        return commentreplynoticeid;
    }

    public void setCommentreplynoticeid(Integer commentreplynoticeid) {
        this.commentreplynoticeid = commentreplynoticeid;
    }

    public Date getDateusernotified() {
        return dateusernotified;
    }

    public void setDateusernotified(Date dateusernotified) {
        this.dateusernotified = dateusernotified;
    }

    public Date getDateuserread() {
        return dateuserread;
    }

    public void setDateuserread(Date dateuserread) {
        this.dateuserread = dateuserread;
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

    public Comment getCommentid() {
        return commentid;
    }

    public void setCommentid(Comment commentid) {
        this.commentid = commentid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (commentreplynoticeid != null ? commentreplynoticeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Commentreplynotice)) {
            return false;
        }
        Commentreplynotice other = (Commentreplynotice) object;
        if ((this.commentreplynoticeid == null && other.commentreplynoticeid != null) || (this.commentreplynoticeid != null && !this.commentreplynoticeid.equals(other.commentreplynoticeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Commentreplynotice[ commentreplynoticeid=" + commentreplynoticeid + " ]";
    }

}

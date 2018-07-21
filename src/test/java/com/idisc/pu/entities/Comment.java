/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.idisc.pu.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * @author Chinomso Bassey Ikwuagwu on Feb 5, 2017 10:52:04 PM
 */
@Entity
@Table(name = "comment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c"),
    @NamedQuery(name = "Comment.findByCommentid", query = "SELECT c FROM Comment c WHERE c.commentid = :commentid"),
    @NamedQuery(name = "Comment.findByCommentSubject", query = "SELECT c FROM Comment c WHERE c.commentSubject = :commentSubject"),
    @NamedQuery(name = "Comment.findByCommentText", query = "SELECT c FROM Comment c WHERE c.commentText = :commentText"),
    @NamedQuery(name = "Comment.findByDatecreated", query = "SELECT c FROM Comment c WHERE c.datecreated = :datecreated"),
    @NamedQuery(name = "Comment.findByTimemodified", query = "SELECT c FROM Comment c WHERE c.timemodified = :timemodified"),
    @NamedQuery(name = "Comment.findByExtradetails", query = "SELECT c FROM Comment c WHERE c.extradetails = :extradetails")})
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "commentid")
    private Integer commentid;
    @Column(name = "commentSubject")
    private String commentSubject;
    @Basic(optional = false)
    @Column(name = "commentText")
    private String commentText;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "commentid", fetch = FetchType.LAZY)
    private List<Commentreplynotice> commentreplynoticeList;
    @JoinColumn(name = "feedid", referencedColumnName = "feedid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Feed feedid;
    @JoinColumn(name = "installationid", referencedColumnName = "installationid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Installation installationid;
    @OneToMany(mappedBy = "repliedto", fetch = FetchType.LAZY)
    private List<Comment> commentList;
    @JoinColumn(name = "repliedto", referencedColumnName = "commentid")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment repliedto;

    public Comment() {
    }

    public Comment(Integer commentid) {
        this.commentid = commentid;
    }

    public Comment(Integer commentid, String commentText, Date datecreated, Date timemodified) {
        this.commentid = commentid;
        this.commentText = commentText;
        this.datecreated = datecreated;
        this.timemodified = timemodified;
    }

    public Integer getCommentid() {
        return commentid;
    }

    public void setCommentid(Integer commentid) {
        this.commentid = commentid;
    }

    public String getCommentSubject() {
        return commentSubject;
    }

    public void setCommentSubject(String commentSubject) {
        this.commentSubject = commentSubject;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
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

    @XmlTransient
    public List<Commentreplynotice> getCommentreplynoticeList() {
        return commentreplynoticeList;
    }

    public void setCommentreplynoticeList(List<Commentreplynotice> commentreplynoticeList) {
        this.commentreplynoticeList = commentreplynoticeList;
    }

    public Feed getFeedid() {
        return feedid;
    }

    public void setFeedid(Feed feedid) {
        this.feedid = feedid;
    }

    public Installation getInstallationid() {
        return installationid;
    }

    public void setInstallationid(Installation installationid) {
        this.installationid = installationid;
    }

    @XmlTransient
    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public Comment getRepliedto() {
        return repliedto;
    }

    public void setRepliedto(Comment repliedto) {
        this.repliedto = repliedto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (commentid != null ? commentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.commentid == null && other.commentid != null) || (this.commentid != null && !this.commentid.equals(other.commentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Comment[ commentid=" + commentid + " ]";
    }

}

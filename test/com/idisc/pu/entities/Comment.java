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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;












@Entity
@Table(name="comment")
@XmlRootElement
@NamedQueries({@javax.persistence.NamedQuery(name="Comment.findAll", query="SELECT c FROM Comment c"), @javax.persistence.NamedQuery(name="Comment.findByCommentid", query="SELECT c FROM Comment c WHERE c.commentid = :commentid"), @javax.persistence.NamedQuery(name="Comment.findByCommentSubject", query="SELECT c FROM Comment c WHERE c.commentSubject = :commentSubject"), @javax.persistence.NamedQuery(name="Comment.findByCommentText", query="SELECT c FROM Comment c WHERE c.commentText = :commentText"), @javax.persistence.NamedQuery(name="Comment.findByDateusernotified", query="SELECT c FROM Comment c WHERE c.dateusernotified = :dateusernotified"), @javax.persistence.NamedQuery(name="Comment.findByDatecreated", query="SELECT c FROM Comment c WHERE c.datecreated = :datecreated"), @javax.persistence.NamedQuery(name="Comment.findByTimemodified", query="SELECT c FROM Comment c WHERE c.timemodified = :timemodified"), @javax.persistence.NamedQuery(name="Comment.findByExtradetails", query="SELECT c FROM Comment c WHERE c.extradetails = :extradetails")})
public class Comment
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Basic(optional=false)
  @Column(name="commentid")
  private Integer commentid;
  @Column(name="commentSubject")
  private String commentSubject;
  @Basic(optional=false)
  @Column(name="commentText")
  private String commentText;
  @Column(name="dateusernotified")
  @Temporal(TemporalType.TIMESTAMP)
  private Date dateusernotified;
  @Basic(optional=false)
  @Column(name="datecreated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date datecreated;
  @Basic(optional=false)
  @Column(name="timemodified")
  @Temporal(TemporalType.TIMESTAMP)
  private Date timemodified;
  @Column(name="extradetails")
  private String extradetails;
  @JoinColumn(name="feedid", referencedColumnName="feedid")
  @ManyToOne(optional=false)
  private Feed feedid;
  @JoinColumn(name="installationid", referencedColumnName="installationid")
  @ManyToOne(optional=false)
  private Installation installationid;
  @OneToMany(mappedBy="repliedto")
  private List<Comment> commentList;
  @JoinColumn(name="repliedto", referencedColumnName="commentid")
  @ManyToOne
  private Comment repliedto;
  
  public Comment() {}
  
  public Comment(Integer commentid)
  {
    this.commentid = commentid;
  }
  
  public Comment(Integer commentid, String commentText, Date datecreated, Date timemodified) {
    this.commentid = commentid;
    this.commentText = commentText;
    this.datecreated = datecreated;
    this.timemodified = timemodified;
  }
  
  public Integer getCommentid() {
    return this.commentid;
  }
  
  public void setCommentid(Integer commentid) {
    this.commentid = commentid;
  }
  
  public String getCommentSubject() {
    return this.commentSubject;
  }
  
  public void setCommentSubject(String commentSubject) {
    this.commentSubject = commentSubject;
  }
  
  public String getCommentText() {
    return this.commentText;
  }
  
  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }
  
  public Date getDateusernotified() {
    return this.dateusernotified;
  }
  
  public void setDateusernotified(Date dateusernotified) {
    this.dateusernotified = dateusernotified;
  }
  
  public Date getDatecreated() {
    return this.datecreated;
  }
  
  public void setDatecreated(Date datecreated) {
    this.datecreated = datecreated;
  }
  
  public Date getTimemodified() {
    return this.timemodified;
  }
  
  public void setTimemodified(Date timemodified) {
    this.timemodified = timemodified;
  }
  
  public String getExtradetails() {
    return this.extradetails;
  }
  
  public void setExtradetails(String extradetails) {
    this.extradetails = extradetails;
  }
  
  public Feed getFeedid() {
    return this.feedid;
  }
  
  public void setFeedid(Feed feedid) {
    this.feedid = feedid;
  }
  
  public Installation getInstallationid() {
    return this.installationid;
  }
  
  public void setInstallationid(Installation installationid) {
    this.installationid = installationid;
  }
  
  @XmlTransient
  public List<Comment> getCommentList() {
    return this.commentList;
  }
  
  public void setCommentList(List<Comment> commentList) {
    this.commentList = commentList;
  }
  
  public Comment getRepliedto() {
    return this.repliedto;
  }
  
  public void setRepliedto(Comment repliedto) {
    this.repliedto = repliedto;
  }
  
  public int hashCode()
  {
    int hash = 0;
    hash += (this.commentid != null ? this.commentid.hashCode() : 0);
    return hash;
  }
  

  public boolean equals(Object object)
  {
    if (!(object instanceof Comment)) {
      return false;
    }
    Comment other = (Comment)object;
    if (((this.commentid == null) && (other.commentid != null)) || ((this.commentid != null) && (!this.commentid.equals(other.commentid)))) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "com.idisc.pu.entities.Comment[ commentid=" + this.commentid + " ]";
  }
}

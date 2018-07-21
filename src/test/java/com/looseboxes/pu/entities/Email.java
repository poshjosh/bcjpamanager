package com.looseboxes.pu.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @(#)Email.java   30-May-2015 10:13:31
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
@Table(name = "email")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Email.findAll", query = "SELECT e FROM Email e"),
    @NamedQuery(name = "Email.findByEmailid", query = "SELECT e FROM Email e WHERE e.emailid = :emailid"),
    @NamedQuery(name = "Email.findByFromEmail", query = "SELECT e FROM Email e WHERE e.fromEmail = :fromEmail"),
    @NamedQuery(name = "Email.findByToEmails", query = "SELECT e FROM Email e WHERE e.toEmails = :toEmails"),
    @NamedQuery(name = "Email.findByEmailSubject", query = "SELECT e FROM Email e WHERE e.emailSubject = :emailSubject"),
    @NamedQuery(name = "Email.findByEmailText", query = "SELECT e FROM Email e WHERE e.emailText = :emailText"),
    @NamedQuery(name = "Email.findByContentType", query = "SELECT e FROM Email e WHERE e.contentType = :contentType"),
    @NamedQuery(name = "Email.findByFile1", query = "SELECT e FROM Email e WHERE e.file1 = :file1"),
    @NamedQuery(name = "Email.findByFile2", query = "SELECT e FROM Email e WHERE e.file2 = :file2"),
    @NamedQuery(name = "Email.findByFile3", query = "SELECT e FROM Email e WHERE e.file3 = :file3"),
    @NamedQuery(name = "Email.findByFile4", query = "SELECT e FROM Email e WHERE e.file4 = :file4"),
    @NamedQuery(name = "Email.findByFile5", query = "SELECT e FROM Email e WHERE e.file5 = :file5"),
    @NamedQuery(name = "Email.findByFile6", query = "SELECT e FROM Email e WHERE e.file6 = :file6"),
    @NamedQuery(name = "Email.findByFile7", query = "SELECT e FROM Email e WHERE e.file7 = :file7"),
    @NamedQuery(name = "Email.findBySent", query = "SELECT e FROM Email e WHERE e.sent = :sent"),
    @NamedQuery(name = "Email.findByDatecreated", query = "SELECT e FROM Email e WHERE e.datecreated = :datecreated")})
public class Email implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "emailid")
    private Integer emailid;
    @Basic(optional = false)
    @Column(name = "fromEmail")
    private String fromEmail;
    @Basic(optional = false)
    @Column(name = "toEmails")
    private String toEmails;
    @Column(name = "emailSubject")
    private String emailSubject;
    @Basic(optional = false)
    @Column(name = "emailText")
    private String emailText;
    @Column(name = "contentType")
    private String contentType;
    @Column(name = "file1")
    private String file1;
    @Column(name = "file2")
    private String file2;
    @Column(name = "file3")
    private String file3;
    @Column(name = "file4")
    private String file4;
    @Column(name = "file5")
    private String file5;
    @Column(name = "file6")
    private String file6;
    @Column(name = "file7")
    private String file7;
    @Basic(optional = false)
    @Column(name = "sent")
    private boolean sent;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;

    public Email() {
    }

    public Email(Integer emailid) {
        this.emailid = emailid;
    }

    public Email(Integer emailid, String fromEmail, String toEmails, String emailText, boolean sent, Date datecreated) {
        this.emailid = emailid;
        this.fromEmail = fromEmail;
        this.toEmails = toEmails;
        this.emailText = emailText;
        this.sent = sent;
        this.datecreated = datecreated;
    }

    public Integer getEmailid() {
        return emailid;
    }

    public void setEmailid(Integer emailid) {
        this.emailid = emailid;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmails() {
        return toEmails;
    }

    public void setToEmails(String toEmails) {
        this.toEmails = toEmails;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailText() {
        return emailText;
    }

    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getFile3() {
        return file3;
    }

    public void setFile3(String file3) {
        this.file3 = file3;
    }

    public String getFile4() {
        return file4;
    }

    public void setFile4(String file4) {
        this.file4 = file4;
    }

    public String getFile5() {
        return file5;
    }

    public void setFile5(String file5) {
        this.file5 = file5;
    }

    public String getFile6() {
        return file6;
    }

    public void setFile6(String file6) {
        this.file6 = file6;
    }

    public String getFile7() {
        return file7;
    }

    public void setFile7(String file7) {
        this.file7 = file7;
    }

    public boolean getSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Date getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreated = datecreated;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (emailid != null ? emailid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Email)) {
            return false;
        }
        Email other = (Email) object;
        if ((this.emailid == null && other.emailid != null) || (this.emailid != null && !this.emailid.equals(other.emailid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Email[ emailid=" + emailid + " ]";
    }

}

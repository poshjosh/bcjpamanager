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
 * @(#)Chatmessage.java   20-May-2015 15:49:51
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
@Table(name = "chatmessage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Chatmessage.findAll", query = "SELECT c FROM Chatmessage c"),
    @NamedQuery(name = "Chatmessage.findByChatmessageid", query = "SELECT c FROM Chatmessage c WHERE c.chatmessageid = :chatmessageid"),
    @NamedQuery(name = "Chatmessage.findByFromEmail", query = "SELECT c FROM Chatmessage c WHERE c.fromEmail = :fromEmail"),
    @NamedQuery(name = "Chatmessage.findByToEmail", query = "SELECT c FROM Chatmessage c WHERE c.toEmail = :toEmail"),
    @NamedQuery(name = "Chatmessage.findByChatText", query = "SELECT c FROM Chatmessage c WHERE c.chatText = :chatText"),
    @NamedQuery(name = "Chatmessage.findByDatecreated", query = "SELECT c FROM Chatmessage c WHERE c.datecreated = :datecreated")})
public class Chatmessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "chatmessageid")
    private Integer chatmessageid;
    @Basic(optional = false)
    @Column(name = "fromEmail")
    private String fromEmail;
    @Basic(optional = false)
    @Column(name = "toEmail")
    private String toEmail;
    @Basic(optional = false)
    @Column(name = "chatText")
    private String chatText;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;

    public Chatmessage() {
    }

    public Chatmessage(Integer chatmessageid) {
        this.chatmessageid = chatmessageid;
    }

    public Chatmessage(Integer chatmessageid, String fromEmail, String toEmail, String chatText, Date datecreated) {
        this.chatmessageid = chatmessageid;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.chatText = chatText;
        this.datecreated = datecreated;
    }

    public Integer getChatmessageid() {
        return chatmessageid;
    }

    public void setChatmessageid(Integer chatmessageid) {
        this.chatmessageid = chatmessageid;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
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
        hash += (chatmessageid != null ? chatmessageid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Chatmessage)) {
            return false;
        }
        Chatmessage other = (Chatmessage) object;
        if ((this.chatmessageid == null && other.chatmessageid != null) || (this.chatmessageid != null && !this.chatmessageid.equals(other.chatmessageid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.looseboxes.pu.entities.Chatmessage[ chatmessageid=" + chatmessageid + " ]";
    }

}

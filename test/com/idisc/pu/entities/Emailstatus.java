/*
 * Copyright 2016 NUROX Ltd.
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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
 * @author Chinomso Bassey Ikwuagwu on Oct 3, 2016 5:25:25 PM
 */
@Entity
@Table(name = "emailstatus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Emailstatus.findAll", query = "SELECT e FROM Emailstatus e"),
    @NamedQuery(name = "Emailstatus.findByEmailstatusid", query = "SELECT e FROM Emailstatus e WHERE e.emailstatusid = :emailstatusid"),
    @NamedQuery(name = "Emailstatus.findByEmailstatus", query = "SELECT e FROM Emailstatus e WHERE e.emailstatus = :emailstatus")})
public class Emailstatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "emailstatusid")
    private Short emailstatusid;
    @Basic(optional = false)
    @Column(name = "emailstatus")
    private String emailstatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emailstatus")
    private List<Extractedemail> extractedemailList;

    public Emailstatus() {
    }

    public Emailstatus(Short emailstatusid) {
        this.emailstatusid = emailstatusid;
    }

    public Emailstatus(Short emailstatusid, String emailstatus) {
        this.emailstatusid = emailstatusid;
        this.emailstatus = emailstatus;
    }

    public Short getEmailstatusid() {
        return emailstatusid;
    }

    public void setEmailstatusid(Short emailstatusid) {
        this.emailstatusid = emailstatusid;
    }

    public String getEmailstatus() {
        return emailstatus;
    }

    public void setEmailstatus(String emailstatus) {
        this.emailstatus = emailstatus;
    }

    @XmlTransient
    public List<Extractedemail> getExtractedemailList() {
        return extractedemailList;
    }

    public void setExtractedemailList(List<Extractedemail> extractedemailList) {
        this.extractedemailList = extractedemailList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (emailstatusid != null ? emailstatusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Emailstatus)) {
            return false;
        }
        Emailstatus other = (Emailstatus) object;
        if ((this.emailstatusid == null && other.emailstatusid != null) || (this.emailstatusid != null && !this.emailstatusid.equals(other.emailstatusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Emailstatus[ emailstatusid=" + emailstatusid + " ]";
    }

}

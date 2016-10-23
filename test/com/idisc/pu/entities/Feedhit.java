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
 * @author Chinomso Bassey Ikwuagwu on Oct 3, 2016 5:25:24 PM
 */
@Entity
@Table(name = "feedhit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feedhit.findAll", query = "SELECT f FROM Feedhit f"),
    @NamedQuery(name = "Feedhit.findByFeedhitid", query = "SELECT f FROM Feedhit f WHERE f.feedhitid = :feedhitid"),
    @NamedQuery(name = "Feedhit.findByHittime", query = "SELECT f FROM Feedhit f WHERE f.hittime = :hittime")})
public class Feedhit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "feedhitid")
    private Integer feedhitid;
    @Basic(optional = false)
    @Column(name = "hittime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hittime;
    @JoinColumn(name = "feedid", referencedColumnName = "feedid")
    @ManyToOne(optional = false)
    private Feed feedid;
    @JoinColumn(name = "installationid", referencedColumnName = "installationid")
    @ManyToOne
    private Installation installationid;

    public Feedhit() {
    }

    public Feedhit(Integer feedhitid) {
        this.feedhitid = feedhitid;
    }

    public Feedhit(Integer feedhitid, Date hittime) {
        this.feedhitid = feedhitid;
        this.hittime = hittime;
    }

    public Integer getFeedhitid() {
        return feedhitid;
    }

    public void setFeedhitid(Integer feedhitid) {
        this.feedhitid = feedhitid;
    }

    public Date getHittime() {
        return hittime;
    }

    public void setHittime(Date hittime) {
        this.hittime = hittime;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (feedhitid != null ? feedhitid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Feedhit)) {
            return false;
        }
        Feedhit other = (Feedhit) object;
        if ((this.feedhitid == null && other.feedhitid != null) || (this.feedhitid != null && !this.feedhitid.equals(other.feedhitid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Feedhit[ feedhitid=" + feedhitid + " ]";
    }

}

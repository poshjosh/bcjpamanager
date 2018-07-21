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
import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 5, 2017 10:52:02 PM
 */
@Entity
@Table(name = "favoritefeed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Favoritefeed.findAll", query = "SELECT f FROM Favoritefeed f"),
    @NamedQuery(name = "Favoritefeed.findByFavoritefeedid", query = "SELECT f FROM Favoritefeed f WHERE f.favoritefeedid = :favoritefeedid"),
    @NamedQuery(name = "Favoritefeed.findByDatecreated", query = "SELECT f FROM Favoritefeed f WHERE f.datecreated = :datecreated")})
public class Favoritefeed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "favoritefeedid")
    private Integer favoritefeedid;
    @Basic(optional = false)
    @Column(name = "datecreated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreated;
    @JoinColumn(name = "installationid", referencedColumnName = "installationid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Installation installationid;
    @JoinColumn(name = "feedid", referencedColumnName = "feedid")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Feed feedid;

    public Favoritefeed() {
    }

    public Favoritefeed(Integer favoritefeedid) {
        this.favoritefeedid = favoritefeedid;
    }

    public Favoritefeed(Integer favoritefeedid, Date datecreated) {
        this.favoritefeedid = favoritefeedid;
        this.datecreated = datecreated;
    }

    public Integer getFavoritefeedid() {
        return favoritefeedid;
    }

    public void setFavoritefeedid(Integer favoritefeedid) {
        this.favoritefeedid = favoritefeedid;
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
        hash += (favoritefeedid != null ? favoritefeedid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Favoritefeed)) {
            return false;
        }
        Favoritefeed other = (Favoritefeed) object;
        if ((this.favoritefeedid == null && other.favoritefeedid != null) || (this.favoritefeedid != null && !this.favoritefeedid.equals(other.favoritefeedid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Favoritefeed[ favoritefeedid=" + favoritefeedid + " ]";
    }

}

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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 5, 2017 10:52:03 PM
 */
@Entity
@Table(name = "howdidyoufindus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Howdidyoufindus.findAll", query = "SELECT h FROM Howdidyoufindus h"),
    @NamedQuery(name = "Howdidyoufindus.findByHowdidyoufindusid", query = "SELECT h FROM Howdidyoufindus h WHERE h.howdidyoufindusid = :howdidyoufindusid"),
    @NamedQuery(name = "Howdidyoufindus.findByHowdidyoufindus", query = "SELECT h FROM Howdidyoufindus h WHERE h.howdidyoufindus = :howdidyoufindus")})
public class Howdidyoufindus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "howdidyoufindusid")
    private Short howdidyoufindusid;
    @Basic(optional = false)
    @Column(name = "howdidyoufindus")
    private String howdidyoufindus;
    @OneToMany(mappedBy = "howDidYouFindUs", fetch = FetchType.LAZY)
    private List<Feeduser> feeduserList;

    public Howdidyoufindus() {
    }

    public Howdidyoufindus(Short howdidyoufindusid) {
        this.howdidyoufindusid = howdidyoufindusid;
    }

    public Howdidyoufindus(Short howdidyoufindusid, String howdidyoufindus) {
        this.howdidyoufindusid = howdidyoufindusid;
        this.howdidyoufindus = howdidyoufindus;
    }

    public Short getHowdidyoufindusid() {
        return howdidyoufindusid;
    }

    public void setHowdidyoufindusid(Short howdidyoufindusid) {
        this.howdidyoufindusid = howdidyoufindusid;
    }

    public String getHowdidyoufindus() {
        return howdidyoufindus;
    }

    public void setHowdidyoufindus(String howdidyoufindus) {
        this.howdidyoufindus = howdidyoufindus;
    }

    @XmlTransient
    public List<Feeduser> getFeeduserList() {
        return feeduserList;
    }

    public void setFeeduserList(List<Feeduser> feeduserList) {
        this.feeduserList = feeduserList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (howdidyoufindusid != null ? howdidyoufindusid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Howdidyoufindus)) {
            return false;
        }
        Howdidyoufindus other = (Howdidyoufindus) object;
        if ((this.howdidyoufindusid == null && other.howdidyoufindusid != null) || (this.howdidyoufindusid != null && !this.howdidyoufindusid.equals(other.howdidyoufindusid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Howdidyoufindus[ howdidyoufindusid=" + howdidyoufindusid + " ]";
    }

}

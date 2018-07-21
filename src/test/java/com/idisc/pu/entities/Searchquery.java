/*
 * Copyright 2018 NUROX Ltd.
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 8, 2018 2:00:40 AM
 */
@Entity
@Table(name = "searchquery")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Searchquery.findAll", query = "SELECT s FROM Searchquery s"),
    @NamedQuery(name = "Searchquery.findBySearchqueryid", query = "SELECT s FROM Searchquery s WHERE s.searchqueryid = :searchqueryid"),
    @NamedQuery(name = "Searchquery.findBySearchquery", query = "SELECT s FROM Searchquery s WHERE s.searchquery = :searchquery"),
    @NamedQuery(name = "Searchquery.findBySearchtime", query = "SELECT s FROM Searchquery s WHERE s.searchtime = :searchtime")})
public class Searchquery implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "searchqueryid")
    private Integer searchqueryid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "searchquery")
    private String searchquery;
    @Basic(optional = false)
    @NotNull
    @Column(name = "searchtime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date searchtime;
    @JoinColumn(name = "installationid", referencedColumnName = "installationid")
    @ManyToOne
    private Installation installationid;

    public Searchquery() {
    }

    public Searchquery(Integer searchqueryid) {
        this.searchqueryid = searchqueryid;
    }

    public Searchquery(Integer searchqueryid, String searchquery, Date searchtime) {
        this.searchqueryid = searchqueryid;
        this.searchquery = searchquery;
        this.searchtime = searchtime;
    }

    public Integer getSearchqueryid() {
        return searchqueryid;
    }

    public void setSearchqueryid(Integer searchqueryid) {
        this.searchqueryid = searchqueryid;
    }

    public String getSearchquery() {
        return searchquery;
    }

    public void setSearchquery(String searchquery) {
        this.searchquery = searchquery;
    }

    public Date getSearchtime() {
        return searchtime;
    }

    public void setSearchtime(Date searchtime) {
        this.searchtime = searchtime;
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
        hash += (searchqueryid != null ? searchqueryid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Searchquery)) {
            return false;
        }
        Searchquery other = (Searchquery) object;
        if ((this.searchqueryid == null && other.searchqueryid != null) || (this.searchqueryid != null && !this.searchqueryid.equals(other.searchqueryid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.idisc.pu.entities.Searchquery[ searchqueryid=" + searchqueryid + " ]";
    }

}

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

package com.bc.jpa.metadata;

import com.bc.xml.PersistenceXmlDomImpl;
import com.bc.node.Node;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import com.bc.xml.PersistenceXmlDom;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 7:59:42 PM
 */
public class PersistenceMetaDataImpl implements PersistenceMetaData, Serializable {

    private static final Logger LOG = Logger.getLogger(PersistenceMetaDataImpl.class.getName());
    
    private final URI persistenceConfigURI;
    
    private final List<PersistenceUnitMetaData> unitMetaDataList;
    
    private final Node<String> node;
    
    public PersistenceMetaDataImpl(URI persistenceConfigUri) { 
    
        this.persistenceConfigURI = Objects.requireNonNull(persistenceConfigUri);
        
        final PersistenceXmlDomImpl pudom = new PersistenceXmlDomImpl(persistenceConfigUri);
        
        final List<String> puNames = pudom.getPersistenceUnitNames();
        
        final List<PersistenceUnitMetaData> puMetas = new ArrayList<>(puNames.size());
        
        for(String puName : puNames) {

            final List<String> puClsNames = pudom.getClassNames(puName);
            
            final Set<Class> puClasses = new LinkedHashSet<>(puClsNames.size());
            
            for(String puClsName:puClsNames) {
                
                try {
                    
                    puClasses.add(Class.forName(puClsName));
                    
                }catch(ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            
            puMetas.add(new PersistenceUnitMetaDataImpl(puName, puClasses));
        }
        
        this.unitMetaDataList = puMetas.size() == 1 ? 
                Collections.singletonList(puMetas.get(0)) :
                Collections.unmodifiableList(puMetas);
        
        this.node = Node.of(PersistenceNode.persistence.getTagName(), null, null);
        
        LOG.log(Level.FINE, "{0}", this.unitMetaDataList);
    }
    
    @Override
    public boolean isBuilt() {
        boolean built = true;
        for(PersistenceUnitMetaData puMeta : this.unitMetaDataList) {
            if(!puMeta.isBuilt()) {
                built = false;
                break;
            }
        }
        return built;
    }
    
    /**
     * Call this method after renaming any of:
     * <b>persistence-unit -> catalog -> schema -> table -> column</b>
     * @param emfProvider
     * @throws SQLException 
     */
    @Override
    public Node<String> build(Function<String, EntityManagerFactory> emfProvider) throws SQLException{
        for(PersistenceUnitMetaData puMeta : this.unitMetaDataList) {
            puMeta.build(node, emfProvider);
        }
        return this.node;
    }
    
    public Node<String> buildUnit(String persistenceUnitName, 
            Function<String, EntityManagerFactory> emfProvider) throws SQLException{
        final PersistenceUnitMetaData puMeta = this.getMetaData(persistenceUnitName);
        return puMeta.build(node, emfProvider);
    }
    
    @Override
    public PersistenceUnitMetaData getMetaData(String persistenceUnit) {
        for(PersistenceUnitMetaData puMetaData : this.unitMetaDataList) {
            if(puMetaData.getName().equals(persistenceUnit)) {
                return puMetaData;
            }
        }
        return null;
    }
    
    @Override
    public URI getURI() {
        return persistenceConfigURI;
    }
    
    @Override
    public Properties getProperties(String persistenceUnitName) throws IOException {
        final PersistenceXmlDom pudom = new PersistenceXmlDomImpl(persistenceConfigURI);
        return pudom.getProperties(persistenceUnitName);
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses(String... persistenceUnitNames) {
        final Map<String, Set<Class>> output = new LinkedHashMap<>();
        for(String puName : persistenceUnitNames) {
            final Collection<Class> puClasses = this.getMetaData(puName).getEntityClasses();
            output.put(puName, this.toSet(puClasses));
        }
        return Collections.unmodifiableMap(output);
    }
    
    /**
     * @param persistenceUnitName The persistence unit name to search.
     * @param entityType The entity type to find
     * @return <code>true</code> if the entity type is listed in the persistence configuration file, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityType(String persistenceUnitName, Class entityType) {
        return this.getMetaData(persistenceUnitName).isListedEntityType(entityType);
    }
    
    /**
     * @param persistenceUnitName The persistence unit name to search.
     * @param entityTypes The entity type to find
     * @return <code>true</code> if the entity types are listed in the persistence configuration file, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityTypes(String persistenceUnitName, List<Class> entityTypes) {
        return this.getMetaData(persistenceUnitName).isListedEntityTypes(entityTypes);
    }
    
    @Override
    public Set<String> getPersistenceUnitNames() {
        final Set<String> output = new LinkedHashSet(this.unitMetaDataList.size());
        for(PersistenceUnitMetaData metaData : this.unitMetaDataList) {
            output.add(metaData.getName());
        }
        return Collections.unmodifiableSet(output);
    }
    
    @Override
    public Set<Class> getEntityClasses(Set<String> persistenceUnitNames) {
    
        final Set<Class> entityClasses = new LinkedHashSet();
        
        for(String puName : persistenceUnitNames) {
            
            entityClasses.addAll(getMetaData(puName).getEntityClasses());
        }
        
        return Collections.unmodifiableSet(entityClasses);
    }
    
    @Override
    public Set<Class> getEntityClasses(String persistenceUnitName) {
        
        final Collection<Class> puClasses = getMetaData(persistenceUnitName).getEntityClasses();
        
        return Collections.unmodifiableSet(this.toSet(puClasses));
    }

    @Override
    public boolean isAnyListedTableExisting(String persistenceUnit) {
        return this.getMetaData(persistenceUnit).isAnyListedTableExisting();
    }
    
    @Override
    public boolean isAnyTableExisting(String persistenceUnit) {
        return this.getMetaData(persistenceUnit).isAnyTableExisting();
    }
    
    @Override
    public Node<String> getNode() {
        return this.node;
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses() {
        final Map<String, Set<Class>> output = new LinkedHashMap(this.unitMetaDataList.size(), 1.0f);
        for(PersistenceUnitMetaData metaData : this.unitMetaDataList) {
            output.put(metaData.getName(), this.toSet(metaData.getEntityClasses()));
        }
        return Collections.unmodifiableMap(output);
    }
    
    private Set<Class> toSet(Collection<Class> classes) {
        return classes instanceof Set ? (Set)classes : new LinkedHashSet(classes);
    }
}

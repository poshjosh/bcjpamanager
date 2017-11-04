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

import com.bc.jpa.dom.PersistenceDOM;
import com.bc.jpa.dom.PersistenceDOMImpl;
import com.bc.node.Node;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
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

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 27, 2017 7:59:42 PM
 */
public class PersistenceMetaDataImpl implements PersistenceMetaData, Serializable {

    private static final Logger logger = Logger.getLogger(PersistenceMetaDataImpl.class.getName());
    
    private final URI persistenceConfigURI;
    
    private final Map<String, PersistenceUnitMetaData> persistenceUnitMetaData;
    
    private final Node<String> node;
    
    public PersistenceMetaDataImpl(URI persistenceConfigUri) { 
    
        this.persistenceConfigURI = Objects.requireNonNull(persistenceConfigUri);
        
        final PersistenceDOMImpl pudom = new PersistenceDOMImpl(persistenceConfigUri);
        
        final List<String> puNames = pudom.getPersistenceUnitNames();
        
        final Map<String, PersistenceUnitMetaData> puMetas = new LinkedHashMap<>(puNames.size(), 1.0f);
        
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
            
            puMetas.put(puName, new PersistenceUnitMetaDataImpl(puName, puClasses));
        }
        
        this.persistenceUnitMetaData = Collections.unmodifiableMap(puMetas);
        
        this.node = Node.of(PersistenceNodeBuilder.NODE_NAME_PERSISTENCE, null, null);
        
        logger.log(Level.FINE, "{0}", this.persistenceUnitMetaData);
    }
    
    @Override
    public boolean isBuilt() {
        boolean built = true;
        final Collection<PersistenceUnitMetaData> puMetas = this.persistenceUnitMetaData.values();
        for(PersistenceUnitMetaData puMeta : puMetas) {
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
        final Collection<PersistenceUnitMetaData> puMetas = this.persistenceUnitMetaData.values();
        for(PersistenceUnitMetaData puMeta : puMetas) {
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
        return this.persistenceUnitMetaData.get(persistenceUnit);
    }
    
    @Override
    public URI getURI() {
        return persistenceConfigURI;
    }
    
    @Override
    public Properties getProperties(String persistenceUnitName) throws IOException {
        final PersistenceDOM pudom = new PersistenceDOMImpl(persistenceConfigURI);
        return pudom.getProperties(persistenceUnitName);
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses(String... persistenceUnitNames) {
        final Map<String, Set<Class>> output = new LinkedHashMap<>();
        for(String puName : persistenceUnitNames) {
            final Set<Class> puClasses = this.persistenceUnitMetaData.get(puName).getEntityClasses();
            output.put(puName, puClasses);
        }
        return Collections.unmodifiableMap(output);
    }
    
    /**
     * If persistence unit name is null then all persistence units will be 
     * searched for the specified entity type
     * @param persistenceUnitName The persistence unit name to search may be null
     * @param entityType The entity type to find
     * @return <code>true</code> if found, otherwise <code>false</code>
     */
    @Override
    public boolean isListedEntityType(String persistenceUnitName, Class entityType) {
        return this.getMetaData(persistenceUnitName).isListedEntityType(entityType);
    }
    
    @Override
    public Set<String> getPersistenceUnitNames() {
        return Collections.unmodifiableSet(this.persistenceUnitMetaData.keySet());
    }
    
    @Override
    public Set<Class> getEntityClasses(Set<String> persistenceUnitNames) {
    
        final Set<Class> entityClasses = new LinkedHashSet();
        
        for(String puName : persistenceUnitNames) {
            
            entityClasses.addAll(persistenceUnitMetaData.get(puName).getEntityClasses());
        }
        
        return Collections.unmodifiableSet(entityClasses);
    }
    
    @Override
    public Set<Class> getEntityClasses(String persistenceUnitName) {
        
        final Set<Class> puClasses = persistenceUnitMetaData.get(persistenceUnitName).getEntityClasses();
        
        return Collections.unmodifiableSet(puClasses);
    }

    @Override
    public boolean isAnyListedTableExisting(String persistenceUnit) {
        return this.getMetaData(persistenceUnit).isAnyListedTableExisting();
    }
    
    @Override
    public boolean isAnyTableExisting(String persistenceUnit) {
        return this.getMetaData(persistenceUnit).isAnyTableExisting();
    }
    
    public NodeSearch getNodeSearch() {
        return new NodeSearchImpl();
    }
    
    @Override
    public Node<String> getNode() {
        return this.node;
    }

    @Override
    public Map<String, Set<Class>> getPersistenceUnitClasses() {
        final Map<String, Set<Class>> output = new LinkedHashMap(this.persistenceUnitMetaData.size(), 1.0f);
        final Set<String> puNames = this.persistenceUnitMetaData.keySet();
        for(String puName : puNames) {
            output.put(puName, this.persistenceUnitMetaData.get(puName).getEntityClasses());
        }
        return Collections.unmodifiableMap(output);
    }
}

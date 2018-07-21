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

package com.bc.jpa;

import com.bc.jpa.classloaders.ContextClassLoaderAccessor;
import com.bc.jpa.functions.GetClassLoaderForPersistenceUri;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 2:39:30 PM
 */
public class EntityManagerFactoryCreatorImpl 
        implements EntityManagerFactoryCreator, Serializable {

    private transient static final Logger LOG = Logger.getLogger(
            EntityManagerFactoryCreatorImpl.class.getName());
    
    private final ClassLoader classLoader;
    
    private final Function<String, Properties> propertiesProvider;
    
    public EntityManagerFactoryCreatorImpl() {
        this(new ContextClassLoaderAccessor().get(), (persistenceUnit) -> new Properties());
    }

    public EntityManagerFactoryCreatorImpl(
            URI uri, 
            Function<String, Properties> propertiesProvider) {
        this(
                new GetClassLoaderForPersistenceUri().apply(uri.toString()), 
                propertiesProvider);
    }
    
    public EntityManagerFactoryCreatorImpl(
            ClassLoader classLoader, 
            Function<String, Properties> propertiesProvider) {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.propertiesProvider = Objects.requireNonNull(propertiesProvider);
    }

    @Override
    public EntityManagerFactory newInstance(String persistenceUnit) {
        
        final Properties properties = this.propertiesProvider.apply(persistenceUnit);
        
        LOG.info(() -> "--------------------------------------------------\nCreating EntityManagerFactory for persistence unit: "
                +persistenceUnit+"\nProperties: "+properties.stringPropertyNames());

        final ContextClassLoaderAccessor accessor = new ContextClassLoaderAccessor();

        final ClassLoader contextClassLoader = accessor.get();
        
        final boolean set;
        if(set = !classLoader.equals(contextClassLoader)) {
            accessor.set(classLoader);
        }
        
        try{
            
            if(properties != null && !properties.isEmpty()) {

                return Persistence.createEntityManagerFactory(
                        persistenceUnit, properties);
            }else{

                return Persistence.createEntityManagerFactory(
                        persistenceUnit);
            }
        }finally{

            if(set) {
                accessor.set(contextClassLoader);
            }
        }
    }
}


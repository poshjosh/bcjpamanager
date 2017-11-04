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

import com.bc.jpa.classloaders.AlternativePersistenceClassLoader;
import com.bc.jpa.classloaders.AlternativePersistenceURLClassLoader;
import com.bc.jpa.classloaders.ContextClassLoaderAccessor;
import com.bc.util.XLogger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 22, 2017 2:03:11 AM
 * @deprecated 
 */
@Deprecated
public class EntityManagerFactoryProviderDeprecated implements Function<String, EntityManagerFactory> {
    
    private final URI persistenceURI;
    
    private final Properties properties;
    
    public EntityManagerFactoryProviderDeprecated(URI persistenceURI, Properties properties) {
        this.persistenceURI = Objects.requireNonNull(persistenceURI);
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public EntityManagerFactory apply(String persistenceUnit) {
        
XLogger.getInstance().log(Level.INFO, 
    "======================== Creating EntityManagerFactory =========================\n"+
    "PersistenceUnit: {0}, URI: {1}",
    this.getClass(), persistenceUnit, this.persistenceURI);

XLogger.getInstance().log(Level.FINE, "Properties: {0}", this.getClass(), this.properties);

        final ContextClassLoaderAccessor accessor = new ContextClassLoaderAccessor();

        final ClassLoader originalClassLoader = accessor.get();

        ClassLoader alternativeClassLoader = null;

        try{

            if(persistenceURI != null && 
                    !persistenceURI.toString().replace('\\', '/').equals("META-INF/persistence.xml")) {
                
                alternativeClassLoader = this.getAlternativeClassLoader(originalClassLoader);
                
                accessor.set(alternativeClassLoader);
            }

            if(this.properties != null && !this.properties.isEmpty()) {

                return Persistence.createEntityManagerFactory(
                        persistenceUnit, this.properties);
            }else{

                return Persistence.createEntityManagerFactory(
                        persistenceUnit);
            }
        }catch(MalformedURLException | URISyntaxException e) {

            throw new RuntimeException("Exception compiling URL from URI: " + persistenceURI, e);

        }finally{

            if(alternativeClassLoader != null) {

                accessor.set(originalClassLoader);
            }
        }
    }
    
    protected ClassLoader getAlternativeClassLoader(ClassLoader toReplace) 
            throws MalformedURLException, URISyntaxException {
        
XLogger logger = XLogger.getInstance();
Level level = Level.FINE;
Class cls = this.getClass();
        
logger.log(level, "ClassLoader to replace. Type: {0}, instance: {1}", cls, toReplace.getClass().getName(), toReplace);

        final URL url = this.persistenceURI.toURL();

        ClassLoader replacement;
        if(toReplace instanceof URLClassLoader) {
            replacement = new AlternativePersistenceURLClassLoader(
                    url, (URLClassLoader)toReplace);
        }else{
            replacement = new AlternativePersistenceClassLoader(
                    url, (URLClassLoader)toReplace);
        }
        
logger.log(level, "Using classloader: {0} to load persistence '.xml' file", cls, replacement.getClass().getName());

        return replacement;
    }
}

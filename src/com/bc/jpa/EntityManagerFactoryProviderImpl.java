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
import com.bc.util.XLogger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 22, 2017 2:03:11 AM
 */
public class EntityManagerFactoryProviderImpl implements EntityManagerFactoryProvider {
    
    private final URI persistenceURI;
    
    private final Properties properties;
    
    public EntityManagerFactoryProviderImpl(URI persistenceURI, Properties properties) {
        this.persistenceURI = Objects.requireNonNull(persistenceURI);
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public EntityManagerFactory newInstance(String persistenceUnit) {
        
XLogger.getInstance().log(Level.INFO, 
    "======================== Creating EntityManagerFactory =========================\n"+
    "PersistenceUnit: {0}, URI: {1}",
    this.getClass(), persistenceUnit, this.persistenceURI);

XLogger.getInstance().log(Level.FINE, "Properties: {0}", this.getClass(), this.properties);

        final ClassLoader originalClassLoader = this.getContextClassLoader();

        ClassLoader alternativeClassLoader = null;

        try{

            if(persistenceURI != null && !persistenceURI.equals("META-INF/persistence.xml")) {
                
                alternativeClassLoader = this.getAlternativeClassLoader(originalClassLoader);
                
                this.setContextClassLoader(alternativeClassLoader);
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

                this.setContextClassLoader(originalClassLoader);
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
    
    /**
     * Wraps <code>Thread.currentThread().setContextClassLoader(ClassLoader)</code> 
     * into a doPrivileged block if security manager is present
     */
    private void setContextClassLoader(final ClassLoader classLoader) {
        if (System.getSecurityManager() == null) {
            Thread.currentThread().setContextClassLoader(classLoader);
        }else {
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        @Override
                        public java.lang.Object run() {
                            Thread.currentThread().setContextClassLoader(classLoader);
                            return classLoader;
                        }
                    }
            );
        }
    }

    /**
     * Wraps <code>Thread.currentThread().getContextClassLoader()</code> 
     * into a doPrivileged block if security manager is present
     */
    private ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }else {
            return  (ClassLoader) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                        @Override
                        public java.lang.Object run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    }
            );
        }
    }
}

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

package com.bc.jpa.classloaders;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 25, 2016 9:33:14 PM
 */
public class AlternativePersistenceURLClassLoader extends java.net.URLClassLoader {

    private transient static final Logger LOG = Logger.getLogger(
            AlternativePersistenceURLClassLoader.class.getName());
    
    private final ResourceContext resourceContext;
    
    public AlternativePersistenceURLClassLoader(URI uri, URLClassLoader parent) throws MalformedURLException {
        this(uri.toURL(), parent); 
    }
    
    public AlternativePersistenceURLClassLoader(URL url, URLClassLoader parent) {
        super(parent.getURLs(), parent);
        this.resourceContext = new AlternativeResourceContext(Collections.singletonMap("META-INF/persistence.xml", url));
    }
    
    @Override
    public URL getResource(String name) {

        LOG.finest(() -> "Resource name: " + name);

        URL output = resourceContext.getResource(name);
        if (output == null) {
            output = super.getResource(name);
        }

        LOG.log(Level.FINEST, "Output: {0}", output);

        return output;
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {

        LOG.finest(() -> "Resource name: " + name);

        Enumeration<URL> output = resourceContext.getResources(name);
        if (output == null) {
            output = super.getResources(name);
        }
        
        final boolean loggingCausesStackOverflow = true;
        if(!loggingCausesStackOverflow && LOG.isLoggable(Level.FINEST)) {  
            List outputList = Collections.list(output);
            LOG.log(Level.FINEST, "Output: {0}", outputList); 
            output = Collections.enumeration(outputList);
        }
        return output;
    }
}

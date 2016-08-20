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

import com.bc.util.XLogger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 25, 2016 9:33:14 PM
 */
public class AlternativePersistenceURLClassLoader extends java.net.URLClassLoader {

    private final ResourceContext rc;
    
    public AlternativePersistenceURLClassLoader(URI uri, URLClassLoader parent) throws MalformedURLException {
        this(uri.toURL(), parent); 
    }
    
    public AlternativePersistenceURLClassLoader(URL url, URLClassLoader parent) {
        super(parent.getURLs(), parent);
        this.rc = new AlternativeResourceContext(Collections.singletonMap("META-INF/persistence.xml", url));
    }
    
    @Override
    public URL getResource(String name) {
        URL output = rc.getResource(name);
        final Level level;
        if (output != null) {
            level = Level.FINE;
        }else{
            level = Level.FINER;
            output = super.getResource(name);
        }
XLogger.getInstance().log(level, "Input: {0}, output: {1}", this.getClass(), name, output);        
        return output;
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> output = rc.getResources(name);
        final Level level;
        if (output != null) {
            level = Level.FINE;
        }else{
            level = Level.FINER;
            output = super.getResources(name);
        }
if(XLogger.getInstance().isLoggable(level, this.getClass())) {  
    List outputList = toList(output);
    XLogger.getInstance().log(level, "Input: {0}, output: {1}", this.getClass(), name, outputList); 
    output = Collections.enumeration(outputList);
}
        return output;
    }
    
    private List<URL> toList(Enumeration<URL> en) {
        final List<URL> output;
        if(en != null) {
            output = new LinkedList();
            while(en.hasMoreElements()) {
                URL u = en.nextElement();
                output.add(u);
            }
        }else{
            output = Collections.EMPTY_LIST;
        }
        return output;
    }
}

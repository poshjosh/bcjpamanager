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

package com.bc.jpa.functions;

import com.bc.jpa.classloaders.AlternativePersistenceClassLoader;
import com.bc.jpa.classloaders.AlternativePersistenceURLClassLoader;
import com.bc.jpa.classloaders.ContextClassLoaderAccessor;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 3:15:42 PM
 */
public class GetClassLoaderForPersistenceUri 
        implements Function<String, ClassLoader>, Serializable {

    private transient static final Logger logger = Logger.getLogger(GetClassLoaderForPersistenceUri.class.getName());

    @Override
    public ClassLoader apply(String uri) {
        
        final ClassLoader output;
        
        final ContextClassLoaderAccessor accessor = new ContextClassLoaderAccessor();
        
        final ClassLoader contextClassLoader = accessor.get();
        
        if(uri != null && !uri.replace('\\', '/').equals("META-INF/persistence.xml")) {

            try{
                output = this.getAlternativeClassLoader(uri, contextClassLoader);
            }catch(MalformedURLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

        }else{

            output = contextClassLoader;
        }
        
        logger.info(() -> "\tURI: " + uri + "\n\tClassLoader: " + output);

        return output;
    }

    public ClassLoader getAlternativeClassLoader(String uri, ClassLoader toReplace) 
            throws MalformedURLException, URISyntaxException {
        
        logger.fine(() -> "ClassLoader to replace. Type: " + 
                toReplace.getClass().getName() +
                ", instance: " + toReplace);
        
        

        URL url;
        try{
            url = URI.create(uri).toURL();
        }catch(Exception e) {
            url = Paths.get(uri).toUri().toURL();
        }

        ClassLoader replacement;
        if(toReplace instanceof URLClassLoader) {
            replacement = new AlternativePersistenceURLClassLoader(
                    url, (URLClassLoader)toReplace);
        }else{
            replacement = new AlternativePersistenceClassLoader(
                    url, (URLClassLoader)toReplace);
        }
        
        logger.fine(() -> "Using classloader: "+replacement.getClass().getName()+
                " to load persistence '.xml' file");

        return replacement;
    }
}

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

package com.bc.jpa.functions;

import com.bc.jpa.classloaders.ContextClassLoaderAccessor;
import com.bc.jpa.metadata.PersistenceMetaData;
import com.bc.jpa.metadata.PersistenceMetaDataImpl;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on May 1, 2018 1:03:46 AM
 */
public class GetPersistenceUriForEntityClasses implements Serializable, Function<List<Class>, URI>{

    private transient static final Logger LOG = Logger.getLogger(GetPersistenceUriForEntityClasses.class.getName());
    
    private final String resourceName;
    
    private final ClassLoader classLoader;

    public GetPersistenceUriForEntityClasses() {
        this(new ContextClassLoaderAccessor().get(), "META-INF/persistence.xml");
    }
    
    public GetPersistenceUriForEntityClasses(ClassLoader classLoader, String resourceName) {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.resourceName = Objects.requireNonNull(resourceName);
        LOG.fine(() -> "Persistence config resource name: " + resourceName);
    }
    
    @Override
    public final URI apply(List<Class> entityTypes) {
        return this.getOrException(entityTypes);
    }
    
    public final URI getOrException(List<Class> entityTypes) {
        final URI output = this.getOrDefault(entityTypes, null);
        return Objects.requireNonNull(output);
    }

    public final URI getOrDefault(List<Class> entityTypes, URI outputIfNone) {
        
        URI output = null;
        
        final List<URI> uriList = this.getUriList();
        
        for(URI uri : uriList) {
        
            final PersistenceMetaData metaData = new PersistenceMetaDataImpl(uri);
            
            if(this.containsClasses(metaData, entityTypes)) {
                
                output = uri;
                
                LOG.fine(() -> "Selected URI: " + uri + " for classes: " + entityTypes);
                
                break;

            }else{
                
                LOG.fine(() -> "Rejected URI: " + uri + " for classes: " + entityTypes);
            }
        }
        
        return output == null ? outputIfNone : output;
    }
    
    public boolean containsClasses(PersistenceMetaData metaData, List<Class> entityClasses) {
        
        boolean output = false;
        
        final Set<String> puNames = metaData.getPersistenceUnitNames();

        for(String puName : puNames) {
            
            final Set<Class> puClasses = metaData.getEntityClasses(puName);

            if(puClasses.containsAll(entityClasses)) {

                output = true;
                
                break;
            }
        }
        
        return output;
    }
    
    public List<URI> getUriList() {

        final List<URL> urlList;
        try{
            urlList = Collections.list(this.classLoader.getResources(this.resourceName));
        }catch(IOException e) {
            throw new RuntimeException(e);
        }

        LOG.fine(() -> "Persistence config URIs: " + urlList);

        final List<URI> output;

        if(urlList.isEmpty()) {

            output = Collections.EMPTY_LIST;

        }else{

            final List<URI> temp = new ArrayList(urlList.size());
            for(URL url : urlList) {
                try{
                    temp.add(url.toURI());
                }catch(URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            output = Collections.unmodifiableList(temp);
        }
        
        return output;
    }
}

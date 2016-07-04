package com.bc.jpa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author Josh
 */
public class AlternativePersistenceClassLoader extends ClassLoader {

    private final URL url;
    
    public AlternativePersistenceClassLoader(URI uri) throws MalformedURLException {
       this.url = uri.toURL(); 
    }
    
    public AlternativePersistenceClassLoader(URL url) {
       this.url = url;
    }
    
    @Override
    public URL getResource(String name) {
        if (name.endsWith("META-INF/persistence.xml")) {
            return this.url;
        }
        return super.getResource(name);
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (name.endsWith("META-INF/persistence.xml")) {
            return Collections.enumeration(Arrays.asList(this.url));
        }
        return super.getResources(name);
    }
}

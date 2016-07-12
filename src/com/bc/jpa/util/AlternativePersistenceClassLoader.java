package com.bc.jpa.util;

import com.bc.util.XLogger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;

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
        if (name.equals("META-INF/persistence.xml")) {
XLogger.getInstance().log(Level.INFO, "For resource {0} returning {1}", this.getClass(), name, this.url);
            return this.url;
        }
        return super.getResource(name);
    }
    
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (name.equals("META-INF/persistence.xml")) {
XLogger.getInstance().log(Level.INFO, "For resource {0} returning {1}", this.getClass(), name, this.url);
            return Collections.enumeration(Arrays.asList(this.url));
        }
        return super.getResources(name);
    }
}

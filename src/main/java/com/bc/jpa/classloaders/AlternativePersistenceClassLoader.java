package com.bc.jpa.classloaders;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Josh
 */
public class AlternativePersistenceClassLoader extends ClassLoader {

    private transient static final Logger LOG = Logger.getLogger(AlternativePersistenceClassLoader.class.getName());

    private final ResourceContext resourceContext;
    
    public AlternativePersistenceClassLoader(URI uri, ClassLoader parent) throws MalformedURLException {
        this(uri.toURL(), parent); 
    }
    
    public AlternativePersistenceClassLoader(URL url, ClassLoader parent) {
        super(parent);
        this.resourceContext = new AlternativeResourceContext(Collections.singletonMap("META-INF/persistence.xml", url));
    }
    
    @Override
    public URL getResource(String name) {

        LOG.finest(() -> "Resource name: " + name);

        URL output = resourceContext.getResource(name);
        if (output == null) {
            output = super.getResource(name);
        }

        LOG.log(Level.FINEST, "URL: {0}", output);

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
            LOG.log(Level.FINEST, "Enumeration<URL>: {0}", outputList); 
            output = Collections.enumeration(outputList);
        }
        return output;
    }
}

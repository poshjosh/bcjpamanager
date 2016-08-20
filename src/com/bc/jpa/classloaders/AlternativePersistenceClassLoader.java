package com.bc.jpa.classloaders;

import com.bc.util.XLogger;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Josh
 */
public class AlternativePersistenceClassLoader extends ClassLoader {

    private final ResourceContext rc;
    
    public AlternativePersistenceClassLoader(URI uri, ClassLoader parent) throws MalformedURLException {
        this(uri.toURL(), parent); 
    }
    
    public AlternativePersistenceClassLoader(URL url, ClassLoader parent) {
        super(parent);
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

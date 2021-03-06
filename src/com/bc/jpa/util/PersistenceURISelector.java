package com.bc.jpa.util;

import com.bc.util.XLogger;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)PersistenceLoader.java   24-Oct-2014 13:39:02
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class PersistenceURISelector {
    
    private final Set<ClassLoader> classLoaders;
    
    public static interface URIFilter{
        boolean accept(URI uri);
    }

    public PersistenceURISelector() { 
        this(Thread.currentThread().getContextClassLoader(), PersistenceURISelector.class.getClassLoader());
    }
    
    public PersistenceURISelector(ClassLoader... loaders) {
        this.classLoaders = new HashSet(Arrays.asList(loaders));
    }
    
    public URI selectURI(String fname, URIFilter filter) throws IOException { 
     
        URI uri = null;
        
        for(ClassLoader classLoader:classLoaders) {
            
            uri = this.selectURI(classLoader, fname, filter);
            
            if(uri != null) {
                break;
            }
        }
        
        return uri;
    }
    
    public URI selectURI(ClassLoader classLoader, String fname, URIFilter filter) throws IOException { 
        
        URI uri = null;
        
XLogger.getInstance().log(Level.FINE, "Selecting persistence files for name: {0}", this.getClass(), fname);                
        
        Enumeration<URL> en = classLoader.getResources(fname);
        
        while(en.hasMoreElements()) {
            
            URL url = en.nextElement();

            try{
                uri = url.toURI();
            }catch(java.net.URISyntaxException shouldNotHappen) { 
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, shouldNotHappen);
                continue;
            }
            
            if(filter == null || filter.accept(uri)) {
                break;
            }else{
                uri = null;
            }
        }

        if(uri == null) {
            File file = new File(fname);
            if(file.exists()) {
                uri = file.toURI();
XLogger.getInstance().log(Level.INFO, "Created URI: {0}, from filename: {1}", 
        this.getClass(), uri, fname);
            }else{
XLogger.getInstance().log(Level.WARNING, "Does not exist file: {0}", this.getClass(), fname);
            }
        }
        
XLogger.getInstance().log(Level.INFO, "Selected persistence file: {0}", this.getClass(), uri);                
        return uri;
    }
    
    public final Set<ClassLoader> getClassLoader() {
        return new HashSet(this.classLoaders);
    }
}

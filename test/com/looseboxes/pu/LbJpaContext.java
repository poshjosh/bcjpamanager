package com.looseboxes.pu;

import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.PersistenceLoader;
import com.bc.jpa.PersistenceMetaData;
import com.bc.sql.MySQLDateTimePatterns;
import com.bc.sql.SQLDateTimePatterns;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @(#)IdiscControllerFactory.java   22-Aug-2014 14:14:23
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
public class LbJpaContext extends JpaContextImpl {

    private static class PersistenceUriFilter implements PersistenceLoader.URIFilter {
        @Override
        public boolean accept(URI uri) {
            return uri.toString().contains("looseboxes");
        }
    }

    public LbJpaContext() throws IOException {
        this(new MySQLDateTimePatterns());
    }

    public LbJpaContext(SQLDateTimePatterns dateTimePatterns) throws IOException {
        this("META-INF/persistence.xml", dateTimePatterns);
    }
    
    public LbJpaContext(String persistenceFile, SQLDateTimePatterns dateTimePatterns) throws IOException {
        super(persistenceFile, new PersistenceUriFilter(), dateTimePatterns, References.ENUM_TYPES);
    }
    
//    public LbJpaContext(String persistenceFile, PersistenceLoader.URIFilter uriFilter, SQLDateTimePatterns dateTimePatterns, ParametersFormatter paramFmt) throws IOException {
//        super(persistenceFile, uriFilter, dateTimePatterns, paramFmt, References.ENUM_TYPES);
//    }

    public LbJpaContext(URI persistenceFile) throws IOException {
        super(persistenceFile, References.ENUM_TYPES);
    }

    public LbJpaContext(URI persistenceFile, SQLDateTimePatterns dateTimePatterns) throws IOException {
        super(persistenceFile, dateTimePatterns, References.ENUM_TYPES);
    }

    public LbJpaContext(File persistenceFile, SQLDateTimePatterns dateTimePatterns) throws IOException {
        super(persistenceFile, dateTimePatterns, References.ENUM_TYPES);
    }

    public LbJpaContext(PersistenceMetaData metaData, SQLDateTimePatterns dateTimePatterns) {
        super(metaData, dateTimePatterns, References.ENUM_TYPES);
    }
}

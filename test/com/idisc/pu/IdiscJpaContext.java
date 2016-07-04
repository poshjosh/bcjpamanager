package com.idisc.pu;

import com.bc.jpa.JpaContextImpl;
import com.bc.jpa.PersistenceLoader;
import com.bc.jpa.PersistenceMetaData;
import com.bc.sql.MySQLDateTimePatterns;
import com.bc.sql.SQLDateTimePatterns;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class IdiscJpaContext extends JpaContextImpl{
    
  private static class PersistenceUriFilter implements PersistenceLoader.URIFilter {
    @Override
    public boolean accept(URI uri) {
        return uri.toString().contains("idisc");
    }
  }

  public IdiscJpaContext() throws IOException {
    this(new MySQLDateTimePatterns());
  }
  
  public IdiscJpaContext(SQLDateTimePatterns dateTimePatterns) throws IOException {
    this("META-INF/persistence.xml", dateTimePatterns);
  }

  public IdiscJpaContext(String persistenceFile, SQLDateTimePatterns dateTimePatterns) throws IOException {
    super(persistenceFile, new PersistenceUriFilter(), dateTimePatterns, References.ENUM_TYPES);
  }
    
//    public IdiscJpaContext(String persistenceFile, PersistenceLoader.URIFilter uriFilter, SQLDateTimePatterns dateTimePatterns, ParametersFormatter paramFmt) throws IOException {
//        super(persistenceFile, uriFilter, dateTimePatterns, paramFmt, References.ENUM_TYPES);
//    }

  public IdiscJpaContext(URI persistenceFile) throws IOException {
    super(persistenceFile, References.ENUM_TYPES);
  }

  public IdiscJpaContext(URI persistenceFile, SQLDateTimePatterns dateTimePatterns) throws IOException {
    super(persistenceFile, dateTimePatterns, References.ENUM_TYPES);
  }

  public IdiscJpaContext(File persistenceFile, SQLDateTimePatterns dateTimePatterns) throws IOException {
    super(persistenceFile, dateTimePatterns, References.ENUM_TYPES);
  }

  public IdiscJpaContext(PersistenceMetaData metaData, SQLDateTimePatterns dateTimePatterns) {
    super(metaData, dateTimePatterns, References.ENUM_TYPES);
  }
}

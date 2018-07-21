package com.idisc.pu;

import com.bc.jpa.controller.EntityController;
import com.bc.jpa.context.JpaContext;
import java.util.logging.Logger;
import com.idisc.pu.entities.Installation;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 13, 2016 9:47:40 AM
 */
public class Installations {
    private transient static final Logger LOG = Logger.getLogger(Installations.class.getName());
    
  private static final AtomicInteger COUNT = new AtomicInteger();
  
  private static final long TIMEOFFSET_MILLIS;
  static {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    cal.set(2016, 0, 22);
    TIMEOFFSET_MILLIS = cal.getTimeInMillis();
  }
  
  private final JpaContext jpaContext;
  
  public Installations(JpaContext jpaContext) {
      this.jpaContext = jpaContext;
  }
  
  public Installation from(
          User user, String installationkey, String screenname, 
          long firstinstallationtime, long lastinstallationtime, boolean createIfNone) {
      
    final Level level = Level.FINER;

    LOG.log(level, "User: {0}", user);

    final EntityController<Installation, Integer> ec = jpaContext.getEntityController(Installation.class, Integer.class);
        
    if (user != null) {
        
      List<Installation> list = user.getInstallationList();
      
      LOG.log(level, "User has {0} installations", (list==null?null:list.size()));

      if(list == null) {
          
        list = ec.select("feeduserid", user.getFeeduserid(), 0, -1);
        
        LOG.log(level, "For user, selected {0} installations", (list==null?null:list.size()));

        user.setInstallationList(list);
        
      }if (list != null && !list.isEmpty()) {
          
        return (Installation)list.get(list.size() - 1);
      }
    }
    
    String installationkeyCol = "installationkey";
    Objects.requireNonNull(installationkey);
    
    Installation output;
    try {
        
      output = getEntity(ec, installationkeyCol, installationkey);
       
      LOG.log(level, "{0} = {1}, found: {2}", new Object[]{installationkeyCol, installationkey, output});
      if ((output == null) && (createIfNone)) {
          
        if (screenname != null && !screenname.isEmpty()) {
            
          if (this.isExistingScreenname(ec, screenname)) {
              
            screenname = generateUniqueId();
          }
        }else{
            
          screenname = generateUniqueId();
        }
        
        output = new Installation();
        
        output.setInstallationkey(installationkey);
        
        output.setScreenname(screenname);
        
        if(firstinstallationtime < TIMEOFFSET_MILLIS) { throw new IllegalArgumentException(); }
        output.setFirstinstallationdate(new Date(firstinstallationtime));
        
        if(lastinstallationtime < TIMEOFFSET_MILLIS) { throw new IllegalArgumentException(); }
        output.setLastinstallationdate(new Date(lastinstallationtime));
        
        output.setDatecreated(new Date());
        
        output.setFeeduserid(user == null ? null : user.getDelegate());
        
        try {
            
          ec.persist(output); 
          
          LOG.log(level, "For: {0} = {1}, created: ", new Object[]{installationkeyCol, installationkey, output});
          
        } catch (Exception e) {
          output = null;  
          LOG.log(Level.WARNING, "Error creating database record for installation with installationkey: " + installationkey, e);
        }
      }
    } catch (RuntimeException e) {
      output = null;  
      LOG.log(Level.WARNING, "Error accessing database installation record for installationkey: " + installationkey, e);
    }
    
    return output;
  }
  
  private String generateUniqueId() {
    long n = System.currentTimeMillis() - TIMEOFFSET_MILLIS;
    return "user_" + Long.toHexString(n) + "_" + COUNT.getAndIncrement();
  }
  
  public boolean isExistingScreenname(EntityController<Installation, Integer> ec, String screenname) {
      
      Installation screennameOwner = getEntity(ec, "screenname", screenname);

      return screennameOwner != null;
  }
  
  public <E> E getEntity(EntityController<E, Integer> ec, String columnName, String columnValue) {
      
    Objects.requireNonNull(columnName, "Attempted to select a 'null' column name from the database");
    
    if ((columnValue == null) || (columnValue.isEmpty())) {
      throw new NullPointerException("Required parameter "+columnName+" is missing");
    }
    
    E output;
    try {
      List<E> found = ec.select(columnName, columnValue, -1, -1);
      final int resultsCount = found == null ? 0 : found.size();
      if (resultsCount < 1) {
        output = null; 
      } else { 
        if (resultsCount == 1) {
          output = found.get(0);
        } else {
          throw new UnsupportedOperationException("Found > 1 record where only 1 was expected for "+columnName+" = " + columnValue);
        }  
      }
    } catch (RuntimeException e) {
      throw new RuntimeException("Error accessing database installation record for "+columnName+" = " + columnValue, e);
    }
    return output;
  }
}

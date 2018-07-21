package com.looseboxes.pu;

import com.bc.io.CharFileIO;
import com.bc.io.IOWrapper;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


/**
 * @(#)Installer.java   10-Apr-2015 17:40:15
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
public class PuInstaller {
    private transient static final Logger LOG = Logger.getLogger(PuInstaller.class.getName());

    public boolean isInstalled() {
        File file = this.getFile("META-INF/persistence.xml");
        return file.exists();
    }
    
    public boolean uninstall() throws ClassNotFoundException, SQLException {

        // Was serialized
        //
        String database = this.getDatabaseName();
        String user = this.getUserName();
        String pass = this.getPassword();

        boolean success = this.uninstall(database, user, pass);
        
        if(success) {
            // Deletes serialized values
            //
            this.setDatabaseName(null);
            this.setUserName(null);
            this.setPassword(null);
        }
        
        return success;
    }

    public boolean uninstall(String database, String user, String pass) throws ClassNotFoundException, SQLException {

if(LOG.isLoggable(Level.INFO)){
LOG.log(Level.INFO, "Dropping database if exists: {0}", database);
}

        try{
            
            this.execute("drop database if exists `"+database+"`", 
                    database, user, pass);
            
        }finally{

            File file = this.getFile("META-INF/persistence.xml");

            if(file.exists()) {
if(LOG.isLoggable(Level.INFO)){
LOG.log(Level.INFO, "Deleting file: {0}", file);
}
                if(!file.delete()) {
                    file.deleteOnExit();
                    return false;
                }else{
                    return true;
                }
            }else{
                return true;
            }
        }
    }
    
    /**
     * The use of unique tableNames is advised to avoid conflicts in
     * a shared database. 
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public boolean install(
            String databaseName, String userName, String password) 
            throws IOException, ClassNotFoundException, SQLException {
     
        if(this.isInstalled()) {
            throw new UnsupportedOperationException();
        }
        
        // These are automatically serialized
        // They are used for uninstallation
        //
        this.setDatabaseName(databaseName);
        this.setUserName(userName);
        this.setPassword(password);
        
        String sql = getSql("META-INF/templates/dbinstall_0_sql.template", databaseName);
        String [] sqls = sql.split(";");
        this.execute(false, sqls, databaseName, userName, password);
        sql = getSql("META-INF/templates/dbinstall_1_sql.template", databaseName);
        sqls = sql.split(";");
        this.execute(false, sqls, databaseName, userName, password);
        sql = getSql("META-INF/templates/dbinstall_2_sql.template", databaseName);
        sqls = sql.split(";");
        this.execute(false, sqls, databaseName, userName, password);
        
        return this.updatePersistenceConfig(databaseName, userName, password);
    }   
    
    private boolean updatePersistenceConfig(
            String databaseName, String userName, String password) 
            throws IOException {
        
        File file = this.getFile("META-INF/templates/persistence_xml.template");
        
        CharFileIO io = new CharFileIO();
        
        CharSequence contents = io.readChars(file);
        
        String sval = contents.toString();
        
        String url = this.getDatabaseURL(databaseName);
        
        sval = sval.replace("${url}", url);
        
        sval = sval.replace("${driver}", this.getDriverName());
        
        sval = sval.replace("${user}", userName);
        
        sval = sval.replace("${password}", password);
        
        File persistence = new File(this.getFile("META-INF"), "persistence.xml");

if(LOG.isLoggable(Level.INFO)){
LOG.log(Level.INFO, "Creating file: {0}", persistence);
}
        
        io.write(false, sval, persistence);
        
        return file.exists();
    }
        
    private String getSql(String templateFilename, String databaseName) throws IOException {
        
        File file = this.getFile(templateFilename);
        
        CharFileIO io = new CharFileIO();
        
        CharSequence contents = io.readChars(file);
        
        return contents.toString().replace("${database}", databaseName);
    }

    private void execute(
            String sql,
            String databaseName, 
            String userName, String password) 
            throws ClassNotFoundException, SQLException {
        
        Connection conn = null;
        
        Statement stmt = null;
        
        try{

            conn = this.newConnection(databaseName, userName, password);
        
            stmt = conn.createStatement();

            stmt.execute(sql);
            
        }finally{
            
            this.close(stmt);
            
            this.close(conn);
        }
    }

    private void execute(
            boolean strict,
            String [] sqls,
            String databaseName, 
            String userName, String password) 
            throws ClassNotFoundException, SQLException {
        
        boolean oldAutoCommit = true;
        
        Connection conn = null;
        
        Statement stmt = null;
        
        try{

            conn = this.newConnection(databaseName, userName, password);
        
            oldAutoCommit = conn.getAutoCommit();
            
            conn.setAutoCommit(true);
        
            stmt = conn.createStatement();

            SQLException first = null;
            for(String sql:sqls) {
                try{
                    stmt.execute(sql);
                }catch(SQLException e) {
                    first = e;
                }
            }
            
            if(strict && first != null) {
                throw first;
            }
        }finally{
            
            if(conn != null) {
                conn.setAutoCommit(oldAutoCommit);
            }
            
            this.close(stmt);
            
            this.close(conn);
        }
    }
    
    protected File getFile(String fname) {
        
        try{
            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            URL url = loader.getResource(fname);

            return Paths.get(url.toURI()).toFile();
            
        }catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getDriverName() {
        return "com.mysql.jdbc.Driver";
    }
    
    public String getDatabaseURL(String databaseName) {
        return "jdbc:mysql://localhost:3306/"+databaseName+"?zeroDateTimeBehavior=convertToNull";
    }
    
    /**
     * Create a new connection using driver <tt>com.mysql.jdbc.Driver</tt>
     * and databaseURL <tt>jdbc:mysql://localhost:3306/[databaseName]?zeroDateTimeBehavior=convertToNull</tt>
     * where [databaseName] is the name of the database
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private Connection newConnection(
            String databaseName, String userName, String password) 
            throws ClassNotFoundException, SQLException {
    
        return this.newConnection(
                this.getDriverName(), 
                this.getDatabaseURL(databaseName), 
                userName, 
                password);
        
    }
    
    private Connection newConnection(
            String driverName, String databaseURL, 
            String userName, String password) 
            throws ClassNotFoundException, SQLException {

        Class.forName(driverName);
        
        Connection con = DriverManager.getConnection(databaseURL, userName, password);

if(LOG.isLoggable(Level.FINE)){
LOG.log(Level.FINE, "New connection:: {0}", con);
}
        return con;
    }
    
    private void close(Connection con) {
        try{
            if (con == null || con.isClosed()) return; 
        }catch(SQLException e) {
            if(LOG.isLoggable(Level.WARNING)){
                  LOG.log(Level.WARNING, "Error checking if connection is closed", e);
            }
        }
        
        try {
            if (!con.getAutoCommit()) con.setAutoCommit(true);
        }catch (SQLException e) {
            if(LOG.isLoggable(Level.WARNING)){
                  LOG.log(Level.WARNING, "Error re-setting connection property (auto-commit) to 'true'", e);
            }
        }finally{

            try {

                con.close();

if(LOG.isLoggable(Level.FINER)){
LOG.log(Level.FINER, "Closed connection:: {0}", con);
}
            }catch (SQLException e) {
                if(LOG.isLoggable(Level.WARNING)){
                        LOG.log(Level.WARNING, "", e);
                }
            }
        }
    }

    private void close(Statement stmt) {
        try{
            if (stmt == null || stmt.isClosed()) return; 
        }catch(SQLException e) {
            if(LOG.isLoggable(Level.WARNING)){
                  LOG.log(Level.WARNING, "Error checking if statement is closed", e);
            }
        }
        try {
            stmt.close();
        }catch (SQLException e) {
            if(LOG.isLoggable(Level.WARNING)){
                  LOG.log(Level.WARNING, "", e);
            }
        }
    }
    
    private String getPassword() {
        IOWrapper<String> iow = this.getIOWrapper(this.getPasswordKey());
        return iow == null ? null : iow.getTarget();
    }
    public void setPassword(String sval) {
        IOWrapper<String> iow = this.getIOWrapper(this.getPasswordKey());
        iow.setTarget(sval);
    }
    private String getPasswordKey() {
        return "com.looseboxes.pu.password.string";
    }
    public String getUserName() {
        IOWrapper<String> iow = this.getIOWrapper(this.getUsernameKey());
        return iow == null ? null : iow.getTarget();
    }
    public void setUserName(String sval) {
        IOWrapper<String> iow = this.getIOWrapper(this.getUsernameKey());
        iow.setTarget(sval);
    }
    private String getUsernameKey() {
        return "com.looseboxes.pu.Username.string";
    }
    public String getDatabaseName() {
        IOWrapper<String> iow = this.getIOWrapper(this.getDatabaseKey());
        return iow == null ? null : iow.getTarget();
    }
    public void setDatabaseName(String sval) {
        IOWrapper<String> iow = this.getIOWrapper(this.getDatabaseKey());
        iow.setTarget(sval);
    }
    private String getDatabaseKey() {
        return "com.looseboxes.pu.databaseName.string";
    }
    private IOWrapper<String> getIOWrapper(String key) {
        return new IOWrapper<>(
                null, "META-INF/cache/"+key, 
                (name) -> PuInstaller.this.getFile(name).getAbsolutePath());
    }
}

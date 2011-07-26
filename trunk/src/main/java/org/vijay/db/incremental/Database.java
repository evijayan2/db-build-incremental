/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import org.vijay.db.incremental.pool.JDCConnectionDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author ElangovanV
 */
public class Database {

    private String command;
    private String sysuser;
    private String syspass;
    private String sysdb;
    private String sysdriver;
    private String sysurl;
    
    private static Database instance=null;
    private JDCConnectionDriver jDCConnectionDriver=null;

    protected Database() {        
    }
    
    private void init(Properties prop) {
        command = prop.getProperty("sysexeccommand");
        sysuser = prop.getProperty("systemuser");
        syspass = prop.getProperty("systempassword");
        sysdb = prop.getProperty("sysdatabase");
        sysdriver = prop.getProperty("sysdriver");
        sysurl = prop.getProperty("url_path");

        try {
             jDCConnectionDriver=new JDCConnectionDriver(sysdriver, sysurl, sysuser, syspass);             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:jdc:jdcpool");
    }

    public static Database getInstance(Properties prop) {
        
        if (instance == null) {
            instance = new Database();
            instance.init(prop);
        }
        return instance;
    }
    
    public void closeConnections() {
        jDCConnectionDriver.close();
    }

    public String getCommand() {
        return command;
    }

    public String getSysdb() {
        return sysdb;
    }

    public String getSysdriver() {
        return sysdriver;
    }

    public String getSyspass() {
        return syspass;
    }

    public String getSysuser() {
        return sysuser;
    }
}

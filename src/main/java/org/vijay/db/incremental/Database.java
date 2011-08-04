/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import org.vijay.db.incremental.pool.JDCConnectionDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

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
    
    private void init(Map prop) {
        command = prop.get("sysexeccommand").toString();
        sysuser = prop.get("systemuser").toString();
        syspass = prop.get("systempassword").toString();
        sysdb = prop.get("sysdatabase").toString();
        sysdriver = prop.get("sysdriver").toString();
        sysurl = prop.get("url_path").toString();

        try {
             jDCConnectionDriver=new JDCConnectionDriver(sysdriver, sysurl, sysuser, syspass);             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:jdc:jdcpool");
    }

    public static Database getInstance(Map prop) {
        
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

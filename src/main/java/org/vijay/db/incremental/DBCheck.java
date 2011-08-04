/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author ElangovanV
 */
public class DBCheck {

    private static Logger logger = Logger.getLogger(DBCheck.class);
    private SqlIncremental sqlIncremental;
    private Database database;

    public DBCheck(Database database, Incremental incremental) {

        this.database = database;

        if (incremental instanceof SqlIncremental) {
            sqlIncremental = (SqlIncremental) incremental;
        }
    }

    public DBCheck() {
    }

    public void insertBuildMetaData(String status) {

        String buildNumber = sqlIncremental.getBuildName();
        String patchLevel = sqlIncremental.getPatchLevel();
        String type = sqlIncremental.getType();
        String name = sqlIncremental.getFileName().getAbsolutePath();
        String submitter = sqlIncremental.getSubmitter();
        String transactionParams = sqlIncremental.getTransactionParams();

        String insertStatement = "INSERT INTO BUILD_TRANSACTION_HISTORY (BUILD_NUMBER,PATCH_LEVEL,TRANSACTION_TYPE,NAME,"
                + "STATUS,SUBMITTER,TRANSACTION_PARAMS) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            if (isIncrementalEntryPresent(buildNumber, transactionParams, patchLevel, name)) {
                updateBuildMetaData(buildNumber, patchLevel,
                        type, name, status, submitter,
                        transactionParams);
                return;
            }
            conn = database.getConnection();
            ps = conn.prepareStatement(insertStatement);
            ps.setString(1, buildNumber);
            if (patchLevel != null) {
                ps.setString(2, patchLevel);
            } else {
                ps.setString(2, "NONE");
            }
            ps.setString(3, type);
            ps.setString(4, name);
            ps.setString(5, status);
            ps.setString(6, submitter);
            ps.setString(7, transactionParams);
            ps.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            try {
                ps.close();
                rs.close();
                conn.close();
            } catch (Exception e) {
                //catch and consume no significance
            }
        }
    }

    public void updateBuildMetaData(String buildNumber, String patchLevel,
            String type, String name, String status, String submitter,
            String transactionParams) {
        String insertStatement = "UPDATE BUILD_TRANSACTION_HISTORY SET  STATUS=?, DATE_EXECUTED=SYSDATE WHERE BUILD_NUMBER=? and "
                + "( TRANSACTION_PARAMS=? OR TRANSACTION_PARAMS IS NULL) and NAME=? and PATCH_LEVEL=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            ps = conn.prepareStatement(insertStatement);
            ps.setString(1, status);
            ps.setString(2, buildNumber);
            ps.setString(3, transactionParams);
            ps.setString(4, name);
            if (patchLevel != null) {
                ps.setString(5, patchLevel);
            } else {
                ps.setString(5, "NONE");
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            try {
                ps.close();
                rs.close();
                conn.close();
            } catch (Exception e) {
                //catch and consume no significance
            }
        }
    }

    public boolean isIncrementalEntryPresent(String buildNumber, String transactionParams, String patchLevel, String name) {
        boolean wasSuccess = false;
        String selectStatement = "SELECT COUNT(*) FROM BUILD_TRANSACTION_HISTORY WHERE BUILD_NUMBER=? and "
                + "( TRANSACTION_PARAMS=? OR TRANSACTION_PARAMS IS NULL) and NAME=? and PATCH_LEVEL=?"; //
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            ps = conn.prepareStatement(selectStatement);
            ps.setString(1, buildNumber);
            ps.setString(2, transactionParams);
            ps.setString(3, name);

            if (patchLevel != null) {
                ps.setString(4, patchLevel);
            } else {
                ps.setString(4, "NONE");;
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);

                if (count > 0) {
                    wasSuccess = true;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("The incremental " + name + "for build number " + buildNumber + "patch level " + patchLevel + " with params " + transactionParams + "has been sucessfully run " + wasSuccess);
        return wasSuccess;
    }

    public boolean wasIncrementalSuccessfullyExecuted() {

        String buildNumber = sqlIncremental.getBuildName();
        String patchLevel = sqlIncremental.getPatchLevel();
        String type = sqlIncremental.getType();
        String name = sqlIncremental.getFileName().getAbsolutePath();
        String submitter = sqlIncremental.getSubmitter();
        String transactionParams = sqlIncremental.getTransactionParams();

        boolean wasSuccess = false;
        String selectStatement = "SELECT COUNT(*) FROM BUILD_TRANSACTION_HISTORY WHERE BUILD_NUMBER=? and "
                + "( TRANSACTION_PARAMS=? OR TRANSACTION_PARAMS IS NULL) and NAME=? and PATCH_LEVEL=? and STATUS='PASSED'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            ps = conn.prepareStatement(selectStatement);
            ps.setString(1, buildNumber);
            ps.setString(2, transactionParams);
            ps.setString(3, name);

            if (patchLevel != null) {
                ps.setString(4, patchLevel);
            } else {
                ps.setString(4, "NONE");;
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);

                if (count > 0) {
                    wasSuccess = true;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("The incremental " + name + " for build number " + buildNumber + " patch level " + patchLevel + " with params " + transactionParams + " has been sucessfully run " + wasSuccess);
        return wasSuccess;
    }

    public boolean validateUsers(Map dbMap, Map userMap) {
        try {
            String sysdriver = dbMap.get("sysdriver").toString();
            String sysurl = dbMap.get("url_path").toString();

            Class.forName(sysdriver);

            Iterator iterator = userMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                logger.debug("Connecting user: " + key);
                Connection con = DriverManager.getConnection(sysurl, key, userMap.get(key).toString());
                logger.debug("Sucessfully connected to user: " + key);
                if (con != null) {
                    con.close();
                    con = null;
                }
            }
            return true;
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            logger.error(ex.getMessage());
        }
        return false;
    }
}

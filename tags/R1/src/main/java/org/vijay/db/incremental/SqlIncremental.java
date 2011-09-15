/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.io.File;
import java.util.Iterator;
import java.util.SortedMap;

/**
 *
 * @author ElangovanV
 * 		<sql file="1/a.sql" submitter="vijay">
 *			<param name="user1" position="1"/>
 *			<param name="user2" position="2"/>
 *			<param name="user3" position="3"/>
 *		</sql>
 */
public class SqlIncremental implements Incremental {

    private String type = "SQL";
    private String sysDbname;
    private String buildName;
    private File fileName;
    private String submitter;
    private String logFile;
    private SortedMap paramList;
    private String patchLevel;
    
    public SqlIncremental(String sysDbname, String buildName, String patchLevel, File fileName, String submitter, String logFile,  SortedMap paramList) {
        this.sysDbname = sysDbname;
        this.buildName = buildName;
        this.patchLevel = patchLevel;
        this.fileName = fileName;
        this.submitter = submitter;
        this.paramList = paramList;        
        this.logFile = logFile;
    }

    public String getType() {
        return type;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getPatchLevel() {
        return patchLevel;
    }

    public void setPatchLevel(String patchLevel) {
        this.patchLevel = patchLevel;
    }

    public String getSysDbname() {
        return sysDbname;
    }

    public void setSysDbname(String sysDbname) {
        this.sysDbname = sysDbname;
    }

    public File getFileName() {
        return fileName;
    }

    public void setFileName(File fileName) {
        this.fileName = fileName;
    }

    public SortedMap getParamList() {
        return paramList;
    }

    public void setParamList(SortedMap paramList) {
        this.paramList = paramList;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    /* execString/dnbusr1/oracle/app/oracle/product/10.2.0/db_1/bin/sqlplus dnbi_install/dnbi_install@gpqa01 
     * @yosemite/global/complete/product/sql/master_sps_v3.sql gpqa01 livexp_v3 fieldrepositoryadmin_v3 
     * all0th3r5 all0th3r5 all0th3r5 all0th3r5 all0th3r5 all0th3r5 all0th3r5 all0th3r5 all0th3r5 all0th3r5 
     * all0th3r5 livexpsched_v3 all0th3r5 all0th3r5
     * 
     */
    public String getCommandString() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(fileName);
        sb.append(" ");
        sb.append(sysDbname);
        sb.append(" ");

        Iterator iterator = paramList.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            UserPassParam userPassParam = (UserPassParam) paramList.get(key);
            sb.append(userPassParam.getPassword());
            sb.append(" ");
        }

        return sb.toString();
    }

    public String getLogFile() {
        return logFile;
    }

    public String getTransactionParams() {
        StringBuilder sb=new StringBuilder();
        
        Iterator iterator = paramList.keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            UserPassParam userPassParam = (UserPassParam) paramList.get(key);
            sb.append(userPassParam.getUsername()).append("|");            
        }

        return sb.toString();
    }
}

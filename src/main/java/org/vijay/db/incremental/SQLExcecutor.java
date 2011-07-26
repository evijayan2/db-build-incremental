/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ElangovanV
 */
public class SQLExcecutor implements Executor {

    public int execute(Database database, Incremental incremental) {
        int exitVal=0;
        
        StringBuilder sb = new StringBuilder();
        sb.append(database.getCommand()).append(" ").append(database.getSysuser()).append("/").append(database.getSyspass()).append("@").append(database.getSysdb()).append(" ").append(incremental.getCommandString());

        System.out.println(sb.toString());

        try {

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(sb.toString());
            //Process proc = rt.exec("cmd /c dir 2.txt");
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", new File(incremental.getLogFile()));

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            exitVal = proc.waitFor();
            String runLog=outputGobbler.getMessage();
            if(runLog!=null&&(runLog.indexOf("ORA-")!=-1||runLog.indexOf("SP2-")!=-1)){
                    Logger.getLogger(SQLExcecutor.class.getName()).log(Level.SEVERE, "Encountered error in executing sql "+incremental.getCommandString());
                    return -1;
            }
            
            System.out.println("ExitValue: " + exitVal);

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return exitVal;
    }
}

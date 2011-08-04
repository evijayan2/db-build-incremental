/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.io.File;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;



/**
 *
 * @author ElangovanV
 */
public class SQLExcecutor implements Executor {
    private static Logger logger = Logger.getLogger(SQLExcecutor.class.getName());
    public int execute(Database database, Incremental incremental, Map prop) {
        int exitVal = 0;

        StringBuilder sb = new StringBuilder();
        sb.append(database.getCommand()).append(" ").append(database.getSysuser()).append("/").append(database.getSyspass()).append("@").append(database.getSysdb()).append(" ").append(incremental.getCommandString());

        logger.debug(sb.toString());

        try {

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(sb.toString());
            
            // any error message?
            //StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", proc);

            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", new File(prop.get("logfolder").toString(), incremental.getLogFile()), proc);

            outputGobbler.start();
            
            exitVal = proc.waitFor();
            
            String runLog = outputGobbler.getMessage();
            if (runLog != null && (runLog.indexOf("ORA-") != -1 || runLog.indexOf("SP2-") != -1)) {
                Logger.getLogger(SQLExcecutor.class.getName()).log(Level.ERROR, "Encountered error in executing sql " + incremental.getCommandString());
                return -1;
            }

        } catch (Throwable t) {
            logger.error("Got Error! ",t);
        }
        return exitVal;
    }
}

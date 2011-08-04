/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *
 * @author ElangovanV
 */
public class IncrementalProcessor {
private static Logger logger = Logger.getLogger(IncrementalProcessor.class.getName());
    private List incrementalList;
    private Database database;
    private Map prop;
    private final int EXECUTE=0;

    public IncrementalProcessor(List incrementalList, Database database, Map prop) {
        this.incrementalList = incrementalList;
        this.database = database;
        this.prop=prop;
    }

    private int preProcess(Incremental incremental) throws SQLException {
        DBCheck dBCheck = new DBCheck(database, incremental);
        if (!dBCheck.wasIncrementalSuccessfullyExecuted()) {
            dBCheck.insertBuildMetaData("PROCESSING");
            return EXECUTE;
        }
        return 1;
    }

    private void postProcess(Incremental incremental, String status) {
        DBCheck dBCheck=new DBCheck(database, incremental);
        dBCheck.insertBuildMetaData(status);       
    }

    public int process() {
        Executor executor = null;
        int exitVal=1;
        for (int i = 0; i < incrementalList.size(); i++) {
            Object executorObject = incrementalList.get(i);

            if (executorObject instanceof SqlIncremental) {
                try {
                    SqlIncremental sqlIncremental = (SqlIncremental) executorObject;
                        
                    executor = new SQLExcecutor();
                    if (preProcess(sqlIncremental) == EXECUTE) {
                        exitVal = executor.execute(database, sqlIncremental, prop);
                        
                        if (exitVal != 0) {
                            postProcess(sqlIncremental, "FAILED");
                            Logger.getLogger(BuildIncremental.class.getName()).log(Level.ERROR, "Incremental failed in "+ sqlIncremental.getFileName() +" submitted by "+sqlIncremental.getSubmitter());
                            return exitVal;
                        } else {
                            postProcess(sqlIncremental, "PASSED");
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IncrementalProcessor.class.getName()).log(Level.ERROR, null, ex);
                }
            }
        }
        return 0;
    }
}

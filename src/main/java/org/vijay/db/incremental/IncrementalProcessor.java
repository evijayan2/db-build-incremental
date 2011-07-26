/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ElangovanV
 */
public class IncrementalProcessor {

    private List incrementalList;
    private Database database;
    private final int EXECUTE=0;

    public IncrementalProcessor(List incrementalList, Database database) {
        this.incrementalList = incrementalList;
        this.database = database;
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
                        exitVal = executor.execute(database, sqlIncremental);
                        
                        if (exitVal != 0) {
                            postProcess(sqlIncremental, "FAILED");
                            Logger.getLogger(BuildIncremental.class.getName()).log(Level.SEVERE, "Incremental failed in {0} submitted by {1}", new Object[]{sqlIncremental.getFileName(), sqlIncremental.getSubmitter()});
                            return exitVal;
                        } else {
                            postProcess(sqlIncremental, "PASSED");
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(IncrementalProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return 0;
    }
}

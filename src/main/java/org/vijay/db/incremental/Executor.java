/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.util.Properties;

/**
 *
 * @author ElangovanV
 */
public interface Executor {
    
    public int execute(Database database, Incremental incremental);
    
}

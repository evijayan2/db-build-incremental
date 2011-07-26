/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

/**
 *
 * @author ElangovanV
 */
public interface Incremental {

    public String getCommandString();
    
    public String getLogFile();
}

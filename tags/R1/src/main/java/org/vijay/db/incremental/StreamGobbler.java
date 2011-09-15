/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author ElangovanV
 */
public class StreamGobbler extends Thread {
private static Logger logger = Logger.getLogger(StreamGobbler.class.getName());
    InputStream is;
    String type;
    File os;
    Process proc;
    
    StringBuffer sb;

    public StreamGobbler(InputStream is, String type, Process proc) {
        this(is, type, null, proc);
    }

    public StreamGobbler(InputStream is, String type, File redirect, Process proc) {
        this.is = is;
        this.type = type;
        this.os = redirect;
        this.proc=proc;
    }

    public void run() {
        PrintWriter pw = null;
        sb=new StringBuffer();
        try {

            if (os != null) {
                pw = new PrintWriter(os);
            }

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                    sb.append(line);
                }
                logger.debug(type + ">" + line);
                
                if(line!=null && (line.contains("SP2-0310"))){
                    
                    proc.destroy();
                    throw new IOException ("File Doesn't Exists = "+line);
                }
            }
            //System.out.println("called 1");
            if (pw != null) {

                pw.flush();
            }
        } catch (Exception ioe) {
            logger.error("Got Error!",ioe);
        } finally {
            if (pw != null) {

                pw.flush();
                pw.close();
                pw = null;
            }
        }
        //System.out.println("called 2");
    }
    
    public String getMessage() {
       //System.out.println("called 2 "+sb.toString()); 
        return sb.toString();
    }
}

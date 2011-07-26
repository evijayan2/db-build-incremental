/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author ElangovanV
 */
public class StreamGobbler extends Thread {

    InputStream is;
    String type;
    File os;
    
    StringBuffer sb;

    public StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    public StreamGobbler(InputStream is, String type, File redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
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
                System.out.println(type + ">" + line);
            }
            if (pw != null) {

                pw.flush();
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            if (pw != null) {

                pw.flush();
                pw.close();
                pw = null;
            }
        }
    }
    
    public String getMessage() {
        return sb.toString();
    }
}

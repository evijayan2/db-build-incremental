/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author ElangovanV
 */
class ConnectionReaper extends Thread {

    private JDCConnectionPool pool;
    private final long delay=300000;

    ConnectionReaper(JDCConnectionPool pool) {
        this.pool=pool;
    }

    private volatile boolean reaper=false;
    public void stopConnectionReaper() {
        reaper=true;       
    }

    public void run() {
        while(!reaper) {
           try {
              sleep(delay);
           } catch( InterruptedException e) { }
           pool.reapConnections();
        }
    }
}

public class JDCConnectionPool {

   private Vector connections;
   private String url, user, password;
   final private long timeout=60000;
   private ConnectionReaper reaper;
   final private int poolsize=10;

   public JDCConnectionPool(String url, String user, String password) {
      this.url = url;
      this.user = user;
      this.password = password;
      connections = new Vector(poolsize);
      reaper = new ConnectionReaper(this);
      reaper.start();
   }

   public synchronized void reapConnections() {

      long stale = System.currentTimeMillis() - timeout;
      Enumeration connlist = connections.elements();
    
      while((connlist != null) && (connlist.hasMoreElements())) {
          JDCConnection conn = (JDCConnection)connlist.nextElement();

          if((conn.inUse()) && (stale >conn.getLastUse()) && 
                                            (!conn.validate())) {
 	      removeConnection(conn);
         }
      }
   }

   public synchronized void closeConnections() {
        
      Enumeration connlist = connections.elements();

      while((connlist != null) && (connlist.hasMoreElements())) {
          JDCConnection conn = (JDCConnection)connlist.nextElement();
          removeConnection(conn);
      }
      reaper.interrupt();
      reaper.stopConnectionReaper();
      
   }

   private synchronized void removeConnection(JDCConnection conn) {
       connections.removeElement(conn);
   }


   public synchronized Connection getConnection() throws SQLException {

       JDCConnection c;
       for(int i = 0; i < connections.size(); i++) {
           c = (JDCConnection)connections.elementAt(i);
           if (c.lease()) {
              return c;
           }
       }

       Connection conn = DriverManager.getConnection(url, user, password);
       c = new JDCConnection(conn, this);
       c.lease();
       connections.addElement(c);
       return c;
  } 

   public synchronized void returnConnection(JDCConnection conn) {
      conn.expireLease();
   }
}

package org.vijay.db.incremental;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Hello world!
 *
 */
public class BuildIncremental extends Task{

    private Properties prop = null;
    private Database database;
    
    private File configFile;
    private File incrementalFile;
    private File logFile;

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public File getIncrementalFile() {
        return incrementalFile;
    }

    public void setIncrementalFile(File incrementalFile) {
        this.incrementalFile = incrementalFile;
    }

    public File getConfigFile() {
        return configFile;
    } 

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public static void main(String[] args) {

        String passwordFile = "d:/tmp/ise-incremental-password.property";
        String incrementalFile = "d:/tmp/incremental.xml";
        BuildIncremental app = new BuildIncremental();
        app.init(passwordFile);
        app.process(incrementalFile);
    }
    
    public void execute() throws BuildException {
        
        if (configFile == null) {
            throw new BuildException("No Config File set.");
        }
        
        if (incrementalFile == null) {
            throw new BuildException("No Incremental File set.");
        }
        
        init(configFile.getAbsolutePath());
        process(incrementalFile.getAbsolutePath());
    }

    public void init(String passwordFile) {
        try {
            prop = new Properties();
            prop.load(new FileInputStream(new File(passwordFile)));

            database = Database.getInstance(prop);
            /*Connection con=database.getConnection();
            
            
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select tname from tab");
            
            while (rs.next()) {
            System.out.println(rs.getString("tname"));
            }
            rs.close();
            stmt.close();
            con.close();
            con=null;
            
            database.closeConnections();*/
        } catch (IOException ex) {
            Logger.getLogger(BuildIncremental.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void process(String incrementalFile) {
        List incrementalList = null;
        try {
            XMLIncrementalProcessor xMLIncrementalProcessor = new XMLIncrementalProcessor();
            incrementalList = xMLIncrementalProcessor.processIncremental(incrementalFile, prop.getProperty("sysdatabase"), prop);
            IncrementalProcessor incrementalProcessor = new IncrementalProcessor(incrementalList, database);
            int exitVal=incrementalProcessor.process();
            
            if (exitVal!=0) {
                Logger.getLogger(BuildIncremental.class.getName()).log(Level.SEVERE, "Incremental complted with errors.");
                database.closeConnections();
                System.exit(exitVal);
            } else {
                Logger.getLogger(BuildIncremental.class.getName()).log(Level.FINE, "Incremental complted sucessfully.");
                database.closeConnections();
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(BuildIncremental.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BuildIncremental.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BuildIncremental.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

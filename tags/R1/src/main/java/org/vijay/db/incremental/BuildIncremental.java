package org.vijay.db.incremental;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.DocumentException;

/**
 * Hello world!
 *
 */
public class BuildIncremental extends Task {

    private static Logger logger = Logger.getLogger(BuildIncremental.class);
    private Database database;
    private File configFile;
    private File incrementalFile;
    private File logFile;
    private ConfigReader configReader;

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

        //String passwordFile = "d:/tmp/ise-incremental-password.property";
        //String incrementalFile = "d:/tmp/incremental.xml";
        String passwordFile = null;
        String incrementalFile = null;

        if (args.length < 1) {
            Logger.getLogger(BuildIncremental.class.getName()).log(Level.ERROR, "Incremental and Config file missing.");
            System.exit(1);
        }

        if (args[0] != null) {
            incrementalFile = args[0];
        }
        if (args[1] != null) {
            passwordFile = args[1];
        }

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

        System.setProperty("javax.xml.stream.XMLInputFactory", "com.bea.xml.stream.MXParserFactory");
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.bea.xml.stream.EventFactory");

        init(configFile.getAbsolutePath());
        logger.debug("configfile " + configFile.getAbsolutePath());
        logger.debug("incrementalFile " + incrementalFile.getAbsolutePath());

        if (configReader.isValidateUser()) {
            boolean validUsers=new DBCheck().validateUsers(configReader.getDatabase(),configReader.getUsers());
            logger.debug("Validate Users: "+validUsers);
            
            if (!validUsers) {
                database.closeConnections();
                logger.error("Validate user fails. Terminating Incremental.");
                System.exit(1);
            }
        }
        process(incrementalFile.getAbsolutePath());
    }

    public void init(String passwordFile) {
        try {
            //prop = new Properties();
            //prop.load(new FileInputStream(new File(passwordFile)));

            configReader = new ConfigReader(passwordFile);

            if (logFile != null) {
                PropertyConfigurator.configure(logFile.getAbsolutePath());
            }


            database = Database.getInstance(configReader.getDatabase());
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
            */
            database.closeConnections();
        } catch (DocumentException ex) {
            logger.log(Level.ERROR, null, ex);
        }
    }

    public void process(String incrementalFile) {
        List incrementalList = null;
        try {
            XMLIncrementalProcessor xMLIncrementalProcessor = new XMLIncrementalProcessor();
            incrementalList = xMLIncrementalProcessor.processIncremental(incrementalFile, database.getSysdb(), configReader);
            IncrementalProcessor incrementalProcessor = new IncrementalProcessor(incrementalList, database, configReader.getConfig());
            int exitVal = incrementalProcessor.process();

            if (exitVal != 0) {
                logger.log(Level.ERROR, "Incremental complted with errors.");
                database.closeConnections();
                System.exit(exitVal);
            } else {
                logger.log(Level.INFO, "Incremental complted sucessfully.");
                database.closeConnections();
            }
        } catch (XMLStreamException ex) {
            logger.log(Level.ERROR, null, ex);
        } catch (FileNotFoundException ex) {
            logger.log(Level.ERROR, null, ex);
        } catch (IOException ex) {
            logger.log(Level.ERROR, null, ex);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author ElangovanV
 */
public class ConfigReader {

    private static Logger logger = Logger.getLogger(ConfigReader.class.getName());
    Document document;

    public ConfigReader(String fileName) throws DocumentException {

        SAXReader saxReader = new SAXReader();
        document = saxReader.read(fileName);
        System.out.println(fileName);
    }

    public Map getUsers() {

        List list = document.selectNodes("//config/users");

        HashMap map = new HashMap();
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            Iterator iterator = element.elementIterator("entry");
            while (iterator.hasNext()) {
                Element userElement = (Element) iterator.next();
                map.put(userElement.attributeValue("name"), userElement.attributeValue("password"));
            }
        }
        return map;
    }

    public Map getDatabase() {

        Map map = new HashMap();

        List list = document.selectNodes("//config/database");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();

            map.put("dbtype", element.element("db").getText());
            map.put("systemuser", element.element("user").getText());
            map.put("systempassword", element.element("password").getText());
            map.put("sysdatabase", element.element("dbname").getText());
            map.put("sysexeccommand", element.element("command").getText());
            map.put("sysdriver", element.element("driver").getText());
            map.put("url_path", element.element("url").getText());
        }
        return map;
    }

    public boolean isValidateUser() {
        List list = document.selectNodes("//config");
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();

            String validate = element.element("validateuser").getText();

            if (validate.equalsIgnoreCase("true")) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public String getLogfolder() {
        List list = document.selectNodes("//config");
        Iterator iter = list.iterator();
        String logfolder = null;
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            logfolder = element.element("logfolder").getText();

        }
        return logfolder;
    }

    public Map getConfig() {
        Map map = new HashMap();

        List list = document.selectNodes("//config");
        Iterator iter = list.iterator();
        String logfolder = null;
        String environment=null;
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            logfolder = element.element("logfolder").getText();
            environment = element.element("environment").getText();
        }
        map.put("logfolder", logfolder);
        map.put("environment", environment);
        
        return map;
    }

    public static void main(String args[]) throws DocumentException {
        ConfigReader cr = new ConfigReader("d:/tmp/ise-incremental-password.xml");
        System.out.println(cr.getUsers());
        System.out.println(cr.isValidateUser());
        System.out.println(cr.getConfig());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vijay.db.incremental;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author ElangovanV
 */
public class XMLIncrementalProcessor {

    public List processIncremental(String fileName, String sysDbname, ConfigReader configReader) throws XMLStreamException, FileNotFoundException {
        
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader xmlr = factory.createXMLEventReader(new java.io.FileReader(fileName));

        List sqlList = new ArrayList();
        SortedMap map = new TreeMap();
        String sqlFileName = null;
        String sqlSubmitter = null;
        boolean isSql = false;
        String sqlUser;
        String sqlUserPosition;
        QName qname;
        String buildName = null;
        String patchLevel = null;
        boolean isBuild = false;
        String sqlLogFile = null;
        boolean isEnvironment= false;
        
        Map userProp=configReader.getUsers();
        Map configMap = configReader.getConfig();

        while (xmlr.hasNext()) {
            XMLEvent e = xmlr.nextEvent();

            switch (e.getEventType()) {

                case XMLStreamConstants.START_ELEMENT:
                    StartElement se = e.asStartElement();
                    
                    qname = se.getName();
                    if (qname.getLocalPart().equalsIgnoreCase("build")) {
                        buildName = se.getAttributeByName(new QName("name")).getValue();
                        Attribute attribute=se.getAttributeByName(new QName("patch-level"));
                        if (attribute!=null)
                            patchLevel = attribute.getValue();
                        isBuild = true;
                    } else if (qname.getLocalPart().equalsIgnoreCase("sql") && ((isBuild) || (isEnvironment))) {
                        map = new TreeMap();
                        sqlFileName = se.getAttributeByName(new QName("file")).getValue();
                        sqlSubmitter = se.getAttributeByName(new QName("submitter")).getValue();
                        sqlLogFile = se.getAttributeByName(new QName("log-file")).getValue();
                        isSql = true;
                    } else if (qname.getLocalPart().equalsIgnoreCase("param") && (isSql)) {
                        sqlUser = se.getAttributeByName(new QName("name")).getValue();
                        sqlUserPosition = se.getAttributeByName(new QName("position")).getValue();                        
                        map.put(sqlUserPosition, new UserPassParam(sqlUser, userProp.get(sqlUser).toString(), Integer.parseInt(sqlUserPosition)));
                    } else if (qname.getLocalPart().equalsIgnoreCase(configMap.get("environment").toString())) {
                        buildName = se.getAttributeByName(new QName("name")).getValue();
                        Attribute attribute=se.getAttributeByName(new QName("patch-level"));
                        if (attribute!=null)
                            patchLevel = attribute.getValue();                        
                        isEnvironment=true;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement ee = e.asEndElement();

                    qname = ee.getName();
                    if (qname.getLocalPart().equalsIgnoreCase("sql") && ((isBuild) || (isEnvironment))) {                       
                        sqlList.add(new SqlIncremental(sysDbname, buildName, patchLevel,new File(sqlFileName), sqlSubmitter, sqlLogFile,map));
                        isSql = false;
                        map=null;
                    } else if (qname.getLocalPart().equalsIgnoreCase("build") && (isSql == false)) {
                        isBuild = false;
                    } else if (qname.getLocalPart().equalsIgnoreCase(configMap.get("environment").toString()) && (isSql == false)) {
                        isEnvironment = false;
                    }
                    break;
            }
        }

        return sqlList;
    }

}

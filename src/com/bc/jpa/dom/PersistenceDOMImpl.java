package com.bc.jpa.dom;

import com.bc.dom.DOMImpl;
import com.bc.dom.XMLUtils;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @(#)PersistenceDOM.java   23-May-2014 16:40:32
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class PersistenceDOMImpl extends DOMImpl implements PersistenceDOM {

    public PersistenceDOMImpl(File file) {
        super(file, false);
    }
    
    public PersistenceDOMImpl(URI uri) {
        super(uri, false);
    }

    public PersistenceDOMImpl(InputStream in) {
        super(in, false);
    }

    public PersistenceDOMImpl(Document doc) {
        super(doc, false);
    }
    
    @Override
    public String getRootNodeName() {
        return "persistence";
    }
    
    @Override
    public List<String> getPersistenceUnitNames() {
        
        ArrayList<String> puNames = new ArrayList<>();
        
        NodeList puNodes = this.get("persistence-unit");
        
        for(int i=0; i<puNodes.getLength(); i++) {
            Node puNode = puNodes.item(i);
            final String puName = XMLUtils.getAttributeValue(puNode, "name");
            puNames.add(puName);
        }
Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
"Persistence units: {0}", puNames);
        
        return puNames;
    }
    
    @Override
    public List<String> getClassNames(String persistenceUnit) {
        
        ArrayList<String> puClasses = new ArrayList<>();
        
        NodeList puNodes = this.get("persistence-unit");
        
        for(int i=0; i<puNodes.getLength(); i++) {
            Node puNode = puNodes.item(i);
            final String puName = XMLUtils.getAttributeValue(puNode, "name");
            if(!persistenceUnit.equals(puName)) {
                continue;
            }
            NodeList children = puNode.getChildNodes();
            if(children == null) {
                break;
            }
//Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
//"Persistence unit: {0}, child nodes: {1}", 
//new Object[]{persistenceUnit, children.getLength()});
            
            for(int j=0; j<children.getLength(); j++) {
                Node child = children.item(j);
                if("class".equals(child.getNodeName())) {
                    puClasses.add(child.getChildNodes().item(0).getNodeValue());    
                }
            }
            break;
        }
Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
"Persistence unit: {0}, classes: {1}", new Object[]{persistenceUnit, puClasses});
        
        return puClasses;
    }
    
    @Override
    public Properties getProperties(String persistenceUnit) {
        
        Properties props = new Properties();
        
        NodeList puNodes = this.get("persistence-unit");
        
        for(int i=0; i<puNodes.getLength(); i++) {
            Node puNode = puNodes.item(i);
            String puName = XMLUtils.getAttributeValue(puNode, "name");
            if(!persistenceUnit.equals(puName)) {
                continue;
            }
            NodeList puChildren = puNode.getChildNodes();
            for(int j=0; j<puChildren.getLength(); j++) {
                Node puChild = puChildren.item(j);
                if(!"properties".equals(puChild.getNodeName())) {
                    continue;
                }
                NodeList ptyNodes = puChild.getChildNodes();
                for(int k=0; k<ptyNodes.getLength(); k++) {
                    
                    Node ptyNode = ptyNodes.item(k);
                    // E.g. name="javax.persistence.jdbc.url" value=""
                    String ptyName = XMLUtils.getAttributeValue(ptyNode, "name");
                    String ptyValue = XMLUtils.getAttributeValue(ptyNode, "value");
                    
                    if(ptyName == null || ptyValue == null) {
                        continue;
                    }
                    
                    // Should not ideally expose password
                    //
//                    if(!"javax.persistence.jdbc.password".equals(ptyName)) {
                        props.setProperty(ptyName, ptyValue);
//                    }
                }
                break;
            }
            break;
        }
// This contains password --- no logging        
//Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
//"Persistence unit: {0}, properties: {1}", new Object[]{persistenceUnit, props});

        return props;
    }
}

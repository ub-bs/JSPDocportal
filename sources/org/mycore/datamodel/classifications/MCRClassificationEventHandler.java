/*
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ***  M y C o R e  *** 
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */

package org.mycore.datamodel.classifications;
import java.util.Map;

import org.apache.commons.collections.LRUMap;
import org.apache.log4j.Logger;
import org.dom4j.util.NodeComparator;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.common.events.MCREvent;
import org.mycore.common.events.MCREventHandlerBase;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectMetadata;

/**
 * This class implements an event handler that reacts on object modify / create / delete events.
 * It basically clears the different classification caches to make sure the number of documents
 *  for each classification item is displayed correctly. This has also an impact to the question
 * if a classification item can be expanded to display subitems.
 * 
 * @author Robert Stephan
 */
public class MCRClassificationEventHandler extends MCREventHandlerBase {

    static MCRClassificationManager classMgr= MCRClassificationManager.instance();

    //a map that stores n items (as key - value pairs) and if full, 
    //deletes the least recently used one.
    public Map metadataChanged = new LRUMap(24);
        
    /**
     * This method add the data to SQL table of XML data via MCRXMLTableManager.
     * 
     * @param evt
     *            the event that occured
     * @param obj
     *            the MCRObject that caused the event
     */
    protected final void handleObjectCreated(MCREvent evt, MCRObject obj) {
        classMgr.jDomCache.clear();
    }

    /**
     * This method update the data to SQL table of XML data via
     * MCRXMLTableManager.
     * 
     * @param evt
     *            the event that occured
     * @param obj
     *            the MCRObject that caused the event
     */
    @SuppressWarnings("unchecked")
	protected final void handleObjectUpdated(MCREvent evt, MCRObject obj) {
        if(metadataChanged.containsKey(obj.getId().getId()) 
                 && equalsMetadata(((MCRObjectMetadata) metadataChanged.get(obj.getId().getId())), obj.getMetadata())){                
                  
            return;
        }
        
        classMgr.jDomCache.clear();
        classMgr.categoryCache.clear();
        classMgr.classificationCache.clear();
                       
        Logger.getLogger(this.getClass()).info("Cleared Classification Caches");        
        metadataChanged.put(obj.getId().getId(), obj.getMetadata());

   }

    

        
    
    /**
     * This method delete the XML data from SQL table data via
     * MCRXMLTableManager.
     * 
     * @param evt
     *            the event that occured
     * @param obj
     *            the MCRObject that caused the event
     */
    protected final void handleObjectDeleted(MCREvent evt, MCRObject obj) {
        classMgr.jDomCache.clear();
    }

    /**
     * compares two MCRObjectMetadata objects by doing a comparision of the generated
     * xml tree
     * This is done by converting the JDOM XML Elements into DOM4J Elements, since
     * only DOM4J provides a NodeComparator class which can do deep comparisons.
     * 
     * @param x one object to compare
     * @param y the other object to compare
     * @return true, if equal
     */
    protected boolean equalsMetadata(MCRObjectMetadata x, MCRObjectMetadata y) {
        if (x.size() != y.size()) {
            return false;
        }
        try {
            Document dX = new Document();
            dX.addContent(x.createXML());
            Document dY = new Document();
            dY.addContent(y.createXML());
            DOMOutputter converter = new DOMOutputter();
            org.w3c.dom.Document domX = converter.output(dX);
            org.w3c.dom.Document domY = converter.output(dY);

            org.dom4j.io.DOMReader reader = new org.dom4j.io.DOMReader();
            org.dom4j.Document dom4jX = reader.read(domX);
            org.dom4j.Document dom4jY = reader.read(domY);

            NodeComparator nc = new NodeComparator();
            return nc.compare(dom4jX, dom4jY) == 0;

        } catch (JDOMException jde) {
            return false;
        }
    }
    
}

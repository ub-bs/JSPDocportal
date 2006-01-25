/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
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
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

// package
package org.mycore.frontend.jsp.taglibs;

// Imported java classes
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.access.MCRAccessManagerBase;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.common.xml.MCRURIResolver;
import org.mycore.common.xml.MCRXMLHelper;
import org.mycore.frontend.jsp.format.MCRResultFormatter;
import org.mycore.frontend.workflow.MCRWorkflowManager;

/**
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRListWorkflowTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRListWorkflowTag.class.getName());
	private static MCRAccessManagerBase AM = (MCRAccessManagerBase) MCRConfiguration.instance().getInstanceOf("MCR.Access_class_name");
	private static MCRWorkflowManager WFM = null;
	private static String SLASH = File.separator;
	private static MCRResultFormatter formatter;
	private String docType;
	private String var;

	public void setDocType(String inputDocType) {
		docType = inputDocType;
		return;
	}


	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}
	
	public void initialize() {
		formatter = (MCRResultFormatter) MCRConfiguration.instance().getSingleInstanceOf("MCR.ResultFormatter_class_name","org.mycore.frontend.jsp.format.MCRResultFormatter");
		try {
			WFM = MCRWorkflowManager.instance();
			
		} catch (Exception e) {
			LOGGER.error("WFM-Error",e);
		} 
		LOGGER.debug("MCRListWorkflowTag: initialized " );
	}	
	
	public void doTag() throws JspException, IOException {
		if (formatter == null) initialize();
		
		PageContext pageContext = (PageContext) getJspContext();
		
		LOGGER.debug("MCRListWorkflowTag: type = " + docType);
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
		String lang = mcrSession.getCurrentLanguage();
		LOGGER.debug("MCRListWorkflowTag: lang = " + lang);
		String userid = mcrSession.getCurrentUserID();
		LOGGER.debug("MCRListWorkflowTag current Session for list workflow = " + mcrSession.getID());
		LOGGER.debug("MCRListWorkflowTag current user for list workflow = " + userid);

		// read directory
		ArrayList workfiles = new ArrayList();
		ArrayList derifiles = new ArrayList();
		workfiles = WFM.getAllObjectFileNames(docType);
		derifiles = WFM.getAllDerivateFileNames(docType);
		String dirname = WFM.getDirectoryPath(docType);

		// read the derivate XML files
		ArrayList derobjid = new ArrayList();
		ArrayList derderid = new ArrayList();
		ArrayList dermain = new ArrayList();
		ArrayList derlabel = new ArrayList();
		org.jdom.Document der_in;
		org.jdom.Element der;
		String mainfile;
		String label;
		String derid;
		String objid;
		String dername;
		for (int i = 0; i < derifiles.size(); i++) {
			dername = (String) derifiles.get(i);
			StringBuffer sd = (new StringBuffer(dirname)).append(SLASH).append(dername);
			mainfile = "";
			label = "Derivate of " + dername.substring(0, dername.length() - 4);
			objid = "";
			try {
				der_in = MCRXMLHelper.parseURI(sd.toString(), false);
				//LOGGER.debug("Derivate file "+dername+" was readed.");
				der = der_in.getRootElement();
				label = der.getAttributeValue("label");
				derid = der.getAttributeValue("ID");
				org.jdom.Element s1 = der.getChild("derivate");
				if (s1 != null) {
					org.jdom.Element s2 = s1.getChild("linkmetas");
					if (s2 != null) {
						org.jdom.Element s3 = s2.getChild("linkmeta");
						if (s3 != null) {
							objid = s3.getAttributeValue("href",org.jdom.Namespace.getNamespace("xlink",MCRDefaults.XLINK_URL));
						}
					}
					s2 = s1.getChild("internals");
					if (s2 != null) {
						org.jdom.Element s3 = s2.getChild("internal");
						if (s3 != null) {
							mainfile = s3.getAttributeValue("maindoc");
						}
					}
				}
				derobjid.add(objid);
				derderid.add(derid);
				derlabel.add(label);
				dermain.add(mainfile);
			} catch (Exception ex) {
				LOGGER.warn("Can't parse workflow file " + dername);
			}
		}
		
		// build the frame of mcr_workflow
		org.jdom.Element root = new org.jdom.Element("mcr_workflow");
		root.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi",	MCRDefaults.XSI_URL));
		root.setAttribute("type", docType);
		root.setAttribute("step", "editor");
		org.jdom.Document workflow_in = null;
		
		String resultlistResource = new StringBuffer("resource:resultlist-").append(docType).append(".xml").toString();
		Element resultlistElement = MCRURIResolver.instance().resolve(resultlistResource);

		// run the loop over all objects in the workflow
		for (int i = 0; i < workfiles.size(); i++) {
		
			String wfile = (String) workfiles.get(i);
			StringBuffer sb = (new StringBuffer(dirname)).append(SLASH).append(	wfile);
			try {
				workflow_in = MCRXMLHelper.parseURI(sb.toString(), false);
				LOGGER.debug("Workflow file "+wfile+" was readed.");
			} catch (Exception ex) {
				LOGGER.warn("Can't parse workflow file " + wfile);
				continue;
			}			
			String ID = workflow_in.getRootElement().getAttributeValue("ID");
			
			// check the modify-access rights
            if( ! AM.checkAccess(ID, "modify", MCRSessionMgr.getCurrentSession())){
            	continue ;
            }
			try {
				// formatting only the files with access!!
		        Element containerHit = formatter.processDocDetails(workflow_in,resultlistElement,lang,"", docType);
		        Element mcr_result = new Element("mcr_result");
		        mcr_result.addContent(containerHit);
		        root.addContent(mcr_result);
		        
				for (int j = 0; j < derifiles.size(); j++) {
					if (ID.equals((String) derobjid.get(j))) {
						dername = (String) derifiles.get(j);
						LOGGER.debug("Check the derivate file " + dername);
						String derpath = (String) derderid.get(j);
						mainfile = (String) dermain.get(j);
						org.jdom.Element deriv = new org.jdom.Element("derivate");
						deriv.setAttribute("ID", (String) derderid.get(j));
						deriv.setAttribute("label", (String) derlabel.get(j));
						File dir = new File(dirname, derpath);
						LOGGER.debug("Derivate under " + dir.getName());
						if (dir.isDirectory()) {
							ArrayList dirlist = MCRUtils.getAllFileNames(dir);
							for (int k = 0; k < dirlist.size(); k++) {
								org.jdom.Element file = new org.jdom.Element("file");
								file.setText(derpath + SLASH + (String) dirlist.get(k));
								File thisfile = new File(dir, (String) dirlist.get(k));
								file.setAttribute("size", String.valueOf(thisfile.length()));
								if (mainfile.equals((String) dirlist.get(k))) {
									file.setAttribute("main", "true");
								} else {
									file.setAttribute("main", "false");
								}
								deriv.addContent(file);
							}
							derifiles.remove(j);
							derobjid.remove(j);
							derderid.remove(j);
							dermain.remove(j);
							derlabel.remove(j);
							j--;
						}
						mcr_result.addContent(deriv);
					}
				}
			} catch (Exception ex) {
				LOGGER.error("Error while read derivates for XML workflow file " + (String) workfiles.get(i));
				LOGGER.error(ex.getMessage());
			}
		}
		org.jdom.Document workflow_doc = new org.jdom.Document(root);

		JSPUtils.getPrettyString(workflow_doc);
		
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(workflow_doc);
		} catch (JDOMException e) {
			Logger.getLogger(MCRSetResultListTag.class).error("Domoutput failed: ", e);
		}
		pageContext.setAttribute(var, domDoc);
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"100\" rows=\"30\">")
				.append("this is the jdom for the browse-control delivered by mcr:MCRListWorkflowCtrlTag\r\n")
				.append(JSPUtils.getPrettyString(workflow_doc))
				.append("</textarea>");
			out.println(debugSB.toString());
		}        
        return;
	}

}

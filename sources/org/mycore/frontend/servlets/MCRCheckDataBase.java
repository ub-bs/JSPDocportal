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

package org.mycore.frontend.servlets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jdom.Namespace;

import org.mycore.common.JSPUtils;
import org.mycore.common.MCRConfigurationException;
import org.mycore.common.MCRDefaults;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRMetaAddress;
import org.mycore.datamodel.metadata.MCRMetaBoolean;
import org.mycore.datamodel.metadata.MCRMetaClassification;
import org.mycore.datamodel.metadata.MCRMetaDate;
import org.mycore.datamodel.metadata.MCRMetaHistoryDate;
import org.mycore.datamodel.metadata.MCRMetaISO8601Date;
import org.mycore.datamodel.metadata.MCRMetaInstitutionName;
import org.mycore.datamodel.metadata.MCRMetaLangText;
import org.mycore.datamodel.metadata.MCRMetaLink;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetaNumber;
import org.mycore.datamodel.metadata.MCRMetaPersonName;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.MCRXMLTableManager;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.workflow.MCRWorkflowManager;
import org.mycore.user.MCRUserMgr;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML and store the XML in a file or if an error was occured start the
 * editor again.
 * 
 * @author Jens Kupferschmidt
 * @version $Revision$ $Date$
 */

abstract public class MCRCheckDataBase extends MCRCheckBase {
	
	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		HttpServletRequest request = job.getRequest();
		HttpServletResponse response = job.getResponse();
		// read the XML data
		MCREditorSubmission sub = (MCREditorSubmission) (request
				.getAttribute("MCREditorSubmission"));
		org.jdom.Document indoc = sub.getXML();

		// read the parameter
		MCRRequestParameters parms;
		if (sub == null)
			parms = new MCRRequestParameters(request);
		else
			parms = sub.getParameters();
		String oldmcrid = parms.getParameter("mcrid");
		String oldtype = parms.getParameter("type");
		String oldstep = parms.getParameter("step");
		logger.debug("XSL.target.param.0 = " + oldmcrid);
		logger.debug("XSL.target.param.1 = " + oldtype);
		logger.debug("XSL.target.param.2 = " + oldstep);

		// get the MCRSession object for the current thread from the session
		// manager.
		MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
		String lang = mcrSession.getCurrentLanguage();
		logger.info("LANG = " + lang);		
		String userid = mcrSession.getCurrentUserID();
		//userid = "administrator";
		logger.debug("Current user for edit check = " + userid);
		String usererrorpage = "mycore-error.jsp?messageKey=SWF.PrivilegesError&lang=" + lang;
		if (!AI.checkPermission(oldmcrid, "create" )) {
			response.sendRedirect(getBaseURL() + usererrorpage);
			return;
		}		

		// prepare the MCRObjectID's for the Metadata
		String mmcrid = "";
		boolean hasid = false;
		try {
			mmcrid = indoc.getRootElement().getAttributeValue("ID");
			if (mmcrid == null) {
				mmcrid = oldmcrid;
			} else {
				hasid = true;
			}
		} catch (Exception e) {
			mmcrid = oldmcrid;
		}
		MCRObjectID ID = new MCRObjectID(mmcrid);
		if (!ID.getTypeId().equals(oldtype)) {
			ID = new MCRObjectID(oldmcrid);
			hasid = false;
		}
		if (!hasid) {
			indoc.getRootElement().setAttribute("ID", ID.getId());
		}

		// Save the incoming to a file
		byte[] outxml = MCRUtils.getByteArray(indoc);
		String savedir = CONFIG.getString("MCR.editor_" + ID.getTypeId()
				+ "_directory");
		String NL = System.getProperty("file.separator");
		String fullname = savedir + NL + ID.getId() + ".xml";
		storeMetadata(outxml, job, ID, fullname, lang, userid);

		// create a metadata object and prepare it
		org.jdom.Document outdoc = prepareMetadata((org.jdom.Document) indoc
				.clone(), ID, job, oldstep, lang);
		if (outdoc == null) return;
		outxml = MCRUtils.getByteArray(outdoc);

		// Save the prepared metadata object
		storeMetadata(outxml, job, ID, fullname, lang, userid);

		// call the getNextURL and sendMail methods
		String url = getNextURL(ID);
		sendMail(ID);
		response.sendRedirect(
				response.encodeRedirectURL(getBaseURL() + url));
	}

	/**
	 * The method stores the data in a working directory dependenced of the
	 * type.
	 * 
	 * @param outdoc
	 *            the prepared JDOM object
	 * @param job
	 *            the MCRServletJob
	 * @param the
	 *            MCRObjectID of the MCRObject/MCRDerivate
	 * @param fullname
	 *            the file name where the JDOM was stored.
	 * @param lang
	 *            the current langauge
	 */
	public final void storeMetadata(byte[] outxml, MCRServletJob job,
			MCRObjectID ID, String fullname, String lang, String userid) throws Exception {
		if (outxml == null)
			return;
		// Save the prepared MCRObject/MCRDerivate to a file
		try {
			FileOutputStream out = new FileOutputStream(fullname);
			out.write(outxml);
			out.flush();
		} catch (IOException ex) {
			logger.error(ex.getMessage());
			logger.error("Exception while store to file " + fullname);
			errorHandlerIO(job, lang);
			return;
		}
		logger.info("Object " + ID.getId() + " stored under " + fullname + ".");
		MCRWorkflowManager.createWorkflowDefaultRule(ID.getId(),userid);
	}

	/**
	 * The method read the incoming JDOM tree in a MCRObject and prepare this by
	 * the following rules. After them it return a JDOM as result of
	 * MCRObject.createXML(). <br/>
	 * <li>remove all target of MCRMetaClassification they have not a categid
	 * attribute.</li>
	 * <br/>
	 * <li>remove all target of MCRMetaLangText they have an empty text</li>
	 * <br/>
	 * 
	 * @param jdom_in
	 *            the JDOM tree from the editor
	 * @param ID
	 *            the MCRObjectID of the MCRObject
	 * @param job
	 *            the MCRServletJob data
	 * @param step
	 *            the current workflow step
	 * @param lang
	 *            the current language
	 */
	protected org.jdom.Document prepareMetadata(org.jdom.Document jdom_in,
			MCRObjectID ID, MCRServletJob job, String step, String lang)
			throws Exception {
		ArrayList errorlog = new ArrayList();
		// add the namespaces (this is a workaround)
		org.jdom.Element root = jdom_in.getRootElement();
		root.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xlink",
				MCRDefaults.XLINK_URL));
		root.addNamespaceDeclaration(org.jdom.Namespace.getNamespace("xsi",
				MCRDefaults.XSI_URL));
		// check the label
		String label = root.getAttributeValue("label");
		if ((label == null) || ((label = label.trim()).length() == 0)) {
			root.setAttribute("label", ID.getId());
		}
		// remove the path elements from the incoming
		org.jdom.Element pathes = root.getChild("pathes");
		if (pathes != null) {
			root.removeChildren("pathes");
		}
		// structure
		boolean hasparent = false;
		org.jdom.Element structure = root.getChild("structure");
		if (structure == null) {
			root.addContent(new Element("structure"));
		} else {
			List structurelist = structure.getChildren();
			if (structurelist != null) {
				int structurelistlen = structurelist.size();
				for (int j = 0; j < structurelistlen; j++) {
					org.jdom.Element datatag = (org.jdom.Element) structurelist
							.get(j);
					String mcrclass = datatag.getAttributeValue("class");
					List datataglist = datatag.getChildren();
					int datataglistlen = datataglist.size();
					for (int k = 0; k < datataglistlen; k++) {
						org.jdom.Element datasubtag = (org.jdom.Element) datataglist
								.get(k);
						// MCRMetaLinkID
						if (mcrclass.equals("MCRMetaLinkID")) {
							String href = datasubtag.getAttributeValue("href");
							if (href == null) {
								datatag.removeContent(datasubtag);
								k--;
								datataglistlen--;
								continue;
							}
							if (datasubtag.getAttribute("type") != null) {
								datasubtag
										.getAttribute("type")
										.setNamespace(
												org.jdom.Namespace
														.getNamespace(
																"xlink",
																MCRDefaults.XLINK_URL));
							}
							if (datasubtag.getAttribute("href") != null) {
								datasubtag
										.getAttribute("href")
										.setNamespace(
												org.jdom.Namespace
														.getNamespace(
																"xlink",
																MCRDefaults.XLINK_URL));
							}
							if (datasubtag.getAttribute("title") != null) {
								datasubtag
										.getAttribute("title")
										.setNamespace(
												org.jdom.Namespace
														.getNamespace(
																"xlink",
																MCRDefaults.XLINK_URL));
							}
							if (datasubtag.getAttribute("label") != null) {
								datasubtag
										.getAttribute("label")
										.setNamespace(
												org.jdom.Namespace
														.getNamespace(
																"xlink",
																MCRDefaults.XLINK_URL));
							}
							try {
								MCRMetaLinkID test = new MCRMetaLinkID();
								test.setFromDOM(datasubtag);
								if (!test.isValid())
									throw new MCRException("");
							} catch (Exception e) {
								errorlog.add("Element " + datasubtag.getName()
										+ " is not valid.");
								datatag.removeContent(datasubtag);
								k--;
								datataglistlen--;
								continue;
							}
							continue;
						}
					}
					datataglist = datatag.getChildren();
					if (datataglist.size() == 0) {
						structure.removeContent(datatag);
						j--;
						structurelistlen--;
					} else {
						if (datatag.getName().equals("parents")) {
							hasparent = true;
						}
					}
				}
			}
		}
		// set the schema
		String mcr_schema = "";
		if (hasparent) {
			logger.debug("A parrent was found.");
		} else {
			logger.debug("No parrent was found.");
		}
		mcr_schema = "datamodel-" + ID.getTypeId() + ".xsd";
		root.setAttribute("noNamespaceSchemaLocation", mcr_schema,
				org.jdom.Namespace.getNamespace("xsi", MCRDefaults.XSI_URL));
		// metadata
		org.jdom.Element metadata = root.getChild("metadata");
		metadata.getAttribute("lang").setNamespace(Namespace.XML_NAMESPACE);
		List metadatalist = metadata.getChildren();
		int metadatalistlen = metadatalist.size();
		for (int j = 0; j < metadatalistlen; j++) {
			org.jdom.Element datatag = (org.jdom.Element) metadatalist.get(j);
			String mcrclass = datatag.getAttributeValue("class");
			List datataglist = datatag.getChildren();
			int datataglistlen = datataglist.size();
			for (int k = 0; k < datataglistlen; k++) {
				org.jdom.Element datasubtag = (org.jdom.Element) datataglist
						.get(k);
				// MCRMetaLangText
				if (mcrclass.equals("MCRMetaLangText")) {
					String text = datasubtag.getTextNormalize();
					if ((text == null) || ((text = text.trim()).length() == 0)) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(
								Namespace.XML_NAMESPACE);
					}
					try {
						MCRMetaLangText test = new MCRMetaLangText();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				// MCRMetaClassification
				if (mcrclass.equals("MCRMetaClassification")) {
					String categid = datasubtag.getAttributeValue("categid");
					if (categid == null) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					try {
						MCRMetaClassification test = new MCRMetaClassification();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				// MCRMetaLink
				if (mcrclass.equals("MCRMetaLink")) {
					String href = datasubtag.getAttributeValue("href");
					if (href == null) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					if (datasubtag.getAttribute("type") != null) {
						datasubtag.getAttribute("type").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					if (datasubtag.getAttribute("href") != null) {
						datasubtag.getAttribute("href").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					if (datasubtag.getAttribute("title") != null) {
						datasubtag.getAttribute("title").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					if (datasubtag.getAttribute("label") != null) {
						datasubtag.getAttribute("label").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					try {
						MCRMetaLink test = new MCRMetaLink();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				// MCRMetaLinkID
				if (mcrclass.equals("MCRMetaLinkID")) {
					String href = datasubtag.getAttributeValue("href");
					if (href == null) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					if (datasubtag.getAttribute("type") != null) {
						datasubtag.getAttribute("type").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					if (datasubtag.getAttribute("href") != null) {
						datasubtag.getAttribute("href").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					if (datasubtag.getAttribute("title") != null) {
						datasubtag.getAttribute("title").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					if (datasubtag.getAttribute("label") != null) {
						datasubtag.getAttribute("label").setNamespace(
								org.jdom.Namespace.getNamespace("xlink",
										MCRDefaults.XLINK_URL));
					}
					try {
						MCRMetaLinkID test = new MCRMetaLinkID();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				// MCRMetaISO8601Date
				if (mcrclass.equals("MCRMetaISO8601Date")) {
					String oldtext = datasubtag.getTextNormalize();
					String text = JSPUtils.convertToISO8601String(oldtext);					
					datasubtag.setText(text);
					logger.debug("convert date '" + text + "' to MCRMetaISO8601Date '" + text + "'");
					if ((text == null) || ((text = text.trim()).length() == 0)) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					try {
						MCRMetaISO8601Date test = new MCRMetaISO8601Date();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				// MCRMetaNumber
				if (mcrclass.equals("MCRMetaNumber")) {
					String text = datasubtag.getTextNormalize();
					if ((text == null) || ((text = text.trim()).length() == 0)) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(
								Namespace.XML_NAMESPACE);
					}
					try {
						MCRMetaNumber test = new MCRMetaNumber();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				if (mcrclass.equals("MCRMetaAddress")) {
					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(
								Namespace.XML_NAMESPACE);
					}
					try {
						MCRMetaAddress test = new MCRMetaAddress();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						//errorlog.add("Element "+datasubtag.getName()+" is not
						// valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				if (mcrclass.equals("MCRMetaInstitutionName")) {
					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(
								Namespace.XML_NAMESPACE);
					}
					try {
						MCRMetaInstitutionName test = new MCRMetaInstitutionName();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				if (mcrclass.equals("MCRMetaPersonName")) {
					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(
								Namespace.XML_NAMESPACE);
					}
					try {
						MCRMetaPersonName test = new MCRMetaPersonName();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				if (mcrclass.equals("MCRMetaBoolean")) {
					try {
						MCRMetaBoolean test = new MCRMetaBoolean();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				logger.error("To do for type " + mcrclass + " not found.");
			}
			datataglist = datatag.getChildren();
			if (datataglist.size() == 0) {
				metadata.removeContent(datatag);
				j--;
				metadatalistlen--;
			}
		}
		// service
		org.jdom.Element service = root.getChild("service");
		List servicelist = service.getChildren();
		int servicelistlen = servicelist.size();
		for (int j = 0; j < servicelistlen; j++) {
			org.jdom.Element datatag = (org.jdom.Element) servicelist.get(j);
			if (datatag.getName().equals("servflags")) {
				// get current user
				MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
				String userid = "User:" + mcrSession.getCurrentUserID();
				// is the a new editor?
				List servflaglist = datatag.getChildren();
				int servflaglistlen = servflaglist.size();
				boolean test = true;
				for (int h = 0; h < servflaglistlen; h++) {
					org.jdom.Element servflag = (org.jdom.Element) servflaglist
							.get(h);
					if (servflag.getText().equals(userid)) {
						test = false;
						break;
					}
				}
				if (test) {
					MCRMetaLangText line = new MCRMetaLangText("servflags",
							"servflag", "de", "", 0, "plain", userid);
					datatag.addContent(line.createXML());
				}
			}
			String mcrclass = datatag.getAttributeValue("class");
			List datataglist = datatag.getChildren();
			int datataglistlen = datataglist.size();
			for (int k = 0; k < datataglistlen; k++) {
				org.jdom.Element datasubtag = (org.jdom.Element) datataglist
						.get(k);
				// MCRMetaLangText
				if (mcrclass.equals("MCRMetaLangText")) {
					String text = datasubtag.getTextNormalize();
					if ((text == null) || ((text = text.trim()).length() == 0)) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(
								Namespace.XML_NAMESPACE);
					}
					try {
						MCRMetaLangText test = new MCRMetaLangText();
						test.setFromDOM(datasubtag);
						if (!test.isValid())
							throw new MCRException("");
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName()
								+ " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;
						continue;
					}
					continue;
				}
				/**
				// MCRMetaDate
				if (mcrclass.equals("MCRMetaDate")) {
				**/
				// MCRMetaISO8601Date
				if (mcrclass.equals("MCRMetaISO8601Date")) {
					String text = datasubtag.getTextNormalize();

					if ((text == null) || ((text = text.trim()).length() == 0)) {
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;

						continue;
					}

					if (datasubtag.getAttribute("lang") != null) {
						datasubtag.getAttribute("lang").setNamespace(Namespace.XML_NAMESPACE);
					}

					try {
						MCRMetaISO8601Date test = new MCRMetaISO8601Date();
						test.setFromDOM(datasubtag);

						if (!test.isValid()) {
							throw new MCRException("");
						}
					} catch (Exception e) {
						errorlog.add("Element " + datasubtag.getName() + " is not valid.");
						datatag.removeContent(datasubtag);
						k--;
						datataglistlen--;

						continue;
					}

					continue;
				}
			}
			datataglist = datatag.getChildren();
			if (datataglist.size() == 0) {
				service.removeContent(datatag);
				j--;
				servicelistlen--;
			}
		}
		// load the incoming
		MCRObject obj = new MCRObject();
		org.jdom.Document jdom_out = jdom_in;
		try {
			// load the JODM object
			byte[] xml = MCRUtils.getByteArray(jdom_in);
			obj.setFromXML(xml, true);
			Date curTime=new Date();
	        obj.getService().setDate("createdate",curTime);
	        obj.getService().setDate("modifydate",curTime);
			// return the XML tree
			jdom_out = obj.createXML();
		} catch (MCRException e) {
			errorlog.add(e.getMessage());
			Exception ex = e.getException();
			if (ex != null) {
				errorlog.add(ex.getMessage());
			}
		}
		boolean valid = errorHandlerValid(job, errorlog, ID, step, lang);
		if (!valid) return null;
		return jdom_out;
	}

	/**
	 * A method to handle valid errors.
	 */
	private final boolean errorHandlerValid(MCRServletJob job, ArrayList logtext,
			MCRObjectID ID, String step, String lang) throws Exception {
		if (logtext.size() == 0)
			return true;
		// write to the log file
		for (int i = 0; i < logtext.size(); i++) {
			logger.error((String) logtext.get(i));
		}
		HttpServletRequest request = job.getRequest();
		request.setAttribute("logtext",logtext);
		request.setAttribute("step",step);
		request.setAttribute("mcrID",ID.getId());
		request.setAttribute("type",ID.getTypeId());
		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/nav?path=~editor-form-error");
		rd.forward(request, job.getResponse());
		return false;
	}
	


}

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

package org.mycore.frontend.workflowengine.jbpm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;

import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRConfigurationException;
import org.mycore.common.MCRException;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.workflow.MCREditorOutValidator;

/**
 * This class is the superclass of servlets which checks the MCREditorServlet
 * output XML and store the XML in a file or if an error was occured start the
 * editor again.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */
public class MCRCheckMetadataServlet extends MCRServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = Logger.getLogger(MCRCheckMetadataServlet.class);
	private static String workflowEngineStartURL = MCRConfiguration.instance().getString("MCR.WorkflowEngine.StartURL.Relative", "~workflowengine");
	
    /**
     * This method overrides doGetPost of MCRServlet. <br />
     */
    public void doGetPost(MCRServletJob job) throws Exception {
    	
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
        // read the XML data
        MCREditorSubmission sub = (MCREditorSubmission) (request.getAttribute("MCREditorSubmission"));
        org.jdom.Document indoc = sub.getXML();

        // read the parameter
        MCRRequestParameters parms;

        if (sub == null) {
            parms = new MCRRequestParameters(request);
        } else {
            parms = sub.getParameters();
        }

        String oldmcrid1 = parms.getParameter("mcrid");
        String oldtype = parms.getParameter("type");
        String oldstep = parms.getParameter("step");
        String oldmcrid2 = parms.getParameter("mcrid2");
        String oldworkflowtype = parms.getParameter("workflowtype");
        LOGGER.debug("mcrid1 = " + oldmcrid1);
        LOGGER.debug("type = " + oldtype);
        LOGGER.debug("step = " + oldstep);
        LOGGER.debug("mcrid2 = " + oldmcrid2);
        LOGGER.debug("workflowtype = " + oldworkflowtype);

        // get the MCRSession object for the current thread from the session
        // manager.
        MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
        String lang = mcrSession.getCurrentLanguage();
        LOGGER.info("LANG = " + lang);

        // prepare the MCRObjectID's for the Metadata
        String mmcrid = "";
        boolean hasid = false;

        try {
            mmcrid = indoc.getRootElement().getAttributeValue("ID");

            if (mmcrid == null) {
                mmcrid = oldmcrid1;
            } else {
                hasid = true;
            }
        } catch (Exception e) {
            mmcrid = oldmcrid1;
        }

        MCRObjectID ID = new MCRObjectID(mmcrid);

        if (!ID.getTypeId().equals(oldtype)) {
            ID = new MCRObjectID(oldmcrid1);
            hasid = false;
        }

        if (!hasid) {
            indoc.getRootElement().setAttribute("ID", ID.getId());
        }

        // create a metadata object and prepare it
        org.jdom.Document outdoc = prepareMetadata((org.jdom.Document) indoc.clone(), ID, job, lang);
        request.setAttribute("unsavedJdom", outdoc);
        
        try{
        	request.getRequestDispatcher("/nav?path=" + workflowEngineStartURL).forward(request, response);
        }catch(MCRException ex) {
            // Save the incoming to a file
            byte[] outxml = MCRUtils.getByteArray(indoc);
            String savedir = CONFIG.getString("MCR.editor_" + ID.getTypeId() + "_directory");
            String NL = System.getProperty("file.separator");
            String fullname = savedir + NL + ID.getId() + ".xml";
            storeMetadata(outxml, job, ID, fullname);
            //TODO sendMail in WFE
            //sendMail(ID);
            String dispatcher = new StringBuffer("/nav?workflowtype=")
            	.append(oldworkflowtype).append("&path=").append(workflowEngineStartURL).toString();
            request.getRequestDispatcher(dispatcher).forward(request, response);        	
        }

    }

    /**
     * The method stores the data in a working directory dependenced of the
     * type.
     * 
     * @param outxml
     *            the prepared JDOM object
     * @param job
     *            the MCRServletJob
     * @param ID
     *            MCRObjectID of the MCRObject/MCRDerivate
     * @param fullname
     *            the file name where the JDOM was stored.
     */
    public final void storeMetadata(byte[] outxml, MCRServletJob job, MCRObjectID ID, String fullname) throws Exception {
        if (outxml == null) {
            return;
        }

        // Save the prepared MCRObject/MCRDerivate to a file
        try {
            FileOutputStream out = new FileOutputStream(fullname);
            out.write(outxml);
            out.flush();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.error("Exception while store to file " + fullname);
            return;
        }

        LOGGER.info("Object " + ID.getId() + " stored under " + fullname + ".");
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
     * @param lang
     *            the current language
     */
    protected org.jdom.Document prepareMetadata(org.jdom.Document jdom_in, MCRObjectID ID, MCRServletJob job, String lang) throws Exception {
        EditorValidator ev = new EditorValidator(jdom_in, ID);
        Document jdom_out = ev.generateValidMyCoReObject();
        errorHandlerValid(job, ev.getErrorLog(), ID, lang);
        return jdom_out;
    }

    /**
     * A method to handle valid errors.
     */
    private final void errorHandlerValid(MCRServletJob job, List logtext, MCRObjectID ID, String lang) throws Exception {
        if (logtext.size() == 0) {
            return;
        }

        // write to the log file
        for (int i = 0; i < logtext.size(); i++) {
            LOGGER.error(logtext.get(i));
        }

        // prepare editor with error messages
        String pagedir = CONFIG.getString("MCR.editor_page_dir", "");
        String myfile = pagedir + CONFIG.getString("MCR.editor_page_error_formular", "editor_error_formular.xml");
        org.jdom.Document jdom = null;

        try {
            InputStream in = (new URL(getBaseURL() + myfile + "?XSL.Style=xml")).openStream();

            if (in == null) {
                throw new MCRConfigurationException("Can't read editor file " + myfile);
            }

            jdom = new org.jdom.input.SAXBuilder().build(in);

            org.jdom.Element root = jdom.getRootElement();
            List sectionlist = root.getChildren("section");

            for (int i = 0; i < sectionlist.size(); i++) {
                org.jdom.Element section = (org.jdom.Element) sectionlist.get(i);

                if (!section.getAttributeValue("lang", org.jdom.Namespace.XML_NAMESPACE).equals(lang.toLowerCase())) {
                    continue;
                }

                org.jdom.Element p = new org.jdom.Element("p");
                section.addContent(0, p);

                org.jdom.Element center = new org.jdom.Element("center");

                // the error message
                org.jdom.Element table = new org.jdom.Element("table");
                table.setAttribute("width", "80%");

                for (int j = 0; j < logtext.size(); j++) {
                    org.jdom.Element tr = new org.jdom.Element("tr");
                    org.jdom.Element td = new org.jdom.Element("td");
                    org.jdom.Element el = new org.jdom.Element("font");
                    el.setAttribute("color", "red");
                    el.addContent((String) logtext.get(j));
                    td.addContent(el);
                    tr.addContent(td);
                    table.addContent(tr);
                }

                center.addContent(table);
                section.addContent(1, center);
                p = new org.jdom.Element("p");
                section.addContent(2, p);

                // the edit button
                org.jdom.Element form = section.getChild("form");
                form.setAttribute("action", job.getResponse().encodeRedirectURL(getBaseURL() + "servlets/MCRStartEditorServlet"));

                org.jdom.Element input1 = new org.jdom.Element("input");
                input1.setAttribute("name", "lang");
                input1.setAttribute("type", "hidden");
                input1.setAttribute("value", lang);
                form.addContent(input1);

                org.jdom.Element input2 = new org.jdom.Element("input");
                input2.setAttribute("name", "se_mcrid");
                input2.setAttribute("type", "hidden");
                input2.setAttribute("value", ID.getId());
                form.addContent(input2);

                org.jdom.Element input3 = new org.jdom.Element("input");
                input3.setAttribute("name", "type");
                input3.setAttribute("type", "hidden");
                input3.setAttribute("value", ID.getTypeId());
                form.addContent(input3);
            }
        } catch (org.jdom.JDOMException e) {
            throw new MCRException("Can't read editor file " + myfile + " or it has a parse error.", e);
        }

        // restart editor
        job.getRequest().setAttribute("MCRLayoutServlet.Input.JDOM", jdom);
        job.getRequest().setAttribute("XSL.Style", lang);

        RequestDispatcher rd = getServletContext().getNamedDispatcher("MCRLayoutServlet");
        rd.forward(job.getRequest(), job.getResponse());
    }
    
    /**
     * provides a wrappe for editor validation and MCRObject creation.
     * 
     * For a new MetaDataType, e.g. MCRMetaFooBaar, create a method
     * 
     * <pre>
     *   boolean checkMCRMetaFooBar(Element)
     * </pre>
     * 
     * use the following methods in that method to do common tasks on element
     * validation
     * <ul>
     * <li>checkMetaObject(Element,Class)</li>
     * <li>checkMetaObjectWithLang(Element,Class)</li>
     * <li>checkMetaObjectWithLangNotEmpty(Element,Class)</li>
     * <li>checkMetaObjectWithLinks(Element,Class)</li>
     * </ul>
     * 
     * @author Thomas Scheffler (yagee)
     * 
     * @version $Revision$ $Date$
     */
    protected class EditorValidator extends MCREditorOutValidator {
        /**
         * instantiate the validator with the editor input <code>jdom_in</code>.
         * 
         * <code>id</code> will be set as the MCRObjectID for the resulting
         * object that can be fetched with
         * <code>generateValidMyCoReObject()</code>
         * 
         * @param jdom_in
         *            editor input
         */
        public EditorValidator(Document jdom_in, MCRObjectID id) {
            super(jdom_in, id);
        }

    }    

}

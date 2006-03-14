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

import java.io.File;
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
	private static String workflowEngineStartURL = MCRConfiguration.instance().getString("MCR.WorkflowEngine.StartURL", "~workflowengine");
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getDefaultImpl();
	
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

        String mcrid1 = parms.getParameter("mcrid");
        String type = parms.getParameter("type");
        String step = parms.getParameter("step");
        String mcrid2 = parms.getParameter("mcrid2");
        String nextPath = parms.getParameter("nextPath");
        LOGGER.debug("mcrid1 = " + mcrid1);
        LOGGER.debug("type = " + type);
        LOGGER.debug("step = " + step);
        LOGGER.debug("mcrid2 = " + mcrid2);
        LOGGER.debug("nextPath = " + nextPath);
        
        WFI.setMetadataValidFlag(mcrid1, false);
        
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
                mmcrid = mcrid1;
            } else {
                hasid = true;
            }
        } catch (Exception e) {
            mmcrid = mcrid1;
        }

        MCRObjectID ID = new MCRObjectID(mmcrid);

        if (!ID.getTypeId().equals(type)) {
            ID = new MCRObjectID(mcrid1);
            hasid = false;
        }

        if (!hasid) {
            indoc.getRootElement().setAttribute("ID", ID.getId());
        }

        // create a metadata object and prepare it
        org.jdom.Document outdoc;
        StringBuffer storePath = new StringBuffer(WFI.getWorkflowDirectory(ID.getTypeId()))
			.append(File.separator).append(ID.getId()).append(".xml");
        try{
        	storeMetadata(MCRUtils.getByteArray(indoc),job, ID, storePath.toString());
        	outdoc = prepareMetadata((org.jdom.Document) indoc.clone(), ID, job, lang, step, nextPath, storePath.toString());
        	storeMetadata(MCRUtils.getByteArray(outdoc),job, ID, storePath.toString());
        	WFI.setMetadataValidFlag(mcrid1, true);
        	request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
        }catch(java.lang.IllegalStateException ill){
        	LOGGER.debug("because of error, forwarding to success page could not be executed [" + ill.getMessage() + "]");        	
        }catch(Exception e){
        	LOGGER.error("catched error:" , e);
        }
        //TODO sendMail in WFE
        //sendMail(ID);
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
    protected org.jdom.Document prepareMetadata(org.jdom.Document jdom_in, MCRObjectID ID, MCRServletJob job, 
    		String lang, String step, String nextPath, String storePath) throws Exception {
        try{
        	EditorValidator ev = new EditorValidator(jdom_in, ID);
        	errorHandlerValid(job, ev.getErrorLog(), ID, lang, step, nextPath, storePath);        	
        	Document jdom_out = ev.generateValidMyCoReObject();
        	return jdom_out;
        }catch(Exception e){
        	
        }
        
        return null;
    }

    /**
     * A method to handle valid errors.
     */
    private final void errorHandlerValid(MCRServletJob job, List logtext, MCRObjectID ID, 
    		String lang, String step, String nextPath, String storePath) throws Exception {
        if (logtext.size() == 0) {
            return;
        }

        // write to the log file
        for (int i = 0; i < logtext.size(); i++) {
            LOGGER.error(logtext.get(i));
        }

        if(logtext != null && logtext.size() > 0) {
	        // redirect to editor
	        HttpServletRequest request = job.getRequest();
	        request.setAttribute("errorList", logtext);
	        request.setAttribute("mcrid", ID.getId());
	        request.setAttribute("type", ID.getTypeId());
	        request.setAttribute("step", step);
	        request.setAttribute("nextPath", nextPath);
	        request.setAttribute("target", "MCRCheckMetadataServlet");
	        request.setAttribute("editorSource", storePath.toString().replaceAll("\\\\","/"));
	
	        getServletContext().getRequestDispatcher("/nav?path=~editor-validating-errors").forward(request, job.getResponse());
        }
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

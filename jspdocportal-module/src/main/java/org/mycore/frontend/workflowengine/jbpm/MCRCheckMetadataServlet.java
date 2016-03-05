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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRUtils;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.metadata.validator.MCREditorOutValidator;
import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.editor.MCRRequestParameters;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

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
		
    /**
     * This method overrides doGetPost of MCRServlet. <br />
     */
    @SuppressWarnings("deprecation")
    public void doGetPost(MCRServletJob job) throws Exception {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	// read the XML data
        MCREditorSubmission sub = (MCREditorSubmission) (request.getAttribute("MCREditorSubmission"));
        org.jdom2.Document indoc = sub.getXML();
 
        // read the parameter
        MCRRequestParameters parms = sub.getParameters();
        
        if(parms.getParameter("processid").equals("0")){
        	doGetPost_NoWorkflow(job);
        	return;
        }
        
        String mcrid1 = parms.getParameter("mcrid");
        String type = parms.getParameter("type");
        long processID = Long.parseLong(parms.getParameter("processid"));
        String workflowType = parms.getParameter("workflowType");
        String step = parms.getParameter("step");
        String mcrid2 = parms.getParameter("mcrid2");
        String nextPath = parms.getParameter("nextPath");
        LOGGER.debug("mcrid1 = " + mcrid1);
        LOGGER.debug("type = " + type);
        LOGGER.debug("workflowType = " + workflowType);
        LOGGER.debug("step = " + step);
        LOGGER.debug("mcrid2 = " + mcrid2);
        LOGGER.debug("nextPath = " + nextPath);
               
        MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(workflowType);
        MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(processID);
        try{
        String publicationType = (String) wfp.getContextInstance().getVariable(MCRWorkflowConstants.WFM_VAR_METADATA_PUBLICATIONTYPE);
        if(publicationType == null) publicationType=type;
        LOGGER.debug("publicationType = " + publicationType);
        
        WFM.setMetadataValid(mcrid1, false, wfp.getContextInstance());
        
        // get the MCRSession object for the current thread from the session
        // manager.
        MCRSession mcrSession = MCRServlet.getSession(request);
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

        MCRObjectID ID = MCRObjectID.getInstance(mmcrid);

        if (!ID.getTypeId().equals(type)) {
            ID = MCRObjectID.getInstance(mcrid1);
            hasid = false;
        }

        if (!hasid) {
            indoc.getRootElement().setAttribute("ID", ID.toString());
        }

        // create a metadata object and prepare it
        org.jdom2.Document outdoc;
        StringBuffer storePath = new StringBuffer(MCRWorkflowDirectoryManager.getWorkflowDirectory(ID.getTypeId()))
			.append("/").append(ID.toString()).append(".xml");
        
        	WFM.storeMetadata(MCRUtils.getByteArray(indoc, Format.getPrettyFormat()), ID.toString(), storePath.toString());
        	outdoc = prepareMetadata((org.jdom2.Document) indoc.clone(), ID, job, lang, step, 
        			   nextPath, storePath.toString(), workflowType, String.valueOf(processID), publicationType);
        	WFM.storeMetadata(MCRUtils.getByteArray(outdoc, Format.getPrettyFormat()), ID.toString(), storePath.toString());
        	WFM.setWorkflowVariablesFromMetadata(wfp.getContextInstance(), indoc.getRootElement().getChild("metadata"));
        	WFM.setMetadataValid(mcrid1, true, wfp.getContextInstance());
        	
        	
        }catch(java.lang.IllegalStateException ill){
        	LOGGER.debug("because of error, forwarding to success page could not be executed [" + ill.getMessage() + "]");        	
        }catch(Exception e){
        	LOGGER.error("catched error:" , e);
        
        }
        finally{
        	 wfp.close();
        	 request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
        }
        //TODO sendMail in WFE
        //sendMail(ID);
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
    protected org.jdom2.Document prepareMetadata(org.jdom2.Document jdom_in, MCRObjectID ID, MCRServletJob job, 
    		String lang, String step, String nextPath, String storePath, String workflowType, 
    		String processID , String publicationType) throws Exception {
    	 MCREditorOutValidator ev = null;
         try {
             ev = new MCREditorOutValidator(jdom_in, ID);
             Document jdom_out = ev.generateValidMyCoReObject();
             if (LOGGER.getEffectiveLevel().isGreaterOrEqual(Level.INFO))
                 for (String logMsg : ev.getErrorLog()) {
                     LOGGER.info(logMsg);
                 }
             return jdom_out;
         } catch (Exception e) {
             List<String> errorLog = ev != null ? ev.getErrorLog() : new ArrayList<String>();
             errorLog.add(e.getLocalizedMessage());
             errorHandlerValid(job, errorLog, ID, lang, step, nextPath, storePath, workflowType, processID, publicationType);
             return null;
         }
     }

    /**
     * A method to handle valid errors.
     */
    private final void errorHandlerValid(MCRServletJob job, @SuppressWarnings("rawtypes") List logtext, MCRObjectID ID, 
    		String lang, String step, String nextPath, String storePath, String workflowType, 
    		String processID, String publicationType) throws Exception {
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
	        request.setAttribute("workflowType", workflowType);
	        request.setAttribute("mcrid", ID.toString());
	        request.setAttribute("type", ID.getTypeId());
	        request.setAttribute("step", step);
	        request.setAttribute("nextPath", nextPath);
	        request.setAttribute("target", "MCRCheckMetadataServlet");
	        request.setAttribute("processID",processID);
	        request.setAttribute("publicationType",publicationType);
	        request.setAttribute("editorSource", storePath.toString().replaceAll("\\\\","/"));
	
	        getServletContext().getRequestDispatcher("/nav?path=~editor-validating-errors").forward(request, job.getResponse());
        }
    }
    
    /**
     * will be used if no jbpm workflow engine is present
     * (test of YAWL)
     * @param job
     * @throws Exception
     * 
     * @author Robert Stephan
     */
    @SuppressWarnings("deprecation")
    public void doGetPost_NoWorkflow(MCRServletJob job) throws Exception {
    	HttpServletRequest request = job.getRequest();
    	HttpServletResponse response = job.getResponse();
    	// read the XML data
        MCREditorSubmission sub = (MCREditorSubmission) (request.getAttribute("MCREditorSubmission"));
        org.jdom2.Document indoc = sub.getXML();

        // read the parameter
        MCRRequestParameters parms = sub.getParameters();
        
        String mcrid1 = parms.getParameter("mcrid");
        String type = parms.getParameter("type");
        long processID = Long.parseLong(parms.getParameter("processid"));
        String workflowType = parms.getParameter("workflowType");
        String step = parms.getParameter("step");
        String mcrid2 = parms.getParameter("mcrid2");
        String nextPath = parms.getParameter("nextPath");
        LOGGER.debug("mcrid1 = " + mcrid1);
        LOGGER.debug("type = " + type);
        LOGGER.debug("workflowType = " + workflowType);
        LOGGER.debug("step = " + step);
        LOGGER.debug("mcrid2 = " + mcrid2);
        LOGGER.debug("nextPath = " + nextPath);
        
        MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(workflowType);
        try{
       
     
        
        // get the MCRSession object for the current thread from the session
        // manager.
        MCRSession mcrSession = MCRServlet.getSession(request);
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

        MCRObjectID ID = MCRObjectID.getInstance(mmcrid);

        if (!ID.getTypeId().equals(type)) {
            ID = MCRObjectID.getInstance(mcrid1);
            hasid = false;
        }

        if (!hasid) {
            indoc.getRootElement().setAttribute("ID", ID.toString());
        }

        // create a metadata object and prepare it
        org.jdom2.Document outdoc;
        StringBuffer storePath = new StringBuffer(MCRWorkflowDirectoryManager.getWorkflowDirectory(ID.getTypeId()))
			.append("/").append(ID.toString()).append(".xml");
        
        	WFM.storeMetadata(MCRUtils.getByteArray(indoc, Format.getPrettyFormat()), ID.toString(), storePath.toString());
        	outdoc = prepareMetadata((org.jdom2.Document) indoc.clone(), ID, job, lang, step, 
        			   nextPath, storePath.toString(), workflowType, String.valueOf(processID), "person");
        	WFM.storeMetadata(MCRUtils.getByteArray(outdoc, Format.getPrettyFormat()), ID.toString(), storePath.toString());
        		
        	
        }catch(java.lang.IllegalStateException ill){
        	LOGGER.debug("because of error, forwarding to success page could not be executed [" + ill.getMessage() + "]");        	
        }catch(Exception e){
        	LOGGER.error("catched error:" , e);
        
        }
        finally{
        	
        	 request.getRequestDispatcher("/nav?path=" + nextPath).forward(request, response);
        }
  
    }
    
    
}

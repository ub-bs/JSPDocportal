package org.mycore.activiti.workflows;

import java.io.File;

import org.jdom2.Document;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;


/**
 * Stores the Metadata (JDOM Document) from XEditor into the MCRObject file in workflow directory
 * 
 * @author Robert Stephan
 */
public class MCRStoreMetadataServlet extends MCRServlet{
	private static final long serialVersionUID = 1L;

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		Document xml = (org.jdom2.Document) (job.getRequest().getAttribute("MCRXEditorSubmission"));
		String mcrID = xml.getRootElement().getAttributeValue("ID");
		MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrID);
		File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrObjID), mcrID+".xml");
		MCRJDOMContent content = new MCRJDOMContent(xml);
		content.sendTo(wfFile);
		
		job.getResponse().sendRedirect(MCRFrontendUtil.getBaseURL()+"showWorkspace.action?mcrobjid_base="+mcrObjID.getProjectId()+"_"+mcrObjID.getTypeId());
	}

}

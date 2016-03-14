package org.mycore.activiti.workflows;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.Document;
import org.mycore.activiti.MCRActivitiUtils;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.MCRFrontendUtil;


/**
 * Stores the Metadata (JDOM Document) from XEditor into the MCRObject file in workflow directory
 * 
 * @author Robert Stephan
 */
public class MCRStoreMetadataServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Document xml = (org.jdom2.Document) (req.getAttribute("MCRXEditorSubmission"));
		String mcrID = xml.getRootElement().getAttributeValue("ID");
		MCRObjectID mcrObjID = MCRObjectID.getInstance(mcrID);
		File wfFile = new File(MCRActivitiUtils.getWorkflowDirectory(mcrObjID), mcrID+".xml");
		MCRJDOMContent content = new MCRJDOMContent(xml);
		content.sendTo(wfFile);
		
		resp.sendRedirect(MCRFrontendUtil.getBaseURL()+"showWorkspace.action?mcrobjid_base="+mcrObjID.getProjectId()+"_"+mcrObjID.getTypeId());
	}
}

package org.mycore.frontend.workflowengine.jbpm;

import javax.servlet.http.HttpServletRequest;

import org.mycore.frontend.editor.MCREditorSubmission;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

public class MCRPassToTargetServlet extends MCRServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
	public void doGetPost(MCRServletJob job) throws Exception {
		// the target request parameter comes from the editor-include.jsp target parameter
		HttpServletRequest request = job.getRequest();
		String servletName = request.getParameter("target");
		if(servletName == null || servletName.equals("")){
			servletName = (String)request.getAttribute("target");
		}
		if(servletName == null || servletName.equals("")){
			servletName = ((MCREditorSubmission)request.getAttribute("MCREditorSubmission"))
				.getParameters().getParameter("target");
		}
		request.getRequestDispatcher(servletName).forward(request,job.getResponse());
	}

}

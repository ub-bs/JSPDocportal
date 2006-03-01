package org.mycore.frontend.workflowengine.jbpm;

import javax.servlet.http.HttpServletRequest;

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
		HttpServletRequest request = job.getRequest();
		String servletName = request.getParameter("target");
		request.getRequestDispatcher(servletName).forward(request,job.getResponse());
	}

}

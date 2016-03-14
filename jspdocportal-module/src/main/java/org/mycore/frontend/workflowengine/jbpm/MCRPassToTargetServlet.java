package org.mycore.frontend.workflowengine.jbpm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mycore.frontend.editor.MCREditorSubmission;

public class MCRPassToTargetServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method overrides doGetPost of MCRServlet. <br />
	 */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String servletName = request.getParameter("target");
		if(servletName == null || servletName.equals("")){
			servletName = (String)request.getAttribute("target");
		}
		if(servletName == null || servletName.equals("")){
			servletName = ((MCREditorSubmission)request.getAttribute("MCREditorSubmission"))
				.getParameters().getParameter("target");
		}
		request.getRequestDispatcher(servletName).forward(request, response);
	}

}

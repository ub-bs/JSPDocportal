package org.mycore.frontend.workflowengine.jbpm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.jdom.Document;
import org.mycore.common.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

public class MCRWorkflowEngineDoorServlet extends MCRServlet{

	private static Logger LOGGER = Logger.getLogger(MCRWorkflowEngineDoorServlet.class);
	private static String guestUserID = MCRConfiguration.instance().getString("MCR.users_guestuser_username");

	public void doGetPost(MCRServletJob job) throws Exception {
		
		HttpServletRequest request = job.getRequest();
		HttpServletResponse response = job.getResponse();
		
		String userID = MCRSessionMgr.getCurrentSession().getCurrentUserID();
		if(userID.equals(guestUserID)){
			//TODO redirect to registration/login/help
			return;
		}
		
		String workflowType = request.getParameter("workflowtype");
		LOGGER.debug("the workflowType = " + workflowType);

		if(workflowType == null || workflowType.equals("")) {
			// TODO redirect a list of allowed or current workflowTypes
		}
		

		workflowType = "xmetadiss";

		//org.jbpm.db.TaskMgmtSession.this.findPooledTaskInstances("heiko");
		//AbstractJbpmWorkflowProcess wp = new MCRXmetadissProcess();
		
//		ProcessDefinition definition = 
//			ProcessDefinition.parseXmlResource("workflow/" + workflowType + ".par/processdefinition.xml");
		
		
		Document unsavedJdom = (Document)request.getAttribute("unsavedJdom");
		if(unsavedJdom != null) {
			LOGGER.debug("root el = " + unsavedJdom.getRootElement().getName());
		}
		System.out.println(workflowType);
		

	}

}

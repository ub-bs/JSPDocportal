package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerInterface;

public class MCRCreateURNAction implements ActionHandler{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateURNAction.class);
	private static MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl("xmetadiss");

	public void execute(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		String urn = WFI.createURNReservation(initiator);
		if(urn != null && !urn.equals("")){
			contextInstance.setVariable("reservatedURN", urn);
			
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());	
		}else{
			logger.error("could not create urn");
			throw new MCRException("could not create urn");
		}
		
	}

}

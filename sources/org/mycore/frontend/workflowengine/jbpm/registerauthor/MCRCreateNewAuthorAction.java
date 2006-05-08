package org.mycore.frontend.workflowengine.jbpm.registerauthor;
        
import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmWorkflowBase;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowEngineManagerFactory;
        
public class MCRCreateNewAuthorAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCreateNewAuthorAction.class);
	private static MCRWorkflowEngineManagerRegisterauthor WFI = (MCRWorkflowEngineManagerRegisterauthor)MCRWorkflowEngineManagerFactory.getImpl("registerauthor");
//besser aufruf der Singleton-Instance: 
//MCRWorkflowEngineManagerRegisterauthor WFI = MCRWorkflowEngineManagerRegisterauthor.instance();
	public void execute(ExecutionContext executionContext) throws MCRException {
		ContextInstance contextInstance;
		contextInstance = executionContext.getContextInstance();
		String initiator = (String)contextInstance.getVariable(MCRJbpmWorkflowBase.varINITIATOR);
		long pid = executionContext.getProcessInstance().getId();
		String authorID = WFI.createNewAuthor(initiator, pid);
		logger.error(" ++++++++++++++++++++ in Create New Author Action");
		if(authorID != null && !authorID.equals("")) {
			contextInstance.setVariable("createdDocID", authorID);
			logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
		}else{
			logger.error("no authorID could be generated");
			throw new MCRException("no authorID could be generated");
		}
	}

}

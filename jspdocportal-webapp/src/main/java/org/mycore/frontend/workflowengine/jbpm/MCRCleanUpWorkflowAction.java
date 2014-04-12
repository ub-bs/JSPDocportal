package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;

public class MCRCleanUpWorkflowAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCleanUpWorkflowAction.class);
	
	private String varnameOBJID;
	private String varnameERROR;
	
	public void execute(ExecutionContext executionContext) throws MCRException {
		try{
			logger.debug("cleanup the workflow from the object ");
			MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(MCRJbpmWorkflowBase.getWorkflowProcessType(executionContext));
			String mcrid = (String)executionContext.getContextInstance().getVariable(varnameOBJID);
			if(!WFM.removeWorkflowFiles(executionContext.getContextInstance())) {					
				executionContext.getContextInstance().setVariable(varnameERROR, "error in committing object [" + mcrid + "]");
			}
			else{
				//everything is OK
				// delete the whole process
            	//leads to exception - confused with different jbpmcontext instances, ... (the usual friends)
           		// WFM.deleteWorkflowProcessInstance(executionContext.getContextInstance().getId());
				executionContext.getJbpmContext().getGraphSession().deleteProcessInstance(executionContext.getProcessInstance());
			}
		
		}catch(Exception e){
			String errMsg = "could not cleanup the workflow from  object";
			executionContext.getContextInstance().setVariable(varnameERROR, errMsg);
			logger.error(errMsg, e);
			throw new MCRException(errMsg);
		}
	}
}

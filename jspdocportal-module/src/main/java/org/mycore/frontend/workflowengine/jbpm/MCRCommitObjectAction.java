package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;

public class MCRCommitObjectAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCommitObjectAction.class);
	
	protected String varnameOBJID;
	protected String varnameERROR;
	
	public void execute(ExecutionContext executionContext) throws MCRException {
		try{
			logger.debug("committing a object to the database");
			MCRWorkflowManager WFM = MCRWorkflowManagerFactory.getImpl(MCRJbpmWorkflowBase.getWorkflowProcessType(executionContext));
			String mcrid = (String)executionContext.getVariable(varnameOBJID);
			if(varnameERROR == null)
				varnameERROR = "ERROR";
			if(!WFM.commitWorkflowObject(executionContext.getContextInstance())){
				executionContext.setVariable(varnameERROR, "error in committing object [" + mcrid + "]");
			}
		}catch(Exception e){
			String errMsg = "could not commit object";
			executionContext.setVariable(varnameERROR, errMsg);
			logger.error(errMsg, e);
			throw new MCRException(errMsg);
		}
	}
}

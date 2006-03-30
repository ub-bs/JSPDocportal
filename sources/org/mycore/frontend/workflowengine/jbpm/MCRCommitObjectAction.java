package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;

public class MCRCommitObjectAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRCommitObjectAction.class);
	
	private String varnameOBJID;
	private String varnameERROR;
	private String varnameDOCTYPE;
	
	public void execute(ExecutionContext executionContext) throws MCRException {
		try{
			logger.debug("committing a object to the database");
			MCRWorkflowEngineManagerInterface WFI = MCRWorkflowEngineManagerFactory.getImpl(executionContext.getProcessDefinition().getName());
			String documentType = "";
			String mcrid = (String)executionContext.getVariable(varnameOBJID);
			if(varnameERROR == null)
				varnameERROR = "ERROR";
			if(varnameDOCTYPE == null || varnameDOCTYPE.equals("")){
				documentType = mcrid.split("_")[1];
			}else{
				documentType = (String)executionContext.getVariable(varnameDOCTYPE);
			}
			if(!WFI.commitWorkflowObject(mcrid, documentType)){
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

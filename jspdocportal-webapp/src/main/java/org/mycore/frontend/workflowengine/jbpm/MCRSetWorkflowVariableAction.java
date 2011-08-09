package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;

public class MCRSetWorkflowVariableAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRSetWorkflowVariableAction.class);
	
	private String varname;
	private String value;

	public void execute(ExecutionContext executionContext) throws MCRException {
		String workflowVarname = varname;
		executionContext.setVariable(workflowVarname, value);		
		logger.debug("setting workflow variable: " + workflowVarname + "=" + value);
		return;
	}

}

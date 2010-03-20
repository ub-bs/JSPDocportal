package org.mycore.frontend.workflowengine.jbpm;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRException;

public abstract class MCRAbstractAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(MCRAbstractAction.class);
	
	public abstract void executeAction(ExecutionContext executionContext);
	
	public void execute(ExecutionContext executionContext) throws MCRException {
		executionContext.getJbpmContext().getSession().flush();
		executeAction(executionContext);
		executionContext.getJbpmContext().getSession().flush();
		logger.debug("workflow changed state to " + executionContext.getProcessInstance().getRootToken().getName());
	}

}

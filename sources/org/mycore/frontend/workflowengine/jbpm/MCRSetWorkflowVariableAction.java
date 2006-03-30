package org.mycore.frontend.workflowengine.jbpm;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.mycore.common.MCRConfigurationException;
import org.mycore.common.MCRException;

public class MCRSetWorkflowVariableAction implements ActionHandler{
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MCRSetWorkflowVariableAction.class);
	
	private String varname;
	private String value;

	public void execute(ExecutionContext executionContext) throws MCRException {
		String workflowVarname = varname;
		int sep = varname.indexOf("@");
		if(sep != -1){
			String classname = "";
			Class cl = null;
	        try {
	        	classname = varname.substring(sep + 1);
	        	cl = Class.forName(classname);
	            Field field = cl.getField(varname.substring(0,sep));
	            workflowVarname = (String)field.get(field);
	        } catch (Exception ex) {
	            throw new MCRConfigurationException("Could not load class " + classname, ex);
	        }
		}
		executionContext.setVariable(workflowVarname, value);
		logger.debug("setting workflow variable: " + workflowVarname + "=" + value);
	}

}

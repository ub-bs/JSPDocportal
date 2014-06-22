package org.mycore.activiti.workflows.create_object_simple.delegates;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MCRActivitiCancelObjectDelegate implements JavaDelegate {
	  
	  public void execute(DelegateExecution execution) throws Exception {
	    String var = (String) execution.getVariable("input");
	    var = var.toUpperCase();
	    execution.setVariable("input", var);
	  }
	  
	}
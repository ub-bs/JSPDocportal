package org.mycore.frontend.workflowengine.jbpm;

import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * 
 * @author Robert Stephan
 * Manager for WorkflowProcessInstances
 * implements singleton interface
 *
 */
public class MCRWorkflowProcessManager {
		private static Logger logger = Logger.getLogger(MCRWorkflowProcessManager.class.getName());
		 private static MCRWorkflowProcessManager _instance; 
		 protected Hashtable workflowprocesses;//key:Long //value: MCRWorkflowProcess
		 
		 private MCRWorkflowProcessManager() { 
		  workflowprocesses = new Hashtable();
		 } 

		 // For lazy initialization 
		 public static synchronized MCRWorkflowProcessManager getInstance() { 
		  if (_instance==null) { 
		   _instance = new MCRWorkflowProcessManager(); 
		  } 
		  return _instance; 
		 } 
		  
		 
			/**
			 * creates and returns a new workflow process of a given type
			 * 	use this function <b>just</b> in initWorkflowProcess
			 * @param workflowProcessType
			 * @return
			 */
			final protected MCRWorkflowProcess createWorkflowProcess(String workflowProcessType){
				
				MCRWorkflowProcess wfp = new MCRWorkflowProcess(workflowProcessType);
				workflowprocesses.put(new Long(wfp.getProcessInstanceID()), wfp);
				return wfp;
			}
			
			final public MCRWorkflowProcess getWorkflowProcess(long processID){
				logger.warn("getting MCRWorkflowProcess: "+processID);
				MCRWorkflowProcess wfp = (MCRWorkflowProcess) workflowprocesses.get(new Long(processID));
				if(wfp==null || wfp.wasClosed()){
					wfp = new MCRWorkflowProcess(processID);
					workflowprocesses.put(new Long(processID), wfp);
				}	
				else{
					wfp.save();
					
				}
				return wfp;
			}
				final public void removeWorkflowProcess(MCRWorkflowProcess wfp){
					logger.warn("closing MCRWorkflowProcess: "+wfp.getProcessInstanceID());
					workflowprocesses.entrySet().remove(wfp);
					
			}
}
	



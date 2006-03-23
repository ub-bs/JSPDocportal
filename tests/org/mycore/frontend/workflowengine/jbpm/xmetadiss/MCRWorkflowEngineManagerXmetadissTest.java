/**
 * $RCSfile$
 * $Revision$ $Date$
 *
 * This file is part of ** M y C o R e **
 * Visit our homepage at http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, normally in the file license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 *
 **/

// package
package org.mycore.frontend.workflowengine.jbpm.xmetadiss;

// Imported java classes
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.mycore.common.MCRException;


/**
 * This class holds methods to manage the workflow file system of MyCoRe.
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */

public class MCRWorkflowEngineManagerXmetadissTest extends TestCase{
	
	private static Logger logger = Logger.getLogger(MCRWorkflowEngineManagerXmetadiss.class.getName());
	private static String processType = "xmetadiss" ;
	
	private static JbpmConfiguration jbpmConfiguration = 
        JbpmConfiguration.parseResource("jbpm.cfg.xml");

	
	public void testDecisionNodeCanBeSubmitted() {
		  JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
			try{
				GraphSession graphSession = jbpmContext.getGraphSession();
				ProcessDefinition processDefinition = graphSession.findLatestProcessDefinition(processType);
				ProcessInstance processInstance = processDefinition.createProcessInstance();
				long pid = processInstance.getId();
				ContextInstance contextInstance = processInstance.getContextInstance();
				contextInstance.setVariable("initiator", "author2A");
				contextInstance.setVariable("authorID", "DocPortal_author_08033302");
				contextInstance.setVariable("reservatedURN", "urn:dummyprefix-diss20060322-1948161568");
				contextInstance.setVariable("createdDocID", "DocPortal_disshab_99999999");
				contextInstance.setVariable("valid-DocPortal_disshab_99999999", "true");
				contextInstance.setVariable("attachedDerivates", "DocPortal_derivate_99999999");
				contextInstance.setVariable("containsPDF", "true");
				jbpmContext.save(processInstance);
				jbpmContext.close();
				jbpmContext = jbpmConfiguration.createJbpmContext();
				processInstance = jbpmContext.getGraphSession().loadProcessInstance(pid);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"processInitialized");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"authorCreated");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"urnCreated");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"disshabCreated");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"disshabSubmitted");
				jbpmContext.save(processInstance);
				jbpmContext.getGraphSession().deleteProcessInstance(processInstance);
			}catch(MCRException e){
				logger.error("error",e);
			}finally{
				jbpmContext.close();
			}		  
	  }		  
	
	public void testTaskNodeDisshabSubmitted() {
		  JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
			try{
				GraphSession graphSession = jbpmContext.getGraphSession();
				ProcessDefinition processDefinition = graphSession.findLatestProcessDefinition(processType);
				ProcessInstance processInstance = processDefinition.createProcessInstance();
				long pid = processInstance.getId();
				ContextInstance contextInstance = processInstance.getContextInstance();
				contextInstance.setVariable("initiator", "author2A");
				contextInstance.setVariable("authorID", "DocPortal_author_08033302");
				contextInstance.setVariable("reservatedURN", "urn:dummyprefix-diss20060322-1948161568");
				contextInstance.setVariable("createdDocID", "DocPortal_disshab_99999999");
				contextInstance.setVariable("valid-DocPortal_disshab_99999999", "true");
				contextInstance.setVariable("attachedDerivates", "DocPortal_derivate_99999999");
				contextInstance.setVariable("containsPDF", "true");
				jbpmContext.save(processInstance);
				jbpmContext.close();
				jbpmContext = jbpmConfiguration.createJbpmContext();
				processInstance = jbpmContext.getGraphSession().loadProcessInstance(pid);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"processInitialized");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"authorCreated");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"urnCreated");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"disshabCreated");
				jbpmContext.save(processInstance);
				processInstance.signal();
				assertEquals(processInstance.getRootToken().getNode().getName(),"disshabSubmitted");
				jbpmContext.save(processInstance);
				
				TaskInstance taskInstance1 = (TaskInstance)  
					processInstance.getTaskMgmtInstance()
		          		.getTaskInstances()
		          		.iterator().next();

				assertTrue(jbpmContext.getTaskMgmtSession().findPooledTaskInstances("editor1A").size() > 0);
				jbpmContext.getGraphSession().deleteProcessInstance(processInstance);
			}catch(MCRException e){
				logger.error("error",e);
			}finally{
				jbpmContext.close();
			}		  
	  }		  	
}

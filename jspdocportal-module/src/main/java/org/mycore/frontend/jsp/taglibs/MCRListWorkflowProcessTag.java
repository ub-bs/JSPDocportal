package org.mycore.frontend.jsp.taglibs;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.DOMOutputter;
import org.mycore.common.JSPUtils;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManagerFactory;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcess;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowProcessManager;

public class MCRListWorkflowProcessTag extends MCRSimpleTagSupport
{
	private static Logger LOGGER = LogManager.getLogger(MCRListWorkflowProcessTag.class);
    //input vars
	private String workflowProcessType;
	private String var;
	
	public void setVar(String inputVar) {
		var = inputVar;
		return;
	}

	public void setWorkflowProcessType(String workfowProcessType){
		this.workflowProcessType = workfowProcessType;
	}
	
	public void doTag() throws JspException, IOException {		
		PageContext pageContext = (PageContext) getJspContext();
        
    	MCRWorkflowManager WFM = null;
		try {
			WFM = MCRWorkflowManagerFactory.getImpl(workflowProcessType);
		} catch (Exception noWFM) {
			LOGGER.error("could not instantiate workflow manager", noWFM);
			return;
		}			
		List lpids = WFM.getCurrentProcessIDsForProcessType(workflowProcessType) ;
		
		org.jdom2.Element processlist = new org.jdom2.Element ("processlist");
		processlist.setAttribute("type", workflowProcessType);
		
		for (int i = 0; i < lpids.size(); i++) {
			long pid = ((Long)lpids.get(i)).longValue();
			org.jdom2.Element process = new org.jdom2.Element ("process");
			process.setAttribute("pid", String.valueOf(pid));
			process.setAttribute("status", WFM.getStatus(pid));
			MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(pid);
			java.util.Map allVars = wfp.getStringVariableMap();
			for ( Iterator it = allVars.keySet().iterator(); it.hasNext(); ) {
				String nextKey  =  (String) it.next();				
				Object nextVal  =  allVars.get(nextKey);
				org.jdom2.Element pvar = new org.jdom2.Element ("variable");
				pvar.setAttribute("name", nextKey);
				pvar.setAttribute("value", (nextVal!=null?nextVal.toString():""));
				process.addContent(pvar);
			}
			processlist.addContent(process);			
			wfp.close();
		}
		org.jdom2.Document result = new Document(processlist);			
		org.w3c.dom.Document domDoc = null;
		try {
			domDoc = new DOMOutputter().output(result);
		} catch (JDOMException e) {
			LOGGER.error("Domoutput failed: ", e);
		}
		
		if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
			JspWriter out = pageContext.getOut();
			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
				.append("found this process list:\r\n")
				.append(JSPUtils.getPrettyString(processlist))
				.append("--------------------\r\n");
			out.println(debugSB.toString());
		}
		
		pageContext.setAttribute(var, domDoc);
		return;

	}	  

}
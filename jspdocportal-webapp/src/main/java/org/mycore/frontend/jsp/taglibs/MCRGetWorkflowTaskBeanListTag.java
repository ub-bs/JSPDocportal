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
package org.mycore.frontend.jsp.taglibs;

// Imported java classes
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.jdom2.input.DOMBuilder;
import org.mycore.common.JSPUtils;
import org.mycore.common.MCRSession;
import org.mycore.common.MCRSessionMgr;
import org.mycore.frontend.workflowengine.jbpm.MCRJbpmTaskBean;
import org.mycore.frontend.workflowengine.jbpm.MCRWorkflowManager;


/**
 * 
 * @author Heiko Helmbrecht, Anja Schaar
 * @version $Revision$ $Date$
 */

public class MCRGetWorkflowTaskBeanListTag extends SimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRGetWorkflowTaskBeanListTag.class.getName());
	private static final String sessionBeanListName = "MCRGETWORKFLOWTASKBEANLIST-BEANLIST";
	
	private String var;
	private int size;
	private String varTotalSize;
	private int offset;
	private String mode;
	
	private String workflowTypes;
	
	private String debugUser;
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setVarTotalSize(String varTotalSize){
		this.varTotalSize = varTotalSize;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
	public void setMode(String mode){
		this.mode = mode;
	}
	
	public void setWorkflowTypes(String workflowTypes){
		this.workflowTypes = workflowTypes;
	}
	
	public void setDebugUser(String debugUser){
		this.debugUser = debugUser;
	}
	
	@SuppressWarnings("unchecked")
	public void doTag() throws JspException, IOException {
		try{
			PageContext pageContext = (PageContext) getJspContext();
			HttpSession jspSession = pageContext.getSession();
			
			MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
			String userid = mcrSession.getUserInformation().getUserID();
			
			if(debugUser != null && !debugUser.equals("")){
				userid = debugUser;
				LOGGER.debug("debug tasklist for user = " + userid);
			}else{
				LOGGER.debug("current Session for workflow tasklist = " + mcrSession.getID());
				LOGGER.debug("current user for workflow tasklist = " + userid);
			}
			
			if(mode == null) mode = "";
			List workflowProcessTypes = new ArrayList();
			if(workflowTypes != null && !workflowTypes.equals("")){
				workflowProcessTypes = Arrays.asList(workflowTypes.split(","));				
			}
			List beans = null;
			if(offset > 0){
				beans = (List)jspSession.getAttribute(sessionBeanListName);
			}else{
				beans = MCRWorkflowManager.getTasks(userid, mode, workflowProcessTypes);
				jspSession.setAttribute(sessionBeanListName, beans);
			}
			
			if(size == 0) size = 20;
			List displayedBeans = new ArrayList();
			for (int i = offset; i < offset + size && i < beans.size(); i++) {
				displayedBeans.add(beans.get(i));
			}
			
			// resort by prozessid
			Collections.sort(displayedBeans,				
					new Comparator<Object>() {
						public int compare(Object o1, Object o2) {
				        long id1 = ((MCRJbpmTaskBean)o1).getProcessID();
				        long id2 = ((MCRJbpmTaskBean)o2).getProcessID();
				        return -1*(new Long(id1)).compareTo(new Long(id2));		        
						}
					}
				);

			
			pageContext.setAttribute(var, displayedBeans);
			pageContext.setAttribute(varTotalSize,String.valueOf(beans.size()));
			if(pageContext.getAttribute("debug") != null && pageContext.getAttribute("debug").equals("true")) {
    			JspWriter out = pageContext.getOut();
    			DOMBuilder builder = new DOMBuilder();
    			StringBuffer debugSB = new StringBuffer("<textarea cols=\"80\" rows=\"30\">")
    				.append("found these workflow java beans");
    			int i = 0;
    			
    			for (Iterator it = displayedBeans.iterator(); it.hasNext();) {
					MCRJbpmTaskBean bean = (MCRJbpmTaskBean) it.next();
					
					debugSB.append("JAVABEAN ").append(i++).append("\r\n")
						.append("processID=").append(bean.getProcessID()).append("\r\n")
						.append("taskName=").append(bean.getTaskName()).append("\r\n")
						.append("workflowStatus=").append(bean.getWorkflowStatus()).append("\r\n")
						.append("WorkflowProcessType=").append(bean.getWorkflowProcessType()).append("\r\n")
						.append("variables=\r\n").append(JSPUtils.getPrettyString(builder.build(bean.getVariables()))).append("\r\n");
					
				}
    			debugSB.append("</textarea>");
    			out.println(debugSB.toString());
    		}			
		}catch(Exception e){
			LOGGER.error("catched error", e);
		}
	}

}


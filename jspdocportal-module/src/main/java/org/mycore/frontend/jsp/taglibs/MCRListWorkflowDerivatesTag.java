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

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.output.DOMOutputter;
import org.mycore.frontend.workflowengine.strategies.MCRDefaultDerivateStrategy;
import org.mycore.frontend.workflowengine.strategies.MCRWorkflowDirectoryManager;

/**
 * 
 * @author Heiko Helmbrecht
 * @version $Revision$ $Date$
 */

public class MCRListWorkflowDerivatesTag extends MCRSimpleTagSupport {
	private static Logger LOGGER = Logger.getLogger(MCRListWorkflowDerivatesTag.class.getName());
	private String varDom;
	private String derivates;
	private String docID;
	private String scope;
	private String workflowprocesstype;

	public void setVarDom(String varDom){
		this.varDom = varDom;
	}
	
	public void setDerivates(String derivates) {
		this.derivates = derivates;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}
	
	public void setScope(String scope){
		this.scope = scope;
	}
	
	public void setWorkflowprocesstype(String workflowprocesstype) {
		this.workflowprocesstype = workflowprocesstype;
	}
	
	public void doTag() throws JspException, IOException {
		try{
			int iScope = getScope(scope);
			JspContext jspContext = getJspContext();
			MCRDefaultDerivateStrategy derStrategy = new MCRDefaultDerivateStrategy();

			String derivateDirectory = MCRWorkflowDirectoryManager.getWorkflowDirectory(workflowprocesstype);
			
			Element elDerivates = new Element("derivates");		
			if(derivates != null && !derivates.equals("")){
				String[] arDerivates = derivates.split(",");
				for (int i = 0; i < arDerivates.length; i++) {
					if ( arDerivates[i].trim().length()>0){
						Element elDerivate = derStrategy.getDerivateData(derivateDirectory, docID, arDerivates[i]);
						elDerivates.addContent(elDerivate);
					}
				}
			}
			jspContext.setAttribute(varDom, 
					new DOMOutputter().output(new org.jdom2.Document(elDerivates)),
					iScope);
		}catch(Exception e){
			LOGGER.error("could not list derivates of " + docID);
		}
	}
}


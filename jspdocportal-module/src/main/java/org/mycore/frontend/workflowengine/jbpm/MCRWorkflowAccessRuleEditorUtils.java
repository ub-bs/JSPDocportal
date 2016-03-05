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

package org.mycore.frontend.workflowengine.jbpm;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;
import org.mycore.access.MCRAccessManager;
import org.mycore.access.mcrimpl.MCRAccessRule;
import org.mycore.access.mcrimpl.MCRAccessStore;
import org.mycore.access.mcrimpl.MCRRuleStore;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.MCRException;
import org.mycore.common.content.MCRStringContent;
import org.mycore.common.xml.MCRXMLParserFactory;

/**
 * This class holds methods for the simple accessrule editor.
 * the UI for the editor can be found in accessruleeditor.jsp
 * 
 * 
 * @author Robert Stephan
 * @version $Revision$ $Date$
 * 
 */

public class MCRWorkflowAccessRuleEditorUtils { 
	//private static JbpmConfiguration jbpmConfiguration =   JbpmConfiguration.parseResource("jbpm.cfg.xml");
	private static Logger logger = Logger
			.getLogger(MCRWorkflowAccessRuleEditorUtils.class.getName());
	
	protected static MCRConfiguration config = MCRConfiguration.instance();

	private static String[] defaultRules = new String[]{};
	
	//static initializer block
	static{
		String defaultRuleString = config.getString("MCR.AccessRuleEditor.defaultrules");
		defaultRules = defaultRuleString.split(",");
		for(int i=0;i<defaultRules.length;i++){
			defaultRules[i]=defaultRules[i].trim();
		}
	}
	
	/**
	 * saves the access rule 
	 * that means it creates a new xml rule string and updates the workflow variables
	 * @param oid - the MCRObject ID
	 * @param rulename - the name of the rule
	 * @param processid - the process id
	 * @param parameter - parameters set in the UI
	 */
	public final static void saveAccessRule(String mcrid, String rulename, String processid, String[] parameter){
		boolean isValidRulename=false;
		for(int i=0;i<defaultRules.length;i++){
			if(rulename.equals(defaultRules[i])){ 
				isValidRulename=true;
				break;
			}
		}
		if(!isValidRulename) return;
		
		String rawRuleString = config.getString("MCR.AccessRuleEditor.defaultrules."+rulename);
		Element eRule=null;
		try{
			SAXBuilder builder = new SAXBuilder();
			eRule = builder.build(new StringReader(rawRuleString)).getRootElement();
			if(rulename.equals("groups")){
				Element e = XPathFactory.instance().compile(".//condition[@value='${group}']", Filters.element()).evaluateFirst(eRule);
				if(e!=null && parameter!=null && !parameter.equals("")){
					Element p = e.getParentElement();
					p.removeChildren(e.getName());
					for(int i=0;i<parameter.length;i++){
						e = (Element) e.clone();
						logger.info(i+": "+parameter[i]);
						e.setAttribute("value", parameter[i]);
						p.addContent(e);
					}
				}
			}
		} catch(Exception e) {
			logger.debug("Error parsing Condition from Properties (MCR.AccessRuleEditor.defaultrules.)"+rulename, e);
			return;
		}	
		
		long lpid = Long.parseLong(processid);
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(lpid);
		setStringVariableInWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_TYPE, rulename, wfp.getContextInstance());
		XMLOutputter xmlOut = new XMLOutputter();
		MCRWorkflowUtils.setLargeStringVariableInWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_XMLSTRING, xmlOut.outputString(eRule), wfp.getContextInstance());
		wfp.close();
		
 	}
	
	/**
	 * returns the default rules - the rules which are predefined in the configuration
	 * @return
	 */
	public static String[] getDefaultRules(){
		return defaultRules;
	}
	
	/**
	 * returns the rule that is currently set
	 * @param oid - the MCRObjectID
	 * @param processid - the ProcessID
	 * @return
	 * 
	 */
	public static String getCurrentRule(String oid, String processid){
		long lpid = Long.parseLong(processid);
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(lpid);
		String rule="";
		try{
		 rule= getStringVariableFromWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_TYPE, wfp.getContextInstance());
		if(rule == null){
			rule = defaultRules[0];
			wfp.getContextInstance().setVariable(MCRWorkflowConstants.WFM_VAR_READRULE_TYPE, rule);
		}
		}
		catch(Exception e){
			logger.error("caught exception: "+e);
		}
		finally{
			wfp.close();
		}
		return rule;
	}
	
	
	/**
	 * returns an array of group names which are associated the rule (should be selected)
	 * @param oid - the MCRObjectID
	 * @param processid - the ProcessID
	 * @return
	 */
	public static String[] getChoosenGroups(String oid, String processid){
		long lpid = Long.parseLong(processid);
		MCRWorkflowProcess wfp = MCRWorkflowProcessManager.getInstance().getWorkflowProcess(lpid);
		try{
			String ruleRawXML = MCRWorkflowUtils.getLargeStringVariableFromWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_XMLSTRING, wfp.getContextInstance());
			Element eRule = (Element)MCRXMLParserFactory.getParser(false).parseXML(new MCRStringContent(ruleRawXML)).getRootElement().detach();
			
			ArrayList<String> listResults = new ArrayList<String>();
			for(Element e: XPathFactory.instance().compile(".//condition[@field='group']", Filters.element()).evaluate(eRule)){
				listResults.add(e.getAttributeValue("value"));
			}
			return (String[])listResults.toArray(new String[]{});
		}catch(Exception e){
			return new String[]{};
		}
		finally{
			wfp.close();
		}
	}
	
	/**
	 * returns a variable from Workflow;
	 * @param varName
	 * @param processID
	 * @return
	 */
	private static String getStringVariableFromWorkflow(String varName, ContextInstance ctxI){

		try{
			Object result = ctxI.getVariable(varName);
			if(result==null){
				return null;
			}
			else{
				return result.toString();
				
			}
		}catch(MCRException e){
			logger.error("could not get workflow variable [" + varName + "] for process [" + ctxI.getProcessInstance().getId()+ "]",e);
		}finally{

		}	
		return "";		
	}

	
	/**
	 * sets a workflow variable
	 * @param varName - the name of the variable
	 * @param varValue - the value of the variable
	 * @param processID - the ProcessID
	 */
	private static void setStringVariableInWorkflow(String varName, String varValue, ContextInstance ctxI){
		
		//JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try{
			//ContextInstance ctxI = jbpmContext.loadProcessInstance(processID).getContextInstance();
			ctxI.setVariable(varName, varValue);				
		}catch(MCRException e){
			logger.error("could not get workflow variable [" + varName + "] for process [" + ctxI.getProcessInstance().getId()+ "]",e);
		}finally{
			//jbpmContext.close();
		}			
	}

	/**
	 * retrieves an AccessRule from the Database and
	 * sets the workflow variables, that the AcessRule can be edited
	 * called from MCRWorkflowManager.initWorkflowProcessForEditing()
	 * @param oid -the MCRObjectID
	 * @param processid - the ProcessID
	 */
	public static void setWorkflowVariablesForAccessRuleEditor(String oid, ContextInstance ctxI){
		MCRAccessStore accessstore = MCRAccessStore.getInstance();
		String ruletype=defaultRules[0];
		String xmlRuleString = config.getString("MCR.AccessRuleEditor.defaultrules."+ruletype);
		if(MCRAccessManager.hasRule(oid, "read")){
			String ruleID= accessstore.getRuleID(oid, "read");
			if(ruleID.startsWith(MCRWorkflowConstants.ACCESSRULE_PREFIX)){
				String test = ruleID.split("_")[1];
				for(int i=0;i<defaultRules.length;i++){
					if(defaultRules[i].toUpperCase(Locale.GERMAN).equals(test)){
						ruletype=defaultRules[i];
					}
				}	
			}
			
			MCRAccessRule mcrRule= MCRRuleStore.getInstance().getRule(ruleID);
			//create a "complete" rule
			Element content = mcrRule.getRule().toXML();
			Element rule = new Element("condition");
			rule.setAttribute("format", "xml");
			if(content.getName().equals("boolean")){
				rule.addContent(content);
			}
			else{
				Element bool = new Element("boolean");
				bool.setAttribute("operator", "and");
				rule.addContent(bool);
				bool.addContent(content);
			}
			XMLOutputter outputter = new XMLOutputter();
			xmlRuleString = outputter.outputString(rule);
		}	
		setStringVariableInWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_TYPE, ruletype, ctxI);
		MCRWorkflowUtils.setLargeStringVariableInWorkflow(MCRWorkflowConstants.WFM_VAR_READRULE_XMLSTRING, xmlRuleString, ctxI);
	}
}

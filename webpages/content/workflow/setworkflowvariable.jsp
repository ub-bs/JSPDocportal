<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<%@page import="org.mycore.frontend.editor.helper.MCRGetEditorElements"%>
<%@page import="org.jdom.Element"%>
<%@page import="org.jdom.output.XMLOutputter"%>

<%@page import="org.mycore.common.MCRSessionMgr"%>
<%@page import="org.jdom.Namespace"%><c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="processid" value="${requestScope.task.processID}" />
<c:set var="workflowType" value="${param.workflowType}" />
<c:set var="endTask" value="${param.endTask}" />
	
	  
   <form action="${baseURL}setworkflowvariable" accept-charset="utf-8" name="setvar" >
     <input name="dispatcherForward" value="/nav?path=~${workflowType}" type="hidden" />
   	 <input name="transition" value="" type="hidden" />
     <input name="endTask" value="${endTask}" type="hidden" />
     <input name="processID" value="${requestScope.task.processID}" type="hidden" />
     <c:choose>
	     <c:when test="${endTask eq 'taskGetInitiatorsEmailAddress'}" >
	    	 <input name="jbpmVariableNames" value="initiatorEmail" type="hidden" />
		   	 <input type="text" size="80" name="initiatorEmail" />
	     	 <input name="submit" type="submit" onclick="return checkEmail();" value="<fmt:message key="WF.common.Send" />"/>      
	     </c:when>
	     <c:when test="${endTask eq 'taskentermessagedata'}" >
	         <input name="jbpmVariableNames" value="tmpTaskMessage" type="hidden" /> 
		     <textarea name="tmpTaskMessage" cols="50" rows="4"   >Sie müssen noch...</textarea>  	     
   	 		 <input name="submit" type="submit"  onchange="return checkText();" value="<fmt:message key="WF.common.Send" />"/>      
		 </c:when>
	     <c:when test="${endTask eq 'getEndOfSuspensionDate'}" >
	         <input name="jbpmVariableNames" value="endOfSuspension" type="hidden" /> 
		     <input type="text" size="80" name="endOfSuspension" />
	     	 <input name="submit" type="submit" onclick="return checkDate();" value="<fmt:message key="WF.common.OK" />"/>      
		 </c:when>
		 

		 <c:when test="${endTask eq 'taskGetPublicationType'}" >
	         <input name="jbpmVariableNames" value="/publication/Type" type="hidden" /> 
	         <select size="1" name="/publication/Type" tabindex="1">
			<%-- <option value="TYPE0001.001">Monographie []</option><option value="TYPE0001.002">Konferenzband []</option><option value="TYPE0001.003">Schriftenreihe []</option><option value="TYPE0001.004">Zeitschrift []</option><option value="TYPE0001.005">Musikalie []</option><option value="TYPE0001.006">Patent []</option><option value="TYPE0001.007">Karte []</option><option value="TYPE0002.001">Aufsatz/Artikel []</option><option value="TYPE0002.002">Rezension []</option><option value="TYPE0010.001">Rede/Vortrag []</option><option value="TYPE0010.002">Sonstiges []</option></select>       --%>
			
				
				<%MCRGetEditorElements helper = new MCRGetEditorElements();
				Element content = helper.resolveElement("localclass:org.mycore.frontend.editor.helper.MCRGetEditorElements?mode=getSpecialCategoriesInItems&amp;classProp=MCR.ClassificationID.Type&amp;categoryProp=MCR.Classification.Type.SelectCategoryIDs.publication");
				String currentLang = MCRSessionMgr.getCurrentSession().getCurrentLanguage();
				for(Object o : content.getChildren()){
					Element e = (Element)o;
					out.write("<option value=\""+e.getAttributeValue("value")+"\">");
					String label = null;
					for(Object oLabel:e.getChildren("label")){
						Element eLabel = (Element)oLabel;
						if(eLabel.getAttributeValue("lang", Namespace.XML_NAMESPACE).equals(currentLang)){
							label = eLabel.getText(); 
						}
					}
					if(label==null){
						label=e.getAttributeValue("value"); 
					}
					out.write(label);
					out.write("</option>");
				}
				%>		     
					
				    
		       </select> &#160;&#160;
   	 		 <input name="submit" type="submit"  value="<fmt:message key="Editor.Common.button.Choose" />"/>      
		 </c:when>
	 
     </c:choose>
   </form>	
	     

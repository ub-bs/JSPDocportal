<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="processid" value="${requestScope.task.processID}" />
<c:set var="workflowType" value="${param.workflowType}" />
<c:set var="endTask" value="${param.endTask}" />
	
	  
   <form action="${baseURL}setworkflowvariable" accept-charset="utf-8" name="setvar" >
     <input name="dispatcherForward" value="/nav?path=~${workflowType}" type="hidden" />
   	 <input name="transition" value="" type="hidden" />
     <input name="endTask" value="taskGetInitiatorsEmailAddress" type="hidden" />
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
     </c:choose>
   </form>	
	     

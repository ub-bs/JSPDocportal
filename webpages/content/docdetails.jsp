
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page import="org.apache.log4j.Logger" %>
<c:catch var="e">
<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="mcrid" value="${param.id}" />
<c:set var="host" value="${param.host}" scope="request" />
<c:set var="offset" value="${param.offset}" />
<c:set var="size" value="${param.size}" />
<c:set var="debug" value="${param.debug}" />
<c:set var="print" value="${param.print}" />
<c:set var="from"  value="${param.fromWForDB}" /> 
<c:set var="path"  value="${param.path}" /> 
<c:set var="style"  value="${param.style}" /> 
<c:set var="type" value="${fn:split(mcrid,'_')[1]}"/>

<c:choose>
 <c:when test="${fn:contains(style,'user')}">
	<mcr:receiveUserAsJdom var="mycoreobject" />
	<c:set var="type" value="user"/>
 </c:when>
 <c:otherwise>
  	<mcr:receiveMcrObjAsJdom var="mycoreobject" mcrid="${mcrid}" fromWForDB="${from}" />
 </c:otherwise>
</c:choose>

<c:choose>
   <c:when test="${requestScope.host}">
      <c:set var="host" value="${requestScope.host}" />
   </c:when>
   <c:otherwise>
      <c:set var="host" value="local" />   
   </c:otherwise>
</c:choose>
<c:choose>
   <c:when test="${param.offset > 0}">
      <c:set var="offset" value="${param.offset}" />
   </c:when>
   <c:otherwise>
      <c:set var="offset" value="0" />
   </c:otherwise>
</c:choose>
<c:choose>
   <c:when test="${param.size > 0}">
      <c:set var="size" value="${param.size}" />
   </c:when>
   <c:otherwise>
     <c:set var="size" value="0" />
   </c:otherwise>
</c:choose>

<c:choose>
 <c:when test="${fn:contains(from,'workflow')}" >
     <c:set var="layout" value="preview" />
 </c:when>
 <c:otherwise>
     <c:set var="layout" value="normal" />
 </c:otherwise> 
</c:choose>

<mcr:checkAccess permission="read" var="readAccess" key="${mcrid}" />
<mcr:docDetails mcrObj="${mycoreobject}" var="docDetails" lang="${requestScope.lang}" style="${style}" />
	
<table class="${layout}" ><tr valign="top">
<td>
<table width="100%" >
 <tr>
   <td>
     <div class="headline">
		 <fmt:message key="OMD.${type}.title" />:

		 <x:forEach select="$docDetails//metaname[@name='OMD.maintitle']/metavalues/metavalue">
		 	<x:out select="./@text" escapeXml="./@escapeXml" />
		 </x:forEach> 
<!--      <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/titles/title[@xml:lang='${requestScope.lang}']" />
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/names/name/fullname" />
	     <mcr:simpleXpath jdom="${mycoreobject}" xpath="/user/@ID" /> -->
     </div>
   </td>
   <td width="30">&nbsp;</td>
   <td align="center" >
   	    <nobr>
         <mcr:browseCtrl id="${param.resultid}" offset="${offset}" >
            <c:if test="${!empty(lastHitID)}">
                <a href="${WebApplicationBaseURL}nav?path=${path}&id=${lastHitID}&offset=${offset -1}&resultid=${param.resultid}">&lt;&lt;</a>&#160;&#160;
            </c:if>
            <a href="${WebApplicationBaseURL}servlets/MCRJSPSearchServlet?mode=results&id=${param.resultid}">^</a>&#160;&#160;
            <c:if test="${!empty(nextHitID)}">
                <a href="${WebApplicationBaseURL}nav?path=${path}&id=${nextHitID}&offset=${offset +1}&resultid=${param.resultid}">&gt;&gt;</a>                        
            </c:if>
         </mcr:browseCtrl>
        </nobr>
   </td>
  </tr>

 <tr valign = "bottom" >
  <td>
  <mcr:session var="curUserID" method="get" type="userID"/>
  <x:forEach select="$docDetails">
  	  	<x:set var="currentID" select="./@ID" />
  </x:forEach>

   <c:choose>
   <c:when test="${readAccess or fn:contains(currentID, curUserID)}">
    <table cellspacing="0" cellpadding="0" id="metaData">
    <c:set var="exist" value="0" />                  
    
    <x:forEach select="$docDetails//metaname">        
		<x:set var="data" select="." scope="request" />
		<c:set var="type"><x:out select = "./@type" /></c:set>
		<x:choose>
			<x:when select="./@type = 'line'">
				<c:if test="${exist > 0}" >
					<jsp:include page="results-config/docdetailitems/docdetailitem-${type}.jsp" /> 
				</c:if>
			    <c:set var="exist" value="0" />  
			</x:when>
			<x:when select="./@type = 'image'">
					<%--Do nothing, handled separately --%>
			</x:when>
					
			<x:otherwise>
				<x:set var="exist" select="count(./metavalues)+count(./digitalobjects)" />
					<jsp:include page="results-config/docdetailitems/docdetailitem-${type}.jsp" /> 
				</x:otherwise>
			</x:choose>
       </x:forEach>   
    
    
    

   
   <!-- show link for this page -->
	<c:if test="${!fn:contains(style,'user')}">
    	<tr><td colspan="2" class="metanone">&nbsp;</td></tr>     
	   <tr>
   			<td class="metaname"> <fmt:message key="OMD.selflink" />:</td>
        	<td class="metavalue">
        		<table><tr><td>
	        		<a href="${WebApplicationBaseURL}metadata/${param.id}">
		          	${WebApplicationBaseURL}metadata/${param.id} </a>
	          	</td></tr></table>
			</td>
	   </tr> 
	</c:if>
    
  </table>
   
  </c:when>
  <c:otherwise>
	    <fmt:message key="Webpage.error.NoAccessRight" />  
  </c:otherwise>
  </c:choose>
  
 </td>
 <td>&nbsp;</td>
 <td align="center" >
     <c:if test="${empty(param.print) and !fn:contains(style,'user')}">
		     <a href="${WebApplicationBaseURL}content/print_details.jsp?id=${param.id}&from=${param.fromWForDB}" target="_blank">
	          	<img src="${WebApplicationBaseURL}images/workflow_print.gif" border="0" alt="<fmt:message key="WF.common.printdetails" />"  class="imagebutton" height="30"/>
	         </a>
   </c:if> 
 </td></tr></table>
</td></tr></table> 

</c:catch>
<c:if test="${e!=null}">
An error occured, hava a look in the logFiles!
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>

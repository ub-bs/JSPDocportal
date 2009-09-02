<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page import="org.apache.log4j.Logger" %>
<c:catch var="e">
<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="mcrid">
   <c:choose>
      <c:when test="${!empty(requestScope.id)}">${requestScope.id}</c:when>
      <c:otherwise>${param.id}</c:otherwise>
   </c:choose>
</c:set>
<c:set var="from"  value="${param.fromWForDB}" /> 
<c:set var="debug" value="${param.debug}" />
<c:set var="style" value="${param.style}" />
<c:set var="type"  value="${fn:split(mcrid,'_')[1]}" /> 
<c:choose>
 <c:when test="${fn:contains(from,'workflow')}" >
     <c:set var="layout" value="preview" />
 </c:when>
 <c:otherwise>
     <c:set var="layout" value="normal" />
 </c:otherwise> 
</c:choose>


<table class="${layout}" >
<tr valign="top">
<td>

<c:choose>
 <c:when test="${fn:contains(mcrid,'codice')}">
     <c:import url="content/results-config/docdetails-codice.jsp" />
 </c:when>
  <c:when test="${fn:contains(mcrid,'_artwork_')}">
     <c:import url="content/results-config/docdetails-artwork.jsp" />
 </c:when>
  <c:when test="${fn:contains(mcrid,'_artwork-person_')}">
     <c:import url="content/results-config/docdetails-artwork-person.jsp" />
 </c:when>
 
 
 <c:when test="${fn:contains(mcrid,'thesis')}">
     <c:import url="content/docdetails/docdetails_thesis.jsp" />
 </c:when>
 
 <c:when test="${fn:contains(mcrid,'disshab')}">
     <c:import url="content/docdetails/docdetails_disshab.jsp" />
 </c:when>
 
 
 <c:when test="${fn:contains(mcrid,'person')}">
     <c:import url="content/docdetails/docdetails_person.jsp" />
 </c:when>
 
  <c:when test="${fn:contains(mcrid,'institution')}">
     <c:import url="content/docdetails/docdetails_institution.jsp" />
 </c:when> 
 
  <c:when test="${fn:contains(mcrid,'document')}">
     <c:import url="content/docdetails/docdetails_document.jsp" />
 </c:when>

 <c:when test="${fn:contains(mcrid,'_series_')}">
     <c:import url="content/docdetails/docdetails_series.jsp" />
 </c:when>
 
 <c:when test="${fn:contains(mcrid,'_series-volume_')}">
     <c:import url="content/docdetails/docdetails_series-volume.jsp" />
 </c:when>
 
 <c:otherwise>
     <c:import url="content/results-config/docdetails-document.jsp" />
 </c:otherwise>
 </c:choose>

 </td>

 <td>&nbsp;</td>
 <td align="center" valign="top" style="padding-top: 20px">
     <c:if test="${empty(param.print) and !fn:contains(style,'user')}">
		     <a href="${WebApplicationBaseURL}content/print_details.jsp?id=${param.id}&from=${param.fromWForDB}" target="_blank">
	          	<img src="${WebApplicationBaseURL}images/workflow_print.gif" border="0" alt="<fmt:message key="WF.common.printdetails" />"  class="imagebutton" height="30"/>
	         </a>
     </c:if>
 
   <c:if test="${!(fn:contains(from,'workflow')) && !fn:contains(style,'user')}" > 
     <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />
     <mcr:isObjectNotLocked var="bhasAccess" objectid="${mcrid}" />
      <c:if test="${modifyAllowed}">
        <c:choose>
         <c:when test="${bhasAccess}"> 
	         <!--  Editbutton -->
	         <br />

	         <form method="get" action="${WebApplicationBaseURL}StartEdit" class="resort">                 
	            <input name="page" value="nav?path=~workflowEditor-${type}"  type="hidden">                                       
	            <input name="mcrid" value="${mcrid}" type="hidden"/>
					<input title="<fmt:message key="WF.common.object.EditObject" />" border="0" src="${WebApplicationBaseURL}images/workflow1.gif" type="image"  class="imagebutton" height="30" />
	         </form> 
         </c:when>
         <c:otherwise>
           <br />  <img title="<fmt:message key="WF.common.object.EditObjectIsLocked" />" border="0" src="${WebApplicationBaseURL}images/workflow_locked.gif" height="30" />
         </c:otherwise>
        </c:choose>  
        <!-- icon for pica export -->
         <c:if test="${!(fn:contains(type,'professorum'))}" > 
          	<br /><a href="${WebApplicationBaseURL}content/pica_export.jsp?id=${param.id}&from=${param.fromWForDB}" target="_blank">
          		<img src="${WebApplicationBaseURL}images/workflow_pica_export.gif" border="0" alt="<fmt:message key="WF.common.picaexport" />"  class="imagebutton" height="30"/>
           </a>        
         </c:if>
         <c:if test="${(fn:contains(type,'professorum'))}" > 
          	<br /> <a href="${WebApplicationBaseURL}servlets/CPR2RTFServlet?id=${param.id}" target="_blank">
          		<img src="${WebApplicationBaseURL}images/workflow_rtf_export.gif" border="0" alt="<fmt:message key="WF.common.rtfexport" />"  class="imagebutton" height="30"/>
           </a>        
         </c:if>
         
               
      </c:if>      
   </c:if>
 </td>
 </tr>
</table>
<br />
<%--
<mcr:imageViewerGetSupport derivID="DocPortal_derivate_000000000401" var="iviewlink" /> 
IviewLink(Liederbuch):&nbsp;[<c:out value="${iviewlink}" />]<br /> 

<mcr:imageViewerGetSupport derivID="atlibri_derivate_000000000003" var="iviewlink" /> 
IviewLink:&nbsp;[<c:out value="${iviewlink}" />]<br /> 

<mcr:imageViewer derivID="atlibri_derivate_000000000003" pathOfImage="${iviewLink}"
display="normal" height="500" width="800" scaleFactor="fitToWidth" style="image"/>  
<br />
<mcr:imageViewerGetEmbeddedThumbnail derivID="atlibri_derivate_000000000003" pathOfImage="\uni-haupt.jpg"/>
<br />
<c:set var="mylink"><mcr:imageViewerGetAddress derivID="DocPortal_derivate_000000000401" pathOfImage="${iviewLink}"
display="normal" height="500" width="800" scaleFactor="fitToWidth" style="image"/></c:set>

<a href="${mylink}">Vollbildmodus</a>

--%>

</c:catch>
<c:if test="${e!=null}">
An error occured, hava a look in the logFiles!
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>





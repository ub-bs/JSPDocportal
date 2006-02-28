<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<%@ page import="org.jdom.Document"%>

<mcr:session method="get" var="username" type="userID" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<mcr:getAuthorFromUser userid="${username}" var="authorobject" status="status"  />


<div class="headline">
   <fmt:message key="Nav.Application.mydiss" /> - 
   <fmt:message key="Nav.Application.mydiss.begin" />
</div>

<table cellspacing="3" cellpadding="3" >

<x:forEach select="$authorobject/mycoreobject" >
   <tr valign="top">
      <td class="metaname"><fmt:message key="SWF.Dissertation.Author" /> </td>
      <td class="metavalue">  
         <x:out select="./metadata/names/name/fullname" escapeXml="false" />
	  </td>
   </tr>  
   <tr valign="top" >
      <td class="metaname"><fmt:message key="SWF.Dissertation.Author.ID" /> </td>
      <td class="metavalue">  
         <x:out select="./@ID" />
	  </td>
   </tr>  
   <tr valign="top">
        <td class="metaname"><fmt:message key="SWF.Dissertation.URN" /> </td>
        <td class="metavalue">            
         <x:set var="mcrid" select="string(./@ID)" />
         <mcr:getURNForAuthor authorid="${mcrid}" status="status2"  urn="urn" />
         <b><c:out value="${urn}" /></b>
         <br/>
         <br/>
         <i><fmt:message key="SWF.Dissertation.URN.Hinweis" /></i>
	  </td>
   </tr>     
 </x:forEach>
 
   <tr>   
      <td class="metaname" >Ergebnis:</td>
      <td class="metavalue"><fmt:message key="SWF.Dissertation.${status}" /> </td>       
   </tr>    
   <tr>
      <td class="metaname" >Nächste Aktionen:</td>
      <td class="metavalue"><fmt:message key="SWF.Dissertation.next.${status}" /> </td>       
   </tr>    
   <tr><td colspan="2">
 	 <hr/>
	 <p><fmt:message key="Dissertation.Service.Hinweis1" /></p>
	 <p><fmt:message key="Dissertation.Service.Hinweis2" /></p>
     </td>
    </tr>
</table>

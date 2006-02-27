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
 <c:if test="${!empty(authorobject)}">
   <tr valign="top">
      <td class="metaname"><fmt:message key="SWF.Dissertation.Author" /> </td>
      <td class="metavalue">  
         <mcr:simpleXpath jdom="${authorobject}" xpath="/mycoreobject/metadata/names/name/fullname" />      
	  </td>
   </tr>  
   <tr valign="top" >
      <td class="metaname"><fmt:message key="SWF.Dissertation.Author.ID" /> </td>
      <td class="metavalue">  
         <mcr:simpleXpath jdom="${authorobject}" xpath="/mycoreobject/@ID" />
	  </td>
   </tr>  
   <tr valign="top">
        <!--  x:set var="mcrid" select="string($authorobject/mycoreobject/@ID)" / -->
        <td class="metaname"><fmt:message key="SWF.Dissertation.URN" /> </td>
        <td class="metavalue">  
         TESTID!!!<br/>
         <mcr:getURNForAuthor authorid="atlibri_author_000000000001" status="status2"  urn="urn" />
         <c:out value="${urn}" />
         <br/>
         <i><fmt:message key="SWF.Dissertation.URN.Hinweis" /></i>
	  </td>
   </tr>  
 </c:if>
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

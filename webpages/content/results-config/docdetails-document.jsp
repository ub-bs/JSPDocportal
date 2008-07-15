<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
<c:set var="host" value="local" scope="request" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:set var="mcrid" value="${param.id}" />
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
  <mcr:session var="curUserID" method="get" type="userID"/>
  <x:forEach select="$docDetails">
  	  	<x:set var="currentID" select="./@ID" />
  </x:forEach>
	
<table width="100%" >
 <tr>
   <td>
     <div class="headline">
		 <fmt:message key="OMD.headline" >
			 <fmt:param><x:out select="string($currentID)" /></fmt:param>
		 </fmt:message>
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

 <tr valign = "top" >
  <td>

   <c:choose>
   <c:when test="${readAccess or fn:contains(currentID, curUserID)}">
    <table cellspacing="0" cellpadding="0" id="metaData" >
    <tr>
    	<td class="metaname"> <fmt:message key="OMD.${type}.title" />:</td>
		
		<td class="metaheadline" colspan="2">
		<table border="0" cellpadding="0" cellspacing="4">

			<tbody><tr valign="top">
			   <td class="derivateHeading">
			   
			   	<x:forEach select="$docDetails//metaname[@name='OMD.parents']/parents/parent">
					      <x:set var="parentID"  select="string(./@parentID)" />							
								<jsp:include page="docdetailitems/docdetailitem-parents-sub.jsp" flush="true" >
				    				<jsp:param name="mcrid" value="${parentID}" />
								</jsp:include>				     
					</x:forEach>
			   		   
				   	<%--<x:forEach select="$docDetails//metaname[@name='OMD.maintitle']/metavalues/metavalue">
			 			<x:out select="./@text" escapeXml="./@escapeXml" /> 			 			
					</x:forEach> --%>
					 <c:set var="title"><mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/titles/title[@type='short']" /></c:set>
					<c:if test="${empty(title)}">
						<c:set var="title"><mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/titles/title[1]" /></c:set>
					</c:if>
					<c:out value="${title}" />
					
			</td></tr></tbody>
			</table>
			</td>
			
	</tr> 
	<tr><td /><td /><td rowspan="100" style="padding-left:20px;padding-top:20px" valign="top">
	<x:forEach select="$docDetails//metaname/digitalobjects/digitalobject[@derivlabel='Cover']">
 		<x:set var="URL" select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@derivmain)" />
 		<img src="${URL}" width="210" title="Cover" alt="Cover" />
 	</x:forEach>
	</td>
		<c:set var="volume"><mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/volumes/volume" /></c:set>
    	<x:forEach select="$docDetails//metaname[@name='OMD.parents']/parents/parent">
					      <x:set var="parentID"  select="string(./@parentID)" />							
								<jsp:include page="docdetailitems/docdetailitem-parent-root-sub.jsp" flush="true" >
				    				<jsp:param name="mcrid" value="${parentID}" />
				    				<jsp:param name="volume" value="${volume}" />
								</jsp:include>				     
					</x:forEach>
    <x:forEach select="$docDetails//metaname">        
				<x:set var="data" select="." scope="request" />
				<c:set var="type"><x:out select = "./@type" /></c:set>
				<x:choose>
					<x:when select="./@type = 'line'">
						<c:if test="${exist > 0}" >
							<jsp:include page="docdetailitems/docdetailitem-${type}.jsp" /> 
						</c:if>
					    <c:set var="exist" value="0" />  
					</x:when>
					<x:otherwise>
						<x:set var="exist1" select="count(./metavalues)+count(./digitalobjects)" />
						<c:set var ="exist" value="${exist+exist1}" />
						<jsp:include page="docdetailitems/docdetailitem-${type}.jsp" /> 
					</x:otherwise>
				</x:choose>
			
				

       </x:forEach>                 
   
   <!-- show link for this page -->
	<c:if test="${!fn:contains(style,'user')}">
    	<tr><td colspan="2" class="metanone">&nbsp;</td></tr>     
	   <tr>
   			<td class="metaname"> <fmt:message key="OMD.selflink" />:</td>
   			   			<td class="metavalue">
   				<c:set var="urn"><x:out select="$docDetails//metaname[@name='OMD.urns']/metavalues/metavalue/@text" /></c:set>
   				<table><tr><td>
   					<c:choose>
   						<c:when test="${not empty urn}">
   							<a href="${WebApplicationBaseURL}resolve?urn=${urn}">
	    			      	${WebApplicationBaseURL}resolve?urn=${urn} </a>
   						</c:when>
   						<c:otherwise>
		   					<a href="${WebApplicationBaseURL}metadata/${param.id}">
	    			      	${WebApplicationBaseURL}metadata/${param.id} </a>	
   						</c:otherwise>
  					</c:choose>
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
</tr>
</table>
 
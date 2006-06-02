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

<c:choose>
 <c:when test="${fn:contains(style,'user')}">
	<mcr:receiveUserAsJdom var="mycoreobject" />
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

<c:set var="type" value="${fn:split(mcrid,'_')[1]}"/>

<table class="${layout}" ><tr valign="top">
<td>
<table width="100%" >
 <tr>
   <td>
     <div class="headline">
      <fmt:message key="metaData.${type}.title" />:
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/titles/title[@xml:lang='${requestScope.lang}']" />
         <mcr:simpleXpath jdom="${mycoreobject}" xpath="/mycoreobject/metadata/names/name/fullname" />
     </div>
   </td>
   <td width="30">&nbsp;</td>
   <td align="center" >
   	    <nobr>
         <mcr:browseCtrl results="${sessionScope.lastMCRResults}" offset="${offset}" >
            <c:if test="${!empty(lastHitID)}">
                <a href="${WebApplicationBaseURL}nav?path=~docdetail&id=${lastHitID}&offset=${offset -1}">&lt;&lt;</a>&#160;&#160;
            </c:if>
            <a href="${WebApplicationBaseURL}nav?path=${sessionScope.lastSearchListPath}">^</a>&#160;&#160;
            <c:if test="${!empty(nextHitID)}">
                <a href="${WebApplicationBaseURL}nav?path=~docdetail&id=${nextHitID}&offset=${offset +1}">&gt;&gt;</a>                        
            </c:if>
          </mcr:browseCtrl>
         </nobr>
   </td>
  </tr>
 <tr valign = "bottom" ><td>
   <table cellspacing="0" cellpadding="0" id="metaData">
    <mcr:docDetails mcrObj="${mycoreobject}" var="docDetails" lang="${requestScope.lang}" style="${style}" />    
    <x:forEach select="$docDetails//metaname">
      <x:choose>
         <x:when select="./@type = 'space'">
            <tr>
               <td colspan="2" class="metanone">&nbsp;</td>
            </tr>                
         </x:when>
         <x:when select="./@type = 'standard'">
            <x:set var="nameKey" select="string(./@name)" />
            <tr>
               <td class="metaname"><fmt:message key="${nameKey}" />:</td>
               <td class="metavalue">
                  <x:forEach select="./metavalues">
                     <x:set var="separator" select="./@separator" />
                     <x:set var="terminator" select="./@terminator" />
                     <x:if select="string-length(./@introkey) > 0" >
                        <x:set var="introkey" select="string(./@introkey)" />
                        <fmt:message key="${introkey}" />
                     </x:if>
                     <x:forEach select="./metavalue">
                        <x:if select="generate-id(../metavalue[position() = 1]) != generate-id(.)">
                           <x:out select="$separator" escapeXml="false" />
                        </x:if>
                        <x:choose>
                           <x:when select="../@type = 'BooleanValues'">
                              <x:set var="booleanKey" select="concat(./@type,'-',./@text)" />
                              <fmt:message key="metaData.${booleanKey}" />
                           </x:when>
                           <x:when select="../@type = 'AuthorJoin'">
                              <x:set var="authorjoinKey" select="concat(./@type,'-',./@text)" />
                                 <a href="<x:out select="./@href" />" target="<x:out select="./@target" />"><fmt:message key="${authorjoinKey}" /></a>
                           </x:when>                                     
                           <x:when select="./@href != ''">
                              <a href="<x:out select="./@href" />" target="<x:out select="./@target" />"><x:out select="./@text" /></a>
                           </x:when>
                           <x:otherwise>
                              <x:out select="./@text" escapeXml="./@escapeXml" />
                           </x:otherwise>
                        </x:choose>
                     </x:forEach>
                     <x:if select="generate-id(../metavalues[position() = last()]) != generate-id(.)">
                        <x:out select="$terminator" escapeXml="false" />
                     </x:if>                               
                  </x:forEach>
                  <x:forEach select="./digitalobjects">
                     <table border="0" cellpadding="0" cellspacing="0" width="100%">
                        <tbody>
                        <x:forEach select="./digitalobject">
                           <tr>
                              <td align="left" valign="top">
                                 <div class="derivateBox">
                                    <div class="derivateHeading"><x:out select="./@derivlabel" /></div>
                                    <div class="derivate">
                                       <x:set var="mainFileURL" select="concat($WebApplicationBaseURL,'file/',./@derivid,'/',./@derivmain,'?hosts=',$host)" />
                                       <x:set var="contentType" select="string(./@contentType)" />
                                       <table>
                                          <tr>
                                             <td><a href="<x:out select="$mainFileURL" />" target="_self"><x:out select="./@derivmain" /></a>&#160;
                                                 (<x:out select="./@size mod 1024" /> kB)&#160;&#160;
                                             </td>
                                             <td>
                                                <a href="<x:out select="concat($WebApplicationBaseURL,'zip?id=',./@derivid)" />" class="linkButton" ><fmt:message key="OMD.zipgenerate" /></a>&#160;&#160;
                                             </td>
                                             <td>
                                                <a href="<x:out select="concat($WebApplicationBaseURL,'nav?path=~derivatedetails&derID=',./@derivid,'&docID=',$mcrid,'&hosts=',$host)" />" target="_self"><fmt:message key="OMD.details" />&gt;&gt;</a>&#160;&#160; 
                                             </td>
                                             <c:if test="${fn:contains('gif-jpeg-png', contentType)}">
                                                <td class="imageInResult"><a href="${mainFileURL}"><img src="${mainFileURL}" width="100"></a></td>
                                             </c:if>                                                 
                                          </tr>
                                       </table>
                                    </div>
                                 </div>
                              </td>
                           </tr>
                        </x:forEach>
                        </tbody>
                     </table>                               
                  </x:forEach>
               </td>
            </tr>        
         </x:when>
      </x:choose>
    </x:forEach>
  </table>
 </td>
 <td>&nbsp;</td>
 <td align="center" >
   <c:if test="${!(fn:contains(from,'workflow')) && !fn:contains(style,'user')}" > 
     <mcr:checkAccess var="modifyAllowed" permission="writedb" key="${mcrid}" />
      <c:if test="${modifyAllowed}">
         <!--  Editbutton -->
         <form method="get" action="${WebApplicationBaseURL}StartEdit" class="resort">                 
            <input name="page" value="nav?path=~workflowEditor-${type}"  type="hidden">                                       
            <input name="mcrid" value="${mcrid}" type="hidden"/>
			<input title="<fmt:message key="Object.EditObject" />" border="0" src="${WebApplicationBaseURL}images/workflow.gif" type="image"  class="imagebutton" />
         </form> 
      </c:if>
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
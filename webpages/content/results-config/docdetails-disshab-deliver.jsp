<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ page import="org.apache.log4j.Logger" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<%@page import="org.hibernate.Transaction"%>
<%@page import="org.mycore.backend.hibernate.MCRHIBConnection"%>
<html>
     <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>
         Abgabeformular - elektronische Dissertation 
        </title>
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_general.css" />
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_navigation.css" />
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_content.css" />
	</head>

<body>
<% Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction(); %>
<c:catch var="e">
<fmt:setLocale value='${requestScope.lang}'/>
<fmt:setBundle basename='messages'/>
<c:set var="mcrid" value="${param.id}" /> 
<c:set var="host" value="${param.host}" />
<c:set var="offset" value="${param.offset}" />
<c:set var="size" value="${param.size}" />
<c:set var="from" value="${param.fromWForDB}" />

<c:set var="debug" value="false" />

<mcr:receiveMcrObjAsJdom var="mycoreobject" mcrid="${mcrid}" fromWForDB="${from}" />
<mcr:docDetails mcrObj="${mycoreobject}" var="docDetails" lang="${requestScope.lang}" style="disshab-deliver" />

<table cellpadding="3" cellspacing="3"  width="90%">
<tr>
	<td id="contentArea" width="100%">
	<div id="contentWrapper">
		<div class="headline"> 
		   <fmt:message key="Webpage.intro.xmetadiss.deliver" />
		</div>
	  <mcr:includeWebContent file="workflow/form_disshab_deliver.html" />
	  
    <hr/>
	<table cellspacing="0" cellpadding="0" id="metaData">
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
               <td class="metaname"><b><fmt:message key="${nameKey}" />:</b></td>
               <td class="metavalue">
                  <x:forEach select="./metavalues">
                     <x:set var="separator" select="./@separator" />
                     <x:set var="terminator" select="./@terminator" />
                     <x:if select="string-length(./@introkey) > 0" >
                        <x:set var="introkey" select="string(./@introkey)" />
                        <b><fmt:message key="${introkey}" /></b>
                     </x:if>
                     <x:forEach select="./metavalue">
						<x:choose>
							   <x:when select="$separator = 'br'">
                                 <x:if select="generate-id(../metavalue[position() = 1]) != generate-id(.)">
                                  <br/>
                                 </x:if>
                               </x:when>
                               <x:when select="$separator = 'ul'"><ul></x:when>                                  
                               <x:when select="$separator = 'li'"><li></x:when>                                  
                               <x:otherwise>
                                <x:if select="generate-id(../metavalue[position() = 1]) != generate-id(.)">
                                   <x:out select="$separator" escapeXml="false" />
                                </x:if>
                              </x:otherwise>
                        </x:choose>                        
                           <x:choose>
                           <x:when select="../@type = 'BooleanValues'">
                              <x:set var="booleanKey" select="concat(./@type,'-',./@text)" />
                              <fmt:message key="${booleanKey}" />
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
                                                <a href="<x:out select="concat($WebApplicationBaseURL,'zip?id=',./@derivid)" />" class="linkButton" ><fmt:message key="zipgenerate" /></a>&#160;&#160;
                                             </td>
                                             <td>
                                                <a href="<x:out select="concat($WebApplicationBaseURL,'nav?path=~derivatedetails&derID=',./@derivid,'&docID=',$mcrid,'&hosts=',$host)" />" target="_self"><fmt:message key="details" />&gt;&gt;</a>&#160;&#160; 
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
  </div>
  <hr/>
  <table border="0" width="100%">
  <tr height="50"><td>&#160;</td></tr>	
  <tr><td class="metaname" ><u><b>Datum</b></u></td><td class="metaname" ><u><b>Unterschrift</b></u></td><td class="metaname" ><u><b>Ort</b></u></td></tr>
  </table>
  
</c:catch>
<c:if test="${e!=null}">
An error occured, hava a look in the logFiles!
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>
<% tx.commit(); %>
    </div>
   </td></tr>
  </table>
<hr/>
	<p>Bitte drucken Sie das Formular aus und geben es in der Dissertationsstelle der Universitätsbibliothek Rostock ab.</p>

</body>
</html>

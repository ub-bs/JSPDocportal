<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<c:set var="debug" value="false" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<c:choose>
   <c:when test="${param.offset > 0}">
      <c:set var="offset" value="${param.offset}" />
   </c:when>
   <c:otherwise>
      <c:set var="offset" value="0" />
   </c:otherwise>
</c:choose>
<c:choose>
   <c:when test="${param.size < 25}">
      <c:set var="size" value="${param.size}" />
   </c:when>
   <c:otherwise>
     <c:set var="size" value="10" />
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
<c:set var="len" value="0" />
<c:set var="lang" value="${requestScope.lang}" />
<c:set var="navPath" value="${requestScope.path}" />
<c:set var="query" value="${requestScope.query}" />
<c:set var="resultlistType" value="${requestScope.resultlistType}" />

<mcr:setMyResultList var="resultList" query="${query}" navPath="${navPath}" resultlistType="${resultlistType}" from="${offset}" until="${offset + size}" lang="${lang}" />
<mcr:setQueryAsString var="strQuery" jdom="${query}" />
<c:set var="headlineKey" value="SR.MyResult" />
<fmt:setLocale value='${lang}'/>
<fmt:setBundle basename='messages'/>
<x:forEach select="$resultList/mcr_results">	
    <x:set var="totalhits" select="string(./@total-hitsize)" scope="page" />
    <div class="headline"><fmt:message key="${headlineKey}" /></div>
    <form action="${WebApplicationBaseURL}resortresult" method="get" id="resortForm">
        <input type="hidden" name="resultlistType" value="${resultlistType}">
        <table cellspacing="0" cellpadding="0">
            <tr>
                <td class="resort">
                    <input type="hidden" name="query" value="${strQuery}">
                        <select name="field1">
                            <option value="modified" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="modified">selected</mcr:ifSorted> ><fmt:message key="SR.sort-modified" /></option>    
                            <option value="title" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="title">selected</mcr:ifSorted> ><fmt:message key="SR.sort-title" /></option>
                            <option value="author" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="author">selected</mcr:ifSorted> ><fmt:message key="SR.sort-author" /></option>
                        </select>
                        <select name="order1">
                            <option value="ascending" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="ascending">selected</mcr:ifSorted> ><fmt:message key="SR.ascending" /></option>
                            <option value="descending" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="descending">selected</mcr:ifSorted> ><fmt:message key="SR.descending" /></option>
                        </select>
                    <input value="Sortiere Ergebnisliste neu" class="resort" type="submit">
                </td>
                <td class="resultCount"><strong>${totalhits} <fmt:message key="SR.foundMCRObjects" /></strong></td>
            </tr>
        </table>
    </form>
    <table id="resultList" cellpadding="0" cellspacing="0">
        <tbody>
            <x:if select="./mcr_result">
                <x:forEach select="./mcr_result/all-metavalues">
                    <x:set var="resultlistLink" select="string(./metaname[1]/resultlistLink/@href)" />
                    <x:set var="docType" select="string(@docType)" />
					<x:set var="mcrID" select="string(@ID)" />
                    <tr>
                        <td class="resultTitle">
                            <a href="${resultlistLink}">
                            	<x:out select="./metaname[1]/metavalues[2]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />
                            	<x:out select="./metaname[1]/metavalues[3]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />                            	                            	                            	
                            	<x:out select="./metaname[1]/metavalues[1]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />
                            </a>
                        </td>
                        <td width="30">&nbsp;</td>                        
                        <td class="author">
                            <x:if select="contains(./metaname[2]/@name,'author')">
                                <x:forEach select="./metaname[2]/metavalues">
                                    <x:if select="generate-id(../metavalues[position() = 1]) != generate-id(.)">
                                       <x:out select="../metavalues/@separator" escapeXml="false" />
                                    </x:if>                    
                                    <x:choose>
                                        <x:when select="./metavalue/@href != '' ">
                                            <a href="<x:out select="./metavalue/@href" />"><x:out select="./metavalue/@text" escapeXml="./@escapeXml" /></a>
                                        </x:when>
                                        <x:otherwise>
                                            <x:out select="./metavalue/@text" />
                                        </x:otherwise>
                                    </x:choose>
                                </x:forEach>
                            </x:if>
                        </td>
	             <td rowspan="2" align="right" valign="top" class="description">
	             <!--  Editbutton -->
	             <table><tr>
	                <td width="10">&nbsp;</td>	             
	                <td>
	                 <form method="get" action="${WebApplicationBaseURL}/servlets/MCRPutDocumenttoWorkflow" class="resort">                 
    				    <input name="page" value="nav?path=~workflow-${docType}" type="hidden" />
	                    <input name="mcrid" value="${mcrID}" type="hidden"/>
						<input title="Dokument bearbeiten" border="0" src="${WebApplicationBaseURL}images/workflow.gif" type="image"  class="imagebutton" />
	                </form> 
	                </td>
	                <td width="10">&nbsp;</td>
	                <td>
					<form method="get" onSubmit="return reallyDeletefromDB();" action="${WebApplicationBaseURL}start_edit" >
						<input value="${requestScope.lang}" name="lang" type="hidden">
						<input name="mcrid" value="${mcrID}" type="hidden">
						<input value="${docType}" name="type" type="hidden">
						<input value="author" name="step" type="hidden">
						<input value="sdelobj" name="todo" type="hidden">
	                    <input value="${navPath}" name="page" type="hidden">                                       
						<input title="<fmt:message key="Object.DelObject" />" src="${WebApplicationBaseURL}images/object_delete.gif" type="image" class="imagebutton">
					</form>
	                </td>
	              </tr></table>  
	              </td>
               </tr>
               <tr>
                  <td colspan="3" class="description" >
					<x:forEach select="./metaname[6]/metavalues/metavalue" >
					 <x:choose>
                        <x:when select="./@href != '' ">
                            <a href="<x:out select="./@href" />"><x:out select="./@text"  /></a>
                        </x:when>
                        <x:otherwise>
                            <x:out select="./@text" />
                        </x:otherwise>               
                     </x:choose>   
                     <br/>                                    
                    </x:forEach>     	
                    <br/>
                    <x:forEach select="./metaname[position() >= 7]/metavalues/metavalue" >
					 <x:choose>
                        <x:when select="./@href != '' ">
                            <a href="<x:out select="./@href" />">
                            <img src="${WebApplicationBaseURL}images/mail.gif" border="0">&#160;<x:out select="./@text" /></a>
                        </x:when>
                        <x:otherwise>
                            <x:out select="./@text" />
                        </x:otherwise>
                      </x:choose>                                            
                     </x:forEach>     	
					<br/>                                      
					<x:out select="./metaname[3]/metavalues/metavalue/@text" escapeXml="./metaname[3]/metavalues/@escapeXml" />
					,
					<x:out select="./metaname[4]/metavalues/metavalue/@text" escapeXml="./metaname[4]/metavalues/@escapeXml" />
					,
					<x:out select="./metaname[5]/metavalues/metavalue/@text" escapeXml="./metaname[5]/metavalues/@escapeXml" />                
                    </td>
                 </tr>
                </x:forEach>
            </x:if>
         </tbody>
    </table>
</x:forEach>    

<br/>

<mcr:browsePageCtrl var="browseControl" totalSize="${totalhits}" size="${size}" offset="${offset}" maxDisplayedPages="10" path="${navPath}" />
<x:forEach select="$browseControl/mcr_resultpages/mcr_resultpage">
    <x:if select="generate-id(../mcr_resultpage[1]) = generate-id(.)">
        <fmt:message key="SR.hitlists" />
    </x:if>
    <x:choose>
        <x:when select="( (contains(../@cutted-left,'true')) and (generate-id(../mcr_resultpage[1]) = generate-id(.)) )">
            <a href="<x:out select="./@href" />">&lt;&lt;&lt;</a>&#160;
        </x:when>
        <x:when select="( (contains(../@cutted-right,'true')) and (generate-id(../mcr_resultpage[last()]) = generate-id(.)) )">
            <a href="<x:out select="./@href" />">&gt;&gt;&gt;</a>&#160;
        </x:when>        
        <x:otherwise>
            <x:choose>
                <x:when select="contains(./@current,'true')">
                    [<x:out select="./@pageNr" />]
                </x:when>
                <x:otherwise>
                    [<a href="<x:out select="./@href" />"><x:out select="./@pageNr" /></a>]                
                </x:otherwise>
            </x:choose>
        </x:otherwise>
    </x:choose>
</x:forEach>  

<hr/>
<br/>


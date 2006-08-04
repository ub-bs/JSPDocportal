<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<c:set var="debug" value="false" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />
<fmt:setBundle basename="messages"/>
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

<mcr:setResultList var="resultList" query="${query}" navPath="${navPath}" resultlistType="${resultlistType}" from="${offset}" until="${offset + size}" lang="${lang}" />
<mcr:setQueryAsString var="strQuery" jdom="${query}" />

<c:choose>
    <c:when test="${fn:startsWith(resultlistType,'class')}">
        <c:set var="headlineKey" value="Webpage.searchresults.result-document-browse" />
    </c:when>
    <c:otherwise>
        <c:set var="headlineKey" value="Webpage.searchresults.result-document-search" />
    </c:otherwise>
</c:choose>
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
                            <option value="modified" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="modified">selected</mcr:ifSorted> ><fmt:message key="Webpage.searchresults.sort-modified" /></option>    
                            <option value="title" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="title">selected</mcr:ifSorted> ><fmt:message key="Webpage.searchresults.sort-title" /></option>
                            <option value="author" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="author">selected</mcr:ifSorted> ><fmt:message key="Webpage.searchresults.sort-author" /></option>
                        </select>
                        <select name="order1">
                            <option value="ascending" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="ascending">selected</mcr:ifSorted> ><fmt:message key="Webpage.searchresults.ascending" /></option>
                            <option value="descending" <mcr:ifSorted query="${query}" attributeName="field" attributeValue="descending">selected</mcr:ifSorted> ><fmt:message key="Webpage.searchresults.descending" /></option>
                        </select>
                    <input value="Sortiere Ergebnisliste neu" class="resort" type="submit">
                </td>
                <td class="resultCount"><strong>${totalhits} <fmt:message key="Webpage.searchresults.foundMCRObjects" /></strong></td>
            </tr>
        </table>
    </form>

    <table cellpadding="0" cellspacing="0">
        <tbody>
            <x:if select="./mcr_result">
                <x:forEach select="./mcr_result/all-metavalues">
                    <x:set var="resultlistLink" select="string(./metaname[1]/resultlistLink/@href)" />
                    <x:set var="mcrID" select="string(@ID)" />
                    <x:set var="docType" select="string(@docType)" />
                    <!--  the number corresponds to the x. entry in resultlist-*.xml -->
                    <x:set var="contentType" select="./metaname[3]/metavalues/metavalue/@text" />					
					<table id="resultList">
					<tbody>
					<tr>
						<td class="resultIcon" rowspan="4">
						<x:choose>
							<x:when select="contains($docType, 'author')">
								<img src="${WebApplicationBaseURL}/images/person.gif" alt="author" />
							</x:when>
							<x:when select="contains($docType, 'institution')">
								<img src="${WebApplicationBaseURL}/images/institution.gif" alt="institution" />							
							</x:when>
							<x:when select="contains($docType, 'disshab')">
								<img src="${WebApplicationBaseURL}/images/disshab.gif" alt="disshab" />							
							</x:when>
							<x:when select="contains($docType, 'document')">
								<x:choose>
									<x:when select="contains($contentType, 'text')">
										<img src="${WebApplicationBaseURL}/images/article.gif" alt="text" />							
									</x:when>
									<x:when select="contains($contentType, 'image')">
										<img src="${WebApplicationBaseURL}/images/picture.gif" alt="image" />							
									</x:when>
									<x:when select="contains($contentType, 'notes')">
										<img src="${WebApplicationBaseURL}/images/musikalie.gif" alt="notes" />							
									</x:when>
									<x:when select="contains($contentType, 'software')">
										<img src="${WebApplicationBaseURL}/images/software.gif" alt="software" />							
									</x:when>
									<x:when select="contains($contentType, 'sound')">
										<img src="${WebApplicationBaseURL}/images/audio.gif" alt="sound" />							
									</x:when>
									<x:when select="contains($contentType, 'video')">
										<img src="${WebApplicationBaseURL}/images/audiovisual.gif" alt="video" />							
									</x:when>
									<x:otherwise>
										<img src="${WebApplicationBaseURL}/images/unknown.gif" alt="unknown" />														
									</x:otherwise>	
								</x:choose>
							</x:when>
							<x:otherwise>
								<img src="${WebApplicationBaseURL}/images/unknown.gif" alt="unknown" />														
							</x:otherwise>
						</x:choose>
					</td>


					<x:choose>
						<x:when select="contains($docType, 'author')">
					     	 <td class="resultTitle">
    	                	        <a href="${resultlistLink}"><x:out select="./metaname[1]/metavalues/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" /></a>
        	                	</td><td /> </tr>
        	                <tr><td colspan="2" class="resultData">
							 	<x:out select="./metaname[@name='OMD.institution']/metavalues/metavalue/@text" />
        	                	</td></tr>
        	                <tr> <td colspan="2" class="resultData">
	        	                	(<x:out select="./metaname[@name='OMD.id']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.id']/metavalues/@escapeXml" />)
								</td> </tr> <tr />
						</x:when>
						<x:when select="contains($docType, 'institution')">
					     	 <td class="resultTitle">
    	                	        <a href="${resultlistLink}"><x:out select="./metaname[1]/metavalues/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" /></a>
        	                	</td><td /> </tr>
        	                <tr><td class="resultData" colspan="2" >
							 	<x:out select="./metaname[@name='OMD.city']/metavalues/metavalue/@text" />
        	                	</td></tr>
        	                <tr> <td class="resultData" colspan="2" >
	        	                	(<x:out select="./metaname[@name='OMD.id']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.id']/metavalues/@escapeXml" />)
								</td> </tr>
						</x:when>

						<x:when select="contains($docType, 'document')">
					     		<td class="resultTitle">
    	                	        <a href="${resultlistLink}"><x:out select="./metaname[@name='OMD.title']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.title']/metavalues/@escapeXml" /></a>
        	                	</td>
                     		   <td class="author">
                            		<x:if select="not(contains(./metaname[2]/@name,'dummy'))">
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
        	              	</tr>
        	                <tr> <td colspan="2" class="resultData">
	        	                	<x:out select="./metaname[@name='OMD.class-types']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.class-types']/metavalues/@escapeXml" />:
	      	               		<fmt:message key="Webpage.searchresults.lastChanged">
		        	                	<fmt:param>
		        	                		<x:out select="./metaname[@name='OMD.changed']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.changed']/metavalues/@escapeXml" />
		        	                	</fmt:param>
	        	                	</fmt:message>							</td> </tr>
        	                <tr> <td colspan="2" class="description">
	        	                	<x:out select="./metaname[@name='OMD.descriptions']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.descriptions']/metavalues/@escapeXml" />
								</td> </tr>
        	        
        	                <tr> <td colspan="2" class="resultData">
	        	                	(<x:out select="./metaname[@name='OMD.id']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.id']/metavalues/@escapeXml" />)
								</td> </tr>
						</x:when>	
						
						<x:when select="contains($docType, 'disshab')">
					     		<td class="resultTitle">
    	                	        <a href="${resultlistLink}"><x:out select="./metaname[@name='OMD.title']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.title']/metavalues/@escapeXml" /></a>
        	                	</td>
                     		   <td class="author">
                            		<x:if select="not(contains(./metaname[2]/@name,'dummy'))">
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
        	              	</tr>
        	                <tr> <td colspan="2" class="resultData">
	        	                	<x:out select="./metaname[@name='OMD.class-types']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.class-types']/metavalues/@escapeXml" />:
	        	    
	        	               		<fmt:message key="Webpage.searchresults.lastChanged">
		        	                	<fmt:param>
		        	                		<x:out select="./metaname[@name='OMD.changed']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.changed']/metavalues/@escapeXml" />
		        	                	</fmt:param>
	        	                	</fmt:message>
								</td> </tr>
        	                <tr> <td colspan="2" class="description">
	        	                	<x:out select="./metaname[@name='OMD.descriptions']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.descriptions']/metavalues/@escapeXml" />
								</td> </tr>
        	                <tr> <td colspan="2" class="resultData">
	        	                	(<x:out select="./metaname[@name='OMD.id']/metavalues/metavalue/@text" escapeXml="./metaname[@name='OMD.id']/metavalues/@escapeXml" />)
								</td> </tr>
						</x:when>
						
									
						<x:otherwise>
						
						<%--Should never occur --%>



  
                    <tr>
                        <td class="resultTitle">
                            <a href="${resultlistLink}"><x:out select="./metaname[1]/metavalues/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" /></a>
                        </td>
                        <td class="author">
                            <x:if select="not(contains(./metaname[2]/@name,'dummy'))">
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
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="description" colspan="2">
                            <table>
                                <tr>
                                    <td class="imageInResultlist">
                                        
                                        <x:set var="mainFileURL" select="concat($WebApplicationBaseURL,'file/',.//digitalobject/@derivid,'/',.//digitalobject/@derivmain,'?hosts=',$host)" />
                                        <c:choose>
                                            <c:when test="${!empty(contentType) and fn:contains('gif-jpeg-png', contentType)}">
                                                <a href="${resultlistLink}"><img src="${mainFileURL}" width="100"></a>
                                            </c:when>
                                            <c:otherwise>
                                                &#160;
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <x:if select="not(contains(./metaname[3]/@name,'dummy'))">                                    
                                            <div class="description">
                                                <x:out select="./metaname[3]/metavalues/metavalue/@text" escapeXml="./metaname[3]/metavalues/@escapeXml" />
                                            </div>
                                        </x:if>
                                        <span>
                                            <x:forEach select="./metaname[position() >= 4]/metavalues">
                                                <x:if select="generate-id(../../metaname[position() = 4]/metavalues) != generate-id(.)">
                                                   ,&#160;
                                                </x:if> 
                                                <x:out select="./metavalue/@text" />
                                            </x:forEach>
                                        <span>                                
                                    </td>
                                    <td rowspan="2" align="right" valign="top" class="description">
						            </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
					</x:otherwise>
					</x:choose>
                    
                </x:forEach>
            </x:if>
         </tbody>
    </table>
</x:forEach>    

<div id="resultListFooter">
<mcr:browsePageCtrl var="browseControl" totalSize="${totalhits}" size="${size}" offset="${offset}" maxDisplayedPages="10" path="${navPath}" />
<x:forEach select="$browseControl/mcr_resultpages/mcr_resultpage">
    <x:if select="generate-id(../mcr_resultpage[1]) = generate-id(.)">
        <fmt:message key="Webpage.searchresults.hitlists" />
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
</div>

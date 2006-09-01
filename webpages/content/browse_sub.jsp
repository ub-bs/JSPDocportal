<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

<%
     java.util.Enumeration ee = request.getParameterNames();
     while ( ee.hasMoreElements() ) {
         String param = (String) ee.nextElement();
    	 System.out.println("PARAM: " + param + " VALUE: "  + 	request.getParameter(param) );
     }
%>    

<c:set var="debug" value="false" />
<c:set var="WebApplicationBaseURL"	value="${applicationScope.WebApplicationBaseURL}" />

<c:set var="lang" value="${requestScope.lang}" />

<c:set var="browserClass" value="${param.browserClass}" />
<c:set var="path" value="${param.path}" />
<c:choose>
<c:when test="${param.actUriPath}">
	<c:set var="actUriPath" value="${param.actUriPath}" />
</c:when>
<c:otherwise>
	<c:set var="actUriPath" value="" />
</c:otherwise>
</c:choose>

<!--  ========== Subselect Parameter ENDE ==========  -->

<c:set var="subselectSession" value="${param.XSL.subselect.session}" />
<c:set var="subselectVarpath" value="${param.XSL.subselect.varpath}" />
<c:set var="subselectWebpage" value="${param.XSL.subselect.webpage}" />

<x:set var="url" 
value="concat($WebApplicationBaseURL,'servlets/XMLEditor?_action=end.subselect&amp;subselect.session=',$subselectSession,
  		 	   '&amp;subselect.varpath=',$subselectVarpath,'&amp;subselect.webpage=', $subselectWebpage)" />

<x:set var="subselectParams"
value="concat('XSL.subselect.session=',$subselectSession,'&amp;XSL.subselect.varpath=',$subselectVarpath,
			  '&amp;XSL.subselect.webpage=', $subselectWebpage )" />

<x:set var="formAction" value="concat($WebApplicationBaseURL, $subselectWebpage, '?XSL.editor.session.id=', $subselectSession)" />     		         		
<!--  ========== Subselect Parameter ENDE ==========  -->
			  
<div class="headline"><fmt:message key="Webpage.browse.generalTitle" /></div>


<mcr:setClassBrowserTreeTag actUriPath="${param.actUriPath}" browserClass="${param.browserClass}"  lang="${lang}"  var="browser" />
<mcr:getConfigProperty defaultValue="default" var="searchField" prop="MCR.ClassificationBrowser.${param.browserClass}.SearchField"/>

<x:set var="hrefStart" select="concat($WebApplicationBaseURL, 'nav?path=', $path )" />

<x:forEach select="$browser/classificationBrowse">    
    <table id="metaHeading" cellpadding="0" cellspacing="0">
		<tr>
	    	<td style="width:60%;" class="desc">
			  <form action="${formAction}" method="post">
			   <input type="submit" class="submit" value="Auswahl abbrechen" />
			   <br/>
			   <br/>
			  </form>
			 </td>
		</tr>
        <tr>
	        <td class="titles">
                <fmt:message key="Webpage.browse.numberOf" /> : <x:out select="./cntDocuments" />
            </td>
            <td class="browseCtrl">
                <a href="${hrefStart}"><x:out select="./description"/>
            </td>
        </tr>
	</table>
 	<hr/>
	<table cellspacing="0" cellpadding="3" width="100%" >
	<tr><td valign="top">
 	  <table id="browseClass" cellspacing="0" cellpadding="3">
		<x:forEach select="./navigationtree/row" >    
		    <x:set var="searchbase" select="string(./col[2]/@searchbase) " />
		    <x:set var="folder1" select="string(./col[1]/@folder1) " />
		    <x:set var="lineID" select="string(./col[2]/@lineID) " />
		    <x:set var="text" select="string(./col[2]) " />

			<x:set var="lineLevel" select="number(./col[1]/@lineLevel)" />
			<x:set var="numDocs" select="number(./col[2]/@numDocs)" />
			<x:set var="fmtnumDocs" select="string(./col[2]/@fmtnumDocs)" />			
			<c:set var="fmtnumDocs" value="${fn:replace(fmtnumDocs,' ', '_')}" />			
			<x:set var="plusminusbase" select="string(./col[1]/@plusminusbase)" />
   	   		<x:set var="subSelectItem" value="concat( $url, '&amp;_var_@categid=',$lineID,'&amp;_var_@title=',$text)" />
			
        	<tr valign="top" >
        	   <td valign="top">
	       	    <c:if test="${lineLevel gt 0 }" >
       	            <img border="0" height="1px" width="${lineLevel * 10}px"  src="${WebApplicationBaseURL}images/folder_blank.gif" />
				</c:if>        	            
				<c:choose>
	       		    <c:when test="${fn:length(plusminusbase) > 0}" >
       	        	 	<a href="${WebApplicationBaseURL}nav?path=${path}&actUriPath=${searchbase}"><img 
       	        	 		class="borderless" src="${WebApplicationBaseURL}images/${folder1}.gif" /></a>
       		        </c:when>
    	   	        <c:otherwise> 
       	        		<img class="borderless" src="${WebApplicationBaseURL}images/${folder1}.gif" />
					</c:otherwise> 		
				</c:choose>
        	   </td>
        	   <td> 
        	      [ <code> <c:out value="${fmtnumDocs}" />  </code>  <fmt:message key="Webpage.browse.doc" />]
        	   </td>
        	   <td class="descr">
    	          <a href="${subSelectItem}"><x:out select="string(./col[2])" /></a>
        	   </td>
	       	    <x:if select="./col[2]/comment">
	       	       <td>
	        	      <x:out select="string(./col[2]/comment)" />
	       	       </td>  
				</x:if>       	       
			</tr>	        	
        	
        </x:forEach>	
	 </table>	 
    </td></tr>		 
   </table>
 </x:forEach>


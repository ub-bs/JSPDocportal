<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
 
<div class="headline">
	<fmt:message key="Admin.User" />
</div>
<br/>

<c:set var="debug" value="false" />
<mcr:listWorkflow var="myWorkflowList" docType="user" />

<table class="access" cellspacing="1" cellpadding="0" >
	<tr><th colspan="2" >Neuregistrierungen</th><th>Bearbeiten</th><th>Löschen</th></tr>
	<x:forEach select="$myWorkflowList/mcr_workflow/mcr_result">	
      <x:set var="filename" select="string(./@filename)" />
      <x:forEach select="all-metavalues">
      <x:set var="itemID" select="string(./@ID)" />
      <tr>
        <td class="resultTitle">
           <b>
            	<x:out select="./metaname[1]/metavalues[1]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />
           	    <x:out select="./metaname[1]/metavalues[2]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />
           	    <x:out select="./metaname[1]/metavalues[3]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />                            	                            	                            	
           </b><br/>
           <table>
             <tr>
              <td>
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
               <x:forEach select="./metaname[position() >= 7]/metavalues/metavalue" >
							<x:choose>
                            <x:when select="./@href != '' ">
                                <a href="<x:out select="./@href" />">
                                <img src="${applicationScope.WebApplicationBaseURL}images/mail.gif" border="0"><x:out select="./@text" /></a>
                            </x:when>
                            <x:otherwise>
                                <x:out select="./@text" />
                            </x:otherwise>
                            </x:choose>                                            
							<br/>                                      
                    </x:forEach>     	
					<x:out select="./metaname[3]/metavalues/metavalue/@text" escapeXml="./metaname[3]/metavalues/@escapeXml" />
					,&#160;
					<x:out select="./metaname[4]/metavalues/metavalue/@text" escapeXml="./metaname[4]/metavalues/@escapeXml" />
					,&#160;
					<x:out select="./metaname[5]/metavalues/metavalue/@text" escapeXml="./metaname[5]/metavalues/@escapeXml" />
                </td>
               </tr>
              </table>  	
         </td>
		 <td width="50">	&nbsp;	</td>
		 <td >
			<form method=post action="${applicationScope.WebApplicationBaseURL}admin/user_validate.jsp" id="overview">	
				<input type="image" title="Benutzer bearbeiten" name="r${itemID}" src="${applicationScope.WebApplicationBaseURL}admin/images/edit.png">						
				<input type=hidden name="filename" value="${filename}" />
				<input type="hidden" name="operation" value="detail">
			</form>					
		 </td>
		 <td >
			<form method=post action="${applicationScope.WebApplicationBaseURL}admin/user_validate.jsp" id="overview">	
				<input type="image" title="Benutzer löschen" name="y${itemID}" src="${applicationScope.WebApplicationBaseURL}admin/images/delete.png" onClick="return questionDel()">	
				<input type=hidden name="filename" value="${filename}" />
				<input type="hidden" name="operation" value="detail">
			</form>					
		 </tr>        
   	  </x:forEach>
</x:forEach>
</table>



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<mcr:session method="get" var="username" type="userID" />

<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<c:set var="debug" value="false" />
<mcr:listWorkflowProcess var="myWorkflowList" workflowProcessType="${param.workflowProcessType}" userid="${username}" status="status"  />

<br/>

<div class="headline"><fmt:message key="SWF.WorkflowHeadline-${param.type}" /></div>

<table cellspacing="3" cellpadding="3" >
   <tr>   
      <td class="metaname" >Ergebnis:</td>
      <td class="metavalue"><fmt:message key="SWF.Dissertation.${status}" /> </td>       
   </tr>    
</table>

<p>	<fmt:message key="SWF.Info" />	</p>

<table border="0" >
<x:forEach select="$myWorkflowList/mcr_workflow/mcr_result">	
     <x:forEach select="all-metavalues">
        <x:set var="itemID" select="string(./@ID)" />
        <tr>
			<td class="nothing" colspan="3"><hr />	</td>
	    </tr>
        <tr>
         <td class="resultTitle">
           <b>
           	    <x:out select="./metaname[1]/metavalues[2]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />
           	    <x:out select="./metaname[1]/metavalues[3]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />                            	                            	                            	
            	<x:out select="./metaname[1]/metavalues[1]/metavalue/@text" escapeXml="./metaname[1]/metavalues/@escapeXml" />
           </b>  	
         </td>
		 <td width="50">	&nbsp;		</td>
		 <td align="right">
				<table cellpadding="0" cellspacing="0">
					<tr>
										<c:if test="${param.type == 'document' || param.type == 'professorum' || param.type == 'disshab' }">
											<td align="center" valign="top" width="30">
												<form method="get" action="${requestScope.WebApplicationBaseURL}nav">
													<input value="~editor-include" name="path" type="hidden">
													<input value="${requestScope.lang}" name="lang" type="hidden">
													<input name="mcrid" value="${itemID}" type="hidden">
													<input value="${param.type}" name="type" type="hidden">
													<input value="${param.step}" name="step" type="hidden">
													<input value="wnewder" name="todo" type="hidden">
												    <input value="~workflow-${param.type}" name="nextPath" type="hidden">																									
													<input title="<fmt:message key="Derivate.AddDerivate" />" src="${requestScope.WebApplicationBaseURL}images/workflow_add.gif" type="image" class="imagebutton">
												</form>
											</td>
										</c:if>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}nav">
												<input value="~editor-include" name="path" type="hidden">
												<input value="withdata" name="start" type="hidden">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="mcrid" value="${itemID}" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
											    <input value="~workflow-${param.type}" name="nextPath" type="hidden">																									
												<input value="weditobj" name="todo" type="hidden">
												<input title="<fmt:message key="Object.EditObject" />" src="${requestScope.WebApplicationBaseURL}images/workflow_edit.gif" type="image" class="imagebutton">
											</form>
										</td>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="mcrid" value="${itemID}" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="wcommit" name="todo" type="hidden">
												<input value="~workflow-${param.type}" name="nextPath" type="hidden">												
												<input title="<fmt:message key="Object.CommitObject" />" src="${requestScope.WebApplicationBaseURL}images/workflow_commit.gif" type="image" class="imagebutton">
											</form>
										</td>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="mcrid" value="${itemID}" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="wdelobj" name="todo" type="hidden">
												<input value="~workflow-${param.type}" name="page" type="hidden">
												<input title="<fmt:message key="Object.DelObject" />" src="${requestScope.WebApplicationBaseURL}images/workflow_delete.gif" type="image" class="imagebutton">
											</form>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}nav">
												<input value="~workflow-preview" name="path" type="hidden">
												<input name="id" value="${itemID}" type="hidden">
												<input name="fromWForDB" value="workflow" type="hidden">
												<input title="<fmt:message key="Object.Preview" />" src="${requestScope.WebApplicationBaseURL}images/workflow_preview.gif" type="image" class="imagebutton">
											</form>
								</td>
							</tr>
						</table>
					</td>
				</tr>        
	       		 <tr>
					 <td class="description" colspan="3">
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
		                                            <img src="${WebApplicationBaseURL}images/mail.gif" border="0"><x:out select="./@text" /></a>
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
           				 </tr>				
		  	   </x:forEach>
		  	   
		       <x:forEach select="derivate">
        		 <x:set var="derivateID" select="string(./@ID)" />
        		 <x:set var="derivateLabel" select="string(./@label)" />
		          <tr>
							<td align="left" valign="top">${derivateLabel}&#160;	</td>
							<td width="30">	&nbsp;	</td>
							<td>
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="mcrid2" value="${derivateID}" type="hidden">
												<input name="mcrid" value="${itemID}" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="waddfile" name="todo" type="hidden">
												<input title="<fmt:message key="Derivate.AddFile" />" src="${requestScope.WebAppliationBaseURL}images/classnew.gif" type="image" class="imagebutton">
											</form>
										</td>
										<td width="10"></td>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}nav">
												<input value="~editor-include" name="path" type="hidden">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="mcrid2" value="${derivateID}" type="hidden">
												<input name="mcrid" value="${itemID}" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="weditder" name="todo" type="hidden">
												<input title="<fmt:message key="Derivate.EditDerivate" />" src="${requestScope.WebAppliationBaseURL}images/classedit.gif" type="image" border="0" class="imagebutton">
											</form>
										</td>
										<td width="10"></td>
										<td align="center" valign="top" width="30">
											<form method="get" action="${requestScope.WebAppliationBaseURL}start_edit">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="mcrid2" value="${derivateID}" type="hidden">
												<input name="mcrid" value="${itemID}" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="wdelder" name="todo" type="hidden">
												<input title="<fmt:message key="Derivate.DelDerivate" />" src="${requestScope.WebAppliationBaseURL}images/classdelete.gif" type="image" border="0" class="imagebutton">
											</form>
										</td>
									</tr>
								</table>
							</td>
					</tr>		       

       		    <x:set var="numFiles" select="count(./file)" />					
				<x:forEach select="file">
       		        <x:set var="fileSize" select="string(./@size)" />    	    		 
					<tr valign="top" >
							<td>
								<img src="images/darkblueBox.gif">
								<a class="linkButton" href="servlets/MCRFileViewWorkflowServlet/<x:out select="."/>?type=${param.type}" target="_blank">
								  <x:out select="."/>
								</a>	[	${fileSize}	]
							</td>
							<td width="30">			&nbsp;		</td>
							<td>
								<table cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" valign="top" width="30">
											<c:if test="${numFiles gt 1}">
												<form method="post" action="start_edit">
													<input value="${requestScope.lang}" name="lang" type="hidden">
													<input name="mcrid2" value="${derivateID}" type="hidden">
													<input name="mcrid" value="${itemID}" type="hidden">
													<input value="${param.type}" name="type" type="hidden">
													<input value="${param.step}" name="step" type="hidden">
													<input value="wdelfile" name="todo" type="hidden">
													<input name="extparm" value="####nrall####2####nrthe####1####filename####<x:out select="."/>" type="hidden">
													<input title="Löschen dieser Datei" src="${requestScope.WebApplicationBaseURL}images/button_delete.gif" type="image" class="imagebutton">
												</form>
											</c:if>
										</td>
									</tr>
								</table>
							</td>
						</tr>
			     </x:forEach>			
	       </x:forEach>
</x:forEach>
</table>



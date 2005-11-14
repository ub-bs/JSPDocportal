<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.mycore.frontend.servlets.MCRServlet" %>
<%@ page import="org.mycore.common.MCRSession" %>
<%@ page import="org.mycore.common.MCRSessionMgr" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.mycore.common.MCRConfiguration" %>
<%@ page import="org.jdom.Element" %>
<%@ page import="org.mycore.common.xml.MCRURIResolver" %>
<%@ page import="org.mycore.common.JSPUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="org.mycore.access.MCRAccessManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<%
   StringBuffer sbURI = new StringBuffer("request:servlets/MCRListWorkflowServlet")
       .append("?XSL.Style=xml&type=").append(request.getParameter("type"))
       .append("&step=").append(request.getParameter("step"));
   Element jdom = MCRURIResolver.instance().resolve(sbURI.toString()) ;
   Logger.getLogger("workflow.jsp").debug("MCRFileListWorkflowServlet delivers:" + JSPUtils.getPrettyString(jdom));
   List items = jdom.getChildren("item");
   MCRSession mcrSession = MCRSessionMgr.getCurrentSession();
 %>
 
<center>
<span class="textboldnormal"><u><fmt:message key="SWF.WorkflowHeadline-${param.type}" /></u></span>
<p>
</p>
<table style="width:98%;margin: 3% 0px 3% 2%;" cellpadding="0" cellspacing="0" >

<%
   for(Iterator it = items.iterator(); it.hasNext();){
       Element item = (Element) it.next();
       String itemID = item.getAttributeValue("ID");
       String itemLabel = item.getChildText("label");
       if (itemLabel == null) itemLabel = "Without words" ;
       StringBuffer sbData = new StringBuffer("");
       for(Iterator it2 = item.getChildren("data").iterator(); it2.hasNext();) {
       	   Element elData = (Element) it2.next();
	       sbData.append(elData.getText());
       }
       %>
        <tr class="result">
            <td>
                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td style="font-weight: bolder;" class="header" align="left"><%= itemLabel %></td>
                        <td class="header" width="20"></td>
                        <td style="text-align: right;" class="header" align="right"><%= itemID %></td>
                    </tr>
                </table>
            </td>
        </tr>       
        <tr class="result">
            <td class="desc" valign="top">
                <table cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td class="metavalue" align="left" valign="top"><%= sbData.toString() %></td>
                        <td width="10"></td>
                        <c:if test="${param.type == 'document'}">
                            <td align="center" valign="top" width="30">
                                <form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
                                    <input value="~workflow-edit" name="path" type="hidden">
                                    <input value="${requestScope.lang}" name="lang" type="hidden">
                                    <input name="se_mcrid" value="<%= itemID %>" type="hidden">
                                    <input value="${param.type}" name="type" type="hidden">
                                    <input value="${param.step}" name="step" type="hidden">
                                    <input value="wnewder" name="todo" type="hidden">
                                    <input title="<fmt:message key="Derivate.AddDerivate" />" src="${requestScope.WebApplicationBaseURL}img/workflow_deradd.gif" type="image">
                                </form>
                            </td>                            
                        </c:if>
                        <td width="10"></td>
                        <td align="center" valign="top" width="30">
                            <form method="get" action="${requestScope.WebApplicationBaseURL}nav">
                                <input value="~workflow-edit" name="path" type="hidden">
                                <input value="withdata" name="start" type="hidden">
                                <input value="${requestScope.lang}" name="lang" type="hidden">
                                <input name="se_mcrid" value="<%= itemID %>" type="hidden">
                                <input value="${param.type}" name="type" type="hidden">
                                <input value="${param.step}" name="step" type="hidden">
                                <input value="weditobj" name="todo" type="hidden">
                                <input title="<fmt:message key="Object.EditObject" />" src="${requestScope.WebApplicationBaseURL}img/workflow_objedit.gif" type="image">
                            </form>
                        </td>
                        <td width="10">
                        </td>
                        <td align="center" valign="top" width="30">
                            <form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
                                <input value="${requestScope.lang}" name="lang" type="hidden">
                                <input name="se_mcrid" value="<%= itemID %>" type="hidden">
                                <input value="${param.type}" name="type" type="hidden">
                                <input value="${param.step}" name="step" type="hidden">
                                <input value="wcommit" name="todo" type="hidden">
                                <input title="<fmt:message key="Object.CommitObject" />" src="${requestScope.WebApplicationBaseURL}img/workflow_objcommit.gif" type="image">
                            </form>
                        </td>
                        <td width="10"></td>
                        <td align="center" valign="top" width="30">
                            <form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
                                <input value="${requestScope.lang}" name="lang" type="hidden">
                                <input name="se_mcrid" value="<%= itemID %>" type="hidden">
                                <input value="${param.type}" name="type" type="hidden">
                                <input value="${requestScope.lang}" name="step" type="hidden">
                                <input value="wdelobj" name="todo" type="hidden">
                                <input title="<fmt:message key="Object.DelObject" />" src="${requestScope.WebApplicationBaseURL}img/workflow_objdelete.gif" type="image">
                            </form>
                        </td>
                    </tr>
                    <%
                    for(Iterator it3 = item.getChildren("derivate").iterator(); it3.hasNext();) {
                        Element derivate = (Element)it3.next();
                        String derivateID = derivate.getAttributeValue("ID");                        
                        String derivateLabel = derivate.getAttributeValue("label");
                        %>
                        <tr>
                        	<td class="metavalue" align="left" valign="top">
								<table cellpadding="0" cellspacing="0" width="100%">
									<tr>
										<td align="left" valign="top"><%= derivateLabel %>&#160;</td>
										<td width="10"></td>
										<td valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}start_edit">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="se_mcrid" value="<%= derivateID %>" type="hidden">
												<input name="re_mcrid" value="<%= itemID %>" type="hidden">
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="waddfile" name="todo" type="hidden">
												<input title="<fmt:message key="Derivate.AddFile" />" src="${requestScope.WebAppliationBaseURL}img/workflow_deradd.gif" type="image">
											</form>
										</td><td width="10"></td>
										<td valign="top" width="30">
											<form method="get" action="${requestScope.WebApplicationBaseURL}nav">
												<input value="~workflow-edit" name="path" type="hidden">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="se_mcrid" value="<%= derivateID %>" type="hidden">
												<input name="re_mcrid" value="<%= itemID %>" type="hidden">												
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">
												<input value="weditder" name="todo" type="hidden">
												<input title="<fmt:message key="Derivate.EditDerivate" />" src="${requestScope.WebAppliationBaseURL}img/workflow_deredit.gif" type="image">
											</form>
										</td>
										<td width="10"></td>
										<td valign="top" width="30">
											<form method="get" action="${requestScope.WebAppliationBaseURL}start_edit">
												<input value="${requestScope.lang}" name="lang" type="hidden">
												<input name="se_mcrid" value="<%= derivateID %>" type="hidden">
												<input name="re_mcrid" value="<%= itemID %>" type="hidden">												
												<input value="${param.type}" name="type" type="hidden">
												<input value="${param.step}" name="step" type="hidden">												
												<input value="wdelder" name="todo" type="hidden">
												<input title="<fmt:message key="Derivate.DelDerivate" />" src="${requestScope.WebAppliationBaseURL}img/workflow_derdelete.gif" type="image">
											</form>
										</td>
										<td width="10"></td>
										<td valign="top" width="30"></td>
									</tr>
								</table>                        	
                        	</td>
                        </tr>                        
                        <%
                        List files = derivate.getChildren("file");
                        pageContext.setAttribute("numFiles",new Integer(files.size()));
                        for(Iterator it4 = derivate.getChildren("file").iterator(); it4.hasNext();) {
	                     	Element file = (Element) it4.next();   
	                        String fileName = file.getText();
							String fileSize = file.getAttributeValue("size");
	                        %>
	                        <tr>
		                        <td class="metavalue" align="left" valign="top">
									<table cellpadding="0" cellspacing="0" width="100%">
										<tr>
											<td valign="top"><img src="${requestScope.WebApplicationBaseURL}/img/button_green.gif"></td>
											<td valign="top"><a class="linkButton" href="${requestScope.WebApplicationBaseURL}servlets/MCRFileViewWorkflowServlet/<%= fileName %>?type=${param.type}" target="_blank"><%= fileName %></a> [<%= fileSize %>] </td>
											<td valign="top">
												<c:if test="${pageScope.numFiles gt 1}">
													<form method="post" action="$requestScope.WebApplicationBaseURL}start_edit">
														<input value="${requestScope.lang}" name="lang" type="hidden">
														<input name="se_mcrid" value="<%= derivateID %>" type="hidden">
														<input name="re_mcrid" value="<%= itemID %>" type="hidden">
														<input value="${param.type}" name="type" type="hidden">
														<input value="${param.step}" name="step" type="hidden">
														<input value="wdelfile" name="todo" type="hidden">
														<input name="extparm" value="####nrall####2####nrthe####1####filename####<%= fileName %>" type="hidden">
														<input title="Löschen dieser Datei" src="${requestScope.WebApplicationBaseURL}img/button_delete.gif" type="image">
													</form>											
												</c:if>
											</td>
										</tr>
									</table>	                        
		                        </td>
	                        </tr>
	                        <%
	                    }
                    }
                    %>
                </table>
            </td>
        </tr>
       <%
   }
%>
</table>
</center>
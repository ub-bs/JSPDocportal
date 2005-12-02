<%@ page import="org.mycore.datamodel.metadata.MCRObject,
                 org.mycore.datamodel.metadata.MCRObjectID,
                 org.mycore.frontend.servlets.MCRServlet,
				 java.util.HashMap,
                 java.util.List,
                 java.util.Iterator,
                 java.util.Enumeration,
				 org.jdom.Element,
				 org.jdom.Document,
                 org.apache.log4j.Logger"%>
<%@ page import="org.jdom.Document" %>
<%@ page import="org.mycore.frontend.jsp.format.MCRResultFormatter" %>
<%@ page import="org.mycore.frontend.jsp.query.MCRDerivateComparator" %>
<%@ page import="org.mycore.datamodel.metadata.MCRObject" %>
<%@ page import="org.mycore.datamodel.metadata.MCRDerivate" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="org.mycore.common.xml.MCRXMLHelper" %>
<%@ page import="javax.xml.xpath.XPath" %>
<%@ page import="org.mycore.access.MCRAccessManager" %>
<%@ page import="org.mycore.common.MCRSession" %>
<%@ page import="org.mycore.common.xml.MCRURIResolver" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>                 
<%
    MCRSession mcrSession = MCRServlet.getSession(request);
    MCRResultFormatter formatter = new MCRResultFormatter();
    String WebApplicationBaseURL = (String) getServletContext().getAttribute("WebApplicationBaseURL");
    String referer = request.getHeader("referer");
    if (referer == null) referer = "";
    pageContext.setAttribute("referer",referer);
    
    String derID = request.getParameter("derID");
    String endPath = request.getParameter("endPath");
    if (endPath == null) endPath = "";
    String docID = request.getParameter("docID");
    
    String hosts = request.getParameter("hosts");
    String lang = (String) request.getAttribute("lang");

    Document doc = null;

    MCRDerivate mcr_der = new MCRDerivate();
    MCRObject mcr_obj = new MCRObject();    

    try {
        doc = mcr_obj.receiveJDOMFromDatastore(docID);
    } catch (Exception e) {
        Logger.getLogger("derivatedetails.jsp").warn(" The ID " + docID + " is not a MCRObjectID!");
        request.setAttribute("message","The docID " + docID + " is not valid!");
        getServletContext().getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);
        return;
    }
    
    mcr_der.receiveFromDatastore(derID);
    String mainDoc = mcr_der.getDerivate().getInternals().getMainDoc();
    String mainDocURL = new StringBuffer(WebApplicationBaseURL).append("file/")
        .append(derID).append("/").append(mainDoc)
        .append("?hosts=").append(hosts).toString();

    String xpTitle = new StringBuffer("/mycoreobject/metadata/titles/title[@xml:lang='")
        .append(lang).append("']").toString();
    String docTitle = MCRResultFormatter.getInstance().getSingleXPathValue(doc.getRootElement(),xpTitle);
    
    
    StringBuffer fileNodeServletURI = new StringBuffer(WebApplicationBaseURL).append("file/")
        .append(derID).append("/").append(endPath).append("?hosts=").append(hosts).append("&XSL.Style=xml");    
        
    //Document derDetails = MCRXMLHelper.parseURI(fileNodeServletURL.toString());
    Element der = MCRURIResolver.instance().resolve(fileNodeServletURI.toString());
    
    Element fileContents = org.mycore.common.xml.MCRURIResolver.instance().resolve("resource:" + "FileContentTypes.xml");
    String allFiles = formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/mcr_directory/numChildren/total/files");
    String derPath = formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/mcr_directory/path");
    String derOwnerID = formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/mcr_directory/ownerID");    
%>
    <fmt:setLocale value='<%= lang %>'/>
    <fmt:setBundle basename='messages'/>
    
    <div class="headline"><fmt:message key="objectsdetaillist" /></div>
    
    <table id="metaHeading" cellpadding="0" cellspacing="0">
       <tr>
          <td class="titles">
             <%= formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/@id") %>
          </td>
          <td class="browseCtrl">
             <x:if select="string-length($referer) != 0">
                <a class="textboldbigger" href="{$referer}">
                   <fmt:message key="IFS.return" />
                </a>
             </x:if>
          </td>
       </tr>
    </table>
    <!-- IE Fix for padding and border -->
    <hr/>
    
    <table id="metaData" cellpadding="0" cellspacing="0">
      <tr>
        <th class="metahead" colspan="2"><fmt:message key="IFS.common" /></th>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.location" />:</td>
        <td class="metavalue"><%= formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/@host") %></td>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.size" />:</td>
        <td class="metavalue"><%= formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/mcr_directory/size") %> Byte</td>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.total" />:</td>
        <td class="metavalue">
          <%= new StringBuffer(formatter.getSingleXPathValue(der,allFiles))
                    .append(" / ").append(formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/mcr_directory/numChildren/total/directories"))
                    .toString() 
           %>
        </td>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.zipfile" />:</td>
        <td class="metavalue"></td>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.startfile" />:</td>
        <td class="metavalue">
            <a href="<%= mainDocURL %>"><%= mainDoc %></a>
        </td>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.lastchanged" />:</td>
        <td class="metavalue"><%= formatter.getSingleXPathValue(der,"/mcr_results/mcr_result/mcr_directory/date") %></td>
      </tr>
      <tr>
        <td class="metaname"><fmt:message key="IFS.title" />:</td>
        <td class="metavalue">
           <%= docTitle %>
        </td>
      </tr>
    </table>
    <table id="files" cellpadding="0" cellspacing="0">
      <tr>
        <th class="metahead"></th>
        <th class="metahead"><fmt:message key="IFS.filename"/></th>
        <th class="metahead"><fmt:message key="IFS.filesize"/></th>
        <th class="metahead"><fmt:message key="IFS.filetype"/></th>
        <th class="metahead"><fmt:message key="IFS.lastchanged"/></th>
        <th class="metahead"></th>
      </tr>
      <%
         List childs = org.jdom.xpath.XPath.selectNodes(der,"/mcr_results/mcr_result/mcr_directory/children/child");
         java.util.Collections.sort(childs, new MCRDerivateComparator());
         String docType = new MCRObjectID(docID).getTypeId();                     
         for(Iterator it = childs.iterator();it.hasNext();) {
            Element child = (Element) it.next();
            String type = child.getAttributeValue("type");
            String fileName = child.getChildText("name");
         %>
           <tr>
              <td class="metavalue">         
              <%
                 if(mainDoc.equals(child.getChildText("name"))) {
                 %>
                    <img src"<%= WebApplicationBaseURL %>images/button_green.gif" alt="Main file" border="0" />
                 <%
                 }else {
                    if (MCRAccessManager.checkAccess("write",derID,mcrSession) && type.equals("file")) {
                    %>
                       <form action="<%= WebApplicationBaseURL %>nav" method="get">
                          <input name="path" type="hidden" value="~workflow-<%= docType %>" >
                          <input name="lang" type="hidden" value="<%= lang %>" >
                          <input name="se_mcrid" type="hidden" value="<%= derID %>">
                          <input name="re_mcrid" type="hidden" value="<%= docID %>">
                          <input name="type" type="hidden" value="<%= docType %>" >
                          <input name="step" type="hidden" value="commit" />
                          <input name="todo" type="hidden" value="ssetfile" />
                          <input name="extparm" type="hidden" value="<%= fileName %>">
                          <input type="image" src="<%= WebApplicationBaseURL %>images/button_light.gif" title="<fmt:message key="IFS.mainbutton" />" border="0" >
                       </form>                    
                    <%   
                    }
                 }
              %>
              </td>
              <td class="metavalue">
                 <%
                    if(type.equals("directory")) {
                       %>
                          <a href="<%= new StringBuffer(WebApplicationBaseURL).append("nav?path=~derivatedetails&derID=")
                                        .append(derID).append("&docID=").append(docID).append("&endPath=").append(fileName)
                                        .append("&hosts=").append(hosts).toString() %>"><%= fileName %></a>
                       <%
                    }else if(type.equals("file")) {
                       %>
                          <a href="<%= new StringBuffer(WebApplicationBaseURL).append("file/")
                                        .append(derPath).append("/").append(fileName)
                                        .append("?hosts=").append(hosts).toString() %>"><%= fileName %></a>
                       <%
                    }
                 %>
             </td>  
             <td class="metavalue"><%= child.getChildText("size") %> Byte</td>
             <%
                if(type.equals("directory")) {
                %>
                    <td class="metavalue">Directory</td>                
                <%
                }else if(type.equals("file")) {
                   StringBuffer xpFileContentType = new StringBuffer("/FileContentTypes/type[@ID='")
                    .append(child.getChildText("contentType")).append("']/label");
                %>
                   <td class="metavalue"><%= formatter.getSingleXPathValue(fileContents, xpFileContentType.toString()) %></td>
                <%
                }
             %> 
             <td class="metavalue"><%= child.getChildText("date") %></td>
             <td class="metavalue">
             <%
                if( MCRAccessManager.checkAccess("write",derID,mcrSession)) {
                %>
                       <form action="<%= WebApplicationBaseURL %>nav" method="get">
                          <input name="path" type="hidden" value="~workflow-<%= docType %>" >
                          <input name="lang" type="hidden" value="<%= lang %>" />
                          <input name="se_mcrid" type="hidden" value="<%= derID %>">
                          <input name="re_mcrid" type="hidden" value="<%= docID %>">
                          <input name="type" type="hidden" value="<%= docType %>" >
                          <input name="step" type="hidden" value="commit" >
                          <input name="todo" type="hidden" value="sdelfile" >
                          <input name="extparm" type="hidden" value="####nrall####<%= allFiles %>####nrthe####1####filename####<%= derPath.replaceFirst(derOwnerID,"") %><%= fileName %>">
                          <input type="image" src="<%= WebApplicationBaseURL %>images/button_delete.gif" title="<fmt:message key="IFS.delbutton" />" border="0" >
                       </form>  
                <%
                }
             %>
             </td>             
           </tr>
         <%
         }        
      %>
      </table>
      <hr>
      

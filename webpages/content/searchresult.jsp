<%@ page import="org.mycore.services.fieldquery.*,
                 org.mycore.datamodel.metadata.MCRObject,
                 org.mycore.common.MCRSession,
                 org.mycore.frontend.jsp.query.MCRResultFormatter,
                 org.jdom.Element,
                 org.jdom.Document,
                 org.jdom.xpath.XPath,
                 org.mycore.frontend.jsp.NavServlet,
                 org.mycore.frontend.jsp.*,
                 org.mycore.frontend.servlets.MCRServlet,
                 org.apache.log4j.Logger,
                 java.util.*" %>
<%@ page import="com.sun.net.ssl.internal.ssl.JS_ConvertBigInteger" %>
<%@ page import="org.mycore.common.JSPUtils" %>
<%@ page import="org.mycore.backend.query.MCRQueryManager" %>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%!

    public String buildHitLists(int ressize, int size, int offset, String path) {
        StringBuffer sbRet = new StringBuffer();
        int totalNumPages= (ressize % size == 0)? ressize / size : ressize / size + 1;
        int currentPage = offset / size +1 ;
        String baseResultURL = new StringBuffer(NavServlet.getNavigationBaseURL()).append("nav?path=")
        	  .append(path).toString();
        int start = Math.max(1,currentPage -5);
        int end = Math.min(start + 9,totalNumPages);
        if (start > 1) {
        	sbRet.append("<a href=\"").append(baseResultURL)
        		.append("&size=").append(size).append("&offset=")
        		.append((start - 1)*size).append("\">...</a>&nbsp;&nbsp;");
        }
        for (int i = start; i <= end ; i++) {
           if( i == currentPage) {
        	   sbRet.append("[<a href=\"").append(baseResultURL)
        		.append("&size=").append(size).append("&offset=")
        		.append((i-1)*size).append("\">").append(i).append("</a>]&nbsp;");
           }else {
        	   sbRet.append("<a href=\"").append(baseResultURL)
       		.append("&size=").append(size).append("&offset=")
       		.append((i-1)*size).append("\">").append(i).append("</a>&nbsp;");
           }
        }
        if (end < totalNumPages) {
        	sbRet.append("<a href=\"").append(baseResultURL)
        		.append("&size=").append(size).append("&offset=")
        		.append((end + 1)*size).append("\">...</a>&nbsp;&nbsp;");
        } 
        return sbRet.toString();
     }
%>
<%
int offset = 0;
int size = 10;
MCRObject mcr_obj = new MCRObject();
String sOffset = request.getParameter("offset");
String sSize = request.getParameter("size");
if (sOffset != null) {
    offset = Integer.parseInt(sOffset);
}
if (sSize != null) {
    int tmp = Integer.parseInt(sSize);
    if (tmp < 21) size = tmp;
}
int len = 0; 
MCRSession mcrsession = MCRServlet.getSession(request);
String lang = (String) request.getAttribute("lang");
String path = (String) request.getParameter("path");


Enumeration paramNames = request.getParameterNames();
MCRResultFormatter formatter = new MCRResultFormatter();

String navPath = (String) request.getAttribute("path");
MCRResults result = null;
org.jdom.Document jdomQuery = (org.jdom.Document) request.getAttribute("query");
String resultlistType = (String) request.getAttribute("resultlistType");
if ((jdomQuery != null) && (resultlistType != null)) {
   mcrsession.put(navPath + "-jdomQuery", jdomQuery);
   mcrsession.put(navPath + "-resultlistType", resultlistType); 
}else {
   jdomQuery = (org.jdom.Document) mcrsession.get(navPath + "-jdomQuery");
   resultlistType = (String) mcrsession.get(navPath + "-resultlistType");
}
String headlineKey = (resultlistType.startsWith("class"))? "SR.result-document-browse" : "SR.result-document-search";
if ((jdomQuery != null) && (resultlistType != null)) {

    result = MCRQueryManager.getInstance().search(jdomQuery);
    result.setComplete();
    Logger.getLogger("content/searchresult.jsp").debug("found hits:" + result.getNumHits());
}

org.jdom.Element sortby = jdomQuery.getRootElement().getChild("sortby");

if (result != null) {
		len = result.getNumHits();
}		
if (len <= 100) {
	mcrsession.put("lastMCRResults",result);
}else {
	mcrsession.put("lastMCRResults",null);
}
org.jdom.output.XMLOutputter output = new org.jdom.output.XMLOutputter(org.jdom.output.Format.getPrettyFormat());
String strQuery = output.escapeAttributeEntities(output.escapeElementEntities(output.outputString(jdomQuery)));


//for (Enumeration e = request.getAttributeNames(); e.hasMoreElements() ;) {
//	         out.println("vorhandenes attribut: " + e.nextElement() + "<br>");
//}
%>
<fmt:setLocale value='<%= lang %>'/>
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="<%= headlineKey %>" /></div>
<form action="<%= NavServlet.getNavigationBaseURL() %>resortresult" method="get" id="resortForm">
<input type="hidden" name="resultlistType" value="<%= resultlistType %>">
<table cellspacing="0" cellpadding="0">
<tr>
<td class="resort">
	<input type="hidden" name="query" value="<%= strQuery %>">
	<select name="field1">
        <option value="modified" <%= formatter.isSorted(sortby,1,"field","modified")? "selected ":"" %> ><fmt:message key="SR.sort-modified" /></option>    
		<option value="title" <%= formatter.isSorted(sortby,1,"field","title")? "selected ":"" %> ><fmt:message key="SR.sort-title" /></option>
		<option value="author" <%= formatter.isSorted(sortby,1,"field","author")? "selected ":"" %> ><fmt:message key="SR.sort-author" /></option>
	</select>
	<select name="order1">
		<option value="ascending" <%= formatter.isSorted(sortby,1,"order","ascending")? "selected ":"" %> ><fmt:message key="SR.ascending" /></option>
		<option value="descending" <%= formatter.isSorted(sortby,1,"order","descending")? "selected ":"" %> ><fmt:message key="SR.descending" /></option>
	</select>
	<input value="Sortiere Ergebnisliste neu" class="resort" type="submit">
</td>
<td class="resultCount"><strong><%= len %> <fmt:message key="SR.foundMCRObjects" /></strong></td>
</tr>
</table>
</form>

<table cellspacing="0" cellpadding="0" id="resultList">
<%
	int max = offset + size;
	if (max > len)
		max = len;

	for (int k = offset; k < max; k++){
        String hitID = result.getHit(k).getID();
        org.jdom.Document hit = mcr_obj.receiveJDOMFromDatastore(hitID);

        Element mycoreobject = hit.getRootElement();
        Element metadata = mycoreobject.getChild("metadata");
        StringBuffer doclink = new StringBuffer(NavServlet.getNavigationBaseURL())
            .append("nav?path=~docdetail&id=").append(hitID)
            .append("&offset=").append(k);
        String mcrID = mycoreobject.getAttributeValue("ID");
        String docType = mcrID.substring(mcrID.indexOf("_")+1,mcrID.lastIndexOf("_"));
        if (JSPUtils.isDocument(docType)) {
        %>
           <tr>
             <td class="resultTitle">
                <a href="<%= doclink.toString() %>"><%= formatter.getSingleXPathValue(metadata,"titles/title[@xml:lang = '" + lang +"']") %></a>
             </td>
             <td class="author">
                <a href="TODOLINKAUTHOR"> <%= !formatter.getSingleXPathValue(metadata,"creatorlinks/creatorlink/@xlink:title").equals("") ?
                                                 formatter.getSingleXPathValue(metadata,"creatorlinks/creatorlink/@xlink:title"):
                                                 formatter.getSingleXPathValue(metadata,"creators/creator")   %>&gt;&gt;</a>
             </td>
           </tr>
           <tr>
              <td class="description" colspan="2">
                 <div class="description"><%= formatter.getSingleXPathValue(metadata,"descriptions/description[@xml:lang = '" + lang +"']") %></div>
                 <span>
                    <%= formatter.getCategoryText(metadata,"formats/format",lang) %>,
                    <%= formatter.getCategoryText(metadata,"types/type",lang) %>,
                    <%= formatter.getSingleXPathValue(mycoreobject,"@ID") %>,    
                    <fmt:message key="SR.changedat" /> <%= formatter.getSingleXPathValue(mycoreobject,"service/servdates/servdate[@type='modifydate']") %>
                 </span>
              </td>
           </tr>        
        <%
        }else if (JSPUtils.isAuthor(docType) || JSPUtils.isInstitution(docType)) {
        %>
           <tr>
             <td colspan="2" class="resultTitle">
                <a href="<%= doclink.toString() %>"><%= formatter.getSingleXPathValue(metadata,"names/name/fullname") %></a>
             </td>
           </tr>
           <tr>
              <td class="description" colspan="2">
                 <span>
                    <%= mcrID %>,
                    <fmt:message key="SR.changedat" /> <%= formatter.getSingleXPathValue(mycoreobject,"service/servdates/servdate[@type='modifydate']") %>
                 </span>
              </td>
           </tr>                
        <%
        }
	}
%>
</table>

	<br>&nbsp;<br>
	<div id="pageSelection">
		<strong><fmt:message key="SR.hitlists" />:  <%= buildHitLists(len, size, offset, path) %> </strong>
	</div>
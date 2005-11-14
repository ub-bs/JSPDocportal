<%@ page import="org.mycore.datamodel.metadata.MCRObject,
                 org.mycore.datamodel.metadata.MCRObjectID,
                 org.mycore.frontend.servlets.MCRServlet,
				 java.util.HashMap,
                 java.util.Enumeration,
				 org.jdom.Element,
				 org.jdom.Document,
                 org.apache.log4j.Logger"%>
<%@ page import="org.jdom.Document" %>
<%@ page import="org.mycore.frontend.jsp.query.MCRResultFormatter" %>
<%@ page import="org.mycore.datamodel.metadata.MCRObject" %>
<%@ page import="org.mycore.common.MCRConfiguration" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>                 
<%!
	//Query query;
	Document result = new Document();
    String docType = "";
	String docFieldResource = "";
	MCRObject mcr_obj = new MCRObject();
    Document doc;
	int offset = 0;
	int size = 100;
	int len = 0;
%>
<%
    String id = request.getParameter("id");
    if (id == null) id = (String) request.getAttribute("id");
    String lang = (String) request.getAttribute("lang");
    
    try {
       	MCRObjectID mcrid = new MCRObjectID(id);
		doc = mcr_obj.receiveJDOMFromDatastore(id);
        docType = mcrid.getTypeId();
    } catch (Exception e) {
        Logger.getLogger("docdetails.jsp").warn(" The ID " + id + " is not a MCRObjectID!");
        request.setAttribute("message","The ID " + id + " is not valid!");
        getServletContext().getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);
        return;
    }
    
	//get all url parameters
    Enumeration paramNames = request.getParameterNames();
    while(paramNames.hasMoreElements()) {
      	String parameter = (String)paramNames.nextElement();
      	if (parameter.equals("offset"))
      		offset = Integer.parseInt(request.getParameter(parameter));
      	if (parameter.equals("size"))
      		size = Integer.parseInt(request.getParameter(parameter));
    }

    request.setAttribute("doc",doc);
    
    // read the searchfield-configuration-file
    docFieldResource = new StringBuffer("docdetails-").append(docType).append(".xml").toString();
    Document docFields = MCRResultFormatter.parseXmlClassResource(docFieldResource);
    request.setAttribute("docFields",docFields);
   
%>

<fmt:setLocale value='<%= lang %>'/>
<fmt:setBundle basename='messages'/>
<div class="headline"><fmt:message key="<%= new StringBuffer("single-result-headline-for-").append(docType).toString() %>" /></div>    
<c:catch var="e">
<c:import url="/content/docdetails-format.jsp" />
</c:catch>
<c:if test="${e!=null}">The caught exception is:
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>



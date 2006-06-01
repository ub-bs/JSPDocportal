<%@ page import="org.jdom.Element,
				 org.jdom.Document,
                 org.apache.log4j.Logger"%>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>                 
<%
        XMLOutputter xmlout = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
        
        String creatorID = request.getParameter("id");
        String creatorName = request.getParameter("name");

        if (creatorID == null) creatorID = "";
        if (creatorName == null) creatorName = "";
        
        Element query = new Element("query");
        query.setAttribute("maxResults","100");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","xml");
        
        Element or = new Element("boolean");
        or.setAttribute("operator","or");
        
        Element creatorCondition = new Element("condition");
        
        creatorCondition.setAttribute("field","creator");
        creatorCondition.setAttribute("value",creatorName);
        creatorCondition.setAttribute("operator","like");
        
        Element creatorIDCondition = new Element("condition");
        creatorIDCondition.setAttribute("field","creatorID");
        creatorIDCondition.setAttribute("value",creatorID);
        creatorIDCondition.setAttribute("operator","like");
        
        Element hosts = new Element("hosts");
        
        Element host = new Element("host");
        host.setAttribute("field","local");
        
        Element types = new Element("types");
        
        Element type = new Element("type");
        type.setAttribute("field","alldocs");
        
        types.addContent(type);
        hosts.addContent(host);
        
        if (!creatorID.equals(""))
            or.addContent(creatorIDCondition);
        if (!creatorName.equals("")) 
            or.addContent(creatorCondition);

        conditions.addContent(or);
        query.addContent(conditions);
        query.addContent(types);
        query.addContent(hosts);
        
        Document queryDoc = new Document(query);
        
        Logger.getLogger("authorsdocuments.jsp").debug("selfcreated query: \n" + xmlout.outputString(queryDoc));
        
        request.setAttribute("query", queryDoc);
        getServletContext().getRequestDispatcher("/nav?path=~searchresult-simple").forward(request, response);
%>        



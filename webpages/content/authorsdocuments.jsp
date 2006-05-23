<%@ page import="org.jdom.Element,
				 org.jdom.Document,
                 org.apache.log4j.Logger"%>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>                 
<%
        XMLOutputter xmlout = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
        
        String resultlistType = "authorjoin";
        
        String creatorID = request.getParameter("id");
        if (creatorID == null) creatorID = "";
        String creatorName = request.getParameter("name");
        if (creatorName == null) creatorName = "";
        
        Element query = new Element("query");
        query.setAttribute("resultType","~searchresult-" + resultlistType);
        query.setAttribute("maxResults","100");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","xml");
        
        Element or = new Element("or");
        
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
        
        Element sortby = new Element("sortby");
        
        Element sortfield = new Element("field");
        sortfield.setAttribute("field","title");
        sortfield.setAttribute("order","ascending");

        sortby.addContent(sortfield);
        
        types.addContent(type);
        hosts.addContent(host);
        
        if (!creatorID.equals(""))
            or.addContent(creatorIDCondition);
        if (!creatorName.equals("")) 
            or.addContent(creatorCondition);
        conditions.addContent(or);
        query.addContent(conditions);
        query.addContent(hosts);
        query.addContent(types);
        query.addContent(sortby);        
        
        Document queryDoc = new Document(query);
        
        Logger.getLogger("authorsdocuments.jsp").debug("selfcreated query: \n" + xmlout.outputString(queryDoc));
        
        request.setAttribute("query", queryDoc);
        getServletContext().getRequestDispatcher("/nav?path=~searchresult-" + resultlistType).forward(request, response);
%>        



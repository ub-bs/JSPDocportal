<%@ page import="org.jdom.Element,
				 org.jdom.Document,
				 org.apache.log4j.Logger"%>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>                 
<%
        XMLOutputter xmlout = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
        String searchField = request.getParameter("searchField");
        String searchValue = request.getParameter("searchValue");
        if ( (searchField == null) || (searchValue == null) ) {
	    	request.setAttribute("message","missing one parameter of: searchField,searchValue,searchClass,searchType");
	   	    getServletContext().getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);             
        }
        
        /***
        <query mask="-" maxResults="0" numPerPage="0">
		  <conditions format="text">origin = Unis.TUMuenchen</conditions>
		</query>
		***/

        String resultlistType = "class" + searchField;
        Element query = new Element("query");
        //query.setAttribute("resultType","~searchresult-" + resultlistType);
        query.setAttribute("maxResults","0");
        query.setAttribute("numPerPage","0");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","text");
        conditions.addContent(searchField + " = " + searchValue);
        query.addContent(conditions);
        Document queryDoc = new Document(query);
        
        Logger.getLogger("browse-search.jsp").debug("selfcreated query: \n" + xmlout.outputString(queryDoc));       
        request.setAttribute("query", queryDoc);
        request.setAttribute("resultlistType",resultlistType);
        getServletContext().getRequestDispatcher("/nav?path=~searchresult-" + resultlistType).forward(request, response);
%>        



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
        OLD !!!
        <query mask="-" maxResults="0" numPerPage="0">
		  <conditions format="text">origin = Unis.TUMuenchen</conditions>
		</query>
		
		NEW !!!
		<query maxResults="100" numPerPage="10">
        <conditions format="xml">
          <boolean operator="and">
            <condition value="TYPE0008" field="type" operator="like" />
          </boolean>
         </conditions>
        </query>        
        ******/

        String resultlistType = "class" + searchField;
        Element query = new Element("query");
        query.setAttribute("maxResults","0");
        query.setAttribute("numPerPage","30");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","xml");
        
        Element and = new Element("boolean");
        and.setAttribute("operator","and");

        Element condition = new Element("condition");
        condition.setAttribute("field",searchField);
        condition.setAttribute("value",searchValue);
        condition.setAttribute("operator","like");

        and.addContent(condition);
        conditions.addContent(and);
        query.addContent(conditions);
        Document queryDoc = new Document(query);

        Logger.getLogger("browse-search.jsp").debug("selfcreated query: \n" + xmlout.outputString(queryDoc));       
        request.setAttribute("query", queryDoc);
        request.setAttribute("resultlistType",resultlistType);
        getServletContext().getRequestDispatcher("/nav?path=~searchresult-" + resultlistType).forward(request, response);
%>        



<%@ page import="org.mycore.datamodel.metadata.MCRObject,
                 org.mycore.datamodel.metadata.MCRObjectID,
                 org.mycore.frontend.servlets.MCRServlet,
                 org.mycore.datamodel.classifications.MCRClassification,
				 java.util.HashMap,
                 java.util.Enumeration,
				 org.jdom.Element,
				 org.jdom.filter.ElementFilter,
				 org.jdom.Document,
				 java.util.List,
				 java.util.Iterator,
                 org.apache.log4j.Logger"%>
<%@ page import="org.jdom.Document" %>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ page import="org.mycore.frontend.jsp.format.MCRResultFormatter" %>
<%@ page import="org.mycore.datamodel.metadata.MCRObject" %>
<%@ page import="org.mycore.common.MCRConfiguration" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>                 
<%
        XMLOutputter xmlout = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());
        String searchField = request.getParameter("searchField");
        String searchValue = request.getParameter("searchValue");
        String searchClass = request.getParameter("searchClass");  
        String searchType = request.getParameter("searchType");  
        if ( (searchField == null) || (searchValue == null) ||
             (searchClass == null) || (searchType == null) ) {
	    	request.setAttribute("message","missing one parameter of: searchField,searchValue,searchClass,searchType");
	   	    getServletContext().getRequestDispatcher("/nav?path=~mycore-error").forward(request,response);             
        }
        String resultlistType = "class" + searchField;
		MCRClassification mcr_class = new MCRClassification();
		Document doc = mcr_class.receiveCategoryAsJDOM(searchClass,searchValue);        

        Element query = new Element("query");
        query.setAttribute("resultType","~searchresult-" + resultlistType);
        query.setAttribute("maxResults","100000");
        
        Element conditions = new Element("conditions");
        conditions.setAttribute("format","xml");
        
        Element or = new Element("boolean");
        or.setAttribute("operator", "or");
       
        for(Iterator it=doc.getDescendants(new ElementFilter("category"));it.hasNext();) {
            // categoryIDs like "Unis" and "Unis.Freiburg" for the children are not required anymore
            // we cannot use only the operator "=" because some documents can have more
            // category-values for the same classification
            // and in the searchfield we will find for example: FORMAT0001|FORMAT0002
            Element category = (Element) it.next();
            String catID = category.getAttributeValue("ID");
	        Element condition = new Element("condition");
	        condition.setAttribute("field",searchField);
	        condition.setAttribute("value",catID);
	        condition.setAttribute("operator","="); 
	        or.addContent(condition); 
	        condition = new Element("condition");
	        condition.setAttribute("field",searchField);
	        condition.setAttribute("value",catID + "|");
	        condition.setAttribute("operator","like"); 
	        or.addContent(condition);
	        condition = new Element("condition");
	        condition.setAttribute("field",searchField);
	        condition.setAttribute("value","|" + catID);
	        condition.setAttribute("operator","like"); 
	        or.addContent(condition);	        	        
        }

        Element hosts = new Element("hosts");
        
        Element host = new Element("host");
        host.setAttribute("field","local");
        
        Element types = new Element("types");
        
        Element type = new Element("type");
        type.setAttribute("field",searchType);
        
        Element sortby = new Element("sortBy");
        
        Element sortfield = new Element("field");
        sortfield.setAttribute("field","title");
        sortfield.setAttribute("order","ascending");

        sortby.addContent(sortfield);
        
        types.addContent(type);
        hosts.addContent(host);
        

        conditions.addContent(or);
        query.addContent(conditions);
        query.addContent(hosts);
        query.addContent(types);
        query.addContent(sortby);        
        
        Document queryDoc = new Document(query);
        
        Logger.getLogger("authorsdocuments.jsp").debug("selfcreated query: \n" + xmlout.outputString(queryDoc));
        
        request.setAttribute("query", queryDoc);
        request.setAttribute("resultlistType",resultlistType);

        getServletContext().getRequestDispatcher("/nav?path=~searchresult-" + resultlistType).forward(request, response);
%>        



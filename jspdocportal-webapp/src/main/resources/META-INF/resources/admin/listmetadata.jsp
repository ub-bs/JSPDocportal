<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@page import="org.mycore.common.MCRException"%>
<%@page import="org.mycore.datamodel.common.MCRXMLMetadataManager"%>
<%@page import="java.util.List"%>
<%@page import="org.mycore.backend.hibernate.MCRHIBConnection"%>
<%@page import="org.hibernate.Transaction"%>
<%@page import="org.apache.log4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Links to XML representation of CPR objects</title>
</head>
<body>
<%
    String type = request.getParameter("type"); 
    if(type==null){
    	type="document";
    }
	Logger logger = Logger.getLogger(this.getClass());
    Transaction tx  = MCRHIBConnection.instance().getSession().beginTransaction();
	try{
		List<String> ids = MCRXMLMetadataManager.instance().listIDsOfType(type);
		String url = application.getAttribute("WebApplicationBaseURL").toString()+"resolve/id/${id}?open=xml";
		out.append("<ul>\n");
		for(String id: ids){
			String u = url.replace("${id}", id);
			out.append("<li><a href=\""+u+"\">"+id+"</a></li>\n");
		}		
		out.append("</ul>\n");
    }	
	catch(MCRException e){
		logger.error(e);
		pageContext.getOut().append(e.getMessage());
	}
	finally{
		tx.commit();
	}
	%>
</body>
</html>
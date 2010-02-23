<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="mcr" uri="/WEB-INF/lib/mycore-taglibs.jar" %>
<%@ taglib prefix="mcrdd" uri="http://www.mycore.de/jspdocportal/docdetails" %>
<%-- Parameter: id - the MCR Object ID--%>
<%-- Parameter: fromWF - from Workflow or database --%>

<mcrdd:docdetails mcrID="${param.id}" lang="de" fromWorkflow="${param.fromWF}"> 
	<mcrdd:setnamespace prefix="xlink" uri="http://www.w3.org/1999/xlink" />
    <mcrdd:row select="/mycoreobject/metadata/names/name/fullname" labelkey="OMD.person.title" showInfo="false">
		<mcrdd:item select="./text()" styleName="docdetails-value-title" />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/females/female" labelkey="OMD.gender" showInfo="false">
		<mcrdd:item select="./text()" labelkeyPrefix="OMD.female." />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/institutions/institution" labelkey="OMD.institution" showInfo="false" >
	  	<mcrdd:classificationitem select="." />  
    </mcrdd:row>
  
  	<mcrdd:row select="/mycoreobject/metadata/addresses/address[@type='office']" labelkey="OMD.addressofficial" showInfo="false" >
    	<mcrdd:outputitem select="." var="current">
   			<x:set var="data" select="$current" scope="request" />
   			<jsp:include page="fragments/address.jsp" />
   		</mcrdd:outputitem>
   	</mcrdd:row>   
   	
   	<mcrdd:row select="/mycoreobject/metadata/phones/phone[@type='phone']" labelkey="OMD.phone" showInfo="false">
		<mcrdd:item select="./text()" />              
    </mcrdd:row>
    <mcrdd:row select="/mycoreobject/metadata/professions/profession[@type='profession']" labelkey="OMD.profession" showInfo="false">
		<mcrdd:item select="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/nationals/national" labelkey="OMD.nationality" showInfo="false" >
	  	<mcrdd:classificationitem select="." />  
    </mcrdd:row>
    
       	       
   	<mcrdd:row select="/mycoreobject/metadata/emails/email" labelkey="OMD.email" showInfo="false" >
   		<mcrdd:outputitem select="." var="current">
   			<x:set var="data" select="$current" scope="request" />
   			<jsp:include page="fragments/email.jsp" />
   		</mcrdd:outputitem>   
   	</mcrdd:row>
   	  
    <mcrdd:row select="/mycoreobject/metadata/notes/note" labelkey="OMD.notes" showInfo="false">
		<mcrdd:item select="./text()" />              
    </mcrdd:row>
    	
   	<mcrdd:separator showLine="true"/>
   	 
   	<mcrdd:row select="/mycoreobject" labelkey="OMD.documents-by-author" showInfo="false" >
   		<mcrdd:outputitem select="./@ID" var="current">
   		   <jsp:element name="a">
   		   		<jsp:attribute name="href">${applicationScope.WebApplicationBaseURL}servlets/MCRJSPSearchServlet?mask=~searchstart-index_creators&query=creatorID+=+<x:out select="string($current)"/>+OR+publisherID+=+<x:out select="string($current)"/></jsp:attribute>
   		   		<jsp:body><fmt:message key="OMD.authorjoin-start-search" /></jsp:body>
   		   </jsp:element>
   		</mcrdd:outputitem> 
   	</mcrdd:row>  
   	
  	<mcrdd:separator showLine="true"/>
 	
 	<mcrdd:row select="/mycoreobject/service/servdates/servdate[@type='createdate']" labelkey="OMD.created" showInfo="false">
		<mcrdd:item select="./text()" datePattern="dd. MMMM yyyy" />              
    </mcrdd:row>
 	
 	<mcrdd:row select="/mycoreobject/service/servdates/servdate[@type='modifydate']" labelkey="OMD.changed" showInfo="false">
		<mcrdd:item select="./text()" datePattern="dd. MMMM yyyy" />              
    </mcrdd:row>
 	
   <mcrdd:row select="/mycoreobject" labelkey="OMD.selflink" showInfo="false" >
   		<mcrdd:outputitem select="./@ID" var="current">
   		   <jsp:element name="a">
   		   		<jsp:attribute name="href">${applicationScope.WebApplicationBaseURL}resolve?id=<x:out select="string($current)"/></jsp:attribute>
   		   		<jsp:body>${applicationScope.WebApplicationBaseURL}resolve?id=<x:out select="string($current)"/></jsp:body>
   		   </jsp:element>
   		</mcrdd:outputitem> 
   	</mcrdd:row>  

</mcrdd:docdetails>
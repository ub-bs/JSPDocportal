<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="mcrdd" uri="http://www.mycore.org/jspdocportal/docdetails.tld" %>
<%-- Parameter: id - the MCR Object ID--%>
<%-- Parameter: fromWF - from Workflow or database --%>

<mcrdd:docdetails mcrID="${param.id}" lang="de" fromWorkflow="${param.fromWF}" var="doc" outputStyle="table"> 
	<mcrdd:setnamespace prefix="xlink" uri="http://www.w3.org/1999/xlink" />
    <mcrdd:row select="/mycoreobject/metadata/titles" labelkey="OMD.maintitle" showInfo="false">
		<mcrdd:outputitem select="." var="xml" styleName="docdetails-value-title" >
			<x:forEach select="$doc/mycoreobject/structure/parents/parent">
			    <c:set var="idparam"><x:out select="./@*[local-name()='href']" /></c:set>
	    		<c:set var="volume"><x:out select="$doc/mycoreobject/metadata/volumes/volume/text()" /></c:set>
  				<jsp:include page="fragments/parentdoc.jsp">
					<jsp:param name="mcrid" value="${idparam}" />
					<jsp:param name="volume" value="${volume}" />
				</jsp:include>
			</x:forEach>
			<c:set var="title"><x:out select="$xml/title[@type='short']" /></c:set>
			<c:if test="${empty title}">
				<c:set var="title"><x:out select="$xml/title[1]" /></c:set>
			</c:if>
			<c:out value="${title}" />		
		</mcrdd:outputitem>              
    </mcrdd:row>

    <mcrdd:row select="/mycoreobject/metadata/titles/title[@type='original-sub']" labelkey="OMD.subtitle" showInfo="false">
		<mcrdd:item select="./text()" />              
    </mcrdd:row>
    
    <mcrdd:preview imageWidth="210" labelContains="Cover" /> 
    	
    <mcrdd:row select="/mycoreobject/metadata/identifiers/identifier" labelkey="OMD.identifiers" showInfo="false">
		<mcrdd:item select="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/dates/date/text" labelkey="OMD.Date.publishingyear" showInfo="false">
		<mcrdd:item select="." />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/structure/children/child" labelkey="OMD.children" showInfo="false">
		<mcrdd:outputitem select="." var="xml" >
			<ul style="line-height:1.5em; list-style:none;list-style-position: inside;margin: 0px;padding: 0px;">
					<x:forEach select="$xml">
					      <x:set var="childID"  select="string(./@*[local-name()='href'])" />							
								<jsp:include page="fragments/childdocs.jsp" flush="true" >
				    				<jsp:param name="mcrid" value="${childID}" />
								</jsp:include>						     
					</x:forEach>
			</ul>
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
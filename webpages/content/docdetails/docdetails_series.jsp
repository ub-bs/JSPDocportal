<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ taglib uri="http://www.mycore.de/jspdocportal/docdetails" prefix="mcrdd" %>
<%-- Parameter: id - the MCR Object ID--%>
<%-- Parameter: fromWF - from Workflow or database --%>

<mcrdd:docdetails mcrID="${param.id}" lang="de" fromWorkflow="${param.fromWF}"> 
    <mcrdd:row xpath="/mycoreobject/metadata/titles/title[@type='original-main']" labelkey="OMD.maintitle" showInfo="false">
		<mcrdd:item xpath="./text()"  styleName="docdetails-value-title" />              
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/titles/title[@type='original-sub']" labelkey="OMD.subtitle" showInfo="false">
		<mcrdd:item xpath="./text()" styleName="docdetails-value-title" />              
    </mcrdd:row>
    
    <mcrdd:preview imageWidth="210" labelContains="Cover" /> 
    	
    <mcrdd:row xpath="/mycoreobject/metadata/participationlinks/participationlink | /mycoreobject/metadata/participants/participant" 
               labelkey="OMD.participants" showInfo="false">
    	<mcrdd:item messagekey="OMD.typeOfParticipation." xpath="./@*[local-name()='label' or local-name()='type']" />
    	<mcrdd:textlinkitem xpath="." />
    </mcrdd:row>
    
     <mcrdd:row xpath="/mycoreobject/metadata/dates/date[@type='create']" labelkey="OMD.Date.publishingyear" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="yyyy" />              
    </mcrdd:row>


	<mcrdd:separator showLine="true" />
	
	
    <mcrdd:row xpath="/mycoreobject/metadata/descriptions/description" labelkey="OMD.descriptions" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/keywords/keyword" labelkey="OMD.keywords" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/subjects/subject" labelkey="OMD.class-subjects" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    	<mcrdd:row xpath="/mycoreobject/metadata/types/type" labelkey="OMD.class-types" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:separator showLine="true"/>
    
        <mcrdd:row xpath="/mycoreobject/metadata/origins/origin" labelkey="OMD.class-origins" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/notes/note" labelkey="OMD.notes" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/bkls/bkl" labelkey="OMD.bkl" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:separator showLine="true"/>  
    
    <mcrdd:row xpath="/mycoreobject/metadata/dates/date/text" labelkey="OMD.Date.publishingyear" showInfo="false">
		<mcrdd:item xpath="." />              
    </mcrdd:row>
	
	<mcrdd:row xpath="/mycoreobject/metadata/types/type" labelkey="OMD.class-types" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/formats/format" labelkey="OMD.class-formats" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/sizes/size" labelkey="OMD.sizes" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row xpath="/mycoreobject/metadata/places/place" labelkey="OMD.places" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
  	<mcrdd:separator showLine="true"/>
    
    <mcrdd:row xpath="/mycoreobject/metadata/notes/note" labelkey="OMD.notes" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:separator showLine="true"/>
    
      <mcrdd:row xpath="/mycoreobject/structure/children/child" labelkey="OMD.children" showInfo="false">
		<mcrdd:outputitem xpath="." varxml="xml" >
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
 	
 	<mcrdd:row xpath="/mycoreobject/service/servdates/servdate[@type='createdate']" labelkey="OMD.created" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="dd. MMMM yyyy" />              
    </mcrdd:row>
 	
 	<mcrdd:row xpath="/mycoreobject/service/servdates/servdate[@type='modifydate']" labelkey="OMD.changed" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="dd. MMMM yyyy" />              
    </mcrdd:row>
 	
   <mcrdd:row xpath="/mycoreobject" labelkey="OMD.selflink" showInfo="false" >
   		<mcrdd:outputitem xpath="./@ID" varxml="current">
   		   <jsp:element name="a">
   		   		<jsp:attribute name="href">${applicationScope.WebApplicationBaseURL}resolve?id=<x:out select="string($current)"/></jsp:attribute>
   		   		<jsp:body>${applicationScope.WebApplicationBaseURL}resolve?id=<x:out select="string($current)"/></jsp:body>
   		   </jsp:element>
   		</mcrdd:outputitem> 
   	</mcrdd:row>  

</mcrdd:docdetails>

<?xml version="1.0" encoding="ISO-8859-1"?>

<MCRDocDetails name="field definition for documentdetails of thesis" >
    <MCRDocDetail rowtype="hidden" labelkey="OMD.maintitle" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/titles/title[@type='original-main']" templatetype="tpl-text-values" separator=", " languageRequired="yes" />
    </MCRDocDetail> 
    <MCRDocDetail rowtype="standard" labelkey="OMD.subtitle" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/titles/title[@type='original-sub']" templatetype="tpl-text-values" separator=", " languageRequired="yes" />
    </MCRDocDetail> 
    <!-- -<MCRDocDetail rowtype="standard" labelkey="OMD.identifiers" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/identifiers/identifier" templatetype="tpl-text-values" separator=", " languageRequired="no" />
    </MCRDocDetail> --> 
   <MCRDocDetail rowtype="table" labelkey="OMD.participants" >
   		<MCRDocDetailContent introkey="OMD.typeOfParticipation" xpath="/mycoreobject/metadata/participationlinks/participationlink/@xlink:label" templatetype="tpl-text-messagekey" separator=":&#160;&lt;br&gt;" terminator=":&#160;" languageRequired="no" />
        <MCRDocDetailContent xpath="/mycoreobject/metadata/participationlinks/participationlink" templatetype="tpl-author_links" separator=":&#160;&lt;br&gt;" languageRequired="no" terminator="&lt;br&gt;" />
    </MCRDocDetail>
   <MCRDocDetail rowtype="table" labelkey="OMD.participants" >
        <MCRDocDetailContent introkey="OMD.typeOfParticipation" xpath="/mycoreobject/metadata/participants/participant/@type" templatetype="tpl-text-messagekey" separator=" " terminator=":&#160;" languageRequired="no" />
        <MCRDocDetailContent xpath="/mycoreobject/metadata/participants/participant" templatetype="tpl-text-values" separator=":&#160;&lt;br&gt;" languageRequired="no" terminator="&lt;br&gt;"/>
    </MCRDocDetail>
    <MCRDocDetail rowtype="standard" labelkey="OMD.date" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/dates/date[@type='create']" templatetype="tpl-date-valuesMinimal" separator=", " languageRequired="no" introkey="datecreated" />
    </MCRDocDetail> 
    <MCRDocDetail rowtype="space" labelkey="" separator="" />    
	<MCRDocDetail rowtype="standard" labelkey="OMD.class-types" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/types/type" templatetype="tpl-classification" separator=", " languageRequired="yes" />
    </MCRDocDetail>        
    <MCRDocDetail rowtype="standard" labelkey="OMD.descriptions" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/descriptions/description" templatetype="tpl-text-values" separator="&lt;br&gt;&lt;br&gt;" languageRequired="no" escapeXml="false" />
    </MCRDocDetail>     
    <MCRDocDetail rowtype="standard" labelkey="OMD.keywords" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/keywords/keyword" templatetype="tpl-text-values" separator="&lt;br&gt;" languageRequired="no" />
    </MCRDocDetail>     
    <MCRDocDetail rowtype="space" labelkey="" separator="" />     
    <MCRDocDetail rowtype="standard" labelkey="OMD.class-subjects" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/subjects/subject" templatetype="tpl-classification" separator="&lt;br&gt;" languageRequired="yes" />
    </MCRDocDetail>    
      
    <MCRDocDetail rowtype="space" labelkey="" separator="" />     
     <MCRDocDetail rowtype="standard" labelkey="OMD.notes" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/notes/note" templatetype="tpl-text-values" separator=", " languageRequired="yes" />
    </MCRDocDetail>   
	<MCRDocDetail rowtype="space" labelkey="" separator="" />   
     <MCRDocDetail rowtype="children" labelkey="OMD.children" >    
       <MCRDocDetailContent xpath="/mycoreobject/structure/children/child" type="document" templatetype="tpl-child" separator="&lt;br&gt;" languageRequired="no" />    
    </MCRDocDetail>
    <MCRDocDetail rowtype="space" labelkey="" separator="" />
    <MCRDocDetail rowtype="standard" labelkey="OMD.created" >
        <MCRDocDetailContent xpath="/mycoreobject/service/servdates/servdate[@type='createdate']" templatetype="tpl-date-values" separator=", " languageRequired="no" />
    </MCRDocDetail>
    <MCRDocDetail rowtype="standard" labelkey="OMD.changed" >
        <MCRDocDetailContent xpath="/mycoreobject/service/servdates/servdate[@type='modifydate']" templatetype="tpl-date-values" separator=", " languageRequired="no" />
    </MCRDocDetail> 
    <MCRDocDetail rowtype="hidden" labelkey="OMD.documents" >    
       <MCRDocDetailContent xpath="/mycoreobject/structure/derobjects/derobject" type="document" templatetype="tpl-alldocument" separator="br" languageRequired="no" />    
    </MCRDocDetail>
</MCRDocDetails>

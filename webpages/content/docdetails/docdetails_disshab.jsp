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
    <mcrdd:row select="/mycoreobject/metadata/titles/title[@type='original-main']" labelkey="OMD.maintitle" showInfo="false">
		<mcrdd:item xpath="./text()" styleName="docdetails-value-title" />              
    </mcrdd:row>
    <mcrdd:row select="/mycoreobject/metadata/titles/title[@type='original-sub']" labelkey="OMD.subtitle" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    <mcrdd:row select="/mycoreobject/metadata/identifiers/identifier" labelkey="OMD.identifiers" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/creatorlinks/creatorlink | /mycoreobject/metadata/creators/creator" 
               labelkey="OMD.author" showInfo="false">
    	<mcrdd:textlinkitem xpath="." />
    </mcrdd:row>

    <mcrdd:row select="/mycoreobject/metadata/contriblinks/contriblink | /mycoreobject/metadata/contributors/contributor[@type='advisor']/fullname" 
               labelkey="OMD.advisor" showInfo="false">
    	<mcrdd:textlinkitem xpath="." />
    </mcrdd:row>

    <mcrdd:row select="/mycoreobject/metadata/origins/origin" labelkey="OMD.class-origins" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/institutions/institution" labelkey="OMD.institution" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>

	<mcrdd:row select="/mycoreobject/metadata/dates/date[@type='submitted']" labelkey="OMD.yearsubmitted" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="yyyy" />              
    </mcrdd:row>

	<mcrdd:row select="/mycoreobject/metadata/dates/date[@type='accepted']" labelkey="OMD.yearaccepted" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="yyyy" />              
    </mcrdd:row>
        
    <mcrdd:separator showLine="true"/>  
     
    <mcrdd:row select="/mycoreobject/structure/derobjects/derobject" labelkey="OMD.documents" showInfo="false" >
  		<mcrdd:derivatelist xpath="." showsize="true" />
  	</mcrdd:row>   
	
	<mcrdd:separator showLine="true"/>  
	
	<mcrdd:row select="/mycoreobject/metadata/types/type" labelkey="OMD.class-types" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/formats/format" labelkey="OMD.class-formats" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/descriptions/description" labelkey="OMD.descriptions" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/keywords/keyword" labelkey="OMD.keywords" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>

  	<mcrdd:separator showLine="false"/>
    
	<mcrdd:row select="/mycoreobject/metadata/subjects/subject" labelkey="OMD.class-subjects" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/bkls/bkl" labelkey="OMD.bkl" showInfo="false" >
	  	<mcrdd:classificationitem xpath="." />  
    </mcrdd:row>
    
    <mcrdd:separator showLine="true"/>
  
    <mcrdd:row select="/mycoreobject/metadata/sources/source" labelkey="OMD.sources" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:row select="/mycoreobject/metadata/coverages/coverage" labelkey="OMD.coverages" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>

    <mcrdd:row select="/mycoreobject/metadata/notes/note" labelkey="OMD.notes" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>

    <mcrdd:row select="/mycoreobject/metadata/sizes/size" labelkey="OMD.sizes" showInfo="false">
		<mcrdd:item xpath="./text()" />              
    </mcrdd:row>
    
    <mcrdd:separator showLine="true"/>
    
    <mcrdd:row select="/mycoreobject/metadata/urns/urn" labelkey="OMD.urns" showInfo="false" >
   		<mcrdd:outputitem xpath="." varxml="current">
   		   <jsp:element name="a">
   		   		<jsp:attribute name="href">http://nbn-resolving.de/<x:out select="string($current)"/></jsp:attribute>
   		   		<jsp:body><x:out select="string($current)"/></jsp:body>
   		   </jsp:element>
   		</mcrdd:outputitem> 
   	</mcrdd:row>  
    
    <mcrdd:separator showLine="true"/>
 	
 	<mcrdd:row select="/mycoreobject/service/servdates/servdate[@type='createdate']" labelkey="OMD.created" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="dd. MMMM yyyy" />              
    </mcrdd:row>
 	
 	<mcrdd:row select="/mycoreobject/service/servdates/servdate[@type='modifydate']" labelkey="OMD.changed" showInfo="false">
		<mcrdd:item xpath="./text()" datePattern="dd. MMMM yyyy" />              
    </mcrdd:row>
 	
   <mcrdd:row select="/mycoreobject/metadata/urns/urn" labelkey="OMD.selflink" showInfo="false" >
   		<mcrdd:outputitem xpath="." varxml="current">
   		   <jsp:element name="a">
   		   		<jsp:attribute name="href">${applicationScope.WebApplicationBaseURL}resolve?urn=<x:out select="string($current)"/></jsp:attribute>
   		   		<jsp:body>${applicationScope.WebApplicationBaseURL}resolve?urn=<x:out select="string($current)"/></jsp:body>
   		   </jsp:element>
   		</mcrdd:outputitem> 
   	</mcrdd:row>  

</mcrdd:docdetails>
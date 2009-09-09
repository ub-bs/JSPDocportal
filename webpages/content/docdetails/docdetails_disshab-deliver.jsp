<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ taglib uri="http://www.mycore.de/jspdocportal/docdetails" prefix="mcrdd" %>
<%@ page import="org.apache.log4j.Logger" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<%@page import="org.hibernate.Transaction"%>
<%@page import="org.mycore.backend.hibernate.MCRHIBConnection"%>
<html>
     <head>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">        
        <title>
         Abgabeformular - elektronische Dissertation 
        </title>
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_general.css" />
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_navigation.css" />
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_content.css" />
		<link type="text/css" rel="stylesheet" href="${baseURL}css/style_docdetails.css" />
	</head>

<body>

<c:catch var="e">
<c:set var="mcrid" value="${param.id}" /> 
<c:set var="host" value="${param.host}" />
<c:set var="offset" value="${param.offset}" />
<c:set var="size" value="${param.size}" />
<c:set var="from" value="${param.fromWF}" />

<c:set var="debug" value="false" />

<table cellpadding="3" cellspacing="3"  width="90%">
<tr>
	<td id="contentArea" width="100%">
	<div id="contentWrapper">
		<div class="headline"> 
		   <fmt:message key="Webpage.intro.xmetadiss.deliver" />
		</div>
		<% Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction(); %>
	  <mcr:includeWebContent file="workflow/form_disshab_deliver.html" />
	  <% tx.commit(); %>
    <hr/>
    <div>
    
		<mcrdd:docdetails mcrID="${mcrid}" lang="de" fromWorkflow="${from}">
			
			<mcrdd:row xpath="/mycoreobject/metadata/creatorlinks/creatorlink | /mycoreobject/metadata/creators/creator" 
            		   labelkey="OMD.author" showInfo="false">
            	<mcrdd:textlinkitem xpath="." />
            </mcrdd:row>
            
            <mcrdd:row xpath="/mycoreobject/metadata/titles/title[@type='original-main']" labelkey="OMD.maintitle" showInfo="false">
            	<mcrdd:item xpath="./text()" />
            </mcrdd:row>
            
            <mcrdd:row xpath="/mycoreobject/metadata/titles/title[@type='translated-main']" labelkey="OMD.translated-maintitle" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/titles/title[@type='original-sub']" labelkey="OMD.subtitle" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/titles/title[@type='translated-sub']" labelkey="OMD.translated-subtitle" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/urns/urn" labelkey="OMD.urns" showInfo="false" >
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/origins/origin" labelkey="OMD.class-origins" showInfo="false" >
				<mcrdd:classificationitem xpath="." />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row xpath="/mycoreobject/metadata/descriptions/description" labelkey="OMD.descriptions" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/types/type" labelkey="OMD.class-types" showInfo="false" >
				<mcrdd:classificationitem xpath="." />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row xpath="/mycoreobject/metadata/dates/date[@type='submitted']" labelkey="OMD.yearsubmitted" showInfo="false">
				<mcrdd:item xpath="./text()" datePattern="yyyy" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/dates/date[@type='accepted']" labelkey="OMD.yearaccepted" showInfo="false">
				<mcrdd:item xpath="./text()" datePattern="yyyy" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/contributors/contributor[@type='advisor']/fullname" labelkey="OMD.advisor" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/contributors/contributor[@type='referee']/fullname" labelkey="OMD.referee" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row xpath="/mycoreobject/metadata/keywords/keyword" labelkey="OMD.keywords" showInfo="false">
				<mcrdd:item xpath="./text()" />
			</mcrdd:row>
			
			<mcrdd:row xpath="/mycoreobject/metadata/subjects/subject" labelkey="OMD.class-subjects" showInfo="false" >
				<mcrdd:classificationitem xpath="." />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row xpath="/mycoreobject/@ID" labelkey="OMD.id" showInfo="false">
				<mcrdd:item xpath="." />
			</mcrdd:row>
		</mcrdd:docdetails>
  	</div>
  	<hr />
  	<% Transaction tx2 = MCRHIBConnection.instance().getSession().beginTransaction(); %>
   	<mcr:includeWebContent file="workflow/form_disshab_deliver_footer.html" />
   	<%tx2.commit(); %>
   	<hr />
  	<table border="0" width="100%">
  		<tr height="50"><td>&#160;</td></tr>	
  		<tr><td class="metaname" ><u><b>Datum</b></u></td><td class="metaname" ><u><b>Unterschrift</b></u></td><td class="metaname" ><u><b>Ort</b></u></td></tr>
  	</table>
  	</div>
  	</td></tr></table>
  
</c:catch>

<c:if test="${e!=null}">
	Es ist ein Fehler bei der Erzeugung des Abgabeformulars aufgetreten. Haben Sie alle Datenfelder ausgefüllt?
	Wenn ja, wenden Sie sich an den Administrator (digibib.ub@uni-rostock.de).
	<% 
  		Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
	%>
</c:if>


   </td></tr>
  </table>
<hr/>
	<p>Bitte drucken Sie das Formular aus und geben es zusammen mit den Pflichtexemplaren in der Universitätsbibliothek Rostock ab.</p>

</body>
</html>

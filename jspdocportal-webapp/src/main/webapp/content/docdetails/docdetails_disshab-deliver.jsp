<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="mcrdd" uri="http://www.mycore.org/jspdocportal/docdetails.tld" %>
<%@ page import="org.apache.log4j.Logger" %>

<c:set  var="baseURL" value="${applicationScope.WebApplicationBaseURL}"/>

<%@page import="org.hibernate.Transaction"%>
<%@page import="org.mycore.backend.hibernate.MCRHIBConnection"%>
<html>
     <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
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
		<h2> 
		   <fmt:message key="Webpage.intro.xmetadiss.deliver" />
		</h2>
		<% Transaction tx = MCRHIBConnection.instance().getSession().beginTransaction(); %>
	  <mcr:includeWebContent file="workflow/form_disshab_deliver.html" />
	  <% tx.commit(); %>
    <hr/>
    <div>
    
		<mcrdd:docdetails mcrID="${mcrid}" lang="de" fromWorkflow="${from}" outputStyle="table">
			<mcrdd:setnamespace prefix="xlink" uri="http://www.w3.org/1999/xlink" />
			
			<mcrdd:row select="/mycoreobject/metadata/creatorlinks/creatorlink | /mycoreobject/metadata/creators/creator" 
            		   labelkey="OMD.author" showInfo="false">
            	<mcrdd:linkitem select="." />
            </mcrdd:row>
            
            <mcrdd:row select="/mycoreobject/metadata/titles/title[@type='original-main']" labelkey="OMD.maintitle" showInfo="false">
            	<mcrdd:item select="./text()" />
            </mcrdd:row>
            
            <mcrdd:row select="/mycoreobject/metadata/titles/title[@type='translated-main']" labelkey="OMD.translated-maintitle" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/titles/title[@type='original-sub']" labelkey="OMD.subtitle" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/titles/title[@type='translated-sub']" labelkey="OMD.translated-subtitle" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/urns/urn" labelkey="OMD.urns" showInfo="false" >
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/origins/origin" labelkey="OMD.class-origins" showInfo="false" >
				<mcrdd:classificationitem select="." />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row select="/mycoreobject/metadata/descriptions/description" labelkey="OMD.descriptions" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/types/type" labelkey="OMD.class-types" showInfo="false" >
				<mcrdd:classificationitem select="." />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row select="/mycoreobject/metadata/dates/date[@type='submitted']" labelkey="OMD.yearsubmitted" showInfo="false">
				<mcrdd:item select="./text()" datePattern="yyyy" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/dates/date[@type='accepted']" labelkey="OMD.yearaccepted" showInfo="false">
				<mcrdd:item select="./text()" datePattern="yyyy" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/contributors/contributor[@type='advisor']/fullname" labelkey="OMD.advisor" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/contributors/contributor[@type='referee']/fullname" labelkey="OMD.referee" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row select="/mycoreobject/metadata/keywords/keyword" labelkey="OMD.keywords" showInfo="false">
				<mcrdd:item select="./text()" />
			</mcrdd:row>
			
			<mcrdd:row select="/mycoreobject/metadata/subjects/subject" labelkey="OMD.class-subjects" showInfo="false" >
				<mcrdd:classificationitem select="." />
			</mcrdd:row>
			
			<mcrdd:separator showLine="false"/>
			
			<mcrdd:row select="/mycoreobject/@ID" labelkey="OMD.id" showInfo="false">
				<mcrdd:item select="." />
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

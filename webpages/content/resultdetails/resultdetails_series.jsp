<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.mycore.de/jspdocportal/docdetails" prefix="mcrdd" %>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%-- Parameter: id - the MCR Object ID--%>
<%-- Parameter: url - the MCR Object ID--%>

<mcr:receiveMcrObjAsJdom mcrid="${param.id}" varDom="xml"/>
<c:set var="type" value="${fn:substringBefore(fn:substringAfter(param.id, '_'),'_')}" />
<c:set var="contentType"><x:out select="$xml/mycoreobject/metadata/types/type/@categid"/></c:set>

<table class="resultdetails-table">
	<tr>
		<td class="resultdetails-icon" rowspan="10">
		<c:choose>
			<c:when test="${fn:contains('series', type) and fn:contains('TYPE0004.001', contentType)}">
				<img src="${WebApplicationBaseURL}/images/pubtype/zeitschrift.gif" align="middle"
					alt="icon" />
			</c:when>
			<c:otherwise>
				<img src="${WebApplicationBaseURL}/images/pubtype/series-volume.gif" alt="icon" align="middle" />
					
			</c:otherwise>
		</c:choose>
		</td>
		<td class="resultdetails-header">
			<c:set var="title"><x:out select="$xml/mycoreobject/metadata/titles/title[1]" /></c:set>
			<a href="${param.url}"><b>${title}</b></a>
		</td>
		<td class="resultdetails-id" rowspan="10">
			[<x:out select="$xml/mycoreobject/@ID" />]
			<br /><br />
			<jsp:include page="fragments/showeditbutton.jsp">
				<jsp:param name="mcrid" value="${param.id}" />
			</jsp:include>
		</td>
	</tr>
 
	<x:set var="data1a" select="substring-before(concat($xml/metadata/participationlinks/cparticipationlink[1]/@*[local-name()='title'],'; ',$xml/metadata/participationlinks/participationlink[2]/@*[local-name()='title'],'; ',$xml/participationlinks/participationlink[3]/@*[local-name()='title'],'; ,'),'; ;')" /> 
	<x:set var="data1b" select="substring-before(concat($xml/metadata/participants/participant[1],'; ',$xml/metadata/participants/participant[2],'; ',$xml/metadata/participants/participant[3],'; ,'),'; ;')" />
	<c:set var="data1"><x:out select="$data1a" /></c:set>
	<x:if select="string-length($data1b)>0">
		<c:if test="${fn:length(data1) > 0}">
			<c:set var="data1"><c:out value="${data1}" />;&nbsp;<x:out select="$data1b" /></c:set>
		</c:if>
		<c:if test="${fn:length(data1) eq 0}">
			<c:set var="data1"><x:out select="$data1b" /></c:set>
		</c:if>
	</x:if>
	<c:if test="${fn:length(data1) > 0}"> 
		<tr>
			<td class="resultdetails-value">
				<fmt:message key="OMD.author" />:&nbsp; <c:out value="${data1}" />
			</td>
		</tr>
	</c:if>
	
	<x:set var="data2" select="$xml/mycoreobject/metadata/types/type" /> 
	<x:if select="string-length($data2/@categid)>0">
		<tr>
			<td class="resultdetails-value">
			    <c:set var="classid"><x:out select="$data2/@classid" /></c:set>
			    <c:set var="categid"><x:out select="$data2/@categid" /></c:set>
			    
				<fmt:message key="OMD.class-types" />:&nbsp; 
				<mcr:displayClassificationCategory lang="de" classid="${classid}" categid="${categid}"/>
			</td>
		</tr>
	</x:if>
		
	<x:set var="data4" select="$xml/mycoreobject/metadata/descriptions/description" /> 
	<x:if select="string-length($data4)>0">
		<c:set var="text"><x:out select="$data4" escapeXml="false"/></c:set>
		<c:if test="${fn:length(text)>300}">
			<c:set var="text"><c:out value="${fn:substring(text,0,300)}" escapeXml="false"/>...</c:set>
		</c:if>
		<tr>
			<td class="resultdetails-value">
				<fmt:message key="OMD.descriptions" />:&nbsp; <c:out value="${text}" escapeXml="false"/>
			</td>
		</tr>
	</x:if>
	
	<x:set var="data5" select="$xml/mycoreobject/service/servdates/servdate[@type='modifydate']" />
	<x:if select="string-length($data5)>0">
		<tr>
			<td class="resultdetails-value">
			    <c:set var="x"><x:out select="$data5" /></c:set>
			    <fmt:parseDate value="${x}" pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" var="moddate" />
			    <fmt:message key="OMD.changed" />:&nbsp; <fmt:formatDate value="${moddate}" dateStyle="full" />
			</td>
		</tr>
	</x:if>
 </table>
 
 <MCRDocDetails name="field definition for resultlist of series" >
    <MCRDocDetail rowtype="standard" labelkey="OMD.title" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/titles/title" templatetype="tpl-text-values" separator=", " languageRequired="yes" withResultlistLink="true" />
    </MCRDocDetail> 
   <MCRDocDetail rowtype="standard" labelkey="OMD.author" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/participationlinks/participationlink" templatetype="tpl-author_links" separator=", " languageRequired="no" />
        <MCRDocDetailContent xpath="/mycoreobject/metadata/participants/participant" templatetype="tpl-text-values" separator=", " languageRequired="no" />
    </MCRDocDetail>
    <MCRDocDetail rowtype="standard" labelkey="OMD.class-types" >
        <MCRDocDetailContent xpath="/mycoreobject/metadata/types/type" templatetype="tpl-classification" separator=", " languageRequired="yes" />
    </MCRDocDetail>            
    <MCRDocDetail rowtype="standard" labelkey="OMD.changed" >
        <MCRDocDetailContent xpath="/mycoreobject/service/servdates/servdate[@type='modifydate']" templatetype="tpl-date-values" separator=", " languageRequired="yes" />
    </MCRDocDetail>
</MCRDocDetails>

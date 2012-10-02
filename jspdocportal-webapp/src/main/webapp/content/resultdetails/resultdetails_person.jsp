<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/docdetails.tld" prefix="mcrdd" %>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%-- Parameter: id - the MCR Object ID--%>
<%-- Parameter: url - the MCR Object ID--%>

<mcr:receiveMcrObjAsJdom mcrid="${param.id}" varDom="xml"/>
<c:set var="type" value="${fn:substringBefore(fn:substringAfter(param.id, '_'),'_')}" />
<table class="searchresult-table">
	<tr>
		<td class="searchresult-table-icon" rowspan="10">
			<img src="${WebApplicationBaseURL}images/pubtype/person.gif" alt="${type}">
		</td>
		<td class="searchresult-table-header">
			<c:set var="title"><x:out select="$xml/mycoreobject/metadata/names/name/fullname" /></c:set>
			<a href="${param.url}"><b>${title}</b></a>
		</td>
		<td class="searchresult-table-id" rowspan="10">
			[<x:out select="$xml/mycoreobject/@ID" />]
			<br /><br />
			<jsp:include page="fragments/showeditbutton.jsp">
				<jsp:param name="mcrid" value="${param.id}" />
			</jsp:include>
		</td>
	</tr>
 
	<x:set var="data2" select="$xml/mycoreobject/metadata/institutions/institution" /> 
	<x:if select="string-length($data2/@categid)>0">
		<tr>
			<td class="searchresult-table-value">
			    <c:set var="classid"><x:out select="$data2/@classid" /></c:set>
			    <c:set var="categid"><x:out select="$data2/@categid" /></c:set>
			    
				<fmt:message key="OMD.class-origins" />:&nbsp; 
				<mcr:displayClassificationCategory lang="de" classid="${classid}" categid="${categid}"/>
			</td>
		</tr>
	</x:if>
	
	<x:set var="data5" select="$xml/mycoreobject/service/servdates/servdate[@type='modifydate']" />
	<x:if select="string-length($data5)>0">
		<tr>
			<td class="searchresult-table-value">
			    <c:set var="x"><x:out select="$data5" /></c:set>
			    <fmt:parseDate value="${x}" pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" var="moddate" />
			    <fmt:message key="OMD.changed" />:&nbsp; <fmt:formatDate value="${moddate}" dateStyle="full" />
			</td>
		</tr>
	</x:if>
 </table>
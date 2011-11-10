<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr" %>

<%--request parameter: mcrid, fromWF, pageFragment --%>
<c:if test="${empty jdom}">
	<mcr:receiveMcrObjAsJdom mcrid="${param.mcrid}" var="jdom" fromWF="${param.fromWF}"/>
</c:if>

<c:choose>
	<c:when test="${param.pageFragment=='pagetitle'}">
		<mcr:simpleXpath jdom="${jdom}" xpath="/mycoreobject/metadata/titles/title[1]" />				                         	
	</c:when>
		
	<c:otherwise>
		<b>Wrong "pageFragment"-Parameter</b>
	</c:otherwise>
</c:choose>
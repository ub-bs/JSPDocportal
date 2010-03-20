<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.de/jspdocportal/browsing" prefix="mcrcb" %>

<fmt:setBundle basename="messages" />
<div class="headline"><fmt:message key="Webpage.browse.generalTitle" /></div>
<fmt:message key="Webpage.browse.subitem" /><br /><br />


<%--<mcr:getConfigProperty defaultValue="default" var="searchField" prop="MCR.ClassificationBrowser.${param.browserClass}.SearchField"/>--%>
<c:if test="${param.browserClass=='ddc_sub'}">
<mcrcb:classificationBrowser 
		classification="DocPortal_class_00000009" 
		count="false" hideemptyleaves="false" 
		level="-1" expand="false" 
		linkall="true" 
		showdescription="true" showuri="false" showid="false" />
</c:if>

<c:if test="${param.browserClass} eq 'bkl_sub'">
<mcrcb:classificationBrowser 
		classification="DocPortal_class_00000020" 
		count="true" hideemptyleaves="false" 
		level="1" expand="false" 
		searchfield="bkl" searchmask="~searchstart-classbkl" 
		showdescription="true" showuri="false" showid="false" />
</c:if>
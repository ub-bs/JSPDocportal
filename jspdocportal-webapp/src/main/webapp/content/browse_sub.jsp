<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/browsing.tld" prefix="mcrcb" %>

<fmt:setBundle basename="messages" />
<h2><fmt:message key="Webpage.browse.generalTitle" /></h2>
<fmt:message key="Webpage.browse.subitem" /><br /><br />


<c:if test="${param.browserClass eq 'ddc_sub'}">
<mcrcb:classificationBrowser 
		classification="DocPortal_class_00000009" 
		count="false" hideemptyleaves="false" 
		level="-1" expand="false" 
		linkall="true" 
		showdescription="true" showuri="false" showid="false" />
</c:if>

<c:if test="${param.browserClass eq 'bkl_sub'}">
<mcrcb:classificationBrowser 
		classification="DocPortal_class_00000020" 
		count="true" hideemptyleaves="false" 
		level="1" expand="false" 
		searchfield="bkl" searchmask="~searchstart-classbkl" 
		showdescription="true" showuri="false" showid="false" />
</c:if>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.de/jspdocportal/browsing" prefix="mcrb" %>

<fmt:setBundle basename="messages" />
	<div class="headline"><fmt:message key="Webpage.indexbrowser.${param.searchclass}.title" /></div>
	<fmt:message key="Webpage.indexbrowser.${param.searchclass}.intro" /><br /><br />	
	<mcrb:indexBrowser
		index="${param.searchclass}" />
		

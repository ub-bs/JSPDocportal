<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>

<div class="headline"><fmt:message key="Nav.Admin.EditWebcontent" /></div>
<span class="subtitle"><fmt:message key="Webpage.admin.webcontent.Backup" /></span>
<p><fmt:message key="Webpage.admin.webcontent.Description" /> </p>
<p>
	<a href="<x:out select="concat($WebApplicationBaseURL,'zipwebcontent')" />"
							class="linkButton"><fmt:message key="OMD.zipgenerate" /></a>&#160;&#160;
</p>



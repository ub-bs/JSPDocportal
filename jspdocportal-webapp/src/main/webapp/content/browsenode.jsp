<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mcr" uri="/WEB-INF/lib/mycore-taglibs.jar" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<fmt:message var="pageTitle" key="Webpage.browse.generalTitle" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">
		<div class="headline"><fmt:message key="Webpage.browse.generalTitle" /></div>
 		<div class="textblock2">
        	<div>
        		<mcr:outputNavigation currentPath="${param.path}" mode="toc" id="left"/>
        	</div>
        	<div style="padding-bottom: 50px;">
            	<mcr:includeWebContent file="browsenode.html" />
           	</div>
        </div>        
	 </stripes:layout-component>
</stripes:layout-render>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/base.tld" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.org/jspdocportal/browsing.tld" prefix="mcrb" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<%--	MCR.ClassificationID.Institution=dbhsnb_class_0000000002
		MCR.ClassificationID.Nationality=dbhsnb_class_0000000001
		MCR.ClassificationID.Origin		=dbhsnb_class_0000000002
		MCR.ClassificationID.Language	=dbhsnb_class_0000000004
		MCR.ClassificationID.Type		=dbhsnb_class_0000000005
		MCR.ClassificationID.Format		=dbhsnb_class_0000000006
		#MCR.ClassificationID.DNB		=dbhsnb_class_0000000007
		MCR.ClassificationID.DDC		=dbhsnb_class_0000000009
		MCR.ClassificationID.Bkl		=dbhsnb_class_0000000020
		MCR.ClassificationID.Collection =dbhsnb_class_0000000022
--%>
<fmt:message var="pageTitle" key="Webpage.browse.title.${param.browserClass}" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="contents">

<c:if test="${param.browserClass=='ddc'}">
	<div class="headline"><fmt:message key="Webpage.browse.title.ddc" /></div>
	<fmt:message key="Webpage.browse.intro.ddc" /><br /><br />
	<fmt:message key="Webpage.browse.subitem" />
	<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.DDC" defaultValue="DocPortal_class_00000009"/>
	<mcrb:classificationBrowser
		classification="${clf}" 
		count="true" hideemptyleaves="true" 
		expand="false" 
		searchfield="ddc" searchmask="~searchstart-classddc" 
		showdescription="false" showuri="false" showid="false" />
</c:if>

<c:if test="${param.browserClass=='bkl'}">
	<div class="headline"><fmt:message key="Webpage.browse.title.bkl" /></div>
	<fmt:message key="Webpage.browse.intro.bkl" /><br /><br />
	<fmt:message key="Webpage.browse.subitem" />
	<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.Bkl" defaultValue="DocPortal_class_00000020"/>
	<mcrb:classificationBrowser
		classification="${clf}" 
		count="true" hideemptyleaves="true" 
		expand="false" 
		searchfield="bkl" searchmask="~searchstart-classbkl" 
		showdescription="false" showuri="false" showid="false" />
</c:if>

<c:if test="${param.browserClass=='origin'}">
	<div class="headline"><fmt:message key="Webpage.browse.title.origin" /></div>
	<fmt:message key="Webpage.browse.intro.origin" /><br /><br />
	<fmt:message key="Webpage.browse.subitem" />
	<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.Origin" defaultValue="DocPortal_class_00000002"/>
	<mcrb:classificationBrowser
		classification="${clf}" 
		count="true" hideemptyleaves="true" 
		expand="true" 
		searchfield="origin" searchmask="~searchstart-classorigin" 
		showdescription="false" showuri="false" showid="false" />
</c:if>

<c:if test="${param.browserClass=='type'}">
	<div class="headline"><fmt:message key="Webpage.browse.title.type" /></div>
	<fmt:message key="Webpage.browse.intro.type" /><br /><br />
	<fmt:message key="Webpage.browse.subitem" />
	<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.Type" defaultValue="DocPortal_class_00000005"/>
	<mcrb:classificationBrowser
		classification="${clf}" 
		count="true" hideemptyleaves="true" 
		expand="true" 
		searchfield="type" searchmask="~searchstart-classtype" 
		showdescription="false" showuri="false" showid="false" />
</c:if>

<c:if test="${param.browserClass=='format'}">
	<div class="headline"><fmt:message key="Webpage.browse.title.format" /></div>
	<fmt:message key="Webpage.browse.intro.format" /><br /><br />
	<fmt:message key="Webpage.browse.subitem" />
	<mcr:getConfigProperty var="clf" prop="MCR.ClassificationID.Format" defaultValue="DocPortal_class_00000006"/>
	<mcrb:classificationBrowser
		classification="${clf}" 
		count="true" hideemptyleaves="true" 
		expand="true" 
		searchfield="format" searchmask="~searchstart-classformat" 
		showdescription="false" showuri="false" showid="false" />
</c:if>
	</stripes:layout-component>
</stripes:layout-render>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="mcrb" uri="http://www.mycore.org/jspdocportal/browsing.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>


<fmt:message var="pageTitle" key="Webpage.title.indexbrowser.${param.searchclass}" /> 
<stripes:layout-render name="../WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="2columns">
	<stripes:layout-component name="contents">
		<h2>
			<fmt:message key="Webpage.indexbrowser.${param.searchclass}.title" />
		</h2>
		<fmt:message key="Webpage.indexbrowser.${param.searchclass}.intro" />
		<br />
		<br />
		<c:choose>
			<c:when test="${param.searchclass eq 'bundle_sub'}">
				<mcrb:indexBrowser index="${param.searchclass}" varurl="url" varxml="xml"
					docdetailsurl="nav?path=~searchdocdetail-index_${param.searchclass}&amp;id={0}">
					<div style="padding-bottom: 5px">
						<img border="0" style="vertical-align: middle; padding-right: 10px"
				 			 src="images/greenArrow.gif" alt="" />
						<a href="${url}"> <x:out select="$xml/value/col[@name='title']" /></a>
					</div>
				</mcrb:indexBrowser>
			</c:when>
			<c:when test="${param.searchclass eq 'document-bundle_sub'}">
				<mcrb:indexBrowser index="${param.searchclass}" varurl="url" varxml="xml"
					docdetailsurl="nav?path=~searchdocdetail-index_${param.searchclass}&amp;id={0}">
					<div style="padding-bottom: 5px">
						<img border="0" style="vertical-align: middle; padding-right: 10px"
				 			 src="images/greenArrow.gif" alt="" />
						<a href="${url}"> <x:out select="$xml/value/col[@name='title']" /></a>
					</div>
				</mcrb:indexBrowser>
			</c:when>

			<c:otherwise>
				<mcrb:indexBrowser index="${param.searchclass}" varurl="url" varxml="xml"
					docdetailsurl="nav?path=~searchdocdetail-index_${param.searchclass}&amp;id={0}">
					<div style="padding-bottom: 5px">
						<img border="0" style="vertical-align: middle; padding-right: 10px"
							 src="images/greenArrow.gif" alt="" />
							<a href="${url}"> <x:out select="$xml/value/col[@name='fullname']" /> </a>
					</div>
				</mcrb:indexBrowser>
			</c:otherwise>
		</c:choose>
	</stripes:layout-component>
</stripes:layout-render>

<%-- Sample index item:
    <value pos="17">
      <sort>wustenberg_peter-wilhelm</sort>
      <idx>wustenberg</idx>
      <id>cpr_person_00001914</id>
      <col name="surname">Wüstenberg</col>
      <col name="firstname">Peter-Wilhelm</col>
      <col name="//nameaffixes/nameaffix" />
      <col name="id">cpr_professor_00001914</col>
    </value>  --%>
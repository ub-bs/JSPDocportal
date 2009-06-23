<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>
<%@ taglib uri="http://www.mycore.de/jspdocportal/browsing"
	prefix="mcrb"%>

<fmt:setBundle basename="messages" />
<div class="headline"><fmt:message
	key="Webpage.indexbrowser.${param.searchclass}.title" /></div>
<fmt:message key="Webpage.indexbrowser.${param.searchclass}.intro" />
<br />
<br />
<c:choose>
	<c:when test="${param.searchclass eq 'series_sub'}">
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

<%-- Sample index item:
    <value pos="17">
      <sort>wustenberg_peter-wilhelm</sort>
      <idx>wustenberg</idx>
      <id>cpr_professor_000000001914</id>
      <col name="surname">Wüstenberg</col>
      <col name="firstname">Peter-Wilhelm</col>
      <col name="//nameaffixes/nameaffix" />
      <col name="id">cpr_professor_000000001914</col>
    </value>  --%>
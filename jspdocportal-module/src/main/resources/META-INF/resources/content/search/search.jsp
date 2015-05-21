<%@ page pageEncoding="UTF-8" contentType="application/xhtml+xml; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="mcrb" uri="http://www.mycore.org/jspdocportal/browsing.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<fmt:message var="pageTitle"
	key="Webpage.editor.title.editor.searchmasks.SearchMask_SimpleDocument.xml" />
<stripes:layout-render name="../../WEB-INF/layout/default.jsp"
	pageTitle="${pageTitle}" layout="2columns">
	<stripes:layout-component name="html_header">

	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="ur-box ur-text">
			<h2>${pageTitle}</h2>
			<mcr:includeXEditor editorPath="${actionBean.editorPath}" pageURL="${actionBean.pageURL}"/>

			<%--
				<c:if test="${not empty actionBean.searcher.result}">
					<mp:searchresult searcher="${actionBean.searcher}">
				
						<c:set var="doc" value="${actionBean.searcher.result.xml}" />
						<table class="table table-striped table-hover ur-table">
							<col style="width:15%" />
    						<col style="width:5%" />
    						<col style="width:20%" />
    						<col style="width:30%" />
    						<col style="width:30%" />
							<tr>
								<th>Semester</th>
								<th>Nr</th>
								<th>Vorname</th>
								<th>Nachname <span class="light">[Normalform]</span></th>
								<th>Herkunft <span class="light">[Normalform]</span></th>
							</tr>
						
							<c:set var="start"><x:out select="$doc/response/result/@start" /></c:set>
							<x:forEach var="x" select="$doc/response/result/doc" varStatus="status">
								<c:set var="id"><x:out select="$x/str[@name='id']"/></c:set>
								<c:set var="url">${pageContext.request.contextPath}/id/${id}?_searcher=${actionBean.searcher.id}&amp;_hit=${start+status.index}</c:set>
								<tr onclick="window.location.href='${url}'">
									<td class="text-center"><x:out select="$x/str[@name='semester_anz']"/></td>
									<td class="text-center">
										<c:set var="nr"><x:out select="$x/str[@name='sem_nr']"/></c:set>
										<a href="${url}">
										<mp:dataformatter strikethrough="${fn:contains(nr, '-')}">
	  										&nbsp;${fn:replace(nr, '-','')}&nbsp;
	  									</mp:dataformatter>
	  									</a>
	  								</td>
									<td><x:out select="$x/str[@name='vorname']"/></td>
									<td>
										<x:out select="$x/str[@name='nachname']"/>
										<x:if select="$x/str[@name='nachname_normal']">
											<span class="light">[<x:out select="$x/str[@name='nachname_normal']"/>]</span>
										</x:if>
									</td>
									<td>
										<mp:dataformatter replaceBlank=""><x:out select="$x/str[@name='treffer_anz']"/></mp:dataformatter>
										<x:if select="$x/str[@name='treffer_normal_anz']">
											<span class="light">[<x:out select="$x/str[@name='treffer_normal_anz']"/>]</span>
										</x:if>
									</td>
								</tr>
							</x:forEach>
						</table>
					</mp:searchresult>		
				</c:if>
				--%>
		</div>
		<c:if test="${not empty actionBean.solrResponse}">
			<c:set var="solrResponse" value="${actionBean.solrResponse}" />
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Treffer:
						${actionBean.numFound}</h3>
				</div>
				<div class="panel-body">
					URL: ${solrResponse.requestUrl}
				</div>
				 <div class="panel-footer">Panel footer</div>
			</div>

		</c:if>
	</stripes:layout-component>
</stripes:layout-render>

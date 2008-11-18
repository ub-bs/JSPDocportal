<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

<fmt:setBundle basename="messages" />
	<div id="resortForm">

	<table cellspacing="0" cellpadding="0">
		<tr>
			<td class="resort">
			<form action="${WebApplicationBaseURL}servlets/MCRJSPSearchServlet"	method="get">
			<input type="hidden" name="mode" value="resort">
			<input type="hidden" name="id" value="${resultid}"> 
			<select	name="field1">
					<option value="title"
						<mcr:ifSorted query="${query}" attributeName="name" attributeValue="title">selected</mcr:ifSorted>><fmt:message
						key="Webpage.searchresults.sort-title" /></option>
				
				 	<x:if select="contains($oType, 'disshab') or contains($oType, 'document')">
						<option value="author"
							<mcr:ifSorted query="${query}" attributeName="name" attributeValue="author">selected</mcr:ifSorted>>
							<fmt:message key="Webpage.searchresults.sort-author" /></option>
					</x:if >
					<option value="modified"
						<mcr:ifSorted query="${query}" attributeName="name" attributeValue="modified">selected</mcr:ifSorted>><fmt:message
						key="Webpage.searchresults.sort-modified" /></option>
			</select> 
			<select name="order1">
				<option value="ascending"
					<mcr:ifSorted query="${query}" attributeName="order" attributeValue="ascending">selected</mcr:ifSorted>><fmt:message
					key="Webpage.searchresults.ascending" /></option>
				<option value="descending"
					<mcr:ifSorted query="${query}" attributeName="order" attributeValue="descending">selected</mcr:ifSorted>><fmt:message
					key="Webpage.searchresults.descending" /></option>
			</select> <input value="Sortiere Ergebnisliste neu" class="resort"
				type="submit"></form>

			</td>
			<td class="resultCount"><strong>${totalhits} 
			   <fmt:message key="Webpage.searchresults.foundMCRObjects" /></strong></td>
		</tr>
	</table>
	</div>				
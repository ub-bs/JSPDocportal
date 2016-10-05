
<%@tag import="org.mycore.datamodel.classifications2.MCRCategory"%>
<%@tag import="org.mycore.datamodel.classifications2.MCRCategoryID"%>
<%@tag import="org.mycore.datamodel.classifications2.MCRCategoryDAOFactory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<%@ attribute name="categid" required="true" type="java.lang.String" %>
<%@ attribute name="mask" required="true" type="java.lang.String" %>
<%@ attribute name="facetField" required="false" type="java.lang.String" %>

<% MCRCategory rootCateg = MCRCategoryDAOFactory.getInstance().getCategory(MCRCategoryID.fromString(categid), -1); 
	jspContext.setAttribute("rootCateg", rootCateg);
%>
<h5>${rootCateg.getRoot().currentLabel.get().text}</h5>
<search:browse-classification-inner category="${rootCateg}" facetField="${facetField}" mask="${mask}"/>
<%@tag import="org.mycore.datamodel.metadata.MCRDerivate"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObject"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="mcrid" required="true" type="java.lang.String" %>
<%@ attribute name="labelContains" type="java.lang.String" %>
<%@ attribute name="width" type="java.lang.String"  %>
<%@ attribute name="showFooter" type="java.lang.Boolean"  %>
<%@ attribute name="protectDownload" type="java.lang.Boolean"  %>
<%-- to change the image used for copyright and download protection, override: /images/image_terms_of_use.png --%>

<c:if test="${empty width}">
	<c:set var="width" value="100%" />
</c:if>

<% 
	StringBuffer sb= new StringBuffer();
	MCRObject obj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(jspContext.getAttribute("mcrid"))));
	if(obj==null){
		out.write("<b>No object found for id: "+mcrid+".</b>");
	}
	else{
		String labelSubstring = String.valueOf(jspContext.getAttribute("labelContains"));

		for(MCRMetaLinkID derId: obj.getStructure().getDerivates()){
			boolean goOn = true;
			if(!"null".equals(labelSubstring)){
				goOn = (derId.getXLinkTitle()!=null && derId.getXLinkTitle().contains(labelSubstring))
					|| (derId.getXLinkLabel()!=null && derId.getXLinkLabel().contains(labelSubstring));
			}
			if(goOn){
				MCRDerivate mcrDerivate = MCRMetadataManager.retrieveMCRDerivate(derId.getXLinkHrefID());
				String mainDoc = mcrDerivate.getDerivate().getInternals().getMainDoc();
				String url = "file/"+obj.getId().toString()+"/"+derId.getXLinkHref()+"/"+mainDoc;
				String title="";
				for(String t: mcrDerivate.getService().getFlags("title")){
					title +=t;
				}
				if(sb.length()>0){
	    			sb.append(";");
	    		}
				sb.append(mainDoc+"="+url+"#"+title);
				
			}
		}
		jspContext.setAttribute("data", sb.toString());
	}
%>

<c:if test="${not empty data}">
	<c:forEach var="x" items="${fn:split(data, ';')}">
		<div class="docdetails-image" style="width:${width}">
			<div style="position:relative">
   				<c:if test="${protectDownload}">
   					<img style="opacity:0.01;position:absolute;top:0px;left:0px;width:100%;height:100%;z-index:1" src="${pageContext.request.contextPath}/images/image_terms_of_use.png"/>
	   			</c:if>
   				<img style="position:relative;top:0px;left:0px;width:98%;padding:1%;display:block;" src="${pageContext.request.contextPath}/${fn:substringAfter(fn:substringBefore(x, '#'),'=')}" border="0" width="${width}" alt="${fn:substringBefore(x, '=')}" />
			</div>
			<c:if test="${showFooter}">
  				<div class="docdetails-image-footer" style="margin:1%">
    				${fn:substringAfter(x,'#')}
				</div>
			</c:if>
		</div>
	</c:forEach>
</c:if>
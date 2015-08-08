<%@tag import="org.mycore.datamodel.ifs.MCRFile"%>
<%@tag import="org.mycore.datamodel.ifs.MCRFilesystemNode"%>
<%@tag import="org.mycore.datamodel.ifs.MCRDirectory"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObject"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>

<%@ attribute name="mcrid" required="true" type="java.lang.String" %>
<%@ attribute name="labelContains" type="java.lang.String" %>
<%@ attribute name="width" type="java.lang.String"  %>

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
				MCRDirectory root = MCRDirectory.getRootDirectory(derId.getXLinkHref());
	    			if(root!=null){
	    				MCRFilesystemNode[] myfiles = root.getChildren();
	    				for ( int j=0; j< myfiles.length; j++) {
	    					MCRFile theFile = (MCRFile) myfiles[j];
	    					if ( theFile.getContentTypeID().indexOf("jpeg")>= 0 ||
	    							theFile.getContentTypeID().indexOf("gif")>= 0 ||
	    							theFile.getContentTypeID().indexOf("png")>= 0) {
	    						String url = "file/"+obj.getId().toString()+"/"+derId.getXLinkHref()+"/"+myfiles[j].getName();
	    						if(sb.length()>0){
	    							sb.append(";");
	    						}
	    						sb.append(myfiles[j].getName()+"="+url);
	    					}
	    				}
	    			}			
					
				}
			}
	}
	jspContext.setAttribute("data", sb.toString());
%>

<c:if test="${not empty data}">
	<c:forEach var="x" items="${fn:split(data, ';')}">
		<div class="docdetails-image">
			<img src="${pageContext.request.contextPath}/${fn:substringAfter(x,'=')}" border="0" width="${width}" alt="${fn:substringBefore(x, '=')}" />  
		</div>
	</c:forEach>
</c:if>
<%@page import="net.sourceforge.stripes.action.ActionBean"%>
<%@page import="org.mycore.datamodel.metadata.MCRDerivate"%>
<%@page import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@page import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@page import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@page import="org.mycore.datamodel.metadata.MCRObject"%>
<%@page import="org.mycore.frontend.jsp.MCRHibernateTransactionWrapper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="iviewBaseURL" value="${applicationScope.WebApplicationBaseURL}modules/iview2/" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.6/css/bootstrap.css" />
<link rel="stylesheet" type="text/css" href="${applicationScope.WebApplicationBaseURL}modules/mcrviewer/mcrviewer.css" />
<link rel="stylesheet" type="text/css" href="${iviewBaseURL}css/default.css" />

<script type="text/javascript" src="${applicationScope.WebApplicationBaseURL}webjars/jquery/2.1.1/jquery.min.js"></script>


<script type="text/javascript" src="${iviewBaseURL}js/iview-client-base.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-desktop.js"></script>


<c:if test="${actionBean.doctype eq 'pdf'}">
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-pdf.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/lib/pdf.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-metadata.js"></script>
<style type="text/css">
button[data-id='CloseViewerButton']{
	/*display:none;*/
}
</style>
<script>
	window.onload = function() {
		var config = {
                "mobile": false,
                pdfProviderURL: "${applicationScope.WebApplicationBaseURL}${actionBean.pdfProviderURL}",
                derivate: "${actionBean.recordIdentifier}",
                filePath: "${actionBean.filePath}",
                doctype: "pdf",
                startImage: "1",
                i18nURL: "${applicationScope.WebApplicationBaseURL}modules/mcrviewer/i18n/mcrviewer_{lang}.json",
                lang: "de",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                pdfWorkerURL: "${iviewBaseURL}js/lib/pdf.worker.js",
                "canvas.startup.fitWidth": true,
                "canvas.overview.enabled": false,
                permalink: {
                    enabled: true,
                    updateHistory: true,
                    viewerLocationPattern:"{baseURL}/mcrviewer/id/{derivate}/{file}"
                },
				onClose: function(){
					window.history.back();
					setTimeout(function(){ window.close(); }, 500);
        		}
		};
		new mycore.viewer.MyCoReViewer(jQuery("body"), config);
	};
</script>
</c:if>
<c:if test="${actionBean.doctype eq 'mets'}">
<c:set var="mcrid">${actionBean.mcrid}</c:set>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-mets.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-metadata.js"></script>
<style type="text/css">
button[data-id='CloseViewerButton']{
	/*display:none;*/
}
</style>
<%
	try(MCRHibernateTransactionWrapper htw = new MCRHibernateTransactionWrapper()){
		MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(pageContext.getAttribute("mcrid"))));
		String derLabel = "MCRVIEWER_METS";
		for(MCRMetaLinkID derLink: mcrObj.getStructure().getDerivates()){
			if(derLink.getXLinkTitle().equals(derLabel)){
		    	MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derLink.getXLinkHrefID());
		    	pageContext.setAttribute("maindoc", der.getDerivate().getInternals().getMainDoc());
		    	pageContext.setAttribute("derid", der.getId().toString());
			}
		}
	}
	catch(Exception e){
	    //do nothing
	}
%>

<script>
	window.onload = function() {
		var config = {
                mobile: false,
                doctype: "mets",
                
                derivate: "${fn:replace(actionBean.recordIdentifier,'/','%252F')}",
                filePath: "${actionBean.filePath}",
                metsURL: "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/${maindoc}",
                imageXmlPath: "${applicationScope.WebApplicationBaseURL}tiles",
                tileProviderPath: "${applicationScope.WebApplicationBaseURL}tiles",
                
                i18nURL: "${applicationScope.WebApplicationBaseURL}modules/mcrviewer/i18n/mcrviewer_{lang}.json",
                lang: "de",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
               // derivateURL : "${applicationScope.WebApplicationBaseURL}depot/${fn:replace(actionBean.recordIdentifier,'/','%25252F')}/",
                derivateURL : "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/",
                "canvas.startup.fitWidth": true,
                "canvas.overview.enabled": false,
                permalink: {
                    enabled: true,
                    updateHistory: true,
                    viewerLocationPattern:"{baseURL}/mcrviewer/recordIdentifier/{derivate}/{file}"
                },
                imageOverview : {
                    enabled: true
                },
                chapter: {
                    enabled: true,
                    showOnStart: true
                },
                text: {
                	enabled: true
                },

                onClose: function(){
					window.history.back();
					setTimeout(function(){ window.close(); }, 500);
        		},
                pdfCreatorStyle: "pdf",
                pdfCreatorURI: "http://wrackdm17.thulb.uni-jena.de/mets-printer/pdf",
                objId: ""
              
              
		};
		new mycore.viewer.MyCoReViewer(jQuery("body"), config);
	};
</script>
</c:if>
</head>

<body>

<script type="text/javascript" src="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.6/js/bootstrap.js"></script>

</body>
</html>

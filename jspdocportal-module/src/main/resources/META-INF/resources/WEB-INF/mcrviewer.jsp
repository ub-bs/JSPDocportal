<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="iviewBaseURL" value="${applicationScope.WebApplicationBaseURL}modules/iview2/" />
<!DOCTYPE html>
<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="${applicationScope.WebApplicationBaseURL}webjars/jquery/2.1.1/jquery.min.js" type="text/javascript"></script>
<script src="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.6/js/bootstrap.min.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.6/css/bootstrap.css" />
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-base.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-desktop.js"></script>
<link rel="stylesheet" type="text/css" href="${iviewBaseURL}css/default.css" />

<c:if test="${actionBean.doctype eq 'pdf'}">
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-pdf.js"></script>
<script src="${iviewBaseURL}js/lib/pdf.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-metadata.js"></script>
<style type="text/css">
button[data-id='CloseViewerButton']{
	//display:none;
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
<script src="${iviewBaseURL}js/iview-client-mets.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-metadata.js"></script>
<style type="text/css">
button[data-id='CloseViewerButton']{
	//display:none;
}
</style>
<script>
	window.onload = function() {
		var config = {
                mobile: false,
                doctype: "mets",
                
                derivate: "${fn:replace(actionBean.recordIdentifier,'/','%252F')}",
                filePath: "${actionBean.filePath}",
                metsURL: "${applicationScope.WebApplicationBaseURL}depot/${fn:replace(actionBean.recordIdentifier,'/','%252F')}/${fn:substringAfter(actionBean.recordIdentifier, '/')}.iview2.mets.xml",
                imageXmlPath: "${applicationScope.WebApplicationBaseURL}tiles",
                tileProviderPath: "${applicationScope.WebApplicationBaseURL}tiles",
                
                i18nURL: "${applicationScope.WebApplicationBaseURL}modules/mcrviewer/i18n/mcrviewer_{lang}.json",
                lang: "de",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                derivateURL : "${applicationScope.WebApplicationBaseURL}depot/${fn:replace(actionBean.recordIdentifier,'/','%252F')}/",
                
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
                metadataURL: "",
                objId: "",
              
              
		};
		new mycore.viewer.MyCoReViewer(jQuery("body"), config);
	};
</script>
</c:if>



</head>

<body>
</body>
</html>

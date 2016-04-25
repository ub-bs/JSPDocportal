<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<link rel="stylesheet" type="text/css" href="${iviewBaseURL}/css/default.css" />
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
                derivate: "${actionBean.objectID}",
                filePath: "${actionBean.filePath}",
                doctype: "pdf",
                startImage: "1",
                i18nURL: "${applicationScope.WebApplicationBaseURL}modules/iview2/i18n/{lang}.json",
                lang: "de",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                pdfWorkerURL: "${iviewBaseURL}js/lib/pdf.worker.js",
                "canvas.startup.fitWidth": true,
                "canvas.overview.enabled": false,
                permalink: {
                    enabled: true,
                    updateHistory: true,
                    viewerLocationPattern:"{baseURL}mcrviewer/{derivate}/{file}"
                },
				onClose: function(){
            		window.close();
        		}
                
		};
		new mycore.viewer.MyCoReViewer(jQuery("body"), config);
	};
</script>
</c:if>
</head>
<body>
</body>
</html>

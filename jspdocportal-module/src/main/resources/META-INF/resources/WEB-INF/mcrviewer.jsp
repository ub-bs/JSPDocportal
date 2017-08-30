<%@page import="net.sourceforge.stripes.action.ActionBean"%>
<%@page import="org.mycore.datamodel.metadata.MCRDerivate"%>
<%@page import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@page import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@page import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@page import="org.mycore.datamodel.metadata.MCRObject"%>
<%@page import="org.mycore.frontend.jsp.MCRHibernateTransactionWrapper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="iviewBaseURL" value="${applicationScope.WebApplicationBaseURL}modules/iview2/" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.7/css/bootstrap.css" />
<link type="text/css" rel="stylesheet" href="${WebApplicationBaseURL}webjars/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="${applicationScope.WebApplicationBaseURL}modules/mcrviewer/mcrviewer.css" />
<link rel="stylesheet" type="text/css" href="${iviewBaseURL}css/default.css" />

<script type="text/javascript" src="${applicationScope.WebApplicationBaseURL}webjars/jquery/2.1.4/jquery.min.js"></script>


<script type="text/javascript" src="${iviewBaseURL}js/iview-client-base.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-desktop.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-logo.js"></script>
<script type="text/javascript" src="${iviewBaseURL}js/iview-client-toolbar-extender.js"></script>

<style type="text/css">
      div.mcrviewer_html{
        border: 1px solid lightgrey;
        padding: 5px;
      }

      .mycoreViewer .navbar .navbar-right {
        margin-right: 20px;
      }

      .mycoreViewer .navbar .navbar-left {
        margin-left: 20px;
      }
</style>

<c:if test="${actionBean.doctype eq 'pdf'}">
	<script type="text/javascript" src="${iviewBaseURL}js/iview-client-pdf.js"></script>
	<script type="text/javascript" src="${iviewBaseURL}js/lib/pdf.js"></script>
	<script type="text/javascript" src="${iviewBaseURL}js/iview-client-metadata.js"></script>
	<style type="text/css">
      button[data-id='ShareButton']{
        border-radius:4px;
      }
      
      button[data-id='PdfDownloadButton']{
        display:none;
      }
      
    </style>
	<script>
		window.onload = function() {
			var config = {
				logoURL:"${applicationScope.WebApplicationBaseURL}images/mcrviewer/mcrviewer.png",
				"mobile" : false,
				pdfProviderURL : "${applicationScope.WebApplicationBaseURL}${actionBean.pdfProviderURL}",
				derivate : "${actionBean.recordIdentifier}",
				filePath : "${actionBean.filePath}",
				doctype : "pdf",
				startImage : "1",
				i18nURL : "${applicationScope.WebApplicationBaseURL}rsc/locale/translate/{lang}/component.viewer.*",
				lang : "de",
				webApplicationBaseURL : "${applicationScope.WebApplicationBaseURL}",
				pdfWorkerURL : "${iviewBaseURL}js/lib/pdf.worker.js",
				"canvas.startup.fitWidth" : true,
				"canvas.overview.enabled" : false,
				permalink : {
					enabled : true,
					updateHistory : true,
					viewerLocationPattern : "{baseURL}/mcrviewer/recordIdentifier/${fn:replace(actionBean.recordIdentifier,'/','%252F')}/{file}"
				},
				onClose : function() {
					window.history.back();
					setTimeout(function() {
						window.close();
					}, 500);
				},
				
				toolbar : [ {
								id: "addOns",
								type: "group"
							},
							{
								id : "pdf_download",
								type : "button",
								label : "buttons.pdf_download",
								href : "${applicationScope.WebApplicationBaseURL}${actionBean.pdfProviderURL}",
								icon: "fa-download",
								inGroup: "addOns"
							} ]
			};
			new mycore.viewer.MyCoReViewer(jQuery("body"), config);
		};
	</script>
</c:if>
<c:if test="${actionBean.doctype eq 'mets'}">
	<c:set var="mcrid">${actionBean.mcrid}</c:set>
	<script type="text/javascript" src="${iviewBaseURL}js/iview-client-mets.js"></script>
	<script type="text/javascript" src="${iviewBaseURL}js/iview-client-metadata.js"></script>
	
	<%
	    try (MCRHibernateTransactionWrapper htw = new MCRHibernateTransactionWrapper()) {
						MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(
								MCRObjectID.getInstance(String.valueOf(pageContext.getAttribute("mcrid"))));
						String derLabel = "MCRVIEWER_METS";
						for (MCRMetaLinkID derLink : mcrObj.getStructure().getDerivates()) {
							if (derLink.getXLinkTitle().equals(derLabel)) {
								MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derLink.getXLinkHrefID());
								pageContext.setAttribute("maindoc", der.getDerivate().getInternals().getMainDoc());
								pageContext.setAttribute("derid", der.getId().toString());
							}
						}
					} catch (Exception e) {
						//do nothing
					}
	%>

	<script>
		window.onload = function() {
			var config = {
				mobile : false,
				doctype : "mets",

				//derivate: "${fn:replace(actionBean.recordIdentifier,'/','%252F')}",
				derivate : "${derid}",
				filePath : "${actionBean.filePath}",
				metsURL : "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/${maindoc}",
				imageXmlPath : "${applicationScope.WebApplicationBaseURL}tiles/${fn:replace(actionBean.recordIdentifier,'/','%252F')}/",
				tileProviderPath : "${applicationScope.WebApplicationBaseURL}tiles/${fn:replace(actionBean.recordIdentifier,'/','%252F')}/",

				i18nURL : "${applicationScope.WebApplicationBaseURL}rsc/locale/translate/{lang}/component.viewer.*",
				lang : "de",
				webApplicationBaseURL : "${applicationScope.WebApplicationBaseURL}",
				// derivateURL : "${applicationScope.WebApplicationBaseURL}depot/${fn:replace(actionBean.recordIdentifier,'/','%25252F')}/",
				derivateURL : "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/",
				"canvas.startup.fitWidth" : true,
				"canvas.overview.enabled" : false,
				permalink : {
					enabled : true,
					updateHistory : true,
					viewerLocationPattern : "{baseURL}/mcrviewer/recordIdentifier/${fn:replace(actionBean.recordIdentifier,'/','%252F')}/{file}"
				},
				imageOverview : {
					enabled : true
				},
				chapter : {
					enabled : true,
					showOnStart : true
				},
				text : {
					enabled : true
				},

				onClose : function() {
					window.history.back();
					setTimeout(function() {
						window.close();
					}, 500);
				},
				
				logoURL:"${applicationScope.WebApplicationBaseURL}images/mcrviewer/mcrviewer.png",
				
				toolbar : [{
				        	   id: "addOns",
		                       type: "group"
	                       },
				           {
	                    	   id : "pdf_download",
	                    	   type : "button",
	                    	   label : "buttons.pdf_download",
	                    	   href : "${applicationScope.WebApplicationBaseURL}/pdfdownload/recordIdentifier/${fn:replace(actionBean.recordIdentifier,'/','%252F')}",
	                    	   icon: "fa-download",
	                    	   inGroup: "addOns"
	                   	  }],
	                   	  
				objId : ""

			};
			new mycore.viewer.MyCoReViewer(jQuery("body"), config);
		};
	</script>
</c:if>
</head>

<body>

	<script type="text/javascript" src="${applicationScope.WebApplicationBaseURL}webjars/bootstrap/3.3.7/js/bootstrap.js"></script>

</body>
</html>

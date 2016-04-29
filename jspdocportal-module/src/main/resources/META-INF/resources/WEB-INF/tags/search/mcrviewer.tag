<%@tag import="org.mycore.frontend.jsp.MCRHibernateTransactionWrapper"%>
<%@tag import="org.mycore.datamodel.metadata.MCRDerivate"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@tag import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@tag import="org.mycore.datamodel.metadata.MCRObject"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="mcrid" required="true" type="java.lang.String" %>
<%@ attribute name="recordidentifier" required="true" type="java.lang.String" %>
<%@ attribute name="doctype" required="true" type="java.lang.String" %>


<c:set var="iviewBaseURL" value="${applicationScope.WebApplicationBaseURL}modules/iview2/" />

    <link href="${iviewBaseURL}css/default.css" type="text/css" rel="stylesheet">
    <script src="${iviewBaseURL}js/iview-client-base.js"></script>
    <script src="${iviewBaseURL}js/iview-client-frame.js"></script>
<c:if test="${doctype eq 'pdf' }">
    <script src="${iviewBaseURL}js/iview-client-pdf.js"></script>
    <script src="${iviewBaseURL}js/lib/pdf.js"></script>
	<style type="text/css">
		.mycoreViewer .navbar{
			position: absolute; left: 0px; right: 0px; top: 0px;
		}
	</style>
	
	<%
	try(MCRHibernateTransactionWrapper htw = new MCRHibernateTransactionWrapper()){
		MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(jspContext.getAttribute("mcrid"))));
		String derLabel = "fulltext";
		for(MCRMetaLinkID derLink: mcrObj.getStructure().getDerivates()){
			if(derLink.getXLinkTitle().equals(derLabel)){
		    	MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derLink.getXLinkHrefID());
		    	jspContext.setAttribute("maindoc", der.getDerivate().getInternals().getMainDoc());
		    	jspContext.setAttribute("derid", der.getId().toString());
			}
		}
	}
	catch(Exception e){
	    //do nothing
	}
%>

    <script>
    window.addEventListener("load", function(){
            new mycore.viewer.MyCoReViewer(jQuery("#${id}"), {
                "mobile": false,
                pdfProviderURL: "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/${maindoc}",
                derivate: "${mcrid}",
                filePath: "${maindoc}",
                doctype: "${doctype}",
                startImage: "1",
                i18nURL: "${applicationScope.WebApplicationBaseURL}modules/iview2/i18n/{lang}.json",
                lang: "de",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                pdfWorkerURL: "${iviewBaseURL}js/lib/pdf.worker.js",
                "canvas.startup.fitWidth": false,
                "canvas.overview.enabled": false,
                "chapter.showOnStart": false,
                permalink: {
                    enabled: true,
                    updateHistory: false,
                    viewerLocationPattern:"{baseURL}/mcrviewer/id/{derivate}/{file}"
                }
            });
        });
    </script>
</c:if>


<c:if test="${doctype eq 'mets' }">
    <script src="${iviewBaseURL}js/iview-client-mets.js"></script>
	<style type="text/css">
		.mycoreViewer .navbar{
			position: absolute; left: 0px; right: 0px; top: 0px;
		}
	</style>
<%
	try(MCRHibernateTransactionWrapper htw = new MCRHibernateTransactionWrapper()){
		MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(jspContext.getAttribute("mcrid"))));
		String derLabel = "DV_METS";
		for(MCRMetaLinkID derLink: mcrObj.getStructure().getDerivates()){
			if(derLink.getXLinkTitle().equals(derLabel)){
		    	MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derLink.getXLinkHrefID());
		    	jspContext.setAttribute("maindoc", der.getDerivate().getInternals().getMainDoc());
		    	jspContext.setAttribute("derid", der.getId().toString());
			}
		}
	}
	catch(Exception e){
	    //do nothing
	}
%>

    <script>
    window.addEventListener("load", function(){
            new mycore.viewer.MyCoReViewer(jQuery("#${id}"),  {
            	"mobile": false,
                doctype: "mets",
                metsURL: "${applicationScope.WebApplicationBaseURL}depot/${fn:replace(recordidentifier, '/','%252F')}/${fn:substringAfter(recordidentifier, '/')}.iview2.mets.xml",
                imageXmlPath: "${applicationScope.WebApplicationBaseURL}tiles",
                tileProviderPath: "${applicationScope.WebApplicationBaseURL}tiles",
                filePath: "/phys_0001.iview2",
                derivate: "${fn:replace(recordidentifier,'/','%252F')}",
                i18nURL: "${applicationScope.WebApplicationBaseURL}modules/iview2/i18n/{lang}.json",
                lang: "de",
                pdfCreatorStyle: "pdf",
                pdfCreatorURI: "http://wrackdm17.thulb.uni-jena.de/mets-printer/pdf",
                metadataURL: "",
                objId: "",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                imageOverview : {
                    enabled: true
                },
                chapter: {
                    enabled: false,
                    showOnStart: false
                },
                permalink: {
                    enabled: true,
                    updateHistory: false,
                    viewerLocationPattern:"{baseURL}/mcrviewer/recordIdentifier/{derivate}/{file}"
                },
                canvas: {
                    overview: {
                        enabled: true
                    }
                }
            });
	   });
    </script>
</c:if>
	

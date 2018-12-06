<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.jdom2.Element"%>
<%@ page import="org.jdom2.filter.Filters"%>
<%@ page import="org.jdom2.xpath.XPathFactory"%>
<%@ page import="org.jdom2.xpath.XPathExpression"%>
<%@ page import="org.mycore.frontend.jsp.MCRHibernateTransactionWrapper"%>
<%@ page import="org.mycore.datamodel.metadata.MCRDerivate"%>
<%@ page import="org.mycore.datamodel.metadata.MCRMetaLinkID"%>
<%@ page import="org.mycore.datamodel.metadata.MCRObjectID"%>
<%@ page import="org.mycore.datamodel.metadata.MCRMetadataManager"%>
<%@ page import="org.mycore.datamodel.metadata.MCRObject"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>


<%-- RequestParameter: id, mcrid, recordidentifier, doctype  --%>
<%
  pageContext.setAttribute("id", request.getParameter("id"));
  pageContext.setAttribute("mcrid", request.getParameter("mcrid"));
  pageContext.setAttribute("recordidentifier", request.getParameter("recordidentifier"));
  pageContext.setAttribute("doctype", request.getParameter("doctype"));
%>

<c:set var="iviewBaseURL" value="${applicationScope.WebApplicationBaseURL}modules/iview2/" />
<!doctype html>
<html>
<head>
  <title>Embedded MyCoRe Viewer</title>
  <script src="${applicationScope.WebApplicationBaseURL}webjars/jquery/3.3.1-1/jquery.min.js"></script>
  <link href="${applicationScope.WebApplicationBaseURL}modules/bootstrap_3.3.7/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
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
    MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(request.getParameter("mcrid"))));
    String derLabel = "fulltext";
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
  <div id="${id}" style="height:600px; margin:0px 16px; position:relative;"></div>
    <script>
    window.addEventListener("load", function(){
            new mycore.viewer.MyCoReViewer(jQuery("#${id}"), {
                "mobile": false,
                pdfProviderURL: "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/${maindoc}",
                derivate: "${mcrid}",
                filePath: "${maindoc}",
                doctype: "${doctype}",
                startImage: "1",
                i18nURL: "${applicationScope.WebApplicationBaseURL}rsc/locale/translate/{lang}/component.viewer.*",
                lang: "de",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                pdfWorkerURL: "${iviewBaseURL}js/lib/pdf.worker.js",
                "canvas.startup.fitWidth": false,
                "canvas.overview.enabled": false,
                "chapter.showOnStart": false,
                "chapter.enabled" : false,
                "imageOverview.enabled" : false,
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
    MCRObject mcrObj = MCRMetadataManager.retrieveMCRObject(MCRObjectID.getInstance(String.valueOf(request.getParameter("mcrid"))));
    String derLabel = "MCRVIEWER_METS";
    for(MCRMetaLinkID derLink: mcrObj.getStructure().getDerivates()){
      if(derLink.getXLinkTitle().equals(derLabel)){
          MCRDerivate der = MCRMetadataManager.retrieveMCRDerivate(derLink.getXLinkHrefID());
          pageContext.setAttribute("maindoc", der.getDerivate().getInternals().getMainDoc());
          pageContext.setAttribute("derid", der.getId().toString());
      }
    }
    pageContext.setAttribute("startImage", "phys_0001");
    if(request.getParameter("start")!=null && !request.getParameter("start").isEmpty()){
        pageContext.setAttribute("startImage", request.getParameter("start"));
    }
    XPathExpression<Element> xpCoverImage = XPathFactory.instance().compile("//irControl/map/entry[@key='start_image']", Filters.element());
    for(Element e : xpCoverImage.evaluate(mcrObj.createXML())){
        pageContext.setAttribute("startImage", e.getTextTrim());
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
                metsURL: "${applicationScope.WebApplicationBaseURL}file/${mcrid}/${derid}/${maindoc}",
                imageXmlPath: "${applicationScope.WebApplicationBaseURL}tiles/${fn:replace(recordIdentifier,'/','_')}/",
                tileProviderPath: "${applicationScope.WebApplicationBaseURL}tiles/${fn:replace(recordIdentifier,'/','_')}/",
                filePath: "iview2/${startImage}.iview2",
               // derivate: "${fn:replace(recordIdentifier,'/','%252F')}",
                derivate: "${derid}",
                i18nURL: "${applicationScope.WebApplicationBaseURL}rsc/locale/translate/{lang}/component.viewer.*",
                lang: "de",
                metadataURL: "",
                derivateURL : "${applicationScope.WebApplicationBaseURL}depot/${fn:replace(recordIdentifier,'/','%252F')}/",
                objId: "",
                webApplicationBaseURL: "${applicationScope.WebApplicationBaseURL}",
                imageOverview : {
                    enabled: false
                },
                chapter: {
                    enabled: false,
                    showOnStart: false
                },
                permalink: {
                    enabled: true,
                    updateHistory: false,
                    viewerLocationPattern:"{baseURL}/mcrviewer/recordIdentifier/${fn:replace(recordIdentifier,'/','%252F')}/{file}"
                },
                canvas: {
                    overview: {
                        enabled: true
                    }
                },
                text: {
                  enabled: false
                }
            });
     });
    </script>
</c:if>
  <script src="${applicationScope.WebApplicationBaseURL}themes/bootstrap_3.3.7/js/bootstrap.min.js"></script>

</body>
</html>
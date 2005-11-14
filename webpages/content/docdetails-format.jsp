<%@ page import="org.jdom.Document" %>
<%@ page import="org.jdom.Element" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="org.mycore.frontend.jsp.query.MCRResultFormatter" %>
<%@ page import="javax.servlet.jsp.PageContext" %>
<%@ page import="org.jdom.filter.ElementFilter" %>
<%@ page import="org.jdom.output.XMLOutputter" %>
<%@ page import="org.jdom.output.Format" %>
<%@ page import="org.jdom.output.DOMOutputter" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% 
Document doc = (Document) request.getAttribute("doc");
String docID = doc.getRootElement().getAttributeValue("ID");
//TODO foreign hosts
String host = "local" ;
Document docFields = (Document) request.getAttribute("docFields");
String WebApplicationBaseURL = (String) request.getAttribute("WebApplicationBaseURL");
Element mycoreobject = doc.getRootElement();
String lang = (String) request.getAttribute("lang");
String langBundle = "messages";
XMLOutputter xmloutput = new XMLOutputter();

MCRResultFormatter formatter = new MCRResultFormatter(); 
%>
<c:catch var="e">
<fmt:setLocale value='<%= lang %>'/>
<fmt:setBundle basename='<%= langBundle %>'/>
<!--  <c:import var="xslt" url="/content/docdetails-config/MCRResultFormatter.xsl" /> -->

<table cellspacing="0" cellpadding="0" id="metaHeading">
   <tr>
      <td class="titles"><%= formatter.getSingleXPathValue(mycoreobject,"/mycoreobject/metadata/titles/title[@xml:lang='" + lang + "']") %>
      </td>
      <td class="browseCtrl">
         <c:if test="${requestScope.query != null}">
            <a href="http://mycoresamplelinux.dl.uni-leipzig.de/servlets/MCRQueryServlet?mode=CachedResultList&type=alldocs">^</a>
  &nbsp;&nbsp;
  <a href="http://mycoresamplelinux.dl.uni-leipzig.de/servlets/MCRQueryServlet?mode=CachedResultList&type=alldocs&ref=DocPortal_document_00774701@local&view=next">&gt;&gt;</a>
         </c:if>
      </td>
</tr>
</table>

<table cellspacing="0" cellpadding="0" id="metaData">
   <%
    for (Iterator it = docFields.getRootElement().getDescendants(new ElementFilter("MCRDocDetail")); it.hasNext();) {
        Element field = (Element) it.next();
        Element allMetaValuesRoot = new Element("all-metavalues");
        Document allMetaValues = new Document(allMetaValuesRoot);
        if (field.getAttributeValue("rowtype").equals("standard")) {
                    List lContent = field.getChildren("MCRDocDetailContent");
                    for(Iterator it2 = lContent.iterator(); it2.hasNext();) {
                        Element content = (Element) it2.next();
                        String languageRequired = content.getAttributeValue("languageRequired");
                        String paramLang = languageRequired.equals("no") ? "":lang;
                        String templatetype = content.getAttributeValue("templatetype");
                        String xpath = content.getAttributeValue("xpath");
                        String contentSeparator = content.getAttributeValue("separator"); 
                        String contentTerminator = content.getAttributeValue("terminator"); 
                        String introkey = content.getAttributeValue("introkey");
                        String escapeXml = content.getAttributeValue("escapeXml");                        
                        if (introkey == null) introkey = "";
                        if (contentSeparator == null) contentSeparator = ", ";
                        if (contentTerminator == null) contentTerminator = ", ";                        
                        if (escapeXml == null) escapeXml = "true";
                        //String singleMetaValue = formatter.getFormattedMCRDocDetailContent(doc, xpath, 
                        //        contentSeparator, paramLang, templatetype);
                        //metaValue.append(singleMetaValue);
                        //if (it2.hasNext() && !singleMetaValue.equals(""))
                        //    metaValue.append(separator);
                        Element metaValues = formatter.getFormattedMCRDocDetailContent(doc, xpath, 
                                contentSeparator, contentTerminator, paramLang, templatetype, introkey, escapeXml);
                        if ((metaValues != null) && (metaValues.getChildren().size() > 0))
                            allMetaValuesRoot.addContent(metaValues);
                    }
                    if (allMetaValues.getRootElement().getChildren().size() > 0) {
                        pageContext.setAttribute("xml",xmloutput.outputString(allMetaValues));
                    %>
                      <tr>
                          <td class="metaname"><fmt:message key="<%= field.getAttributeValue("labelkey") %>" />:</td>
                          <td class="metavalue">
                            <x:parse var="doc" doc="${xml}" />
                            <x:forEach select="$doc/all-metavalues/metavalues">
                               <x:set var="separator" select="./@separator" />
                               <x:set var="terminator" select="./@terminator" />
                               <x:if select="string-length(./@introkey) > 0" >
                                  <x:set var="introkey" select="string(./@introkey)" />
                                  <fmt:message key="${introkey}" />
                               </x:if>
                               <x:forEach select="./metavalue">
                                  <x:if select="generate-id(../metavalue[position() = 1]) != generate-id(.)">
                                     <x:out select="$separator" escapeXml="false" />
                                  </x:if>
                                  <x:choose>
                                     <x:when select="../@type = 'BooleanValues'">
                                        <x:set var="booleanKey" select="concat(./@type,'-',./@text)" />
                                        <fmt:message key="${booleanKey}" />
                                     </x:when>
                                     <x:when select="../@type = 'AuthorJoin'">
                                        <x:set var="authorjoinKey" select="concat(./@type,'-',./@text)" />
                                        <a href="<x:out select="./@href" />" target="<x:out select="./@target" />"><fmt:message key="${authorjoinKey}" /></a>
                                     </x:when>                                     
                                     <x:when select="./@href != ''">
                                        <a href="<x:out select="./@href" />" target="<x:out select="./@target" />"><x:out select="./@text" /></a>
                                     </x:when>
                                     <x:otherwise>
                                        <x:out select="./@text" escapeXml="./@escapeXml" />
                                     </x:otherwise>
                                  </x:choose>
                               </x:forEach>
                               <x:if select="generate-id(../metavalues[position() = last()]) != generate-id(.)">
                                  <x:out select="$terminator" escapeXml="false" />
                               </x:if>                               
                            </x:forEach>
                            <x:if select="$doc/all-metavalues/digitalobjects">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                    <tbody>
                                    <x:forEach select="$doc/all-metavalues/digitalobjects/digitalobject">
                                       <tr>
                                           <td align="left" valign="top">
                                              <div class="derivateBox">
                                                <div class="derivateHeading"><x:out select="./@derivlabel" /></div>
                                                <div class="derivate">
                                                   <a href="<%= WebApplicationBaseURL %><x:out select="concat('file/',./@derivid,'/',./@derivmain,'?hosts=')" /><%= host %>" target="_self"><x:out select="./@derivmain" /></a>
                                                    (<x:out select="./@size mod 1024" /> kB) &nbsp;&nbsp;
                                                   <a href="<%= WebApplicationBaseURL %><x:out select="concat('zip?id=',./@derivid)" /> class="linkButton"><fmt:message key="zipgenerate" /></a>
                                                        &nbsp;
                                                   <a href="<%= WebApplicationBaseURL %><x:out select="concat('nav?path=~derivatedetails&derID=',./@derivid,'&docID=')" /><%= new StringBuffer(docID).append("&hosts=").append(host).toString() %>" target="_self"><fmt:message key="details" />&gt;&gt;</a>
                                                </div>
                                              </div>
                                           </td>
                                       </tr>
                                    </x:forEach>
                                </tbody></table>                               
                            </x:if>
                          </td>
                      </tr>
                    <%
                    }
        }else if(field.getAttributeValue("rowtype").equals("space")){
            %>
            <tr>
                <td colspan="2" class="metanone">&nbsp;</td>
            </tr>
            <%
        }else if (1==1) {
            // TODO, IF NECESSARY
        }
    }
   %>
</table>
</c:catch>
<c:if test="${e!=null}">
An error occured, hava a look in the logFiles!
<% 
  Logger.getLogger("test.jsp").error("error", (Throwable) pageContext.getAttribute("e"));   
%>
</c:if>

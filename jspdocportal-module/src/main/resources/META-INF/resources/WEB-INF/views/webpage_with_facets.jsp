<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ page import="org.mycore.frontend.jsp.search.MCRSearchResultDataBean" %>
<%@ page import="org.mycore.common.config.MCRConfiguration" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.title.${fn:replace(actionBean.path, '/', '.')}" />
<c:set var="layout">1column</c:set>
<c:if test="${not empty actionBean.info}"><c:set var="layout">2columns_right</c:set></c:if>

<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}" layout="${layout}">
	<stripes:layout-component name="contents">
		<div class="ir-box">
			<mcr:includeWebcontent id="${fn:replace(actionBean.path, '/', '.')}" file="${actionBean.path}.html" />

			<c:if test="${(actionBean.path eq 'epub') or (actionBean.path eq 'histbest') }">	
				<script type="text/javascript">
				function changeFacetIncludeURL(key, value, mask, searchId) {
					window.location=$("meta[name='mcr:baseurl']").attr("content")
							 	       + "browse/"+mask+"?_search="+searchId
							           + "&_add-filter="
							       + encodeURIComponent("+" + key +":"+ value);
					}
										
				</script>
				<c:set var="mask" value="${actionBean.path}" />
				<%
			        MCRSearchResultDataBean result = new MCRSearchResultDataBean();
		    	    result = new MCRSearchResultDataBean();
		        	result.setQuery(MCRConfiguration.instance().getString("MCR.Browse." + pageContext.getAttribute("mask") + ".Query", "*:*"));
		        	result.setMask((String)pageContext.getAttribute("mask"));
		        	result.setAction("browse/" + pageContext.getAttribute("mask"));
		        	result.getFacetFields().clear();
		        	for (String ff : MCRConfiguration.instance().getString("MCR.Browse." + pageContext.getAttribute("mask") + ".FacetFields", "").split(",")) {
		            	if (ff.trim().length() > 0) {
			                result.getFacetFields().add(ff.trim());
			            }
			        }
			        result.setRows(20);
			        MCRSearchResultDataBean.addSearchresultToSession(request, result);
			        result.doSearch();
					pageContext.setAttribute("result", result);					
				%>
			</c:if>
			<c:if test="${actionBean.path eq 'histbest' }">
				<div class="row">
					<div class="col-sm-4 col-xs-12 ir-browse-classification">
						<%-- <search:browse-facet result="${result}" mask="${mask}" facetField="ir.doctype_class.facet" /> --%>
						<search:browse-classification categid="doctype:histbest" mask="${mask}" facetField="ir.doctype_class.facet" />
					</div>
					<div class="col-sm-4 col-xs-12 ir-browse-classification">
						<%-- <search:browse-facet result="${result}" mask="${mask}" facetField="ir.collection_class.facet" /> --%>
						<search:browse-classification categid="collection" mask="${mask}" facetField="ir.collection_class.facet" />
					</div>
					<div class="col-sm-4 col-xs-12 ir-browse-classification">
						<%-- <search:browse-facet result="${result}" mask="${mask}" facetField="ir.epoch_msg.facet" /> --%>
						<search:browse-classification categid="epoch" mask="${mask}" facetField="ir.epoch_msg.facet" />
					</div>
				</div>
			</c:if>
			
			<c:if test="${actionBean.path eq 'epub' }">
				<div class="row">
					<div class="col-sm-4 col-xs-12 ir-browse-classification">
						<search:browse-facet result="${result}" mask="${mask}" facetField="ir.doctype_class.facet" />
					</div>
					<div class="col-sm-4 col-xs-12 ir-browse-classification">
						<search:browse-facet result="${result}" mask="${mask}" facetField="ir.sdnb_class.facet" />
					</div>
					<div class="col-sm-4 col-xs-12 ir-browse-classification">
						
					</div>
				</div>
			</c:if>
			
		</div>
		<script>
		$( document ).ready(function() {
			$.ajax({
				type : "GET",
				url : "${WebApplicationBaseURL}api/v1/search?q=category%3A%22doctype%3Ahistbest%22&rows=1&wt=json&indent=true&facet=true&facet.field=ir.doctype_class.facet&facet.field=ir.collection_class.facet&facet.field=ir.epoch_msg.facet&json.wrf=?",
				dataType : "jsonp",
				success : function(data) {
					var fc = data.facet_counts.facet_fields;
					$('.mcr-facet-count').each(function(index, el){
						 var fvalues = fc[$(el).attr('data-mcr-facet-field')];
						 //TODO remove tempory fix replace("epoch:", ...)
						 var idx = $.inArray($(el).attr('data-mcr-facet-value').replace("epoch:", ""), fvalues);
					    if(idx == -1){
					    	if($.inArray($(el).attr('data-mcr-facet-value'), ["doctype:histbest.print", "doctype:histbest.manuscript"])!=-1){
					    		$(el).parent().parent().attr('disabled', 'disabled');
					    	}
					    	else{
					    		$(el).parent().parent().addClass('hidden');
					    	}
					    }
					    else{
							$(el).text(fvalues[idx + 1]);
					 	}
					});
				},
			});
		});
		</script>
	</stripes:layout-component>
	<stripes:layout-component name="right_side">
		<c:forEach var="id" items="${fn:split(actionBean.info,',')}" >
			<div class="ir-box ir-box-bordered ir-infobox">
				<mcr:includeWebcontent id="${id}" file="${fn:replace(id, '.', '/')}.html" />
			</div>
		</c:forEach>
		<div style="padding-top: 32px; padding-bottom: 32px; text-align: center;">
			<a href="http://www.mycore.org"> <img
		       alt="powered by MyCoRe 2016_LTS"
			   src="${WebApplicationBaseURL}images/mycore_logo_powered_129x34_knopf_hell.png"
			   style="border: 0; text-align: center;" />
			</a>
		</div>
	</stripes:layout-component>
</stripes:layout-render>

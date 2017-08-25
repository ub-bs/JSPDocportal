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
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle = "${pageTitle}">
	<stripes:layout-component name="main_part">
      <div class="row">
        <div class="col-xs-12 ir-divider">
          <hr/>
        </div>
      </div>
      <div class="row">
      <div class="col-xs-12">
        <div class="ir-box">
          <mcr:includeWebcontent id="${fn:replace(actionBean.path, '/', '.')}" file="${actionBean.path}.html" />

          <c:if test="${(actionBean.path eq 'epub') or (actionBean.path eq 'histbest') }">
            <script type="text/javascript">
				function changeFacetIncludeURL(key, value, mask) {
					window.location=$("meta[name='mcr:baseurl']").attr("content")
							 	       + "browse/"+mask+"?"
							           + "&_add-filter="
							       + encodeURIComponent("+" + key +":"+ value.replace('epoch:',''));
					}
				
				function changeFilterIncludeURL(key, value, mask) {
					window.location=$("meta[name='mcr:baseurl']").attr("content")
			 				    + "browse/"+mask+"?"
			        			+ $("meta[name='mcr:search.id']").attr("content")
				    			+ "&_add-filter="
				    			+ encodeURIComponent("+" + key+":"+value);
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
          <c:if test="${(actionBean.path eq 'histbest') or (actionBean.path eq 'epub') }">
            <%-- key=$("input[name='filterField']:checked").val(); value=$('#filterValue').val()); --%>
            <div class="row"></div>
            <div class="row">
              <div class="col-sm-7 col-xs-10">
                <input class="form-control" id="filterValue" name="filterValue" style="width: 100%" placeholder="Wert"
                  onkeypress="if (event.keyCode == 13) { changeFilterIncludeURL($('input[name=\'filterField\']:checked').val(), $('#filterValue').val(), 'histbest');}"
                  type="text">
              </div>
              <div class="col-sm-1 col-xs-2">
                <button id="filterInclude" class="btn btn-primary"
                  onclick="changeFilterIncludeURL($('input[name=\'filterField\']:checked').val(), $('#filterValue').val(), '${actionBean.path}');">
                  <i class="fa fa-search"></i>
                </button>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-8 col-xs-12">
                <table>
                  <tbody>
                    <tr>
                      <td class="radio input-sm"><label> <input name="filterField" value="ir.title_all"
                          checked="checked" type="radio"> Titel
                      </label></td>
                      <td></td>
                      <td class="radio input-sm"><label> <input name="filterField" value="ir.creator_all"
                          type="radio"> Autor
                      </label></td>
                      <td></td>
                      <td class="radio input-sm"><label> <input name="filterField" value="ir.pubyear_start"
                          type="radio"> erschienen nach
                      </label></td>
                      <td></td>
                      <td class="radio input-sm"><label> <input name="filterField" value="ir.pubyear_end"
                          type="radio"> erschienen vor
                      </label></td>
                      <td></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </c:if>
          <c:if test="${actionBean.path eq 'histbest' }">
            <div class="row">
              <div class="col-sm-4 col-xs-12 ir-browse-classification">
                <%-- <search:browse-facet result="${result}" mask="${mask}" facetField="ir.doctype_class.facet" /> --%>
                <%-- <search:browse-classification categid="doctype:histbest" mask="${mask}" facetField="ir.doctype_class.facet" /> --%>
                <search:browse-classification categid="collection:Materialart" mask="${mask}"
                  facetField="ir.collection_class.facet" />
              </div>
              <div class="col-sm-4 col-xs-12 ir-browse-classification">
                <%-- <search:browse-facet result="${result}" mask="${mask}" facetField="ir.collection_class.facet" /> --%>
                <search:browse-classification categid="collection:Projekte" mask="${mask}"
                  facetField="ir.collection_class.facet" />
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
                <search:browse-facet result="${result}" mask="${mask}" facetField="ir.institution_class.facet" />
              </div>
            </div>
          </c:if>
        </div>
      </div>
    </div>
    <script>
		$( document ).ready(function() {
			$.ajax({
				type : "GET",
				url : "${WebApplicationBaseURL}api/v1/search?q=category%3A%22doctype%3A${mask}%22&rows=1&wt=json&indent=true&facet=true&facet.field=ir.doctype_class.facet&facet.field=ir.institution_class.facet&facet.field=ir.collection_class.facet&facet.field=ir.epoch_msg.facet&json.wrf=?",
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
</stripes:layout-render>

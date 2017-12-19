<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x"       uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" 	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
	
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>
<c:set var="org.mycore.navigation.path" scope="request">left.histbest.recherche</c:set>
<fmt:message var="pageTitle" key="Webpage.browse.title.${actionBean.result.mask}" />
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}">
  <stripes:layout-component name="html_header">
    <meta name="mcr:search.id" content="${actionBean.result.id}" />
  </stripes:layout-component>
  <stripes:layout-component name="main_part">
    <div class="row">
      <div class="col-xs-12">
        <h2>${pageTitle}</h2>
      </div>
    </div>
    <div class="row">
      <div class="col-xs-12">
        <search:result-sorter result="${actionBean.result}"
          fields="score,ir.pubyear_start,modified,ir.creator.result,ir.title.result" mask="histbest" />
      </div>
    </div>
    <div class="row">
      <div class="col-xs-12 col-md-3">
        <div class="ir-box ir-box-bordered">
          <h3><fmt:message key="Browse.Filter.headline" /></h3>
          <form class="form-horizontal" onsubmit="return false;">
            <div class="form-group">
              <div class="col-sm-12">
                <div class="input-group input-group-sm">
                  <script type="text/javascript">
					function changeFilterIncludeURL() {
						window.location=$("meta[name='mcr:baseurl']").attr("content")
				 				    + "browse/histbest?_search="
				        			+ $("meta[name='mcr:search.id']").attr("content")
					    			+ "&_add-filter="
					    			+ encodeURIComponent("+" + $("input[name='filterField']:checked").val()+":"+$("#filterValue").val());
					}
					function changeFilterExcludeURL() {
						window.location=$("meta[name='mcr:baseurl']").attr("content")
					    		   + "browse/histbest?_search="
				      	   	       + $("meta[name='mcr:search.id']").attr("content")
					   	   		   + "&_add-filter="
					   	   		   + encodeURIComponent("-" + $("input[name='filterField']:checked").val()+":"+$("#filterValue").val());
					}
										<%-- for select box use: $("#filterField option:selected").val() --%>
				</script>
                  <%--
 				<span class="input-group-addon">
 					<select id="filterField" name="filterField" style="height:99%">
						<option value="ir.creator_all"><fmt:message key="Browse.Filter.histbest.ir.creator_all"/></option>
						<option value="ir.title_all"><fmt:message key="Browse.Filter.histbest.ir.title_all"/></option>
						<option value="ir.pubyear_start"><fmt:message key="Browse.Filter.histbest.ir.pubyear_start"/></option>
						<option value="ir.pubyear_end"><fmt:message key="Browse.Filter.histbest.ir.pubyear_end" /></option>
				    </select>
			   </span>
			--%>
                  <input class="form-control" id="filterValue" name="filterValue" style="width: 100%" placeholder="Wert"
                    type="text" onkeypress="if (event.keyCode == 13) { changeFilterIncludeURL();}"> <span
                    class="input-group-btn">
                    <button id="filterInclude" class="btn btn-primary ir-facets-btn-plus" onclick="changeFilterIncludeURL();">
                      <i class="fa fa-plus"></i>
                    </button>
                  </span>
                </div>
              </div>
              <div class="col-sm-12">
                <table>
                  <tr>
                    <td class="radio input-sm"><label> <input name="filterField" value="ir.title_all"
                        type="radio" checked="checked" /> <fmt:message key="Browse.Filter.histbest.ir.title_all" />
                    </label>
                    <td>
                    <td class="radio input-sm"><label> <input name="filterField" value="ir.pubyear_start"
                        type="radio" /> <fmt:message key="Browse.Filter.histbest.ir.pubyear_start" />
                    </label>
                    <td>
                  </tr>
                  <tr>
                    <td class="radio input-sm"><label> <input name="filterField" value="ir.creator_all"
                        type="radio" /> <fmt:message key="Browse.Filter.histbest.ir.creator_all" />
                    </label>
                    <td>
                    <td class="radio input-sm"><label> <input name="filterField" value="ir.pubyear_end"
                        type="radio" /> <fmt:message key="Browse.Filter.histbest.ir.pubyear_end" />
                    </label>
                    <td>
                  </tr>
                </table>
              </div>
            </div>
          </form>

          <div class="row" style="margin-bottom: 10px;">
            <div class="col-sm-12">
              <c:forEach var="fq" items="${actionBean.result.filterQueries}">
                <c:if test="${not fn:contains(fq, '.facet:')}">
                  <c:url var="url" value="${WebApplicationBaseURL}browse/histbest">
                    <c:param name="_search" value="${actionBean.result.id}" />
                    <c:param name="_remove-filter" value="${fq}" />
                  </c:url>
                  <c:set var="c">
                    <fmt:message key="Browse.Filter.histbest.${fn:substringBefore(fn:substring(fq, 1, -1),':')}" />: ${actionBean.calcFacetOutputString(fn:substringBefore(fn:substring(fq, 1, -1),':'), fn:substringAfter(fn:substring(fq, 1, -1),':'))}</c:set>
                  <a class="btn btn-sm btn-default ir-form-control"
                    style="display: block; position:relative; text-align: left; white-space: normal; margin-bottom: 3px; color: black; width: 100%"
                    href="${url}"> <i class="fa fa-times" style="position:absolute; top: 5px; right:20px; color: darkred;"></i>
                    ${c}
                  </a>
                </c:if>
              </c:forEach>
            </div>
          </div>

          <search:result-facets result="${actionBean.result}" mask="histbest" top="5" />
        </div>
      </div>
      <div class="col-xs-12 col-md-9">
        <search:result-browser result="${actionBean.result}">
          <c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
          <c:choose>
            <c:when test="${(doctype eq 'document') or (doctype eq 'bundle')}">
              <search:result-entry-document entry="${entry}" url="${url}" />
            </c:when>
            <c:otherwise>
              <search:result-entry entry="${entry}" url="${url}" />
            </c:otherwise>
          </c:choose>
          <div class="row">
            <div class="col-xs-12">
              <search:show-edit-button mcrid="${mcrid}" cssClass="btn btn-sm btn-primary ir-edit-btn" />
              <span class="label label-default ir-label-default">${entry.data['ir.doctype.result']}</span>
            </div>
          </div>
        </search:result-browser>
      </div>
    </div>
  </stripes:layout-component>
</stripes:layout-render>

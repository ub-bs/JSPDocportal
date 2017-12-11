<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<fmt:message var="pageTitle" key="Webpage.title.${fn:replace(actionBean.path, '/', '.')}" />

<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="${layout}">
  <stripes:layout-component name="main_part">
    <c:if test="${not empty actionBean.info}">
      <div class="row">
        <div class="col-xs-12 col-md-8">
          <div class="ir-box">
            <mcr:includeWebcontent id="${fn:replace(actionBean.path, '/', '.')}" file="${actionBean.path}.html" />
          </div>
        </div>
        <div class="col-xs-12 col-md-4">
          <div class="row">
            <c:forEach var="id" items="${fn:split(actionBean.info,',')}">
              <div class="col-xs-12">
                <div class="ir-box ir-box-bordered">
                  <mcr:includeWebcontent id="${id}" file="${fn:replace(id, '.', '/')}.html" />
                </div>
              </div>
            </c:forEach>
          </div>
        </div>
      </div>
    </c:if>

    <c:if test="${empty actionBean.info}">
      <div class="row">
        <div class="col-xs-12">
          <div class="ir-box">
            <mcr:includeWebcontent id="${fn:replace(actionBean.path, '/', '.')}" file="${actionBean.path}.html" />
          </div>
        </div>
      </div>
    </c:if>
  </stripes:layout-component>
</stripes:layout-render>

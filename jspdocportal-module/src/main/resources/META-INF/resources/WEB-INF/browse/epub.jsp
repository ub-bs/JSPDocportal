<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x"       uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"      uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mcr" 	uri="http://www.mycore.org/jspdocportal/base.tld"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
	
<%@ taglib prefix="search" tagdir="/WEB-INF/tags/search"%>

<fmt:message var="pageTitle" key="Webpage.browse.title.${actionBean.result.mask}" />
<stripes:layout-render name="/WEB-INF/layout/default.jsp" pageTitle="${pageTitle}" layout="1column">
	<stripes:layout-component name="html_header">
	</stripes:layout-component>
	<stripes:layout-component name="contents">
		<div class="roW">
			<div class="col-xs-12 ur-text">
				<h2>${pageTitle}</h2>
			</div>
		</div>
		<div class="row">
			<div class="col-md-9">
				<div class="ur-box">
					<search:result-browser result="${actionBean.result}">
						<c:set var="doctype" value="${fn:substringBefore(fn:substringAfter(mcrid, '_'),'_')}" />
						<search:show-edit-button mcrid="${mcrid}" />
						<c:choose>
							<c:when test="${doctype eq 'disshab'}">
								<search:result-entry-disshab data="${entry}" url="${url}" />
							</c:when>
							<c:otherwise>
								<search:result-entry data="${entry}" url="${url}" />
							</c:otherwise>
						</c:choose>
						<div style="clear:both"></div>
					</search:result-browser>
				</div>
			</div>
			<div class="col-md-3">
				<div class="ur-box ur-box-bordered ur-infobox hidden-sm hidden-xs">
         			 <h3>Filter</h3>
          			 <div class="panel panel-default">
  						<div class="panel-heading">
  							<a style="width:100%; margin-bottom:6px;color:black" class="btn btn-default" href="/rosdok/browse/epub?_remove-filter=0&amp;search=2d29fd92-f743-4d4e-a232-b487bcba16e8">
  								<span style="color:red" class="glyphicon glyphicon-trash pull-right"></span>
  								<span class="glyphicon glyphicon-plus pull-left"></span>
  								<span style="padding-left:6px" class="pull-left">Autor:Müller</span>
    						</a>
    						<a style="width:100%; margin-bottom:6px;color:black" class="btn btn-default" href="/rosdok/browse/epub?_remove-filter=1&amp;search=2d29fd92-f743-4d4e-a232-b487bcba16e8">
  								<span style="color:red" class="glyphicon glyphicon-trash pull-right"></span>
  								<span class="glyphicon glyphicon-minus pull-left"></span>
  								<span style="padding-left:6px" class="pull-left">Titel:XML</span>
    						</a>
    						<a style="width:100%; margin-bottom:6px;color:black" class="btn btn-default" href="/rosdok/browse/epub?_remove-filter=2&amp;search=2d29fd92-f743-4d4e-a232-b487bcba16e8">
  								<span style="color:red" class="glyphicon glyphicon-trash pull-right"></span>
  								<span class="glyphicon glyphicon-minus pull-left"></span>
  								<span style="padding-left:6px" class="pull-left">Ersch.jahr: 2015</span>
    						</a>
  						</div>
  						<div class="panel-body row">
  							<div class="col-sm-9">
    							<div class="form-group">
    							  	<select id="filterField" name="filterField" class="form-control" style="width:100%">
  										<option value="author">Autor</option>
  										<option value="title">Titel</option>
  										<option value="pub-year">Erscheinungsjahr</option>
 									</select>
   								</div>
  								<div class="form-group">
   									<input class="form-control" id="filterValue" name="filterValue" style="width:100%" placeholder="Wert" type="text">
   								</div>
  							</div>
  							<div class="col-sm-3">
								<button class="btn btn-primary" style="margin-top:6px; margin-left:-9px"><span class="glyphicon glyphicon-plus"></span></button> 	
								<button class="btn btn-primary" style="margin-top:3px; margin-left:-9px"><span class="glyphicon glyphicon-minus"></span></button>
          					</div> 		
						</div>
  					</div>
				</div>
        	</div>
		</div>
	</stripes:layout-component>
</stripes:layout-render>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"   %>
<%@ taglib prefix="mcr" uri="http://www.mycore.org/jspdocportal/base.tld" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>

<html>
	<head>
		<title><fmt:message key="Webpage.editwebcontent.form.headline" /></title> 
		<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
   		<%--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script> --%>
   		<script src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
   		<script src="${pageContext.request.contextPath}/ckeditor/adapters/jquery.js"></script>
	</head>

	<h2><fmt:message key="Webpage.editwebcontent.form.headline" /></h2>
		<form id="editWebcontent_${actionBean.id}" method="post" action="saveWebcontent.action">
		    <input type="hidden" name="file_${actionBean.id}" value="${actionBean.file}" />
		    <input type="hidden" name="referer_${actionBean.id}" value="${actionBean.referer}" />
		    <textarea  id="taedit_${actionBean.id}" name="content_${actionBean.id}" rows="10" cols="80">${actionBean.content}</textarea>
		<script type="text/javascript">
		    $(document).ready( function() {$('textarea#taedit_${actionBean.id}').ckeditor(); });
			<%--
		        CKEDITOR.replace( 'taedit_"+id+"');");
		            ,{customConfig : '"+MCRFrontendUtil.getBaseURL() +"admin/ckeditor_config.js'}");
		         );"); --%>
		</script>
		    <div class="panel-body bg-warning">
		        <input type="submit"  name="doSave_${actionBean.id}" class="btn btn-primary" 
		               title="<fmt:message key="Webpage.editwebcontent.save"/>" value="Speichern"> 
		                 <%-- <span class="glyphicon glyphicon-floppy-disk"></span> <fmt:message key="Webpage.editwebcontent.save" /> --%>
				</input>

		        <input type="submit"  name="doCancel_${actionBean.id}" class="btn btn-danger" 
		        	title="<fmt:message key="Webpage.editwebcontent.cancel" />" value="Abbrechen">
		        		<%-- <span class="glyphicon glyphicon-remove"></span> <fmt:message key="Webpage.editwebcontent.cancel" /> --%>
		        </input>

		    </div>
		</form>
</html>
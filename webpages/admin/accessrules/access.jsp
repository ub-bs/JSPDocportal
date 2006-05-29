<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr" %>
<script>
	function setValue(obj){
		if (obj.checked==true){
			document.getElementById("ids").value += obj.name + " ";
		}else{
			document.getElementById("ids").value = document.getElementById("ids").value.replace(obj.name + " ","");
		}
	}

	function setID(val){
		if (countCheck()==0){
			document.getElementById("ids").value="";
		}
		document.getElementById("ids").value = document.getElementById("ids").value.replace(val + " ","");
		document.getElementById("ids").value += val + " ";
	}
    
    function setIDFromInputField(val){
        selectAll(false);
        document.getElementById("ids").value = document.getElementById("objID").value;
    }    

	function selectAll(val){
		var theForm = document.getElementById("overview"), z=0;
		while (theForm[z].type =="checkbox") {
			if(theForm[z].name.indexOf("_")!=-1){
				theForm[z].checked = val;
				setValue(theForm[z]);
			}
			z++;
		}
    }

	function selectAllType(obj){
		var theForm = document.getElementById("overview"), z=0;
		while (theForm[z].type =="checkbox") {
			if(theForm[z].name.indexOf("_")!=-1 && theForm[z].name.indexOf(obj.name)!=-1){
			theForm[z].checked = !(theForm[z].checked);
			setValue(theForm[z]);
			}
			z++;
		}
		obj.checked = false;
    }

	function countCheck(){
		var theForm = document.getElementById("overview"), z=0, anz=0;
		while (theForm[z].type =="checkbox") {
			if(theForm[z].checked == true){
				anz++;
			}
			z++;
		}
		return anz;
	}
	
</script>
<c:set var="debug" value="false" />
<c:choose>
   <c:when test="${param.step > 0}">
      <c:set var="step" value="${param.step}" />
   </c:when>
   <c:otherwise>
      <c:set var="step" value="50" />
   </c:otherwise>
</c:choose>
<c:choose>
   <c:when test="${param.start > 0}">
      <c:set var="start" value="${param.start}" />
   </c:when>
   <c:otherwise>
      <c:set var="start" value="0" />
   </c:otherwise>
</c:choose>
<mcr:setAccessRuleEditorData var="index" docType="${param.docType}" start="${start}" step="${step}" />
<c:set var="WebApplicationBaseURL" value="${applicationScope.WebApplicationBaseURL}" />


<h4><fmt:message key="Access.AssignRule" /></h4>

<form method=post action="${WebApplicationBaseURL}admin/accessrules/access_validate.jsp" id="overview">
<table border="0">
<tr>
<td>

<table class="access" cellspacing="1" cellpadding="0">
	<tr>
		<td rowspan=2">
			<fmt:message key="Access.AvailableStringIDs" />
		</td>
        <x:set var="poolNum" select="$index/accessrule-index/permissions/@numHits + 0" />
		<td class="pool" colspan="${poolNum}">
			<fmt:message key="Access.Permissions" />
		</td>
		<td rowspan="2">
			&nbsp;
		</td>
	</tr>
	<tr>
       <x:forEach select="$index/accessrule-index/permissions/permission">
          <td class="pool"><x:out select="." /></td>
       </x:forEach>
	</tr>
    <x:forEach select="$index/accessrule-index/result/value">
       <tr>
          <td><x:out select="./idx" /></td>
          <x:forEach select="./permission">
             <td class="pool"><x:out select="./@value" />&nbsp;</td>
          </x:forEach>
          <td><input type="checkbox" name="<x:out select="./idx" />" onclick="setValue(this)">&nbsp;&nbsp;<input type="image" src="./admin/images/edit.png" onclick="setID('<x:out select="./idx" />')" /></td>
       </tr>
    </x:forEach>
    <tr>
       <td><input type="text" name="objID" id="objID" size="25"></td>
       <td colspan="${poolNum + 1}">
        &nbsp;&nbsp;<input type="image" src="./admin/images/edit.png" onclick="setIDFromInputField()" />&nbsp;&nbsp;
        <fmt:message key="Access.EnterObjIDDirectly" />
       </td>
    </tr>    
</table>
<input type="hidden" value="detail" name="operation">
<input type="hidden" id="ids" name="ids" style="width:500px">
</td>
</tr>
<tr>
<td align="right">
<a href="#" onclick="selectAll(true)"><fmt:message key="Access.SelectAll" /></a> | <a href="#" onclick="selectAll(false)"><fmt:message key="Access.DeselectAll" /></a>
<x:set var="maxPos" select="$index/accessrule-index/result/value[last()]/@pos +1" />
<x:set var="numHits" select="$index/accessrule-index/result/@numHits + 0" />
<c:if test="${maxPos < numHits}">
 | <a href="${WebApplicationBaseURL}admin?path=${param.path}&start=${fn:substringBefore(maxPos,'.')}&step=${step}&docType=${param.docType}"><fmt:message key="Access.NextIDs" /></a>
</c:if>
</td>
</tr>
</table>
</form>

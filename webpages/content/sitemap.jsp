<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/lib/mycore-taglibs.jar" prefix="mcr"%>

<fmt:setBundle basename='messages' />
<c:set var="cnt" value="0" />
<c:set var="Navigation" value="${applicationScope.navDom}" />
<table width="100%" cellspacing="0" cellpadding="0">
<tr width="100%"><th width="50%" style="text-align: right">Hauptmen&uuml; links</th><th width="50%">&nbsp;</th></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<x:forEach
	select="$Navigation//navigation[@name='left']/navitem[@name='left']/navitem[not(@hidden = 'true')]">

	<x:set var="href1" select="string(./@path)" />
	<x:set var="labelKey1" select="string(./@label)" />
	<c:if test="${(cnt mod 2) == 0}">
	  <tr width="100%">
	</c:if>
	  <td width="50%" valign="top" >	
		<table class="navi_main" border="0"  cellpadding="0" cellspacing="0">
		 <tr><td colspan="2" >
			<a class="MainMenuPoint" target="_self" href="${href1}"><b><fmt:message key="${labelKey1}" /></b></a><br/>
		 </td>	
		 </tr>
		 <x:forEach select="./navitem[not(@hidden = 'true')]">
			 <tr><td>
				 <img title="" alt="" src="images/greenArrow.gif">  
			 </td><td width="100%">	
				 <x:set var="href2" select="string(./@path)" />
				 <x:set var="labelKey2" select="string(./@label)" />
				 <a target="_self" href="${href2}"><fmt:message key="${labelKey2}" /></a><br/>  		    
			 </td></tr>	
  	    </x:forEach>
  	    </table>
  	    <br/>
       </td>	   
	 <c:if test="${(cnt mod 2) == 1}">
	   </tr>
	 </c:if>
     <c:set var="cnt" value="${cnt+1}" />
  </x:forEach> 
</table>

<c:set var="cnt" value="0" />
<table width="100%" cellspacing="0" cellpadding="0">
<tr width="100%"><th width="50%" style="text-align: right">Hauptmen&uuml; oben</th><th width="50%">&nbsp;</th></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<x:forEach
	select="$Navigation//navigation[@name='top']/navitem[@name='top']/navitem[not(@hidden = 'true')]">

	<x:set var="href1" select="string(./@path)" />
	<x:set var="labelKey1" select="string(./@label)" />
	<c:if test="${(cnt mod 2) == 0}">
	  <tr width="100%">
	</c:if>
	  <td width="50%" valign="top" >	
		<table class="navi_main" border="0"  cellpadding="0" cellspacing="0">
		 <tr><td colspan="2" >
			<a class="MainMenuPoint" target="_self" href="${href1}"><b><fmt:message key="${labelKey1}" /></b></a><br/>
		 </td>	
		 </tr>
		 <x:forEach select="./navitem[not(@hidden = 'true')]">
			 <tr><td>
				 <img title="" alt="" src="images/greenArrow.gif">  
			 </td><td width="100%">	
				 <x:set var="href2" select="string(./@path)" />
				 <x:set var="labelKey2" select="string(./@label)" />
				 <a target="_self" href="${href2}"><fmt:message key="${labelKey2}" /></a><br/>  		    
			 </td></tr>	
  	    </x:forEach>
  	    </table>
  	    <br/>
       </td>	   
	 <c:if test="${(cnt mod 2) == 1}">
	   </tr>
	 </c:if>
     <c:set var="cnt" value="${cnt+1}" />
  </x:forEach> 
</table>

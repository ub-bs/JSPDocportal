<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision: 1.74 $ $Date: 2007-10-10 13:32:28 $ -->
<!-- ============================================== --> 

<xsl:stylesheet 
  version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:encoder="xalan://java.net.URLEncoder"
  exclude-result-prefixes="xsl xalan encoder i18n"
  xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
>
	<xsl:include href="coreFunctions.xsl" />
	<xsl:include href="editor.xsl" />
</xsl:stylesheet>
<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" 
	exclude-result-prefixes="xlink">
   <xsl:variable name="Empty.Derivate">
      <xsl:value-of select="'Leeres Derivate'" />
   </xsl:variable>
   <xsl:variable name="Layout.browse">
      <xsl:value-of select="'Navigieren'" />
   </xsl:variable>
   <xsl:variable name="Layout.Login">
      <xsl:value-of select="'Anmelden'" />
   </xsl:variable>
   <xsl:variable name="Layout.LoginText">
      <xsl:value-of select="'Benutzer'" />
   </xsl:variable>
   <xsl:variable name="Layout.Printview">
      <xsl:value-of select="'Druckansicht'" />
   </xsl:variable>      
   <xsl:include href="MyCoReLayout.xsl" />
</xsl:stylesheet>
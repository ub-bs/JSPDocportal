<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" 
	exclude-result-prefixes="xlink" >
    <xsl:include href="MyCoReLayout-de.xsl" />
    <xsl:include href="MyCoReWebPage.xsl" />
    
   <xsl:variable name="WebPage.PrivilegeError">
      <xsl:value-of select="'Sie haben nicht die erforderliche Berechtigung.'" />
   </xsl:variable>          
       
</xsl:stylesheet>

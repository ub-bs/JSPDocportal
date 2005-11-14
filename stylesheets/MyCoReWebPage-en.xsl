<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" 
	exclude-result-prefixes="xlink" >
    <xsl:include href="MyCoReLayout-en.xsl" />
    <xsl:include href="MyCoReWebPage.xsl" />
    
   <xsl:variable name="WebPage.PrivilegeError">
      <xsl:value-of select="'You dont have the necessary privileges.'" />
   </xsl:variable>          
       
</xsl:stylesheet>

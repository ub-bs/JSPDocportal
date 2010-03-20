<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" />

<xsl:template match="/mcr_workflow">
   <items>
      <xsl:for-each select="item">
         <xsl:element name="item">
            <xsl:attribute name="value"><xsl:value-of select="@ID" /></xsl:attribute>
            <xsl:element name="label">
               <xsl:attribute name="xml:lang">de</xsl:attribute>
               <xsl:value-of select="concat(./label,' (',./@ID,') workflow')" />               
            </xsl:element>
         </xsl:element>
      </xsl:for-each>
   </items>
</xsl:template>

</xsl:stylesheet>


<?xml version="1.0" encoding="iso-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.2 $ $Date: 2006-01-17 17:18:46 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" />

<xsl:template match="/mycoreobject">
 <item>
  <xsl:attribute name="ID">
   <xsl:value-of select="@ID" />
  </xsl:attribute>
  <!-- Name -->
  <label>
  <xsl:if test="metadata/names/name">
   <xsl:value-of select="metadata/names/name/fullname" />
  </xsl:if>
  </label>
  <!-- Date -->
  <xsl:if test="metadata/dates/date">
   <xsl:for-each select="metadata/dates/date">
    <data>
     <xsl:value-of select="text()|*" />
    </data>
   </xsl:for-each>
  </xsl:if>
  <!-- Create Date -->
  <xsl:if test="service/servdates/servdate">
   <data>
   <xsl:for-each select="service/servdates/servdate">
    <xsl:if test="@type = 'modifydate'">
     <xsl:variable name="datum"><xsl:value-of select="text()" /></xsl:variable>		
     Zuletzt bearbeitet am:  <xsl:value-of select="substring-before($datum,'T')" />

    </xsl:if>
   </xsl:for-each>
   </data>
  </xsl:if>
 </item>
</xsl:template>

</xsl:stylesheet>


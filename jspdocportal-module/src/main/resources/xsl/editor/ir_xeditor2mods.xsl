<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  version="1.0" 
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:mods="http://www.loc.gov/mods/v3">
  
  <xsl:include href="copynodes.xsl" />
  <!-- create value URI using valueURIxEditor and authorityURI -->
  <xsl:template match="@valueURIxEditor">
    <xsl:attribute name="valueURI">
          <xsl:value-of select="concat(../@authorityURI,'#',.)" />
        </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
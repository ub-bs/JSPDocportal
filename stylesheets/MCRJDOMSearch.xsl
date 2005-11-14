<?xml version="1.0" encoding="UTF-8"?>

<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->

<xsl:stylesheet 
  version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:java="http://xml.apache.org/xslt/java"
  extension-element-prefixes="java"
>

<xsl:output method="xml" encoding="UTF-8" />

<xsl:template match="/">
  <result>
    <xsl:choose>
      <xsl:when test="mycoreobject">true</xsl:when>
      <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
  </result>
</xsl:template>

</xsl:stylesheet>


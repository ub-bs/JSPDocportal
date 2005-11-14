<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== --> 

<xsl:stylesheet 
  version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:output 
  method="xml" 
  encoding="UTF-8" 
/>

<xsl:template match="/">
  <items>
    <xsl:apply-templates select="/mycoreclass/categories/category" />
  </items>
</xsl:template>

<xsl:template match="category">
  <item value="{@ID}">
    <xsl:apply-templates select="label"    />
    <xsl:apply-templates select="category" />
  </item>
</xsl:template>

<xsl:template match="label">
  <label>
    <xsl:copy-of select="@xml:lang" />
    <xsl:value-of select="concat(@text,' [',../@counter,']')" />
  </label>
</xsl:template>

</xsl:stylesheet>

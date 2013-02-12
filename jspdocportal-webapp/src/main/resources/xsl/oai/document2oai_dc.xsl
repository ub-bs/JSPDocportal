<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/">
  
<xsl:include href="document-dc.xsl"/>
<xsl:include href="document2record.xsl"/>

<xsl:template match="document" mode="metadata">
  <oai_dc:dc>
    <xsl:apply-templates select="." mode="dc"/>
  </oai_dc:dc> 
</xsl:template>

</xsl:stylesheet>
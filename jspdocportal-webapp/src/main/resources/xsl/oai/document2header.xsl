<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" encoding="UTF-8" indent="yes" />
  <xsl:param name="MCR.OAIDataProvider.RosDokOAIDefault.RepositoryIdentifier" />

  <xsl:include href="output-header.xsl" />

  <xsl:template match="/">
    <header>
      <xsl:apply-templates select="mycoreobject" mode="header" />
    </header>
  </xsl:template>

</xsl:stylesheet>
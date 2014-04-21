<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" encoding="UTF-8" indent="yes" />
  <xsl:param name="MCR.OAIDataProvider.RosDokOAIDefault.RepositoryIdentifier" />

  <xsl:template match="mycoreobject" mode="header">

    <identifier>
      <xsl:text>oai:</xsl:text>
      <xsl:value-of select="$MCR.OAIDataProvider.RosDokOAIDefault.RepositoryIdentifier" />
      <xsl:text>:</xsl:text>
      <xsl:value-of select="@ID" />
    </identifier>

    <datestamp>
      <xsl:choose>
        <xsl:when test="./service/servdates/servdate[@type='modifydate']">
          <xsl:apply-templates select="./service/servdates/servdate[@type='modifydate']" mode="yyyy-mm-dd" />
        </xsl:when>
      </xsl:choose>
    </datestamp>

    <xsl:for-each select="./metadata/accessrights/accessright[@categid='OpenAccess']">
      <setSpec>open_access</setSpec>
    </xsl:for-each>

    <xsl:for-each select="./metadata/mappings/mapping[@classid='DNB-DDC-SG']">
      <setSpec>
        <xsl:text>ddc:</xsl:text>
        <xsl:value-of select="@categid" />
      </setSpec>
    </xsl:for-each>

    <xsl:for-each select="./metadata/mappings/mapping[@classid='diniPublType']">
      <setSpec>
        <xsl:text>doc-type:</xsl:text>
        <xsl:value-of select="@categid" />
      </setSpec>
    </xsl:for-each>

    <xsl:for-each select="./metadata/mappings/mapping[@classid='dctermsDCMIType']">
      <setSpec>
        <xsl:text>DCMIType:</xsl:text>
        <xsl:value-of select="@categid" />
      </setSpec>
    </xsl:for-each>
    <xsl:for-each select="./metadata/mappings/mapping[@classid='XMetaDissPlusThesisLevel']">
      <setSpec>
        <xsl:text>XMetaDissPlusThesisLevel:</xsl:text>
        <xsl:value-of select="@categid" />
      </setSpec>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="servdate" mode="yyyy-mm-dd">
    <xsl:value-of select="substring(.,1,10)" />
  </xsl:template>

</xsl:stylesheet>
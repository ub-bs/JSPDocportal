<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:oai="http://www.openarchives.org/OAI/2.0/"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xalan="http://xml.apache.org/xalan" xmlns:mets="http://www.loc.gov/METS/"
  exclude-result-prefixes="xalan xsl xlink mods">

  <xsl:param name="WebApplicationBaseURL" select="''"/>
  <xsl:param name="ServletsBaseURL" select="''"/>
  <xsl:param name="HttpSession" select="''"/>

  <xsl:include href="mods2record.xsl"/>

  <xsl:template match="mycoreobject" mode="metadata">
    <xsl:if test="//derobject[@xlink:title='DV_METS']">
      <xsl:variable name="mcrId" select="@ID"/>
      <xsl:variable name="ifsTemp">
        <xsl:for-each select="structure/derobjects/derobject[@xlink:title='DV_METS']">
          <der id="{@xlink:href}">
            <xsl:copy-of select="document(concat('xslStyle:mcr_directory-recursive:ifs:',@xlink:href,'/'))"/>
          </der>
        </xsl:for-each>
      </xsl:variable>
      <xsl:variable name="ifs" select="xalan:nodeset($ifsTemp)"/>
      <xsl:variable name="metsFile">
        <xsl:variable name="uri" select="$ifs/der/mcr_directory/children//child[@type='file']/uri"/>
        <xsl:variable name="derId" select="substring-before(substring-after($uri,':/'), ':')"/>
        <xsl:variable name="filePath" select="substring-after(substring-after($uri, ':'), ':')"/>
        <xsl:value-of select="concat($WebApplicationBaseURL,'file/',$mcrId,'/',$derId,$filePath)"/>
      </xsl:variable>

      <xsl:copy-of select="document($metsFile)/mets:mets"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
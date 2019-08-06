<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:oai="http://www.openarchives.org/OAI/2.0/"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
  xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:mcr="xalan://org.mycore.common.xml.MCRXMLFunctions" xmlns:xalan="http://xml.apache.org/xalan"
  exclude-result-prefixes="xalan xsl xlink mods mcr">
  
  <xsl:output method="xml" encoding="UTF-8" />
  <xsl:include href="mods2record.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" select="''"/>
  <xsl:param name="ServletsBaseURL" select="''"/>
  <xsl:param name="HttpSession" select="''"/>

<!-- /mycore/mycore-mods/src/main/resources/xsl/mods2dc.xsl -->
<!-- <xsl:include href="mods2dc.xsl" -->

  <xsl:variable name="ifsTemp">
    <xsl:for-each select="mycoreobject/structure/derobjects/derobject[@xlink:title='DV_METS']">
      <der id="{@xlink:href}">
        <xsl:copy-of select="document(concat('xslStyle:mcr_directory-recursive:ifs:',@xlink:href,'/'))" />
      </der>
    </xsl:for-each>
  </xsl:variable>
  <xsl:variable name="ifs" select="xalan:nodeset($ifsTemp)" />

  <xsl:template match="mycoreobject" mode="metadata">
      <xsl:if test="$ifs/der">
           <xsl:variable name="uri" select="$ifs/der/mcr_directory/children//child[@type='file']/uri" />
           <!-- <uri>ifs:/rosdok_derivate_0000033887:/ppn574591117.dv.mets.xml</uri> -->
           <xsl:variable name="fileuri" select="concat('mcrfile:', $ifs/der/@id, '/', $ifs/der/mcr_directory/children//child[@type='file']/name)" />
            
          <xsl:copy-of select="document($fileuri)" />
      </xsl:if>
  </xsl:template>

</xsl:stylesheet>

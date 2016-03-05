<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
   xmlns="http://www.openarchives.org/OAI/2.0/"  
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xalan="http://xml.apache.org/xalan"
   xmlns:class2set="xalan://org.mycore.oai.classmapping.MCRClassificationAndSetMapper"
   exclude-result-prefixes="xalan class2set">
  
<xsl:output method="xml" encoding="UTF-8" indent="yes" />
  
<xsl:param name="MCR.OAIDataProvider.OAI2.RepositoryIdentifier" />

<xsl:template match="mycoreobject" mode="header">
  <xsl:apply-templates select="@ID" />
  <xsl:apply-templates select="service/servdates/servdate[@type='modifydate']" />
  <xsl:for-each select="metadata/types/type|metadata/formats/format">
  	<setSpec>
    	<xsl:value-of select="concat(@classid,':',@categid)" />
  	</setSpec>
  </xsl:for-each>
  <xsl:for-each select="metadata/mappings/mapping">
  	<setSpec>
    	<xsl:value-of select="concat(class2set:mapClassificationToSet('MCR.OAIDataProvider.OAI2.', @classid),':',@categid)" />
  	</setSpec>
  </xsl:for-each>
</xsl:template>

<xsl:template match="@ID">
  <identifier>
    <xsl:value-of select="concat('oai:',$MCR.OAIDataProvider.OAI2.RepositoryIdentifier,':',.)" />
  </identifier>
</xsl:template>


<xsl:template match="servdate">
  <datestamp> 
    <xsl:value-of select="substring(text(),1,10)" />
  </datestamp>
</xsl:template>

<xsl:template match="*" />

</xsl:stylesheet>
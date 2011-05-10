<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:oai="http://www.openarchives.org/OAI/2.0/"
  xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
  xmlns:dc="http://purl.org/dc/elements/1.1/">
  
<xsl:output method="xml" encoding="UTF-8"/>
  
<xsl:template match="/classification">
  <oai:listSets>
    <xsl:apply-templates select="category">
      <xsl:with-param name="prefix" select="@id" />
    </xsl:apply-templates>
  </oai:listSets>
</xsl:template>

<xsl:template match="category">
  <xsl:param name="prefix" />
  
  <xsl:variable name="id" select="concat($prefix,':',@id)" />
  
  <oai:set>
    <oai:setSpec>
      <xsl:value-of select="$id" />
    </oai:setSpec>
    <oai:setName>
      <xsl:value-of select="@label" />
    </oai:setName>
    <xsl:if test="comment">
      <oai:setDescription>
        <oai_dc:dc>
          <dc:description>
            <xsl:value-of select="comment" />
          </dc:description>    
        </oai_dc:dc>
      </oai:setDescription>
    </xsl:if>
  </oai:set>

  <xsl:apply-templates select="category">
    <xsl:with-param name="prefix" select="$id" />
  </xsl:apply-templates>
</xsl:template>

</xsl:stylesheet>

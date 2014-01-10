<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- transforms output of documentDetails:.. URI to unqualified Dublin Core -->
<!-- is used in OAI to produce oai_dc and in DocumentServlet output by document.xsl to produce meta tags for the HTML header -->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:encoder="xalan://java.net.URLEncoder"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:pc="http://www.d-nb.de/standards/pc/"
  xmlns:cc="http://www.d-nb.de/standards/cc/"
  exclude-result-prefixes="xalan xsl encoder pc cc">

  <xsl:param name="ServletsBaseURL" />
  <xsl:param name="WebApplicationBaseURL" />

  <xsl:include href="output-category.xsl" />

  <xsl:template match="mycoreobject" mode="dc">
    <dc:identifier>http://rosdok.uni-rostock.de/resolve/id/<xsl:value-of select="./@ID" /></dc:identifier>
    <xsl:for-each select="./metadata">
      <xsl:for-each select="./titles/title">
        <dc:title><xsl:value-of select="./text()" /></dc:title>
      </xsl:for-each>
      <xsl:for-each select="./creators/creator">
        <dc:creator><xsl:value-of select="./pc:person/pc:name/pc:surName/text()" />, <xsl:value-of select="./pc:person/pc:name/pc:foreName/text()" /></dc:creator>
      </xsl:for-each>
      <xsl:for-each select="./subjects/subject[@classid='rosdok_class_000000000009']">
        <dc:subject>DDC:<xsl:call-template name="classification_label">
            <xsl:with-param name="lang">de</xsl:with-param>
            <xsl:with-param name="classID"><xsl:value-of select="./@classid" /></xsl:with-param>
            <xsl:with-param name="categID"><xsl:value-of select="./@categid" /> </xsl:with-param>
          </xsl:call-template></dc:subject>
      </xsl:for-each>
       <xsl:for-each select="./keywords/keyword">
        <dc:subject><xsl:value-of select="./text()" /></dc:subject>
      </xsl:for-each>
       <xsl:for-each select="./contributors/contributor">
        <dc:contributor><xsl:value-of select="./pc:person/pc:name/pc:surName/text()" />, <xsl:value-of select="./pc:person/pc:name/pc:foreName/text()" /></dc:contributor>
       </xsl:for-each>
       <xsl:for-each select="./origins/origin">
        <dc:contributor>
          <xsl:call-template name="classification_label">
            <xsl:with-param name="lang">de</xsl:with-param>
            <xsl:with-param name="classID"><xsl:value-of select="./@classid" /></xsl:with-param>
            <xsl:with-param name="categID"><xsl:value-of select="./@categid" /> </xsl:with-param>
          </xsl:call-template>
        </dc:contributor>
       </xsl:for-each>
      
       <xsl:for-each select="./dates/date[@type='published']">
        <dc:date><xsl:value-of select="./text()" /> </dc:date>
       </xsl:for-each>
       
       <xsl:for-each select="./mappings/mapping[@classid='diniPublType']">
        <dc:type><xsl:value-of select="./@categid" /></dc:type>
       </xsl:for-each>
       
       <xsl:for-each select="./mappings/mapping[@classid='dctermsDCMIType']">
        <dc:type><xsl:value-of select="./@categid" /></dc:type>
       </xsl:for-each>
       <xsl:for-each select="./urns/urn">
        <dc:identifier><xsl:value-of select="./text()" /></dc:identifier>
       </xsl:for-each>
       <xsl:for-each select="./languages/language">
        <dc:language><xsl:value-of select="./@categid" /></dc:language>
       </xsl:for-each>
       <xsl:for-each select="./descriptions/description">
        <dc:description><xsl:value-of select="./text()" /></dc:description>
       </xsl:for-each>
       <xsl:for-each select="./publishers/publisher">
        <dc:publisher><xsl:value-of select="./cc:universityOrInstitution/cc:name/text()" /></dc:publisher>
       </xsl:for-each>
       <xsl:for-each select="./accessrights/accessright">
        <dc:rights><xsl:value-of select="./@categid" /></dc:rights>
       </xsl:for-each>
       
     </xsl:for-each>
    </xsl:template>

 <xsl:template name="classification_label">
      <xsl:param name="classID" />
      <xsl:param name="categID" />
      <xsl:param name="lang" />
      <xsl:for-each select="document(concat('classification:metadata:1:children:',$classID,':',$categID))//category/label[@xml:lang=$lang]">
        <xsl:value-of select="./@text" />
      </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>

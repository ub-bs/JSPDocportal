<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:dini="http://www.d-nb.de/standards/xmetadissplus/type/"
  xmlns:ddb="http://www.d-nb.de/standards/ddb/"
  xmlns:cc="http://www.d-nb.de/standards/cc/"
  xmlns:pc="http://www.d-nb.de/standards/pc/"
  xmlns:thesis="http://www.ndltd.org/standards/metadata/etdms/1.0/"
  xmlns:xMetaDiss="http://www.d-nb.de/standards/xmetadissplus/xmetadissplus.xsd"
>
  
<xsl:param name="ServletsBaseURL" />

<xsl:include href="document2record.xsl" />

<xsl:template name="publisher">
  <dc:publisher xsi:type="cc:Publisher" ddb:role="Universitätsbibliothek" type="dcterms:ISO3166" countryCode="DE">
    <cc:universityOrInstitution cc:GKD-Nr="16023254-5">
      <cc:name>Universitätsbibliothek Duisburg-Essen</cc:name>
      <cc:place>Essen</cc:place>
      <cc:place>Duisburg</cc:place>
    </cc:universityOrInstitution>
    <cc:address>Universitätsstraße 9 - 11, 45141 Essen</cc:address>
  </dc:publisher>
</xsl:template>

<xsl:template name="type">
  <dc:type xsi:type="dini:PublType">doctoralThesis</dc:type>
  <dc:type xsi:type="dcterms:DCMIType">Text</dc:type>
</xsl:template>

<xsl:template name="version">
  <dini:version_driver>publishedVersion</dini:version_driver>
</xsl:template>
  
<xsl:template match="document" mode="metadata">
  <xMetaDiss:xMetaDiss xsi:schemaLocation="http://www.d-nb.de/standards/xmetadissplus/ http://www.d-nb.de/standards/xmetadissplus/xmetadissplus.xsd">
    <xsl:apply-templates select="titles/title"  />
    <xsl:apply-templates select="creators/creator" />
    <xsl:call-template name="publisher" />
    <xsl:apply-templates select="contributors/contributor[((@role = 'advisor') or (@role = 'referee')) and (legalEntity[@type='person'])]" />
    <xsl:apply-templates select="dates/date" />
    <xsl:call-template name="type" />
    <xsl:call-template name="version" />
    <xsl:apply-templates select="identifier" />
    <xsl:call-template name="citation" />
  </xMetaDiss:xMetaDiss>
</xsl:template>

<xsl:template match="title">
  <xsl:choose>
    <xsl:when test="preceding::title[@lang = current()/@lang]">
      <dcterms:alternative xsi:type="ddb:talternativeISO639-2">
        <xsl:apply-templates select="." mode="common" />
      </dcterms:alternative>
    </xsl:when>
    <xsl:otherwise>
      <dc:title xsi:type="ddb:titleISO639-2">
        <xsl:apply-templates select="." mode="common" />
      </dc:title>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="title" mode="common">
  <xsl:if test="not(current()/@lang=../title[1]/@lang)">
    <xsl:attribute name="ddb:type">translated</xsl:attribute>
  </xsl:if> 
  <xsl:copy-of select="@lang" />
  <xsl:value-of select="text()" />
</xsl:template>

<xsl:template match="creator">
  <dc:creator>
    <xsl:for-each select="legalEntity">
      <xsl:attribute name="xsi:type">
        <xsl:choose>
          <xsl:when test="@type='person'">pc:MetaPers</xsl:when>
          <xsl:otherwise>cc:UniversityOrInstitution</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="." />
    </xsl:for-each>
  </dc:creator>
</xsl:template>

<xsl:template match="contributor">
  <dc:contributor xsi:type="pc:Contributor" thesis:role="{@role}">
    <xsl:apply-templates select="legalEntity" />
  </dc:contributor>
</xsl:template>

<xsl:template match="legalEntity[@type='corporation']">
  <cc:universityOrInstitution>
    <xsl:for-each select="names/name">
      <cc:name>
        <xsl:value-of select="text()" />
      </cc:name>
    </xsl:for-each>
  </cc:universityOrInstitution>
</xsl:template>  

<xsl:template match="legalEntity[@type='person']">
  <pc:person>
    <xsl:apply-templates select="names/name" />
    <xsl:apply-templates select="title" mode="person" />
    <xsl:apply-templates select="born/*" />
  </pc:person>
</xsl:template>

<xsl:template match="name[1]">
  <pc:name type="nameUsedByThePerson">
    <xsl:apply-templates select="." mode="common" />
  </pc:name>
</xsl:template>

<xsl:template match="name">
  <pc:name type="otherName" otherNameType="variants">
    <xsl:apply-templates select="." mode="common" />
  </pc:name>
</xsl:template>

<xsl:template match="name" mode="common">
  <xsl:choose>
    <xsl:when test="contains(text(),',')">
      <pc:foreName>
        <xsl:value-of select="normalize-space(substring-after(text(),','))" />
      </pc:foreName>
      <pc:sureName>
        <xsl:value-of select="normalize-space(substring-before(text(),','))" />
      </pc:sureName>
    </xsl:when>
    <xsl:otherwise>
      <pc:foreName>
        <xsl:call-template name="foreName">
          <xsl:with-param name="rest" select="text()" />
        </xsl:call-template>
      </pc:foreName>
      <pc:lastName>
        <xsl:call-template name="lastName">
          <xsl:with-param name="rest" select="text()" />
        </xsl:call-template>
      </pc:lastName>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="foreName">
  <xsl:param name="rest" />
  
  <xsl:if test="contains($rest,' ')">
    <xsl:value-of select="substring-before($rest,' ')" />
    <xsl:if test="contains(substring-after($rest,' '),' ')"> </xsl:if>
    <xsl:call-template name="foreName">
      <xsl:with-param name="rest" select="substring-after($rest,' ')" />
    </xsl:call-template>
  </xsl:if>
</xsl:template>
  
<xsl:template name="lastName">
  <xsl:param name="rest" />

  <xsl:choose>  
    <xsl:when test="contains($rest,' ')">
      <xsl:call-template name="lastName">
        <xsl:with-param name="rest" select="substring-after($rest,' ')" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="normalize-space($rest)" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="title" mode="person">
  <pc:academicTitle>
    <xsl:value-of select="text()" />
  </pc:academicTitle>
</xsl:template>

<xsl:template match="born/place">
  <pc:placeOfBirth>
    <xsl:value-of select="text()" />
  </pc:placeOfBirth>
</xsl:template>

<xsl:template match="born/date">
  <pc:dateOfBirth>
    <xsl:apply-templates select="." mode="W3CDTF" />
  </pc:dateOfBirth>
</xsl:template>

<xsl:template match="dates/date[@type='created']">
  <dcterms:created>
    <xsl:apply-templates select="." mode="W3CDTF" />
  </dcterms:created>
</xsl:template>

<xsl:template match="dates/date[@type='submitted']">
  <dcterms:dateSubmitted>
    <xsl:apply-templates select="." mode="W3CDTF" />
  </dcterms:dateSubmitted>
</xsl:template>

<xsl:template match="dates/date[@type='accepted']">
  <dcterms:dateAccepted>
    <xsl:apply-templates select="." mode="W3CDTF" />
  </dcterms:dateAccepted>
</xsl:template>

<xsl:template match="dates/date[@type='modified']">
  <dcterms:modified>
    <xsl:apply-templates select="." mode="W3CDTF" />
  </dcterms:modified>
</xsl:template>

<xsl:template match="date" mode="W3CDTF">
  <xsl:attribute name="xsi:type">W3CDTF</xsl:attribute>
  <xsl:apply-templates select="." mode="yyyymmdd" />
</xsl:template>

<xsl:template match="identifier">
  <dc:identifier xsi:type="urn:nbn">
    <xsl:value-of select="text()" />
  </dc:identifier>
</xsl:template>

<xsl:template name="citation">
  <dcterms:bibliographicCitation>
    <xsl:for-each select="creators/creator">
      <xsl:value-of select="legalEntity/names/name[1]" />
      <xsl:if test="position() != last()">, </xsl:if>
    </xsl:for-each>
    <xsl:text>: </xsl:text>
    <xsl:for-each select="titles/title[@lang = ../title[1]/@lang]">
      <xsl:value-of select="text()" />
      <xsl:choose>
        <xsl:when test="position() != last()">: </xsl:when>
        <xsl:otherwise>, </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:for-each select="dates/date[(@type='created') or (@type='modified')][1]">
      <xsl:value-of select="substring(text(),7,4)" />
    </xsl:for-each>
    <xsl:text>: </xsl:text>
    <xsl:choose>
      <xsl:when test="identifier">
        <xsl:value-of select="identifier" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$ServletsBaseURL" />
        <xsl:text>DocumentServlet=id=</xsl:text>
        <xsl:value-of select="@ID" />
      </xsl:otherwise>
    </xsl:choose>
  </dcterms:bibliographicCitation>
</xsl:template>

<xsl:template match="*" />

</xsl:stylesheet>

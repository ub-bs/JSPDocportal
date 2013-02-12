<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- transforms output of documentDetails:.. URI to unqualified Dublin Core -->
<!-- is used in OAI to produce oai_dc and in DocumentServlet output by document.xsl to produce meta tags for the HTML header -->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  exclude-result-prefixes="xalan xsl">

  <xsl:param name="ServletsBaseURL" />
  <xsl:param name="WebApplicationBaseURL" />

  <xsl:include href="output-category.xsl" />

  <xsl:template match="document" mode="dc">
    <xsl:apply-templates select="titles/title" mode="dc" />
    <xsl:apply-templates select="creators/creator" mode="dc" />
    <xsl:apply-templates select="publishers/publisher" mode="dc" />
    <xsl:apply-templates select="contributors/contributor" mode="dc" />
    <xsl:call-template name="dc.date" />
    <xsl:apply-templates select="@alias" mode="dc" />
    <xsl:apply-templates select="@ID" mode="dc" />
    <xsl:apply-templates select="identifier|purl|link" mode="dc" />
    <xsl:apply-templates select="derivates/derivate[@private='false']" mode="dc" />
    <xsl:apply-templates select="descriptions/description" mode="dc" />
    <xsl:apply-templates select="sources/source" mode="dc" />
    <xsl:call-template name="dc.source.relation" />
    <xsl:apply-templates select="coverages/coverage" mode="dc" />
    <xsl:apply-templates select="rightsRemarks/rightsRemark" mode="dc" />
    <xsl:apply-templates select="keywords" mode="dc" />
    <xsl:apply-templates select="subjects/subject/category" mode="dc" />

    <xsl:apply-templates select="orgUnits/category" mode="dc">
      <xsl:with-param name="classID" select="'ORIGIN'" />
    </xsl:apply-templates>
    <xsl:apply-templates select="documentTypes/category" mode="dc">
      <xsl:with-param name="classID" select="'TYPE'" />
      <xsl:with-param name="name" select="'type'" />
    </xsl:apply-templates>

    <xsl:apply-templates select="languages/language" mode="dc" />
    <xsl:apply-templates select="relation" mode="dc" />
    <xsl:call-template name="dc.format" />
    <xsl:apply-templates select="mediaTypes/category" mode="dctype" />
  </xsl:template>

  <xsl:template match="title" mode="dc">
    <dc:title>
      <xsl:apply-templates select="@lang" mode="dc" />
      <xsl:value-of select="text()" />
    </dc:title>
  </xsl:template>

  <xsl:template match="@lang" mode="dc">
    <xsl:attribute name="xml:lang">
      <xsl:value-of select="document(concat('language:',.))/language/@xmlCode" />
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="creator" mode="dc">
    <dc:creator>
      <xsl:value-of select="@name" />
    </dc:creator>
  </xsl:template>

  <xsl:template match="publisher" mode="dc">
    <dc:publisher>
      <xsl:value-of select="@name" />
    </dc:publisher>
  </xsl:template>

  <xsl:template match="contributor" mode="dc">
    <dc:contributor>
      <xsl:value-of select="@name" />
    </dc:contributor>
  </xsl:template>

  <xsl:template name="dc.date">
    <xsl:choose>
      <xsl:when test="dates/date[@type='created']">
        <xsl:apply-templates select="dates/date[@type='created']" mode="dc" />
      </xsl:when>
      <xsl:when test="dates/date[@type='modified']">
        <xsl:apply-templates select="dates/date[@type='modified']" mode="dc" />
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="date" mode="dc">
    <dc:date>
      <xsl:value-of select="substring(.,7,4)" />
      <xsl:text>-</xsl:text>
      <xsl:value-of select="substring(.,4,2)" />
      <xsl:text>-</xsl:text>
      <xsl:value-of select="substring(.,1,2)" />
    </dc:date>
  </xsl:template>

  <xsl:template match="@ID" mode="dc">
    <dc:identifier>
      <xsl:value-of select="concat($ServletsBaseURL,'DocumentServlet?id=',.)" />
    </dc:identifier>
  </xsl:template>

  <xsl:template match="@alias" mode="dc">
    <dc:identifier>
      <xsl:value-of select="concat($WebApplicationBaseURL,'go/',.)" />
    </dc:identifier>
  </xsl:template>

  <xsl:template match="identifier|purl|link" mode="dc">
    <dc:identifier>
      <xsl:value-of select="text()" />
    </dc:identifier>
  </xsl:template>

  <xsl:template match="derivate[@private='false']" mode="dc">
    <dc:identifier>
      <xsl:value-of select="$ServletsBaseURL" />
      <xsl:text>DerivateServlet/Derivate-</xsl:text>
      <xsl:value-of select="@ID" />
      <xsl:text>/</xsl:text>
      <xsl:value-of select="files/@main" />
    </dc:identifier>
  </xsl:template>

  <xsl:template match="description" mode="dc">
    <dc:description>
      <xsl:apply-templates select="@lang" mode="dc" />
      <xsl:value-of select="text()" />
    </dc:description>
  </xsl:template>

  <xsl:template match="source" mode="dc">
    <dc:source>
      <xsl:apply-templates select="@lang" mode="dc" />
      <xsl:value-of select="text()" />
    </dc:source>
  </xsl:template>

  <xsl:template match="coverage" mode="dc">
    <dc:coverage>
      <xsl:apply-templates select="@lang" mode="dc" />
      <xsl:value-of select="text()" />
    </dc:coverage>
  </xsl:template>

  <xsl:template match="rightsRemark" mode="dc">
    <dc:rights>
      <xsl:apply-templates select="@lang" mode="dc" />
      <xsl:value-of select="text()" />
    </dc:rights>
  </xsl:template>

  <xsl:template name="dc.source.relation">
    <xsl:for-each select="relation[@type='isPartOf']">
      <dc:source>
        <xsl:apply-templates select="document(concat('documentDetails:',@target))/document" mode="dcsource" />
      </dc:source>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="document" mode="dcsource">
    <xsl:for-each select="relation[@type='isPartOf']">
      <xsl:apply-templates select="document(concat('documentDetails:',@target))/document" mode="dcsource" />
      <xsl:text>, </xsl:text>
    </xsl:for-each>
    <xsl:value-of select="titles/title[1]/text()" />
  </xsl:template>

  <xsl:template match="keywords" mode="dc">
    <xsl:call-template name="keywordTokenizer">
      <xsl:with-param name="keywords" select="." />
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="keywordTokenizer">
    <xsl:param name="keywords" />

    <xsl:choose>
      <xsl:when test="contains($keywords,',')">
        <dc:subject>
          <xsl:value-of select="normalize-space(substring-before($keywords,','))" />
        </dc:subject>
        <xsl:call-template name="keywordTokenizer">
          <xsl:with-param name="keywords" select="normalize-space(substring-after($keywords,','))" />
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($keywords,';')">
        <dc:subject>
          <xsl:value-of select="normalize-space(substring-before($keywords,';'))" />
        </dc:subject>
        <xsl:call-template name="keywordTokenizer">
          <xsl:with-param name="keywords" select="normalize-space(substring-after($keywords,';'))" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <dc:subject>
          <xsl:value-of select="$keywords" />
        </dc:subject>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="category" mode="dc">
    <xsl:param name="classID" select="@CLASSID" />
    <xsl:param name="name" select="'subject'" />
    
    <xsl:element name="dc:{$name}">
      <xsl:call-template name="output.category">
        <xsl:with-param name="classID" select="$classID" />
        <xsl:with-param name="categID" select="@ID" />
      </xsl:call-template>
    </xsl:element>
  </xsl:template>

  <xsl:template match="language" mode="dc">
    <dc:language>
      <xsl:value-of select="document(concat('language:',.))/language/@termCode" />
    </dc:language>
  </xsl:template>

  <xsl:template match="relation" mode="dc">
    <dc:relation>
      <xsl:value-of select="concat($ServletsBaseURL,'DocumentServlet?id=',@target)" />
    </dc:relation>
  </xsl:template>

  <xsl:template name="dc.format">
    <!-- Collect MIME types of all files in all derivates -->
    <xsl:variable name="fcts" select="document('resource:FileContentTypes.xml')/FileContentTypes/type" />

    <xsl:variable name="mimes1">
      <xsl:for-each select="derivates/derivate[@private='false']/files/file">
        <mime>
          <xsl:value-of select="$fcts[@ID=current()/@contenttype]/mime" />
        </mime>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="mimes2" select="xalan:nodeset($mimes1)" />

  <!-- Sort MIME types by frequency -->
    <xsl:variable name="mimes3">
      <xsl:for-each select="$mimes2/mime">
        <xsl:sort select="count($mimes2/mime[text()=current()/text()])" data-type="number" order="descending" />
        <xsl:copy-of select="." />
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="mimes4" select="xalan:nodeset($mimes3)" />

    <xsl:for-each select="$mimes4/mime">
      <xsl:if test="not(preceding-sibling::mime[text()=current()/text()])">
        <dc:format>
          <xsl:value-of select="text()" />
        </dc:format>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="category" mode="dctype">
    <xsl:if test="@ID=1">
      <dc:type>Text</dc:type>
    </xsl:if>
    <xsl:if test="@ID=2">
      <dc:type>Sound</dc:type>
    </xsl:if>
    <xsl:if test="@ID=3">
      <dc:type>MovingImage</dc:type>
    </xsl:if>
    <xsl:if test="@ID=4">
      <dc:type>StillImage</dc:type>
    </xsl:if>
    <xsl:if test="(@ID=3) or (@ID=4)">
      <dc:type>Image</dc:type>
    </xsl:if>
    <xsl:if test="(@ID=5) or (@ID=7)">
      <dc:type>InteractiveResource</dc:type>
    </xsl:if>
    <xsl:if test="@ID=6">
      <dc:type>Software</dc:type>
    </xsl:if>
  </xsl:template>

  <xsl:template match="*" mode="dc" />

</xsl:stylesheet>

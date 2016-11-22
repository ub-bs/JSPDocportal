<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:mods="http://www.loc.gov/mods/v3" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation" 
  xmlns:mcrmods="xalan://org.mycore.mods.classification.MCRMODSClassificationSupport"
  xmlns:mcrxsl="xalan://org.mycore.common.xml.MCRXMLFunctions" 
  xmlns:mcr="http://www.mycore.org/" 
  exclude-result-prefixes="mods xlink xalan i18n mcrmods mcrxsl mcr">
 
  <!-- to enable relative urls in import set xsltSystemId attribute in x:transform of JSP XML Tag Library !!! -->
  <xsl:import href="mods-util.xsl" />
  
  <xsl:output method="html" indent="yes" standalone="no" encoding="UTF-8"/>

  <xsl:param name="WebApplicationBaseURL"></xsl:param>

  <xsl:template match="/">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
      <xsl:for-each select="./mods:relatedItem[@type='host']/mods:recordInfo">
            <xsl:element name="a">
              <xsl:attribute name="class">btn btn-default btn-sm pull-right ir-btn-goto-top</xsl:attribute>
              <xsl:attribute name="href"><xsl:value-of select="$WebApplicationBaseURL" />resolve/recordIdentifier/<xsl:value-of select="substring-before(./mods:recordIdentifier, '/')"/>%252F<xsl:value-of select="substring-after(./mods:recordIdentifier, '/')"/></xsl:attribute>
              <xsl:attribute name="title"><xsl:value-of select="../mods:titleInfo/mods:title" /></xsl:attribute>
              <xsl:value-of select="i18n:translate('Webpage.docdetails.gotoParent')" />&#160;&#160;<span class="glyphicon glyphicon-share-alt"></span>
            </xsl:element>
      </xsl:for-each> 

      <p>
        <xsl:call-template name="mods-name" /><br />
      </p>
      
     <xsl:call-template name="mods-title" />
     

      <p>
      <xsl:call-template name="mods-originInfo" />
      </p>
      <xsl:choose>
       <xsl:when test="./mods:identifier[@type='doi']">
        <p>DOI: <xsl:element name="a">
            <xsl:attribute name="href">http://dx.doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" /></xsl:attribute>
             <xsl:value-of select="./mods:identifier[@type='doi']" />
          </xsl:element>
         </p>
        </xsl:when>
       <xsl:when test="./mods:identifier[@type='purl']">
        <p>
          <!-- <span style="display:inline-block;background-color: #E6E6E6; padding: 3px; color:#666666;">
           <abbr title="dauerhafte, zitierbare Adresse">Persistente URL</abbr>:
            </span>&#160; -->
            <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="./mods:identifier[@type='purl']" /></xsl:attribute>
             <xsl:value-of select="./mods:identifier[@type='purl']" />
          </xsl:element>
         </p>
        </xsl:when>
        </xsl:choose>
        <xsl:if test="./mods:abstract">
        <p class="ir-docdetails-abstract">
          <xsl:value-of select="./mods:abstract" />
       </p>       
       </xsl:if>
        <xsl:if test="./mods:classification[@displayLabel='doctype']">
        <p>
        <span class="label label-default">
          <xsl:for-each select="./mods:classification[@displayLabel='doctype']/@valueURI">
            <xsl:call-template name="classLabel">
              <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>
        </span>
        </p>
      </xsl:if>
        
    </xsl:for-each>
    
  </xsl:template>

</xsl:stylesheet>
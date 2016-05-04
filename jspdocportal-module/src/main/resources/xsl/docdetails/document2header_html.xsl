<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
  version="1.0" exclude-result-prefixes="mods">
 
  <!-- to enable relative urls in import set xsltSystemId attribute in x:transform of JSP XML Tag Library !!! -->
  <xsl:import href="mods-util.xsl" />
  
  <xsl:output method="html" indent="yes" standalone="no" encoding="UTF-8"/>

  <xsl:param name="WebApplicationBaseURL"></xsl:param>

  <xsl:template match="/">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
      <xsl:for-each select="./mods:relatedItem[@type='host']">
         <xsl:for-each select="./mods:recordInfo/mods:recordIdentifier">
              <span class="button">
                  <xsl:element name="a">
                       <xsl:attribute name="href"><xsl:value-of select="$WebApplicationBaseURL" />resolve/recordIdentifier/<xsl:value-of select="substring-before(., '/')"/>%252F<xsl:value-of select="substring-after(., '/')"/></xsl:attribute>
                       <xsl:attribute name="title"><xsl:value-of select="./mods:titleInfo/mods:title" /></xsl:attribute>
                        zum übergeordneten Dokument
                  </xsl:element>
               </span> 
                <br />  <br />
         </xsl:for-each> 
        </xsl:for-each>
       
      <p>
        <xsl:call-template name="mods-name" /><br />
      </p>
      <p>    
      <span style="font-size:115%;line-height:130%;color:#004A99;">
        <xsl:call-template name="mods-title" />
      </span>
      </p>
      <p>
      <xsl:call-template name="mods-originInfo" />
      </p>
      
       <xsl:if test="./mods:abstract">
        <p style="font-size:90%">
          <xsl:value-of select="./mods:abstract" />
       </p>
       <p></p>
       </xsl:if>
       <xsl:if test="./mods:identifier[@type='doi']">
        <p>DOI:&#160; 
            <xsl:element name="a">
            <xsl:attribute name="href">http://dx.doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" /></xsl:attribute>
             <xsl:value-of select="./mods:identifier[@type='doi']" />
          </xsl:element>
         </p>
        </xsl:if>
       <xsl:if test="./mods:identifier[@type='purl']">
        <p>
          <!-- <span style="display:inline-block;background-color: #E6E6E6; padding: 3px; color:#666666;">
           <abbr title="dauerhafte, zitierbare Adresse">Persistente URL</abbr>:
            </span>&#160; -->
            <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="./mods:identifier[@type='purl']" /></xsl:attribute>
             <xsl:value-of select="./mods:identifier[@type='purl']" />
          </xsl:element>
         </p>
        </xsl:if>
    </xsl:for-each>
    
  </xsl:template>

</xsl:stylesheet>
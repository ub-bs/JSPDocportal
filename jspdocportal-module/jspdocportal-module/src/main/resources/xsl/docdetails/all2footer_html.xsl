<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:mods="http://www.loc.gov/mods/v3" 
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:ubr-legal="http://purl.uni-rostock.de/ub/standards/ubr-legal-information-v1.0"
  version="1.0" exclude-result-prefixes="mods xlink">
    <xsl:import href="mods-util.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" />
  <xsl:output method="html" indent="yes" standalone="no" />
  <xsl:template match="/">
  <table class="table ir-table-docdetails">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:identifier[@type='purl']">
          <tr>
            <th>Persistente URL:</th>
            <td><table class="ir-table-docdetails-values"><tr><td><xsl:value-of select="." />
            </td></tr></table>
            </td>
        </tr>
    </xsl:for-each>
         <tr>
            <th>erstellt am:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="substring-before(/mycoreobject/service/servdates/servdate[@type='createdate'],'T')" />
            </td></tr></table>
            </td>
          </tr>
          <tr>
            <th>zuletzt geändert am:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="substring-before(/mycoreobject/service/servdates/servdate[@type='modifydate'],'T')" />
            </td></tr></table>
            </td>
          </tr>
          
      
          <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:metadata">
          <tr>
            <th>Metadaten-Lizenz:</th>
              <td>
              <table class="ir-table-docdetails-values"><tr><td>
                  <xsl:if test="./ubr-legal:license/@uri='http://creativecommons.org/publicdomain/zero/1.0/'">
                      <div style="float:left">
                        <a rel="license" href="http://creativecommons.org/publicdomain/zero/1.0/">
                          <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/p/zero/1.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">CC0</xsl:attribute>
                           </xsl:element>
                          </a>
                      </div>
                      <div style="position: relative; margin-left:100px; font-size:90%; text-align:justify">
                          Die UB Rostock stellt die 
                          <xsl:element name="a">
                            <xsl:attribute name="href"><xsl:value-of select="$WebApplicationBaseURL"/>api/v1/objects/<xsl:value-of select="/mycoreobject/@ID" /></xsl:attribute>
                          Metadaten
                          </xsl:element> 
                          zu diesem Dokument unter <a href="http://creativecommons.org/publicdomain/zero/1.0/">Public Domain</a> und verzichtet damit weltweit auf alle urheberrechtlichen und verwandten Schutzrechte. 
                      </div>
              </xsl:if>
              </td></tr></table>
           </td>
         </tr>
         </xsl:for-each>
          
  </table>            
  </xsl:template>
</xsl:stylesheet>
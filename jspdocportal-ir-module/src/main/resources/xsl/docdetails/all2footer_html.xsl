<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:mods="http://www.loc.gov/mods/v3" 
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:ubr-legal="http://purl.uni-rostock.de/ub/standards/ubr-legal-information-v1.0"
  exclude-result-prefixes="mods xlink ubr-legal">
  
  <xsl:import href="mods-util.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" />
  <xsl:output method="html" indent="yes" standalone="no" />
  <xsl:template match="/">
  <table class="ir-table-docdetails" style="margin-top:45px">
          <tr><td colspan="2"><hr /></td></tr>
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
          
      
          <xsl:if test="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:metadata or
                          /mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods/mods:classification[@displayLabel='licenseinfo'][contains(@valueURI, '#metadata.cc0')]">
          <tr>
            <th>Metadaten-Lizenz:</th>
              <td>
              <table class="ir-table-docdetails-values"><tr><td>
                      <div style="float:left">
                        <a rel="license" href="http://creativecommons.org/publicdomain/zero/1.0/">
                          <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/p/zero/1.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">CC0</xsl:attribute>
                           </xsl:element>
                          </a>
                      </div>
                      <div class="small" style="position: relative; margin-left:100px;">
                          Die Hochschulbibliothek Neubrandenburg stellt die 
                          <xsl:element name="a">
                            <xsl:attribute name="href"><xsl:value-of select="$WebApplicationBaseURL"/>api/v1/objects/<xsl:value-of select="/mycoreobject/@ID" /></xsl:attribute>
                          Metadaten
                          </xsl:element> 
                          zu diesem Dokument unter <a href="http://creativecommons.org/publicdomain/zero/1.0/">Public Domain</a> und verzichtet damit weltweit auf alle urheberrechtlichen und verwandten Schutzrechte. 
                      </div>
              </td></tr></table>
           </td>
         </tr>
         </xsl:if>
          
  </table>            
  </xsl:template>
</xsl:stylesheet>
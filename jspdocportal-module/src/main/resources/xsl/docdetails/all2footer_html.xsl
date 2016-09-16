<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink"
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
  </table>            
  </xsl:template>
</xsl:stylesheet>
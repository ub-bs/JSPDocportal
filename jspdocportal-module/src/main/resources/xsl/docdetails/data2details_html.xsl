<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:ubr-researchdata="http://purl.uni-rostock.de/ub/standards/ubr-researchdata-information-v1.0"
  xmlns:ubr-legal="http://purl.uni-rostock.de/ub/standards/ubr-legal-information-v1.0"
  version="1.0" exclude-result-prefixes="mods xlink">
    <xsl:import href="mods-util.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" />
  <xsl:output method="html" indent="yes" standalone="no" />
  <xsl:template match="/">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
      <xsl:if test="./mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:originalWork">
        <table class="table ir-table-docdetails">
          <tr>
            <th>Lizenz:</th>
            <td><table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:originalWork">
                 <xsl:if test="./ubr-legal:license/@uri='http://creativecommons.org/licenses/by/4.0/'">
                 <tr><td>
                   <div style="float:left">
                   <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">
                    <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/l/by/4.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">Creative Commons Lizenzvertrag (CC-BY)</xsl:attribute>
                    </xsl:element>
                   </a>
                   </div>
                   <div style="position: relative; margin-left:100px; font-size:90%; text-align:justify">
                     Dieses Werk ist lizenziert unter einer 
                     <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">
                     Creative Commons Namensnennung 4.0 International Lizenz</a>.
                   </div>
                 </td></tr>     
                 </xsl:if>
                 <xsl:if test="./ubr-legal:license/@uri='http://creativecommons.org/licenses/by-sa/4.0/'">
                 <tr><td>
                   <div style="float:left">
                   <a rel="license" href="http://creativecommons.org/licenses/by-sa/4.0/">
                    <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/l/by-sa/4.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">Creative Commons Lizenzvertrag (CC-BY-SA)</xsl:attribute>
                    </xsl:element>
                   </a>
                   </div>
                   <div style="position: relative; margin-left:100px; font-size:90%; text-align:justify">
                     Dieses Werk ist lizenziert unter einer 
                     <a rel="license" href="http://creativecommons.org/licenses/by-sa/4.0/">
                     Creative Commons Namensnennung - Weitergabe unter gleichen Bedingungen 4.0 International Lizenz</a>.
                   </div>
                 </td></tr>     
                 </xsl:if>
                 <xsl:if test="./ubr-legal:license/@uri='http://creativecommons.org/licenses/by-nc/4.0/'">
                 <tr><td>
                   <div style="float:left">
                   <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">
                    <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/l/by-nc/4.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">Creative Commons Lizenzvertrag (CC-BY-NC)</xsl:attribute>
                    </xsl:element>
                   </a>
                   </div>
                   <div style="position: relative; margin-left:100px; font-size:90%; text-align:justify">
                     Dieses Werk ist lizenziert unter einer 
                     <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">
                     Creative Commons Namensnennung - Nicht-kommerziell 4.0 International Lizenz</a>.
                   </div>
                 </td></tr>
                 </xsl:if>
                 <xsl:if test="./ubr-legal:license/@uri='http://creativecommons.org/licenses/by-nc-sa/4.0/'">
                 <tr><td>
                   <div style="float:left">
                   <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">
                    <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/l/by-nc-sa/4.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">Creative Commons Lizenzvertrag (CC-BY-NC-SA)</xsl:attribute>
                    </xsl:element>
                   </a>
                   </div>
                   <div style="position: relative; margin-left:100px; font-size:90%; text-align:justify">
                     Dieses Werk ist lizenziert unter einer 
                     <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">
                     Creative Commons Namensnennung - Nicht-kommerziell - Weitergabe unter gleichen Bedingungen 4.0 International Lizenz</a>.
                   </div>
                 </td></tr>     
                 </xsl:if>
                 
             </xsl:for-each>
             </table>
           </td>
         </tr>
         </table>
      </xsl:if> 

      <table class="table ir-table-docdetails">
      <xsl:for-each select="./mods:originInfo[@eventType='creation']">
          <tr>
            <th>Jahr der Erstellung:</th>
            <td><table class="ir-table-docdetails-values">
              <xsl:if test="./mods:dateCreated">
               <tr><td><xsl:value-of select="./mods:dateCreated" /></td></tr>
              </xsl:if>
              </table>
            </td>
          </tr>
        </xsl:for-each>
      
      <xsl:if test="./mods:identifier[@type='local']">
          <tr>
            <th>Lokaler Identifikator:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
               <xsl:value-of select="./mods:identifier[@type='local']" />
            </td></tr></table></td>
          </tr>
        </xsl:if>
        
         <xsl:if test="./mods:name[@type='personal'][position()>1 and not(contains('aut edt cre', ./mods:role/mods:roleTerm[@type='code']/text()))]">
          <tr>
            <th>weitere Beteiligte:</th>
            <td>
              <table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:name[@type='personal'][position()>1 and not(contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))]">
                  <tr><td>
                  <xsl:call-template name="display-name">
                    <xsl:with-param name="name" select="." />
                  </xsl:call-template>
                  <xsl:if test="position()!=last()">
                    <xsl:text>&#160;;&#160;&#160;</xsl:text>
                  </xsl:if>
                  </td></tr>
              </xsl:for-each>
              </table>
            </td>
          </tr>
        
        </xsl:if>
     
        <xsl:if test="./mods:extension/ubr-researchdata:researchDataInformation/ubr-researchdata:researchDataType">
          <tr>
            <th>Typ:</th>
            <td>
              <table class="ir-table-docdetails-values"><tr><td>
                 <xsl:value-of select="./mods:extension/ubr-researchdata:researchDataInformation/ubr-researchdata:researchDataType" />
               </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        
       <xsl:if test="./mods:subject/mods:topic">
          <tr>
            <th class="docdetails-label">Schlagworte:</th>
            <td>
              <table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:subject/mods:topic">
                  <tr><td>
                  <xsl:value-of select="./text()" />
                  </td></tr>
              </xsl:for-each>
              </table>
            </td>
          </tr>
        </xsl:if>
        
          <xsl:if test="./mods:relatedItem[@type='isReferencedBy']">
          <tr>
            <th>Publikationen:</th>
            <td>
              <table class="ir-table-docdetails-values">
             <xsl:for-each select="./mods:relatedItem[@type='isReferencedBy']">
             <tr><td>
                <xsl:value-of select="./mods:note" />
                <xsl:choose>
                  <xsl:when test="./mods:identifier[@type='urn']">
                       <br />URN: <xsl:element name="a">
                            <xsl:attribute name="href">http://nbn-resolving.org/<xsl:value-of select="./mods:identifier[@type='urn']" /></xsl:attribute> 
                            <xsl:value-of select="./mods:identifier[@type='urn']" />
                           </xsl:element> 
                  </xsl:when>
                  <xsl:when test="./mods:identifier[@type='doi']">
                       <br />DOI:<xsl:element name="a">
                            <xsl:attribute name="href">https://doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" /></xsl:attribute> 
                            https://doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" />
                           </xsl:element> 
                  </xsl:when>
                  <xsl:when test="./mods:identifier[@type='url' or @type='purl']">
                      <br />URL: <xsl:element name="a">
                            <xsl:attribute name="href"><xsl:value-of select="./mods:identifier[@type='url' or @type='purl']" /></xsl:attribute> 
                            <xsl:value-of select="./mods:identifier[@type='url' or @type='purl']" />
                           </xsl:element>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="./mods:identifier" />
                  </xsl:otherwise>
                </xsl:choose>
                </td></tr>
              </xsl:for-each>
              </table>
            </td>
          </tr>
        </xsl:if>
        </table>
      
      <table class="table ir-table-docdetails">      
      <xsl:if test="./mods:classification[@displayLabel='doctype']">
          <tr>
            <th>Dokumenttyp:</th>
            <td>
              <table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:classification[@displayLabel='doctype']/@valueURI">
              <tr><td>
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               </td></tr>
               </xsl:for-each>
               </table>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:language/mods:languageTerm">
          <tr>
            <th>Sprache(n):</th>
             <td>
              <table class="ir-table-docdetails-values"><tr><td>
             <xsl:call-template name="language">
                <xsl:with-param name="term"><xsl:value-of select="./mods:language/mods:languageTerm" /></xsl:with-param>
                <xsl:with-param name="lang">de</xsl:with-param>
              </xsl:call-template>
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:classification[@displayLabel='sdnb']">
          <tr>
            <th>DNB-Sachgruppe:</th>
             <td>
              <table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:classification[@displayLabel='sdnb']/@valueURI">
              <tr><td>
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               </td></tr>
               </xsl:for-each>
               </table>
            </td>
          </tr>
        </xsl:if>
        
          <xsl:if test="./mods:classification[@displayLabel='institution']">
          <tr>
            <th>Fakultät:</th>
            <td>
              <table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:classification[@displayLabel='institution']/@valueURI">
              <tr><td>
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               </td></tr>
               </xsl:for-each>
               </table>
            </td>
          </tr>
        </xsl:if>
      </table>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:ubr-researchdata="http://purl.uni-rostock.de/ub/standards/ubr-researchdata-information-v1.0"
  xmlns:ubr-legal="http://purl.uni-rostock.de/ub/standards/ubr-legal-information-v1.0"
  version="1.0" exclude-result-prefixes="mods xlink">
    <xsl:import href="mods-util.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" />
  <xsl:output method="html" indent="yes" standalone="no" />
  <xsl:template match="/">
       <xsl:if test="/mycoreobject/structure/derobjects/derobject[@xlink:title='DATA' or @xlink:title='DOCUMENTATION']/@xlink:href">
       <tr><td colspan="3">
            <div id="download_area" style="margin-bottom:48px; margin-top:24px; border:1px solid #A6A6A6;padding:12px;">
                 <table style="font-size:115%; width:100%">
                  <colgroup>
                    <col style="width: 50%;" />
                    <col style="width: 50%;" />
                  </colgroup>
                  <tr>
                    <td>
                        <xsl:call-template name="data-derivates">
                          <xsl:with-param name="label">DATA</xsl:with-param>
                           <xsl:with-param name="headline">Daten:</xsl:with-param>
                        </xsl:call-template>  
                    </td>
                    <td>
                        <xsl:call-template name="data-derivates">
                          <xsl:with-param name="label">DOCUMENTATION</xsl:with-param>
                           <xsl:with-param name="headline">Dokumentation:</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    </tr>
                   </table>
                  </div>
                  </td></tr>
            </xsl:if>
            
    
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
      
        <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
         <xsl:if test="./mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:originalWork">
          <tr>
            <td class="docdetails-label">Lizenz:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:originalWork">
                 <xsl:if test="./ubr-legal:license/@uri='http://creativecommons.org/licenses/by/4.0/'">
                   <div style="float:left">
                   <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">
                    <xsl:element name="img">
                            <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/creativecommons/l/by/4.0/88x31.png</xsl:attribute>
                            <xsl:attribute name="style">border-style: none</xsl:attribute>
                            <xsl:attribute name="alt">Creative Commons Lizenzvertrag</xsl:attribute>
                    </xsl:element>
                   </a>
                   </div>
                   <div style="position: relative; margin-left:100px; font-size:90%; text-align:justify">
                     Dieses Werk ist lizenziert unter einer 
                     <a rel="license" href="http://creativecommons.org/licenses/by-sa/4.0/">
                     Creative Commons Namensnennung 4.0 International Lizenz</a>.
                   </div>
                      
                 </xsl:if>
             </xsl:for-each>
           </td>
         </tr>
      </xsl:if> 
      <!-- 
      <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
      <xsl:if test="./mods:identifier[@type='urn']">
        <tr>
              <td class="docdetails-label">URN:</td>
              <td class="docdetails-values">
                       <xsl:element name="a">
                            <xsl:attribute name="href">http://nbn-resolving.org/<xsl:value-of select="./mods:identifier[@type='urn']" /></xsl:attribute> 
                            <xsl:value-of select="./mods:identifier[@type='urn']" />
                           </xsl:element>
              </td>
          </tr>
      </xsl:if>
      -->
       
      <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
      <xsl:for-each select="./mods:originInfo[@eventType='creation']">
          <tr>
            <td class="docdetails-label">Jahr der Erstellung:</td>
            <td class="docdetails-values">
              <xsl:if test="./mods:dateCreated">
               <xsl:value-of select="./mods:dateCreated" />
              </xsl:if>
            </td>
          </tr>
        </xsl:for-each>
      
      <xsl:if test="./mods:identifier[@type='local']">
          <tr>
            <td class="docdetails-label">Lokaler Identifikator:</td>
            <td class="docdetails-values">
               <xsl:value-of select="./mods:identifier[@type='local']" />
            </td>
          </tr>
        </xsl:if>
        
         <xsl:if test="./mods:name[@type='personal'][position()>1 and not(contains('aut edt cre', ./mods:role/mods:roleTerm[@type='code']/text()))]">
          <tr>
            <td class="docdetails-label">weitere Beteiligte:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:name[@type='personal'][position()>1 and not(contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))]">
                  <xsl:call-template name="display-name">
                    <xsl:with-param name="name" select="." />
                  </xsl:call-template>
                  <xsl:if test="position()!=last()">
                    <xsl:text>&#160;;&#160;&#160;</xsl:text>
                  </xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        
        </xsl:if>
     
        <xsl:if test="./mods:extension/ubr-researchdata:researchDataInformation/ubr-researchdata:researchDataType">
          <tr>
            <td class="docdetails-label">Typ:</td>
            <td class="docdetails-values">
               <xsl:value-of select="./mods:extension/ubr-researchdata:researchDataInformation/ubr-researchdata:researchDataType" />
            </td>
          </tr>
        </xsl:if>
        
       <xsl:if test="./mods:subject/mods:topic">
          <tr>
            <td class="docdetails-label">Schlagworte:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:subject/mods:topic">
                  <xsl:value-of select="./text()" />
                  <xsl:if test="./position()!=./last()">
                    <br />
                  </xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        
          <xsl:if test="./mods:relatedItem[@type='isReferencedBy']">
          <tr>
            <td class="docdetails-label">Publikationen:</td>
            <td class="docdetails-values">
             <xsl:for-each select="./mods:relatedItem[@type='isReferencedBy']">
                <p>
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
                            <xsl:attribute name="href">https://dx.doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" /></xsl:attribute> 
                            <xsl:value-of select="./mods:identifier[@type='doi']" />
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
                </p>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        
           <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
     
      
      <xsl:if test="./mods:classification[@displayLabel='doctype']">
          <tr>
            <td class="docdetails-label">Dokumenttyp:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:classification[@displayLabel='doctype']/@valueURI">
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               <br /> 
               </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:language/mods:languageTerm">
          <tr>
            <td class="docdetails-label">Sprache(n):</td>
            <td class="docdetails-values">
             <xsl:call-template name="language">
                <xsl:with-param name="term"><xsl:value-of select="./mods:language/mods:languageTerm" /></xsl:with-param>
                <xsl:with-param name="lang">de</xsl:with-param>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:classification[@authorityURI='http://rosdok.uni-rostock.de/classifications/DNB-DDC-SG']">
          <tr>
            <td class="docdetails-label">DNB-Sachgruppe:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:classification[@authorityURI='http://rosdok.uni-rostock.de/classifications/DNB-DDC-SG']/@valueURI">
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               <br /> 
               </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        
          <xsl:if test="./mods:classification[@displayLabel='institution']">
          <tr>
            <td class="docdetails-label">Fakultät:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:classification[@displayLabel='institution']/@valueURI">
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               <br /> 
               </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
      
       <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
        
        <xsl:if test="./mods:identifier[@type='purl']">
          <tr>
            <td class="docdetails-label">Persistente URL:</td>
            <td class="docdetails-values"><xsl:value-of select="./mods:identifier[@type='purl']" /></td>
        </tr>
        </xsl:if>
         <tr>
            <td class="docdetails-label">erstellt am:</td>
            <td class="docdetails-values">
              <xsl:value-of select="substring-before(./../../../../service/servdates/servdate[@type='createdate'],'T')" />
            </td>
          </tr>
          <tr>
            <td class="docdetails-label">zuletzt geändert am:</td>
            <td class="docdetails-values">
              <xsl:value-of select="substring-before(./../../../../service/servdates/servdate[@type='modifydate'],'T')" />
            </td>
          </tr>
          <xsl:for-each select="./mods:accessCondition/ubr-legal:legalInformation/ubr-legal:licenseInformation/ubr-legal:metadata">
          <tr>
            <td class="docdetails-label">Metadaten-Lizenz:</td>
            <td class="docdetails-values">
              
          
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
           </td>
         </tr>
      </xsl:for-each>
      
    </xsl:for-each>

  </xsl:template>
</xsl:stylesheet>
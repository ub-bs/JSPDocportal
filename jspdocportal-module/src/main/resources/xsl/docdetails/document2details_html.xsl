<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink"
  version="1.0" exclude-result-prefixes="mods xlink">
    <xsl:import href="mods-util.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" />
  <xsl:output method="html" indent="yes" standalone="no" />
  <xsl:template match="/">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">


    
       <!--  children -->
       <!--  
       <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL" />api/v1/search?q=hostRecordIdentifier:<xsl:value-of select="./mods:recordInfo/mods:recordIdentifier" />&amp;sort=hostSortstring+asc</xsl:variable>
       <xsl:variable name="searchresult" select="document($url)/response/result" />
       
       <xsl:if test="$searchresult/@numFound > 0">
        <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
             <tr>
             <xsl:element name="td">
                <xsl:attribute name="class">docdetails-label</xsl:attribute>
                <xsl:attribute name="rowspan"> <xsl:value-of select="$searchresult/@numFound + 1" /></xsl:attribute>
                zugehörige Dokumente:
            </xsl:element>
            </tr>
            
              <xsl:for-each select="$searchresult/doc">
            <tr><td><table class="ir-table-docdetails-values"><tr><td>
                <xsl:variable name="docUrl"><xsl:value-of select="$WebApplicationBaseURL" />api/v1/objects/<xsl:value-of select="./str[@name='id']" /></xsl:variable> 
                <xsl:for-each  select="document($docUrl)/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
                  <p>
                  <xsl:call-template name="mods-name-short" />
                  </p>
                  <p>
                     <xsl:for-each select="./mods:recordInfo/mods:recordIdentifier">
                          <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL" />api/v1/objects/recordIdentifier:<xsl:value-of select="substring-before(., '/')"/>%252F<xsl:value-of select="substring-after(., '/')"/></xsl:variable>
                          <xsl:element name="a">
                            <xsl:attribute name="style">font-size:108%</xsl:attribute>
                            <xsl:attribute name="href"><xsl:value-of select="$WebApplicationBaseURL" />resolve/id/<xsl:value-of select="document($url)/mycoreobject/@ID" /></xsl:attribute>
                            <xsl:for-each select="./../..">
                              <xsl:call-template name="mods-title" />
                              </xsl:for-each>        
                            
                        </xsl:element>
                  </xsl:for-each>
                  </p>
                  <p><xsl:call-template name="mods-place-date" /></p>
                  <p>http://purl.uni-rostock.de/<xsl:value-of select="./mods:recordInfo/mods:recordIdentifier" /></p>

           
                  </xsl:for-each>
                  <xsl:if test="position() != last()"><p style="padding-top:20px;"><hr style="color:#E6E6E6;"/></p></xsl:if>
              </td></tr>   
              </xsl:for-each>
        </xsl:if>
        -->
        
        <table class="table ir-table-docdetails">
        <xsl:if test="./mods:note[@type='creator_info']">
          <tr>
            <th>Titelzusatz:</th>
            <td>
               <table class="ir-table-docdetails-values">
                  <tr>
                    <td>
                      <xsl:value-of select="./mods:note[@type='creator_info']" />
                    </td>
                  </tr>
                </table>
            </td>
          </tr>
        </xsl:if>

        <xsl:if test="./mods:name[@type='personal'][position()>1 and not(contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))]">
          <tr>
            <th>weitere Beteiligte:</th>
            <td><table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:name[@type='personal'][position()>1 and not(contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))]">
                  <tr><td>
                  <xsl:call-template name="display-name">
                    <xsl:with-param name="name" select="." />
                  </xsl:call-template>
                  </td></tr>
              </xsl:for-each>
           </table>
            </td>
          </tr>
        
        </xsl:if>
        
        <xsl:if test="./mods:physicalDescription/mods:extent">
          <tr>
            <th>Umfang:</th>
            <td>
              <table class="ir-table-docdetails-values"><tr><td>
              <xsl:for-each select="./mods:physicalDescription/mods:extent">
                <xsl:value-of select="." />
                <xsl:if test="position() != last()"> ; </xsl:if>
              </xsl:for-each>
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:location/mods:shelfLocator">
          <tr>
            <th>Signatur:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:location[./mods:shelfLocator]/mods:physicalLocation" />:
              <xsl:value-of select="./mods:location/mods:shelfLocator" />
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:originInfo[@eventType='creation']/mods:place[@supplied='yes']/mods:placeTerm">
          <tr>
            <th>Ort:</th>
            <td><table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:originInfo[@eventType='creation']/mods:place[@supplied='yes']/mods:placeTerm">
                <tr><td>
                <xsl:value-of select="." />
                </td></tr>  
              </xsl:for-each>
              </table>
            </td>
          </tr>
        </xsl:if>
          
        <!-- XSLT 2.0 is nicer: and not ends-with(@xlink:href,'/') -->
        <xsl:if test="./mods:note[@type='publisher_authority' and not(substring(@xlink:href,string-length(@xlink:href),1)='/')]">
          <tr>
            <th>Verlag:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:note[@type='publisher_authority']" />
              
              <xsl:if test="./mods:note[@type='publisher_authority']/@xlink:href">
                 <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="./mods:note[@type='publisher_authority']/@xlink:href" /></xsl:attribute>
              <xsl:element name="img">
                <xsl:attribute name="style">vertical-align:middle;padding-left:6px</xsl:attribute>
                <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/icon_gnd.png</xsl:attribute>
                <xsl:attribute name="title">Datensatz in der Gemeinsamen Normdatei der Deutschen Nationalbibliothek (GND) anzeigen</xsl:attribute>
               </xsl:element>
          </xsl:element>
              </xsl:if>
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:language/mods:languageTerm">
          <tr>
            <th>Sprache(n):</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
             <xsl:call-template name="language">
                <xsl:with-param name="term"><xsl:value-of select="./mods:language/mods:languageTerm" /></xsl:with-param>
                <xsl:with-param name="lang">de</xsl:with-param>
              </xsl:call-template>
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
       <xsl:if test="./mods:genre">
          <tr>
            <th>Genre:</th>
            <td><table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:genre">
              <tr><td>
                <xsl:value-of select="." />
              </td></tr>  
              </xsl:for-each>
              </table>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:note[@type='other']">
          <tr>
            <th>Anmerkungen:</th>
            <td><table class="ir-table-docdetails-values">
              <xsl:for-each select="./mods:note[@type='other']">
              <tr><td>
                <xsl:value-of select="." />
              </td></tr>  
              </xsl:for-each>
              </table>
            </td>
          </tr>
        </xsl:if>
        
        </table>
        <table class="table ir-table-docdetails">
        
        <xsl:if test="./mods:identifier[@type='PPN']">
          <tr>
            <th>PPN (Katalog-ID):</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:identifier[@type='PPN']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://katalog.ub.uni-rostock.de/DB=1/PPNSET?PPN=<xsl:value-of select="./mods:identifier[@type='PPN']" /></xsl:attribute>
                [OPAC]
              </a>
              &#160;
              <a>
                <xsl:attribute name="href">http://gso.gbv.de/DB=2.1/PPNSET?PPN=<xsl:value-of select="./mods:identifier[@type='PPN']" /></xsl:attribute>
                [GBV]
              </a>
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='vd18']">
          <tr>
            <th>VD18Nr:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:identifier[@type='vd18']" />
              </td></tr></table>              
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='vd17']">
          <tr>
            <th>VD17Nr:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:identifier[@type='vd17']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://gso.gbv.de/DB=1.28/CMD?ACT=SRCHA&amp;IKT=8002&amp;TRM=%27<xsl:value-of select="./mods:identifier[@type='vd17']" />%27</xsl:attribute>
                [VD17]
              </a>
             </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='kalliope']">
          <tr>
            <th>Kalliope ID:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:identifier[@type='kalliope']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://kalliope-verbund.info/<xsl:value-of select="./mods:identifier[@type='kalliope']" /></xsl:attribute>
                [Kalliope Verbundkatalog]
              </a>
             </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='vd16']">
          <tr>
            <th>VD16Nr:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:identifier[@type='vd16']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://gateway-bayern.de/VD16+<xsl:value-of select="concat(substring-before(./mods:identifier[@type='vd16'],' '),'+',substring-after(./mods:identifier[@type='vd16'],' '))" /></xsl:attribute>
                [VD16]
              </a>
             </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='urn']">
          <tr>
            <th>URN:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="./mods:identifier[@type='urn']" />
              </td></tr></table>
            </td>
          </tr>
        </xsl:if>
        
        </table>
        <table class="table ir-table-docdetails">
        
       <xsl:if test="./mods:classification[@displayLabel='doctype']">
          <tr>
            <th>Dokumententyp:</th>
            <td><table class="ir-table-docdetails-values">
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
        <xsl:if test="./mods:classification[@displayLabel='collection']">
          <tr>
            <th>Sammlung:</th>
            <td><table class="ir-table-docdetails-values">
                  <xsl:for-each select="./mods:classification[@displayLabel='collection']/@valueURI">
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
       <table class="table ir-table-docdetails">
        
        <xsl:if test="./mods:identifier[@type='purl']">
          <tr>
            <th>Persistente URL:</th>
            <td><table class="ir-table-docdetails-values"><tr><td><xsl:value-of select="./mods:identifier[@type='purl']" />
            </td></tr></table>
            </td>
        </tr>
        </xsl:if>
         <tr>
            <th>erstellt am:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="substring-before(./../../../../service/servdates/servdate[@type='createdate'],'T')" />
            </td></tr></table>
            </td>
          </tr>
          <tr>
            <th>zuletzt geändert am:</th>
            <td><table class="ir-table-docdetails-values"><tr><td>
              <xsl:value-of select="substring-before(./../../../../service/servdates/servdate[@type='modifydate'],'T')" />
            </td></tr></table>
            </td>
          </tr>
            <xsl:variable name="derID" select="/mycoreobject/structure/derobjects/derobject[@xlink:title='METS' or @xlink:title='DV_METS']/@xlink:href" />
            <xsl:if test="$derID">
              <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL"/>api/v1/objects/<xsl:value-of select="/mycoreobject/@ID" />/derivates/<xsl:value-of select="$derID" /></xsl:variable>
              <xsl:variable name="maindoc"><xsl:value-of select="document($url)/mycorederivate/derivate/internals/internal/@maindoc" /></xsl:variable>
              <xsl:variable name="derLink"><xsl:value-of select="$WebApplicationBaseURL"/>file/<xsl:value-of select="/mycoreobject/@ID" />/<xsl:value-of select="$derID" />/<xsl:value-of select="$maindoc" /></xsl:variable>
              <xsl:if test="$derLink">
                <tr class="div-technical-data" style="display:none">
                  <th>DV-METS:</th>
                  <td>
                  <table class="ir-table-docdetails-values"><tr><td>
                    <xsl:element name="a">
                      <xsl:attribute name="href"><xsl:value-of select="$derLink" /></xsl:attribute>
                      <xsl:value-of select="$maindoc" />
                    </xsl:element>
                    </td>
                    </tr>
                    </table>
                  </td>
                </tr>
               </xsl:if>
            </xsl:if>
      </table>
    </xsl:for-each>

  </xsl:template>
  
  
  
</xsl:stylesheet>
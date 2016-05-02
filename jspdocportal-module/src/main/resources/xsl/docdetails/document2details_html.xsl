<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink"
  version="1.0" exclude-result-prefixes="mods xlink">
    <xsl:import href="mods-util.xsl" />
  
  <xsl:param name="WebApplicationBaseURL" />
  <xsl:output method="html" indent="yes" standalone="no" />
  <xsl:template match="/">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
      <table class="docdetails-table" style="margin-top:20px">
        <colgroup><col style="width:150px" /></colgroup>
    
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
            <tr><td class="docdetails-values">
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
        
        <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
        
        <xsl:if test="./mods:note[@type='creator_info']">
          <tr>
            <td class="docdetails-label">Titelzusatz:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:note[@type='creator_info']" />
            </td>
          </tr>
        </xsl:if>

        <xsl:if test="./mods:name[@type='personal'][position()>1 and not(contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))]">
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
        
        <xsl:if test="./mods:physicalDescription/mods:extent">
          <tr>
            <td class="docdetails-label">Umfang:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:physicalDescription/mods:extent">
                <xsl:value-of select="." />
                <xsl:if test="position() != last()"> ; </xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:location/mods:shelfLocator">
          <tr>
            <td class="docdetails-label">Signatur:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:location[./mods:shelfLocator]/mods:physicalLocation" />:
              <xsl:value-of select="./mods:location/mods:shelfLocator" />
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:originInfo[@eventType='creation']/mods:place[@supplied='yes']/mods:placeTerm">
          <tr>
            <td class="docdetails-label">Ort:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:originInfo[@eventType='creation']/mods:place[@supplied='yes']/mods:placeTerm">
                <xsl:value-of select="." />
                <xsl:if test="position() != last()"><br /></xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
          
        <!-- XSLT 2.0 is nicer: and not ends-with(@xlink:href,'/') -->
        <xsl:if test="./mods:note[@type='publisher_authority' and not(substring(@xlink:href,string-length(@xlink:href),1)='/')]">
          <tr>
            <td class="docdetails-label">Verlag:</td>
            <td class="docdetails-values">
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
       <xsl:if test="./mods:genre">
          <tr>
            <td class="docdetails-label">Genre:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:genre">
                <xsl:value-of select="." />
                <xsl:if test="position() != last()"><br /></xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        
        <xsl:if test="./mods:note[@type='other']">
          <tr>
            <td class="docdetails-label">Anmerkungen:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:note[@type='other']">
                <xsl:value-of select="." />
                <xsl:if test="position() != last()"><br /></xsl:if>
              </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        
        <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
        
        <xsl:if test="./mods:identifier[@type='PPN']">
          <tr>
            <td class="docdetails-label">PPN (Katalog-ID):</td>
            <td class="docdetails-values">
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
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='vd18']">
          <tr>
            <td class="docdetails-label">VD18Nr:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:identifier[@type='vd18']" />              
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='vd17']">
          <tr>
            <td class="docdetails-label">VD17Nr:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:identifier[@type='vd17']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://gso.gbv.de/DB=1.28/CMD?ACT=SRCHA&amp;IKT=8002&amp;TRM=%27<xsl:value-of select="./mods:identifier[@type='vd17']" />%27</xsl:attribute>
                [VD17]
              </a>
             
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='kalliope']">
          <tr>
            <td class="docdetails-label">Kalliope ID:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:identifier[@type='kalliope']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://kalliope-verbund.info/<xsl:value-of select="./mods:identifier[@type='kalliope']" /></xsl:attribute>
                [Kalliope Verbundkatalog]
              </a>
             
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='vd16']">
          <tr>
            <td class="docdetails-label">VD16Nr:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:identifier[@type='vd16']" />
              &#160;
              <a>
                <xsl:attribute name="href">http://gateway-bayern.de/VD16+<xsl:value-of select="concat(substring-before(./mods:identifier[@type='vd16'],' '),'+',substring-after(./mods:identifier[@type='vd16'],' '))" /></xsl:attribute>
                [VD16]
              </a>
             
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:identifier[@type='urn']">
          <tr>
            <td class="docdetails-label">URN:</td>
            <td class="docdetails-values">
              <xsl:value-of select="./mods:identifier[@type='urn']" />
            </td>
          </tr>
        </xsl:if>
        
              
        
        <tr><td class="docdetails-separator" colspan="2"><hr /></td></tr>
        
       <xsl:if test="./mods:classification[@authorityURI='http://rosdok.uni-rostock.de/classifications/rosdok_class_doctypes']">
          <tr>
            <td class="docdetails-label">Dokumententyp:</td>
            <td class="docdetails-values">
              <xsl:for-each select="./mods:classification[@authorityURI='http://rosdok.uni-rostock.de/classifications/rosdok_class_doctypes']/@valueURI">
               <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
               </xsl:call-template>
               <br /> 
               </xsl:for-each>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="./mods:classification[@authorityURI='http://rosdok.uni-rostock.de/classifications/rosdok_class_collections']">
          <tr>
            <td class="docdetails-label">Sammlung:</td>
            <td class="docdetails-values">
                  <xsl:for-each select="./mods:classification[@authorityURI='http://rosdok.uni-rostock.de/classifications/rosdok_class_collections']/@valueURI">
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
            <xsl:variable name="derID" select="/mycoreobject/structure/derobjects/derobject[@xlink:title='METS' or @xlink:title='DV_METS']/@xlink:href" />
            <xsl:if test="$derID">
              <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL"/>api/v1/objects/<xsl:value-of select="/mycoreobject/@ID" />/derivates/<xsl:value-of select="$derID" /></xsl:variable>
              <xsl:variable name="maindoc"><xsl:value-of select="document($url)/mycorederivate/derivate/internals/internal/@maindoc" /></xsl:variable>
              <xsl:variable name="derLink"><xsl:value-of select="$WebApplicationBaseURL"/>file/<xsl:value-of select="/mycoreobject/@ID" />/<xsl:value-of select="$derID" />/<xsl:value-of select="$maindoc" /></xsl:variable>
              <xsl:if test="$derLink">
                <tr class="div-technical-data" style="display:none">
                  <td class="docdetails-label">DV-METS:</td>
                  <td class="docdetails-values">
                    <xsl:element name="a">
                      <xsl:attribute name="href"><xsl:value-of select="$derLink" /></xsl:attribute>
                      <xsl:value-of select="$maindoc" />
                    </xsl:element>
                  </td>
                </tr>
               </xsl:if>
            </xsl:if>
      </table>
    </xsl:for-each>

  </xsl:template>
  
  
  
</xsl:stylesheet>
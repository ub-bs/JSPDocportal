<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:p="info:srw/schema/5/picaXML-v1.0"
  xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="p">
  <xsl:output method="xml" indent="yes" />
  <xsl:variable name="ubr_pica2mods_version">UB Rostock: Pica2MODS v0.9_20131220</xsl:variable>
  <xsl:template match="/p:record">
    <mods:mods xmlns:mods="http://www.loc.gov/mods/v3" version="3.5" >
    <!-- <mods:mods xmlns:mods="http://www.loc.gov/mods/v3" version="3.5" xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-5.xsd"> -->
      <mods:recordInfo>
           <mods:recordIdentifier source="DE-28">rosdok/ppn<xsl:value-of select="./p:datafield[@tag='003@']/p:subfield[@code='0']" /></mods:recordIdentifier>
      </mods:recordInfo>
            <xsl:for-each select="./p:datafield[@tag='009P' and @occurrence='03']"> <!-- 4083 (kein eigenes Feld) -->
          <xsl:if test="contains(./p:subfield[@code='a'], '//purl.')">
            <mods:identifier type="purl"><xsl:value-of select="./p:subfield[@code='a']" /></mods:identifier>
          </xsl:if>          
      </xsl:for-each>
      
      <xsl:for-each select="./p:datafield[@tag='003@']"> <!--  0100 -->
          <mods:identifier type="PPN"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each> 
      <xsl:for-each select="./p:datafield[@tag='004U']"> <!-- 2050 -->
          <mods:identifier type="urn"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each> 
      <xsl:for-each select="./p:datafield[@tag='004V']"> <!-- 2051 -->
          <mods:identifier type="doi"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each> 
      <xsl:for-each select="./p:datafield[@tag='004R']"> <!-- 2052 -->
          <mods:identifier type="hdl"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each>
      <xsl:for-each select="./p:datafield[@tag='006W']"> <!--  2191 -->
          <mods:identifier type="vd17"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each>
      <!-- VD17 nur nicht in 2191, sondern als bibliogr. Zitat in 2277 -->
      <xsl:if test="not(./p:datafield[@tag='006W'])">
      	<xsl:for-each select="./p:datafield[@tag='007S' and starts-with(./p:subfield[@code='0'], 'VD17')]">
      	 	<mods:identifier type="vd17"><xsl:value-of select="normalize-space(substring-after(./p:subfield[@code='0'], 'VD17'))" /></mods:identifier>          
     	 </xsl:for-each>
      </xsl:if>
      
      <xsl:for-each select="./p:datafield[@tag='006M']"> <!--  2192 -->
          <mods:identifier type="vd18"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each>
      <xsl:for-each select="./p:datafield[@tag='006Z']"> <!--  2110 -->
          <mods:identifier type="zdb"><xsl:value-of select="./p:subfield[@code='0']" /></mods:identifier>          
      </xsl:for-each>       
      <xsl:for-each select="./p:datafield[@tag='007S']"><!-- 2277 -->
        <xsl:if test="starts-with(./p:subfield[@code='0'], 'VD 16') or starts-with(./p:subfield[@code='0'], 'VD16')">
          <mods:identifier type="vd16"><xsl:value-of select="normalize-space(substring-after(./p:subfield[@code='0'], '16'))" /></mods:identifier>
        </xsl:if>
        <xsl:if test="starts-with(./p:subfield[@code='0'], 'RISM')">
          <mods:identifier type="rism"><xsl:value-of select="normalize-space(substring-after(./p:subfield[@code='0'], 'RISM'))" /></mods:identifier>
        </xsl:if>
        <xsl:if test="starts-with(./p:subfield[@code='0'], 'Kalliope')">
          <mods:identifier type="kalliope"><xsl:value-of select="normalize-space(substring-after(./p:subfield[@code='0'], 'Kalliope'))" /></mods:identifier>
        </xsl:if>
      </xsl:for-each>
            
      <xsl:for-each select="./p:datafield[@tag='028A' or @tag='028B']"> <!-- 300x -->
        <xsl:call-template name="PersonalName">
          <xsl:with-param name="marcrelatorCode">aut</xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
      
      <xsl:for-each select="./p:datafield[@tag='028C' or @tag='028D' or @tag='028E'or @tag='028F'or @tag='028G'or @tag='028H'or @tag='028L'or @tag='028M']"> <!-- 300x -->
        <xsl:call-template name="PersonalName" />
      </xsl:for-each>
      
      <xsl:for-each select="./p:datafield[@tag='029A' or @tag='029F' or @tag='029G' or @tag='029E']"> <!-- 310X -->
        <xsl:call-template name="CorporateName">
          
        </xsl:call-template>
      </xsl:for-each>
      
      
      <xsl:choose>
         <xsl:when test="substring(./p:datafield[@tag='002@']/p:subfield[@code='0'],2,1)='f' or substring(./p:datafield[@tag='002@']/p:subfield[@code='0'],2,1)='F' ">
           <xsl:for-each select="./p:datafield[@tag='036C']"><!-- 4150 -->
              <xsl:call-template name="Title" />
           </xsl:for-each>  
        </xsl:when>
        <xsl:when test="substring(./p:datafield[@tag='002@']/p:subfield[@code='0'],2,1)='v' and ./p:datafield[@tag='027D']">
           <xsl:for-each select="./p:datafield[@tag='027D']"><!-- 3290 -->
              <xsl:call-template name="Title" />
           </xsl:for-each>  
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="./p:datafield[@tag='021A']"> <!--  4000 -->
              <xsl:call-template name="Title" />
           </xsl:for-each>  
        </xsl:otherwise>
      </xsl:choose>
      
      
      
      <!--  Titel fingiert, wenn kein Titel in 4000 --> 
      

      <xsl:for-each select="./p:datafield[@tag='010@']"> <!-- 1500 Language -->
      <!-- weiter Unterfelder für Orginaltext / Zwischenübersetzung nicht abbildbar -->
        <xsl:for-each select="./p:subfield[@code='a']">
          <mods:language>
             <mods:languageTerm type="code" authority="iso639-2b"><xsl:value-of select="." /></mods:languageTerm>
          </mods:language>
        </xsl:for-each>
      </xsl:for-each>
      
      <xsl:for-each select="./p:datafield[@tag='039B']"> <!-- 4241  übergeordnetes Werk-->
        <xsl:call-template name="ArticleParent" />
      </xsl:for-each>
    
      <xsl:for-each select="./p:datafield[@tag='036D']"> <!-- 4160  übergeordnetes Werk-->
        <xsl:call-template name="HostOrSeries">
           <xsl:with-param name="type">host</xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>

      <xsl:for-each select="./p:datafield[@tag='036F']"> <!-- 4180  Schriftenreihe-->
          <xsl:call-template name="HostOrSeries">
           <xsl:with-param name="type">series</xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>
      
      <xsl:for-each select="./p:datafield[@tag='039P']"> <!-- 4261  RezensiertesWerk-->
          <xsl:call-template name="Review" />
      </xsl:for-each>

      <!--033J =  4033 Druckernormadaten, aber kein Ort angegeben (müsste aus GND gelesen werden)
      MODS unterstützt keine authorityURIs für Verlage
      deshalb 033A verwenden -->

      <!-- check use of eventtype attribute -->
          <mods:originInfo eventType="creation">
              <xsl:for-each select="./p:datafield[@tag='033A']">
             <xsl:if test="./p:subfield[@code='n']">  <!-- 4030 Ort, Verlag -->
                 <mods:publisher><xsl:value-of select="./p:subfield[@code='n']" /></mods:publisher>
             </xsl:if>
              <xsl:for-each select="./p:subfield[@code='p']">
                  <mods:place><mods:placeTerm type="text"><xsl:value-of select="." /></mods:placeTerm></mods:place>
                </xsl:for-each>
            
              </xsl:for-each>
              
            <xsl:for-each select="./p:datafield[@tag='011@']">
              <xsl:choose>
                 <xsl:when test="./p:subfield[@code='b']">
                  <mods:dateIssued keyDate="yes" encoding="iso8601" point="start"><xsl:value-of select="./p:subfield[@code='a']" /></mods:dateIssued>
                  <mods:dateIssued encoding="iso8601" point="end"><xsl:value-of select="./p:subfield[@code='b']" /></mods:dateIssued>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:choose>
                        <xsl:when test="contains(./p:subfield[@code='a'], 'X')">
                            <mods:dateCreated keyDate="yes" encoding="iso8601" point="start"><xsl:value-of select="translate(./p:subfield[@code='a'], 'X','0')" /></mods:dateCreated>
                            <mods:dateCreated encoding="iso8601" point="end"><xsl:value-of select="translate(./p:subfield[@code='a'], 'X', '9')" /></mods:dateCreated>
                            <mods:dateCreated qualifier="approximate">
                              <xsl:value-of select="./p:subfield[@code='a']"></xsl:value-of>
                              <xsl:if test="./p:subfield[@code='n']">
                                  <xsl:text> </xsl:text><xsl:value-of select="./p:subfield[@code='n']" />
                              </xsl:if>
                              </mods:dateCreated>
                        </xsl:when>
                        <xsl:otherwise>
                            <mods:dateIssued keyDate="yes" encoding="iso8601"><xsl:value-of select="./p:subfield[@code='a']" /></mods:dateIssued>
                        </xsl:otherwise>
                      </xsl:choose>
                  </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
            <xsl:for-each select="./p:datafield[@tag='032@']"> <!-- 4020 Ausgabe-->
              <xsl:choose>
                 <xsl:when test="./p:subfield[@code='c']">
                   <mods:edition><xsl:value-of select="./p:subfield[@code='a']" /> / <xsl:value-of select="./p:subfield[@code='c']" /></mods:edition>
                  </xsl:when>
                  <xsl:otherwise>
                      <mods:edition><xsl:value-of select="./p:subfield[@code='a']" /></mods:edition>
                  </xsl:otherwise>   
              </xsl:choose>
            </xsl:for-each>
            
            <xsl:for-each select="./p:datafield[@tag='002@']">
              <xsl:choose>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='a'"><mods:issuance>monographic</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='b'"><mods:issuance>serial</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='c'"><mods:issuance>multipart monograph</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='d'"><mods:issuance>serial</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='f'"><mods:issuance>monographic</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='F'"><mods:issuance>monographic</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='j'"><mods:issuance>single unit</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='s'"><mods:issuance>single unit</mods:issuance></xsl:when>
                 <xsl:when test="substring(./p:subfield[@code='0'],2,1)='v'"><mods:issuance>monographic</mods:issuance></xsl:when>                     
              </xsl:choose>              
            </xsl:for-each>
            <xsl:for-each select="./p:datafield[@tag='031@']/p:subfield[@code='a']">
              <mods:frequency><xsl:value-of select="." /></mods:frequency>
            </xsl:for-each>

            <!-- normierte Orte -->
            <xsl:for-each select="./p:datafield[@tag='033B' and @occurrence='03']/p:subfield[@code='p']">
              <mods:place supplied="yes"><mods:placeTerm lang="ger" type="text"><xsl:value-of select="." /></mods:placeTerm></mods:place>
            </xsl:for-each>
            
          </mods:originInfo>
            <!--033J =  4033 Druckernormadaten -->
         <xsl:for-each select="./p:datafield[@tag='033J' and ./p:subfield[@code='0']]">
            <mods:note type="publisher_authority">
              <xsl:attribute name="xlink:href">http://d-nb.info/<xsl:value-of select="./p:subfield[@code='0']" /></xsl:attribute>
              <xsl:value-of select="./p:subfield[@code='a']"></xsl:value-of>
              <xsl:if test="./p:subfield[@code='d']">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="./p:subfield[@code='d']"></xsl:value-of>
              </xsl:if>
            </mods:note>
         </xsl:for-each>
         
         <mods:originInfo eventType="online_publication"> <!-- 4031 -->
              <xsl:if test="./p:datafield[@tag='033B' and @occurrence='01']/p:subfield[@code='n']">  <!-- 4030 Ort, Verlag -->
                 <mods:publisher><xsl:value-of select="./p:datafield[@tag='033B' and @occurrence='01']/p:subfield[@code='n']" /></mods:publisher>
             </xsl:if>
             <xsl:if test="./p:datafield[@tag='033B' and @occurrence='01']/p:subfield[@code='p']">  <!-- 4030 Ort, Verlag -->
                <mods:place><mods:placeTerm type="text"><xsl:value-of select="./p:datafield[@tag='033B' and @occurrence='01']/p:subfield[@code='p']" /></mods:placeTerm></mods:place>
            </xsl:if>
            <mods:edition>[Electronic ed.]</mods:edition>
         
             <xsl:for-each select="./p:datafield[@tag='011B']">   <!-- 1109 -->
              <xsl:choose>
                 <xsl:when test="./p:subfield[@code='b']">
                  <mods:dateCaptured encoding="iso8601" point="start"><xsl:value-of select="./p:subfield[@code='a']" /></mods:dateCaptured>
                  <mods:dateCaptured encoding="iso8601" point="end"><xsl:value-of select="./p:subfield[@code='b']" /></mods:dateCaptured>
                  </xsl:when>
                  <xsl:otherwise>
                      <mods:dateCaptured encoding="iso8601"><xsl:value-of select="./p:subfield[@code='a']" /></mods:dateCaptured>
                    </xsl:otherwise>
                  </xsl:choose>
              </xsl:for-each>
         
         </mods:originInfo>
         
         <xsl:for-each select="./p:datafield[@tag='009A']"> <!-- 4065 Besitznachweis der Vorlage-->
           <mods:location>
              <xsl:if test="./p:subfield[@code='c']">
                <xsl:choose>
                 <xsl:when test="./p:subfield[@code='c']='UB Rostock'">
                   <mods:physicalLocation type="current" authorityURI="http://d-nb.info/gnd/" valueURI="http://d-nb.info/gnd/25968-8">Universitätsbibliothek Rostock</mods:physicalLocation>
                  </xsl:when>
                  <xsl:otherwise>
                      <mods:physicalLocation type="current"><xsl:value-of select="./p:subfield[@code='c']" /></mods:physicalLocation>
                  </xsl:otherwise>   
                </xsl:choose>
             </xsl:if>
             <xsl:if test="./p:subfield[@code='a']">
                <mods:shelfLocator><xsl:value-of select="./p:subfield[@code='a']" /></mods:shelfLocator>
             </xsl:if>
           </mods:location>
        </xsl:for-each>

         <xsl:variable name="digitalOrigin">
            <xsl:for-each select="./p:datafield[@tag='037H']/p:subfield[@code='a']">   <!-- 4238 Technische Angaben zum elektr. Dokument  -->
               <xsl:if test="contains(., 'Digitalisierungsvorlage: Original')">
                  <mods:digitalOrigin>reformatted digital</mods:digitalOrigin>
               </xsl:if>
              <xsl:if test="contains(., 'Digitalisierungsvorlage: Mikrofilm')">
                  <mods:digitalOrigin>digitized microfilm</mods:digitalOrigin>
               </xsl:if>
          </xsl:for-each>
         </xsl:variable>
        <xsl:if test="$digitalOrigin or ./p:datafield[@tag='034D'  or @tag='034M' or @tag='034I' or @tag='034K']">        
        <mods:physicalDescription>
           <xsl:for-each select="./p:datafield[@tag='034D']/p:subfield[@code='a']">   <!--  4060 Umfang, Seiten -->
            <mods:extent><xsl:value-of select="." /></mods:extent>
          </xsl:for-each>
          <xsl:for-each select="./p:datafield[@tag='034M']/p:subfield[@code='a']">   <!--  4061 Illustrationen -->
              <mods:extent><xsl:value-of select="." /></mods:extent>
          </xsl:for-each>
          <xsl:for-each select="./p:datafield[@tag='034I']/p:subfield[@code='a']">   <!-- 4062 Format, Größe  -->
              <mods:extent><xsl:value-of select="." /></mods:extent>
          </xsl:for-each>
          <xsl:for-each select="./p:datafield[@tag='034K']/p:subfield[@code='a']">   <!-- 4063 Begleitmaterial  -->
              <mods:extent><xsl:value-of select="." /></mods:extent>
          </xsl:for-each>
          <xsl:copy-of select="$digitalOrigin" />
        </mods:physicalDescription>
        </xsl:if>
        
                     
        <xsl:for-each select="./p:datafield[@tag='009P' and contains(./p:subfield[@code='a'], 'rosdok')][1]">
          <mods:location>
                <mods:physicalLocation type="online" authorityURI="http://d-nb.info/gnd/" valueURI="http://d-nb.info/gnd/25968-8">Universitätsbibliothek Rostock</mods:physicalLocation>
                <mods:url usage="primary" access="object in context"><xsl:value-of select="./p:subfield[@code='a']" /></mods:url>
         </mods:location>              
        </xsl:for-each>
        
        
         
         <xsl:for-each select="./p:datafield[@tag='044S']"> <!-- 5570 Gattungsbegriffe AAD -->
          <mods:genre type="aadgenre"><xsl:value-of select="./p:subfield[@code='a']"/></mods:genre>
          <xsl:call-template name="UBR_Class_AADGenres" />
         </xsl:for-each>     
         
         <xsl:call-template name="UBR_Class_Collection" />
         <xsl:call-template name="UBR_Class_Doctype" />

        <xsl:for-each select="./p:datafield[@tag='009P' and @occurrence='09']">
          <mods:note>
            <xsl:attribute name="xlink:href"><xsl:value-of select="./p:subfield[@code='a']" /></xsl:attribute>
            <xsl:value-of select="./p:subfield[@code='y']" />
          </mods:note>
        </xsl:for-each>
    
        <xsl:for-each select="./p:datafield[@tag='007S']"><!-- 2277 -->
        <xsl:if test="not(starts-with(./p:subfield[@code='0'], 'VD 16')) and not(starts-with(./p:subfield[@code='0'], 'VD16')) and not(starts-with(./p:subfield[@code='0'], 'VD17')) and not(starts-with(./p:subfield[@code='0'], 'RISM')) and not(starts-with(./p:subfield[@code='0'], 'Kalliope')) and not(./p:subfield[@code='S']='e')">
          <mods:note type="bibliographic_reference"><xsl:value-of select="./p:subfield[@code='0']" /></mods:note>
        </xsl:if>
      </xsl:for-each>
      
      
      <xsl:for-each select="./p:datafield[@tag='037A' or @tag='037B' or @tag='046L' or @tag='046F' or @tag='046G' or @tag='046H' or @tag='046I']"><!-- 4201, 4202, 4221, 4215, 4216, 4217, 4218 -->
          <mods:note type="other"><xsl:value-of select="./p:subfield[@code='a']" /></mods:note>
      </xsl:for-each>
      
      <xsl:for-each select="./p:datafield[@tag='047C']"><!-- 4200 -->
          <mods:note type="titlewordindex"><xsl:value-of select="./p:subfield[@code='a']" /></mods:note>
      </xsl:for-each>
      
	<!-- 
      <mods:extension displayLabel="picaxml">
        <xsl:copy-of select="." />
      </mods:extension>
	-->
    </mods:mods>
  </xsl:template>
  <xsl:template name="HostOrSeries">
    <xsl:param name="type" />
          <mods:relatedItem>
          <!--  ToDo teilweise redundant mit title template -->
           <xsl:attribute name="type"><xsl:value-of select="$type" /></xsl:attribute>
           <mods:titleInfo>
           <xsl:if test="./p:subfield[@code='a']">
              <xsl:variable name="mainTitle" select="./p:subfield[@code='a']" />
               <xsl:choose>
          <xsl:when test="contains($mainTitle, '@')">
            <xsl:variable name="nonSort" select="normalize-space(substring-before($mainTitle, '@'))" />
            <xsl:choose>
              <xsl:when test="string-length(nonSort) &lt; 9">
                <mods:nonSort><xsl:value-of select="$nonSort" /></mods:nonSort>
                <mods:title>
                  <xsl:value-of select="substring-after($mainTitle, '@')"  />
                </mods:title>
              </xsl:when>
              <xsl:otherwise>
                <mods:title><xsl:value-of select="$mainTitle" /></mods:title>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <mods:title><xsl:value-of select="$mainTitle"/></mods:title>
          </xsl:otherwise>
        </xsl:choose>
           </xsl:if>
          </mods:titleInfo>
           <xsl:if test="./p:subfield[@code='9']">
           <xsl:if test="not($type = 'series')">
              <mods:recordInfo><mods:recordIdentifier source="DE-28">rosdok/ppn<xsl:value-of select="./p:subfield[@code='9']" /></mods:recordIdentifier></mods:recordInfo>
              <mods:identifier type="purl">http://purl.uni-rostock.de/rosdok/ppn<xsl:value-of select="./p:subfield[@code='9']" /></mods:identifier>
            </xsl:if>
            <xsl:if test="$type = 'series'">
              <mods:identifier type="gvk:ppn"><xsl:value-of select="./p:subfield[@code='9']" /></mods:identifier>
            </xsl:if>
          </xsl:if>
           
            <mods:part>
              <!-- order attributefrom subfield $X - without check
              <xsl:if test="./p:subfield[@code='X']">
                <xsl:attribute name="order">
                  <xsl:choose>
                    <xsl:when test="contains(./p:subfield[@code='X'], ',')">
                        <xsl:value-of select="substring-before(substring-before(./p:subfield[@code='X'], '.'), ',')" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="substring-before(./p:subfield[@code='X'], '.')" />
                    </xsl:otherwise>
                 </xsl:choose>   
                 </xsl:attribute>
              </xsl:if>
 			  -->

				<!-- set order attribute only if value of subfield $X is a number --> 
				<xsl:if test="./p:subfield[@code='X']">
                  <xsl:choose>
                    <xsl:when test="contains(./p:subfield[@code='X'], ',')">
                	    <xsl:if test="number(substring-before(substring-before(./p:subfield[@code='X'], '.'), ','))">
    						<xsl:attribute name="order">		
                        		<xsl:value-of select="substring-before(substring-before(./p:subfield[@code='X'], '.'), ',')" />
							</xsl:attribute>
						</xsl:if>			
                    </xsl:when>
                    <xsl:otherwise>
                    	<xsl:if test="number(substring-before(./p:subfield[@code='X'], '.'))">
                    		<xsl:attribute name="order">
                        		<xsl:value-of select="substring-before(./p:subfield[@code='X'], '.')" />
                        	</xsl:attribute>
                        </xsl:if>
                    </xsl:otherwise>
                 </xsl:choose>   
              </xsl:if>
              
              <!-- ToDo:  type attribute: issue, volume, chapter, .... --> 
              <xsl:if test="./p:subfield[@code='l']">
                <mods:detail type="volume"><mods:number><xsl:value-of select="./p:subfield[@code='l']" /></mods:number></mods:detail>
              </xsl:if>
              <xsl:if test="./p:subfield[@code='x']">
                  <mods:text type="sortstring"><xsl:value-of select="./p:subfield[@code='x']" /></mods:text>
              </xsl:if>
            </mods:part>
          
        </mods:relatedItem>
  </xsl:template>

    <xsl:template name="ArticleParent">
      <mods:relatedItem>
          <!--  ToDo teilweise redundant mit title template -->
           <xsl:attribute name="type">host</xsl:attribute>
           <xsl:attribute name="displayLabel">appears_in</xsl:attribute>
           <mods:titleInfo>
           <xsl:if test="./p:subfield[@code='a']">
              <xsl:variable name="mainTitle" select="./p:subfield[@code='a']" />
               <xsl:choose>
          <xsl:when test="contains($mainTitle, '@')">
            <xsl:variable name="nonSort" select="normalize-space(substring-before($mainTitle, '@'))" />
            <xsl:choose>
              <xsl:when test="string-length(nonSort) &lt; 9">
                <mods:nonSort><xsl:value-of select="$nonSort" /></mods:nonSort>
                <mods:title>
                  <xsl:value-of select="substring-after($mainTitle, '@')"  />
                </mods:title>
              </xsl:when>
              <xsl:otherwise>
                <mods:title><xsl:value-of select="$mainTitle" /></mods:title>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <mods:title><xsl:value-of select="$mainTitle"/></mods:title>
          </xsl:otherwise>
        </xsl:choose>
           </xsl:if>
           <xsl:if test="./p:subfield[@code='d']">
            <mods:subTitle><xsl:value-of select="./p:subfield[@code='d']"/></mods:subTitle>
           </xsl:if>
           
           
          </mods:titleInfo>
           
            <mods:part>
               <xsl:if test="./p:subfield[@code='x']">
                <xsl:attribute name="order">
                    <xsl:value-of select="substring(./p:subfield[@code='x'],1,4)" />   
                 </xsl:attribute>
              </xsl:if>
              <xsl:if test="./p:subfield[@code='x']">
                  <mods:text type="sortstring"><xsl:value-of select="./p:subfield[@code='x']" /></mods:text>
              </xsl:if>
              <xsl:for-each select="./../p:datafield[@tag='031A']" > <!-- 4070 -->
                 <!-- Volume -->
                 <xsl:if test="./p:subfield[@code='d']">
                  <mods:detail type="volume"><mods:number><xsl:value-of select="./p:subfield[@code='d']" /></mods:number></mods:detail>
                 </xsl:if>
                 <!-- Issue -->
                 <xsl:if test="./p:subfield[@code='e']">
                  <mods:detail type="issue"><mods:number><xsl:value-of select="./p:subfield[@code='e']" /></mods:number></mods:detail>
                 </xsl:if>
                 
                 
                 
                  <!-- Seitenzahlen zu Pica to MODS -->
                  <xsl:if test="./p:subfield[@code='h' or @code='g']">
                    <mods:extent unit="page">
                      <xsl:if test="./p:subfield[@code='g']">
                          <mods:total><xsl:value-of select="./p:subfield[@code='g']" /></mods:total>                         
                      </xsl:if>
                      <xsl:if test="./p:subfield[@code='h']">
                        <xsl:if test="not (contains(./p:subfield[@code='h'], ','))">
                           <xsl:if test="not (contains(./p:subfield[@code='h'], '-'))">
                              <mods:start><xsl:value-of select="./p:subfield[@code='h']" /></mods:start>
                           </xsl:if> 
                           <xsl:if test="contains(./p:subfield[@code='h'], '-')">
                              <mods:start><xsl:value-of select="substring-before(./p:subfield[@code='h'], '-')" /></mods:start>
                              <mods:end><xsl:value-of select="substring-after(./p:subfield[@code='h'], '-')" /></mods:end>
                           </xsl:if>
                        </xsl:if>
                        <xsl:if test="contains(./p:subfield[@code='h'], ',')">
                          <mods:list><xsl:value-of select="./p:subfield[@code='h']" /></mods:list>
                        </xsl:if>
                      </xsl:if>
                    </mods:extent>
                  </xsl:if>
                 <!-- Date -->
                 <xsl:if test="./p:subfield[@code='j']">
                  <mods:date encoding="iso8601"><xsl:value-of select="substring(./p:subfield[@code='j'],1,4)" /></mods:date>
                 </xsl:if>
                 <xsl:if test="./p:subfield[@code='y']">
                  <mods:text type="display"><xsl:value-of select="substring(./p:subfield[@code='y'],1,4)" /></mods:text>
                 </xsl:if>                  
                 
                 <xsl:for-each select="./../p:datafield[@tag='031C']"> <!-- 4072 -->
                    <mods:text type="article series"><xsl:value-of select="./p:subfield[@code='a']" /></mods:text>
                  </xsl:for-each>          
              </xsl:for-each>
            </mods:part>
          
        </mods:relatedItem>
  </xsl:template>
  
  <xsl:template name="Review">
      <mods:relatedItem type="reviewOf">
           <mods:titleInfo>
           <xsl:if test="./p:subfield[@code='a']">
              <xsl:variable name="mainTitle" select="./p:subfield[@code='a']" />
               <xsl:choose>
          <xsl:when test="contains($mainTitle, '@')">
            <xsl:variable name="nonSort" select="normalize-space(substring-before($mainTitle, '@'))" />
            <xsl:choose>
              <xsl:when test="string-length(nonSort) &lt; 9">
                <mods:nonSort><xsl:value-of select="$nonSort" /></mods:nonSort>
                <mods:title>
                  <xsl:value-of select="substring-after($mainTitle, '@')"  />
                </mods:title>
              </xsl:when>
              <xsl:otherwise>
                <mods:title><xsl:value-of select="$mainTitle" /></mods:title>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <mods:title><xsl:value-of select="$mainTitle"/></mods:title>
          </xsl:otherwise>
        </xsl:choose>
           </xsl:if>
          </mods:titleInfo>
          <mods:identifier type="PPN"><xsl:value-of select="./p:subfield[@code='9']"/></mods:identifier>
    
  </mods:relatedItem>
  </xsl:template>
  
  <xsl:template name="Title">
    <mods:titleInfo>
      <xsl:if test="./p:subfield[@code='a']">
        <xsl:variable name="mainTitle" select="./p:subfield[@code='a']" />
        <xsl:choose>
          <xsl:when test="contains($mainTitle, '@')">
            <xsl:variable name="nonSort" select="normalize-space(substring-before($mainTitle, '@'))" />
            <xsl:choose>
              <xsl:when test="string-length(nonSort) &lt; 9">
                <mods:nonSort><xsl:value-of select="$nonSort" /></mods:nonSort>
                <mods:title>
                  <xsl:value-of select="substring-after($mainTitle, '@')"  />
                </mods:title>
              </xsl:when>
              <xsl:otherwise>
                <mods:title><xsl:value-of select="$mainTitle" /></mods:title>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <mods:title><xsl:value-of select="$mainTitle"/></mods:title>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='d']">
        <mods:subTitle><xsl:value-of select="./p:subfield[@code='d']" /></mods:subTitle>
      </xsl:if>
      
      <!--  nur in fingierten Titel 036C / 4150 -->
      <xsl:if test="./p:subfield[@code='y']">
        <mods:subTitle><xsl:value-of select="./p:subfield[@code='y']" /></mods:subTitle>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='l']">
        <mods:partNumber><xsl:value-of select="./p:subfield[@code='l']" /></mods:partNumber>
      </xsl:if>
      
      <xsl:if test="@tag='027D'">
        <mods:partNumber><xsl:value-of select="./../p:datafield[@tag='036F']/p:subfield[@code='l']" /></mods:partNumber>
      </xsl:if>

       <xsl:if test="@tag='036C' and ./../p:datafield[@tag='021A']">
       		<xsl:if test="./../p:datafield[@tag='021A']/p:subfield[@code='a'] != '@'">
            	<mods:partName><xsl:value-of select="translate(./../p:datafield[@tag='021A']/p:subfield[@code='a'], '@', '')" /></mods:partName>
            </xsl:if>
       </xsl:if>
    </mods:titleInfo>

    <xsl:if test="./../p:datafield[@tag='021A']/p:subfield[@code='h']">
      <mods:note type="creator_info"><xsl:value-of select="./../p:datafield[@tag='021A']/p:subfield[@code='h']" /></mods:note>
    </xsl:if>
  </xsl:template>

  <xsl:template name="PersonalName">
    <xsl:param name="marcrelatorCode" />
    <mods:name type="personal">
      <xsl:if test="./p:subfield[@code='0']">
        <xsl:attribute name="authority">gnd</xsl:attribute>
        <xsl:attribute name="authorityURI">http://d-nb.info/gnd/</xsl:attribute>
        <xsl:attribute name="valueURI">http://d-nb.info/<xsl:value-of select="./p:subfield[@code='0']" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='a']">
        <mods:namePart type="family">
          <xsl:value-of select="./p:subfield[@code='a']" />
        </mods:namePart>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='d']">
        <mods:namePart type="given">
          <xsl:value-of select="./p:subfield[@code='d']" />
        </mods:namePart>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='P']">
        <mods:namePart>
          <xsl:value-of select="./p:subfield[@code='P']" />
        </mods:namePart>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='n']">
        <mods:namePart type="termsOfAddress">
          <xsl:value-of select="./p:subfield[@code='n']" />
        </mods:namePart>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='l']">
        <mods:namePart type="termsOfAddress">
          <xsl:value-of select="./p:subfield[@code='l']" />
        </mods:namePart>
      </xsl:if>
      
      <xsl:if test="./p:subfield[@code='E'] or ./p:subfield[@code='F']">
        <mods:namePart type="date">
          <xsl:value-of select="./p:subfield[@code='E']" />-<xsl:value-of select="./p:subfield[@code='F']" />
        </mods:namePart>
      </xsl:if>
          <xsl:choose>
            <xsl:when test="./p:subfield[@code='B']">
              <mods:role><mods:roleTerm type="code" authority="marcrelator">
              
              <xsl:choose> 
                <!-- RAK WB §185, 2 -->
                <xsl:when test="./p:subfield[@code='B']='Bearb.'">ctb</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Begr.'">org</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Hrsg.'">edt</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Ill.'">ill</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Komp.'">cmp</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Mitarb.'">ctb</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Red.'">red</xsl:when>
                <!-- GBV Katalogisierungsrichtlinie -->
                <xsl:when test="./p:subfield[@code='B']='Adressat'">rcp</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='angebl. Hrsg.'">edt</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='mutmaßl. Hrsg.'">edt</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Komm.'">ann</xsl:when><!-- Kommentator = annotator -->
                <xsl:when test="./p:subfield[@code='B']='Stecher'">egr</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='angebl. Übers.'">trl</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='mutmaßl. Übers.'">trl</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='angebl. Verf.'">dub</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='mutmaßl. Verf.'">dub</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Verstorb.'">oth</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Zeichner'">drm</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Präses'">pra</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Resp.'">rsp</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Widmungsempfänger'">dto</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Zensor'">cns</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Beiträger'">ctb</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Beiträger k.'">ctb</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Beiträger m.'">ctb</xsl:when>
                <xsl:when test="./p:subfield[@code='B']='Interpr.'">prf</xsl:when> <!-- Interpret = Performer-->               
                <xsl:otherwise>oth</xsl:otherwise>
                </xsl:choose>
                </mods:roleTerm>
                   <mods:roleTerm type="text" authority="gbv">[<xsl:value-of select="./p:subfield[@code='B']" />]</mods:roleTerm>
                </mods:role>
            </xsl:when>
            <xsl:otherwise>
            	<xsl:choose>
              		<xsl:when test="$marcrelatorCode">
                		<mods:role><mods:roleTerm type="code" authority="marcrelator"><xsl:value-of select="$marcrelatorCode" /></mods:roleTerm></mods:role>
              		</xsl:when>
              		<xsl:otherwise>
              			<mods:role><mods:roleTerm type="code" authority="marcrelator">oth</mods:roleTerm></mods:role>
              		</xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
    </mods:name>
  </xsl:template>
  <xsl:template name="CorporateName">
    <xsl:param name="marcrelatorCode" />
    <mods:name type="corporate">
      <xsl:if test="./p:subfield[@code='0']">
        <xsl:attribute name="authority">gnd</xsl:attribute>
        <xsl:attribute name="authorityURI">http://d-nb.info/gnd/</xsl:attribute>
        <xsl:attribute name="valueURI">http://d-nb.info/<xsl:value-of select="./p:subfield[@code='0']" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='a']">
      <xsl:variable name="mainTitle" select="./p:subfield[@code='a']" />
        <xsl:choose>
          <xsl:when test="contains($mainTitle, '@')">
            <xsl:variable name="nonSort" select="normalize-space(substring-before($mainTitle, '@'))" />
            <xsl:choose>
              <xsl:when test="string-length(nonSort) &lt; 9">
                <mods:namePart><xsl:value-of select="normalize-space(substring-before($mainTitle, '@'))"/> <xsl:value-of select="normalize-space(substring-after($mainTitle, '@'))"/></mods:namePart>
              </xsl:when>
              <xsl:otherwise>
                <mods:namePart><xsl:value-of select="$mainTitle" /></mods:namePart>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <mods:namePart><xsl:value-of select="$mainTitle"/></mods:namePart>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='b']">
        <mods:namePart>
          <xsl:value-of select="./p:subfield[@code='b']" />
        </mods:namePart>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='d']">
        <mods:namePart type="date">
          <xsl:value-of select="./p:subfield[@code='d']" />
        </mods:namePart>
      </xsl:if>
       <xsl:if test="./p:subfield[@code='g']"> <!--  non-normative type "place" -->
        <mods:namePart>
          <xsl:value-of select="./p:subfield[@code='g']" />
        </mods:namePart>
      </xsl:if>
      <xsl:if test="./p:subfield[@code='c']"> <!--  non-normative type "place" -->
        <mods:namePart>
          <xsl:value-of select="./p:subfield[@code='c']" />
        </mods:namePart>
      </xsl:if>
      
      <xsl:if test="./p:subfield[@code='B']">
        <mods:role><mods:roleTerm type="text" authority="gbv"><xsl:value-of select="./p:subfield[@code='B']" /></mods:roleTerm></mods:role>
      </xsl:if>
    </mods:name>
  </xsl:template>
  
  <xsl:template name="UBR_Class_Doctype">
    <xsl:variable name="pica4110" select="./p:datafield[@tag='036L']/p:subfield[@code='a']" />
    <xsl:variable name="pica0500_2" select="substring(./p:datafield[@tag='002@']/p:subfield[@code='0'],2,1)" />
    <xsl:for-each select="document('http://rosdok.uni-rostock.de/api/v1/classifications/rosdok_class_doctypes')//category[./label[@xml:lang='x-pica-0500-2']]">
      <xsl:if test="$pica4110 = ./label[@xml:lang='x-pica-4110']/@text and contains(./label[@xml:lang='x-pica-0500-2']/@text, $pica0500_2)">
        <xsl:element name="mods:classification">
          <xsl:attribute name="authorityURI">http://rosdok.uni-rostock.de/classifications/rosdok_class_doctypes</xsl:attribute>
            <xsl:attribute name="valueURI">http://rosdok.uni-rostock.de/classifications/rosdok_class_doctypes#<xsl:value-of select="./@ID" /></xsl:attribute>
          </xsl:element>
        </xsl:if>
    </xsl:for-each>     
  </xsl:template>

  <xsl:template name="UBR_Class_Collection">
      <xsl:for-each select="./p:datafield[@tag='036L']/p:subfield[@code='a']/text()">
        <xsl:variable name="pica4110" select="." />
          <xsl:for-each select="document('http://rosdok.uni-rostock.de/api/v1/classifications/rosdok_class_collections')//category/label[@xml:lang='de']">
            <xsl:if test="$pica4110 = ./@text">
              <xsl:element name="mods:classification">
                <xsl:attribute name="authorityURI">http://rosdok.uni-rostock.de/classifications/rosdok_class_collections</xsl:attribute>
                <xsl:attribute name="valueURI">http://rosdok.uni-rostock.de/classifications/rosdok_class_collections#<xsl:value-of select="./../@ID" /></xsl:attribute>
              </xsl:element>
            </xsl:if>
          </xsl:for-each>
      </xsl:for-each>
  </xsl:template>
  
  <xsl:template name="UBR_Class_AADGenres">
      <xsl:for-each select="./p:subfield[@code='9']/text()">
        <xsl:variable name="ppn" select="." />
          <xsl:for-each select="document('http://rosdok.uni-rostock.de/api/v1/classifications/class_aadgenres')//category/label[@xml:lang='x-ppn']">
            <xsl:if test="$ppn = ./@text">
              <xsl:element name="mods:classification">
                <xsl:attribute name="authorityURI">http://rosdok.uni-rostock.de/classifications/class_aadgenres</xsl:attribute>
                <xsl:attribute name="valueURI">http://rosdok.uni-rostock.de/classifications/rosdok_class_aadgenres#<xsl:value-of select="./../@ID" /></xsl:attribute>
              </xsl:element>
            </xsl:if>
          </xsl:for-each>
      </xsl:for-each>
  </xsl:template>
  
</xsl:stylesheet> 
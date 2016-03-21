<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xalan="http://xml.apache.org/xalan"

  xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation" xmlns:mcrmods="xalan://org.mycore.mods.classification.MCRMODSClassificationSupport"

  xmlns:mcrxsl="xalan://org.mycore.common.xml.MCRXMLFunctions" xmlns:mcr="http://www.mycore.org/" version="1.0"
  exclude-result-prefixes="mods xlink xalan i18n mcrmods mcrxsl mcr">

  <xsl:import href="mods-util.xsl" />
  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" encoding="UTF-8" />
  <xsl:param name="WebApplicationBaseURL" />

  <xsl:template match="/">
    <xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
      <table class="table ir-table-docdetails" style="width:100%">
        <tr>
          <th>
            <xsl:value-of select="i18n:translate('OMD.institution')" />
            :
          </th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr>
                <td>
                  <xsl:variable name="classlink"
                    select="mcrmods:getClassCategLink(mods:classification[@displayLabel='institution'])" />
                  <xsl:value-of
                    select="document($classlink)/mycoreclass/categories/category/label[@xml:lang='de']/@text" />
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <th>
            <xsl:value-of select="i18n:translate('OMD.referee')" />
            :
          </th>
          <td>
            <table class="ir-table-docdetails-values">
                <xsl:for-each select="mods:name[mods:role/mods:roleTerm/@valueURI='http://id.loc.gov/vocabulary/relators/dgs']">
                <tr><td>
                    <xsl:call-template name="advisor">
		    			<xsl:with-param name="name" select="." />
    				</xsl:call-template>
                </td></tr>
                
                </xsl:for-each>
            </table>
          </td>
        </tr>
        <tr>
          <th><xsl:value-of select="i18n:translate('OMD.yearsubmitted')" disable-output-escaping="yes"/>:</th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr><td>
                <xsl:value-of select="mods:originInfo[@eventType='creation']/mods:dateCreated" />
              </td></tr>
            </table>
          </td>
        </tr>
        <tr>
          <th><xsl:value-of select="i18n:translate('OMD.yearaccepted')" disable-output-escaping="yes"/>:</th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr><td>
                <xsl:value-of select="mods:originInfo[@eventType='creation']/mods:dateOther[@type='defence']" />
              </td></tr>
            </table>
          </td>
        </tr>
      </table>
      
      <table class="table ir-table-docdetails" style="width:100%">
        <xsl:for-each select="mods:titleInfo[@type='translated']">
          <tr>
          <th>
            <xsl:value-of select="i18n:translate('OMD.translated-title')" />
            :
          </th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr>
                <td style="text-align:justify">
                  <xsl:value-of select="mods:title" />
                  <xsl:if test="mods:subTitle">
                    : <br /> <xsl:value-of select="mods:subTitle" />
                  </xsl:if>     
                </td>
              </tr>
            </table>
          </td>
          </tr>
        </xsl:for-each>
        <xsl:for-each select="mods:abstract[@type='summary'][1]">
        <tr>
          <th>
            <xsl:value-of select="i18n:translate('OMD.translated-abstract')" />
            :
          </th>
          <td>
            <table class="ir-table-docdetails-values">
                <tr><td style="text-align:justify">
                    <xsl:value-of select="." />
                </td></tr>
            </table>
          </td>
        </tr>
        </xsl:for-each>
         <xsl:for-each select="mods:abstract[@type='author_keywords']">
        <tr>
          <th><xsl:value-of select="i18n:translate('OMD.keywords')" />:</th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr><td>
                <xsl:value-of select="." />
              </td></tr>
            </table>
          </td>
        </tr>
        </xsl:for-each>
        
        <tr>
          <th><xsl:value-of select="i18n:translate('OMD.ddc-class')" />:</th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr><td>
                <xsl:variable name="classlink" select="mcrmods:getClassCategLink(mods:classification[@displayLabel='sdnb'])" />
			 	<xsl:value-of select="document($classlink)/mycoreclass/categories/category/label[@xml:lang='de']/@text" />
			  </td></tr>
            </table>
          </td>
        </tr>
      </table>
      
      <table class="table ir-table-docdetails" style="width:100%">
       <tr>
          <th>
            <xsl:value-of select="i18n:translate('OMD.urns')" />
            :
          </th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr>
                <td>
                  <xsl:variable name="urn" select="mods:identifier[@type='urn']" />
                  <xsl:element name="a">
                    <xsl:attribute name="href">http://nbn-resolving.de<xsl:value-of select="$urn" /></xsl:attribute>
                    <xsl:value-of select="$urn" />
                  </xsl:element>                       
                </td>
              </tr>
            </table>
          </td>
          </tr>
      </table>
      </xsl:for-each>
      <table class="table ir-table-docdetails" style="width:100%">
        <tr>
          <th><xsl:value-of select="i18n:translate('OMD.created_changed')" />:</th>
          <td>
            <table class="ir-table-docdetails-values">
              <tr><td>
                <xsl:value-of select="substring(/mycoreobject/service/servdats/servdate[@type='createdate'], 1,10)" /> / 
                <xsl:value-of select="substring(/mycoreobject/service/servdates/servdate[@type='modifydate'], 1, 10)" />  
              </td></tr>
            </table>
          </td>
        </tr>
     </table>
    
  </xsl:template>
</xsl:stylesheet>
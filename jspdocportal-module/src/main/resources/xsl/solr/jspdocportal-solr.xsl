<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
  xmlns:mcrxml="xalan://org.mycore.common.xml.MCRXMLFunctions"
  xmlns:mcrmods="xalan://org.mycore.mods.classification.MCRMODSClassificationSupport"
  xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="mods xlink">
  <xsl:import href="xslImport:solr-document:solr/jspdocportal-solr.xsl" />

  <xsl:template match="mycoreobject">
    <xsl:apply-imports />

    <xsl:apply-templates select="structure"/>
    <xsl:apply-templates select="metadata"/>
 	</xsl:template>

	<xsl:template match="structure">   
      <!-- online type of derivate -->
      <xsl:for-each select="/mycoreobject/structure/derobjects/derobject">
         <field name="derivatelabel"><xsl:value-of select="@xlink:label|@xlink:title" /></field>
      </xsl:for-each>
      <xsl:for-each select="/mycoreobject/structure/derobjects/derobject[@xlink:title='cover'][1]">
         <xsl:variable name="derId" select="@xlink:href" />
         <xsl:variable name="derXML" select="document(concat('mcrobject:',$derId))" />
     	 <xsl:for-each select="$derXML/mycorederivate/derivate/internals/internal">
     		<xsl:if test="string-length(@maindoc)>0">
     			<field name="ir.cover_url">file/<xsl:value-of select="$derXML/mycorederivate/derivate/linkmetas/linkmeta/@xlink:href" />/<xsl:value-of select="$derXML/mycorederivate/@ID" />/<xsl:value-of select="@maindoc" /></field>
     		</xsl:if>
     	 </xsl:for-each>
      </xsl:for-each>
     
      <xsl:for-each select="/mycoreobject/structure/derobjects/derobject[@xlink:title='fulltext'][1]">
        <xsl:variable name="derId" select="@xlink:href" />
        <xsl:variable name="derXML" select="document(concat('mcrobject:',$derId))" />
        <xsl:for-each select="$derXML/mycorederivate/derivate/internals/internal">
          <xsl:if test="string-length(@maindoc)>0">
            <field name="ir.pdffulltext_url">file/<xsl:value-of select="$derXML/mycorederivate/derivate/linkmetas/linkmeta/@xlink:href" />/<xsl:value-of select="$derXML/mycorederivate/@ID" />/<xsl:value-of select="@maindoc" /></field>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
      
      <xsl:for-each select="/mycoreobject/structure/derobjects/derobject[@xlink:title='REPOS_METS'][1]">
        <xsl:variable name="derId" select="@xlink:href" />
        <xsl:variable name="derXML" select="document(concat('mcrobject:',$derId))" />
        <xsl:for-each select="$derXML/mycorederivate/derivate/internals/internal">
           <xsl:if test="string-length(@maindoc)>0">
             <field name="ir.reposmets_url">file/<xsl:value-of select="$derXML/mycorederivate/derivate/linkmetas/linkmeta/@xlink:href" />/<xsl:value-of select="$derXML/mycorederivate/@ID" />/<xsl:value-of select="@maindoc" /></field>
           </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
  </xsl:template>

  <xsl:template match="metadata">
    <xsl:apply-imports/>
  
  	<xsl:for-each select="def.modsContainer/modsContainer/mods:mods">
        <field name="recordIdentifier"><xsl:value-of select="mods:recordInfo/mods:recordIdentifier" /></field>
  		
      <xsl:choose>
            <xsl:when test="mods:identifier[@type='urn']">
              <field name="purl">http://nbn-resolving.org/<xsl:value-of select="mods:identifier[@type='urn']" /></field>
            </xsl:when>
            <xsl:when test="mods:identifier[@type='purl']">
              <field name="purl"><xsl:value-of select="mods:identifier[@type='purl']" /></field>  
            </xsl:when>
        </xsl:choose>
        
        <xsl:variable name="var_creator">
  			<xsl:for-each select="mods:name[mods:role/mods:roleTerm/@valueURI='http://id.loc.gov/vocabulary/relators/aut' or mods:role/mods:roleTerm='aut']">
  				<xsl:if test="position()> 1">; </xsl:if>
                <xsl:if test="mods:namePart[@type='family']">
                  <xsl:value-of select="mods:namePart[@type='family']" />
  				  <xsl:value-of select="', '" />
                </xsl:if>
  				<xsl:value-of select="mods:namePart[@type='given']" />
                <xsl:value-of select="mods:namePart[not(@type)]" />
  			</xsl:for-each>
  		</xsl:variable>
  	
  		<field name="ir.creator.result"><xsl:value-of select="normalize-space($var_creator)"></xsl:value-of></field>
  		<xsl:for-each select="mods:titleInfo[1]">
          <field name="ir.title.result"><xsl:if test="mods:nonSort"><xsl:value-of select="mods:nonsort" /><xsl:value-of select="' '" /></xsl:if><xsl:value-of select="mods:title" /><xsl:if test="mods:subTtitle"><xsl:value-of select="' : '" /><xsl:value-of select="mods:subTitle" /></xsl:if><xsl:if test="mods:partNumber|mods:partName"><xsl:value-of select="' ['" /><xsl:value-of select="mods:partNumber" /><xsl:value-of select="' '" /><xsl:value-of select="mods:partName" /><xsl:value-of select="']'" /></xsl:if></field> 
       </xsl:for-each>
       <xsl:for-each select="mods:location[mods:shelfLocator]">
          <field name="ir.shelfLocator.result"><xsl:if test="mods:physicalLocation"><xsl:value-of select="mods:physicalLocation" />: </xsl:if><xsl:value-of select="mods:shelfLocator" /></field>
       </xsl:for-each>

       <xsl:variable name="classlink" select="mcrmods:getClassCategLink(mods:classification[@displayLabel='doctype'])" />
  	   <xsl:variable name="var_published">
			<xsl:if test="string-length($classlink) &gt; 0">
       			<xsl:value-of select="concat(document($classlink)/mycoreclass/categories/category/label[@xml:lang='de']/@text, ', ')" />
       		</xsl:if>
            <xsl:choose>
              <xsl:when test="mods:originInfo[@eventType='publication']"> 
                <xsl:for-each select="mods:originInfo[@eventType='publication'][1]">
                  <xsl:if test="mods:edition"><xsl:value-of select="mods:edition" /><xsl:value-of select="' , '" /></xsl:if>    
                  <xsl:if test="mods:place/mods:placeTerm"><xsl:value-of select="mods:place/mods:placeTerm" /><xsl:value-of select="' : '" /></xsl:if>
                  <xsl:if test="mods:publisher"><xsl:value-of select="mods:publisher" /><xsl:value-of select="' , '" /></xsl:if>
                  <xsl:value-of select="mods:dateIssued" />
                    <xsl:value-of select="mods:dateCreated[@qualifier='approximate']" />
                </xsl:for-each>  
              </xsl:when>
              <xsl:when test="mods:originInfo[@eventType='creation']"> 
                <xsl:for-each select="mods:originInfo[@eventType='creation'][1]">
                  <xsl:if test="mods:edition"><xsl:value-of select="mods:edition" /><xsl:value-of select="' , '" /></xsl:if>    
                  <xsl:if test="mods:place/mods:placeTerm"><xsl:value-of select="mods:place/mods:placeTerm" /><xsl:value-of select="' : '" /></xsl:if>
                  <xsl:if test="mods:publisher"><xsl:value-of select="mods:publisher" /><xsl:value-of select="' , '" /></xsl:if>
                  <xsl:value-of select="mods:dateIssued" />
                  <xsl:value-of select="mods:dateCreated[@qualifier='approximate']" />
                </xsl:for-each>  
              </xsl:when>
            </xsl:choose>
 			
  		</xsl:variable>
  		<field name="ir.originInfo.result"><xsl:value-of select="normalize-space($var_published)"></xsl:value-of></field>
      
        <xsl:if test="mods:abstract">
  	  	<xsl:variable name="var_abstract" select="mods:abstract[1]" />
  	  	<xsl:choose>
  	  		<xsl:when test="string-length($var_abstract)>300">
  	  			<field name="ir.abstract300.result"><xsl:value-of select="concat(substring($var_abstract, 1,300),' ...')" /></field>
  	  		</xsl:when>
  	  		<xsl:otherwise>
  	  			<field name="ir.abstract300.result"><xsl:value-of select="normalize-space($var_abstract)" /></field>
  	  		</xsl:otherwise>
  	  	</xsl:choose>
        </xsl:if>
        
        <xsl:for-each select="mods:relatedItem[@type='host'][1]">
            <xsl:for-each select="mods:titleInfo">
              <field name="ir.host.title.result"><xsl:if test="mods:nonSort"><xsl:value-of select="mods:nonsort" /><xsl:value-of select="' '" /></xsl:if><xsl:value-of select="mods:title" /><xsl:if test="mods:subTtitle"><xsl:value-of select="' : '" /><xsl:value-of select="mods:subTitle" /></xsl:if><xsl:if test="mods:partNumber|mods:partName"><xsl:value-of select="' ['" /><xsl:value-of select="mods:partNumber" /><xsl:value-of select="' '" /><xsl:value-of select="mods:partName" /><xsl:value-of select="']'" /></xsl:if></field> 
            </xsl:for-each>
            <xsl:for-each select="mods:recordInfo/mods:recordIdentifier">
              <field name="ir.host.recordIdentifier"><xsl:value-of select="mods:recordInfo/mods:recordIdentifier" /></field> 
            </xsl:for-each>
        </xsl:for-each>
        
  	  	<xsl:for-each select="mods:name[mods:role/mods:roleTerm/@valueURI='http://id.loc.gov/vocabulary/relators/aut']">
  	  		<field name="ir.creator_all"><xsl:value-of select="mods:namePart[@type='termsOfAddress']" /><xsl:value-of select="' '"/><xsl:value-of select="mods:namePart[@type='given']" /><xsl:value-of select="' '"/><xsl:value-of select="mods:namePart[@type='family']" /></field>
  	    </xsl:for-each>
  		<xsl:for-each select="//mods:titleInfo/*">
  			<field name="ir.title_all"><xsl:value-of select="text()" /></field>
  		</xsl:for-each>
        <xsl:for-each select="//mods:note[@type='titlewordindex']">
          <field name="ir.title_all"><xsl:value-of select="text()" /></field>
        </xsl:for-each>
        
          <xsl:for-each select="//mods:location//*">
            <field name="ir.location_all"><xsl:value-of select="text()" /></field>
          </xsl:for-each>
          <xsl:for-each select="//mods:name//*">
            <field name="ir.creator_all"><xsl:value-of select="text()" /></field>
          </xsl:for-each>
          
          <xsl:variable name="pubyear_start">
          <xsl:choose>
            <xsl:when test="mods:originInfo[@eventType='publication']">
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateIssued[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateIssued,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateCreated[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateCreated,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateCreated[@point]]">
                <xsl:value-of select="substring(mods:dateCreated[@point='start'],1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateIssued[@point]]">
                <xsl:value-of select="substring(mods:dateIssued[@point='start'],1,4)" />
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateIssued[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateIssued,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateCreated[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateCreated,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateCreated[@point]]">
                <xsl:value-of select="substring(mods:dateCreated[@point='start'],1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateIssued[@point]]">
                <xsl:value-of select="substring(mods:dateIssued[@point='start'],1,4)" />
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>          
          </xsl:variable>
          
          <xsl:variable name="pubyear_end">
          <xsl:choose>
            <xsl:when test="mods:originInfo[@eventType='publication']">
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateIssued[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateIssued,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateCreated[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateCreated,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateCreated[@point]]">
                <xsl:value-of select="substring(mods:dateCreated[@point='end'],1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='publication'][mods:dateIssued[@point]]">
                <xsl:value-of select="substring(mods:dateIssued[@point='end'],1,4)" />
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateIssued[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateIssued,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateCreated[not(@point) and not(@qualifier)]]">
                <xsl:value-of select="substring(mods:dateCreated,1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateCreated[@point]]">
                <xsl:value-of select="substring(mods:dateCreated[@point='end'],1,4)" />
              </xsl:for-each>
              <xsl:for-each select="mods:originInfo[@eventType='creation'][mods:dateIssued[@point]]">
                <xsl:value-of select="substring(mods:dateIssued[@point='end'],1,4)" />
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>  
          </xsl:variable>
          
          <xsl:if test="$pubyear_start and $pubyear_end">
            <field name="ir.pubyear_start"><xsl:value-of select="$pubyear_start" /></field>
            <field name="ir.pubyear_end"><xsl:value-of select="$pubyear_end" /></field>
            
          <!-- epoch facets -->
            <xsl:if test="$pubyear_start &lt;= 1500">
              <field name="ir.epoch_msg.facet">1500_and_earlier</field>
            </xsl:if>
            <xsl:if test="$pubyear_start &lt;= 1600 and $pubyear_end &gt; 1500">
              <field name="ir.epoch_msg.facet">16th_century</field>
            </xsl:if>
            <xsl:if test="$pubyear_start &lt;= 1700 and $pubyear_end &gt; 1600">
              <field name="ir.epoch_msg.facet">17th_century</field>
            </xsl:if>
            <xsl:if test="$pubyear_start &lt;= 1800 and $pubyear_end &gt; 1700">
              <field name="ir.epoch_msg.facet">18th_century</field>
            </xsl:if>
            <xsl:if test="$pubyear_start &lt;= 1900 and $pubyear_end &gt; 1800">
              <field name="ir.epoch_msg.facet">19th_century</field>
            </xsl:if>
            <xsl:if test="$pubyear_start &lt;= 2000 and $pubyear_end &gt; 1900">
              <field name="ir.epoch_msg.facet">20th_century</field>
            </xsl:if>
            <xsl:if test="$pubyear_start &lt;= 2100 and $pubyear_end &gt; 2000">
              <field name="ir.epoch_msg.facet">21th_century</field>
            </xsl:if>
          </xsl:if>
          
          <!-- more facets -->
          
          <xsl:for-each select="mods:language/mods:languageTerm">
               <field name="ir.language_class.facet">rfc4646:<xsl:value-of select="." /></field>
          </xsl:for-each>
          
           <xsl:for-each select="mods:originInfo[@eventType='creation']/mods:place/mods:placeTerm">
                <field name="ir.place.facet"><xsl:value-of select="translate(.,'[]','')" /></field>
           </xsl:for-each>
           <xsl:for-each select="mods:classification[@displayLabel='doctype']">
                <field name="ir.doctype_class.facet"><xsl:value-of select="translate(substring-after(@valueURI,'classifications/'),'#',':')" /></field>
           </xsl:for-each>
           <xsl:for-each select="mods:classification[@displayLabel='collection']">
                <field name="ir.collection_class.facet"><xsl:value-of select="translate(substring-after(@valueURI,'classifications/'),'#',':')" /></field>
           </xsl:for-each>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
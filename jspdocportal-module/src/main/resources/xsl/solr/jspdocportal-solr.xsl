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
     			<field name="ds.result.cover_url">file/<xsl:value-of select="$derXML/mycorederivate/derivate/linkmetas/linkmeta/@xlink:href" />/<xsl:value-of select="$derXML/mycorederivate/@ID" />/<xsl:value-of select="@maindoc" /></field>
     		</xsl:if>
     	</xsl:for-each>
     </xsl:for-each>
     
     <xsl:for-each select="/mycoreobject/structure/derobjects/derobject[@xlink:title='fulltext'][1]">
        <xsl:variable name="derId" select="@xlink:href" />
        <xsl:variable name="derXML" select="document(concat('mcrobject:',$derId))" />
        <xsl:for-each select="$derXML/mycorederivate/derivate/internals/internal">
          <xsl:if test="string-length(@maindoc)>0">
            <field name="mcrviewer.pdf">file/<xsl:value-of select="$derXML/mycorederivate/derivate/linkmetas/linkmeta/@xlink:href" />/<xsl:value-of select="$derXML/mycorederivate/@ID" />/<xsl:value-of select="@maindoc" /></field>
          </xsl:if>
        </xsl:for-each>
     </xsl:for-each>
  </xsl:template>

  <xsl:template match="metadata">
    <xsl:apply-imports/>
  
  	<xsl:for-each select="def.modsContainer/modsContainer/mods:mods">
  		<xsl:variable name="var_creator">
  			<xsl:for-each select="mods:name[mods:role/mods:roleTerm/@valueURI='http://id.loc.gov/vocabulary/relators/aut']">
  				<xsl:if test="position()> 1">, </xsl:if>
  				<xsl:value-of select="mods:namePart[@type='given']" />
  				<xsl:value-of select="' '" />
  				<xsl:value-of select="mods:namePart[@type='family']" />
  			</xsl:for-each>
  		</xsl:variable>
  	
  		<field name="ds.result.creator"><xsl:value-of select="normalize-space($var_creator)"></xsl:value-of></field>
  		<field name="ds.result.title"><xsl:value-of select="mods:titleInfo/mods:title"></xsl:value-of></field>
  		<xsl:variable name="classlink" select="mcrmods:getClassCategLink(mods:classification[@displayLabel='document_type'])" />
  		<xsl:variable name="var_published">
			<xsl:if test="string-length($classlink) &gt; 0">
       			<xsl:value-of select="concat(document($classlink)/mycoreclass/categories/category/label[@xml:lang='de']/@text, ', ')" />
       		</xsl:if>
 			<xsl:for-each select="mods:originInfo[@eventType='publication']">
  				<xsl:value-of select="mods:publisher" />
  				<xsl:value-of select="' '" />
  				<xsl:value-of select="mods:place/mods:placeTerm" />,
  				<xsl:value-of select="mods:dateIssued" />
  			</xsl:for-each>
  		</xsl:variable>
  		<field name="ds.result.published"><xsl:value-of select="normalize-space($var_published)"></xsl:value-of></field>
  	  	<xsl:variable name="var_abstract" select="mods:abstract[1]" />
  	  	<xsl:choose>
  	  		<xsl:when test="string-length($var_abstract)>300">
  	  			<field name="ds.result.abstract300"><xsl:value-of select="concat(substring($var_abstract, 1,300),' ...')" /></field>
  	  		</xsl:when>
  	  		<xsl:otherwise>
  	  			<field name="ds.result.abstract300"><xsl:value-of select="normalize-space($var_abstract)" /></field>
  	  		</xsl:otherwise>
  	  	</xsl:choose>
  	  	<xsl:for-each select="mods:name[mods:role/mods:roleTerm/@valueURI='http://id.loc.gov/vocabulary/relators/aut']">
  	  		<field name="ds.creator"><xsl:value-of select="mods:namePart[@type='termsOfAddress']" /><xsl:value-of select="' '"/><xsl:value-of select="mods:namePart[@type='given']" /><xsl:value-of select="' '"/><xsl:value-of select="mods:namePart[@type='family']" /></field>
  	    </xsl:for-each>
  	    <xsl:for-each select="mods:originInfo[@eventType='publication']">
  			<field name="ds.pubyear"><xsl:value-of select="substring(mods:dateIssued,1,4)" /></field>
  		</xsl:for-each>
  		<xsl:for-each select="mods:titleInfo/*">
  			<field name="ds.title"><xsl:value-of select="text()" /></field>
  		</xsl:for-each>
  	  
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
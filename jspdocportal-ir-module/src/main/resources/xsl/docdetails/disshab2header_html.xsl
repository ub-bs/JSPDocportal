<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:mcrmods="xalan://org.mycore.mods.classification.MCRMODSClassificationSupport" exclude-result-prefixes="mods mcrmods">

    <!-- to enable relative urls in import set xsltSystemId attribute in x:transform of JSP XML Tag Library !!! -->
	<xsl:import href="mods-util.xsl" />
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" encoding="UTF-8" />

	<xsl:param name="WebApplicationBaseURL"></xsl:param>

	<xsl:template match="/">
		<xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
  		  <p>
            <xsl:call-template name="mods-name" /><br />
          </p>
		  
		  <xsl:call-template name="mods-title" />
          
                <p>
					<xsl:call-template name="mods-originInfo" />
				</p>
                <xsl:choose>
                  <xsl:when test="./mods:identifier[@type='doi']">
					<p>
						<xsl:element name="a">
							<xsl:attribute name="href">https://doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" /></xsl:attribute>
							https://doi.org/<xsl:value-of select="./mods:identifier[@type='doi']" />
						</xsl:element>
					</p>
				  </xsl:when>
                  <xsl:otherwise>
				    <xsl:choose>
                        <xsl:when test="./mods:identifier[@type='purl']">
					       <p>
						    <xsl:element name="a">
							     <xsl:attribute name="href"><xsl:value-of select="./mods:identifier[@type='purl']" /></xsl:attribute>
							     <xsl:value-of select="./mods:identifier[@type='purl']" />
						    </xsl:element>
					       </p>
                         </xsl:when>
				         <xsl:otherwise>
                            <xsl:if test="./mods:url[@access='object in context']">
                                <p>
                                <xsl:element name="a">
                                  <xsl:attribute name="href"><xsl:value-of select="./mods:url[@access='object in context']" /></xsl:attribute>
                                  <xsl:value-of select="./mods:url[@access='object in context']" />
                                </xsl:element>
                              </p>
                            </xsl:if>
                         </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="./mods:abstract">
                  <p class="ir-docdetails-abstract">
                    <xsl:value-of select="./mods:abstract" />
                  </p>
              </xsl:if>
  			  <xsl:if test="./mods:classification[@displayLabel='doctype']">
            <p>
            <span class="badge badge-secondary">
              <xsl:for-each select="./mods:classification[@displayLabel='doctype']/@valueURI">
                <xsl:call-template name="classLabel">
                  <xsl:with-param name="valueURI"><xsl:value-of select="." /></xsl:with-param>
                </xsl:call-template>
              </xsl:for-each>
            </span>
            <xsl:call-template name="accessLabel" />
            </p>
          </xsl:if>
		</xsl:for-each>

	</xsl:template>

</xsl:stylesheet>
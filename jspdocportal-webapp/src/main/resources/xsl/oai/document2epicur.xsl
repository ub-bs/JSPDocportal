<?xml version="1.0" encoding="UTF-8"?>
<!-- ============================================== -->
<!-- $Revision: 1.6 $ $Date: 2008-01-15 14:20:55 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:urn="http://www.ddb.de/standards/urn" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://www.openarchives.org/OAI/2.0/">

	<xsl:output method="xml" encoding="UTF-8" />

	<xsl:param name="ServletsBaseURL" select="''" />
	<xsl:param name="JSessionID" select="''" />
	<xsl:param name="WebApplicationBaseURL" select="''" />
    
	<xsl:include href="document2record.xsl" />

	<xsl:template match="mycoreobject" mode="metadata">
		<xsl:text disable-output-escaping="yes">&lt;epicur xsi:schemaLocation="urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd"
                xmlns="urn:nbn:de:1111-2004033116" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;</xsl:text>
	    <xsl:variable name="epicurType" select="./metadata/urns/urn/@type" />
		<xsl:call-template name="administrative_data">
			<xsl:with-param name="epicurType" select="$epicurType" />
		</xsl:call-template>
		<xsl:call-template name="record">
			<xsl:with-param name="epicurType" select="$epicurType" />
		</xsl:call-template>
		<xsl:text disable-output-escaping="yes">&lt;/epicur&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="linkQueryURL">
		<xsl:param name="id" />
		<xsl:value-of select="concat('mcrobject:',$id)" />
	</xsl:template>

	<xsl:template name="linkDerDetailsURL">
		<xsl:param name="host" select="'local'" />
		<xsl:param name="id" />
		<xsl:variable name="derivbase"
			select="concat($ServletsBaseURL,'MCRFileNodeServlet/',$id,'/')" />
		<xsl:value-of
			select="concat($derivbase,'?MCRSessionID=',$JSessionID,'&amp;hosts=',$host,'&amp;XSL.Style=xml')" />
	</xsl:template>

	<xsl:template name="linkClassQueryURL">
		<xsl:param name="type" select="'class'" />
		<xsl:param name="host" select="'local'" />
		<xsl:param name="classid" select="''" />
		<xsl:param name="categid" select="''" />
		<xsl:value-of
			select="concat($ServletsBaseURL,'MCRQueryServlet',$JSessionID,'?XSL.Style=xml&amp;type=',$type,'&amp;hosts=',$host,'&amp;query=%2Fmycoreclass%5B%40ID%3D%27',$classid,'%27%20and%20*%2Fcategory%2F%40ID%3D%27',$categid,'%27%5D')" />
	</xsl:template>

<!-- 
	<xsl:template name="lang">
		<xsl:choose>
			<xsl:when test="./@xml:lang='de'">
				ger
			</xsl:when>
			<xsl:when test="./@xml:lang='en'">
				eng
			</xsl:when>
			<xsl:when test="./@xml:lang='fr'">
				fre
			</xsl:when>
			<xsl:when test="./@xml:lang='es'">
				spa
			</xsl:when>
		</xsl:choose>
	</xsl:template>
  
  -->

	<xsl:template name="administrative_data">
		<xsl:param name="epicurType" select="''" />
		<xsl:element name="administrative_data" namespace="urn:nbn:de:1111-2004033116">
			<xsl:element name="delivery" namespace="urn:nbn:de:1111-2004033116">
				<xsl:element name="update_status" namespace="urn:nbn:de:1111-2004033116">
					<xsl:attribute name="type"><xsl:value-of select="$epicurType" />
				</xsl:attribute></xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="url_update_general">
	</xsl:template>

	<xsl:template name="record">
		<xsl:param name="epicurType" select="''" />
		<xsl:variable name="mycoreobjectID" select="@ID" />
		<xsl:element name="record" namespace="urn:nbn:de:1111-2004033116">
			<xsl:element name="identifier" namespace="urn:nbn:de:1111-2004033116">
				<xsl:attribute name="scheme">urn:nbn:de</xsl:attribute>
				<xsl:value-of select="./metadata/urns/urn[@type=$epicurType]" />
			</xsl:element>
			<xsl:if test="$epicurType='urn_new_version'">
				<xsl:element name="isVersionOf" namespace="urn:nbn:de:1111-2004033116">
					<xsl:attribute name="scheme">urn:nbn:de</xsl:attribute>
					<xsl:value-of select="./metadata/urns/urn[@type='urn_first']" />
				</xsl:element>
			</xsl:if>
			<xsl:element name="resource" namespace="urn:nbn:de:1111-2004033116">
				<xsl:element name="identifier" namespace="urn:nbn:de:1111-2004033116">
					<xsl:attribute name="scheme">url</xsl:attribute>
					<xsl:attribute name="role">primary</xsl:attribute>
					<xsl:attribute name="origin">original</xsl:attribute>
					<xsl:attribute name="type">frontpage</xsl:attribute>
					<xsl:if test="$epicurType = 'urn_new'">
						<xsl:attribute name="status">new</xsl:attribute>
					</xsl:if>
					<xsl:value-of
						select="concat($WebApplicationBaseURL,'metadata/', $mycoreobjectID)" />
				</xsl:element>
				<xsl:element name="format" namespace="urn:nbn:de:1111-2004033116">
					<xsl:attribute name="scheme">imt</xsl:attribute>
					<xsl:value-of select="'text/html'" />
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="./structure/derobjects/derobject">
				<xsl:variable name="derID" select="./@xlink:href" />
				<xsl:variable name="filelink"
					select="concat($WebApplicationBaseURL,'servlets/MCRFileNodeServlet/',
                     $derID,'/?hosts=local&amp;XSL.Style=xml')" />
				<xsl:variable name="details" select="document($filelink)" />
				<xsl:variable name="filenumber"
					select="$details/mcr_directory/numChildren/here/files" />

				<xsl:if test="number($filenumber) = 1">
					<xsl:for-each select="$details/mcr_directory/children/child[@type='file']">

						<xsl:element name="resource" namespace="urn:nbn:de:1111-2004033116">
							<xsl:element name="identifier" namespace="urn:nbn:de:1111-2004033116">
								<xsl:attribute name="scheme">url</xsl:attribute>
								<xsl:value-of
									select="concat($WebApplicationBaseURL,'file/',$derID,'/',./name)" />
							</xsl:element>
							<xsl:element name="format" namespace="urn:nbn:de:1111-2004033116">
								<xsl:attribute name="scheme">imt</xsl:attribute>
								<xsl:choose>
									<xsl:when test="./contentType='pdf'">
										application/pdf
									</xsl:when>
									<xsl:when test="./contentType='html'">
										application/html
									</xsl:when>

									<xsl:when test="./contentType='gif'">
										mage/gif
									</xsl:when>
									<xsl:when test="./contentType='jpeg'">
										image/jpeg
									</xsl:when>
									<xsl:when test="./contentType='png'">
										image/png
									</xsl:when>
									<xsl:when test="./contentType='tiff'">
										image/tiff
									</xsl:when>
									
									<xsl:when test="./contentType='msexcel'">
										application/msexcel
									</xsl:when>
									<xsl:when test="./contentType='msword97'">
										application/msword
									</xsl:when>
									<xsl:when test="./contentType='msword95'">
										application/msword
									</xsl:when>
									<xsl:when test="./contentType='msword'">
										application/msword
									</xsl:when>
									<xsl:when test="./contentType='msppt'">
										application/mspowerpoint
									</xsl:when>

									<xsl:when test="./contentType='odg'">
										application/vnd.oasis.opendocument.graphics
									</xsl:when>
									<xsl:when test="./contentType='odp'">
										application/vnd.oasis.opendocument.presentation
									</xsl:when>
									<xsl:when test="./contentType='ods'">
										application/vnd.oasis.opendocument.spreadsheet
									</xsl:when>
									<xsl:when test="./contentType='odt'">
										application/vnd.oasis.opendocument.text
									</xsl:when>
									<xsl:otherwise>
										application/octet-stream
									</xsl:otherwise>
								</xsl:choose>
							</xsl:element>
						</xsl:element>
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="number($filenumber) &gt; 1">
					<xsl:element name="resource" namespace="urn:nbn:de:1111-2004033116">
						<xsl:element name="identifier" namespace="urn:nbn:de:1111-2004033116">
							<xsl:attribute name="scheme">url</xsl:attribute>
							<xsl:attribute name="target">transfer</xsl:attribute>
							<xsl:value-of
								select="concat($WebApplicationBaseURL,'zip?id=',$derID)" />
						</xsl:element>
						<xsl:element name="format" namespace="urn:nbn:de:1111-2004033116">
							<xsl:attribute name="scheme">imt</xsl:attribute>
							<xsl:value-of select="'application/zip'" />
						</xsl:element>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
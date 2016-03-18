<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink"
	version="1.0" exclude-result-prefixes="mods xlink">
    <xsl:import href="mods-util.xsl" />
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes" encoding="UTF-8" />
	<xsl:param name="WebApplicationBaseURL" />
	
	<xsl:template match="/">
		<xsl:for-each select="/mycoreobject/metadata/def.modsContainer/modsContainer/mods:mods">
			<table class="table ir-table-docdetails" style="width:100%">
				<tr>
					<th>Einrichtung:</th>
					<td>
						<table class="ir-table-docdetails-values">
							<tr>
								<td>Fakultät für Informatik und Elektrotechnik</td>
							</tr>
						</table>
					</td>

				</tr>
			</table>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
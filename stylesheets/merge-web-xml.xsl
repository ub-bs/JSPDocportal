<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision$ $Date$ -->
<!-- ============================================== --> 

<xsl:stylesheet 
  version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<xsl:output 
  indent="yes"
  method="xml" 
  encoding="UTF-8"
/>
<xsl:strip-space elements="*" />
<xsl:preserve-space elements="" />

<xsl:template match="/">
  <xsl:variable name="wia" select="document('web1.xml')/web-app" />
  <xsl:variable name="wib" select="web-app" />
  
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
    version="2.4">
    
    <xsl:copy-of select="$wia/display-name" />

    <xsl:copy-of select="$wia/listener" />
    <xsl:copy-of select="$wib/listener" />

    <xsl:copy-of select="$wia/filter" />
    <xsl:copy-of select="$wib/filter" />

    <xsl:copy-of select="$wia/filter-mapping" />
    <xsl:copy-of select="$wib/filter-mapping" />

    <xsl:copy-of select="$wia/servlet" />
    <xsl:copy-of select="$wib/servlet" />

    <xsl:copy-of select="$wia/servlet-mapping" />
    <xsl:copy-of select="$wib/servlet-mapping" />

    <xsl:copy-of select="$wia/mime-mapping" />
    <xsl:copy-of select="$wib/mime-mapping" />

    <xsl:copy-of select="$wia/session-config" />
    <xsl:copy-of select="$wia/welcome-file-list" />
    
    <xsl:copy-of select="$wia/error-page" />    
    <xsl:copy-of select="$wia/jsp-config" />    
    
  </web-app>

</xsl:template>

</xsl:stylesheet>


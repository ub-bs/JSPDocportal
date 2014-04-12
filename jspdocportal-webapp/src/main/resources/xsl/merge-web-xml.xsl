<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision$ $Date$ -->
<!-- ============================================== --> 

<xsl:stylesheet 
  version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:j2ee="http://java.sun.com/xml/ns/j2ee"
  xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
>
<xsl:output 
  indent="yes"
  method="xml" 
  encoding="UTF-8"
/>
<xsl:strip-space elements="*" />
<xsl:preserve-space elements="" />

<xsl:template match="/">
  <xsl:variable name="wia" select="document('web1.xml')/j2ee:web-app" />
  <xsl:variable name="wib" select="j2ee:web-app" />
  
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
    version="2.4">
    
    <xsl:copy-of select="$wia/j2ee:display-name" />

    <xsl:copy-of select="$wia/j2ee:listener" />
    <xsl:copy-of select="$wib/j2ee:listener" />

    <xsl:copy-of select="$wia/j2ee:filter" />
    <xsl:copy-of select="$wib/j2ee:filter" />

    <xsl:copy-of select="$wia/j2ee:filter-mapping" />
    <xsl:copy-of select="$wib/j2ee:filter-mapping" />

    <xsl:copy-of select="$wia/j2ee:servlet" />
    <!--  problems with jetty, display-name must be before servlet-name -->
    <xsl:for-each select="$wib/j2ee:servlet">
       <servlet>
           <xsl:copy-of select="./j2ee:display-name" />
           <xsl:copy-of select="./j2ee:servlet-name" />
           <xsl:copy-of select="./j2ee:servlet-class" />
           <xsl:copy-of select="./j2ee:load-on-startup" />                      
       </servlet>
    </xsl:for-each>

    <xsl:copy-of select="$wia/j2ee:servlet-mapping" />
    <xsl:copy-of select="$wib/j2ee:servlet-mapping" />

    <xsl:copy-of select="$wia/j2ee:mime-mapping" />
    <xsl:copy-of select="$wib/j2ee:mime-mapping" />

    <xsl:copy-of select="$wia/j2ee:session-config" />
    <xsl:copy-of select="$wia/j2ee:welcome-file-list" />
    
    <xsl:copy-of select="$wia/j2ee:error-page" />    
    <xsl:copy-of select="$wia/j2ee:jsp-config" />    
    
  </web-app>

</xsl:template>

</xsl:stylesheet>


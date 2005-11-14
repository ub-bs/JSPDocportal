<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:03 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" 
   exclude-result-prefixes="xlink">

   <xsl:include href="coreFunctions.xsl"/>

   <xsl:param name="DocumentBaseURL" />
   <xsl:param name="ServletsBaseURL" />
   <xsl:param name="RequestURL" />
   <xsl:param name="CurrentUser" />
   <xsl:param name="CurrentGroups" />
   <xsl:param name="MCRSessionID" />
   <!-- HttpSession is empty if cookies are enabled, else ";jsessionid=<id>" -->
   <xsl:param name="HttpSession" />
   <!-- JSessionID is alway like ";jsessionid=<id>" and good for internal calls -->
   <xsl:param name="JSessionID" />
   <xsl:param name="WebApplicationBaseURL" />
   <xsl:param name="DefaultLang" />
   <xsl:param name="CurrentLang" />
   <xsl:param name="Referer" />
   <xsl:param name="TypeMapping" />
   
   <!-- ======================================================================================================= -->

   <xsl:template match="/">
      <p>
      <xsl:apply-templates/>
      </p>
   </xsl:template>
   <!-- ======================================================================================================= -->
<xsl:template name="personxml">
    <xsl:param name="type" select="'allpers'"/>
    <xsl:param name="host"/>
    <xsl:param name="id"/>
    <xsl:value-of select="concat($ServletsBaseURL,'MCRQueryServlet',$JSessionID,'?XSL.Style=xml&amp;type=',$type,'&amp;hosts=',$host,'&amp;query=%2Fmycoreobject%5B%40ID%3D%27',$id,'%27%5D')" />
</xsl:template>

<xsl:template name="personlink">
    <xsl:param name="nodes"/>
    <xsl:param name="host"/>
    <xsl:for-each select="$nodes">
        <xsl:if test="position() != 1">, </xsl:if>
        <xsl:variable name="type">
            <xsl:call-template name="mappedTypeOfObjectID">
                <xsl:with-param name="id" select="@xlink:href" />
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="personlink">
            <xsl:call-template name="personxml">
                <xsl:with-param name="type" select="$type"/>
                <xsl:with-param name="id" select="@xlink:href"/>
                <xsl:with-param name="host" select="$host"/>
            </xsl:call-template>
        </xsl:variable> 
        <xsl:variable name="thepriv">
            <xsl:for-each select="document($personlink)/mcr_results/mcr_result/mycoreobject/service/servflags/servflag">
                <xsl:if test="starts-with(text(),'Access:')" >
                    <xsl:value-of select="text()" />
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="docpriv">
            <xsl:choose>
                <xsl:when test="starts-with($thepriv,'Access:')" >
                    <xsl:copy-of select="substring-after($thepriv,'Access:')" />
                </xsl:when>
                <xsl:otherwise>public</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="theip">
            <xsl:for-each select="service/servflags/servflag">
                <xsl:if test="starts-with(text(),'IP:')" >
                    <xsl:value-of select="substring-after(text(),'IP:')" />
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="accessurl" select="concat($ServletsBaseURL,'MCRAccessServlet',$JSessionID,'?XSL.Style=xml&amp;mode=reader&amp;privilege=',$docpriv,'&amp;ip=',theip)" />
	<!--
        <xsl:choose>
            <xsl:when test="document($accessurl)/mycoreaccess/access/@return = 'true'">-->

                <a href="{$ServletsBaseURL}MCRQueryServlet{$HttpSession}?mode=ObjectMetadata&amp;type={$type}&amp;hosts={$host}&amp;query=%2Fmycoreobject%5B%40ID%3D'{@xlink:href}'%5D">
                    <xsl:apply-templates select="document($personlink)/mcr_results/mcr_result/mycoreobject/metadata/names" />
               &gt;&gt;</a>
	    <!--
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable xmlns:encoder="xalan://java.net.URLEncoder" name="LoginURL" select="concat( $ServletsBaseURL, 'MCRLoginServlet',$HttpSession,'?url=', encoder:encode( string( $RequestURL ) ) )" />
                <xsl:apply-templates select="document($personlink)/mcr_results/mcr_result/mycoreobject/metadata/names" />&#160;<a href="{$LoginURL}">
                    <img src="{concat($WebApplicationBaseURL,'img/paper_lock.gif')}" />
                </a>
            </xsl:otherwise>
        </xsl:choose>
	    -->
    </xsl:for-each>
</xsl:template>

<xsl:template name="printClass">
    <xsl:param name="nodes"/>
    <xsl:param name="host"/>
    <xsl:for-each select="$nodes">
        <xsl:if test="position() != 1"><br /></xsl:if>
        <xsl:variable name="classlink">
            <xsl:call-template name="ClassCategLink">
                <xsl:with-param name="classid" select="@classid"/>
                <xsl:with-param name="categid" select="@categid"/>
                <xsl:with-param name="host" select="$host"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:for-each select="document($classlink)/mcr_results/mcr_result/mycoreclass/categories/category" >
            <xsl:variable name="categurl">
                <xsl:if test="url" >
                    <xsl:choose>
                        <!-- MCRObjectID should contain a ':' so it must be an external link then -->
                        <xsl:when test="contains(url/@xlink:href,':')">
                            <xsl:value-of select="url/@xlink:href" />
                        </xsl:when>
                        <xsl:otherwise>
                        <xsl:variable name="type">
                            <xsl:call-template name="mappedTypeOfObjectID">
                                <xsl:with-param name="id" select="url/@xlink:href" />
                            </xsl:call-template>
                        </xsl:variable>
                            <xsl:value-of select="concat($ServletsBaseURL,'MCRQueryServlet',$HttpSession,'?mode=ObjectMetadata&amp;type=',$type,'&amp;hosts=',$host,'&amp;query=%2Fmycoreobject%5B%40ID%3D%27',url/@xlink:href,'%27%5D')" />
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:variable>
            <xsl:variable name="selectLang">
                <xsl:call-template name="selectLang">
                    <xsl:with-param name="nodes" select="./label"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:for-each select="./label[lang($selectLang)]">
                <xsl:choose>
                    <xsl:when test="string-length($categurl) != 0">
                        <a href="{$categurl}">
                            <!--<xsl:if test="$wcms.useTargets = 'yes'">
                               <xsl:attribute name="target">_blank</xsl:attribute>
                            </xsl:if>-->
                            <xsl:value-of select="@text" />
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@text" />
                    </xsl:otherwise>
                </xsl:choose>
           </xsl:for-each>
        </xsl:for-each>
    </xsl:for-each>
</xsl:template>

<xsl:template name="printI18N">
    <xsl:param name="nodes"/>
   <xsl:variable name="selectLang">
        <xsl:call-template name="selectLang">
            <xsl:with-param name="nodes" select="$nodes"/>
        </xsl:call-template>
   </xsl:variable>
   <xsl:for-each select="$nodes[lang($selectLang)]" >
        <xsl:if test="position() != 1">, </xsl:if>
            <xsl:value-of select="." />
   </xsl:for-each>
</xsl:template>

<xsl:template name="webLink">
    <xsl:param name="nodes" />
    <xsl:for-each select="$nodes" >
        <xsl:if test="position() != 1"><br /></xsl:if>
        <xsl:variable name="href" select="@xlink:href"/>
        <xsl:variable name="title">
            <xsl:choose>
                <xsl:when test="@xlink:title">
                    <xsl:value-of select="@xlink:title"/>
                </xsl:when>
                <xsl:when test="@xlink:label">
                    <xsl:value-of select="@xlink:label"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@xlink:href" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <a href="{@xlink:href}">
            <xsl:value-of select="$title"/>
        </a>
    </xsl:for-each>
</xsl:template>
<xsl:template name="mailLink">
    <xsl:param name="nodes" />
    <xsl:variable name="selectLang">
        <xsl:call-template name="selectLang">
            <xsl:with-param name="nodes" select="$nodes"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:for-each select="$nodes[lang($selectLang)]" >
        <xsl:if test="position() != 1"><br /></xsl:if>
        <xsl:variable name="email" select="."/>
        <a href="mailto:{$email}"><xsl:value-of select="$email" /></a>
    </xsl:for-each>
</xsl:template>

<!-- Person name form LegalEntity ******************************** -->
<xsl:template match="names">
    <xsl:variable name="name" select="./name[1]"/>
    <xsl:choose>
        <xsl:when test="$name/fullname">
            <xsl:value-of select="$name/fullname"/>
        </xsl:when> 
        <xsl:otherwise>
            <xsl:value-of select="$name/academic"/><xsl:text> </xsl:text>
            <xsl:value-of select="$name/peerage"/><xsl:text> </xsl:text>
            <xsl:value-of select="$name/callname"/><xsl:text> </xsl:text> 
            <xsl:value-of select="$name/prefix"/><xsl:text> </xsl:text> 
            <xsl:value-of select="$name/surname"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.4 $ $Date: 2006-01-23 18:13:51 $ -->
<!-- ============================================== -->
<!-- Authors: Thomas Scheffler (yagee) -->
<!-- Authors: Andreas Trappe (lezard) -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" 
      exclude-result-prefixes="xlink">
	  <!--
      <xsl:include href="coreFunctions_encode.xsl" />
      <xsl:include href="coreFunctions_decode.xsl" />       
	  -->
    <!--
    Template: UrlSetParam
    synopsis: Inserts a $HttpSession to an URL
    param:

    url: URL to include the session
    -->
    <xsl:template name="UrlAddSession">
        <xsl:param name="url"/>
        <!-- There are two possibility for a parameter to appear in an URL:
            1.) after a ? sign
            2.) after a & sign
            In both cases the value is either limited by a & sign or the string end
        //-->
        <xsl:choose>
            <xsl:when test="starts-with($url,$WebApplicationBaseURL)">
                <!--The document is on our server-->
                <xsl:variable name="pathPart">
                    <xsl:choose>
                        <xsl:when test="contains($url,'?')">
                            <xsl:value-of select="substring-before($url,'?')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$url"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="queryPart">
                    <xsl:value-of select="substring-after($url,$pathPart)"/>
                </xsl:variable>
                <xsl:value-of select="concat($pathPart,$HttpSession,$queryPart)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$url"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!--
    Template: UrlSetParam
    synopsis: Replaces a parameter value or adds a parameter to an URL
    param:

    url: URL to contain the parameter and value
    par: name of the parameter
    value: new value
    -->
    <xsl:template name="UrlSetParam">
        <xsl:param name="url"/>
        <xsl:param name="par"/>
        <xsl:param name="value"/>
        <!-- There are two possibility for a parameter to appear in an URL:
            1.) after a ? sign
            2.) after a & sign
            In both cases the value is either limited by a & sign or the string end
        //-->
        <xsl:variable name="asFirstParam">
            <xsl:value-of select="concat('?',$par,'=')"/>
        </xsl:variable>
        <xsl:variable name="asOtherParam">
            <xsl:value-of select="concat('&amp;',$par,'=')"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="contains($url,$asFirstParam) or contains($url,$asOtherParam)">
            <!-- Parameter is present -->
                <xsl:variable name="asParam">
                    <xsl:choose>
                        <xsl:when test="contains($url,$asFirstParam)">
                            <!-- Parameter is right after a question mark //-->
                            <xsl:value-of select="$asFirstParam"/>
                        </xsl:when>
                        <xsl:when test="contains($url,asOtherParam)">
                            <!-- Parameter is right after a & sign //-->
                            <xsl:value-of select="$asOtherParam"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="newurl">
                    <xsl:value-of select="substring-before($url,$asParam)"/>
                    <xsl:value-of select="$asParam"/>
                    <xsl:value-of select="$value"/>
                    <xsl:if test="contains(substring-after($url,$asParam),'&amp;')">
                        <!--OK now we know that there are parameters left //-->
                        <xsl:value-of select="concat('&amp;',substring-after(substring-after($url,$asParam),'&amp;'))"/>
                    </xsl:if>
                </xsl:variable>
                <xsl:value-of select="$newurl"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- The parameter is not yet specified //-->
                <xsl:choose>
                    <xsl:when test="contains($url,'?')">
                        <!-- Other parameters are present //-->
                        <xsl:value-of select="concat($url,'&amp;',$par,'=',$value)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- No other parameter are present //-->
                        <xsl:value-of select="concat($url,'?',$par,'=',$value)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--
    Template: UrlGetParam
    synopsis: Gets the value of a given parameter from a specific URL
    param:

    url: URL containing the parameter and value
    par: name of the parameter
    -->
    <xsl:template name="UrlGetParam">
        <xsl:param name="url"/>
        <xsl:param name="par"/>
        <!-- There are two possibility for a parameter to appear in an URL:
        1.) after a ? sign
        2.) after a & sign
        In both cases the value is either limited by a & sign or the string end
        //-->
        <xsl:variable name="afterParam">
            <xsl:choose>
                <xsl:when test="contains($url,concat('?',$par,'='))">
                    <!-- Parameter is right after a question mark //-->
                    <xsl:value-of select="substring-after($url,concat('?',$par,'='))"/>
                </xsl:when>
                <xsl:when test="contains($url,concat('&amp;',$par,'='))">
                    <!-- Parameter is right after a & sign //-->
                    <xsl:value-of select="substring-after($url,concat('&amp;',$par,'='))"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- The parameter is not specified //-->
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="contains($afterParam,'&amp;')">
                <!-- cut off other parameters -->
                <xsl:value-of select="substring-before($afterParam,'&amp;')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$afterParam"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!--
    Template: UrlDelParam
    synopsis: Removes the parameter and value of a given parameter from a specific URL

    url: URL containing the parameter and value
    par: name of the parameter
    -->
    <xsl:template name="UrlDelParam">
        <xsl:param name="url"/>
        <xsl:param name="par"/>

        <xsl:choose>
            <xsl:when test="contains($url,concat($par,'='))">
                <!-- get value of par's value -->
                <xsl:variable name="valueOfPar">
                    <!-- cut off everything before value -->
                    <xsl:variable name="valueBlured" >
                        <xsl:choose>
                            <xsl:when test="contains($url,concat('?',$par,'=')) ">
                                <xsl:value-of select="substring-after($url,concat('?',$par,'='))" />
                            </xsl:when>
                            <xsl:when test="contains($url,concat('&amp;',$par,'='))">
                                <xsl:value-of select="substring-after($url,concat('&amp;',$par,'='))"/>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:choose>
                        <!-- found value is not the last one in $url -->
                        <xsl:when test="contains($valueBlured,'&amp;')">
                            <xsl:value-of select="substring-before($valueBlured,'&amp;')" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$valueBlured"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>              
                <xsl:variable name="parAndVal">
                    <xsl:value-of select="concat($par,'=',$valueOfPar)"/>
                </xsl:variable>

                <xsl:choose>
                    <!-- more params append afterwards -->                              
                    <xsl:when test="contains(substring-after($url,$parAndVal),'&amp;')">
                        <xsl:choose>
                            <!-- $par is not the first in list -->
                            <xsl:when 
                                                test="contains(substring($url,string-length(substring-before($url,$parAndVal)+1),string-length(substring-before($url,$parAndVal)+1)),'&amp;')">
                                <xsl:value-of 
                                                      select="concat(substring-before($url,concat('&amp;',$parAndVal)),substring-after($url,$parAndVal))"/>
                            </xsl:when>
                            <!-- $par is logical the first one in $url-->
                            <xsl:otherwise>
                                <xsl:value-of select="concat(substring-before($url,$parAndVal),substring-after($url,concat($parAndVal,'&amp;')))"/>
                            </xsl:otherwise>
                        </xsl:choose>           
                    </xsl:when>
                    <!-- no more params append afterwards -->
                    <xsl:otherwise>
                        <xsl:value-of select="substring($url,1, (string-length($url)-(string-length($parAndVal)+1))) "/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$url"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>      
      
    <!--
    Template: ShortenText
    synopsis: Cuts text after a maximum of given chars but at end of the word that would be affected. If the text is shortened "..." is appended.
    param:

    text: the text to be shorten
    length: the number of chars
    -->
    <xsl:template name="ShortenText">
        <xsl:param name="text"/>
        <xsl:param name="length"/>
        <xsl:choose>
            <xsl:when test="string-length($text) > $length">
                <xsl:value-of select="concat(substring($text,1,$length),substring-before(substring($text,($length+1)),' '),'...')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
      
    <!--
    Template: ClassCategLink
    synopsis: Generates a link to get a classification
    param:

    classid: classification id
    categid: category id
    host: host to query
    -->
    <xsl:template name="ClassCategLink">
        <xsl:param name="classid"/>
        <xsl:param name="categid"/>
        <xsl:param name="host"/>
        <xsl:value-of select="concat('query:type=class&amp;hosts=',$host,'&amp;query=%2Fmycoreclass%5B%40ID%3D%27',$classid,'%27%20and%20*%2Fcategory%2F%40ID%3D%27',$categid,'%27%5D')" />
    </xsl:template>
    
    <!--
    Template: ClassLink
    synopsis: Generates a link to get a classification
    param:

    classid: classification id
    host: host to query
    -->
    <xsl:template name="ClassLink">
        <xsl:param name="classid"/>
        <xsl:param name="host"/>
        <xsl:value-of select="concat('query:type=class&amp;hosts=',$host,'&amp;query=%2Fmycoreclass%5B%40ID%3D%27',$classid,'%27%5D')" />
    </xsl:template>
    <!--
    Template: PrivLink
    synopsis: Generates a link to get the privileges of the current user

    -->
    <xsl:template name="PrivLink">
        <xsl:value-of select="concat($WebApplicationBaseURL,'servlets/MCRUserPrivServlet',$JSessionID)" />
    </xsl:template>
    
    <!--
    Template: PrivOfUserLink
    synopsis: returns the privileges of the current user

    -->
    <xsl:template name="PrivOfUser">
        <xsl:variable name="link">
            <xsl:call-template name="PrivLink" />
        </xsl:variable>
        <xsl:copy-of select="document($link)/mycoreuserpriv/user" />
    </xsl:template>
    <!-- Template typeOfObjectID
    synopsis: returns the type of the ObjectID submitted usally the second part of the ID
    
    parameters:
    id: MCRObjectID
    -->
    <xsl:template name="typeOfObjectID">
        <xsl:param name="id" />
        <xsl:variable name="delim" select="'_'"/>
        <xsl:value-of select="substring-before(substring-after($id,$delim),$delim)"/>
    </xsl:template>
    <!-- Template mappedTypeOfObjectID
    synopsis: returns the mapped type of the ObjectID submitted usally the second part of the ID.
        Use this when calling an ObjectMetaData or ResultList page.
    
    parameters:
    id: MCRObjectID
    -->
    <xsl:template name="mappedTypeOfObjectID">
        <xsl:param name="id" />
        <xsl:variable name="type">
            <xsl:call-template name="typeOfObjectID">
                <xsl:with-param name="id" select="$id"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="mapping">
            <xsl:call-template name="getValue">
                <xsl:with-param name="pairs" select="$TypeMapping"/>
                <xsl:with-param name="name" select="$type"/>
            </xsl:call-template>
        </xsl:variable>
        <!-- the mapping -->
        <xsl:choose>
            <xsl:when test="string-length($mapping) > 0">
                <xsl:value-of select="$mapping"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$type"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Template getValue
    synopsis: returns the value of name value pairs in this layout:
        name1:value1;name2:value2;...;
    parameters:
    pairs: the name value pairs as described above
    name: a name to return the value for
    -->
    <xsl:template name="getValue">
        <xsl:param name="pairs"/>
        <xsl:param name="name"/>
        <xsl:if test="string-length($pairs) > string-length($name) and string-length($name) > 0">
            <xsl:value-of select="substring-before(substring-after($pairs,concat($name,':')),';')"/>
        </xsl:if>
    </xsl:template>

    <!-- Template selectLang
    synopsis: returns $CurrentLang if $nodes[lang($CurrentLang)] is not empty, else $DefaultLang

    parameters:
    nodes: the nodeset to check
    -->
    <xsl:template name="selectLang">
        <xsl:param name="nodes" />
        <xsl:choose>
            <xsl:when test="$nodes[lang($CurrentLang)]">
                <xsl:value-of select="$CurrentLang"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$DefaultLang"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Template selectPresentLang
    synopsis: returns the result of selectLang if nodes for that language are present, else returns a language for which nodes a present

    parameters:
    nodes: the nodeset to check
    -->
    <xsl:template name="selectPresentLang">
        <xsl:param name="nodes" />
        <xsl:variable name="check">
            <xsl:call-template name="selectLang">
                <xsl:with-param name="nodes" select="$nodes"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$nodes[lang($check)]">
                <xsl:value-of select="$check"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$nodes[1]/@xml:lang"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
 
    <!--
    Template: formatISODate
    synopsis: formates the given date (ISO 8601) to the defined local format
    param:

    date: date in ISO 8601 format
    format: target format (must suit to SimpleDateFormat)
    locale: use local, e.g. "de" "en"
    -->
    <xsl:template name="formatISODate">
        <xsl:param name="date"/>
        <xsl:param name="isoFormat" select="'YYYY-MM-DDThh:mm:ss.sTZD'"/>
        <xsl:param name="format"/>
        <xsl:param name="locale" select="$CurrentLang"/>
        <xsl:value-of xmlns:mcrxml="xalan://org.mycore.common.xml.MCRXMLFunctions" select="mcrxml:formatISODate( string( $date ),string( $isoFormat ),string( $format ),string( $locale ) )" />
    </xsl:template>
</xsl:stylesheet>

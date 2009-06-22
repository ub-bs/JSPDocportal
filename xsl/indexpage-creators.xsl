<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- DEPRECATED - no longer needed for index browser -->
<!-- ============================================== -->
<!-- $Revision: 1.12 $ $Date: 2006-11-30 17:21:10 $ -->
<!-- ============================================== -->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xalan"
>
<xsl:output 
  method="xml" 
  encoding="UTF-8" 
/>
<xsl:variable name="MainTitle" select="'Indexsuche '"/>
<xsl:variable name="PageTitle" select="'Autorenindex'"/>
<xsl:param name="WebApplicationBaseURL" />
<xsl:param name="prevFromTo"/> <!-- navigation path of previous fromTos in reverse order, separated by '.'-->

<!-- ========== Variablen ========== -->
<xsl:variable name="search"  select="/indexpage/results/@search" />
<xsl:variable name="mode"    select="/indexpage/results/@mode" />
<xsl:variable name="IndexID" select="/indexpage/index/@id" />

<!-- ======== headline ======== -->
<xsl:template name="index.headline">
  <tr valign="top">
    <td class="metaname">
      <xsl:value-of select="'Auswahl:'"/>
      <xsl:value-of select="$IndexTitle"/>
    </td>
  </tr>
</xsl:template>

<!-- ======== intro text ======== -->
<xsl:template name="index.intro">
  <tr>
    <td class="metavalue">
      <xsl:call-template name="IntroText" />
    </td>
  </tr>
</xsl:template>

<!-- ======== index search ======== -->
<xsl:template name="index.search">
  <tr>
    <td>
      <table border="0" cellpadding="0" cellspacing="0"><tr>
        <td class="metavalue">
          <form action="nav?path=~indexSearchCreators" method="post">
            <b>Index </b>
            <select name="mode" size="1" class="button">
              <option value="prefix">
                <xsl:if test="$mode = 'prefix'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>enthält</xsl:text>
              </option>
            </select>
            <xsl:text> </xsl:text>
            <input type="text" class="button" size="30" name="search" value="{$search}" />
            <xsl:text> </xsl:text>
            <input type="submit" class="button" value="suchen..." />
          </form>
        </td>
        <xsl:if test="string-length($search) &gt; 0 ">
          <td class="metavalue">
            <form action="nav?path=~indexSearchCreators" method="post">
              <b>
                <xsl:text> </xsl:text>
                <xsl:value-of select="/indexpage/results/@numHits"/>
                <xsl:text> Treffer </xsl:text>
              </b>
              <input type="submit" class="button" value="Filter aufheben" />
            </form>
           </td>
         </xsl:if>
       </tr></table>
    </td>
  </tr>
</xsl:template>

<!-- ======== indexpage ======== -->
<xsl:template match="indexpage">
 <table>
  <xsl:call-template name="index.headline" />
  <xsl:call-template name="index.intro" />
  <xsl:call-template name="index.search" />
  <xsl:apply-templates select="results" />
 </table>
</xsl:template>

<xsl:variable name="upFromTo">
	<xsl:choose>
		<xsl:when test="/indexpage/results/range[last()]/to/@pos + 1 - /indexpage/results/range[1]/from/@pos = /indexpage/results/@numHits">
			<xsl:text></xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="/indexpage/results/range[1]/from/@pos" />
			<xsl:text>-</xsl:text>
			<xsl:value-of select="/indexpage/results/range[last()]/to/@pos" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:variable name="up.url">
	<xsl:text>nav?path=~indexSearchCreators</xsl:text>
	<xsl:text>&amp;fromTo=</xsl:text>
	<xsl:value-of select="substring-before($prevFromTo, '.')" />
	<xsl:text>&amp;prevFromTo=</xsl:text>
	<xsl:value-of select="substring-after($prevFromTo, '.')" />
	<xsl:if test="string-length($search) &gt; 0">
	  	<xsl:text>&amp;search=</xsl:text>
   		<xsl:value-of select="$search" />
    </xsl:if>	
</xsl:variable> 

<!-- ========== results ========== -->
<xsl:template match="results">
  <tr>
    <td class="metavalue">
      <xsl:if test="range">
        <dl>
          <dt>
          <xsl:choose>
            <xsl:when test="contains(/indexpage/@path,'-')">
              <b><a class="nav" href="{$up.url}">Zurück...</a></b>
            </xsl:when>
            <xsl:when test="string-length($search) &gt; 0">
              <b>Gesamtindex (über Suchausdruck gefiltert)</b>
            </xsl:when>
            <xsl:otherwise>
              <b>Gesamtindex</b>
            </xsl:otherwise>
          </xsl:choose>

          </dt>
          <xsl:apply-templates select="range" />
        </dl>
      </xsl:if>
      <xsl:if test="value">
        <dl>
          <dt>
          <xsl:choose>
            <xsl:when test="contains(/indexpage/@path,'-')">
              <b><a class="nav" href="{$up.url}">Zurück...</a></b>
            </xsl:when>
            <xsl:when test="string-length($search) &gt; 0">
              <b>Einträge (über Suchausdruck gefiltert)</b>
            </xsl:when>
          </xsl:choose>

          </dt>
          <dd>
		  <table border="0" cellpadding="0" cellspacing="0" style="padding-bottom:5px">
           <xsl:apply-templates select="value" />
		  </table></dd>
        </dl>
      </xsl:if>
    </td>
  </tr>
</xsl:template>

<!-- ========== value ========== -->
<xsl:template match="value">
  
  <xsl:variable name="urlAuthor">
    <xsl:text>nav?path=left.search.persons.docdetail</xsl:text>
	<xsl:text>&amp;id=</xsl:text>
    <xsl:value-of select="./id" />
  </xsl:variable>

  <xsl:variable name="offset">
   <xsl:text>&amp;offset=</xsl:text>
   <xsl:value-of select="@pos" />
  </xsl:variable>
	
    <tr>
      <td class="td1" valign="top">
        <img border="0" src="images/folder_plain.gif"/>
      </td>
      <td class="td1" valign="top" style="padding-right:5px;">
      <xsl:value-of select="col[@name='fullname']" />
   <!--     <xsl:value-of select="col[@name='surname']" />   
        <xsl:variable name="secondnamepart">
          <xsl:value-of select="concat(col[@name='academic'],' ', col[@name='firstname'], ' ', col[@name='prefix'], ' ', col[@name='peerage'])" />   
        </xsl:variable>
        <xsl:if test="string-length($secondnamepart) &gt; 1">
          <xsl:text>, </xsl:text>   
          <xsl:value-of select="$secondnamepart" />           
        </xsl:if> -->
	  </td>
      <td class="td1" valign="top" style="padding-right:5px;">
        <a href="{$urlAuthor}{$offset}" ><xsl:text> [Detailansicht] </xsl:text></a>
      </td>
	</tr>
		
</xsl:template>

<!-- ========== range ========== -->
<xsl:template match="range">
  <xsl:variable name="url">
    <xsl:value-of select="concat($WebApplicationBaseURL,'nav?path=~indexSearchCreators&amp;fromTo=', from/@pos,'-', to/@pos, '&amp;prevFromTo=',$upFromTo,'.',$prevFromTo)" />	
    <xsl:if test="string-length($search) &gt; 0">
      <xsl:text>&amp;search=</xsl:text>		
      <xsl:value-of select="$search" />
    </xsl:if>
  </xsl:variable>

  <dd>
    <img border="0" src="{$WebApplicationBaseURL}images/folder_plus.gif" align="middle"/>
    <xsl:text>  </xsl:text>
    <a href="{$url}" class="nav">
      <xsl:value-of select="from/@short"/>
      <xsl:text> - </xsl:text>
      <xsl:value-of select="to/@short" />
    </a>
  </dd>
</xsl:template>


<!-- ========== Titel ========== -->
<xsl:variable name="IndexTitle" select="'Personen von A - Z'" />

<!-- ========== Einleitender Text ========== -->
<xsl:template name="IntroText">
Dieser Index enthält die Namen der Autoren, die Dokumente publiziert haben.
<br/><br/>
<xsl:text> | </xsl:text>
<xsl:for-each select="xalan:nodeset($AtoZ)/search">
  <a href="{$WebApplicationBaseURL}nav?path=~indexSearchCreators&amp;search={@prefix}">
  <!-- a href="{$WebApplicationBaseURL}index/{$IndexID}/index.html?mode=prefix&amp;search={@prefix}" -->
    <xsl:value-of select="@prefix" />
  </a>
  <xsl:text> | </xsl:text>
</xsl:for-each>

</xsl:template>

<xsl:variable name="AtoZ">
  <search prefix="A" />
  <search prefix="B" />
  <search prefix="C" />
  <search prefix="D" />
  <search prefix="E" />
  <search prefix="F" />
  <search prefix="G" />
  <search prefix="H" />
  <search prefix="I" />
  <search prefix="J" />
  <search prefix="K" />
  <search prefix="L" />
  <search prefix="M" />
  <search prefix="N" />
  <search prefix="O" />
  <search prefix="P" />
  <search prefix="Q" />
  <search prefix="R" />
  <search prefix="S" />
  <search prefix="T" />
  <search prefix="U" />
  <search prefix="V" />
  <search prefix="W" />
  <search prefix="X" />
  <search prefix="Y" />
  <search prefix="Z" />
</xsl:variable>

</xsl:stylesheet>



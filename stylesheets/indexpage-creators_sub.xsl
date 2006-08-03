<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision: 1.4 $ $Date: 2006-08-03 11:34:08 $ -->
<!-- ============================================== -->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xalan"
>
<xsl:param name="subselect.webpage" />
<xsl:param name="WebApplicationBaseURL" />
<xsl:param name="ServletsBaseURL" />
<!-- ========== Subselect Parameter ========== -->
<xsl:param name="subselect.varpath" />
<xsl:param name="subselect.session" />

		
<!-- ========== Variablen ========== -->
<xsl:variable name="search"  select="/indexpage/results/@search" />
<xsl:variable name="mode"    select="/indexpage/results/@mode" />
	
	
<xsl:variable name="subselect.params">
  <xsl:text>XSL.subselect.session=</xsl:text>
  <xsl:value-of select="$subselect.session" />
  <xsl:text>&amp;XSL.subselect.varpath=</xsl:text>
  <xsl:value-of select="$subselect.varpath" />
  <xsl:text>&amp;XSL.subselect.webpage=</xsl:text>
  <xsl:value-of select="$subselect.webpage" />
</xsl:variable>

<!-- ======== headline ======== -->
<xsl:template name="index.headline">
  <tr valign="top">
    <td class="metaname">
      <xsl:value-of select="'Auswahl:'"/>
      <xsl:value-of select="$IndexTitle"/>
	  <br/>
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
          <form action="{$WebApplicationBaseURL}nav?path=~indexSearchCreatorsSub" method="post">
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
            <input type="hidden" name="XSL.subselect.session" value="{$subselect.session}" />
            <input type="hidden" name="XSL.subselect.varpath" value="{$subselect.varpath}" />
            <input type="hidden" name="XSL.subselect.webpage" value="{$subselect.webpage}" />
            <input type="submit" class="button" value="suchen..." />
          </form>
        </td>
        <xsl:if test="string-length($search) &gt; 0 ">
          <td class="metavalue">
            <form action="{$WebApplicationBaseURL}nav?path=~indexSearchCreatorsSub" method="post">
              <b>
                <xsl:text> </xsl:text>
                <xsl:value-of select="results/@numHits"/>
                <xsl:text> Treffer </xsl:text>
              </b>
              <input type="hidden" name="XSL.subselect.session" value="{$subselect.session}" />
              <input type="hidden" name="XSL.subselect.varpath" value="{$subselect.varpath}" />
              <input type="hidden" name="XSL.subselect.webpage" value="{$subselect.webpage}" />
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

<xsl:variable name="up.url">
  <xsl:text>{$WebApplicationBaseURL}nav?path=~indexSearchCreatorsSub</xsl:text>
  <xsl:text>?</xsl:text>
  <xsl:value-of select="$subselect.params" />
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
            <img border="0" src="{$WebApplicationBaseURL}/images/folder_open.gif" align="middle"/>

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
            <img border="0" src="{$WebApplicationBaseURL}/images/folder_open.gif" align="middle"/>

          <xsl:choose>
            <xsl:when test="contains(/indexpage/@path,'-')">
              <b><a class="nav" href="{$up.url}">Zurück...</a></b>
            </xsl:when>
            <xsl:when test="string-length($search) &gt; 0">
              <b>Einträge (über Suchausdruck gefiltert)</b>
            </xsl:when>
          </xsl:choose>

          </dt>
          <dd><table border="0" cellpadding="0" cellspacing="0" style="padding-bottom:5px">
          <xsl:apply-templates select="value" /></table></dd>
        </dl>
      </xsl:if>
    </td>
  </tr>
</xsl:template>

<!-- ========== value ========== -->
<xsl:template match="value">
  <xsl:variable name="url">
    <xsl:value-of select="$ServletsBaseURL" />
    <xsl:text>XMLEditor?_action=end.subselect</xsl:text>
    <xsl:text>&amp;subselect.session=</xsl:text>
    <xsl:value-of select="$subselect.session" />
    <xsl:text>&amp;subselect.varpath=</xsl:text>
    <xsl:value-of select="$subselect.varpath" />
    <xsl:text>&amp;subselect.webpage=</xsl:text>
    <xsl:value-of select="$subselect.webpage" />
  </xsl:variable>

   <xsl:variable name="title">
       <xsl:value-of select="col[@name='fullname']" />   
   </xsl:variable>
   
    <tr>
      <td class="td1" valign="top">
        <img border="0" src="{$WebApplicationBaseURL}/images/folder_plain.gif"/>
      </td>
      <td class="td1" valign="top" style="padding-right:5px;">
        <a href="{$url}&amp;_var_@href={col[@name='id']}&amp;_var_@title={$title}">
          <xsl:value-of select="$title" />
        </a>
      </td>
    </tr>
</xsl:template>

<!-- ========== range ========== -->
<xsl:template match="range">
  <xsl:variable name="url">
    <xsl:value-of select="concat($WebApplicationBaseURL,'nav?path=~indexSearchCreatorsSub&amp;fromTo=', from/@pos,'-', to/@pos )" />	  
    <xsl:text>?</xsl:text>
    <xsl:value-of select="$subselect.params" />
    <xsl:if test="string-length($search) &gt; 0">
      <xsl:text>&amp;search=</xsl:text>
      <xsl:value-of select="$search" />
      <xsl:text>&amp;mode=</xsl:text>
      <xsl:value-of select="$mode" />
    </xsl:if>
  </xsl:variable>

  <dd>
    <img border="0" src="{$WebApplicationBaseURL}/images/folder_closed_in_use.gif" align="middle"/>
    <xsl:text> </xsl:text>
    <a href="{$url}" class="nav">
      <xsl:value-of select="from/@short"/>
      <xsl:text> - </xsl:text>
      <xsl:value-of select="to/@short" />
    </a>
  </dd>
</xsl:template>

<!-- ========== Titel ========== -->
<xsl:variable name="IndexTitle" select="'Autoren von A - Z'" />

<!-- ========== Einleitender Text ========== -->
<xsl:template name="IntroText">
Dieser Index enthält die Namen von Personen, die Autoren, Herausgeber oder sonstige Beitragende zu @libri Dokumenten sind.
<br/><br/>
<form action="{$WebApplicationBaseURL}{$subselect.webpage}?XSL.editor.session.id={$subselect.session}" method="post">
  <input type="submit" class="submit" value="Auswahl abbrechen" />
</form>
<br/><br/>
<xsl:text> | </xsl:text>
<xsl:for-each select="xalan:nodeset($AtoZ)/search">
 <!-- a href="{$WebApplicationBaseURL}index/{$IndexID}/index.html?mode=prefix&amp;search={@prefix}&amp;{$subselect.params}" -->
 <a href="{$WebApplicationBaseURL}nav?path=~indexSearchCreatorsSub&amp;search={@prefix}&amp;{$subselect.params}">
 	  
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



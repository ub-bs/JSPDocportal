<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- ============================================== -->
<!-- $Revision: 1.2 $ $Date: 2006-05-30 06:08:35 $ -->
<!-- ============================================== -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="xlink" >
	<xsl:variable name="MainTitle" select="'Dissertation abgeben und Abgabeformular erzeugen'"/>
	<xsl:variable name="PageTitle" select="'Dissertation abgeben und Abgabeformular erzeugen'"/>
	<xsl:variable name="SearchMask.Search" select="'Suche starten'"/>
	<xsl:variable name="SearchMask.Reset" select="'Eingabe löschen'"/>
	<xsl:variable name="Servlet" select="'ATLDisshabServlet'"/>
	<xsl:include href="MyCoReLayout.xsl" />
	<xsl:param name="WebApplicationBaseURL" />

<xsl:variable name="OMD.ID" select="'ID'" />
<xsl:variable name="OMD.Title" select="'Erklärung zur Abgabe digitaler Dissertationen'" />
<xsl:variable name="OMD.Title2" select="'Angaben zu Dissertation und Rigorosum/Disputation '" />
<xsl:variable name="OMD.Title.Main" select="'Haupttitel'" />
<xsl:variable name="OMD.Title.Sub" select="'weiterer Titel'" />
<xsl:variable name="OMD.Creatorlink" select="'Personalien'" />
<xsl:variable name="OMD.Contrib" select="'Beteiligte'" />
<xsl:variable name="OMD.Contrib.referee" select="'Gutachter'" />
<xsl:variable name="OMD.Contrib.advisor" select="'Betreuer'" /> 
<xsl:variable name="OMD.Origin" select="'Fachbereich'" />
<xsl:variable name="OMD.Descr" select="'Beschreibung'" />
<xsl:variable name="OMD.Abstract" select="'Kurzfassung'" />
<xsl:variable name="OMD.Content" select="'Inhalt'" />
<xsl:variable name="OMD.Date" select="'Datum'" />
<xsl:variable name="OMD.Date.c" select="'Erzeugt am'" />
<xsl:variable name="OMD.Date.s" select="'Eingereicht am'" />
<xsl:variable name="OMD.Date.a" select="'Verteidigt am'" />
<xsl:variable name="OMD.Date.d" select="'Beschlossen am'" />
<xsl:variable name="OMD.Subject" select="'Metadaten (Schlagworte/Stichworte): '" />
<xsl:variable name="OMD.Type" select="'Typ'" />
<xsl:variable name="OMD.Format" select="'Format'" />
<xsl:variable name="OMD.Keywords" select="'Schl&#252;sselworte'" />
<xsl:variable name="OMD.Publisher" select="'Publizist'" />
<xsl:variable name="OMD.Ident" select="'Identifizier'" />
<xsl:variable name="OMD.Source" select="'Quelle'" />
<xsl:variable name="OMD.Coverage" select="'Erstreckung'" />
<xsl:variable name="OMD.Relation" select="'Relation'" />
<xsl:variable name="OMD.Rights" select="'Rechte'" />
<xsl:variable name="OMD.Size" select="'Umfang'" />
<xsl:variable name="OMD.Note" select="'Hinweis'" />
<xsl:variable name="OMD.Citation" select="'Zitiervorlage'" />
<xsl:variable name="OMD.Created" select="'Eingestellt am'" />
<xsl:variable name="OMD.Changed" select="'Letzte Änderung'" />
<xsl:variable name="OMD.Derivates" select="'Dokumente'" />

<xsl:variable name="OMD.Person" select="'Person'" />
<xsl:variable name="OMD.Female" select="'Geschlecht'" />
<xsl:variable name="OMD.Female.w" select="'weiblich'" />
<xsl:variable name="OMD.Female.m" select="'männlich'" />
<xsl:variable name="OMD.Institution" select="'Institution'" />
<xsl:variable name="OMD.Address" select="'Anschrift'" />
<xsl:variable name="OMD.Address.o" select="'dienstlich'" />
<xsl:variable name="OMD.Address.p" select="'privat'" />
<xsl:variable name="OMD.Dates" select="'Daten'" />
<xsl:variable name="OMD.Dates.b" select="'Geburtstag'" />
<xsl:variable name="OMD.Phones" select="'Telefon'" />
<xsl:variable name="OMD.Phones.f" select="'Fax'" />
<xsl:variable name="OMD.Phones.p" select="'Telefon'" />
<xsl:variable name="OMD.Profession" select="'Beruf'" />
<xsl:variable name="OMD.Profession.j" select="'Tätigkeit'" />
<xsl:variable name="OMD.Profession.p" select="'Beruf'" />
<xsl:variable name="OMD.ProfClass" select="'Berufsklasse'" />
<xsl:variable name="OMD.National" select="'Nationalität'" />
<xsl:variable name="OMD.Webs" select="'URL'" />
<xsl:variable name="OMD.Emails" select="'eMail'" />

   <xsl:template match="/">
	 <html>
      		<xsl:call-template name="header" />
	  <body>
		<table>
		 <tr><td>
		 <table class="caro" width="550" >
		 <tr><td>
			<xsl:call-template name="firsttext" />
		 </td></tr>
		 <tr><td>
			<xsl:apply-templates select="/mcr_results" />
		  </td></tr>
		  <tr><td height="50" valign="bottom">
			<xsl:call-template name="lasttext" />
		  </td></tr>
		  </table>
		</td>
		<td valign="top" ><img border="0" valign="top" src="{$WebApplicationBaseURL}/images/print.gif" />
		</td>
		</tr>
		</table>
			
	  </body>	
	 </html>
   </xsl:template>
   
<xsl:template name="header">
	<head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8"></meta>
		<title><xsl:value-of select="$MainTitle" /> @<xsl:value-of select="$PageTitle" /></title>
		<link href="{$WebApplicationBaseURL}modules/module-wcms/uif/templates/master/template_print/CSS/atlibri.css" rel="stylesheet" type="text/css"/>
		<link href="{$WebApplicationBaseURL}modules/module-wcms/uif/templates/master/template_print/CSS/style_general.css"	rel="stylesheet" type="text/css"/>
		<link href="{$WebApplicationBaseURL}modules/module-wcms/uif/templates/master/template_print/CSS/style_navigation.css" rel="stylesheet" type="text/css"/>
		<link href="{$WebApplicationBaseURL}modules/module-wcms/uif/templates/master/template_print/CSS/style_content.css" rel="stylesheet" type="text/css"/>			
	</head>
</xsl:template>   	
	
	
<!-- Metadata from the Dissertation  ******************************** -->
<xsl:template match="/mcr_results">
  <xsl:variable name = "obj_host" ><xsl:value-of select="./mcr_result/@host"/></xsl:variable>
  
  <table  width="550" >
   <tr><td valign="top" class="OULine" colspan="2"><b><xsl:value-of select="$OMD.Title" /></b>
	</td> </tr>
   
   <xsl:call-template name="print_metagen">
          <xsl:with-param name="obj_host" select="$obj_host"/>
          <xsl:with-param name="accessedit" select="'true'"/>
   </xsl:call-template>
 	<!--
    <xsl:apply-templates select="creatorlinks"/>
    <xsl:apply-templates select="./metadata/titles"/>
    <xsl:apply-templates select="./metadata/origins"/>
    <xsl:apply-templates select="./metadata/contributors"/>
    <xsl:apply-templates select="./metadata/subjects"/>
    <xsl:apply-templates select="./metadata/keywords"/>
    <xsl:apply-templates select="./metadata/types"/>
   </xsl:for-each>	
   	-->
 	
 </table>

	<br />    
</xsl:template>
	
<xsl:template name="print_metagen">
 <xsl:param name="obj_host"/>
 <xsl:param name="accessedit"/>
 <xsl:variable name="metaData.Titles" select="/mcr_results/mcr_result/mycoreobject/metadata/titles"/>
 <xsl:variable name="metaData.Contrib" select="/mcr_results/mcr_result/mycoreobject/metadata/contributors"/>
 <xsl:variable name="metaData.Date" select="/mcr_results/mcr_result/mycoreobject/metadata/dates"/>
 
    <xsl:if test="./mcr_result/mycoreobject/metadata/creatorlinks">
 	<xsl:for-each select="./mcr_result/mycoreobject/metadata/creatorlinks/creatorlink">
		<xsl:variable name="id" select="@xlink:href"/>	
		<xsl:variable name="personlink">
            <xsl:call-template name="personxml">
                <xsl:with-param name="type" select="'allpers'"/>
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="host" select="$obj_host"/>
            </xsl:call-template>
        </xsl:variable> 
		<tr>
		<td class="metaname"><xsl:value-of select="$OMD.Creatorlink" /></td>
		<td class="metavalue">		
				<xsl:apply-templates select="document($personlink)/mcr_results/mcr_result/mycoreobject/metadata/names" />
		<br/>
		</td>
		</tr>
		<tr>
		<td class="metaname"><xsl:value-of select="$OMD.Institution" /></td>
		<td class="metavalue">		
         <xsl:call-template name="printClass">
           <xsl:with-param name="nodes" select="document($personlink)/mcr_results/mcr_result/mycoreobject/metadata/institutions/institution"/>
           <xsl:with-param name="host" select="$obj_host"/>
         </xsl:call-template>
		</td>
		</tr>
		<xsl:call-template name="adresse">
           <xsl:with-param name="nodes" select="document($personlink)/mcr_results/mcr_result/mycoreobject/metadata/addresses/address"/>
           <xsl:with-param name="host" select="$obj_host"/>          
		</xsl:call-template>
	</xsl:for-each>
	</xsl:if>	
    <tr> <td colspan="2"><b><xsl:value-of select="$OMD.Title2" /></b></td></tr>
	<xsl:if test="./mcr_result/mycoreobject/metadata/titles">
		<xsl:apply-templates select="$metaData.Titles"/>
	</xsl:if>
	<xsl:if test="./mcr_result/mycoreobject/metadata/origins">
	 <tr>
      <td valign="top" class="metaname"><xsl:copy-of select="$OMD.Origin"/>:</td>
      <td class="metavalue" >
        <xsl:call-template name="printClass">
            <xsl:with-param name="nodes" select="./mcr_result/mycoreobject/metadata/origins/origin"/>
            <xsl:with-param name="host" select="$obj_host"/>
        </xsl:call-template>
      </td>
	  </tr>
	</xsl:if>
	<xsl:if test="./mcr_result/mycoreobject/metadata/contributors">
        <xsl:apply-templates select="$metaData.Contrib"/>
 	</xsl:if>
	<xsl:if test="./mcr_result/mycoreobject/metadata/dates">
        <xsl:apply-templates select="$metaData.Date"/>
 	</xsl:if>	
	<tr>
		<td class="metaname"><xsl:value-of select="$OMD.Subject" /></td>
		<td class="metavalue" >		
         <xsl:call-template name="printClass">
           <xsl:with-param name="nodes" select="./mcr_result/mycoreobject/metadata/subjects/subject"/>
           <xsl:with-param name="host" select="$obj_host"/>
         </xsl:call-template>
		</td>
	</tr>
</xsl:template>

<xsl:template name="adresse">
	<xsl:param name="nodes"/>
    <xsl:param name="host"/>
    <xsl:for-each select="$nodes">
	<!-- Adresses ********************************************* -->
     <tr>
       <td class="metaname" ><xsl:value-of select="$OMD.Address"/>:</td>
       <td class="metavalue">
        <xsl:variable name="selectLang">
          <xsl:call-template name="selectLang">
             <xsl:with-param name="nodes" select="$nodes"/>
          </xsl:call-template>
        </xsl:variable>
        <div class="addressBox">
          <div class="addressType">
             <xsl:choose>
              <xsl:when test="@type = 'office'"><xsl:value-of select="$OMD.Address.o" /></xsl:when>
              <xsl:when test="@type = 'private'"><xsl:value-of select="$OMD.Address.p" /></xsl:when>
              <xsl:otherwise><xsl:value-of select="@type" /></xsl:otherwise>
             </xsl:choose>
          </div>
          <div class="address">
            <xsl:if test="street">
              <xsl:value-of select="street" /><xsl:text> </xsl:text>
            </xsl:if>
            <xsl:if test="number"><xsl:value-of select="number" /><br /></xsl:if>
            <xsl:if test="zipcode"><xsl:value-of select="zipcode" />
				<xsl:text> </xsl:text>
            </xsl:if>
            <xsl:if test="city"><xsl:value-of select="city" /><br /></xsl:if>
            <xsl:if test="state"><xsl:value-of select="state" />
				<xsl:text>, </xsl:text>
            </xsl:if>
            <xsl:if test="country"><xsl:value-of select="country" /></xsl:if>
          </div>
         </div>
        </td>
       </tr>
	</xsl:for-each>
</xsl:template>

<!-- Titles from a Document ************************************** -->
<xsl:template match="titles">
  <xsl:variable name="selectLang" >
   <xsl:choose>
    <xsl:when test="title[lang($CurrentLang) and @inherited = '0']" > 
    	<xsl:value-of select="$CurrentLang" /> </xsl:when>
    <xsl:otherwise> <xsl:value-of select="$DefaultLang" /> </xsl:otherwise>
   </xsl:choose>
  </xsl:variable>	
  <xsl:for-each select="title[lang($selectLang) and @inherited = '0']" >
  <tr>
    <td valign="top" class="metaname">
    <xsl:choose>
      <xsl:when test="@type = 'main'"><xsl:value-of select="$OMD.Title.Main"/>:</xsl:when>
      <xsl:when test="@type = 'sub'"><xsl:value-of select="$OMD.Title.Sub"/>:</xsl:when>
    </xsl:choose>
    </td>
    <td class="metavalue"><xsl:value-of select="text()" /></td>
   </tr>
  </xsl:for-each>
</xsl:template>

<!-- DC 10 *** Contributor *************************************** -->
<!-- DC 11 *** ContributorLink *********************************** -->
<xsl:template match="contributors">
    <tr>
     <td class="metaname"><xsl:copy-of select="$OMD.Contrib"/>:</td>
     <td class="metavalue">
		 <xsl:variable name="selectLang">
            <xsl:choose>
                <xsl:when test="contributor[lang($CurrentLang)]">
                    <xsl:value-of select="$CurrentLang"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$DefaultLang"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
		<xsl:for-each select="contributor[lang($selectLang)]" >
            <xsl:choose>
                <xsl:when test="@type = 'referee'">
                    <xsl:copy-of select="$OMD.Contrib.referee" />:
                </xsl:when>
                <xsl:when test="@type = 'advisor'">
                    <xsl:copy-of select="$OMD.Contrib.advisor" />:
                </xsl:when>
            </xsl:choose>
            <xsl:value-of select="fullname" /><br/>     
		</xsl:for-each>	
		</td>
    </tr>
 </xsl:template>

<!--    05 *** Origin ********************************************* -->
<xsl:template match="//metadata/origins">
  <xsl:if test="./mcr_result/mycoreobject/metadata/origins">
    <tr>
      <td valign="top" class="metaname"><xsl:copy-of select="$OMD.Origin"/>:</td>
      <td class="metavalue">
        <xsl:call-template name="printClass">
            <xsl:with-param name="nodes" select="./mcr_result/mycoreobject/metadata/origins/origin"/>
            <xsl:with-param name="host"  select="local"/>
        </xsl:call-template>
      </td> 
    </tr> 
  </xsl:if>
</xsl:template>

<!-- DC 12 *** Date ********************************************** -->
<xsl:template match="dates">
    <tr>
      <td valign="top" class="metaname"><xsl:copy-of select="$OMD.Date"/>:</td> 
      	<td class="metavalue"> 
	  <xsl:variable name="selectLang"> 
	    <xsl:choose> 
	     <xsl:when test="date[lang($CurrentLang)]"> 
	      <xsl:value-of select="$CurrentLang"/>
	     </xsl:when> 
	     <xsl:otherwise> <xsl:value-of select="$DefaultLang"/> </xsl:otherwise> 
	     </xsl:choose> 
	  </xsl:variable> 
	<table>
	  <xsl:for-each select="date[lang($selectLang)]" > 
		<tr><td>
		 <xsl:choose> 
	      <xsl:when test="@type = 'create'"> <xsl:copy-of select="$OMD.Date.c" />:  </xsl:when> 
	      <xsl:when test="@type = 'submitted'"> <xsl:copy-of select="$OMD.Date.s" />:  </xsl:when> 
	      <xsl:when test="@type = 'accepted'"> <xsl:copy-of select="$OMD.Date.a" />:  </xsl:when> 
	      <xsl:when test="@type = 'decide'"> <xsl:copy-of select="$OMD.Date.d" />:  </xsl:when>
		  <xsl:otherwise><xsl:value-of select="@type" />: </xsl:otherwise>
        </xsl:choose> 
		</td>
		<td>	   <xsl:value-of select="." /> </td>
		</tr>
	 </xsl:for-each>
	 </table> 
     </td> 
    </tr> 
 
</xsl:template>

<!-- DC 04 *** Subject ******************************************** -->
<xsl:template match="//metadata/subjects">
  <xsl:if test="./mcr_result/mycoreobject/metadata/subjects"> 
  <tr> 
    <td valign="top" class="metaname"><xsl:copy-of select="$OMD.Subject"/>:</td>
    <td class="metavalue"> 
        <xsl:call-template name="printClass"> 
	  <xsl:with-param name="nodes" select="./mcr_result/mycoreobject/metadata/subjects/subject"/>
          <xsl:with-param name="host" select="localhost"/> 
	</xsl:call-template>
    </td> </tr> 
  </xsl:if>
</xsl:template>

<!-- DC 14 *** Format  ******************************************** -->
<xsl:template match="//metadata/formats"> 
 <xsl:if test="./mcr_result/mycoreobject/metadata/formats">
  <tr> 
    <td valign="top" class="metaname"><xsl:copy-of select="$OMD.Format"/>:</td> 
    <td class="metavalue">
        <xsl:call-template name="printClass"> 
	    <xsl:with-param name="nodes" select="./mcr_result/mycoreobject/metadata/formats/format"/>
            <xsl:with-param name="host" select="local"/> 
	</xsl:call-template> 
  </td></tr>
  </xsl:if>
</xsl:template>
  
<!-- DC 19 *** Keywords ****************************************** -->

<xsl:template match="//metadata/keywords">
  <xsl:if test="./mcr_result/mycoreobject/metadata/keywords">
    <tr>
      <td valign="top" class="metaname"><xsl:copy-of select="$OMD.Keywords"/>:</td>
      <td class="metavalue">
        <xsl:call-template name="printI18N">
            <xsl:with-param name="nodes" select="./mcr_result/mycoreobject/metadata/keywords/keyword"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:if>
</xsl:template>

<!-- erster Text ******************************************************* -->
<xsl:template name="firsttext">
  <table width="100%">
  <tr>
  <td> <h3>Digitale Dissertationen der Universität Rostock</h3> </td>
  </tr>
  <tr>
  <td> Erklärung zur Abgabe digitaler Dissertationen </td>
  </tr>
  <tr>
  <td valign="top" class="OULine"> <b>Original </b>  </td></tr>
  <tr>
	<td>
    <div class="textblock1">   Die Doktorandin/der Doktorand versichert hiermit, dass die in der Universitätsbibliothek
	abgegebene digitale Dissertation mit dem vom Promotionsausschuss genehmigten 
	Prüfungsexemplar übereinstimmt und formal den Bestimmungen der Promotionsordnung 
	entspricht. Die Drucklegung der Dissertation wurde vom Promotionsausschuss genehmigt. Die 
	Publikation enthält eine Zusammenfassung in deutscher Sprache. 
	Datenfomate, Datenträger und die Metadaten sowie die Anzahl der abzugebenden 
	unentgeltlichen Exemplare in Papier- und elektronischer Form entsprechen den Vorgaben der 
	Universitätsbibliothek. 
	<br/>
	Die abgelieferte digitale Version wurde von der Doktorandin/vom Doktorand auf Vollständigkeit 
	und Lesbarkeit geprüft. 
	</div>
  </td></tr>
  <tr>
  <td valign="top" class="metaname"> <b>Zugänglichkeit in Datennetzen </b> 
  </td></tr>
  <tr>
	<td>
    <div class="textblock1"> Die Doktorandin/der Doktorand gestattet der Universität, an der die Dissertation eingereicht 
	wurde bzw. der Universitätsbibliothek, Kopien der digitalen Dissertation zu erstellen, und diese 
	unter Verwendung der Metadaten auf ihren Servern für die Benutzung bereitzustellen, ggf. in 
	andere Formate zu konvertieren und ihre bibliographischen Daten und ggf. das Abstract 
	Datenbanken zugänglich zu machen. Die elektronische Dissertation wird archiviert und im 
	Internet publiziert, solange dies technisch und mit vertretbarem Aufwand möglich ist. 
	Die Doktorandin/der Doktorand räumt der Universitätbibliothek das einfache 
	Nutzungsrecht ein, die elektronische Dissertation in Datennetzen öffentlich wiederzugeben und 
	auf Einzelabruf zu übertragen.<br/> 
	Der Lieferung der digitale Dissertation an Die Deutsche Bibliothek und der dortigen 
	Speicherung zwecks Archivierung und Zurverfügungstellung nach geltendem Recht sowie ggf. 
	an die Sondersammelgebietsbibliothek zu gleichen Konditionen stimmt die Doktorandin/der 
	Doktorand zu. <br/>
	Die Universitätsbibliothek stellt eine Bescheinigung über das Erbringen der Publikationspflicht 
	aus. 
	</div>
  </td></tr>
  <tr>
  <td valign="top" class="metaname"> <b>Rechte Dritter </b>  </td></tr>
  <tr>
	<td>
	<div class="textblock1"> Die Doktorandin/der Doktorand versichert, dass mit der elektronischen Publikation der 
	Dissertation keine Rechte Dritter verletzt werden und dass die Hochschule von etwaigen 
	Rechten Dritter freigestellt wird. </div>
  </td></tr>
  <tr>
  <td valign="top" class="metaname"> <b>Datenschutz </b>  </td></tr>
  <tr>
	<td>
	<div class="textblock1"> Die Doktorandin/der Doktorand ist damit einverstanden, dass ihre/seine persönlichen Daten 
	gem. Promotionsordnung maschinell gespeichert und zusammen mit der Dissertation 
	bereitgestellt werden. </div>
  </td></tr>
  </table>
 </xsl:template>

 <xsl:template name="lasttext">
  <table border="0" width="100%">
  <tr><td></td></tr>	
  <tr><td>
	<u><b>Ort</b></u>
      </td>
      <td>
	<u><b>Datum</b></u>
      </td>
      <td>
	<u><b>Unterschrift</b></u>
      </td>
   </tr>
  </table>
 </xsl:template>
</xsl:stylesheet>

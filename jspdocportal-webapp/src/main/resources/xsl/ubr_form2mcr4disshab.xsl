<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:pc="http://www.d-nb.de/standards/pc/">
  <xsl:output method="xml" indent="yes" />
  <xsl:param name="formData" />
  <xsl:variable name="props" select="$formData/properties" />

  <xsl:template match="/mycoreobject">
    <xsl:copy>
      <xsl:copy-of select="./@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>


  <xsl:template match="/mycoreobject/structure">
    <xsl:copy-of select="." />
  </xsl:template>

  <xsl:template match="/mycoreobject/metadata">
    <xsl:copy>
      <xsl:copy-of select="./urns" />
      <xsl:copy-of select="./identifiers" />
      <!-- <xsl:copy-of select="$props" /> -->

      <titles class="MCRMetaLangText" heritable="true" notinherit="false">
        <xsl:if test="$props/entry[@key='titel_main']/text()">
            <title type="original-main" inherited="0" form="plain">
                <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='titel_main_lang']" /></xsl:attribute>
                <xsl:value-of select="$props/entry[@key='titel_main']" />
            </title>
        </xsl:if>
        <xsl:if test="$props/entry[@key='titel_sub']/text()">
          <title type="original-sub" inherited="0" form="plain">
            <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='titel_sub_lang']" /></xsl:attribute>
            <xsl:value-of select="$props/entry[@key='titel_sub']" />
          </title>
        </xsl:if>
        <xsl:if test="$props/entry[@key='titel_translated_main']/text()">
            <title type="translated-main" inherited="0" form="plain">
              <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='titel_translated_main_lang']" /></xsl:attribute>
              <xsl:value-of select="$props/entry[@key='titel_translated_main']" />
            </title>
        </xsl:if>
        <xsl:if test="$props/entry[@key='titel_translated_sub']/text()">
            <title type="translated-sub" inherited="0" form="plain">
              <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='titel_translated_sub_lang']" /></xsl:attribute>
              <xsl:value-of select="$props/entry[@key='titel_translated_sub']" /></title>
        </xsl:if>
      </titles>
    
      <xsl:if test="$props/entry[@key='verfasser_nachname']/text()">
        <xsl:variable name="gnd">
          <xsl:value-of select="./creators/creator/pc:person/@PND-Nr" />
        </xsl:variable>
        <creators class="MCRMetaXML" heritable="false" notinherit="false">
          <creator inherited="0">
            <pc:person xmlns:pc="http://www.d-nb.de/standards/pc/">
              <xsl:attribute name="gender"><xsl:value-of select="substring($props/entry[@key='verfasser_geschlecht'],1,1)" /></xsl:attribute>
              <pc:name type="nameUsedByThePerson" Scheme="PND">
                <pc:foreName>
                  <xsl:value-of select="$props/entry[@key='verfasser_vorname']" />
                </pc:foreName>
                <pc:surName>
                  <xsl:value-of select="$props/entry[@key='verfasser_nachname']" />
                </pc:surName>
              </pc:name>
              <pc:academicTitle></pc:academicTitle>
              <pc:dateOfBirth>
                <xsl:value-of select="$props/entry[@key='verfasser_geburtsjahr']" />
              </pc:dateOfBirth>
            </pc:person>
          </creator>
        </creators>
      </xsl:if>
      <xsl:if test="$props/entry[@key='ddc1_nr']/text()">
        <subjects class="MCRMetaClassification" heritable="true" notinherit="false">
          <subject inherited="0" classid="rosdok_class_000000000009">
            <xsl:attribute name="categid">
              <xsl:value-of select="$props/entry[@key='ddc1_nr']" />
            </xsl:attribute>
            <xsl:attribute name="type">
              <xsl:value-of select="$props/entry[@key='ddc1_label']" />
            </xsl:attribute>
          </subject>
          <xsl:if test="$props/entry[@key='ddc2_nr']/text()">
            <subject inherited="0" classid="rosdok_class_000000000009">
              <xsl:attribute name="categid">
                <xsl:value-of select="$props/entry[@key='ddc2_nr']" />
              </xsl:attribute>
              <xsl:attribute name="type">
                <xsl:value-of select="$props/entry[@key='ddc2_label']" />
              </xsl:attribute>
            </subject>
          </xsl:if>

        </subjects>
      </xsl:if>

      <descriptions class="MCRMetaLangText" heritable="false" notinherit="true">
        <xsl:if test="$props/entry[@key='zusammenfassung']/text()">
          <description type="abstract" inherited="0" form="plain">
            <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='zusammenfassung_lang']" /></xsl:attribute>
            <xsl:value-of select="$props/entry[@key='zusammenfassung']" />
          </description>
        </xsl:if>
        <xsl:if test="$props/entry[@key='zusammenfassung_translated']/text()">
          <description type="abstract" inherited="0" form="plain">
            <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='zusammenfassung_translated_lang']" /></xsl:attribute>
            <xsl:value-of select="$props/entry[@key='zusammenfassung_translated']" />
            
          </description>
        </xsl:if>
      </descriptions>
      
      <contributors class="MCRMetaXML" heritable="false" notinherit="false">
         <xsl:if test="$props/entry[@key='gutachter1_nachname']/text()">
          <contributor inherited="0" type="referee">
            <pc:person xmlns:pc="http://www.d-nb.de/standards/pc/">
              <pc:name type="nameUsedByThePerson" Scheme="PND">
                <pc:foreName><xsl:value-of select="$props/entry[@key='gutachter1_vorname']" /></pc:foreName>
                <pc:surName><xsl:value-of select="$props/entry[@key='gutachter1_nachname']" /></pc:surName>
               </pc:name>
              <pc:academicTitle><xsl:value-of select="$props/entry[@key='gutachter1_titel']" /></pc:academicTitle>
              <pc:affiliation>
                <cc:universityOrInstitution xmlns:cc="http://www.d-nb.de/standards/cc/">
                  <cc:name><xsl:value-of select="$props/entry[@key='gutachter1_universitaet']" />, <xsl:value-of select="$props/entry[@key='gutachter1_institution']" /></cc:name>
                </cc:universityOrInstitution>
              </pc:affiliation>
            </pc:person>
          </contributor>
        </xsl:if>
        <xsl:if test="$props/entry[@key='gutachter2_nachname']/text()">
          <contributor inherited="0">
            <pc:person xmlns:pc="http://www.d-nb.de/standards/pc/">
              <pc:name type="nameUsedByThePerson" Scheme="PND">
                <pc:foreName><xsl:value-of select="$props/entry[@key='gutachter2_vorname']" /></pc:foreName>
                <pc:surName><xsl:value-of select="$props/entry[@key='gutachter2_nachname']" /></pc:surName>
               </pc:name>
              <pc:academicTitle><xsl:value-of select="$props/entry[@key='gutachter2_titel']" /></pc:academicTitle>
              <pc:affiliation>
                <cc:universityOrInstitution xmlns:cc="http://www.d-nb.de/standards/cc/">
                  <cc:name><xsl:value-of select="$props/entry[@key='gutachter2_universitaet']" />, <xsl:value-of select="$props/entry[@key='gutachter2_institution']" /></cc:name>
                </cc:universityOrInstitution>
              </pc:affiliation>
            </pc:person>
          </contributor>
        </xsl:if>
        <xsl:if test="$props/entry[@key='gutachter3_nachname']/text()">
          <contributor inherited="0">
            <pc:person xmlns:pc="http://www.d-nb.de/standards/pc/">
              <pc:name type="nameUsedByThePerson" Scheme="PND">
                <pc:foreName><xsl:value-of select="$props/entry[@key='gutachter3_vorname']" /></pc:foreName>
                <pc:surName><xsl:value-of select="$props/entry[@key='gutachter3_nachname']" /></pc:surName>
               </pc:name>
              <pc:academicTitle><xsl:value-of select="$props/entry[@key='gutachter3_titel']" /></pc:academicTitle>
              <pc:affiliation>
                <cc:universityOrInstitution xmlns:cc="http://www.d-nb.de/standards/cc/">
                  <cc:name><xsl:value-of select="$props/entry[@key='gutachter3_universitaet']" />, <xsl:value-of select="$props/entry[@key='gutachter3_institution']" /></cc:name>
                </cc:universityOrInstitution>
              </pc:affiliation>
            </pc:person>
          </contributor>
        </xsl:if>
    </contributors>
     <dates class="MCRMetaISO8601Date" heritable="false" notinherit="false">
      <date type="submitted" inherited="0"><xsl:value-of select="$props/entry[@key='datum_abgabe']" /></date>
      <date type="accepted" inherited="0"><xsl:value-of select="$props/entry[@key='datum_verteidigung']" /></date>
      <date type="published" inherited="0"><xsl:value-of select="substring($props/entry[@key='datum_verteidigung'],7,4)" /></date>
    </dates>
     <types class="MCRMetaClassification" heritable="false" notinherit="true">
      <type inherited="0" classid="rosdok_class_000000000005">
        <xsl:attribute name="categid"><xsl:value-of select="$props/entry[@key='dokumenttyp']" /></xsl:attribute>
      </type>
    </types>
    <formats class="MCRMetaClassification" heritable="false" notinherit="true">
      <format inherited="0" classid="rosdok_class_000000000006" categid="FORMAT0001" />
    </formats>
    <languages class="MCRMetaClassification" heritable="false" notinherit="true">
      <language inherited="0" classid="rfc4646" >
        <xsl:attribute name="categid"><xsl:value-of select="$props/entry[@key='sprache']" /></xsl:attribute>      
      </language>>
    </languages>
    <keywords class="MCRMetaLangText" heritable="false" notinherit="true">
        <xsl:if test="$props/entry[@key='keyword1']/text()">
           <keyword type="freetext" inherited="0" form="plain">
              <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='keyword1_lang']" /></xsl:attribute>
              <xsl:value-of select="$props/entry[@key='keyword1']" />
          </keyword>
        </xsl:if>
        <xsl:if test="$props/entry[@key='keyword2']/text()">
           <keyword type="freetext" inherited="0" form="plain">
              <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='keyword3_lang']" /></xsl:attribute>
              <xsl:value-of select="$props/entry[@key='keyword2']" />
          </keyword>
        </xsl:if>
        <xsl:if test="$props/entry[@key='keyword3']/text()">
           <keyword type="freetext" inherited="0" form="plain">
             <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='keyword3_lang']" /></xsl:attribute>
             <xsl:value-of select="$props/entry[@key='keyword3']" />
          </keyword>
        </xsl:if>
        <xsl:if test="$props/entry[@key='keyword4']/text()">
           <keyword type="freetext" inherited="0" form="plain">
             <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='keyword4_lang']" /></xsl:attribute>
             <xsl:value-of select="$props/entry[@key='keyword4']" />
          </keyword>
        </xsl:if>
        <xsl:if test="$props/entry[@key='keyword5']/text()">
           <keyword type="freetext" inherited="0" form="plain">
             <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='keyword5_lang']" /></xsl:attribute>
             <xsl:value-of select="$props/entry[@key='keyword5']" />
          </keyword>
        </xsl:if>
        <xsl:if test="$props/entry[@key='keyword6']/text()">
           <keyword type="freetext" inherited="0" form="plain">
            <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='keyword6_lang']" /></xsl:attribute>
            <xsl:value-of select="$props/entry[@key='keyword6']" />
          </keyword>
        </xsl:if>
    </keywords>
    <xsl:copy-of select="./grantors" />
    
    <origins class="MCRMetaClassification" heritable="false" notinherit="true">
      <origin inherited="0" classid="rosdok_class_000000000002">
          <xsl:attribute name="categid"><xsl:value-of select="$props/entry[@key='fakultaet']" /></xsl:attribute>
      </origin>
    </origins>
    <xsl:copy-of select="./publishers" />
    
    <xsl:if test="$props/entry[@key='hinweise']/text() | $props/entry[@key='nachricht_bibliothek']/text()">
        <notes class="MCRMetaLangText" heritable="false" notinherit="true">
            <xsl:if test="$props/entry[@key='hinweise']/text()">
            <note inherited="0" form="plain">
                <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='hinweise_lang']" /></xsl:attribute>
                <xsl:value-of select="$props/entry[@key='hinweise']" />
            </note>
            </xsl:if>
            <xsl:if test="$props/entry[@key='hinweise_translated']/text()">
             <note inherited="0" form="plain">
                 <xsl:attribute name="xml:lang"><xsl:value-of select="$props/entry[@key='hinweise_translated_lang']" /></xsl:attribute>
                 <xsl:value-of select="$props/entry[@key='hinweise_translated']" />
            </note>
            </xsl:if>
            <xsl:if test="$props/entry[@key='nachricht_bibliothek']/text()">
             <note inherited="0" form="plain" xml:lang="de">NACHRICHT_VOM_AUTOR (LOESCHEN!!!):&#13;<xsl:value-of select="$props/entry[@key='nachricht_bibliothek']" />
            
            </note>
            </xsl:if>            
      </notes>
     </xsl:if>
     <xsl:copy-of select="./accessrights" />
    
    
    </xsl:copy> <!-- end of metadata -->
  </xsl:template>

  <xsl:template match="/mycoreobject/service">
    <xsl:copy-of select="." />
  </xsl:template>
</xsl:stylesheet> 
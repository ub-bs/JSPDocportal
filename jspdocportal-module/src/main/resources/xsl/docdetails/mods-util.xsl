<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" 
     xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
     xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" 
     version="1.0" exclude-result-prefixes="mods xlink">
<xsl:output method="xml" indent="yes" omit-xml-declaration="yes" encoding="UTF-8" />     

<xsl:template name="mods-title">
  <xsl:for-each select="./mods:titleInfo[@usage='primary']">
    <h4>
      <xsl:if test="./mods:nonSort">
        <xsl:value-of select="./mods:nonSort" />&#160;<xsl:value-of select="./mods:title" />  
      </xsl:if>
      <xsl:if test="not(./mods:nonSort)">
        <xsl:value-of select="./mods:title" />
        <xsl:if test="./mods:subTitle">&#160;:&#160;</xsl:if>  
      </xsl:if>
    </h4>
    <xsl:if test="./mods:subTitle">
      <h4>
        <xsl:value-of select="./mods:subTitle" />
      </h4>
    </xsl:if>

    <xsl:if test="./mods:partNumber or ./mods:partName">
      <br />
      <h5>
        <xsl:value-of select="./mods:partNumber" />
        <xsl:if test="./mods:partNumber and ./mods:partName">: </xsl:if>
        <xsl:value-of select="./mods:partName" />
      </h5>
  </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template name="display-name">
      <xsl:param name="name"  />
      <xsl:if test="$name/@type='personal'">
      <xsl:choose>
    <xsl:when test="$name/mods:namePart[@type='family'] and ./mods:namePart[@type='given']">
    <xsl:value-of select="$name/mods:namePart[@type='family']" />,&#160;
    <xsl:value-of select="$name/mods:namePart[@type='given']" />
    </xsl:when>
    <xsl:otherwise>
      <xsl:for-each select="$name/mods:namePart[not(@type='date')]">
        <xsl:value-of select="." />&#160;
      </xsl:for-each>
    </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$name/@valueURI">
      <xsl:element name="a">
        <xsl:attribute name="href">
          <xsl:value-of select="$name/@valueURI" />
        </xsl:attribute>
        <xsl:element name="span">
          <xsl:attribute name="class">badge ir-badge-gnd</xsl:attribute>
          <xsl:attribute name="title">Datensatz in der Gemeinsamen Normdatei der Deutschen Nationalbibliothek
            (GND) anzeigen</xsl:attribute>
            GND
        </xsl:element>
      </xsl:element>
    </xsl:if>
    <xsl:if test="$name/mods:affiliation">
      <xsl:element name="span">
        <xsl:attribute name="style">position:relative; top:-0.50em;color:#004A99; font-size:90%;font-weight:bold</xsl:attribute>
        <xsl:attribute name="title"><xsl:value-of select="$name/mods:affiliation" /></xsl:attribute>
        &#9432;
      </xsl:element>
    </xsl:if>
    
    </xsl:if>
    <xsl:if test="$name/@type='corporate'">
      <xsl:for-each select="./mods:namePart">
          <xsl:value-of select="." />
          <xsl:if test="position()!=last()">
            <xsl:text>,&#160;</xsl:text>
          </xsl:if>
        
        </xsl:for-each>
       
       <xsl:if test="$name/@valueURI">
      <xsl:element name="a">
        <xsl:attribute name="href">
          <xsl:value-of select="$name/@valueURI" />
        </xsl:attribute>
        <xsl:element name="img">
          <xsl:attribute name="style">vertical-align:middle;padding-left:6px</xsl:attribute>
          <xsl:attribute name="src">
            <xsl:value-of select="$WebApplicationBaseURL" />images/icon_gnd.png</xsl:attribute>
          <xsl:attribute name="title">Datensatz in der Gemeinsamen Normdatei der Deutschen Nationalbibliothek
            (GND) anzeigen</xsl:attribute>
        </xsl:element>
      </xsl:element>
    </xsl:if>
    
    </xsl:if>
    
    <xsl:if test="$name/mods:role/mods:roleTerm[@authority='gbv']">
        &#160;<xsl:value-of select="$name/mods:role/mods:roleTerm[@authority='gbv']" />
    </xsl:if>

</xsl:template>

<xsl:template name="advisor">
      <xsl:param name="name"  />

      <xsl:choose>
        <xsl:when test="$name/mods:namePart[@type='family'] and ./mods:namePart[@type='given']">
          <xsl:value-of select="$name/mods:namePart[@type='family']" />,&#160;
          <xsl:value-of select="$name/mods:namePart[@type='given']" />
          <xsl:if test="$name/mods:namePart[@type='termsOfAddress']" >
            (<xsl:value-of select="$name/mods:namePart[@type='termsOfAddress']" />)
          </xsl:if>
    
    </xsl:when>
    <xsl:otherwise>
      <xsl:for-each select="$name/mods:namePart[not(@type='date')]">
        <xsl:value-of select="." />&#160;
      </xsl:for-each>
    </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$name/@valueURI">
      <xsl:element name="a">
        <xsl:attribute name="href">
          <xsl:value-of select="$name/@valueURI" />
        </xsl:attribute>
        <xsl:element name="span">
          <xsl:attribute name="class">badge ir-badge-gnd</xsl:attribute>
          <xsl:attribute name="title">Datensatz in der Gemeinsamen Normdatei der Deutschen Nationalbibliothek
            (GND) anzeigen</xsl:attribute>
            GND
        </xsl:element>
      </xsl:element>
    </xsl:if>
    <xsl:if test="$name/mods:affiliation">
      <xsl:element name="span">
        <xsl:attribute name="class">ir-advisor-affiliation</xsl:attribute>
        <xsl:value-of select="$name/mods:affiliation" />
      </xsl:element>
    </xsl:if>
</xsl:template>

<xsl:template name="mods-name">
  <xsl:for-each select="./mods:name[@type='personal'][position()=1 or contains('aut edt cre', ./mods:role/mods:roleTerm[@type='code']/text())]">
    <xsl:call-template name="display-name">
      <xsl:with-param name="name" select="." />
    </xsl:call-template>

    <xsl:if test="position()!=last() or ./../mods:name[@type='corporate']">
      <xsl:text>&#160;;&#160;&#160;</xsl:text>
    </xsl:if>
   </xsl:for-each>
   
   <xsl:for-each select="./mods:name[@type='corporate']">
       <xsl:call-template name="display-name">
        <xsl:with-param name="name" select="." />
        </xsl:call-template>
       
       <xsl:if test="position()!=last()">
      <xsl:text>&#160;;&#160;&#160;</xsl:text>
    </xsl:if>
    </xsl:for-each>
   
</xsl:template>

<xsl:template name="mods-name-short">
<xsl:for-each select="./mods:name[(@type='personal' and(position()=1 or contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))) or @type='corporate']">
    <xsl:if test="position()&lt;4">
    <xsl:if test="./@type='personal'">
    <xsl:choose>
    <xsl:when test="./mods:namePart[@type='family'] and ./mods:namePart[@type='given']">
    <xsl:value-of select="./mods:namePart[@type='family']" />,&#160;
    <xsl:value-of select="./mods:namePart[@type='given']" />
    </xsl:when>
    <xsl:otherwise>
      <xsl:for-each select="./mods:namePart[not(@type='date')]">
        <xsl:value-of select="." />&#160;
      </xsl:for-each>
    </xsl:otherwise>
    </xsl:choose>
   </xsl:if>
   
   <xsl:if test="./@type='corporate'">
        <xsl:for-each select="./mods:namePart">
          <xsl:value-of select="." />
          <xsl:if test="position()!=last()">
            <xsl:text>&#160;</xsl:text>
          </xsl:if>
        
        </xsl:for-each>
  </xsl:if>       
       
       <xsl:if test="position()!=last()">
      <xsl:text>&#160;;&#160;&#160;</xsl:text>
    </xsl:if>
    
  
    </xsl:if>
    </xsl:for-each>
    <xsl:if test="count(./mods:name[(@type='personal' and(position()=1 or contains('aut edt', ./mods:role/mods:roleTerm[@type='code']/text()))) or @type='corporate'])&gt;3">
      ...
    </xsl:if>
   
</xsl:template>




<xsl:template name="mods-originInfo">
            <xsl:choose>
              <xsl:when test="mods:originInfo[@eventType='publication']"> 
                <xsl:for-each select="mods:originInfo[@eventType='publication'][1]">
                  <xsl:if test="mods:edition"><xsl:value-of select="mods:edition" /><xsl:value-of select="' , '" /></xsl:if>    
                  <xsl:if test="mods:place/mods:placeTerm"><xsl:value-of select="mods:place/mods:placeTerm" /><xsl:value-of select="' : '" /></xsl:if>
                  <xsl:if test="mods:publisher"><xsl:value-of select="mods:publisher" /><xsl:value-of select="' , '" /></xsl:if>
                  <xsl:value-of select="mods:dateIssued" />
                    <xsl:value-of select="mods:dateCreated[@qualifier='approximate']" />
                </xsl:for-each>  
              </xsl:when>
              <xsl:when test="mods:originInfo[@eventType='creation']"> 
                <xsl:for-each select="mods:originInfo[@eventType='creation'][1]">
                  <xsl:if test="mods:edition"><xsl:value-of select="mods:edition" /><xsl:value-of select="' , '" /></xsl:if>    
                  <xsl:if test="mods:place/mods:placeTerm"><xsl:value-of select="mods:place/mods:placeTerm" /><xsl:value-of select="' : '" /></xsl:if>
                  <xsl:if test="mods:publisher"><xsl:value-of select="mods:publisher" /><xsl:value-of select="' , '" /></xsl:if>
                  <xsl:value-of select="mods:dateIssued" />
                  <xsl:value-of select="mods:dateCreated[@qualifier='approximate']" />
                </xsl:for-each>  
              </xsl:when>
            </xsl:choose>
</xsl:template>

<xsl:template name="language">
    <xsl:param name="term"  />
    <xsl:param name="lang"  />
    
    <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL" />api/v1/classifications/rfc4646</xsl:variable>
    <xsl:value-of select="document($url)/mycoreclass/categories/category[./label[@xml:lang='x-bibl']/@text=$term]/label[@xml:lang=$lang]/@text" /> 

  </xsl:template>
  

  <!-- retrieves the label of a MyCoRe classification category  for a given MODS valueURI -->
  <!--  reverse is necessary since sub-string-after-last is not supported  -->
  <xsl:template name="classLabel">
    <xsl:param name="valueURI"  />
     <xsl:variable name="x">
      <xsl:call-template name="reverse">
        <xsl:with-param name="input"><xsl:value-of select="$valueURI" /></xsl:with-param>
     </xsl:call-template>
     </xsl:variable>

    <xsl:variable name="y" select="substring-before($x, '/')" />
 
    <xsl:variable name="z">
      <xsl:call-template name="reverse">
        <xsl:with-param name="input"><xsl:value-of select="$y" /></xsl:with-param>
     </xsl:call-template>
     </xsl:variable>
          
     <xsl:variable name="classid" select="substring-before($z,'#')" />
     <xsl:variable name="categid" select="substring-after($z,'#')" />
 
     <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL" />api/v1/classifications/<xsl:value-of select="$classid" />?filter=root:<xsl:value-of select="$categid" />;nochildren;lang:de</xsl:variable>
     <xsl:value-of select="document($url)/category/label[1]/@text" /> 
      
    <!-- http://localhost:8080/rosdok/api/v1/classifications/rosdok_class_000000000009?filter=root:00;lang:de;nochildren -->
</xsl:template>


  <!-- Reverse Algorithm from: XSLT Cookbook, 2nd Edition -->
  <xsl:template name="reverse">
     <xsl:param name="input"/>
     <xsl:variable name="len" select="string-length($input)"/>
     <xsl:choose>
          <!-- Strings of length less than 2 are trivial to reverse -->
          <xsl:when test="$len &lt; 2">
               <xsl:value-of select="$input"/>
          </xsl:when>
          <!-- Strings of length 2 are also trivial to reverse -->
          <xsl:when test="$len = 2">
               <xsl:value-of select="substring($input,2,1)"/>
               <xsl:value-of select="substring($input,1,1)"/>
          </xsl:when>
          <xsl:otherwise>
               <!-- Swap the recursive application of this template to 
               the first half and second half of input -->
               <xsl:variable name="mid" select="floor($len div 2)"/>
               <xsl:call-template name="reverse">
                    <xsl:with-param name="input"
                         select="substring($input,$mid+1,$mid+1)"/>
               </xsl:call-template>
               <xsl:call-template name="reverse">
                    <xsl:with-param name="input"
                         select="substring($input,1,$mid)"/>
               </xsl:call-template>
          </xsl:otherwise>
     </xsl:choose>
  </xsl:template>

  <xsl:template name="data-derivates">
     <xsl:param name="label"/>
     <xsl:param name="headline"/>
    <xsl:for-each select="/mycoreobject/structure/derobjects/derobject[@xlink:title=$label]">
       <xsl:element name="strong">
          <xsl:value-of select="$headline"></xsl:value-of>
          <br />
       </xsl:element>
       <xsl:variable name="url"><xsl:value-of select="$WebApplicationBaseURL"/>api/v1/objects/<xsl:value-of select="/mycoreobject/@ID" />/derivates/<xsl:value-of select="./@xlink:href" />/files</xsl:variable>
       <xsl:variable name="derLink" select="document($url)/files/file[1]" />
       <xsl:if test="$derLink">
         <xsl:variable name="type" select="substring($derLink/@href, string-length($derLink/@href)-2)" />
         <xsl:element name="img">
           <xsl:choose>
             <xsl:when test="contains('zip|pdf', $type)">
               <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/derivate_<xsl:value-of select="$type"/>.gif</xsl:attribute>
             </xsl:when>
             <xsl:otherwise>
               <xsl:attribute name="src"><xsl:value-of select="$WebApplicationBaseURL"/>images/derivate_unknown.gif</xsl:attribute>
             </xsl:otherwise>
           </xsl:choose>
           <xsl:attribute name="style">float:left;</xsl:attribute>
         </xsl:element>
         <div  style="padding-left:30px">
         <xsl:element name="a">
           <xsl:attribute name="href"><xsl:value-of select="$derLink/@href" /></xsl:attribute>
           <strong><xsl:value-of select="$derLink/@name" /></strong>
         </xsl:element>
         
         <br />
         <span style="font-size:80%">
         <xsl:value-of select="round($derLink/@size div 1024 div 1024 * 1000) div 1000" /> MB<br />
         MD5: <xsl:value-of select="$derLink/@md5" />
         </span>
         </div>
       </xsl:if>
     </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>


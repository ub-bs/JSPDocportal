<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->

<!-- +
     | This stylesheet controls the Web-Layout of the Login Servlet. The Login Servlet
     | gathers information about the session, user ID, password and calling URL and
     | then tries to login the user by delegating the login request to the user manager.
     | Depending on whether the login was successful or not, the Login Servlet generates
     | the following XML output stream:
     |
     | <mcr_user unknown_user="true|false"
     |           user_disabled="true|false"
     |           invalid_password="true|false">
     |   <guest_id>...</guest_id>
     |   <guest_pwd>...</guest_pwd>
     |   <backto_url>...<backto_url>
     | </mcr_user>
     |
     | The XML stream is sent to the Layout Servlet and finally handled by this stylesheet.
     |
     | Authors: Detlev Degenhardt, Thomas Scheffler
     | Last changes: 2004-03-08
     + -->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  exclude-result-prefixes="xlink">

<!-- The main template -->
<xsl:template match="/mcr_user">
  <!-- BEGIN: frame -->
  <table class="frame" >
    <tr>
      <td>   

    <!-- At first we display the current user in a head line. -->
      <table class="frameintern" cellpadding="0" cellspacing="0">
        <tr style="height:20px;">
          <td class="resultcmd">
            <xsl:value-of select="$Login.current_account"/>
			&#160;&#160;[&#160;<span class="username"><xsl:value-of select="$CurrentUser"/></span>&#160;]
          </td>
        </tr>
      </table>

    <!-- +
         | There are three possible error-conditions: wrong password, unknown user and disabled
         | user. If one of these conditions occured, the corresponding information will be
         | presented at the top of the page.
         + -->

    <table class="frameintern" style="height:20px;">
      <tr><td colspan="2">&#160;</td></tr>
      <xsl:if test="@invalid_password='true'">
        <tr><td class="errormain"><xsl:value-of select="$Login.login_failed"/></td></tr>
        <tr><td class="errormain"><xsl:value-of select="$Login.invalid_pwd"/></td></tr>
      </xsl:if>
      <xsl:if test="@unknown_user='true'">
        <tr><td colspan="2">&#160;</td></tr>
        <tr><td class="errormain"><xsl:value-of select="$Login.login_failed"/></td></tr>
        <tr><td class="errormain"><xsl:value-of select="$Login.unknown_user"/></td></tr>
      </xsl:if>
      <xsl:if test="@user_disabled='true'">
        <tr><td colspan="2">&#160;</td></tr>
        <tr><td class="errormain"><xsl:value-of select="$Login.login_failed"/></td></tr>
        <tr><td class="errormain"><xsl:value-of select="$Login.user_disabled"/></td></tr>
      </xsl:if>
    </table>

    <!-- +
         | Now the login dialog will be presented. There are two input fields, one for the
         | user account and one for the password. Additionally there are 3 buttons: the
         | "cancel"-button redirects back to the originating url, the "logout"-button changes
         | the user to the guest account and the "login"-button submits the data to the
         | LoginServlet.
         + -->

    <xsl:variable name="backto_url" select="/mcr_user/backto_url" />
    <xsl:variable name="guest_id" select="/mcr_user/guest_id" />
    <xsl:variable name="guest_pwd" select="/mcr_user/guest_pwd" />

    <xsl:variable
      xmlns:encoder="xalan://java.net.URLEncoder"
      name="href"
      select="concat($ServletsBaseURL, 'MCRLoginServlet?lang=', $CurrentLang, '&amp;url=', encoder:encode(string($backto_url)))">
    </xsl:variable>

    <form class="login" action="{$ServletsBaseURL}MCRLoginServlet" method="post">
      <input type="hidden" name="url" value="{backto_url}"/>
      <table class="frameintern" style="height:50px;" >

        <!-- Here come the input fields... -->
        <tr>
          <td class="inputcaption"><xsl:value-of select="$Login.account"/></td>
          <td class="inputfield"><input name="uid" type="text" class="text" maxlength="30"/></td>
        </tr>
        <tr>
          <td class="inputcaption"><xsl:value-of select="$Login.password"/></td>
          <td class="inputfield"><input name="pwd" type="password" class="text" maxlength="30"/></td>
        </tr>
        <tr><td colspan="2">&#160;</td></tr>

        <!-- and finally the buttons. -->
        <tr>
          <td class="logincmd">&#160;</td>
          <td class="resultcmd" style="white-space: nowrap;vertical-align:top;">
            &#160;
            <input type="submit" class="submitbutton" value="{$Login.submit}" name="LoginSubmit"/>
            &#160;
            <input type="reset" class="submitbutton" value="{$Login.cancel}" name="LoginReset"/>
          </td>
        </tr>
      </table>
    </form>

      <!-- END OF: frame -->
      </td>
    </tr>
  </table>  
</xsl:template>
</xsl:stylesheet>
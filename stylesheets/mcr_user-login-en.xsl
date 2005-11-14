<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:03 $ -->
<!-- ============================================== -->

<!-- +
     | This stylesheet provides the language dependent values (english language) for
     | the presentation of the login dialog. It is called by the MCRLoginServlet
     | by forwarding an XML stream to the LayoutServlet (for this see mcr_user-login.xsl).
     | After defining the language dependent values this stylesheet finally includes
     | the stylesheet mcr_user-login.xsl.
     |
     | Author: Detlev Degenhardt
     | Last changes: 2004-03-06
     + -->

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  exclude-result-prefixes="xlink"
>

<xsl:variable name="Login.title"            select="'MyCoRe: change user account'" />
<xsl:variable name="Login.login_failed"     select="'The login attempt failed!'" />
<xsl:variable name="Login.invalid_pwd"      select="'The password is not correct.'" />
<xsl:variable name="Login.unknown_user"     select="'The user account is unknown.'" />
<xsl:variable name="Login.user_disabled"    select="'The user account is disabled.'" />
<xsl:variable name="Login.current_account"  select="'You are currently logged in as:'" />
<xsl:variable name="Login.account"          select="'User account:'" />
<xsl:variable name="Login.password"         select="'Password:'" />
<xsl:variable name="Login.submit"           select="'Login'" />
<xsl:variable name="Login.logout"           select="'Logout'" />
<xsl:variable name="Login.cancel"           select="'Cancel'" />

<xsl:variable name="MainTitle" select="'MyCoRe Sample'"/>
<xsl:variable name="PageTitle" select="$Login.title"/>

<xsl:variable name="Servlet" select="'LoginServlet'"/>

<xsl:include href="MyCoReLayout-en.xsl" />
<xsl:include href="mcr_user-login.xsl" />

</xsl:stylesheet>

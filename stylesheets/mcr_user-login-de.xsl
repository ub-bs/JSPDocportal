<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- ============================================== -->
<!-- $Revision: 1.1 $ $Date: 2005-11-14 12:51:02 $ -->
<!-- ============================================== -->

<!-- +
     | This stylesheet provides the language dependent values (german language) for
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

<xsl:variable name="Login.title"            select="'MyCoRe: Nutzerkennung wechseln'" />
<xsl:variable name="Login.login_failed"     select="'Die Anmeldung ist fehlgeschlagen!'" />
<xsl:variable name="Login.invalid_pwd"      select="'Das Passwort ist nicht korrekt.'" />
<xsl:variable name="Login.unknown_user"     select="'Die angegebene Benutzerkennung ist unbekannt.'" />
<xsl:variable name="Login.user_disabled"    select="'Die angegebene Benutzerkennung ist gesperrt.'" />
<xsl:variable name="Login.current_account"  select="'Sie sind derzeit angemeldet als:'" />
<xsl:variable name="Login.account"          select="'Benutzerkennung:'" />
<xsl:variable name="Login.password"         select="'Passwort:'" />
<xsl:variable name="Login.submit"           select="'Anmelden'" />
<xsl:variable name="Login.logout"           select="'Abmelden'" />
<xsl:variable name="Login.cancel"           select="'Abbrechen'" />

<xsl:variable name="MainTitle" select="'DocPortal'"/>
<xsl:variable name="PageTitle" select="$Login.title"/>

<xsl:variable name="Servlet" select="'LoginServlet'"/>

<xsl:include href="MyCoReLayout-de.xsl" />
<xsl:include href="mcr_user-login.xsl" />

</xsl:stylesheet>

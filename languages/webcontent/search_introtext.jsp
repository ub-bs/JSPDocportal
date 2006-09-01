<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<p>Bitte beachten Sie auch, dass die Einträge in den einzelnen Eingabe-
und Auswahlfelder der Suchmasken bei der Ausführung der Anfrage mit
einem logischen UND verbunden werden, d. h. jeder Eintrag schränkt die
Suche zusätzlich ein. Aus Gründen einer sinnvollen Trefferanzeige ist
die maximale Anzahl von Suchresultaten auf 100 beschränkt. Sollten Sie
das gewünschte Ergebnis nicht unter den Treffern finden, formulieren Sie
die Suche mit den entsprechenden Kriterien neu.</p>

<p>Das System unterscheidet bei der Suche zwischen beschreibenden Daten
(auch Metadaten genannt) und dem Volltext des Dokumentes. Eine
Volltextsuche kann nur erfolgreich sein, wenn das vorliegende Objekt
textindizierbar war. Bilder, Videos und ein Teil der PDF-Dateien sind
nicht textindizierbar und nur über die Metadaten recherchierbar.</p>

<p>Ausserdem ist zu beachten, dass für eine textliche Suche sowohl in
den Metadaten als auch in den Volltexten die Suchbegriffe mindestens 3
Zeichen lang sein müssen. Wortstücke können mit einem * ergänzt werden.
Derzeit ist auf korrekte Groß- und Kleinschreibung und auf die richtige
Nutzung der deutschen Umlaute zu achten.</p>

<!-- Webpage.intro.search.HintsBool
     Webpage.intro.search.HintsFulltext
     Webpage.intro.search.HintsMinLength -->


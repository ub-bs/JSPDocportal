<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages' />
<p>This server is an example for a common document and dissertation server.</p>
<p> All documents are inserted with meta data in an extended Dublin Core format. This
meta data is searchable. Depending on the used hard- and software full text search is 
possible as well. </p>

<p>Within this application there are two basic types of data: dissertation and document.
Since the workflow for adding and managing simple documents is different from the worfklow for dissertations
they are treated separately. There are strong requirements for dissertations, since they are part of the 
process of a promotion. They have to be published by the university library after strict rules.</p>

<h3> noch zu übersetzen ... </h3>
<p>Jeder dieser beiden Typen kennt die Metadaten des eigentlichen
Objektes, das Objekt an sich sowie Institutions- und Personen-Metadaten,
auf welche in den Dokument-Metadaten referenziert wird. Hinzu kommen
noch Informationen über Klassifikation denen das Objekt via Metadaten
zugeordnet ist.</p>
<p><a href="nav?path=%7Esearchstart"> <strong>Die Suche in den Daten</strong>
</a></p>
<p>Für die Suche in den Daten werden Ihnen interaktive Suchmasken
angeboten. Alle Auswahlfelder der Suchmaske sind implizit mit UND
verknüpft. Um den Google-Effekt riesiger Trefferlisten zu vermeiden,
sollten Sie Ihre Suche durch die Auswahl mehrerer Parameter
einschränken.</p>
<p>Der Erfolg der Volltext-Suche ist ein wenig vom verwendeten
Server-System abhängig, hier kann es bedingt durch die unterschiedlichen
Volltext-Suchmaschinen zu Differenzen in der Trefferliste kommen. Auch
die Qualität der Textindizierbarkeit der eingestellten Objekte ist hier
von Bedeutung. PDF Dateien beispielsweise, die den Text als Bilder (z.
B. Scan) vorhalten sind nicht textindizierbar und damit auch nicht
Volltext-recherchierbar!</p>

<p><a href="nav?path=%7Edocumentmanagement"> <strong>Die
Dokumentenverwaltung</strong> </a></p>
<p>Auch im Autorenbereich wird zwischen allgemeinen Dokumenten und
Dissertationen / Habilitationen unterschieden. Während letztere
rechtlich für die Autoren relevant sind und durch die Bibliothek betreut
werden, haben Dokumente keine Bedeutung bei der Graduierung. Dieser Teil
ist für Preprints, Lehrmaterial usw. gedacht. Auch Videos und
multimediales Material kann als Dokument abgelegt werden.</p>
<p>Im Autorenbereich wird unter DocPortal immer zwischen einem Autor und
einem Bearbeiter unterschieden. Beide haben unterschiedliche Rechte.
Autoren dürfen ihre Arbeiten nur in einen Workflow einbringen,
Bearbeiter können dann diese Daten freigeben und in das System
hochladen. Die Arbeitsabläufe unterscheiden sich dabei zwischen den
beiden Typen, Dokumente werden direkt nach dem Bearbeiten geladen, für
Dissertationen / Habilitationen ist dafür noch ein extra Arbeitsschritt
erforderlich.</p>
<p>Wenn Sie eine ausführliche Information zum Autor oder zu beteiligten
Personen oder Institutionen in die Metadaten Ihres Objektes integrieren
möchten (für Dissertationen / Habilitationen ist das PFLICHT), so legen
Sie zuerst einen Personen- bzw. Institutionen-Datensatz an und merken
sich die ID dieses Datensatzes. Nun können Sie diese ID beim Ausfüllen
der Dokumentdaten als Referenz bei den Feldern Autor, Publizist und
Beteiligter entsprechend angeben. Start.Workflow=Daten die neu
eingegeben wurden werden erstmal im Workflow zwischengespeichert. Hier
befinden sich alle Daten, die noch nicht in das System eingestellt
wurden. Da bei der Bearbeitung von Dissertationen / Habilitationen oft
mehrere Iterationsschritte erforderlich sind, müssen diese Daten dann
expizit in den Server gestellt werden.</p>
<p>Daten die neu eingegeben wurden werden erstmal im Workflow
zwischengespeichert. Hier befinden sich alle Daten, die noch nicht in
das System eingestellt wurden. Da bei der Bearbeitung von Dissertationen
/ Habilitationen oft mehrere Iterationsschritte erforderlich sind,
müssen diese Daten dann expizit in den Server gestellt werden.</p>
<%-- 	Webpage.intro.ExampleApplication
		Webpage.intro.ExplainSearch
		Webpage.intro.TwoFundamentalTypes
		Webpage.intro.TypesKnowMetadata
		Webpage.intro.SearchData
		Webpage.intro.SearchMasks
		Webpage.intro.FulltextSearchDependencies
		Webpage.intro.TheDocumentManagement
		Webpage.intro.AlsoTwoTypesForAuthors
		Webpage.intro.DifferenceAuthorEdito
		Webpage.intro.MoreAuthorData
		Webpage.intro.Workflow --%>

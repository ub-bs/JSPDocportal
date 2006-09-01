<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="${requestScope.lang}" />
<fmt:setBundle basename='messages'/>
<p>Dieser Server ist eine Beispiel-Anwendung zur Gestaltung allgemeiner Dokumenten- und Dissertations/Habilitations-Server.</p>

<p>Alle Dokumente werden mit einem erweiterten Dublin Core Metadatensatz in das System eingestellt und sind �ber diese Metadaten recherchierbar. Je nach eingesetzter Hard- und Software kommt hierzu noch eine Volltext-Recherchem�glichkeit.</p>
<p>Innerhalb dieser Anwendung sind die Daten in zwei Grundtypen aufgeteilt. Da sich die Arbeitsabl�ufe f�r das Einstellen und Verwalten einfacher Dokumente von denen einer Dissertation/Habilitation wesentlich unterscheiden, werden diese im Projekt auch getrennt behandelt. Dissertationen/Habilitation unterliegen strengen Vorschriften, da sie einen Teil des Promotionsprozesses darstellen und nach festgelegten Regeln durch die Universit�tsbibliotheken vorzuhalten sind.</p>
<p>Jeder dieser beiden Typen kennt die Metadaten des eigentlichen Objektes, das Objekt an sich sowie Institutions- und Personen-Metadaten, auf welche in den Dokument-Metadaten referenziert wird. Hinzu kommen noch Informationen �ber Klassifikation denen das Objekt via Metadaten zugeordnet ist.</p>
<p>
<a href="nav?path=%7Esearchstart">
      <strong>Die Suche in den Daten</strong>
    </a>
</p>
<p>F�r die Suche in den Daten werden Ihnen interaktive Suchmasken angeboten. Alle Auswahlfelder der Suchmaske sind implizit mit UND verkn�pft. Um den Google-Effekt riesiger Trefferlisten zu vermeiden, sollten Sie Ihre Suche durch die Auswahl mehrerer Parameter einschr�nken.</p>
<p>Der Erfolg der Volltext-Suche ist ein wenig vom verwendeten Server-System abh�ngig, hier kann es bedingt durch die unterschiedlichen Volltext-Suchmaschinen zu Differenzen in der Trefferliste kommen. Auch die Qualit�t der Textindizierbarkeit der eingestellten Objekte ist hier von Bedeutung. PDF Dateien beispielsweise, die den Text als Bilder (z. B. Scan) vorhalten sind nicht textindizierbar und damit auch nicht Volltext-recherchierbar!</p>

<p>

      <a href="nav?path=%7Edocumentmanagement">
      <strong>Die Dokumentenverwaltung</strong>
      </a>
</p>
<p>Auch im Autorenbereich wird zwischen allgemeinen Dokumenten und Dissertationen / Habilitationen unterschieden. W�hrend letztere rechtlich f�r die Autoren relevant sind und durch die Bibliothek betreut werden, haben Dokumente keine Bedeutung bei der Graduierung. Dieser Teil ist f�r Preprints, Lehrmaterial usw. gedacht. Auch Videos und multimediales Material kann als Dokument abgelegt werden.</p>
<p>Im Autorenbereich wird unter DocPortal immer zwischen einem Autor und einem Bearbeiter unterschieden. Beide haben unterschiedliche Rechte. Autoren d�rfen ihre Arbeiten nur in einen Workflow einbringen, Bearbeiter k�nnen dann diese Daten freigeben und in das System hochladen. Die Arbeitsabl�ufe unterscheiden sich dabei zwischen den beiden Typen, Dokumente werden direkt nach dem Bearbeiten geladen, f�r Dissertationen / Habilitationen ist daf�r noch ein extra Arbeitsschritt erforderlich.</p>
<p>Wenn Sie eine ausf�hrliche Information zum Autor oder zu beteiligten Personen oder Institutionen in die Metadaten Ihres Objektes integrieren m�chten (f�r Dissertationen / Habilitationen ist das PFLICHT), so legen Sie zuerst einen Personen- bzw. Institutionen-Datensatz an und merken sich die ID dieses  Datensatzes. Nun k�nnen Sie diese ID beim Ausf�llen der Dokumentdaten als Referenz bei den Feldern Autor, Publizist und Beteiligter entsprechend angeben. Start.Workflow=Daten die neu eingegeben wurden werden erstmal im Workflow zwischengespeichert. Hier befinden sich alle Daten, die noch nicht in das System eingestellt wurden. Da bei der Bearbeitung von Dissertationen / Habilitationen oft mehrere Iterationsschritte erforderlich sind, m�ssen diese Daten dann expizit in den Server gestellt werden.</p>
<p>Daten die neu eingegeben wurden werden erstmal im Workflow zwischengespeichert. Hier befinden sich alle Daten, die noch nicht in das System eingestellt wurden. Da bei der Bearbeitung von Dissertationen / Habilitationen oft mehrere Iterationsschritte erforderlich sind, m�ssen diese Daten dann expizit in den Server gestellt werden.</p>
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
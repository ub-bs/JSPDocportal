SQLTree Plug-in for Eclipse
===========================

Leistungsumfang
---------------
Das SQLTree-Plugin dient zur Demonstration von Algorithmen, die für die Realisierung der Speicherung von Bäumen in einer Tabellenstruktur benötigt werden. Das Konzept wurde aus Artikeln "SQL for Smarties" von Joe Celko aus DBMSonline übernommen.
(siehe http://www.dbmsmag.com/9603d06.html, http://www.dbmsmag.com/9604d06.html, http://www.dbmsmag.com/9605d06.html http://www.dbmsmag.com/9606d06.html)
Implementiert wurden, das Erzeugen von Bäumen, das Einfügen von Knoten, das Verschieben von Teilbäumen und das Löschen von Teilbäumen.
Die Bäume werden in einer HSQL-DB im Speicher abgelegt, die beim Beenden des Programms nicht gesichert wird.

Installation
------------
Das Tool wird als Plug-in in Eclipse integriert. Zur Installation ist lediglich der Inhalt der Zip-Datei in das ../eclipse/plugins Verzeichnis zu entpacken.

Start
-----
Das Tool wurde als View in die Eclipse-Oberfläche integriert.
Er wird über das Menü: Window -> Show View -> Other... geöffnet.
Im sich öffnenden Dialog kann er unter SQLTree -> SQLTree View gefunden werden.

Eignabe-Syntax für Bäume
------------------------
Für die effiziente Eingabe wurde die LISP-Notation gewählt
tree = subtree
subtree = (node (subtree)...(subtree)) | (node)
node = <string>

z.B: "(World(Europe(Germany)(UK))(Asia))" ergibt:
              World
         +------+---------+  	
       Europe            Asia
   +------+---+
Germany       UK		


Algorithmen
-----------
Die Implementation der Algorithmen erfolgt in der Klasse sqltree.SQLTreeManager



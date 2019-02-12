## Herzlich Willkommen zu EVE - Lego Image Recognition Project

Im folgenden werden wir das "EVE - Lego Image Recognition Project" vorstellen:

### Idee des Projekts
Die Idee des Projektes entstand während der Vorlesung Intelligente Agenten und Multiagentensysteme an der Dualen Hochschule Baden-Württemberg. 
Ziel war es, einen Roboter zur Erkennung von Bildern zu bauen und zu programmieren. Dabei ging es uns darum, mit einem lernenden Algorithmus (sog. Support Vector Machines) in der Lage zu sein, Kreise von Quadraten zu unterscheiden. Eingesetzt werden sollen zur Unterscheidung die beiden Parameter Flächeninhalt und Umfang der einzelnen Figuren. 
Eingesetzt wird bei diesem Projektder EV3-Brick von Lego Mindstorms. Dieser wurde mit der Anleitung unter https://github.com/ChristopherTr/legoAgent/wiki/Installation-Toolchain mit dem Projekt LeJOS installiert und kann damit mit Java programmiert werden. 

### Aufbau
#### Hardware
![Bild der Hardware](pictures/IMG_20190211_131934.jpg)

#### Software
Die Software des Roboters ist modular aufgebaut und besteht aus diversen Klassen für verschiedene Aufgaben. 
![Klassendiagramm](pictures/klassendiagramm.png)
* Die GUI-Klasse des Programms stellt das Menü auf dem LCD-Display des Bricks dar und übernimmt die Interaktion mit dem Benutzer. 
* Durch die Recognition-Klasse wird für die GUI die Logik des Programms gekapselt. Damit kann vermieden werden, dass die GUI Hintergrundwissen über den Aufbau des Programms haben muss. 
* Die Scanner-Klasse steuert die Hardware und liest das Bild ein. Zusätzlich wird von dieser Klasse das Bild ausgewertet und die beiden relevanten Größen Umfang und Flächeninhalt berechnet. 
* In der DataPoint-Klasse wird ein einzelner gesammelter Datenpunkt (also die Auswertung eines Bildes) gespeichert. 
* Die SVM-Klasse übernimmt die Implementierung der Support Vector Machine und führt die erforderlichen Berechnungen zur Erstellung und dem Training der SVM. Auch enthalten ist die Auswertung weiterer Bilder, welche von der SVM klassifiziert werden soll. 
* Durch die Dataset-Klasse werden die verwendeten Daten verwaltet. Der Zustand der SVM wird mithilfe dieser Klasse persistent auf der SD-Karte abgelegt. 
* Vector ist eine Hilfsklasse, welche verwendet wird, um Berechnungen der SVM zu erleichtern. 
* Logger dient dem Debugging und der Protokollierung



### weitere Schritte

# Robot Control GUI

Dieses Projekt bietet eine grafische Benutzeroberfläche (GUI) zur Steuerung eines Roboters und ermöglicht die Kommunikation über OSC-Signale.

## Voraussetzungen

- IntelliJ IDEA (oder ein vergleichbares Tool)
- Kotlin- und Java-Support
- Der Computer und der Roboter müssen sich im **gleichen WLAN-Netzwerk** befinden, um OSC-Signale austauschen zu können.

## Installation

1. **Repository klonen**  
   Klone das Repository lokal, indem du folgenden Befehl ausführst:  
   ```bash
   git clone <URL-des-Repository>
   
2.**Projekt öffnen**

Öffne das geklonte Projekt in IntelliJ.

3.**Abhängigkeiten sicherstellen**

Stelle sicher, dass Kotlin und Java korrekt in IntelliJ installiert sind. Falls nicht, installiere sie über die IDE.

4.**Starten**

Das Programm kann direkt in IntelliJ gestartet werden. Nutze dazu die bereitgestellte Run-Konfiguration oder erstelle eine neue.

## Buttons der GUI


**Vorwärts**
Lässt den Roboter vorwärts fahren. Mit gedrückter Shift-Taste bewegt er sich in kleinen Schritten.

**Links**
Dreht den Roboter nach links. Mit gedrückter Shift-Taste erfolgt eine kleinere Drehung.

**Rechts**
Dreht den Roboter nach rechts. Mit gedrückter Shift-Taste erfolgt eine kleinere Drehung.

**Rückwärts**
Lässt den Roboter rückwärts fahren. Mit gedrückter Shift-Taste bewegt er sich in kleinen Schritten.

**Look (Kopf drehen)**
Der Roboter scannt den aktuellen Bereich und sendet die erkannten Wende an die GUI.

**Pos (Positionieren)**
Startet die Selbstpositionierung des Roboters.

**Test**
Fügt ein Beispiel-Labyrinth in die GUI ein und demonstriert die Funktionalitäten der Kachelvisualisierung.

**Quickest**
Berechnet den kürzesten Weg durch das Labyrinth. Der Pfad wird in der GUI hervorgehoben.

**Back**
Der Roboter navigiert über den schnellsten Weg zurück zum Startpunkt.

## Hinweise
Die GUI zeigt das Labyrinth und die Position des Roboters visuell an.
Alle Aktionen und Bewegungen des Roboters werden durch OSC-Signale gesteuert.

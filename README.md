# Robot Control GUI

Dieses Projekt bietet eine grafische Benutzeroberfläche (GUI) zur Steuerung eines Roboters und ermöglicht die Kommunikation über OSC-Signale.

## Voraussetzungen

- IntelliJ IDEA (oder ein vergleichbares Tool)
- Kotlin- und Java-Support
- Der EV3 muss das [Lego OSC](https://github.com/WerthersEchte/legoOSC) Programm installiert haben.
- Der Computer und der Roboter müssen sich im **gleichen WLAN-Netzwerk** befinden, um OSC-Signale austauschen zu können.

## Installation

1. **Repository klonen oder als .zip installieren**  
   Klone das Repository lokal, indem du folgenden Befehl ausführst:  
   ```bash
   git clone <URL-des-Repository>
   
2.**Projekt öffnen**

Öffne das geklonte oder installierte Projekt in IntelliJ.

3.**Starten**

Das Programm kann direkt in IntelliJ gestartet werden.
Der Roboter muss das Lego OSC Program laufen lassen.

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

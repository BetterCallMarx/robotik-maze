package gui

import graphing.Tile
import enums.TileColor
import graphing.GraphFrontend
import java.awt.*
import javax.swing.JPanel
import javax.swing.Timer
import javax.imageio.ImageIO
import java.io.File

class MazePanel : JPanel() {
    private val rooms = mutableListOf<Room>()
    private val scale = 2
    private val gridSize = 32 * scale
    private var debugMessage: String = "" // Holds the debug message
    private val pathPoints = mutableListOf<Pair<Int, Int>>() // Speichert die Punkte des Pfads
    private val overlayImage: Image

    init {
        // Lade das Bild aus dem src-Ordner
        overlayImage = ImageIO.read(File("src/GOAT.png"))
        val timer = Timer(100) { repaint() } // Repaints every 100 milliseconds
        timer.start()
    }

    private fun drawDebugMessage(g: Graphics2D) {
        g.color = Color.BLACK
        g.font = Font("Arial", Font.PLAIN, 16)
        g.drawString("Debug: ${DebugMessage.debugMessage}", 30, 90) // Draw the debug message
    }

    fun addTile(tile: Tile) {
        // Erstelle die Liste der Wände aus den Tile-Eigenschaften
        val walls = mutableListOf(
            tile.northOpen,
            tile.eastOpen,
            tile.southOpen,
            tile.westOpen
        )

        // Suche nach einem Raum mit den gleichen Koordinaten
        val existingRoomIndex = rooms.indexOfFirst { it.position == mazeCoordToGuiCoord(tile.coordinates) }

        if (existingRoomIndex >= 0) {
            // Wenn der Raum bereits existiert, ersetze ihn
            val existingRoom = rooms[existingRoomIndex]
            existingRoom.color = tile.color  // Ersetze die Farbe des bestehenden Raums
            existingRoom.walls = walls      // Ersetze die Wände
        } else {
            // Wenn der Raum nicht existiert, erstelle einen neuen Raum
            val newRoom = Room(tile.color, walls, mazeCoordToGuiCoord(tile.coordinates))
            rooms.add(newRoom)  // Füge den neuen Raum hinzu
        }

        // Repaint der Anzeige, um die Änderungen zu visualisieren
        repaint()
    }


    private fun mazeCoordToGuiCoord(coords: Pair<Int, Int>): Pair<Int, Int> {
        val originX = width / 2
        val originY = height - gridSize - 100
        return Pair((originX + (coords.first/30) * gridSize), (originY - (coords.second/30) * gridSize))
    }

    fun showQuickOnMaze(path: List<Pair<Int, Int>>) {
        pathPoints.clear() // Alte Punkte entfernen
        pathPoints.addAll(path) // Neue Punkte speichern
        repaint() // Aktualisiere das Panel
    }

    //fun showPathOnMaze

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        // Setze den Hintergrund auf Weiß
        g2.color = Color.WHITE
        g2.fillRect(0, 0, width, height)

        // Zeichne das Overlay-Bild oben rechts
        if (overlayImage != null) {
            val overlayWidth = 450  // Breite des Overlay-Bildes
            val overlayHeight = 450 // Höhe des Overlay-Bildes
            val xPosition = 0 // X-Koordinate für rechtsbündige Position
            val yPosition = 0 // Y-Koordinate für obere Ecke
            g2.drawImage(overlayImage, xPosition, yPosition, overlayWidth, overlayHeight, null)
        }



        // Zeichne alle Räume
        for (room in rooms) {
            drawTile(g2, room.position, room.color, room.walls)

            g2.color = Color.BLACK
            for (coords in pathPoints) {
                val guiCoords = mazeCoordToGuiCoord(coords)
                val centerX = guiCoords.first + gridSize / 2
                val centerY = guiCoords.second + gridSize / 2
                val radius = 5 // Radius des Punkts
                g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2)
            }
        }

        // Zeichne Status-Text und Debug-Nachricht
        drawStatusText(g2)
        drawDebugMessage(g2)
    }


    private fun drawTile(g: Graphics2D, position: Pair<Int, Int>, color: TileColor, walls: List<Boolean>) {
        val x = position.first
        val y = position.second
        val wallThickness = 2 * scale

        when (color) {
            TileColor.GREEN -> g.color = Color.GREEN
            TileColor.BLUE -> g.color = Color.BLUE
            TileColor.RED -> g.color = Color.RED
            TileColor.NONE -> g.color = Color.LIGHT_GRAY
        }
        g.fillRect(x, y, gridSize, gridSize)

        g.color = Color.BLACK
        if (!walls[0]) g.fillRect(x, y - wallThickness, gridSize, wallThickness) // North
        if (!walls[1]) g.fillRect(x + gridSize, y, wallThickness, gridSize) // East
        if (!walls[2]) g.fillRect(x, y + gridSize, gridSize, wallThickness) // South
        if (!walls[3]) g.fillRect(x - wallThickness, y, wallThickness, gridSize) // West


    }
    private fun drawStatusText(g: Graphics2D) {
        // Set font and color
        g.color = Color.BLACK
        g.font = Font("Arial", Font.BOLD, 16)

        // Text for current position and facing
        val positionText = "Position: ${GraphFrontend.currentPosition}"
        val facingText = "Facing: ${GraphFrontend.facing}"

        // Draw the text in the upper left corner
        g.drawString(positionText, 32, 55) //hier
        g.drawString(facingText, 32, 73)//hier
    }


    data class Room(var color: TileColor, var walls: List<Boolean>, val position: Pair<Int, Int>)
}

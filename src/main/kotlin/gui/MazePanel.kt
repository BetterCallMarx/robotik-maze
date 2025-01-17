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
    private val rooms = mutableListOf<Room>() // liste der räume die in gui dargestellt werden
    private val scale = 2 // skallierung für die darstellung
    private val gridSize = 32 * scale // größe eines rasters im laybrinth
    private val pathPoints = mutableListOf<Pair<Int, Int>>() // liste der punkte für den quickestpath
    private val overlayImage: Image // overlay cat img

    init {
        // laden von cat
        overlayImage = ImageIO.read(File("src/GOAT.png"))

        // timer zur regelmäßigen aktualisierung der GUI
        val timer = Timer(100) { repaint() }
        timer.start()
    }

    /**
     * zeichnet debug nachricht auf der gui
     * (global debug msg)
     */
    private fun drawDebugMessage(g: Graphics2D) {
        g.color = Color.BLACK
        g.font = Font("Arial", Font.PLAIN, 16)
        g.drawString("Debug: ${DebugMessage.debugMessage}", 30, 90)
    }

    /**
     * fügt ein neuen raum hinzu oder aktuallisiert ihn
     * - tile -> das tile objekt das hinzugefügt oder aktualisiert wird.
     */
    fun addTile(tile: Tile) {
        val walls = mutableListOf(
            tile.northOpen,
            tile.eastOpen,
            tile.southOpen,
            tile.westOpen
        )

        // prüfen ob ein raum bereits exestiert
        val existingRoomIndex = rooms.indexOfFirst { it.position == mazeCoordToGuiCoord(tile.coordinates) }

        if (existingRoomIndex >= 0) {
            // aktualliesieren von bestehenden raumes
            val existingRoom = rooms[existingRoomIndex]
            existingRoom.color = tile.color
            existingRoom.walls = walls
        } else {
            // erstellen von neuen raum
            val newRoom = Room(tile.color, walls, mazeCoordToGuiCoord(tile.coordinates))
            rooms.add(newRoom)
        }

        // aktualliesieren der GUI
        repaint()
    }

    /**
     * konventiert die koordinaten in GUI koordinaten
     * - coords -> labyrinth koordinaten (x, y).
     * - rückgabewert -> GUI kooridnaten (x, y).
     */
    private fun mazeCoordToGuiCoord(coords: Pair<Int, Int>): Pair<Int, Int> {
        val originX = width / 2 // Zentrum der GUI (x)
        val originY = height - gridSize - 100 // Startpunkt (y) von unten
        return Pair((originX + (coords.first / 30) * gridSize), (originY - (coords.second / 30) * gridSize))
    }

    /**
     * visualisiert schnellsten weg im laybrinth
     * - path -> beschreibt schnellsten weg (zeigt die koordinaten)
     */
    fun showQuickOnMaze(path: List<Pair<Int, Int>>) {
        pathPoints.clear()
        pathPoints.addAll(path)
        repaint()
    }

    /**
     * zeichnet, hintergrund. overlay pic, räume, schnellsten weg, status und debug text
     */
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        // background white
        g2.color = Color.WHITE
        g2.fillRect(0, 0, width, height)

        // cat pic top left
        if (overlayImage != null) {
            val overlayWidth = 450
            val overlayHeight = 450
            val xPosition = 0
            val yPosition = 0
            g2.drawImage(overlayImage, xPosition, yPosition, overlayWidth, overlayHeight, null)
        }

        // räume zeichnen
        for (room in rooms) {
            drawTile(g2, room.position, room.color, room.walls)
        }

        // quickest path zeichnen
        g2.color = Color.BLACK
        for (coords in pathPoints) {
            val guiCoords = mazeCoordToGuiCoord(coords)
            val centerX = guiCoords.first + gridSize / 2
            val centerY = guiCoords.second + gridSize / 2
            val radius = 5
            g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2)
        }

        // status+ debug text
        drawStatusText(g2)
        drawDebugMessage(g2)
    }

    /**
     * zeichnet boden/räume und wände
     */
    private fun drawTile(g: Graphics2D, position: Pair<Int, Int>, color: TileColor, walls: List<Boolean>) {
        val x = position.first
        val y = position.second
        val wallThickness = 2 * scale

        // setzt farbe
        g.color = when (color) {
            TileColor.GREEN -> Color.GREEN
            TileColor.BLUE -> Color.BLUE
            TileColor.RED -> Color.RED
            TileColor.NONE -> Color.LIGHT_GRAY
        }
        g.fillRect(x, y, gridSize, gridSize)

        // zeichnet wände (falls vorhanden)
        g.color = Color.BLACK
        if (!walls[0]) g.fillRect(x, y - wallThickness, gridSize, wallThickness) // North
        if (!walls[1]) g.fillRect(x + gridSize, y, wallThickness, gridSize) // East
        if (!walls[2]) g.fillRect(x, y + gridSize, gridSize, wallThickness) // South
        if (!walls[3]) g.fillRect(x - wallThickness, y, wallThickness, gridSize) // West
    }

    /**
     * status informationen des roboters (position und himmelsrichtung)
     */
    private fun drawStatusText(g: Graphics2D) {
        g.color = Color.BLACK
        g.font = Font("Arial", Font.BOLD, 16)

        // position und ausrichtung des roboters
        val positionText = "Position: ${GraphFrontend.currentPosition}"
        val facingText = "Facing: ${GraphFrontend.facing}"

        g.drawString(positionText, 32, 55)
        g.drawString(facingText, 32, 73)
    }

    /**
     * datenklasse zum speichern der informationen der räume
     * - color-> farbe des raumes
     * - walls-> wände der räume
     * - position -> gui koordinaten des raumes
     */
    data class Room(var color: TileColor, var walls: List<Boolean>, val position: Pair<Int, Int>)
}

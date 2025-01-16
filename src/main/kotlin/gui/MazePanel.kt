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
    private val pathPoints = mutableListOf<Pair<Int, Int>>() // Speichert die Punkte des Pfads
    private val movementPath = mutableListOf<Pair<Int, Int>>() // Speichert den Live-Fahrweg
    private val overlayImage: Image
    private var debugMessage: String = "" // Debug-Nachricht

    init {
        // Overlay-Bild laden
        overlayImage = ImageIO.read(File("src/GOAT.png"))
        Timer(100) { repaint() }.start() // Repaint alle 100ms
    }

    fun addTile(tile: Tile) {
        val walls = listOf(
            tile.northOpen,
            tile.eastOpen,
            tile.southOpen,
            tile.westOpen
        )
        val newRoom = Room(tile.color, walls, mazeCoordToGuiCoord(tile.coordinates))
        rooms.add(newRoom)
        repaint()
    }

    fun showQuickOnMaze(path: List<Pair<Int, Int>>) {
        pathPoints.clear()
        pathPoints.addAll(path)
        repaint()
    }

    fun updateMovementPath(newPoint: Pair<Int, Int>) {
        movementPath.add(newPoint)
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        // Hintergrund
        g2.color = Color.WHITE
        g2.fillRect(0, 0, width, height)

        // Overlay zeichnen
        g2.drawImage(overlayImage, 0, 0, 450, 450, null)

        // Räume und Pfade zeichnen
        drawRooms(g2)
        drawPathPoints(g2)
        drawMovementPath(g2)

        // Status-Text und Debug-Nachricht
        drawStatusText(g2)
        drawDebugMessage(g2)
    }

    private fun drawRooms(g: Graphics2D) {
        rooms.forEach { room ->
            drawTile(g, room.position, room.color, room.walls)
        }
    }

    private fun drawPathPoints(g: Graphics2D) {
        g.color = Color.BLACK
        pathPoints.forEach { coords ->
            val guiCoords = mazeCoordToGuiCoord(coords)
            val centerX = guiCoords.first + gridSize / 2
            val centerY = guiCoords.second + gridSize / 2
            val radius = 5
            g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2)
        }
    }

    private fun drawMovementPath(g: Graphics2D) {
        if (movementPath.size > 1) {
            g.color = Color.ORANGE
            for (i in 0 until movementPath.size - 1) {
                val start = mazeCoordToGuiCoord(movementPath[i])
                val end = mazeCoordToGuiCoord(movementPath[i + 1])
                g.drawLine(
                    start.first + gridSize / 2, start.second + gridSize / 2,
                    end.first + gridSize / 2, end.second + gridSize / 2
                )
                val triangle = createTriangle(
                    start.first + (end.first - start.first) / 2 + gridSize / 2,
                    start.second + (end.second - start.second) / 2 + gridSize / 2,
                    end.first - start.first, end.second - start.second
                )
                g.fillPolygon(triangle)
            }
        }
    }

    private fun drawTile(g: Graphics2D, position: Pair<Int, Int>, color: TileColor, walls: List<Boolean>) {
        val x = position.first
        val y = position.second
        val wallThickness = 2 * scale

        g.color = when (color) {
            TileColor.GREEN -> Color.GREEN
            TileColor.BLUE -> Color.BLUE
            TileColor.RED -> Color.RED
            TileColor.NONE -> Color.LIGHT_GRAY
        }
        g.fillRect(x, y, gridSize, gridSize)

        g.color = Color.BLACK
        if (!walls[0]) g.fillRect(x, y - wallThickness, gridSize, wallThickness) // North
        if (!walls[1]) g.fillRect(x + gridSize, y, wallThickness, gridSize) // East
        if (!walls[2]) g.fillRect(x, y + gridSize, gridSize, wallThickness) // South
        if (!walls[3]) g.fillRect(x - wallThickness, y, wallThickness, gridSize) // West
    }

    private fun mazeCoordToGuiCoord(coords: Pair<Int, Int>): Pair<Int, Int> {
        val originX = width / 2
        val originY = height - gridSize - 100
        return Pair(originX + coords.first / 30 * gridSize, originY - coords.second / 30 * gridSize)
    }

    private fun drawStatusText(g: Graphics2D) {
        g.color = Color.BLACK
        g.font = Font("Arial", Font.BOLD, 16)
        g.drawString("Position: ${GraphFrontend.currentPosition}", 32, 55)
        g.drawString("Facing: ${GraphFrontend.facing}", 32, 73)
    }

    private fun drawDebugMessage(g: Graphics2D) {
        g.color = Color.BLACK
        g.font = Font("Arial", Font.PLAIN, 16)
        g.drawString("Debug: $debugMessage", 30, 90)
    }

    private fun createTriangle(midX: Int, midY: Int, dx: Int, dy: Int): Polygon {
        val baseSize = 10
        val triangle = Polygon()
        triangle.addPoint(midX, midY)
        triangle.addPoint(midX - dy / baseSize, midY + dx / baseSize)
        triangle.addPoint(midX + dy / baseSize, midY - dx / baseSize)
        return triangle
    }

    data class Room(val color: TileColor, val walls: List<Boolean>, val position: Pair<Int, Int>)
}

package gui

import graphing.Tile
import enums.TileColor
import graphing.GraphFrontend
import java.awt.*
import javax.swing.JPanel
import javax.swing.Timer

class MazePanel : JPanel() {
    private val rooms = mutableListOf<Room>()
    private val scale = 3
    private val gridSize = 32 * scale

    init {
        background = Color(255, 255, 255) // White background
        val timer = Timer(100) { repaint() } // Repaints every 50 milliseconds
        timer.start()
    }

    fun addTile(tile: Tile) {
        val walls = mutableListOf(
            tile.northOpen,
            tile.eastOpen,
            tile.southOpen,
            tile.westOpen
        )
        val newRoom = Room(tile.color, walls, mazeCoordToGuiCoord(tile.coordinates))
        println(newRoom.position)
        rooms.add(newRoom)
        repaint()
    }

    private fun mazeCoordToGuiCoord(coords: Pair<Int, Int>): Pair<Int, Int> {
        val originX = width / 2
        val originY = height - gridSize - 30
        return Pair((originX + (coords.first/30) * gridSize), (originY - (coords.second/30) * gridSize))
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        for (room in rooms) {
            drawTile(g2, room.position, room.color, room.walls)
        }
        drawStatusText(g2)
    }

    private fun drawTile(g: Graphics2D, position: Pair<Int, Int>, color: TileColor, walls: List<Boolean>) {
        val x = position.first
        val y = position.second
        val wallThickness = 2 * scale

        when (color) {
            TileColor.GREEN -> g.color = Color.GREEN
            TileColor.BLUE -> g.color = Color.BLUE
            TileColor.RED -> g.color = Color.RED
            TileColor.NONE -> g.color = Color.WHITE
        }
        g.fillRect(x, y, gridSize, gridSize)

        g.color = Color.BLACK
        if (!walls[0]) g.fillRect(x, y - wallThickness, gridSize, wallThickness) // North
        if (!walls[1]) g.fillRect(x + gridSize, y, wallThickness, gridSize) // East
        if (!walls[2]) g.fillRect(x, y + gridSize, gridSize, wallThickness) // South
        if (!walls[3]) g.fillRect(x - wallThickness, y, wallThickness, gridSize) // West

        g.color = Color.LIGHT_GRAY
        if (!walls[0]) {
            g.fillRect(x - wallThickness, y - wallThickness, wallThickness, wallThickness)
            g.fillRect(x + gridSize, y - wallThickness, wallThickness, wallThickness)
        }
        if (!walls[1]) {
            g.fillRect(x + gridSize, y - wallThickness, wallThickness, wallThickness)
            g.fillRect(x + gridSize, y + gridSize, wallThickness, wallThickness)
        }
        if (!walls[2]) {
            g.fillRect(x + gridSize, y + gridSize, wallThickness, wallThickness)
            g.fillRect(x - wallThickness, y + gridSize, wallThickness, wallThickness)
        }
        if (!walls[3]) {
            g.fillRect(x - wallThickness, y - wallThickness, wallThickness, wallThickness)
            g.fillRect(x - wallThickness, y + gridSize, wallThickness, wallThickness)
        }
    }
    private fun drawStatusText(g: Graphics2D) {
        // Set font and color
        g.color = Color.BLACK
        g.font = Font("Arial", Font.BOLD, 16)

        // Text for current position and facing
        val positionText = "Position: ${GraphFrontend.currentPosition}"
        val facingText = "Facing: ${GraphFrontend.facing}"

        // Draw the text at the upper left corner
        g.drawString(positionText, 10, 20)
        g.drawString(facingText, 10, 40)
    }


    data class Room(val color: TileColor, val walls: List<Boolean>, val position: Pair<Int, Int>)
}

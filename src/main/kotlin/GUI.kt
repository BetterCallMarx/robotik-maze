import java.awt.*
import javax.swing.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import kotlin.random.Random

class GUI : JFrame() {

    init {
        title = "Dynamic Maze GUI"
        minimumSize = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE

        val mazePanel = MazePanel()
        add(mazePanel, BorderLayout.CENTER)

        isVisible = true
    }
}

class MazePanel : JPanel() {

    private val rooms = mutableListOf<Room>()

    init {
        background = Color(255, 253, 208) // Creme-Ton

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                addTile(e.x, e.y)
            }
        })
    }

    // Raum hinzufügen
    fun addTile(x: Int, y: Int) {
        val randomColor = when (Random.nextInt(3)) {
            0 -> Color.GREEN
            1 -> Color.RED
            else -> Color.BLUE
        }

        val walls = mutableListOf(
            Random.nextBoolean(), // Obere Wand
            Random.nextBoolean(), // Rechte Wand
            Random.nextBoolean(), // Untere Wand
            Random.nextBoolean()  // Linke Wand
        )

        // Sicherstellen, dass mindestens eine Wand vorhanden ist
        if (!walls.contains(true)) {
            walls[Random.nextInt(4)] = true
        }

        val newRoom = Room(randomColor, walls, Point(x, y))
        rooms.add(newRoom)

        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        for (room in rooms) {
            drawTile(g2, room.position, room.color, room.walls)
        }
    }

    // Raum zeichnen
    private fun drawTile(g: Graphics2D, position: Point, color: Color, walls: List<Boolean>) {
        val x = position.x
        val y = position.y
        val wallThickness = 5 // Wandstärke auf 5 reduzieren

        // Boden zeichnen
        g.color = color
        g.fillRect(x, y, 75, 75) // Raumgröße auf 75x75 reduzieren

        // Wände zeichnen
        g.color = Color.WHITE

        if (walls[0]) g.fillRect(x, y - wallThickness, 75, wallThickness) // Obere Wand
        if (walls[1]) g.fillRect(x + 75, y, wallThickness, 75) // Rechte Wand
        if (walls[2]) g.fillRect(x, y + 75, 75, wallThickness) // Untere Wand
        if (walls[3]) g.fillRect(x - wallThickness, y, wallThickness, 75) // Linke Wand

        // Leisten nur an den Ecken
        g.color = Color.LIGHT_GRAY

        // Leisten für jede Wand am Anfang und Ende
        if (walls[0]) {
            // Leisten oben links und oben rechts
            g.fillRect(x - wallThickness, y - wallThickness, wallThickness, wallThickness) // Leiste oben links
            g.fillRect(x + 75, y - wallThickness, wallThickness, wallThickness) // Leiste oben rechts
        }
        if (walls[1]) {
            // Leisten rechts oben und rechts unten
            g.fillRect(x + 75, y - wallThickness, wallThickness, wallThickness) // Leiste rechts oben
            g.fillRect(x + 75, y + 75, wallThickness, wallThickness) // Leiste rechts unten
        }
        if (walls[2]) {
            // Leisten unten rechts und unten links
            g.fillRect(x + 75, y + 75, wallThickness, wallThickness) // Leiste unten rechts
            g.fillRect(x - wallThickness, y + 75, wallThickness, wallThickness) // Leiste unten links
        }
        if (walls[3]) {
            // Leisten links oben und links unten
            g.fillRect(x - wallThickness, y - wallThickness, wallThickness, wallThickness) // Leiste links oben
            g.fillRect(x - wallThickness, y + 75, wallThickness, wallThickness) // Leiste links unten
        }
    }

    // Datenklasse für einen Raum
    data class Room(val color: Color, val walls: List<Boolean>, val position: Point)
}

fun main() {
    GUI()
}

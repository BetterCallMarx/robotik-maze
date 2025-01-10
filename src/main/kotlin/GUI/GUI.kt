package GUI

import enums.Direction
import enums.TileColor
import graphing.GraphFrontend
import graphing.Tile
import graphing.Tree
import gui.MazePanel
import java.awt.*
import javax.swing.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import Robot

class GUI : JFrame() {

    private val robot: Robot = Robot()

    init {

        title = "Dynamic Maze GUI with Controls"
        extendedState = MAXIMIZED_BOTH
        isUndecorated = false
        //minimumSize = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        // Maze Panel
        val mazePanel = MazePanel()
        add(mazePanel, BorderLayout.CENTER)

        // Button Panel
        val buttonPanel = JPanel(GridLayout(2, 3)) // Arrange buttons in 2x3 grid
        addButtonControls(buttonPanel)
        add(buttonPanel, BorderLayout.SOUTH)

        isVisible = true
    }

    private fun addButtonControls(panel: JPanel) {
        panel.add(JPanel()) // Empty space

        val forward = JButton("A")
        forward.addActionListener {
            println(GraphFrontend.currentPosition)
            robot.driveForward()
            GraphFrontend.visitedPositions.add(GraphFrontend.currentPosition)
            println(GraphFrontend.updatePosition(Pair(30, 30)))
            println(GraphFrontend.currentPosition)
        }
        panel.add(forward)

        panel.add(JPanel()) // Empty space

        val left = JButton("<")
        left.addActionListener {
            robot.turnLeft()
        }
        panel.add(left)

        val right = JButton(">")
        right.addActionListener {
            robot.turnRight()
        }
        panel.add(right)

        val back = JButton("V")
        back.addActionListener {
            robot.driveBackward()
        }
        panel.add(back)

        val turnHead = JButton("*")
        turnHead.addActionListener {
            val distances: MutableList<Pair<Int, Direction>> = robot.completeHeadTurn()
            val color = robot.colorSensorColor()
            val tile: Tile = GraphFrontend.createTile(distances, GraphFrontend.colorToEnum(color))
            tile.printTile()

            if (GraphFrontend.currentPosition == Pair(0, 0) && !Tree.rootSet) {
                Tree.addRoot(tile)
            } else {
                Tree.addTileToTile(
                    GraphFrontend.currentPosition,
                    GraphFrontend.visitedPositions.last(),
                    GraphFrontend.getInverseDirection(),
                    tile
                )
            }
            println(tile)
        }
        panel.add(turnHead)

        val test = JButton("T")
        test.addActionListener {
            println("Test button pressed")
        }
        panel.add(test)
    }
}


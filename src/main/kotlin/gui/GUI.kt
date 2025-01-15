package gui

import Robot
import de.fhkiel.rob.legoosctester.osc.OSCSender
import enums.Direction
import enums.TileColor
import graphing.GraphFrontend
import graphing.Tile
import graphing.Tree
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel


class GUI : JFrame() {

    private val robot: Robot = Robot()
    private val mazePanel : MazePanel = MazePanel()

    init {

        title = "GUI"
        extendedState = MAXIMIZED_BOTH
        isUndecorated = false
        isResizable = false
        //minimumSize = Dimension(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()


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
            robot.drive(500,612)
        }
        panel.add(forward)


        panel.add(JPanel()) // Empty space

        val left = JButton("<")
        left.addActionListener {
            robot.turn(500,187,-187)

        }
        panel.add(left)

        val right = JButton(">")
        right.addActionListener {
            robot.turn(500,-187,187)
        }
        panel.add(right)

        val back = JButton("V")
        back.addActionListener {
            robot.drive(500,-612)
            DebugMessage.debugMessage = "hallo"
        }
        panel.add(back)

        val turnHead = JButton("*")
        turnHead.addActionListener {
            val distances: MutableList<Pair<Int, Direction>> = robot.completeHeadTurn()
            val color = robot.colorSensorColor()
            if(distances.isNotEmpty() && color.isNotEmpty()) {
                val tile: Tile = GraphFrontend.createTile(distances, GraphFrontend.colorToEnum(color))
                tile.printTile()
                mazePanel.addTile(tile)
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
                Tree.printTree()
            }else{
                DebugMessage.debugMessage = "Es konnte kein Tile erfasst werden"
            }

        }
        panel.add(turnHead)

        val test = JButton("Test")
        test.addActionListener {
            println("Test button pressed")
            val root = Tile(false, true, true, false, TileColor.RED, Pair(0, 0))
            val tile1 = Tile(true, false, false, true, TileColor.RED, Pair(30, 0))
            val tile2 = Tile(true, false, true, false, TileColor.RED, Pair(30, 30))
            val tile3 = Tile(true, false, true, false, TileColor.RED, Pair(30, 60))

            Tree.addRoot(root)
            Tree.addTileToTile(tile1.coordinates, root.coordinates, Direction.WEST, tile1)
            Tree.addTileToTile(tile2.coordinates, tile1.coordinates, Direction.SOUTH, tile2)
            Tree.addTileToTile(tile3.coordinates, tile2.coordinates, Direction.SOUTH, tile3)

            mazePanel.addTile(root)
            mazePanel.addTile(tile1)
            mazePanel.addTile(tile2)
            mazePanel.addTile(tile3)

            val path = Tree.findShortestPathToRoot(tile3.coordinates)
            println(path)
        }
        panel.add(test)

        val tree = JButton("Tree")
        tree.addActionListener {
            val path = Tree.findShortestPathToRoot(GraphFrontend.currentPosition)
            println(path)


        }
        panel.add(tree)

        val exit = JButton("->")
        exit.addActionListener {
            val path = Tree.findShortestPathToRoot(Pair(30, 60))
            println(path)
            val directions = GraphFrontend.getTotalDirections(path)
            println(directions)
            var success = false
            val maxRetries = 5
            var attempts = 0
            while (!success && attempts < maxRetries) {
                attempts++
                try {
                    robot.driveToExit(path, directions)
                    success = true // If the function completes without exceptions, set success to true
                } catch (e: Exception) {
                    println("An error occurred: ${e.message}. Retrying...")
                }
            }
        }
        panel.add(exit)
    }
}


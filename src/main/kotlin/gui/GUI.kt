package gui

import Robot
import de.fhkiel.rob.legoosctester.osc.OSCSender
import enums.Direction
import enums.TileColor
import graphing.GraphFrontend
import graphing.Tile
import graphing.Tree
import java.awt.*
import javax.swing.*

class GUI : JFrame() {

    private val robot: Robot = Robot()
    private val mazePanel : MazePanel = MazePanel()

    init {
        title = "GUI"
        extendedState = MAXIMIZED_BOTH
        isUndecorated = false
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(mazePanel, BorderLayout.CENTER)

        // Button Panel
        val buttonPanel = JPanel(GridBagLayout()) // Using GridBagLayout for more control
        addButtonControls(buttonPanel)
        add(buttonPanel, BorderLayout.SOUTH)

        isVisible = true
    }

    private fun addButtonControls(panel: JPanel) {
        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.CENTER // Align to the center of grid cells
        gbc.insets = Insets(5, 5, 5, 5) // Add spacing between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL // Ensure buttons fill horizontally

        // Setze den Hintergrund des gesamten Button-Panels auf eine gewünschte Farbe
        panel.background = Color(255,255,255);



        // Create two sub-grids for left and right sections
        val leftPanel = JPanel(GridBagLayout()) // Left grid for arrow buttons
        val rightPanel = JPanel(GridBagLayout()) // Right grid for other buttons

        // Add the buttons to the left panel (Arrow Buttons)
        addArrowButtons(leftPanel, gbc)

        // Add the buttons to the right panel (Other Buttons)
        addOtherButtons(rightPanel, gbc)

        // Place the left and right panels in the main button panel
        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(leftPanel, gbc) // Left panel

        gbc.gridx = 1
        gbc.gridy = 0
        panel.add(rightPanel, gbc) // Right panel
    }

    private fun addArrowButtons(panel: JPanel, gbc: GridBagConstraints) {
        // Forward Button
        val forward = JButton("^")
        forward.addActionListener { robot.drive(500, 612) }
        forward.font = Font("Arial", Font.BOLD, 15)  // Increase font size
        gbc.gridx = 1
        gbc.gridy = 0
        panel.add(forward, gbc)


        // Left Button
        val left = JButton("<")
        left.addActionListener { robot.turn(500, 187, -187) }
        left.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(left, gbc)

        // Right Button
        val right = JButton(">")
        right.addActionListener { robot.turn(500, -187, 187) }
        right.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 2
        gbc.gridy = 1
        panel.add(right, gbc)

        // Back Button
        val back = JButton("v")
        back.addActionListener { robot.drive(500, -612) }
        back.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 1
        gbc.gridy = 2
        panel.add(back, gbc)
    }

    private fun addOtherButtons(panel: JPanel, gbc: GridBagConstraints) {
        // Turn Head Button
        val turnHead = JButton("Look")
        turnHead.addActionListener {
            val distances: MutableList<Pair<Int, Direction>> = robot.completeHeadTurn()
            val color = robot.colorSensorColor()
            if (distances.isNotEmpty() && color.isNotEmpty()) {
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
            } else {
                DebugMessage.debugMessage = "Es konnte kein Tile erfasst werden"
            }
        }
        turnHead.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(turnHead, gbc)

        // Test Button
        val test = JButton("Test")
        test.addActionListener {
            println("Test button pressed")
            val root = Tile(false, true, true, false, TileColor.RED, Pair(0, 0))
            val tile1 = Tile(true, false, false, true, TileColor.RED, Pair(30, 0))
            val tile2 = Tile(true, false, true, false, TileColor.RED, Pair(30, 30))
            val tile3 = Tile(true, false, true, false, TileColor.NONE, Pair(30, 60))

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
        test.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(test, gbc)

        // Quickest Path Button
        val quickestpath = JButton("Quickest")
        quickestpath.addActionListener {
            val path = Tree.findShortestPathToRoot(Pair(30,60))
            if (path.isEmpty()) {
                println("Kein gültiger Pfad gefunden!")
            } else {
                println("Kürzester Pfad zur Wurzel: $path")
                mazePanel.showQuickOnMaze(path)
            }
        }
        quickestpath.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 2
        panel.add(quickestpath, gbc)

        // Exit Button
        val exit = JButton("Back")
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
                    success = true
                } catch (e: Exception) {
                    println("An error occurred: ${e.message}. Retrying...")
                }
            }
        }
        exit.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 3
        panel.add(exit, gbc)
    }
}

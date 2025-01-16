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
    private val mazePanel: MazePanel = MazePanel()

    init {
        title = "GUI"
        extendedState = MAXIMIZED_BOTH
        isUndecorated = false
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(mazePanel, BorderLayout.CENTER)

        // Button Panel
        val buttonPanel = JPanel(GridBagLayout())
        addButtonControls(buttonPanel)
        add(buttonPanel, BorderLayout.SOUTH)

        isVisible = true
    }

    private fun addButtonControls(panel: JPanel) {
        val gbc = GridBagConstraints().apply {
            anchor = GridBagConstraints.CENTER
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
        }

        panel.background = Color(255, 255, 255)

        val leftPanel = JPanel(GridBagLayout())
        val rightPanel = JPanel(GridBagLayout())

        addArrowButtons(leftPanel, gbc)
        addOtherButtons(rightPanel, gbc)

        panel.apply {
            gbc.gridx = 0
            gbc.gridy = 0
            add(leftPanel, gbc)

            gbc.gridx = 1
            gbc.gridy = 0
            add(rightPanel, gbc)
        }
    }

    private fun addArrowButtons(panel: JPanel, gbc: GridBagConstraints) {
        val font = Font("Arial", Font.BOLD, 15)

        // Forward Button
        val forward = JButton("^").apply {
            addActionListener { robot.drive(500, 612) }
            this.font = font
        }
        gbc.gridx = 1
        gbc.gridy = 0
        panel.add(forward, gbc)

        // Left Button
        val left = JButton("<").apply {
            addActionListener { robot.turn(500, 187, -187) }
            this.font = font
        }
        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(left, gbc)

        // Right Button
        val right = JButton(">").apply {
            addActionListener { robot.turn(500, -187, 187) }
            this.font = font
        }
        gbc.gridx = 2
        gbc.gridy = 1
        panel.add(right, gbc)

        // Back Button
        val back = JButton("v").apply {
            addActionListener { robot.drive(500, -612) }
            this.font = font
        }
        gbc.gridx = 1
        gbc.gridy = 2
        panel.add(back, gbc)
    }

    private fun addOtherButtons(panel: JPanel, gbc: GridBagConstraints) {
        val font = Font("Arial", Font.BOLD, 15)

        // Turn Head Button
        val turnHead = JButton("Look").apply {
            addActionListener {
                val distances = robot.completeHeadTurn()
                val color = robot.colorSensorColor()
                if (distances.isNotEmpty() && color.isNotEmpty()) {
                    val tile = GraphFrontend.createTile(distances, GraphFrontend.colorToEnum(color))
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
            this.font = font
        }
        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(turnHead, gbc)

        // Test Button
        val test = JButton("Test").apply {
            addActionListener {
                println("Test button pressed")
                val root = Tile(false, true, true, false, TileColor.NONE, Pair(0, 0))
                val tile1 = Tile(true, false, false, true, TileColor.NONE, Pair(30, 0))
                val tile2 = Tile(true, false, true, false, TileColor.NONE, Pair(30, 30))
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
            this.font = font
        }
        gbc.gridy = 1
        panel.add(test, gbc)

        // Quickest Path Button
        val quickestPath = JButton("Quickest").apply {
            addActionListener {
                val path = Tree.findShortestPathToRoot(Pair(30, 60))
                if (path.isEmpty()) {
                    println("Kein gültiger Pfad gefunden!")
                } else {
                    println("Kürzester Pfad zur Wurzel: $path")
                    mazePanel.showQuickOnMaze(path)
                }
            }
            this.font = font
        }
        gbc.gridy = 2
        panel.add(quickestPath, gbc)

        // Exit Button
        val exit = JButton("Back").apply {
            addActionListener {
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
            this.font = font
        }
        gbc.gridy = 3
        panel.add(exit, gbc)
    }
}

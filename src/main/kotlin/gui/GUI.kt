package gui

import Robot
import enums.Direction
import enums.TileColor
import graphing.GraphFrontend
import graphing.Tile
import graphing.Tree
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class GUI : JFrame() {

    private val robot: Robot = Robot() //roboter objekt zur steurung
    private val mazePanel : MazePanel = MazePanel()
    private var shiftPressed = false// status ob die shift taste gedrückt ist

    /**
     * einstellungen für das fenster, fenstergröße, fensterrahmen, fixirte größe, layout
     */

    init {
        title = "GUI"
        extendedState = MAXIMIZED_BOTH
        isUndecorated = false
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(mazePanel, BorderLayout.CENTER) // einsetzten des panels im zentrum des fensters

        /**
         * Button Panel
         */
        val buttonPanel = JPanel(GridBagLayout())//bessere kontrolle
        addButtonControls(buttonPanel)
        add(buttonPanel, BorderLayout.SOUTH)
        isVisible = true

        // tasturlistener für shift key
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { e ->
            when (e.id) {
                KeyEvent.KEY_PRESSED -> if (e.keyCode == KeyEvent.VK_SHIFT) shiftPressed = true
                KeyEvent.KEY_RELEASED -> if (e.keyCode == KeyEvent.VK_SHIFT) shiftPressed = false
            }
            false
        }
    }

    /**
     * steuerung der buttons
     */

    private fun addButtonControls(panel: JPanel) {
        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.CENTER //positon
        gbc.insets = Insets(5, 5, 5, 5) // spacing
        gbc.fill = GridBagConstraints.HORIZONTAL // position versichert horizontal

        // panel auf weiß
        panel.background = Color(255,255,255);

        // zwei sub grids für links und rechts
        val leftPanel = JPanel(GridBagLayout()) // links pfeil buttons
        val rightPanel = JPanel(GridBagLayout()) // rechts rest

        // hinzufügen von pfeil buttons links
        addArrowButtons(leftPanel, gbc)
        // andere buttons rechts
        addOtherButtons(rightPanel, gbc)
        // plazierung der sub panels ins main panel
        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(leftPanel, gbc) // linker panel


        gbc.gridx = 1
        gbc.gridy = 0
        panel.add(rightPanel, gbc) // rechter panel
    }


    /**
     * hinzufügen der arrow Buttons für das fahren (linker panel)
     * oben fahren  = ^
     * link drehung  = <
     * rechts drehung = >
     * unten fahren = v
     */
    private fun addArrowButtons(panel: JPanel, gbc: GridBagConstraints) {



        // vorwärts Button
        val forward = JButton("^")
        forward.addActionListener {
            if (shiftPressed) {
                robot.driveNoPosition(500,61)
            } else {
                robot.drive2(500, 612)
            }
        }
        forward.font = Font("Arial", Font.BOLD, 15)  // Increase font size
        gbc.gridx = 1
        gbc.gridy = 0
        panel.add(forward, gbc)

        // links Button
        val left = JButton("<")
        left.addActionListener {
            if (shiftPressed) {
                robot.turnNoDirection(500,45,-45)
            } else {
                robot.turn2(500, 187, -187)
            }
        }
        left.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(left, gbc)

        // rechts Button
        val right = JButton(">")
        right.addActionListener {
            if (shiftPressed) {
                robot.turnNoDirection(500,-45,45)
            } else {
                robot.turn2(500, -187, 187)
            }
        }
        right.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 2
        gbc.gridy = 1
        panel.add(right, gbc)

        // runter Button
        val back = JButton("v")
        back.addActionListener {
            if (shiftPressed) {
                robot.driveNoPosition(500, -61)
            } else {
                robot.drive2(500, -612)
            }
        }
        back.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 1
        gbc.gridy = 2
        panel.add(back, gbc)
    }

    /**
     * hinzufügen ander Steuerungen (rechter panel)
     * look = scannend er wände
     * test = simuliertes laybrint in der GUI für tests
     * quick = anzeigen des schnellste weges
     * back = zurückfahren über den schnellsten weg
     * pos = repositonierung des roboters
     */

    private fun addOtherButtons(panel: JPanel, gbc: GridBagConstraints) {
        // scan Button (kopf bewegt sich)
        val turnHead = JButton("Look")

        turnHead.addActionListener {
            try {
                val tile = robot.getTile()
                mazePanel.addTile(tile)
            }catch (e: Exception){
                DebugMessage.debugMessage = "Es konnte kein Tile erfasst werden ${e.message}"
            }

        }

        turnHead.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 0
        panel.add(turnHead, gbc)

        //test button
        val test = JButton("Test")
        test.addActionListener {

            val root = Tile(true, true, true, false, TileColor.NONE, Pair(0, 0)) // Entrance with walls on the east and south


            val tile1 = Tile(true, false, true, false, TileColor.NONE, Pair(0, 30)) // South open
            val tile2 = Tile(false, false, true, false, TileColor.NONE, Pair(0, 60)) // South and west walls
            val tile3 = Tile(false, true, true, false, TileColor.BLUE, Pair(0, 90)) // Blue Tile

            val tile7 = Tile(false, true, false, false, TileColor.NONE, Pair(30, 60))
            val tile4 = Tile(false, true, false, true, TileColor.NONE, Pair(30, 90))
            val tile5 = Tile(false, false, true, true, TileColor.NONE, Pair(60, 90)) // Open to south

            val tile6 = Tile(true,false , true, true, TileColor.RED, Pair(60, 60)) // West open
            val tile9 = Tile(true, false, true, false, TileColor.NONE, Pair(60, 30)) // Open to south
            val tile10 = Tile(true, false, false, true, TileColor.GREEN, Pair(60, 0)) // No walls
            val tile11 = Tile(false,true,false,true,TileColor.NONE,Pair(30,0))
            val tile8 = Tile(true,true,false,false,TileColor.NONE,Pair(30,30))

            Tree.addRoot(root)
            Tree.addTileToTile(tile1.coordinates, root.coordinates, Direction.SOUTH, tile1)
            Tree.addTileToTile(tile2.coordinates, tile1.coordinates, Direction.SOUTH, tile2)
            Tree.addTileToTile(tile3.coordinates, tile2.coordinates, Direction.SOUTH, tile3)
            Tree.addTileToTile(tile4.coordinates, tile3.coordinates, Direction.WEST, tile4)
            Tree.addTileToTile(tile5.coordinates, tile4.coordinates, Direction.WEST, tile5)

            Tree.addTileToTile(tile6.coordinates, tile5.coordinates, Direction.NORTH, tile6)
            Tree.addTileToTile(tile7.coordinates, tile6.coordinates, Direction.EAST, tile7)

            Tree.addTileToTile(tile8.coordinates, tile7.coordinates, Direction.NORTH, tile8)
            Tree.addTileToTile(tile9.coordinates, tile6.coordinates, Direction.NORTH, tile9)
            Tree.addTileToTile(tile10.coordinates, tile9.coordinates, Direction.NORTH, tile10)
            Tree.addTileToTile(tile11.coordinates, tile10.coordinates, Direction.EAST, tile10)
            Tree.addTileToTile(root.coordinates,tile11.coordinates,Direction.EAST,root)



            mazePanel.addTile(root)
            mazePanel.addTile(tile1)
            mazePanel.addTile(tile2)
            mazePanel.addTile(tile3)
            mazePanel.addTile(tile4)
            mazePanel.addTile(tile5)
            mazePanel.addTile(tile6)
            mazePanel.addTile(tile7)
            mazePanel.addTile(tile8)
            mazePanel.addTile(tile9)
            mazePanel.addTile(tile10)
            mazePanel.addTile(tile11)

        }
        test.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 1
        panel.add(test, gbc)

        // schnellster weg button
        val quickestpath = JButton("Quickest")
        quickestpath.addActionListener {
            val colorsToVisit = setOf(TileColor.BLUE, TileColor.RED, TileColor.GREEN) // Colored tiles to visit
            val path = Tree.findShortestPathThroughColorsAndReturn(GraphFrontend.currentPosition, colorsToVisit)
            val dir = GraphFrontend.getTotalDirections(path)
            println("Shortest Path: $path")
            println("Shortest Path: $dir")
            if (path.isEmpty()) {
                DebugMessage.debugMessage = "Kein gültiger Weg gefunden"
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
            robot.driveToExitRetry()
        }
        exit.font = Font("Arial", Font.BOLD, 15)
        gbc.gridx = 0
        gbc.gridy = 3
        panel.add(exit, gbc)

        //repositons button
        val position = JButton("Pos")
        position.addActionListener {
            robot.positionSelf()
        }
        panel.add(position)
    }
}

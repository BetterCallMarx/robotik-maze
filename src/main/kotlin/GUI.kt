import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import de.fhkiel.rob.legoosctester.osc.OSCSender
import enums.Direction
import graphing.GraphFrontend
import graphing.Node
import graphing.Tile
import graphing.Tree
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame

import javax.swing.JPanel

class GUI : JFrame() {

    val robot: Robot = Robot()


    init {
        title = "ButtonKram"
        minimumSize = Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE

        layout = GridLayout(3, 3)

        add(JPanel())
        val forward = JButton("A")
        forward.addActionListener {
            println(GraphFrontend.currentPosition)
            robot.driveForward()
            GraphFrontend.visitedPositions.add(GraphFrontend.currentPosition)
            println(GraphFrontend.updatePosition(Pair(30,30)))
            println(GraphFrontend.currentPosition)
        }
        add(forward)

        add(JPanel())
        val left = JButton("<")
        left.addActionListener {
            robot.turnLeft()
        }
        add(left)

        add(JPanel())
        val right = JButton(">")
        right.addActionListener {
            robot.turnRight()
        }
        add(right)

        add(JPanel())
        val back = JButton("V")
        back.addActionListener {
            robot.driveBackward()
        }
        add(back)

        add(JPanel())
        val turnHead = JButton("*")
        turnHead.addActionListener {

            //turn head and detect walls
            val distances: MutableList<Pair<Int,Direction>> = robot.completeHeadTurn()
            //detect color
            val color = robot.colorSensorColor()
            //create Tile based on sensor data
            val tile : Tile = GraphFrontend.createTile(distances,GraphFrontend.colorToEnum(color))
            tile.printTile()
            if(GraphFrontend.currentPosition == Pair(0,0) && !Tree.rootSet){
                Tree.addRoot(tile)
            }else {
                println("debug 1")
                Tree.addTileToTile(
                    GraphFrontend.currentPosition,
                    GraphFrontend.visitedPositions.last(),
                    GraphFrontend.getInverseDirection(),
                    tile
                )
                println("debug 2")

            }
            println(tile)
        }
        add(turnHead)

        add(JPanel())
        val test = JButton("T")
        test.addActionListener {

        }
        add(test)
        isVisible = true
    }

}
import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import graphing.GraphFrontend
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

        val receiver: OSCReceiver = OSCReceiver
        receiver.start()


        layout = GridLayout(3, 3)
        add(JPanel())
        val forward = JButton("A")
        forward.addActionListener {
            robot.driveForward()
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
            val distances: List<Int> = robot.completeHeadTurn()
            val string: String = GraphFrontend.createTile(distances)
            println(string)
        }
        add(turnHead)
        isVisible = true
    }

}
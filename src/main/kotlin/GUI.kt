import de.fhkiel.rob.legoosctester.gui.Incoming
import de.fhkiel.rob.legoosctester.gui.Outgoing
import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import de.fhkiel.rob.legoosctester.osc.OSCSender
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE

import javax.swing.JPanel

class GUI: JFrame() {


    init {
        title = "ButtonKram"
        minimumSize =  Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE

        val oscreceiver: OSCReceiver = OSCReceiver
        oscreceiver.start()


        layout = GridLayout(3, 3)
        add(JPanel())
        val forward = JButton("A")
        forward.addActionListener {
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ad/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ad/run/target", 100, 550)
        }
        add(forward)

        add(JPanel())
        val left = JButton("<")
        left.addActionListener {
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ad/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ad/multirun/target", 100, 183, -183)
        }
        add(left)

        add(JPanel())
        val right = JButton(">")
        right.addActionListener {
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ad/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ad/multirun/target", 100, -183, 183)
        }
        add(right)
        
        add(JPanel())
        val back = JButton("V")
        back.addActionListener {
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ab/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/ab/run/target", -100, -360)
        }
        add(back)

        add(JPanel())
        val turnHead = JButton("*")
        turnHead.addActionListener{

            var distance: Int = 0
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/run/target", 1000, 92)
            println("rechts")
            Thread.sleep(1500)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/stop")
            OSCSender("192.168.178.255", 9001).send("/robot/ultrasonic/s1/distance")
            //distance = oscreceiver.returnData()
            println(distance)
            Thread.sleep(1500)

            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/run/target", 1000, 92)
            println("hinten")
            Thread.sleep(1500)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/stop")
            OSCSender("192.168.178.255", 9001).send("/robot/ultrasonic/s1/distance")
            //distance = oscreceiver.returnData()
            println(distance)
            Thread.sleep(1500)

            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/run/target", 1000, 92)
            println("links")
            Thread.sleep(1500)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/stop")
            OSCSender("192.168.178.255", 9001).send("/robot/ultrasonic/s1/distance")
            //distance = oscreceiver.returnData()
            println(distance)
            Thread.sleep(1500)

            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/angle", 0)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/run/target", 1000,-272)
            println("vorne")
            Thread.sleep(1500)
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/stop")
            OSCSender("192.168.178.255", 9001).send("/robot/motor/c/angle", 0)


        }
        add(turnHead)
        isVisible = true
    }

}
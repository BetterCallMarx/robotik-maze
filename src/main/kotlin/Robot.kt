import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import de.fhkiel.rob.legoosctester.osc.OSCSender
import enums.Direction
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/*
TODO: Alle sensoren und motoren ab roboter fertig bringen
TODO: FUnktionen zu Bewegung und Erfassung von Sensordaten
TODO: Einfache Graph Bildung mittels odometrischen daten
TODO: Vielleicht homogene kooridaten in betracht ziehen
TODO: Je mehr informationen desto mehr certainty, eventuell für lokalisierung odometrische und entfernungssensor
TODO: Beim zurückfahren least squares slam verwenden um error zwischen poses zu minimieren
*/


//TODO für jeden Befehl eine Funktion
class Robot {
    lateinit var direction: Direction
    lateinit var currentData: Data
    val robotName: String = "robot"
    val ultraSonicPort: String = "s2"
    val colorSensorPort: String = "s4"
    val touchSensorPort: String = "s1"
    private val leftMotorPort: String = "d"
    private val rightMotorPort: String = "a"
    private val headMotorPort: String = "c"
    private val ipTarget: String = "192.168.178.255"
    private val port: Int = 9001

    init {
        OSCReceiver.start()
        OSCSender(ipTarget, port).send("/$robotName/motor/$headMotorPort/angle", 0)
    }


   /*fun resetPosition() {
        driveForward()
        while (!OSCReceiver.returnData().touched) {
            Thread.sleep(100)
        }
        driveBackward()
        Thread.sleep(1500)
        turnLeft()
        Thread.sleep(1500)
        driveBackward()
    }
*/

    //driving the robot forward, optimal inputs for a distance of 28cm is : 100, 550
    fun driveForward() {
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target", 100, 500)
    }

    //driving the robot backward, optimal inputs for a distance of 28cm is : 100, -550
    fun driveBackward() {
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target", 100, 500)
    }

    fun drive(speed: Int, angle: Int) {
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target", speed, angle)
    }

    //turn the Robot to the left optimal speed and angle for 90-degree turn: 100, 183, -183
    fun turnLeft() {
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send(
            "/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target",
            100,
            183,
            -183
        )
    }

    //turn the Robot to the right optimal speed and angle for a 90-degree turn: 100, -183, 183
    fun turnRight() {
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send(
            "/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target",
            100,
            -183,
            183
        )
    }

    fun turn(speed: Int, angle: Int, angle1: Int) {
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send(
            "/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target",
            speed,
            angle,
            angle1
        )
    }

    //one headturn
    private fun turnHead(speed: Int, angle: Int) {
        println("debug")
        var turned: Boolean = false
        val path: String = "/$robotName/motor/$headMotorPort/reached/target"
        OSCReceiver.subListener(path
        ) {
            if(it[0] as Int == angle){
                turned = true
                OSCReceiver.unsubListener(path)
            }else{
                OSCSender(ipTarget, port).send("/$robotName/motor/$headMotorPort/run/target", speed, angle)
            }
        }
        OSCSender(ipTarget, port).send("/$robotName/motor/$headMotorPort/run/target", speed, angle)
        while(!turned){
            sleep(10)
        }

    }

    fun colorSensorColor(): String {
        var color : String = ""
        val path: String = "/$robotName/color/$touchSensorPort/is"
        OSCReceiver.subListener(path
        ) {
            color = it[0] as String
            OSCReceiver.unsubListener(path)
        }
        OSCSender(ipTarget, port).send("/$robotName/color/$colorSensorPort")
        while(color.isNotBlank()){
            sleep(10)
        }
        return color
    }

    fun touchSensorTouched(): Boolean {
        var touched : Boolean = false
        var measured: Boolean = false
        val path: String = "/$robotName/touch/$touchSensorPort/pressed"
        OSCReceiver.subListener(path
        ) {
            touched = it[0] as Boolean
            OSCReceiver.unsubListener(path)
            measured = true
        }
        OSCSender(ipTarget, port).send("/$robotName/touch/$touchSensorPort")
        while(!measured){
            sleep(10)
        }
        return touched
    }

    private fun ultraSensorDistance(): Int {
        var distance : Int = -1
        val path: String = "/$robotName/ultrasonic/${ultraSonicPort}/distance/is"
        OSCReceiver.subListener(path
        ) {
            distance = it[0] as Int
            OSCReceiver.unsubListener(path)
        }
        OSCSender(ipTarget, port).send("/$robotName/ultrasonic/$ultraSonicPort/distance")
        while(distance<0){
            sleep(10)
        }
        return distance
    }



    fun completeHeadTurn(): List<Int> {
        val distances: MutableList<Int> = mutableListOf()
        val distanceNorth = ultraSensorDistance()
        distances.add(distanceNorth)


        turnHead(1000, 90)
        val distanceEast = ultraSensorDistance()
        distances.add(distanceEast)

        turnHead(1000, 180)
        val distanceSouth = ultraSensorDistance()
        distances.add(distanceSouth)

        turnHead(1000, -90)
        val distanceWest = ultraSensorDistance()
        distances.add(distanceWest)
        turnHead(1000, 0)

        return distances
    }

    private val touchListenerPath: String = "/$robotName/touch/$touchSensorPort/changed/pressed"
     fun subTouchListener(){
        OSCReceiver.subListener(touchListenerPath
        ) {
            println(it)
        }
        OSCSender(ipTarget, port).send("/$robotName/touch/$touchSensorPort/onchange/start")
    }

    fun unsubTouchListener() {
        OSCReceiver.unsubListener(touchListenerPath)
        OSCSender(ipTarget, port).send("/$robotName/touch/$touchSensorPort/onchange/stop")
    }


}
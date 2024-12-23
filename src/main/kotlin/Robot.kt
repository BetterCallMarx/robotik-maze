import de.fhkiel.rob.legoosctester.osc.OSCSender
import de.fhkiel.rob.legoosctester.osc.OSCReceiver
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
    private var oscReceiver: OSCReceiver = OSCReceiver
    lateinit var currentData: Data
    val robotName: String = "robot"
    val ultraSonicPort: String = "s1"
    val colorSensorPort: String = "s3"
    val touchSensorPort: String = "s4"
    val leftMotorPort: String = "d"
    val rightMotorPort: String = "a"
    val headMotorPort: String = "c"
    private val ipTarget: String = "192.168.178.255"
    private val port: Int = 9001

    init {
        oscReceiver.start()

    }

    //driving the robot forward, optimal inputs for a distance of 28cm is : 100, 550
    fun driveForward(speed: Int, angle: Int){
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target",speed,angle)
    }
    //driving the robot backward, optimal inputs for a distance of 28cm is : 100, -550
    fun driveBackward(speed: Int, angle: Int){
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target",speed,angle)
    }

    //turn the Robot to the left optimal speed and angle for 90-degree turn: 100, 183, -183
    fun turnLeft(speed: Int, angle: Int, angle1: Int){
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target", speed, angle, angle1)
    }

    //turn the Robot to the right optimal speed and angle for a 90-degree turn: 100, -183, 183
    fun turnRight(speed: Int, angle: Int, angle1: Int){
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target", speed, angle, angle1)
    }

    //one headturn
    fun turnHead(speed: Int, angle: Int){
        OSCSender(ipTarget, port).send("/$robotName/motor/$headMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$headMotorPort/run/target", speed, angle)
    }

    fun colorSensorColor(): String{
        OSCSender(ipTarget, port).send("/$robotName/color/$colorSensorPort")
        return oscReceiver.returnData().color
    }
    fun touchSensorTouched(): Boolean{
        OSCSender(ipTarget, port).send("/$robotName/touch/$touchSensorPort")
        return oscReceiver.returnData().touched
    }
    fun ultraSensorDistance(): Int{
        OSCSender(ipTarget, port).send("/$robotName/ultrasonic/$ultraSonicPort/distance")
        return oscReceiver.returnData().distance
    }

    fun completeHeadTurn(): List<Int>{
        val distances: MutableList<Int> = mutableListOf()
        val distanceNorth = ultraSensorDistance()
        distances.add(distanceNorth)
        Thread.sleep(1500)

        turnHead(1000,92)
        val distanceEast = ultraSensorDistance()
        distances.add(distanceEast)
        Thread.sleep(1500)

        turnHead(1000,92)
        val distanceSouth = ultraSensorDistance()
        distances.add(distanceSouth)
        Thread.sleep(1500)

        turnHead(1000,92)
        val distanceWest = ultraSensorDistance()
        distances.add(distanceWest)
        Thread.sleep(1500)

        turnHead(1000,-272)

        return distances
    }










}
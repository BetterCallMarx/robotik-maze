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
    private val robotName: String = "robot"
    private val ultraSonicPort: String = "s1"
    private val colorSensorPort: String = "s3"
    private val touchSensorPort: String = "s2"
    private val leftMotorPort: String = "a"
    private val rightMotorPort: String = "d"
    private val headMotorPort: String = "c"
    private val ipTarget: String = "192.168.178.255"
    private val port: Int = 9001

    init {
        oscReceiver.start()

    }

    //driving the robot forward, optimal inputs for a distance of 28cm is : 100, 550
    fun driveForward(speed: Int, angle: Int){
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target",speed,angle)
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/stop")
    }
    //driving the robot backward, optimal inputs for a distance of 28cm is : 100, -550
    fun driveBackward(speed: Int, angle: Int){
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target",speed,angle)
        OSCSender(ipTarget,port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/stop")
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

    fun completeHeadTurn(){

        var distanceNorth = ultraSensorDistance()
        Thread.sleep(1500)

        turnHead(1000,92)
        var distanceEast = ultraSensorDistance()
        Thread.sleep(1500)

        turnHead(1000,92)
        var distanceSouth = ultraSensorDistance()
        Thread.sleep(1500)

        turnHead(1000,92)
        var distanceWest = ultraSensorDistance()
        Thread.sleep(1500)

        turnHead(1000,-272)
    }










}
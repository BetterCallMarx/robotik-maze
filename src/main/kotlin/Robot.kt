import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import de.fhkiel.rob.legoosctester.osc.OSCSender
import enums.Direction
import graphing.GraphFrontend
import graphing.Tile
import graphing.Tree
import gui.DebugMessage
import java.lang.Thread.sleep


//TODO für jeden Befehl eine Funktion
class Robot {
    lateinit var direction: Direction
    private val robotName: String = "robot"
    private val ultraSonicPort: String = "s1"
    private val colorSensorPort: String = "s4"
    private val touchSensorPort: String = "s2"
    private val gyroscopeSensorPort: String = "s3"
    private val leftMotorPort: String = "d"
    private val rightMotorPort: String = "a"
    private val headMotorPort: String = "c"
    private val ipTarget: String = "192.168.178.255"
    private val port: Int = 9001


    init {
        OSCReceiver.start()
        OSCSender(ipTarget, port).send("/$robotName/motor/$headMotorPort/angle", 0)
    }


    //for 30cm forward movement angle is 612, for backwards its -612. 500 is appropriate speed
    fun drive(speed: Int, angle: Int) : Boolean{
        val start = System.currentTimeMillis()
        val timeout = 5000L
        var driven: Boolean = false
        //val path: String = "/$robotName/motor/$rightMotorPort/reached/target"
        val path: String = "/$robotName/motor/$rightMotorPort/target/reached"
        OSCReceiver.subListener(path
        ) {
            if(it[0] as Int == angle){
                driven = true
                GraphFrontend.visitedPositions.add(GraphFrontend.currentPosition)
                if(angle>0){
                    GraphFrontend.updatePosition(Pair(30, 30))
                }else{
                    GraphFrontend.updatePosition(Pair(-30, -30))
                }
                OSCReceiver.unsubListener(path)
            }else{
                OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
                OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target", speed, angle)
            }
        }
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/run/target", speed, angle)
        while(!driven && System.currentTimeMillis() - start < timeout) {
            sleep(10)
        }
        if(!driven){
            DebugMessage.debugMessage = "Es konnte nicht um $angle gefahren werden"
            throw Exception("Es konnte nicht gefahren werden")
        }
        println("driven")
        return true
    }


    //one headturn
    fun turnHead(speed: Int, angle: Int) {
        val start = System.currentTimeMillis()
        val timeout = 5000L
        var turned: Boolean = false
        //val path: String = "/$robotName/motor/$headMotorPort/reached/target"
        val path: String = "/$robotName/motor/$headMotorPort/target/reached"
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
        while(!turned && System.currentTimeMillis() - start < timeout) {
            sleep(10)
        }
        if(!turned){
            throw Exception("Kopf konnte nicht gedreht werden")
        }

    }

    fun positionSelf(){
        try {
            val start = System.currentTimeMillis()
            val timeout = 5000L
            drive(250, 300)
            while (!touchSensorTouched() && System.currentTimeMillis() - start < timeout) {
                sleep(10)
            }
            drive(250, -150)
        }catch (e: Exception){
            DebugMessage.debugMessage = e.message.toString()
        }
    }



    //for 90 degreee turn optimal is -187,187 for a right turn and 187,-187 for a left turn
    fun turn(speed: Int, angleRight: Int, angleLeft: Int): Boolean {
        val start = System.currentTimeMillis()
        val timeout = 5000L
        var drivenRight: Boolean = false
        var drivenLeft: Boolean = false
        //val pathRight: String = "/$robotName/motor/$rightMotorPort/reached/target"
        //val pathLeft: String = "/$robotName/motor/$leftMotorPort/reached/target"
        val pathRight: String = "/$robotName/motor/$rightMotorPort/target/reached"
        val pathLeft: String = "/$robotName/motor/$leftMotorPort/target/reached"
        OSCReceiver.subListener(pathRight
        ){
            if(it[0] == angleRight){
                drivenRight = true
                OSCReceiver.unsubListener(pathRight)
            }else{
                OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
                OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target", speed, angleRight, angleLeft)
            }
        }
        OSCReceiver.subListener(pathLeft
        ){
            if(it[0] == angleLeft){
                drivenLeft = true
                OSCReceiver.unsubListener(pathLeft)
            }else{
                OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
                OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target", speed, angleRight, angleLeft)
            }
        }
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/angle", 0)
        OSCSender(ipTarget, port).send("/$robotName/motor/$rightMotorPort$leftMotorPort/multirun/target", speed, angleRight, angleLeft)
        while(!drivenRight && !drivenLeft && System.currentTimeMillis() - start < timeout) {
            sleep(10)
        }
        if(!drivenRight && !drivenLeft){
            DebugMessage.debugMessage = "Es konnte nicht um gedreht werden"
            throw Exception("Es konnte nicht gedreht werden")
        }

        if(angleLeft<angleRight){
                GraphFrontend.turnWest()
        }else{
                GraphFrontend.turnEast()
        }
        println("turned")
        return true
    }

    private fun colorSensorColor(): String {
        val start = System.currentTimeMillis()
        val timeout = 5000L
        var color : String = ""
        val path: String = "/$robotName/color/$colorSensorPort/is"
        OSCReceiver.subListener(path
        ) {
            color = it[0] as String
            OSCReceiver.unsubListener(path)
        }
        OSCSender(ipTarget, port).send("/$robotName/color/$colorSensorPort")
        while(color.isBlank() && System.currentTimeMillis() - start < timeout){
            sleep(10)
        }
        if(color.isBlank()){
            DebugMessage.debugMessage = "Farbe konnte nicht erfasst werden"
        }
        return color
    }

    private fun touchSensorTouched(): Boolean {
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
        var distance: Int = -1
        try {
            val timeout = 5000L
            val start = System.currentTimeMillis()
            val path: String = "/$robotName/ultrasonic/${ultraSonicPort}/distance/is"
            OSCReceiver.subListener(
                path
            ) {
                distance = it[0] as Int
                OSCReceiver.unsubListener(path)
            }
            OSCSender(ipTarget, port).send("/$robotName/ultrasonic/$ultraSonicPort/distance")
            while (distance < 0 && System.currentTimeMillis() - start < timeout) {
                sleep(10)
            }
            if(distance<0){
                println("Error")
                return -1
            }
        }catch (e: Exception){
            return -1
        }
        return distance
    }

    private fun completeHeadTurnAverage(): MutableList<Pair<Int, Direction>> {
        val distances: MutableList<Pair<Int, Direction>> = mutableListOf()
        val numSamples = 3 // Number of samples to average
        try {
            // Function to calculate the average distance from multiple readings
            fun calculateAverageDistance(): Int {
                val samples = mutableListOf<Int>()
                repeat(numSamples) {
                    val distance = ultraSensorDistance()
                    if (distance != -1) {
                        samples.add(distance)
                    }
                }
                if (samples.isEmpty()) throw Exception("Ultrasonic sensor failed to provide valid readings.")
                return samples.average().toInt()
            }

            // NORTH
            val distanceNorth = calculateAverageDistance()
            distances.add(Pair(distanceNorth, Direction.NORTH))

            // EAST
            turnHead(1000, 90)
            val distanceEast = calculateAverageDistance()
            distances.add(Pair(distanceEast, Direction.EAST))

            // SOUTH
            turnHead(1000, 180)
            val distanceSouth = calculateAverageDistance()
            distances.add(Pair(distanceSouth, Direction.SOUTH))

            // WEST
            turnHead(1000, -90)
            val distanceWest = calculateAverageDistance()
            distances.add(Pair(distanceWest, Direction.WEST))

            // Reset head to initial position
            turnHead(1000, 0)
        } catch (e: Exception) {
            DebugMessage.debugMessage = "Error during head turn: ${e.message}"
            turnHead(1000, 0)
            return mutableListOf() // Return an empty list in case of an error
        }
        return distances
    }

    private fun completeHeadTurn(): MutableList<Pair<Int,Direction>> {
        val distances: MutableList<Pair<Int,Direction>> = mutableListOf()
        try {
            val distanceNorth = ultraSensorDistance()
            if (distanceNorth == -1) throw Exception("Ultrasonic sensor failed to read distance for NORTH.")
            distances.add(Pair(distanceNorth, Direction.NORTH))

            turnHead(1000, 90)
            val distanceEast = ultraSensorDistance()
            if (distanceEast == -1) throw Exception("Ultrasonic sensor failed to read distance for WEST.")
            distances.add(Pair(distanceEast, Direction.EAST))

            turnHead(1000, 180)
            val distanceSouth = ultraSensorDistance()
            if (distanceSouth == -1) throw Exception("Ultrasonic sensor failed to read distance for SOUTH.")
            distances.add(Pair(distanceSouth, Direction.SOUTH))

            turnHead(1000, -90)
            val distanceWest = ultraSensorDistance()
            if (distanceWest == -1) throw Exception("Ultrasonic sensor failed to read distance for WEST.")
            distances.add(Pair(distanceWest, Direction.WEST))
            turnHead(1000, 0)
        }catch (e: Exception){
            DebugMessage.debugMessage="Error during head turn: ${e.message}"
            turnHead(1000,0)
            return emptyList<Pair<Int,Direction>>().toMutableList()
        }
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

    private fun driveToExit(directionsNeeded: List<Pair<Int, Int>>){

        try {
            for (d in directionsNeeded) {
                while (GraphFrontend.facing.second != d) {
                    while(!turn(500, -187, 187)){
                        sleep(100)
                    }
                }
                while (!drive(500, 612))
                {
                    sleep(100)
                }
            }
        }catch (e: Exception){
            println(e)
            throw e
        }
    }

    fun driveToExitRetry(){
        var success = false
        val maxRetries = 5
        var attempts = 0
        while (!success && attempts < maxRetries) {
            val path = Tree.findShortestPathToRoot(GraphFrontend.currentPosition)
            println(path)
            val directions = GraphFrontend.getTotalDirections(path)
            attempts++
            try {
                driveToExit(directions)
                success = true
            } catch (e: Exception) {
                DebugMessage.debugMessage = "Ein Fehler beim zurückfahren ist aufgetreten: ${e.message}"
            }
        }
    }

    fun getTile(): Tile {
        val distances: MutableList<Pair<Int, Direction>> = completeHeadTurnAverage()
        val color = colorSensorColor()
        if (distances.isNotEmpty() && color.isNotEmpty()) {
            val tile: Tile = GraphFrontend.createTile(distances, GraphFrontend.colorToEnum(color))
            tile.printTile()
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
            return tile
        } else {
            throw Exception("Tile konnte nicht erfasst werden")
        }
    }


}
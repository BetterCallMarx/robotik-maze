package de.fhkiel.rob.legoosctester.osc

import Data
import Robot
import com.illposed.osc.MessageSelector
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.transport.OSCPortIn
import kotlin.concurrent.thread


//TODO Data class member variable welche mittels getter und setter

object OSCReceiver {
    var port: Int = -1
        private set
    private lateinit var receiver: OSCPortIn
    private var data: Data = Data()
    private var robot: Robot = Robot()
    fun start(port: Int = 9001) {
        receiver = OSCPortIn(port)
        receiver.dispatcher.addListener(
            object : MessageSelector {
                override fun isInfoRequired(): Boolean {
                    return false
                }

                override fun matches(messageEvent: OSCMessageEvent?): Boolean {
                    return true
                }
            }
        ) { event ->
            if (event != null) {
                newMessage(event.message.address, event.message.arguments)
            }
        }
        thread { receiver.startListening() }

        this.port = port
    }

    fun stop() {
        if (this::receiver.isInitialized) {
            receiver.stopListening()
            this.port = -1
        }
    }

    private var usDistance: Int = 0

    fun returnData(): Data {
        return data
    }


    private fun newMessage(path: String, args: List<Any>) {
        var ultraSonicSensorDistance: Int?
        val colorSensorColor: String?
        val touchSensorTouched: Boolean?

        when {
            path.startsWith("/${robot.robotName}/ultrasonic/${robot.ultraSonicPort}/distance/is") -> {
                ultraSonicSensorDistance = (args.lastOrNull() as? Int) ?: 0
                data.distance = ultraSonicSensorDistance
            }

            path.startsWith("/${robot.robotName}/color/${robot.colorSensorPort}/is") -> {
                colorSensorColor = (args.lastOrNull() as? String) ?: ""
                data.color = colorSensorColor
            }

            path.startsWith("/${robot.robotName}/touch/${robot.touchSensorPort}/pressed") -> {
                touchSensorTouched = (args.lastOrNull() as? Boolean) ?: false
                data.touched = touchSensorTouched
            }

            path.startsWith("/${robot.robotName}/touch/${robot.touchSensorPort}/changed/pressed") -> {
                touchSensorTouched = (args.lastOrNull() as? Boolean) ?: false
                data.touched = touchSensorTouched
            }


            else -> {
                // println("Unknown path: $path")
            }

        }
    }
}
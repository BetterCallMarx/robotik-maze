package de.fhkiel.rob.legoosctester.osc

import Data
import com.illposed.osc.MessageSelector
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.transport.OSCPortIn
import de.fhkiel.rob.legoosctester.gui.Incoming.log
import kotlin.concurrent.thread



//TODO Data class member variable welche mittels getter und setter

object OSCReceiver {
    var port: Int = -1
        private set
    private lateinit var receiver: OSCPortIn

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
    fun setData(data: Int){
        usDistance = data
    }

    fun returnData(): Int{
        return usDistance
    }


    private fun newMessage(path: String, args: List<Any>) {
        var ultraSonicSensorDistance: Int? = null
        var colorSensorColor: String? = null
        var touchSensorTouched: Boolean? = null

        when {
            path.startsWith("/robot/ultrasonic/s1/distance/is") -> {
                ultraSonicSensorDistance = (args.lastOrNull() as? Int) ?: 0
                setData(ultraSonicSensorDistance)
                println("Ultrasonic Distance: $ultraSonicSensorDistance")
            }

            path.startsWith("/robot/color/s4/is") -> {
                colorSensorColor = (args.lastOrNull() as? String) ?: ""
                println("color Distance: $colorSensorColor")
            }

            path.startsWith("/robot/ultrasonic/s3/distance/is") -> {
                touchSensorTouched = (args.lastOrNull() as? Boolean) ?: false
                println("Touch: $touchSensorTouched")
            }

            else -> {
               // println("Unknown path: $path")
            }

        }
    }
}
package de.fhkiel.rob.legoosctester.osc

import com.illposed.osc.OSCMessage
import com.illposed.osc.transport.OSCPortOut
import java.net.InetAddress

class OSCSender(ip: String, port: Int) {
    private val sender: OSCPortOut = OSCPortOut(
        InetAddress.getByName(ip),
        port
    )

    fun <T> send(path: String, vararg payload: T) {
        send(path, payload.toList())
    }

    fun <T> send(path: String, payload: List<T>) {
        sender.send(OSCMessage(path, payload))
    }

    fun send(path: String) {
        sender.send(OSCMessage(path))
    }
}
package de.fhkiel.rob.legoosctester.osc

import com.illposed.osc.MessageSelector
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.transport.OSCPortIn
import DEBUG
import kotlin.concurrent.thread




object OSCReceiver {
    var port: Int = -1
        private set
    private lateinit var receiver: OSCPortIn
    fun start(port: Int = 9001) {
        synchronized(this) {
            if (this.port != -1) {
                return
            }

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
    }

    fun stop() {
        if (this::receiver.isInitialized) {
            receiver.stopListening()
            this.port = -1
        }
    }


    /**
     * Listener list
     */
    private val listenerList: MutableMap<String,(args: List<Any>) -> Unit > = mutableMapOf()
    fun subListener(path: String, callBack: (args: List<Any>) -> Unit){
        listenerList[path] = callBack
    }

    /**
     * Unsub listener
     *
     * @param path
     */
    fun unsubListener(path: String){
        listenerList.remove(path)
    }

    /**
     * New message
     *
     * @param path
     * @param args
     */
    private fun newMessage(path: String, args: List<Any>) {
        listenerList.getOrDefault(path,{if(DEBUG) println("unknown: $path: $args") }).invoke(args)
    }

}
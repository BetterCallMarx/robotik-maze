package de.fhkiel.rob.legoosctester.gui

import de.fhkiel.rob.legoosctester.osc.OSCReceiver
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.time.LocalDateTime
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

object Incoming : JPanel() {
    private fun readResolve(): Any = Incoming

    private val log: JTextArea = JTextArea()
    private val filter: JTextField = JTextField()

    private var messages: MutableList<Pair<LocalDateTime, String>> = mutableListOf()

    init {
        layout = GridBagLayout()

        val control = JPanel()
        control.layout = GridBagLayout()

        val clearLog = JButton("Clear Log")
        clearLog.addActionListener {
            messages = mutableListOf()
            onChange()
        }
        control.add(clearLog)
        val portToListenTo = JTextField("9001")
        setHint(portToListenTo, "ServerPort")
        val serverChange = {
            try {
                val port = portToListenTo.text.toInt()
                if (OSCReceiver.port != port) {
                    OSCReceiver.stop()
                    OSCReceiver.start(port)
                    log("Server restarted", "Listening to port $port")
                }
            } catch (e: Exception) {
                log("Error: Need valid port", e.localizedMessage)
            }
        }
        portToListenTo.addActionListener {
            serverChange()
        }
        portToListenTo.addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent?) { /* not needed */
            }

            override fun focusLost(e: FocusEvent?) {
                serverChange()
            }
        })
        portToListenTo.horizontalAlignment = JTextField.CENTER
        portToListenTo.minimumSize = Dimension(50, 27)
        portToListenTo.preferredSize = Dimension(50, 27)
        control.add(portToListenTo)

        setHint(filter, "filter")
        filter.minimumSize = Dimension(50, 27)
        filter.preferredSize = Dimension(Int.MAX_VALUE, 27)
        filter.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                onChange()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                onChange()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                onChange()
            }

        })
        val filterConstraints = GridBagConstraints()
        filterConstraints.weightx = 1.0
        filterConstraints.fill = GridBagConstraints.HORIZONTAL
        control.add(filter, filterConstraints)

        val controlConstraints = GridBagConstraints()
        controlConstraints.gridx = 0
        controlConstraints.gridy = 0
        controlConstraints.weightx = 1.0
        controlConstraints.fill = GridBagConstraints.HORIZONTAL
        add(control, controlConstraints)

        log.isEditable = false
        val logConstraints = GridBagConstraints()
        logConstraints.gridx = 0
        logConstraints.gridy = 1
        logConstraints.weightx = 1.0
        logConstraints.weighty = 1.0
        logConstraints.fill = GridBagConstraints.BOTH
        val scroll = JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
        add(scroll, logConstraints)
    }

    private fun onChange() {
        deleteOldAndToMuch()
        val toDisplay = messages
            .filter { containsAll(it.second) }
            .map { "${it.first.format(TIME_FORMATER)}: ${it.second}\n" }
            .fold("", { a, b -> a + b })

        SwingUtilities.invokeLater {
            log.text = toDisplay
            log.setCaretPosition(log.document.length)
        }
    }

    private fun containsAll(string: String): Boolean {
        if (filter.text.trim() == filter.toolTipText.trim()) {
            return true
        }

        for (part in filter.text.split(" ")) {
            if (!string.contains(part, true)) {
                return false
            }
        }
        return true
    }

    private fun deleteOldAndToMuch(minutesAgo: Long = 10, maxMessages: Int = 10000) {
        val tenMinutesAgo = LocalDateTime.now().minusMinutes(minutesAgo)
        messages = messages.filter { it.first.isAfter(tenMinutesAgo) }.takeLast(maxMessages).toMutableList()
    }

    fun log(source: String, message: String) {
        val osc = "$source -> $message"
        messages.add(Pair(LocalDateTime.now(), osc))
        if (containsAll(osc)) {
            onChange()
        }
    }

}
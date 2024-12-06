package de.fhkiel.rob.legoosctester.gui

import java.awt.Color
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.time.format.DateTimeFormatter
import javax.swing.text.JTextComponent

val TIME_FORMATER = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")

fun setHint(component: JTextComponent, hint: String) {
    val paddedHint = " $hint"
    component.addFocusListener(object : FocusListener {
        override fun focusGained(e: FocusEvent) {
            component.setForeground(Color.BLACK)
            if (component.text != null && component.text == paddedHint) {
                component.text = ""
            }
        }

        override fun focusLost(e: FocusEvent) {
            if (component.text == null || component.text.trim().isEmpty()) {
                component.text = paddedHint
            }
            if (component.text == paddedHint) {
                component.foreground = Color.LIGHT_GRAY
            }
        }
    })

    if (component.text == null || component.text.trim().isEmpty()) {
        component.text = paddedHint
        component.foreground = Color.LIGHT_GRAY
    }
    component.toolTipText = hint
}

fun parseArgs(list: List<String>): List<Any> {
    val args = mutableListOf<Any>()
    for (e in list) {
        try {
            if (e.contains(".")) {
                args.add(e.toFloat())
                continue
            }
        } catch (_: Exception) {
        }
        try {
            args.add(e.toInt())
            continue
        } catch (_: Exception) {
        }
        if (e.trim().lowercase() == "true") {
            args.add(true)
            continue
        }
        if (e.trim().lowercase() == "false") {
            args.add(false)
            continue
        }
        args.add(e)
    }
    return args
}
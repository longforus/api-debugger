package com.longforus.apidebugger

import com.longforus.apidebugger.ui.MainPanel
import java.awt.EventQueue

/**
 * Created by XQ Yang on 8/30/2018  10:07 AM.
 * Description :
 */

fun main(args:Array<String>){
    EventQueue.invokeLater {

        val mainPanel = MainPanel("Fec Api debugger")

    }

    println("hello world")
}
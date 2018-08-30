package com.longforus.apidebugger

import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

/**
 * Created by XQ Yang on 8/30/2018  4:00 PM.
 * Description :
 */

object JsonTreeActionHandler: TreeSelectionListener {
    override fun valueChanged(e: TreeSelectionEvent?) {
        println("on tree node selected")
    }

}
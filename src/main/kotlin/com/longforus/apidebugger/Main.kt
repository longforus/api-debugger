package com.longforus.apidebugger

import com.longforus.apidebugger.ui.JSONEditPanel
import com.longforus.apidebugger.ui.MainPanel
import java.awt.Component
import java.awt.EventQueue

/**
 * Created by XQ Yang on 8/30/2018  10:07 AM.
 * Description :
 */

lateinit var mainPanel: MainPanel
val appName = "Fec Api debugger"

fun main(args: Array<String>) {
    OB.init()
    EventQueue.invokeLater {
        mainPanel = MainPanel(appName)
        UILifecycleHandler.onResume(mainPanel)
        mainPanel.jep.alignmentX = Component.LEFT_ALIGNMENT
        mainPanel.jep.addTreeSelectionListener(JsonTreeActionHandler)
        HttpManage.mainPanel = mainPanel


        mainPanel.jep.setJson("[\n" +
            "[{\"id\":1, \"value\":\"horse shoes\"},{\"id\":2, \"value\":\"teather ball\"}],\n" +
            "[{\"id\":3, \"value\":\"frisbee\"},{\"id\":4, \"value\":\"monkey bars\"}, {\"id\":5, \"value\":{\"count\": \"dracula\"}},{},[false]]\n" +
            "]", JSONEditPanel.UpdateType.REPLACE)
    }
}


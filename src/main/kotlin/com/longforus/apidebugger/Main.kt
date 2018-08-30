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

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        netWorkInit()
        mainPanel = MainPanel("Fec Api debugger")
        mainPanel.jep.alignmentX = Component.LEFT_ALIGNMENT
        mainPanel.jep.addTreeSelectionListener(JsonTreeActionHandler)
        HttpManage.mainPanel = mainPanel


        mainPanel.jep.setJson("[\n" +
            "[{\"id\":1, \"value\":\"horse shoes\"},{\"id\":2, \"value\":\"teather ball\"}],\n" +
            "[{\"id\":3, \"value\":\"frisbee\"},{\"id\":4, \"value\":\"monkey bars\"}, {\"id\":5, \"value\":{\"count\": \"dracula\"}},{},[false]]\n" +
            "]", JSONEditPanel.UpdateType.REPLACE)
    }
}

fun netWorkInit() {
    HttpManage.encryptHandler = MyValueHandler.encryptImplList[0]
}

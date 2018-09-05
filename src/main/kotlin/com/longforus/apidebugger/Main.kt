package com.longforus.apidebugger

import com.longforus.apidebugger.ui.MainPanel
import com.teamdev.jxbrowser.chromium.az
import java.awt.EventQueue
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.math.BigInteger

/**
 * Created by XQ Yang on 8/30/2018  10:07 AM.
 * Description :
 */

lateinit var mainPanel: MainPanel
val appName = "Fec Api debugger"


fun main(args: Array<String>) {
    jxInit()
    OB.init()
    EventQueue.invokeLater {
//        Notepad()
        mainPanel = MainPanel(appName)
        UILifecycleHandler.onResume(mainPanel)
        HttpManage.mainPanel = mainPanel
    }
}

fun jxInit() {
    try {
        val e = az::class.java.getDeclaredField("e")
        e.isAccessible = true
        val f = az::class.java.getDeclaredField("f")
        f.isAccessible = true
        val modifersField = Field::class.java.getDeclaredField("modifiers")
        modifersField.isAccessible = true
        modifersField.setInt(e, e.modifiers and Modifier.FINAL.inv())
        modifersField.setInt(f, f.modifiers and Modifier.FINAL.inv())
        e.set(null, BigInteger("1"))
        f.set(null, BigInteger("1"))
        modifersField.isAccessible = false
    } catch (e1: Exception) {
        e1.printStackTrace()
    }
}


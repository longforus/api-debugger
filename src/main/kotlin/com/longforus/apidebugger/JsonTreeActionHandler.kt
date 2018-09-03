package com.longforus.apidebugger

import com.longforus.apidebugger.ui.JSONJTreeNode
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener

/**
 * Created by XQ Yang on 8/30/2018  4:00 PM.
 * Description :
 */

object JsonTreeActionHandler: TreeSelectionListener {
    override fun valueChanged(e: TreeSelectionEvent?) {
        println(e.toString())
        e?.newLeadSelectionPath?.lastPathComponent?.let {
            val treeNode = it as JSONJTreeNode
            val start = MyValueHandler.curShowJsonStr.indexOf(treeNode.toSSearchStr())
//            mainPanel.tpResponse.select(start,start+treeNode.toSSearchStr().length)
        }
    }

}
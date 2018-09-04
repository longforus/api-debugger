package com.longforus.apidebugger

import java.awt.Color
import javax.swing.JOptionPane
import javax.swing.JTextPane
import javax.swing.text.BadLocationException
import javax.swing.text.MutableAttributeSet
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

/**
 * Created by XQ Yang on 8/30/2018  3:05 PM.
 * Description :
 */
fun JTextPane.append(str:String,color: Color? = null,autoScroll:Boolean = true){
    val doc = this.document
    if (doc != null) {
        try {
            var attr: MutableAttributeSet? = null
            if (color != null) {
                attr = SimpleAttributeSet()
                StyleConstants.setForeground(attr, color)
                StyleConstants.setBold(attr, true)
            }
            doc.insertString(doc.length, str, attr)
            if (autoScroll) {
                this.caretPosition = this.styledDocument.length
            }
        } catch (e: BadLocationException) {
        }

    }
}

fun showErrorMsg(msg:String){
    JOptionPane.showMessageDialog(null, msg,"Error", JOptionPane.ERROR)
}
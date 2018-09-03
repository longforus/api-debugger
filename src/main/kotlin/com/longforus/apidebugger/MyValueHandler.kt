package com.longforus.apidebugger

import com.google.gson.GsonBuilder
import com.longforus.apidebugger.bean.ApiBean
import com.longforus.apidebugger.bean.ProjectBean
import com.longforus.apidebugger.encrypt.DefaultEncryptHandler
import com.longforus.apidebugger.encrypt.IEncryptHandler
import com.longforus.apidebugger.encrypt.kzEncryptHandler
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


/**
 * Created by XQ Yang on 8/30/2018  5:24 PM.
 * Description :
 */

object MyValueHandler {

    const val PARAME_TABLE_ROW_COUNT = 15


    val encryptImplList = listOf<IEncryptHandler>(kzEncryptHandler(), DefaultEncryptHandler())
    val mGson =GsonBuilder().setPrettyPrinting().create()

    var curProject: ProjectBean? = null
        set(value) {
            if (value == field) {
                return
            }
            field = value
            value?.let {
                UILifecycleHandler.initProject(it, mainPanel)
            }
        }


    var curApi: ApiBean? = null
     set(value) {
         if (value == field) {
             return
         }
         field = value
         UILifecycleHandler.initApi(value)
     }

    var curBaseUrl = ""
    var curShowJsonStr   = ""

    fun encryptId2Index(id: Int): Int {
        encryptImplList.forEachIndexed { index, iEncryptHandler ->
            if (iEncryptHandler.typeCode == id) {
                return index
            }
        }
        return 0
    }
    fun encryptIndex2Id(index: Int)=encryptImplList[index].typeCode




    /**
     * 将字符串复制到剪切板。
     */
    fun setSysClipboardText(writeMe: String) {
        val clip = Toolkit.getDefaultToolkit().systemClipboard
        val tText = StringSelection(writeMe)
        clip.setContents(tText, null)
    }

}
package com.longforus.apidebugger

import com.google.gson.Gson
import com.longforus.apidebugger.bean.ApiBean
import com.longforus.apidebugger.bean.ProjectBean
import com.longforus.apidebugger.encrypt.DefaultEncryptHandler
import com.longforus.apidebugger.encrypt.IEncryptHandler
import com.longforus.apidebugger.encrypt.kzEncryptHandler

/**
 * Created by XQ Yang on 8/30/2018  5:24 PM.
 * Description :
 */

object MyValueHandler {
    var encryptHandler: IEncryptHandler = DefaultEncryptHandler()
    val encryptImplList = listOf<IEncryptHandler>(kzEncryptHandler(), DefaultEncryptHandler())
    val gson = Gson()

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
         value?.let {
             UILifecycleHandler.initApi(value)
         }
     }

    var curBaseUrl = ""

    fun encrcyptId2Index(id: Int): Int {
        encryptImplList.forEachIndexed { index, iEncryptHandler ->
            if (iEncryptHandler.typeCode == id) {
                return index
            }
        }
        return 0
    }

}
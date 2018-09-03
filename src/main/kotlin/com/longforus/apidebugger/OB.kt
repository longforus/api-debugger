package com.longforus.apidebugger

import com.longforus.apidebugger.bean.ApiBean
import com.longforus.apidebugger.bean.MyObjectBox
import com.longforus.apidebugger.bean.ProjectBean
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

/**
 * Created by XQ Yang on 8/31/2018  9:44 AM.
 * Description :
 */


object OB{

    lateinit var store:BoxStore
    lateinit var projectBox: Box<ProjectBean>
    lateinit var apiBox: Box<ApiBean>
    fun init(){
        store = MyObjectBox.builder().name("api-debugger-db").build()
        projectBox= store.boxFor()
        apiBox= store.boxFor()
    }

    fun onExit(){
        store.close()
    }
}
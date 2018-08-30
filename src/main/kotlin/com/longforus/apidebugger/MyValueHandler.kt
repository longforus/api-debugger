package com.longforus.apidebugger

import com.google.gson.Gson
import com.longforus.apidebugger.encrypt.DefaultEncryptHandler
import com.longforus.apidebugger.encrypt.IEncryptHandler
import com.longforus.apidebugger.encrypt.kzEncryptHandler

/**
 * Created by XQ Yang on 8/30/2018  5:24 PM.
 * Description :
 */

 object MyValueHandler{
    val encryptImplList = listOf<IEncryptHandler>(kzEncryptHandler(),DefaultEncryptHandler())
    val gson = Gson()
}
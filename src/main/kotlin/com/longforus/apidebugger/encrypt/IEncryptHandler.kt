package com.longforus.apidebugger.encrypt

import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by XQ Yang on 8/30/2018  5:11 PM.
 * Description :
 */
abstract class IEncryptHandler {
    abstract val typeCode:Int
    abstract val title: String
    abstract fun onGetMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String)
    abstract fun onPostMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String): RequestBody
    override fun toString(): String {
        return title
    }
}
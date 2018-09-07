package com.longforus.apidebugger.encrypt

import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by XQ Yang on 8/30/2018  5:11 PM.
 * Description : 加密处理
 */
abstract class IEncryptHandler {
    //这个加密类型的code,同一工程不允许出现相同的
    abstract val typeCode:Int
    //显示在界面上的名字
    abstract val title: String
    //实现get方法的参数加密
    abstract fun onGetMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String)
    //实现post方法的参数加密
    abstract fun onPostMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String): RequestBody
    override fun toString(): String {
        return title
    }
}
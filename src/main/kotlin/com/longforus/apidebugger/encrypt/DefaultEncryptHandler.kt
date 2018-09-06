package com.longforus.apidebugger.encrypt

import com.longforus.apidebugger.MyValueHandler
import com.longforus.apidebugger.append
import com.longforus.apidebugger.mainPanel
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by XQ Yang on 8/30/2018  5:13 PM.
 * Description :
 */
class DefaultEncryptHandler : IEncryptHandler() {
    override val typeCode: Int = 0
    override val title: String = "default"


    override fun onPostMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String): RequestBody {
        val sb = StringBuilder("?")
        val encodingBuilder = FormBody.Builder()
        if (params?.isNotEmpty() == true) {
            mainPanel.tpInfo.append("params :\n")
            params.forEach {
                encodingBuilder.add(it.key, it.value)
                sb.append(it.key).append("=").append(it.value).append("&")
                mainPanel.tpInfo.append("    ${it.key}   =  ${it.value} \n")
            }
        }
        if (MyValueHandler.curProject?.defaultParams?.isNotEmpty() ==true) {
            mainPanel.tpInfo.append("default params :\n")
            MyValueHandler.curProject?.defaultParams?.forEach {
                if (it.selected) {
                    encodingBuilder.add(it.key, it.value)
                    sb.append(it.key).append("=").append(it.value).append("&")
                    mainPanel.tpInfo.append("    ${it.key}   =  ${it.value} \n")
                }
            }
        }
        val resultUrl = url + if (sb.endsWith("?") || sb.endsWith("&")) sb.subSequence(0, sb.length - 1) else sb.toString()
        mainPanel.tpInfo.append("Url :\n$resultUrl \n")
        return encodingBuilder.build()
    }

    override fun onGetMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String) {
        val sb = StringBuilder("?")
        params?.let {
            for (entry in params) {
                sb.append(entry.key).append("=").append(entry.value).append("&")
            }
        }
        if (MyValueHandler.curProject?.defaultParams?.isNotEmpty() ==true) {
            MyValueHandler.curProject?.defaultParams?.forEach {
                if (it.selected) {
                    sb.append(it.key).append("=").append(it.value).append("&")
                }
            }
        }
        val resultUrl = url + if (sb.endsWith("?") || sb.endsWith("&")) sb.subSequence(0, sb.length - 1) else sb.toString()
        builder.url(resultUrl)
        mainPanel.tpInfo.append("Url :\n    $resultUrl \n")
    }

}
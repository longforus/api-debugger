package com.longforus.apidebugger.encrypt

import com.longforus.apidebugger.append
import com.longforus.apidebugger.mainPanel
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by XQ Yang on 8/30/2018  5:13 PM.
 * Description :
 */
class DefaultEncryptHandler:IEncryptHandler(){
    override val typeCode: Int= 0
    override val title: String = "default"


    override fun onPostMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String): RequestBody {
        val encodingBuilder = FormBody.Builder()
        params?.forEach {
            encodingBuilder.add(it.key,it.value)
            mainPanel.tpInfo.append("key  =  ${it.key}   value =  ${it.value} \n")
        }
       return encodingBuilder.build()
    }

    override fun onGetMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String) {
        val sb = StringBuilder("?")
        params?.let {
            for (entry in params) {
                sb.append(entry.key).append("=").append(entry.value).append("&")
            }
        }
        val resultUrl = url + if (sb.endsWith("?") || sb.endsWith("&")) sb.subSequence(0, sb.length - 1) else sb.toString()
        builder.url(resultUrl)
        mainPanel.tpInfo.append(" url : $resultUrl \n")
    }

}
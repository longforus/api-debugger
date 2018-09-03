package com.longforus.apidebugger.encrypt

import com.google.gson.JsonObject
import com.longforus.apidebugger.HttpManage
import com.longforus.apidebugger.append
import com.longforus.apidebugger.mainPanel
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*

/**
 * Created by XQ Yang on 8/30/2018  4:47 PM.
 * Description :
 */

class kzEncryptHandler : IEncryptHandler() {
    override val title: String = "筷子"
    override val typeCode: Int = 10
    override fun onGetMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String) {
        val pair = getSiAndPa(params)
        val urlResult = "${url}si=${pair.first}&pa=${pair.second}"
        mainPanel.tpInfo.append("encrypted url : $url \n")
        builder.url(urlResult)
    }

    override fun onPostMethodEncrypt(params: Map<String, String>?, builder: Request.Builder, url: String): RequestBody {
        val encodingBuilder = FormBody.Builder()
        val pair = getSiAndPa(params)
        encodingBuilder.add("si", pair.first)
        encodingBuilder.add("pa", pair.second)
        HttpManage.mainPanel.tpInfo.append("encrypted url: $url?si=${pair.first}&pa=${pair.second} \n")
        return encodingBuilder.build()
    }


    fun getSiAndPa(params: Map<String, String>?): Pair<String, String> {
        val paObject = getPaObject(params ?: mapOf())

        val param = HashMap<String, String>()
        for (entry in paObject.entrySet()) {
            param[entry.key] = entry.value.asString
        }

        val si = DetectTool.getSign(param)//签名
        val pa = EncryptUtil.encrypt(paObject.toString())//DES加密

        return si to pa
    }


    private fun getPaObject(params: Map<String, String>): JsonObject {
        val allDataObject = JsonObject()
        var v: String? = null
        for (entry in params) {
            allDataObject.addProperty(entry.key, entry.value)
            if ("v" == entry.key) {
                v = entry.value
            }
            HttpManage.mainPanel.tpInfo.append("key  =  ${entry.key}   value =  ${entry.value}")
        }
        val ts = DetectTool.getTS()
        allDataObject.addProperty("m", DetectTool.getType())
        allDataObject.addProperty("p", DetectTool.getType())
        allDataObject.addProperty("u", DetectTool.getToken())
        allDataObject.addProperty("v", v ?: DetectTool.getVersionName())
        allDataObject.addProperty("i", DetectTool.getIMEI())
        allDataObject.addProperty("t", ts)
        return allDataObject
    }
}


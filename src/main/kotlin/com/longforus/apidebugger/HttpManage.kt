package com.longforus.apidebugger

import com.google.gson.JsonObject
import com.longforus.apidebugger.MyValueHandler.encryptHandler
import com.longforus.apidebugger.MyValueHandler.gson
import com.longforus.apidebugger.ui.JSONEditPanel
import com.longforus.apidebugger.ui.MainPanel
import okhttp3.*
import java.awt.Color
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by XQ Yang on 8/30/2018  2:29 PM.
 * Description :
 */
object HttpManage {

    lateinit var mainPanel: MainPanel

    val okHttpClient = OkHttpClient.Builder().writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS).build()






    /**
     * get方式联网请求数据
     */
    fun get(relativeUrl: String, params: Map<String,String>) {
        val request = buildRequest(getAbsoluteUrl(relativeUrl), params, 0)
        doRequest(request)
    }

    fun post(relativeUrl: String, params: Map<String,String>) {
        val request = buildRequest(getAbsoluteUrl(relativeUrl), params, 1)
        doRequest(request)
    }


    private fun doRequest(request: Request) {


        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainPanel.lbStatus.text = "onFailure message : ${e.message} "
//                val byteArrayOutputStream = ByteArrayOutputStream()
//                val ps = PrintStream(byteArrayOutputStream)
//                e.printStackTrace(ps)
                mainPanel.tpResponse.append(e.toString())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                mainPanel.lbStatus.text = "onResponse code: ${response.code()} "
                if (response.isSuccessful) {
                    val body = response.body() ?: return
                    val resStr = body.string()
                    val json = gson.fromJson<JsonObject>(resStr, JsonObject::class.java)
                    mainPanel.tpResponse.append(json.toString())
                    mainPanel.jep.setJson(resStr,JSONEditPanel.UpdateType.REPLACE)
                } else {
                    mainPanel.tpInfo.append("on response but not success", Color.RED)
                    mainPanel.tpInfo.append("code = ${response.code()}", Color.RED)
                    mainPanel.tpInfo.append("message = ${response.message()}", Color.RED)
                }
            }
        })
    }

    /**
     * 根据网络请求方式构建联网请求时所需的request
     *
     * @param httpMethodType 联网请求方式
     * @return 联网所需的request
     */

    @Throws(Exception::class)
    private fun buildRequest(url: String, params: Map<String, String>?, httpMethodType: Int): Request {
        val builder = Request.Builder()
        if (httpMethodType == 0) {
            mainPanel.tpInfo.append("OkHttp GET \n", Color.RED)
            encryptHandler.onGetMethodEncrypt(params, builder, url)
            builder.get()

        } else if (httpMethodType == 1) {
            mainPanel.tpInfo.append("OkHttp POST \n", Color.RED)
            builder.url(url)
            builder.post( encryptHandler.onPostMethodEncrypt(params,builder,url))
        }
        return builder.build()
    }



     var SERVER_IP: String = ""

    /**
     * 根据相对路径获取全路径
     *
     * @param relativeUrl 相对路径
     */
    private fun getAbsoluteUrl(relativeUrl: String) = SERVER_IP + relativeUrl
}
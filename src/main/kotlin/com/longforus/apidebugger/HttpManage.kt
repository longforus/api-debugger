package com.longforus.apidebugger

import com.google.gson.JsonObject
import com.longforus.apidebugger.MyValueHandler.mGson
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
    val okHttpClient = OkHttpClient.Builder().writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS).build()


    fun sendTestRequest(request: Request? = getRequest(), isLast: Boolean = false, allCount: Int = 0,startTime:Long = 0) {
        request?.let {
            if (!isLast) {
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                    }

                    override fun onResponse(call: Call, response: Response) {
                        synchronized(this@HttpManage) {
                            com.longforus.apidebugger.mainPanel.pb.value += 1
                        }
                    }
                })
            } else {
                doRequest(request,startTime,allCount)
            }
        }
    }

    fun sendRequest(request: Request? = getRequest()) {
        request?.let {
            doRequest(request)
        }
    }


    fun getRequest(): Request? {
        val url = getAbsoluteUrl(mainPanel.curApiUrl)
        if (url.isEmpty()) {
            return null
        }
        return buildRequest(url, UIActionHandler.getParamsMap(mainPanel.paramsTableModel, false), mainPanel.curMethod, mainPanel.curEncryptCode)
    }


    private fun doRequest(request: Request, startTime: Long = System.currentTimeMillis(), allCount: Int = 0) {
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mainPanel.lbStatus.text = "onFailure message : ${e.message}\n"
                mainPanel.tpInfo.append( "\nonFailure \n message : ${e.message}\n", Color.RED)
                mainPanel.tpInfo.append(" consuming: ${System.currentTimeMillis() - startTime}ms \n", Color.RED)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                UIActionHandler.onSaveApi(mainPanel.cbApiUrl.editor.item)
                mainPanel.lbStatus.text = "onResponse code: ${response.code()} "
                mainPanel.tpInfo.append("\nconsuming: ${System.currentTimeMillis() - startTime}ms  \n", Color.BLUE)
                if (response.isSuccessful) {
                    val body = response.body() ?: return
                    val bytes = body.bytes()
                    val resStr = String(bytes)
                    mainPanel.tpInfo.append("response size: ${bytes.size} byte  \n", Color.BLUE)
                    val json = mGson.fromJson<JsonObject>(resStr, JsonObject::class.java)
                    val jsonStr = mGson.toJson(json, JsonObject::class.java)
                    MyValueHandler.curShowJsonStr = jsonStr
                    mainPanel.setJsonData(jsonStr)
                } else {
                    mainPanel.tpInfo.append("\non response but not success\n", Color.RED)
                    mainPanel.tpInfo.append("code = ${response.code()} \n ", Color.RED)
                    mainPanel.tpInfo.append("message = ${response.message()}  \n", Color.RED)
                }
                if (allCount!=0) {
                    mainPanel.tpInfo.append("\nAll test count: $allCount  \n", Color.GREEN)
                    mainPanel.tpInfo.append("\nSuccess test count: ${mainPanel.pb.value}  \n", Color.GREEN)
                    mainPanel.tpInfo.append("\nThe total time consuming: ${System.currentTimeMillis() - startTime}ms  \n", Color.GREEN)
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
    private fun buildRequest(url: String, params: Map<String, String>?, httpMethodType: Int, encryptType: Int): Request {
        val builder = Request.Builder()
        val iEncryptHandler = MyValueHandler.encryptImplList[encryptType]
        if (httpMethodType == 1) {
            mainPanel.tpInfo.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~这是一条分割线~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n", Color.ORANGE)
            mainPanel.tpInfo.append("OkHttp GET \n", Color.RED)
            iEncryptHandler.onGetMethodEncrypt(params, builder, url)
            builder.get()
        } else if (httpMethodType == 0) {
            mainPanel.tpInfo.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~这是一条分割线~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n", Color.ORANGE)
            mainPanel.tpInfo.append("OkHttp POST \n", Color.RED)
            builder.url(url)
            builder.post(iEncryptHandler.onPostMethodEncrypt(params, builder, url))
        }
        return builder.build()
    }


    /**
     * 根据相对路径获取全路径
     *
     * @param relativeUrl 相对路径
     */
    private fun getAbsoluteUrl(relativeUrl: String): String {
        if (mainPanel.curBaseUrl.isNullOrEmpty()) {
            showErrorMsg("BaseURL is Null")
            return ""
        }
        return mainPanel.curBaseUrl + relativeUrl
    }
}
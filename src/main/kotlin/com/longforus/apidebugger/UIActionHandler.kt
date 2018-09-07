package com.longforus.apidebugger

import com.longforus.apidebugger.bean.ApiBean
import com.longforus.apidebugger.ui.ParamsTableModel
import javax.swing.DefaultComboBoxModel

/**
 * Created by XQ Yang on 8/31/2018  11:21 AM.
 * Description :
 */
object UIActionHandler {

    fun onSaveBaseUrl(selectedItem: Any) {
        MyValueHandler.curProject?.let {
            if (!it.baseUrlList.contains(selectedItem as String)) {
                it.baseUrlList.add(0, selectedItem)
                MyValueHandler.curBaseUrl = selectedItem
                OB.projectBox.put(it)
                val model = mainPanel.cbBaseUrl.model as DefaultComboBoxModel
                model.insertElementAt(selectedItem, 0)
            }
        }
    }

    fun onSaveApi(selectedItem: Any) {
        MyValueHandler.curProject?.let {
            val apiBean: ApiBean
            if (selectedItem is String) {
                apiBean = ApiBean(selectedItem, it.id)
                if (!it.apis.contains(apiBean)) {
                    apiBean.encryptType = mainPanel.selectedEncryptID
                    apiBean.method = mainPanel.selectedMethodType
                    apiBean.paramsMap = getParamsMap(mainPanel.paramsTableModel)
                    it.apis.add(0, apiBean)
                    val model = mainPanel.cbApiUrl.model as DefaultComboBoxModel
                    model.insertElementAt(apiBean, 0)
                    mainPanel.cbApiUrl.selectedIndex = 0
                    MyValueHandler.curApi = apiBean
                }
            } else {
                apiBean = selectedItem as ApiBean
                apiBean.encryptType = mainPanel.selectedEncryptID
                apiBean.method = mainPanel.selectedMethodType
                apiBean.paramsMap = getParamsMap(mainPanel.paramsTableModel)
            }
            OB.apiBox.put(apiBean)
        }
    }

    fun getParamsMap(model: ParamsTableModel, isSave: Boolean = true): MutableMap<String, String> {
        val map = HashMap<String, String>()
        for (bean in model.data) {
            if (bean.selected || isSave) {
                    map[bean.key] = bean.value
            }
        }
        return map
    }

    fun onNewApi() {
        val clone = MyValueHandler.curApi?.clone() as ApiBean?
        clone?.let {
            OB.apiBox.put(it)
            MyValueHandler.curApi = it
            mainPanel.cbApiUrl.insertItemAt(it, 0)
            mainPanel.cbApiUrl.selectedIndex = 0
        }
    }

    fun onSend() {
        if (MyValueHandler.curProject == null) {
            showErrorMsg("Please create the project first")
        } else {
            HttpManage.sendRequest()
        }
    }

    fun onDelBaseUrl(selectedItem: Any) {
        MyValueHandler.curProject?.baseUrlList?.remove(selectedItem)
        mainPanel.cbBaseUrl.removeItem(selectedItem)
        OB.projectBox.put(MyValueHandler.curProject)
    }

    fun onDelApiUrl(selectedItem: ApiBean) {
        MyValueHandler.curProject?.apis?.remove(selectedItem)
        mainPanel.cbApiUrl.removeItem(selectedItem)
        OB.apiBox.remove(selectedItem)
        MyValueHandler.curApi = MyValueHandler.curProject?.apis?.get(0)
    }

    fun onApiItemChanged(item: ApiBean) {
        MyValueHandler.curApi = item
    }

    fun onMethodChanged(index: Int) {
        if (MyValueHandler.curApi?.method != index) {
            MyValueHandler.curApi?.method = index
            if (MyValueHandler.curApi != null) {
                OB.apiBox.put(MyValueHandler.curApi)
            }
        }
    }

    fun onEncryptTypeChanged(typeCode: Int) {
        if (MyValueHandler.curApi?.encryptType != typeCode) {
            MyValueHandler.curApi?.encryptType = typeCode
            if (MyValueHandler.curApi != null) {
                OB.apiBox.put(MyValueHandler.curApi)
            }
        }
    }

    fun onClearParams() {
        MyValueHandler.curApi?.paramsMap?.clear()
        mainPanel.paramsTableModel.clear()
        if (MyValueHandler.curApi != null) {
            OB.apiBox.put(MyValueHandler.curApi)
        }
    }

    fun onStartTest() {
        val str = mainPanel.tvTestCount.text
        if (str.isNullOrEmpty()) {
            showErrorMsg("Test times cannot be empty")
            return
        }
        val count = str.toInt()
        if (count > 0) {
            mainPanel.pb.minimum = 0
            mainPanel.pb.value = 0
            mainPanel.pb.maximum = count
            mainPanel.lbStatus.text = "Stress testing..."
            val startTime = System.currentTimeMillis()
            val request = HttpManage.getRequest()
            request?.let {
                for (i in 0..count) {
                    if (i == count) {
                        HttpManage.sendTestRequest(it,true,count,startTime)
                    } else {
                        HttpManage.sendTestRequest(it,false)
                    }
                }
            }

        }
    }
}
package com.longforus.apidebugger

import com.longforus.apidebugger.bean.ApiBean
import javax.swing.DefaultComboBoxModel
import javax.swing.JTable

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
                apiBean.encryptType = mainPanel.selectedEncryptID
                apiBean.method = mainPanel.selectedMethodType
                apiBean.parameMap = getParameMap(mainPanel.tbParams)
                it.apis.add(0, apiBean)
                val model = mainPanel.cbApiUrl.model as DefaultComboBoxModel
                model.insertElementAt(apiBean, 0)
                MyValueHandler.curApi = apiBean
            } else {
                apiBean = selectedItem as ApiBean
                apiBean.encryptType = mainPanel.selectedEncryptID
                apiBean.method = mainPanel.selectedMethodType
                apiBean.parameMap = getParameMap(mainPanel.tbParams)
            }
            OB.apiBox.put(apiBean)
        }
    }

    fun getParameMap(jTable: JTable, isSave: Boolean = true): Map<String, String> {
        val map = HashMap<String, String>()
        for (i in 0..jTable.rowCount) {
            if (jTable.getValueAt(i, 1) == null) {
                break
            }
            if (jTable.getValueAt(i, 0) as Boolean || isSave) {
                map[jTable.getValueAt(i, 1) as String] = jTable.getValueAt(i, 2) as String
            }
        }
        return map
    }

    fun onNewApi() {
        val clone = MyValueHandler.curApi?.clone() as ApiBean?
        clone?.let {
            OB.apiBox.put(it)
            MyValueHandler.curProject?.apis?.add(it)
            MyValueHandler.curApi = it
            mainPanel.cbApiUrl.insertItemAt(it, 0)
            mainPanel.cbApiUrl.selectedIndex = 0
        }
    }

    fun onSend() {
        HttpManage.sendRequest()
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
}
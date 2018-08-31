package com.longforus.apidebugger

import com.longforus.apidebugger.bean.ApiBean
import javax.swing.DefaultComboBoxModel

/**
 * Created by XQ Yang on 8/31/2018  11:21 AM.
 * Description :
 */
object UIActionHandler{


    fun onSaveBaseUrl(selectedItem: Any) {
        MyValueHandler.curProject?.let {
            if (!it.baseUrlList.contains(selectedItem as String)) {
                it.baseUrlList.add(0,selectedItem)
                MyValueHandler.curBaseUrl = selectedItem
                OB.projectBox.put(it)
                val model = mainPanel.cbBaseUrl.model as DefaultComboBoxModel
                model.insertElementAt(selectedItem,0)
            }
        }

    }

    fun onSaveApi(selectedItem: Any) {
        MyValueHandler.curProject?.let {
            val bean  = if (selectedItem is String) ApiBean(selectedItem) else selectedItem as ApiBean
            if (it.apis.contains(bean)) {

            } else {
                it.apis.add(0,bean)
                val model = mainPanel.cbApiUrl.model as DefaultComboBoxModel
                model.insertElementAt(bean,0)
            }

            OB.projectBox.put(it)

        }
    }

    fun onNewApi() {

    }

    fun onSend() {

    }

    fun onDelBaseUrl(selectedItem: Any) {

    }

    fun onDelApiUrl(selectedItem: Any) {

    }
}
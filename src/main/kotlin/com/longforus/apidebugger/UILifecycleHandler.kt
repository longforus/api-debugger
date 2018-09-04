package com.longforus.apidebugger

import com.longforus.apidebugger.MyValueHandler.curApi
import com.longforus.apidebugger.MyValueHandler.curProject
import com.longforus.apidebugger.bean.ApiBean
import com.longforus.apidebugger.bean.ProjectBean
import com.longforus.apidebugger.bean.ProjectBean_
import com.longforus.apidebugger.ui.MainPanel
import io.objectbox.kotlin.query
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * Created by XQ Yang on 8/31/2018  11:23 AM.
 * Description :
 */
object UILifecycleHandler {

    val cacheMenu = mutableMapOf<String,JMenuItem>()

    fun onResume(mainPanel: MainPanel) {
        mainPanel.cbEncrypt.model = DefaultComboBoxModel(MyValueHandler.encryptImplList.toTypedArray())
        val allProject = OB.projectBox.query().build().find()
        if (allProject.isNotEmpty()) {
            curProject = allProject.last()
        }
    }

    fun initProject(it: ProjectBean, mainPanel: MainPanel) {
        mainPanel.title= " $appName    Current Project : ${it.name}"
        mainPanel.cbBaseUrl.model = DefaultComboBoxModel(it.baseUrlList.toTypedArray())
        if (it.baseUrlList.isNotEmpty()) {
            MyValueHandler.curBaseUrl = it.baseUrlList.last()
        }
        val apis = it.apis
        mainPanel.cbApiUrl.model = DefaultComboBoxModel(apis.toTypedArray())
        curApi = if (apis.isNotEmpty()) {
            apis[0]
        } else {
            null
        }
    }

    fun initApi(api: ApiBean?){
        if (api == null) {
            mainPanel.cbEncrypt.selectedIndex = 0
            mainPanel.cbMethod.selectedIndex = 0
            mainPanel.tbParams.model = DefaultTableModel(arrayOf( "key", "value"), MyValueHandler.PARAME_TABLE_ROW_COUNT)
        } else {
            val id2Index = MyValueHandler.encryptId2Index(api.encryptType)
            mainPanel.cbEncrypt.selectedIndex = id2Index
            mainPanel.cbMethod.selectedIndex = api.method
            if (api.parameMap.isEmpty()) {
                mainPanel.tbParams.model = DefaultTableModel(arrayOf( "key", "value"), MyValueHandler.PARAME_TABLE_ROW_COUNT)
            } else {
//                api.parameMap.entries.forEachIndexed { index, entry ->
//                    mainPanel.tbParame.model.setValueAt(entry.key, index, 1)
//                    mainPanel.tbParame.model.setValueAt(entry.value, index, 2)
//                }
                mainPanel.myTableModel.data = ApiBean.getTableVlaueList(api)
            }
        }
    }

    fun getMenuBar(): JMenuBar {
        val menuBar = JMenuBar()
        val pm = JMenu("Project")
        val item = JMenuItem("new")
        item.addActionListener {
            val projectName = JOptionPane.showInputDialog("Input Project Name")
            if (projectName.isNullOrEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "input error")
                return@addActionListener
            }
            val count = OB.projectBox.query {
                equal(ProjectBean_.name, projectName)
            }.count()
            if (count > 0) {
                showErrorMsg("Project existing")
            } else {
                val project = ProjectBean()
                project.name = projectName
                val newPro = JMenuItem(projectName)
                cacheMenu[projectName] = newPro
                newPro.addActionListener {_->
                    curProject = project
                }
                pm.insert(newPro,2)
                OB.projectBox.put(project)
                curProject = project
            }
        }
        pm.add(item)
        pm.addSeparator()
        OB.projectBox.query().build().find().sortedByDescending { it.id }.forEach {pro->
            val tempItem = JMenuItem(pro.name)
            cacheMenu[pro.name] = tempItem
            tempItem.addActionListener {
                curProject = pro
            }
            pm.add(tempItem)
        }
        pm.addSeparator()
        val deleteItem = JMenuItem("delete current open project")
        deleteItem.addActionListener {
            if (curProject != null) {
                OB.projectBox.remove(curProject)
                val jMenuItem = cacheMenu.remove(curProject?.name)
                pm.remove(jMenuItem)
                val mutableList = OB.projectBox.query().build().find()
                if (mutableList.isNotEmpty()) {
                    val last = mutableList.last()
                    if (last != null) {
                        curProject = last
                    }
                }
            }
        }
        pm.add(deleteItem)

        val am = JMenu("About")
        val item1 = JMenuItem("about")
        am.add(item1)
        item1.addActionListener {
            JOptionPane.showMessageDialog(null,"version 0.9  \nAuthor longforus  \nQQ  89082243 ","About",JOptionPane.INFORMATION_MESSAGE)
        }
        menuBar.add(pm)
        menuBar.add(am)
        return menuBar
    }
}
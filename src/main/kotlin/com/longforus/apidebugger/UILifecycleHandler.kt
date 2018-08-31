package com.longforus.apidebugger

import com.longforus.apidebugger.MyValueHandler.curApi
import com.longforus.apidebugger.MyValueHandler.curProject
import com.longforus.apidebugger.MyValueHandler.encrcyptId2Index
import com.longforus.apidebugger.MyValueHandler.encryptHandler
import com.longforus.apidebugger.bean.ApiBean
import com.longforus.apidebugger.bean.ProjectBean
import com.longforus.apidebugger.bean.ProjectBean_
import com.longforus.apidebugger.ui.MainPanel
import io.objectbox.kotlin.query
import javax.swing.*

/**
 * Created by XQ Yang on 8/31/2018  11:23 AM.
 * Description :
 */
object UILifecycleHandler {

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
        if (apis.isNotEmpty()) {
            curApi = apis.last()
        }
    }

    fun initApi(api: ApiBean?){
        api?.let {
            val id2Index = encrcyptId2Index(it.encryptType)
            mainPanel.cbEncrypt.selectedIndex = id2Index
            encryptHandler = MyValueHandler.encryptImplList[id2Index]
            it.parameMap.entries.forEachIndexed { index, entry ->
                mainPanel.tbParame.model.setValueAt(entry.key, index, 0)
                mainPanel.tbParame.model.setValueAt(entry.value, index, 1)
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
                OB.projectBox.put(project)
                curProject = project
            }
        }
        pm.add(item)
        pm.addSeparator()
        OB.projectBox.query().build().find().forEach {pro->
            val tempItem = JMenuItem(pro.name)
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
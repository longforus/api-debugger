package com.longforus.apidebugger.bean

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

/**
 * Created by XQ Yang on 8/30/2018  5:41 PM.
 * Description :
 */
@Entity
data class ApiBean(@Id
var id: Long = 0,
    var method: Int = 0,
    @Index
    var url: String = "",
    @Convert(converter = MapDbConverter::class,
        dbType = String::class)
    var paramsMap: MutableMap<String, String> = HashMap(),
    var encryptType: Int = 0,var projectId:Long = 0): kotlin.Cloneable {

    constructor(url: String,projectId: Long) : this() {
        this.url = url
        this.projectId = projectId
    }



    companion object {
        fun getTableValueList(bean:ApiBean):MutableList<TableBean>{
            if (bean.paramsMap.isEmpty()) {
                return mutableListOf()
            }
            val list = mutableListOf<TableBean>()
            bean.paramsMap.forEach {
                list.add(TableBean(true,it.key,it.value))
            }
            return list
        }
    }


    override fun toString(): String {
        return url
    }


    public override fun clone(): Any {
        val map = HashMap<String,String>()
        paramsMap.forEach { t, u ->
            map[t] = u
        }
        return ApiBean(method = this.method,url = this.url+"新",paramsMap = map,encryptType = this.encryptType,projectId = this.projectId)
    }



}
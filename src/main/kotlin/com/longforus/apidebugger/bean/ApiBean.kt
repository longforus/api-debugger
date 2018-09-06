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
class ApiBean(@Id(assignable = true)
var id: Long = 0,
    var method: Int = 0,
    var url: String = "",
    @Convert(converter = MapDbConverter::class,
        dbType = String::class)
    var paramsMap: MutableMap<String, String> = HashMap(),
    var encryptType: Int = 0, @Index var projectId: Long = 0, val createDate: Long = System.currentTimeMillis()) : kotlin.Cloneable {

    constructor(url: String, projectId: Long) : this() {
        this.url = url
        this.projectId = projectId
        this.id = hashCode().toLong()
    }


    companion object {
        fun getTableValueList(bean: ApiBean): MutableList<TableBean> {
            if (bean.paramsMap.isEmpty()) {
                return mutableListOf()
            }
            val list = mutableListOf<TableBean>()
            bean.paramsMap.forEach {
                list.add(TableBean(selected= true,key =  it.key,value = it.value))
            }
            return list
        }
    }


    override fun toString(): String {
        return url
    }


    public override fun clone(): Any {
        val map = HashMap<String, String>()
        paramsMap.forEach { t, u ->
            map[t] = u
        }
        val bean = ApiBean(method = this.method, url = this.url + "新", paramsMap = map, encryptType = this.encryptType, projectId = this.projectId)
        bean.id = bean.hashCode().toLong()
        return bean
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApiBean

        if (url != other.url) return false
        if (projectId != other.projectId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + projectId.hashCode()
        return result
    }


}
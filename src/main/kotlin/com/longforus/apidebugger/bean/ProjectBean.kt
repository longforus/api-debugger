package com.longforus.apidebugger.bean

import io.objectbox.annotation.*

/**
 * Created by XQ Yang on 8/30/2018  5:40 PM.
 * Description :
 */
@Entity
class ProjectBean{
    @Id
    var id: Long = 0
    @Index
    var name:String = ""
    @Convert(converter = ListDbConverter::class,
        dbType = String::class)
    var baseUrlList:List<String> = listOf()
    @Backlink
    var apis:List<ApiBean> = listOf()

    override fun toString(): String {
        return name
    }
}
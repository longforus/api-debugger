package com.longforus.apidebugger.bean

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany

/**
 * Created by XQ Yang on 8/30/2018  5:40 PM.
 * Description :
 */
@Entity
data class ProjectBean(
    @Id
    var id: Long = 0,
    @Index
    var name: String = "",
    @Convert(converter = ListDbConverter::class,
        dbType = String::class)
    var baseUrlList: MutableList<String> = mutableListOf()) {

//    @Backlink(to = "project")
//    var apis: ToMany<ApiBean> = ToMany(this,ProjectBean_.apis)
    @Backlink(to = "project")
    var apis: ToMany<ApiBean> = ToMany(this,ProjectBean_.apis)
//    get() {if (field==null) mutableListOf<ApiBean>() else field}

    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectBean

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}
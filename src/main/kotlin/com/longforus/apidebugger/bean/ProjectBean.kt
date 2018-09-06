package com.longforus.apidebugger.bean

import com.longforus.apidebugger.OB
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.kotlin.query

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


    @Transient
    var apis: MutableList<ApiBean> = mutableListOf()
        get() {
            field.clear()
            field.addAll(OB.apiBox.query {
                equal(ApiBean_.projectId, id)
            }.find().sortedByDescending { apiBean -> apiBean.createDate })
            return field
        }


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
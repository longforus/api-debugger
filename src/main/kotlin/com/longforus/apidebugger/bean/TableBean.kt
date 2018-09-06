package com.longforus.apidebugger.bean

import com.longforus.apidebugger.MyValueHandler
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

/**
 * Created by XQ Yang on 9/4/2018  2:41 PM.
 * Description :
 */
@Entity
data class TableBean(@Id(assignable = true) var id: Long = 0, var selected: Boolean, var key: String, var value: String, @Index  var projectId: Long = 0) {

    constructor(s: Boolean, k: String, v: String) : this(selected = s, key = k, value = v)

    init {
        projectId = MyValueHandler.curProject?.id ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableBean

        if (key != other.key) return false
        if (value != other.value) return false
        if (projectId != other.projectId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + projectId.hashCode()
        return result
    }
}
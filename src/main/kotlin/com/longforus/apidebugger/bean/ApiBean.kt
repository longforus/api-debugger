package com.longforus.apidebugger.bean

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.relation.ToOne

/**
 * Created by XQ Yang on 8/30/2018  5:41 PM.
 * Description :
 */
@Entity
class ApiBean {
    @Id
    var id: Long = 0
    var method: Int = 0
    @Index
    var url: String = ""
    @Convert(converter = MapDbConverter::class,
        dbType = String::class)
    var parameMap: Map<String, String> = mapOf()
    var encryptType: Int = 0
    lateinit var project: ToOne<ProjectBean>

    override fun toString(): String {
        return url
    }
}
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
data class ApiBean(@Id var id: Long = 0, val method: Int = 0,@Index val url: String, @Convert(converter = MapDbConverter::class,
    dbType = String::class) var parameMap: Map<String, String>? = null, val encryptType: Int = 0)
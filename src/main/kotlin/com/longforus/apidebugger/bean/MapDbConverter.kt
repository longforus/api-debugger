package com.longforus.apidebugger.bean

import com.google.gson.reflect.TypeToken
import com.longforus.apidebugger.MyValueHandler
import io.objectbox.converter.PropertyConverter

/**
 * Created by XQ Yang on 8/30/2018  5:48 PM.
 * Description :
 */
class MapDbConverter : PropertyConverter<Map<String, String>, String> {

    override fun convertToEntityProperty(databaseValue: String?): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return MyValueHandler.mGson.fromJson(databaseValue, type)
    }

    override fun convertToDatabaseValue(entityProperty: Map<String, String>?): String {
      return MyValueHandler.mGson.toJson(entityProperty)
    }

}
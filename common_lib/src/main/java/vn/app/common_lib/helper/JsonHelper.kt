package vn.app.common_lib.helper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

object JsonHelper {

    val gson: Gson = GsonBuilder()
//        .serializeNulls() // Default Gson will ignore null values on both serialize and deserialize
        .create()

    fun <T> saveObject(obj: T): String {
        return gson.toJson(obj)
    }

    fun <T> getObject(json: String?, clazz: Class<T>): T? {
        return try {
            if (json.isNullOrBlank().not()) Gson().fromJson(json, clazz) else null
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    inline fun <reified T> saveList(list: List<T>, clazz: Class<T>): String {
        return gson.toJson(list.toTypedArray(), TypeToken.getArray(clazz).type)
    }

    fun <T> getList(json: String?, clazz: Class<T>): List<T> {
        return try {
            if (json.isNullOrBlank().not()) {
                Gson().fromJson<Array<T>>(json, TypeToken.getArray(clazz).type).toList()
            } else listOf()
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        }
    }

}
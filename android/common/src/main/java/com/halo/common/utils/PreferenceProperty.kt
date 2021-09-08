/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/** using :
private var timerEndResend: Long by longPreference(
TIMER_END,
0LM
)
 * */

object SharedConfig {
    const val APP_PREF_NAME = "MODEL_ENTITY"
    const val APP_PREF_MODE = Context.MODE_PRIVATE
}

private class PreferenceProperty<T>(
    private val key: String,
    private val defaultValue: T,
    private val getter: SharedPreferences.(String, T) -> T,
    private val setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
) : ReadWriteProperty<Context, T> {

    override fun getValue(thisRef: Context, property: KProperty<*>): T =
        thisRef.getPreferences()
            .getter(key, defaultValue)

    override fun setValue(thisRef: Context, property: KProperty<*>, value: T) =
        thisRef.getPreferences()
            .edit()
            .setter(key, value)
            .apply()

    private fun Context.getPreferences(): SharedPreferences =
        getSharedPreferences(
            SharedConfig.APP_PREF_NAME,
            SharedConfig.APP_PREF_MODE
        )
}

fun intPreference(key: String, defaultValue: Int = 0): ReadWriteProperty<Context, Int> =
    PreferenceProperty(
        key = key,
        defaultValue = defaultValue,
        getter = SharedPreferences::getInt,
        setter = SharedPreferences.Editor::putInt
    )

fun longPreference(key: String, defaultValue: Long = 0): ReadWriteProperty<Context, Long> =
    PreferenceProperty(
        key = key,
        defaultValue = defaultValue,
        getter = SharedPreferences::getLong,
        setter = SharedPreferences.Editor::putLong
    )

fun booleanPreference(
    key: String,
    defaultValue: Boolean = false
): ReadWriteProperty<Context, Boolean> =
    PreferenceProperty(
        key = key,
        defaultValue = defaultValue,
        getter = SharedPreferences::getBoolean,
        setter = SharedPreferences.Editor::putBoolean
    )


fun stringPreference(
    key: String,
    defaultValue: String? = null
): ReadWriteProperty<Context, String?> =
    PreferenceProperty(
        key = key,
        defaultValue = defaultValue,
        getter = SharedPreferences::getString,
        setter = SharedPreferences.Editor::putString
    )


interface InteractionPreference {
    fun <T : Any> saveDataModel(vararg data: T)
    fun <T> getDataModel(data: Class<T>): T?
    fun setContext(context: Context?) {}
    fun remove() {}
    fun removeAll() {}
}


class ModelPreference(var context: Context? = null) :
    ReadWriteProperty<Any, InteractionPreference> {
    var key: String = "MODEL_DATA"
    private var defaultValue: String = ""
    private var ct: Context? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): InteractionPreference {
        (thisRef as? Context)?.apply {
            ct = this
        }
        (thisRef as? Fragment)?.apply {
            ct = this.context
        }
        return object : InteractionPreference {
            override fun <T : Any> saveDataModel(vararg data: T) {
                val listData = mutableListOf<Pair<String, String>>()
                data.forEach { _data ->
                    listData.add(
                        Pair(
                            _data::class.java.toString(),
                            Gson().toJson(_data)
                        )
                    )
                }
                ct?.getPreferences()?.edit()?.putString(key, Gson().toJson(listData))?.apply()
                //Log.i("===", "save done : ${Gson().toJson(listData)}")
            }

            override fun <T> getDataModel(data: Class<T>): T? {
                fun convertJsonToList(json: JSONArray?): List<JSONObject> {
                    val list = mutableListOf<JSONObject>()
                    json?.apply {
                        for (_index in 0 until length()) {
                            list.add(getJSONObject(_index))
                        }
                    }
                    return list
                }

                fun <T> convertJsonToObject(json: String, classOfT: Class<T>): T {
                    return Gson().fromJson(json, classOfT)
                }
                return try {
                    val jsonArray = convertJsonToList(
                        JSONArray(ct?.getPreferences()?.getString(key, defaultValue) ?: "")
                    )
                    val model = jsonArray.find { it.getString("first").contains(data.name, true) }
                    //Log.i("===", "save done : ${Gson().toJson(model)}")
                    convertJsonToObject(model?.getString("second").toString(), data)
                } catch (e: Exception) {
                    Log.i("===", "e.message :... " + e.message)

                    e.printStackTrace()
                    null
                }
            }
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: InteractionPreference) {
        //No_OP
    }

    private fun Context.getPreferences(): SharedPreferences = getSharedPreferences(
        SharedConfig.APP_PREF_NAME,
        SharedConfig.APP_PREF_MODE
    )

}


/*internal class CustomizedObjectTypeAdapter : TypeAdapter<Any?>() {
    private val delegate =
        Gson().getAdapter(
            Any::class.java
        )

    @Throws(IOException::class)
    fun write(out: JsonWriter?, value: Any) {
        delegate.write(out, value)
    }

    @Throws(IOException::class)
    fun read(`in`: JsonReader): Any? {
        val token = `in`.peek()
        return when (token) {
            JsonToken.BEGIN_ARRAY -> {
                val list: MutableList<Any?> = ArrayList()
                `in`.beginArray()
                while (`in`.hasNext()) {
                    list.add(read(`in`))
                }
                `in`.endArray()
                list
            }
            JsonToken.BEGIN_OBJECT -> {
                val map: MutableMap<String, Any?> =
                    LinkedTreeMap()
                `in`.beginObject()
                while (`in`.hasNext()) {
                    map[`in`.nextName()] = read(`in`)
                }
                `in`.endObject()
                map
            }
            JsonToken.STRING -> `in`.nextString()
            JsonToken.NUMBER -> {
                //return in.nextDouble();
                val n = `in`.nextString()
                if (n.indexOf('.') != -1) {
                    n.toDouble()
                } else n.toLong()
            }
            JsonToken.BOOLEAN -> `in`.nextBoolean()
            JsonToken.NULL -> {
                `in`.nextNull()
                null
            }
            else -> throw IllegalStateException()
        }
    }
}*/


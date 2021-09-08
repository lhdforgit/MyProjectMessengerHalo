package com.halo.widget.googleplace


import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.halo.common.utils.InteractionPreference
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


object HaloPlaceConfig {
    const val APP_PREF_NAME = "PLCACE_INFO"
    const val APP_PREF_MODE = Context.MODE_PRIVATE
}

class HaloPlacePreference(var context: Context? = null) :
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
                    convertJsonToObject(model?.getString("second").toString(), data)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: InteractionPreference) {
        //No_OP
    }

    private fun Context.getPreferences(): SharedPreferences = getSharedPreferences(
        HaloPlaceConfig.APP_PREF_NAME,
        HaloPlaceConfig.APP_PREF_MODE
    )
}
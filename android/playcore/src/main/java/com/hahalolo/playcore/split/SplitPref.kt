/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.playcore.split

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.hahalolo.playcore.language.LanguageApp

interface SplitPref {

    fun getCurrLanguageApp(): String

    fun setNewLanguageApp(code: String?)

    fun changeEnglish()

    fun changeVietnamese()

    fun withContext(context: Context)
}

object SharePreferenceImp : SplitPref {

    private const val LANGUAGE_PREF = "Language-pref"
    private const val LANGUAGE_PREF_CODE = "Language-pref-curr"
    private var sharePre: SharedPreferences? = null

    override fun getCurrLanguageApp(): String {
        return sharePre?.getString(
            LANGUAGE_PREF_CODE, ""
        ) ?: ""
    }

    override fun setNewLanguageApp(code: String?) {
        if (code != null) {
            sharePre?.apply {
                val editor: SharedPreferences.Editor = this.edit()
                editor.putString(LANGUAGE_PREF_CODE, code)
                editor.apply()
            }
        }
    }

    override fun changeEnglish() {
        sharePre?.apply {
            val editor: SharedPreferences.Editor = this.edit()
            editor.putString(
                LANGUAGE_PREF_CODE,
                LanguageApp.ENGLISH
            )
            editor.apply()
        }

    }

    override fun changeVietnamese() {
        sharePre?.apply {
            val editor: SharedPreferences.Editor = this.edit()
            editor.putString(
                LANGUAGE_PREF_CODE,
                LanguageApp.VIETNAMESE
            )
            editor.apply()
        }
    }

    override fun withContext(context: Context) {
        if (sharePre == null) {
            sharePre = context.getSharedPreferences(
                LANGUAGE_PREF, Context.MODE_PRIVATE
            )
        }
    }

    fun init(ctx: Context) {
        sharePre = ctx.getSharedPreferences(
            LANGUAGE_PREF, Context.MODE_PRIVATE
        )
    }

    fun saveString(value: String, key: String) {
        sharePre?.apply {
            val editor: SharedPreferences.Editor = this.edit()
            editor.putString(key, value)
            editor.apply()
            editor.commit()
        }
    }

    fun getString(key: String) = sharePre?.getString(key, "") ?: ""

    fun <T> getStringWtGson(key: String, classOff: Class<T>): T {
        val json = getString(key)
        return Gson().fromJson(json, classOff)
    }

    fun <T> putObjWtGson(key: String, t: T) {
        val json = Gson().toJson(t)
        saveString(json, key)
    }

    fun <T> putListObjWtGson(key: String, list: List<T>?) {
        val json = Gson().toJson(list)
        saveString(json, key)
    }

    inline fun <reified T> putString(key: String, list: List<T>?) {
        val json = Gson().toJson(list)
        saveString(json, key)
    }

    fun <T> getListWtGson(key: String, classOff: Class<T>): List<T> {
        try {
            val json = getString(key)
            val type = object : TypeToken<List<T>>() {}.type
            if (type != null) {
                return Gson().fromJson(json, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun <T> getObjectList(jsonString: String?, cls: Class<T>?): List<T>? {
        val list: MutableList<T> = ArrayList()
        try {
            val gson = Gson()
            val arry: JsonArray = JsonParser.parseString(jsonString).asJsonArray
            for (jsonElement in arry) {
                list.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun <T> getArrayList(key: String?, cls: Class<T>?): List<T>? {
        val list: MutableList<T> = ArrayList()
        return sharePre?.run {
            try {
                val json = getString(key, "")
                val gson = Gson()
                val array: JsonArray = JsonParser.parseString(json).asJsonArray
                for (jsonElement in array) {
                    list.add(gson.fromJson(jsonElement, cls))
                }
                list
            } catch (e: Exception) {
                e.printStackTrace()
                list
            }
        }
    }
}
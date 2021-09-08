package com.hahalolo.call.client.utils

import android.content.SharedPreferences


public const val SHARED_PREFS_NAME = "qb"
private const val QB_USER_LOGIN = "qb_user_login"
private const val QB_USER_PASSWORD = "qb_user_password"
private const val QB_USER_FULL_NAME = "qb_user_full_name"
private const val QB_USER_TAGS = "qb_user_tags"

object SharedPrefsHelper {

    fun delete(key: String, sharedPreferences: SharedPreferences) {
        if (sharedPreferences.contains(key)) {
            sharedPreferences.edit().remove(key).apply()
        }
    }

    fun save(key: String, value: Any?, sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        when {
            value is Boolean -> editor.putBoolean(key, (value))
            value is Int -> editor.putInt(key, (value))
            value is Float -> editor.putFloat(key, (value))
            value is Long -> editor.putLong(key, (value))
            value is String -> editor.putString(key, value)
            value is Enum<*> -> editor.putString(key, value.toString())
            value != null -> throw RuntimeException("Attempting to save non-supported preference")
        }
        editor.apply()
    }

    fun clearAllData(sharedPreferences: SharedPreferences ) {
        sharedPreferences.edit().clear().apply()
    }

    fun hasQbUser(sharedPreferences: SharedPreferences ): Boolean {
        return has(QB_USER_LOGIN, sharedPreferences) && has(QB_USER_PASSWORD, sharedPreferences)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String, sharedPreferences: SharedPreferences ): T {
        return sharedPreferences.all[key] as T
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String, defValue: T, sharedPreferences: SharedPreferences ): T {
        val returnValue = sharedPreferences.all[key] as T
        return returnValue ?: defValue
    }

    private fun has(key: String, sharedPreferences: SharedPreferences ): Boolean {
        return sharedPreferences.contains(key)
    }
}
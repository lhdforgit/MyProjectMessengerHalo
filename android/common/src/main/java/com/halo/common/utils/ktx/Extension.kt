package com.halo.common.utils.ktx

import com.google.gson.Gson

/**
 * Create by ndn
 * Create on 8/8/20
 * com.halo.common.utils.ktx
 */

fun Any?.serialize(clazz: Class<*>): String {
    return try {
        if (this == null) {
            ""
        } else Gson().toJson(this, clazz)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
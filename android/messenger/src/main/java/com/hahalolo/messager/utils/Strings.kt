/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.utils

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import java.text.Normalizer
import java.util.regex.Pattern

object Strings {
    fun deAccent(str: String?): String? {
        try {
            return str?.run {
                val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
                val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                pattern.matcher(nfdNormalizedString).replaceAll("")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun logPer(`object`: Any) {
        val elements = Thread.currentThread().stackTrace
        var method = ""
        method = elements[3].methodName
        val content = `object`.javaClass.simpleName + "::" + method + "():"
        log(content, "")
    }

    fun logPer(`object`: Any, o: Any?) {
        val elements = Thread.currentThread().stackTrace
        var method = ""
        method = elements[3].methodName
        val content = `object`.javaClass.simpleName + "::" + method + "():"
        log(content, o)
    }

    fun log(o: Any?) {
        log("", o)
    }

    fun log(message: String, o: Any?) {
        var json = ""
        json = if (o is String) {
            o
        } else {
            if (o == null) "NULL" else Gson().toJson(o)
        }
        val maxLogSize = 2600
        val partSize = json.length / maxLogSize
        if (partSize > 0) Log.d(
            "TESTAPP_ANHSONDAY",
            (if (TextUtils.isEmpty(message)) "" else "$message: ") + "Size: " + json.length
        )
        for (i in 0..partSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = Math.min(end, json.length)
            Log.d(
                "TESTAPP_ANHSONDAY",
                (if (TextUtils.isEmpty(message)) "" else "$message: ")
                        + (if (partSize > 0) "PART " + (i + 1) + ": " else "")
                        + json.substring(start, end)
            )
        }
    }
}
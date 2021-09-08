/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.log

import android.util.Log

object Log {
    var turnOn = true
    fun logTest(string: String?, isShow: Boolean) {
        if (!isShow) {
            return
        }
        try {
            val stElement = Thread.currentThread().stackTrace
            val className = stElement[3].className
            val lastIndex = className.lastIndexOf(".")
            val callerMethod = stElement[3].methodName
            if (lastIndex == -1) {
                Log.e("==${className}", "=> $callerMethod  =>>> $string")
            } else {
                val callerClass = className.subSequence(lastIndex + 1, className.length)
                Log.e("==${callerClass}", "=> $callerMethod  =>>> $string")
            }
        } catch (ex: Exception) {
            Log.e("==", "==> ${ex.message.toString()}")
        }
    }

    fun logTest(string: String?) {
        if (!turnOn) {
            return
        }
        try {
            val stElement = Thread.currentThread().stackTrace
            val className = stElement[3].className
            val lastIndex = className.lastIndexOf(".")
            val callerMethod = stElement[3].methodName
            val content = if (string.isNullOrBlank()) "" else "=>>> $string"
            if (lastIndex == -1) {
                Log.e("----${className}", "=> $callerMethod  $content")
            } else {
                val callerClass = className.subSequence(lastIndex + 1, className.length)
                Log.e("----${callerClass}", "=> $callerMethod  $content")
            }
        } catch (ex: Exception) {
            Log.e("----", "==> ${ex.message.toString()}")
        }
    }
}
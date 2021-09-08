/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson

const val PACKAGE = "com.halo"

enum class T {
    EDITOR,
    JOB,
    COMMON,
    TEST,
    MAP
}

private const val isLog = true

const val LOG_ENABLE = isLog
const val LOG_ENABLE_NORMAL = isLog

fun String.log() {
    if (!LOG_ENABLE_NORMAL) return
    Log.e("$$$$", this)
}

inline fun <reified T : Any> T.gsonLog(infix: String = "$$$$"): T? {
    if (!LOG_ENABLE) return this
    return runCatching {
        Log.e(infix, Gson().toJson(this))
        this
    }.getOrNull()
}

inline fun <reified T : Any> T.stringLog(infix: String = "$$$$"): T {
    if (!LOG_ENABLE) return this
    Log.e(infix, this.toString())
    return this
}

inline fun <reified T : Any> T?.stringLogNullable(infix: String = "$$$$"): T? {
    if (!LOG_ENABLE) return this
    Log.e(infix, this.toString())
    return this
}

inline fun <reified T : Any> T.toast(context: Context, infix: String = "$$$$"): T {
    if (!LOG_ENABLE) return this
    Toast.makeText(context,infix,Toast.LENGTH_SHORT).show()
    return this
}

fun CharSequence.wMessage(extras: CharSequence? = null): CharSequence {
    LogTrace.wMessage("$extras $this")
    return this
}

fun Any.wMessage(extras: CharSequence? = null) {
    LogTrace.wMessage("$extras {${this}}")
}

fun CharSequence.w(extras: CharSequence? = null): CharSequence {
    LogTrace.w("$extras {$this}")
    return this
}

object LogTrace {

    private const val isTest = isLog
    fun trace(t: T) {
        if (isTest) {
            try {
                val trace = Thread.currentThread().stackTrace[3]
                trace?.apply {
                    val indexClassName = trace.className.lastIndexOf(".")
                    val className =
                        trace.className.substring(indexClassName + 1, trace.className.length)
                    val methodName = trace.methodName
                    if (methodName == className || methodName == "<init>") {
                        Log.w("$t == ${insideLocalFunc(className)}", "----> Start Create Class ")
                    } else {
                        Log.w("$t == ${insideLocalFunc(className)}", "----> $methodName")
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    fun trace(t: T, string: String) {
        if (isTest) {
            try {
                val trace = Thread.currentThread().stackTrace[3]
                trace?.apply {
                    val indexClassName = trace.className.lastIndexOf(".")
                    val className =
                        trace.className.substring(indexClassName + 1, trace.className.length)
                    val methodName = trace.methodName
                    if (methodName == className || methodName == "<init>") {
                        Log.w(
                            "$t == ${insideLocalFunc(className)}",
                            "----> Start Create Class =>>> $string"
                        )
                    } else {
                        Log.w(
                            "$t == ${insideLocalFunc(className)}",
                            "----> $methodName =>>> $string"
                        )
                    }
                }
            } catch (e: Exception) {

            }
        }

    }

    private fun ignore(): List<StackTraceElement> {
        val stack = Thread.currentThread()
        return stack.stackTrace.filter {
            val stackS = it.methodName
            it.className.contains(PACKAGE) && (!stackS.contains("invoke")
                    && !stackS.contains("testStacktraceFull")
                    && !stackS.contains("w")
                    && !stackS.contains("ignore"))
        }.reversed()
    }

    fun w(vararg string: CharSequence) {
        if (isTest) {
            kotlin.runCatching {
                Log.e(T.MAP.toString(), "================================================")
                val point = ignore()
                point.forEach {
                    it.apply {
                        Log.e(
                            T.MAP.toString(),
                            "${String.format("(%s:%s)", fileName, lineNumber)}$methodName()"
                        )
                    }
                }
                if (string.isNotEmpty()) {
                    val stringBuilder = java.lang.StringBuilder()
                    string.forEach {
                        if (string.indexOf(it) == string.size - 1) {
                            stringBuilder.append(it)
                        } else {
                            stringBuilder.append(it).append("\n")
                        }
                    }
                    if (string.size == 1) {
                        Log.e(T.MAP.toString(), "Message = { $stringBuilder }")
                    } else {
                        Log.e(T.MAP.toString(), "Message = \n{ $stringBuilder }")
                    }
                }
            }
        }
    }

    fun wMessage(string: CharSequence) {
        Log.e(T.MAP.toString(), "================================================")
        Log.w(T.MAP.toString(), "Message = {$string}")
    }

    fun iMessage(string: CharSequence) {
        Log.i(T.MAP.toString(), "Message = {$string}")
    }

    fun wMessage(vararg string: CharSequence) {
        Log.e(T.MAP.toString(), "================================================")
        if (string.isNotEmpty()) {
            val stringBuilder = java.lang.StringBuilder()
            string.forEach {
                if (string.indexOf(it) == string.size - 1) {
                    stringBuilder.append(it)
                } else {
                    stringBuilder.append(it).append("\n")
                }
            }
            if (string.size == 1) {
                Log.w(T.MAP.toString(), "Message = { $stringBuilder }")
            } else {
                Log.w(T.MAP.toString(), "Message = \n{ $stringBuilder }")
            }
        }
    }

    fun iMessage(vararg string: CharSequence) {
        if (string.isNotEmpty()) {
            val stringBuilder = java.lang.StringBuilder()
            string.forEach {
                if (string.indexOf(it) == string.size - 1) {
                    stringBuilder.append(it)
                } else {
                    stringBuilder.append(it).append("\n")
                }
            }
            if (string.size == 1) {
                Log.i(T.MAP.toString(), "Message = { $stringBuilder }")
            } else {
                Log.i(T.MAP.toString(), "Message = \n{ $stringBuilder }")
            }
        }
    }

    fun traceTest(string: CharSequence) {
        if (isTest) {
            try {
                val trace = Thread.currentThread().stackTrace[3]
                trace?.apply {
                    val indexClassName = trace.className.lastIndexOf(".")
                    val className =
                        trace.className.substring(indexClassName + 1, trace.className.length)
                    val methodName = trace.methodName
                    if (methodName == className || methodName == "<init>") {
                        Log.w(
                            "${T.TEST} == ${insideLocalFunc(className)}",
                            "----> Start Create Class =>>> $string"
                        )
                    } else {
                        val method = methodName.find { it == '$' }
                            ?.let { methodName.subSequence(0, methodName.indexOf(it)) }
                        Log.w(
                            "${T.TEST} == ${insideLocalFunc(className)}",
                            "----> ${method ?: methodName}  ${if (!string.isBlank()) "|| Message = $string" else ""} "
                        )
                    }
                }
            } catch (e: Exception) {

            }
        }

    }

    fun traceD(t: T, string: String) {
        kotlin.runCatching {
            val trace = Thread.currentThread().stackTrace[3]
            trace?.apply {
                val indexClassName = trace.className.lastIndexOf(".")
                val className =
                    trace.className.substring(indexClassName + 1, trace.className.length)
                val methodName = trace.methodName
                if (methodName == className || methodName == "<init>") {
                    Log.d(
                        "$t == ${insideLocalFunc(className)}",
                        "----> Start Create Class =>>> $string"
                    )
                } else {
                    Log.d(
                        "$t == ${insideLocalFunc(className)}",
                        "----> $methodName || Message = $string"
                    )
                }
            }
        }
    }

    fun traceE(t: T, string: String) {
        if (isTest) {
            try {
                val trace = Thread.currentThread().stackTrace[3]
                trace?.apply {
                    val indexClassName = trace.className.lastIndexOf(".")
                    val className =
                        trace.className.substring(indexClassName + 1, trace.className.length)
                    val methodName = trace.methodName
                    if (methodName == className || methodName == "<init>") {
                        Log.e(
                            "$t == ${insideLocalFunc(className)}",
                            "----> Start Create Class =>>> $string"
                        )
                    } else {
                        val method = methodName.find { it == '$' }?.let {
                            methodName.subSequence(
                                methodName.indexOf(it),
                                methodName.length - 1
                            )
                        }
                        Log.e(
                            "$t == ${insideLocalFunc(className)}",
                            "----> ${method ?: methodName} =>>> $string"
                        )
                    }
                }
            } catch (e: Exception) {

            }
        }
    }


    private fun insideLocalFunc(name: String): String {
        val index = name.indexOf("$")
        var nameSub = ""
        kotlin.runCatching {
            nameSub = if (index > -1) {
                name.substring(0, index)
            } else {
                name
            }
        }
        return nameSub
    }

    fun show(string: String) {
        try {
            val trace = Thread.currentThread().stackTrace[3]
            trace?.apply {
                val indexClassName = trace.className.lastIndexOf(".")
                val className =
                    trace.className.substring(indexClassName + 1, trace.className.length)
                val methodName = trace.methodName
                if (methodName == className || methodName == "<init>") {
                    Log.e(
                        "== ${insideLocalFunc(className)}",
                        "----> Start Create Class ==|| $string "
                    )
                } else {
                    Log.e("== ${insideLocalFunc(className)}", string)

                }
            }
        } catch (e: Exception) {

        }
    }

    fun show(first: String, string: String) {
        if (isTest) {
            try {
                val trace = Thread.currentThread().stackTrace[3]
                trace?.apply {
                    val indexClassName = trace.className.lastIndexOf(".")
                    val className =
                        trace.className.substring(indexClassName + 1, trace.className.length)
                    val methodName = trace.methodName
                    if (methodName == className || methodName == "<init>") {
                        Log.e(
                            "$first == ${insideLocalFunc(className)}",
                            "----> Start Create Class ==|| $string "
                        )
                    } else {
                        Log.e("$first ${insideLocalFunc(className)}", string)

                    }
                }
            } catch (e: Exception) {

            }
        }

    }
}

fun logTest(t: String) {
    LogTrace.trace(T.TEST, t)
}
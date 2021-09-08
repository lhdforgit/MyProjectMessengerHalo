package com.halo.widget.common

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.halo.constant.BuildConfig
import java.util.*


fun <T> T.Log(message: String = ""): T {
    //remove all log..
    if (BuildConfig.DEBUG) {
        var messenger = message
        (this as? Objects)?.apply {
            this::class.java.name.apply {
                if (!TextUtils.isEmpty(this)) {
                    messenger = "$this : "
                }
            }
        }
        logCat("$messenger ${Gson().toJson(this)}")
    }
    return this
}

fun <T> T.Log(message: String, handle: (it: T) -> String): T {
    handle(this).let { _result ->
        _result.Log(message)
    }
    return this
}

private fun logCat(messenger: String) {
    if (BuildConfig.DEBUG) {
        Log.i(tag(), messenger)
    }
}

private fun tag(): String? {
    fun getNumberStack(): Int {
        var index = 0
        Thread.currentThread().stackTrace.filterIndexed { _index, _item ->
            return@filterIndexed if (_item.fileName.equals(
                    "CommonUtils.kt",
                    ignoreCase = true
                )
            ) {
                index = _index
                true
            } else {
                false
            }
        }
        return index
    }
    return Thread.currentThread().stackTrace.getOrNull(getNumberStack())?.let { element ->
        "(${element.fileName}:${element.lineNumber})I/~~~"
    }
}

fun <T> T.toJson(): String {
    return Gson().toJson(this)
}

//
object VNCharacterUtils {
    private val SOURCE_CHARACTERS = charArrayOf(
        'À',
        'Á',
        'Â',
        'Ã',
        'È',
        'É',
        'Ê',
        'Ì',
        'Í',
        'Ò',
        'Ó',
        'Ô',
        'Õ',
        'Ù',
        'Ú',
        'Ý',
        'à',
        'á',
        'â',
        'ã',
        'è',
        'é',
        'ê',
        'ì',
        'í',
        'ò',
        'ó',
        'ô',
        'õ',
        'ù',
        'ú',
        'ý',
        'Ă',
        'ă',
        'Đ',
        'đ',
        'Ĩ',
        'ĩ',
        'Ũ',
        'ũ',
        'Ơ',
        'ơ',
        'Ư',
        'ư',
        'Ạ',
        'ạ',
        'Ả',
        'ả',
        'Ấ',
        'ấ',
        'Ầ',
        'ầ',
        'Ẩ',
        'ẩ',
        'Ẫ',
        'ẫ',
        'Ậ',
        'ậ',
        'Ắ',
        'ắ',
        'Ằ',
        'ằ',
        'Ẳ',
        'ẳ',
        'Ẵ',
        'ẵ',
        'Ặ',
        'ặ',
        'Ẹ',
        'ẹ',
        'Ẻ',
        'ẻ',
        'Ẽ',
        'ẽ',
        'Ế',
        'ế',
        'Ề',
        'ề',
        'Ể',
        'ể',
        'Ễ',
        'ễ',
        'Ệ',
        'ệ',
        'Ỉ',
        'ỉ',
        'Ị',
        'ị',
        'Ọ',
        'ọ',
        'Ỏ',
        'ỏ',
        'Ố',
        'ố',
        'Ồ',
        'ồ',
        'Ổ',
        'ổ',
        'Ỗ',
        'ỗ',
        'Ộ',
        'ộ',
        'Ớ',
        'ớ',
        'Ờ',
        'ờ',
        'Ở',
        'ở',
        'Ỡ',
        'ỡ',
        'Ợ',
        'ợ',
        'Ụ',
        'ụ',
        'Ủ',
        'ủ',
        'Ứ',
        'ứ',
        'Ừ',
        'ừ',
        'Ử',
        'ử',
        'Ữ',
        'ữ',
        'Ự',
        'ự'
    )
    private val DESTINATION_CHARACTERS = charArrayOf(
        'A',
        'A',
        'A',
        'A',
        'E',
        'E',
        'E',
        'I',
        'I',
        'O',
        'O',
        'O',
        'O',
        'U',
        'U',
        'Y',
        'a',
        'a',
        'a',
        'a',
        'e',
        'e',
        'e',
        'i',
        'i',
        'o',
        'o',
        'o',
        'o',
        'u',
        'u',
        'y',
        'A',
        'a',
        'D',
        'd',
        'I',
        'i',
        'U',
        'u',
        'O',
        'o',
        'U',
        'u',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'A',
        'a',
        'E',
        'e',
        'E',
        'e',
        'E',
        'e',
        'E',
        'e',
        'E',
        'e',
        'E',
        'e',
        'E',
        'e',
        'E',
        'e',
        'I',
        'i',
        'I',
        'i',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'O',
        'o',
        'U',
        'u',
        'U',
        'u',
        'U',
        'u',
        'U',
        'u',
        'U',
        'u',
        'U',
        'u',
        'U',
        'u'
    )

    @JvmStatic
    fun removeAccent(mCh: Char): Char {
        var ch = mCh
        val index = Arrays.binarySearch(SOURCE_CHARACTERS, mCh)
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index]
        }
        return ch
    }

    @JvmStatic
    fun removeAccent(str: String): String {
        val sb = StringBuilder(str)
        for (i in sb.indices) {
            sb.setCharAt(i, removeAccent(sb[i]))
        }
        return sb.toString()
    }
}

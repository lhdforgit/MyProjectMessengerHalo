/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.felling.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.halo.widget.sticker.R
import kotlinx.android.parcel.Parcelize

@Parcelize
class FeelingEntity(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("code")
    var code: String? = null,
    @SerializedName("utf_8")
    var utf_8: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("langs")
    var langs: List<FeelingLang>? = null
) : Parcelable {

    @SuppressLint("DefaultLocale")
    fun getContentSearch(): String {
        var content = ""
        if (langs != null && !langs!!.isEmpty()) {
            for (feelingLang in langs!!) {
                content = content + feelingLang.value + " "
            }
        }
        return content.toUpperCase()
    }

    fun getContent(context: Context): String {
        val content = ""
        val language = context.getString(R.string.editor_felling_language)
        if (langs != null && langs!!.isNotEmpty()) {
            for (feelingLang in langs!!) {
                if (TextUtils.equals(language, feelingLang.key)) {
                    return feelingLang.value!!
                }
            }
            return langs!![0].value!!
        }
        return content
    }

    fun utf8Decode(): String {
        return try {
            val utf = Integer.decode(utf_8 ?: "")
            String(Character.toChars(utf))
        } catch (e: Exception) {
            utf_8 ?: ""
        }
    }
}
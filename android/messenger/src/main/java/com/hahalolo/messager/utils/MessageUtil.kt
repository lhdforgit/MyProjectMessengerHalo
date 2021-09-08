/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.utils

import android.content.Context
import android.text.TextUtils
//import com.hahalolo.messager.mqtt.model.type.MessageType
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.halo.common.utils.DateUtils
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.room.type.MessageType
import java.text.SimpleDateFormat
import java.util.*

object MessageUtil {

    fun getTimeString(date: Date?, code: String): String {
        val now = Calendar.getInstance()
        val stringFormat = when {
            DateUtils.isToday(date) -> {
                ("HH:mm")
            }
            DateUtils.isDateInCurrentWeek(date) -> {
                ("E HH:mm")
            }
            DateUtils.getDiffYears(date, now.time) != 0 -> {
                if (TextUtils.equals(code, com.hahalolo.playcore.language.LanguageApp.VIETNAMESE)) {
                    "dd MMMM, yyyy"
                } else {
                    "MMMM dd, yyyy"
                }
            }
            else -> {
                if (TextUtils.equals(code, com.hahalolo.playcore.language.LanguageApp.VIETNAMESE)) {
                    "dd MMMM"
                } else {
                    "MMMM dd"
                }
            }
        }
        val dateFM: SimpleDateFormat
        dateFM = if (TextUtils.isEmpty(code)) {
            SimpleDateFormat(stringFormat, Locale.getDefault())
        } else {
            SimpleDateFormat(stringFormat, Locale(code))
        }
        date?.let {
            return dateFM.format(it)
        }
        return ""
    }
    fun getLastMessage(roomEntity: ChannelEntity?, context: Context?, userIdToken: String?): String {
        val span = SpanBuilderUtil()
        return span.build()?.toString() ?: ""
    }
}
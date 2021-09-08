/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.IntDef
import com.halo.data.room.type.MessageStatus.Companion.ERROR
import com.halo.data.room.type.MessageStatus.Companion.SENDDING
import com.halo.data.room.type.MessageStatus.Companion.SENDED

@IntDef(SENDDING, ERROR, SENDED)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageStatus {
    companion object {
        const val ERROR = 0
        const val SENDDING = 1
        const val SENDED = 2
        const val TYPING = 3
    }
}
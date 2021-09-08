/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.MessageType.Companion.DELETED
import com.halo.data.room.type.MessageType.Companion.REVOKED
import com.halo.data.room.type.MessageType.Companion.STICKER
import com.halo.data.room.type.MessageType.Companion.SYSTEM
import com.halo.data.room.type.MessageType.Companion.TEXT
import com.halo.data.room.type.MessageType.Companion.WAVE

@StringDef(
    TEXT, DELETED, REVOKED,
    SYSTEM, STICKER, WAVE
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class MessageType {
    companion object {
        const val TEXT = "def"
        const val SYSTEM = "sys"

        const val WAVE = "wave"
        const val GIF = "gif"
        const val STICKER = "sticker"
        const val DELETED = "deleted"
        const val REVOKED = "revoked"
    }
}
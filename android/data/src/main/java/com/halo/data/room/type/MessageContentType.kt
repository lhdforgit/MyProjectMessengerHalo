/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.MessageContentType.Companion.CREATE_GROUP
import com.halo.data.room.type.MessageContentType.Companion.JOIN_GROUP
import com.halo.data.room.type.MessageContentType.Companion.NEW_INVITATION
import com.halo.data.room.type.MessageContentType.Companion.SAY_HI
import com.halo.data.room.type.MessageContentType.Companion.SEND_GIF
import com.halo.data.room.type.MessageContentType.Companion.SEND_MEDIA
import com.halo.data.room.type.MessageContentType.Companion.SEND_STICKER
import com.halo.data.room.type.MessageContentType.Companion.WANT_CONNECT
import com.halo.data.room.type.MessageType.Companion.DELETED
import com.halo.data.room.type.MessageType.Companion.REVOKED
import com.halo.data.room.type.MessageType.Companion.STICKER
import com.halo.data.room.type.MessageType.Companion.SYSTEM
import com.halo.data.room.type.MessageType.Companion.TEXT
import com.halo.data.room.type.MessageType.Companion.WAVE

@StringDef(
    SAY_HI, SEND_GIF, SEND_STICKER, SEND_MEDIA, NEW_INVITATION,
    CREATE_GROUP,
    WANT_CONNECT, JOIN_GROUP
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class MessageContentType {
    companion object {
        const val SAY_HI = "Hi"

        const val SEND_GIF = "{{actor}} sent a GIF"
        const val SEND_STICKER = "{{actor}} sent a sticker"
        const val SEND_MEDIA = "{{actor}} sent attachment(s)"

        const val NEW_INVITATION = "You got a new invitation"
        const val CREATE_GROUP = "{{actor}} created this group"
        const val WANT_CONNECT = "{{actor}} want to connect"
        const val JOIN_GROUP = "{{actor}} joined this group"
    }
}
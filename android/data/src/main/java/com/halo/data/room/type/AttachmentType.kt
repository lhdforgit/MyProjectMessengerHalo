/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.AttachmentType.Companion.DOCUMENT
import com.halo.data.room.type.AttachmentType.Companion.GIF
import com.halo.data.room.type.AttachmentType.Companion.IMAGE
import com.halo.data.room.type.AttachmentType.Companion.LINK
import com.halo.data.room.type.AttachmentType.Companion.LINKS
import com.halo.data.room.type.AttachmentType.Companion.MEDIA
import com.halo.data.room.type.AttachmentType.Companion.OTHER
import com.halo.data.room.type.AttachmentType.Companion.STICKER
import com.halo.data.room.type.AttachmentType.Companion.VIDEO

@StringDef(IMAGE, VIDEO, STICKER, GIF, LINKS, DOCUMENT, LINK, MEDIA, OTHER)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class AttachmentType {
    companion object {
        const val IMAGE = "image"
        const val VIDEO = "video"
        const val GIF = "gif"
        const val STICKER = "sticker"
        const val LINKS = "links"


        const val DOCUMENT = "document"
        const val LINK = "link"
        const val MEDIA = "media"
        const val OTHER = "other"
    }
}
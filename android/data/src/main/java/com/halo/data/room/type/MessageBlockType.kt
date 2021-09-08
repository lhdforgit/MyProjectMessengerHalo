/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.MessageBlockType.Companion.BLOCK_QUOTE
import com.halo.data.room.type.MessageBlockType.Companion.UNSTYLED

@StringDef(BLOCK_QUOTE, UNSTYLED)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class MessageBlockType {
    companion object {
        const val BLOCK_QUOTE = "blockquote"
        const val UNSTYLED = "unstyled"
    }
}
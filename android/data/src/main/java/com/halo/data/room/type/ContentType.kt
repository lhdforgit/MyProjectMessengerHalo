/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.ContentType.Companion.NOTIFICATION

@StringDef(NOTIFICATION)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ContentType {
    companion object {
        const val NOTIFICATION = "notification"
    }
}
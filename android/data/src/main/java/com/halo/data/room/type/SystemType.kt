/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.SystemType.Companion.CREATE_CHANNEL

@StringDef(CREATE_CHANNEL )
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class SystemType{
    companion object {
        const val CREATE_CHANNEL = "MSG_CREATE_CHANNEL"
    }
}
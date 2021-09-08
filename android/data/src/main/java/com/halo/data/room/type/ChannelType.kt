/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.ChannelType.Companion.GROUP
import com.halo.data.room.type.ChannelType.Companion.PRIVATE
import com.halo.data.room.type.ChannelType.Companion.PUBLIC

@StringDef(PRIVATE, GROUP, PUBLIC)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ChannelType {
    companion object {
        const val PRIVATE = "private"
        const val GROUP = "group"
        const val PUBLIC = "public"
    }
}

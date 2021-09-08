/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.type

import androidx.annotation.IntDef
import com.halo.data.room.type.RoleType.Companion.ADMIN
import com.halo.data.room.type.RoleType.Companion.MEMBER
import com.halo.data.room.type.RoleType.Companion.OWNER

@IntDef(ADMIN, MEMBER, OWNER)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class RoleType {
    companion object {
        const val OWNER = 0
        const val ADMIN = 1
        const val MEMBER = 2
    }
}

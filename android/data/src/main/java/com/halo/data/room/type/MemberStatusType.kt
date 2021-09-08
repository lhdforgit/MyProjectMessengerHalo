/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.MemberStatusType.Companion.ACTIVE
import com.halo.data.room.type.MemberStatusType.Companion.INACTIVE

@StringDef(ACTIVE, INACTIVE)
@Retention(AnnotationRetention.SOURCE)
annotation class MemberStatusType {
    companion object {
        const val ACTIVE = "active"
        const val INACTIVE = "inactive"
    }
}

/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.data.entities.contact

import androidx.annotation.StringDef
import kotlin.annotation.Retention
import com.halo.data.entities.contact.ContactType.Companion.BLOCKED
import com.halo.data.entities.contact.ContactType.Companion.CONTACTED
import com.halo.data.entities.contact.ContactType.Companion.PENDING

@StringDef(CONTACTED, BLOCKED, PENDING)
@Retention(AnnotationRetention.SOURCE)
annotation class ContactType {
    companion object {
        const val CONTACTED = "contacted"
        const val BLOCKED = "blocked"
        const val PENDING = "pending"
    }
}
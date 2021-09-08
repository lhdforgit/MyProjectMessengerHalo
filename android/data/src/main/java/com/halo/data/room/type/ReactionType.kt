/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.type

import androidx.annotation.StringDef
import com.halo.data.room.type.ReactionType.Companion.ANGRY
import com.halo.data.room.type.ReactionType.Companion.CRY
import com.halo.data.room.type.ReactionType.Companion.DISLiKE
import com.halo.data.room.type.ReactionType.Companion.HAHA
import com.halo.data.room.type.ReactionType.Companion.LIKE
import com.halo.data.room.type.ReactionType.Companion.LOVE
import com.halo.data.room.type.ReactionType.Companion.WOW

@StringDef(HAHA, LOVE, CRY, ANGRY, WOW, LIKE, DISLiKE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ReactionType {
    companion object {
        const val LOVE = "❤"
        const val HAHA = "😆"
        const val WOW = "😮"
        const val CRY = "😢"
        const val ANGRY = "😠"
        const val LIKE = "👍"
        const val DISLiKE = "👎"
    }
}

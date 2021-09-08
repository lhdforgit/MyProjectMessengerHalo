/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.felling.utils

import android.content.Context
import com.halo.widget.felling.model.FeelingEntity
import com.halo.widget.felling.repository.EditorFeelingRepositoryImpl.Companion.getInstance

object FeelingUtils {
    fun getFeelingModel(
        context: Context?,
        code: String?
    ): FeelingEntity? {
        return context?.run {
            getInstance(this).getFeelingModel(code)
        }
    }

    @JvmStatic
    fun isStickerCard(stickerName: String?): Boolean {
        return stickerName?.contains("hahalolo.com/sticker/rat_card") == true
                || stickerName?.contains("gifcard") == true
    }
}
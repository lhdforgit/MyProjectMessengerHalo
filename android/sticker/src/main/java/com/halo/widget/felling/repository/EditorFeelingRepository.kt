/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.felling.repository

import androidx.lifecycle.LiveData
import com.halo.widget.felling.model.FeelingEntity
import com.halo.widget.felling.model.LastStickerEntity
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.felling.model.StickerPackEntity

interface EditorFeelingRepository {

    fun feelingList(query: String?): LiveData<MutableList<FeelingEntity>>

    fun stickerPackList(): List<StickerPackEntity>

    fun getFeelingModel(code: String?): FeelingEntity?

    fun addLastStickerEntity(stickerEntity: StickerEntity)

    fun getLastStickerEntity():LiveData<MutableList<LastStickerEntity>>
}
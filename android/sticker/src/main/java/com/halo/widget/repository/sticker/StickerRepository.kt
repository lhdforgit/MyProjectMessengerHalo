/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.repository.sticker

import androidx.lifecycle.LiveData
import com.giphy.sdk.core.models.Media
import com.halo.widget.model.GifModel
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable

interface StickerRepository {

    fun stickerPacks(token: String? = null): LiveData<MutableList<StickerPackTable>>

    fun stickers(token: String? = null, packId: String): LiveData<MutableList<StickerTable>>

    fun gifs(token: String? = null, query: String?): LiveData<MutableList<GifModel>>

    fun clickSticker(stickerId: String)

    fun latestStickers(): LiveData<MutableList<StickerTable>>

    fun clickGif(media: Media)
}
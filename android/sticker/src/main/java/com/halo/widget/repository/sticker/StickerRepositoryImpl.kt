/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.repository.sticker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.giphy.sdk.core.models.Media
import com.halo.widget.api.GifService
import com.halo.widget.api.StickerService
import com.halo.widget.model.GifModel
import com.halo.widget.room.dao.StickerDao
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StickerRepositoryImpl @Inject
constructor(
    private val context: Context,
    private val stickerDao: StickerDao,
    private val stickerService: StickerService,
    private val gifService: GifService
) : StickerRepository {

    override fun stickerPacks(token: String?): LiveData<MutableList<StickerPackTable>> {
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                val result = stickerService.collections("Bearer $token")
                result?.takeIf { it.isNotEmpty() }?.mapNotNull {
                    StickerPackTable(
                        id = it.collectionId ?: ""
                    ).apply {
                        packIcon = it.location ?: ""
                        packUrl = it.location
                        packName = it.collectionName
                    }.takeIf { it.id.isNotEmpty() }
                }?.toMutableList()?.takeIf { it.isNotEmpty() }?.run {
                    stickerDao.insertPacks(this)
                }
            }.getOrElse {
            }
        }
        return stickerDao.stickerPacks()
    }

    override fun stickers(token: String?, packId: String): LiveData<MutableList<StickerTable>> {
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                val result =
                    stickerService.stickers(header = "Bearer $token", collectionId = packId)
                result?.takeIf { it.isNotEmpty() }?.mapNotNull {
                    StickerTable(it.stickerId ?: "").apply {
                        this.packId = it.collectionId ?: ""
                        stickerImage = it.location
                        stickerUrl = it.location
                    }.takeIf { it.id.isNotEmpty() }
                }?.toMutableList()?.takeIf { it.isNotEmpty() }?.run {
                    stickerDao.inserts(this)
                }
            }.getOrElse {

            }
        }
        return stickerDao.stickers(packId)
    }

    override fun gifs(token: String?, query: String?): LiveData<MutableList<GifModel>> {
        val result = MutableLiveData<MutableList<GifModel>>()
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                val response: MutableList<GifModel>? = query?.takeIf { it.isNotEmpty() }?.run {
                    gifService.search(header = "Bearer $token", query = this)
                } ?: kotlin.run {
                    gifService.trending(header = "Bearer $token")
                }
                result.postValue(response?: mutableListOf())
            }.getOrElse {
                result.postValue(mutableListOf())
            }
        }
        return result
    }

    override fun clickSticker(stickerId: String) {
        stickerDao.updateStickerTime(stickerId, System.currentTimeMillis())
    }

    override fun latestStickers(): LiveData<MutableList<StickerTable>> {
        return stickerDao.latestSticker()
    }

    override fun clickGif(media: Media) {
        stickerDao.insert(StickerTable(media.id).apply {
            stickerImage = media.images.fixedHeightSmall.gifUrl ?: ""
            stickerUrl = media.images?.original?.gifUrl
            stickerTime = System.currentTimeMillis()
            stickerType = 2
        })
    }
}
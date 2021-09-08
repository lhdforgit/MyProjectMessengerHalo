/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.scribbles.sticker

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.halo.widget.felling.model.StickerPackEntity
import com.halo.widget.felling.repository.EditorFeelingRepository
import com.halo.widget.felling.repository.EditorFeelingRepositoryImpl
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/4/20
 * com.halo.editor.scribbles.sticker
 */

class ImageEditorStickerSelectViewModel
@Inject constructor() : ViewModel() {

    fun getListPack(context: Context): MutableLiveData<List<StickerPackEntity>> {
        val repository: EditorFeelingRepository = EditorFeelingRepositoryImpl.getInstance(context)
        val list: MutableLiveData<List<StickerPackEntity>> = MutableLiveData()
        list.value = repository.stickerPackList()
        return list
    }
}
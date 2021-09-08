/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.mediaviewer

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.hahalolo.player.media.VolumeInfo
import com.halo.common.utils.list.ListUtils
import com.halo.data.entities.media.MediaEntity
import com.halo.presentation.base.AbsHandleViewModel
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 9/27/18
 */
class MediaViewerViewModel @Inject
internal constructor( )
    : AbsHandleViewModel() {


    var position = MutableLiveData<Int>()
    var listDataSet = MutableLiveData<List<MediaEntity>>()

    fun getCurrentMediaEntity(): MediaEntity? {
        val po = position.value ?: -1

        return listDataSet.value?.takeIf { it.size > po && po >= 0 }?.run {
            this[po]
        }
    }

    fun updatePosition(position: Int) {
        this.position.value = position
    }

    fun updateListDataSet(listDataSet: List<MediaEntity>) {
        this.listDataSet.value = listDataSet
    }

    fun getMediaEntity(id: String): MediaEntity? {
        return ListUtils.getDataPosition(listDataSet.value) { input -> input != null && TextUtils.equals(input.getId(), id) }
    }

    fun updateMediaEntity(mediaEntity: MediaEntity) {
        //update with medi not have path or path is thumb ,
        val result = ListUtils.update(mediaEntity, listDataSet.value,
                { input ->
                    (input != null
                            && TextUtils.equals(input.getId(), mediaEntity.getId())
                            && !TextUtils.equals(input.getPath(), mediaEntity.getPath()))
                })
        if (result) listDataSet.value = listDataSet.value
    }

    fun removeMediaEntity(id: String): Boolean {
        val listRemove = listDataSet.value ?: mutableListOf()
        val result = ListUtils.remove(listRemove
        ) { input -> input != null && TextUtils.equals(input.getId(), id) }
        listDataSet.value = listRemove
        return result
    }
    val timelineVolume = MutableLiveData(VolumeInfo(false, 1F))
}

/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main.group

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
//import com.hahalolo.messager.respository.room.ChatRoomRepository
import com.halo.data.room.entity.ChannelEntity
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */

class ChatHomeGroupViewModel @Inject internal constructor(
//    private val roomRepository: ChatRoomRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController){

    val query = MutableLiveData<String>("")
//    val groupPageList: LiveData<MutableList<ChannelEntity>>

    init {
//        groupPageList = Transformations.switchMap(query) {
//            return@switchMap roomRepository.getListRoomSearch(userIdToken(),it )
//        }
    }

    fun updateQuery(query: String?) {
        if (!TextUtils.equals(query ?: "", this.query.value ?: "")) {
            this.query.value = query ?: ""
        }
    }
}

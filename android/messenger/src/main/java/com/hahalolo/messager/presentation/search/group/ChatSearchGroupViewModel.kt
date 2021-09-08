/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
//import com.hahalolo.messager.respository.room.ChatRoomRepository
import com.halo.data.room.entity.ChannelEntity
import javax.inject.Inject

class ChatSearchGroupViewModel @Inject
constructor(
//    chatRoomRepository: ChatRoomRepository,
            appController: MessengerController,
) : AbsMessViewModel(appController) {

    private val query = MutableLiveData<String>()
//    val roomSocketList: LiveData<MutableList<ChannelEntity>>

//    init {
//        roomSocketList = Transformations.switchMap(query) { input ->
//            chatRoomRepository.getListRoomSearch(userIdToken(), input)
//        }
//        query.setValue("")
//    }

    internal fun updateQueryUser(s: String) {
        query.value = s
    }

}

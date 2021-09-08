/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.name

import androidx.lifecycle.LiveData
import com.hahalolo.messager.MessengerController
//import com.hahalolo.messager.mqtt.model.type.GroupSettingType
import com.hahalolo.messager.presentation.base.AbsMessViewModel
//import com.hahalolo.messager.respository.chat.ChatActType
//import com.hahalolo.messager.respository.chat.ChatRepository
//import com.hahalolo.messager.respository.room.ChatRoomRepository
import com.halo.data.common.resource.Resource

import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class ChatChangeGroupNameViewModel @Inject constructor(
//    private val chatRepository: ChatRepository,
//    private val roomRepository: ChatRoomRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {
    var roomId: String? = null
    private var oldName:  String? = null
    var updateRoomNameResponse: LiveData<Resource<String>>? = null

    init {

    }

    fun updateRoomName(newName: String) {
//        roomId?.let { roomId ->
//            updateRoomNameResponse =
//                roomRepository.updateSettingGroup(roomId, GroupSettingType.CHANGE_NAME, newName)
//        }
    }

    //check to connect
    fun onResumeChatAct(){
//        chatRepository.onResumeChatAct(ChatActType.CHAT_GROUP_NAME)
    }

    fun onFinishChatAct(){
//        chatRepository.onFinishChatAct(ChatActType.CHAT_GROUP_NAME)
    }
}

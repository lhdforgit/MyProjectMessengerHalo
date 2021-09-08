/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hahalolo.cache.setting.SettingPref
import com.hahalolo.messager.MessengerController
//import com.hahalolo.messager.mqtt.model.entity.Member
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.socket.SocketManager
//import com.hahalolo.messager.respository.chat.ChatActType
//import com.hahalolo.messager.respository.chat.ChatRepository
//import com.hahalolo.messager.respository.room.ChatRoomRepository
import com.halo.data.common.resource.Resource
import java.lang.reflect.Member
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatViewModel @Inject
internal constructor(
    private val settingPref: SettingPref,
    private val socketManager: SocketManager,
    appController: MessengerController,
) : AbsMessViewModel(appController) {
    var removeRoomId = MutableLiveData<String>()

    val socketState = socketManager.getSocketState()

    init {
    }

    fun removeConversation(roomId: String?) {
        roomId?.let {
            removeRoomId.value = it
        }
    }

    fun onUpdateStatusNotifyAction(status: Boolean, roomId: String?): LiveData<Resource<Member>>? {
//        roomId?.takeIf { it.isNotEmpty() }?.let {
//            return chatRoomRepository.updateToggleNotify(it, userIdToken(), status)
//        }
        return null
    }

    fun onLeaveGroup(newOwnerId: String?, roomId: String?): LiveData<Resource<String>>? {
//        roomId?.takeIf { it.isNotEmpty() }?.let {
//            return chatRoomRepository.leaveGroup(roomId, userIdToken(), newOwnerId)
//        }
        return null
    }

    //check to connect
    fun onResumeChatAct() {
//        chatRepository.onResumeChatAct(ChatActType.CHAT_HOME)
    }

    fun onFinishChatAct() {
//        chatRepository.onFinishChatAct(ChatActType.CHAT_HOME)
    }

    fun onPauseChartAct() {
//        chatRepository.onPauseChatAct(ChatActType.CHAT_HOME)
    }

    val isBubbleSettingOpen
        get() = settingPref.isBubbleOpen()

    val isShowRemindBubble
        get() = settingPref.isShowRemindBubble()

    fun turnOffBubblePermissionRemind() {
        settingPref.setBubblePermissionRemind(false)
    }

}

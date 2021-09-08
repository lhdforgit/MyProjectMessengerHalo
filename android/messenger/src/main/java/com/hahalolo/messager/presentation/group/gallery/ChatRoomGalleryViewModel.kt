/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery

import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import javax.inject.Inject

class ChatRoomGalleryViewModel @Inject constructor(
//    private val chatRepository: ChatRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    var roomId: String = ""
    var tabIndex: Int = 0

    //check to connect
    fun onResumeChatAct(){
//        chatRepository.onResumeChatAct(ChatActType.CHAT_GROUP_MEDIA)
    }

    fun onFinishChatAct(){
//        chatRepository.onFinishChatAct(ChatActType.CHAT_GROUP_MEDIA)
    }
}
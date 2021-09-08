package com.hahalolo.messager.bubble.conversation.message

import com.hahalolo.messager.bubble.UserTokenCallback
import com.halo.data.repository.tet.TetRepository

class ConversationMessageViewModel(
    private val userIdToken:UserTokenCallback,
//    private val groupRepository: ChatRoomRepository,
//    private val chatRepository: ChatRepository,
//    private val reactionRepository: ChatReactionRepository,
//    private val messageRepository: ChatMessageRepository,
    private val tetRepository: TetRepository
){

    init {

    }

    class CreateRoom {
        var groupId: String? = null
        var friendId: String? = null
    }
}
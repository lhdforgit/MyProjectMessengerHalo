package com.halo.data.repository.message

import androidx.lifecycle.LiveData
import com.halo.data.common.resource.Resource
import com.halo.data.entities.attachment.AttachmentBody
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.Member
import com.halo.data.entities.message.DeleteMessageBody
import com.halo.data.entities.message.Message
import com.halo.data.entities.message.MessageSendBody
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun messagePaging(
        token: String? = null,
        workspaceId: String = "0",
        channelId: String?,
        ownerId: String?
    ): LiveData<MessagePaging>

    suspend fun createMessage(body: MessageSendBody): Resource<Message>

    fun deleteMessage(ownerId: String, body: DeleteMessageBody) : Flow<Resource<Message>>

    //from socket
    fun insertNewMessage( token: String, ownerId: String, message: Message)

}
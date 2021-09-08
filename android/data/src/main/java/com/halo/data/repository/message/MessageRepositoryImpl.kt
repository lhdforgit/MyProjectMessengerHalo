package com.halo.data.repository.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.halo.data.AppDatabase
import com.halo.data.api.invite.InviteResApi
import com.halo.data.api.message.MessageRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.common.utils.Strings
import com.halo.data.entities.invite.InviteInfo
import com.halo.data.entities.message.DeleteMessageBody
import com.halo.data.entities.message.Message
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.room.type.SaveType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageRestApi: MessageRestApi,
    private val inviteRestApi: InviteResApi,
    private val appDataBase: AppDatabase,
) :
    MessageRepository {

    companion object {
        const val PAGING_SIZE = 20
    }

    override fun messagePaging(
        token: String?,
        workspaceId: String,
        channelId: String?,
        ownerId: String?
    ): LiveData<MessagePaging> {
        return MutableLiveData<MessagePaging>().apply {
            CoroutineScope(Dispatchers.IO).launch {
                appDataBase.messageDao().clearCache(channelId ?: "", PAGING_SIZE)
                postValue(
                    MessagePaging(
                        api = messageRestApi,
                        appDataBase = appDataBase,
                        token = token ?: "",
                        workspaceId = workspaceId,
                        channelId = channelId ?: "",
                        ownerId = ownerId ?: "",
                        pagingSize = PAGING_SIZE,
                        messageInfo = { message ->
                            loadMessageInfo(token = token ?: "", message)
                        },
                        refreshMessage = { token, ownerId, workspaceId, channelId, messageId ->
                            refreshMessage(token = token, ownerId = ownerId,
                                workspaceId = workspaceId,
                                channelId = channelId,
                                msgId = messageId)
                        }
                    )
                )
            }
        }
    }

    override suspend fun createMessage(
        body: MessageSendBody
    ): Resource<Message> =
        kotlin.runCatching {
            body.takeIf { it.editMsgId.isNullOrEmpty() }?.run {
                appDataBase.insertMessageSending(body)
            }
            val message: Message? = body.requestBody()?.run {
                //msg attachment
                messageRestApi.createMessage(
                    token = body.token,
                    workspaceId = body.workspaceId,
                    channelId = body.channelId,
                    path = this
                )
            } ?: body.textBody?.run {
                body.editMsgId?.takeIf { it.isNotEmpty() }?.run {
                    messageRestApi.editMessage(
                        token = body.token,
                        workspaceId = body.workspaceId,
                        channelId = body.channelId,
                        messageId = this,
                        body = body.textBody
                    )
                }?: body.quoteMessageId?.takeIf { it.isNotEmpty() }?.run {
                    //quote msg
                    messageRestApi.createMessageQuote(
                        token = body.token,
                        workspaceId = body.workspaceId,
                        channelId = body.channelId,
                        messageId = this,
                        body = body.textBody
                    )
                } ?: run {
                    //msg text
                    messageRestApi.createMessage(
                        token = body.token,
                        workspaceId = body.workspaceId,
                        channelId = body.channelId,
                        body = this
                    )
                }
            }
            message?.run {
                Strings.log("MessageContentUtils insertNewMessage ", this)
                insertNewMessage(body.token, body.ownerId, this)
                Resource.success(this)
            } ?: kotlin.run {
                insertSendErrorMessage(body.ref)
                Resource.error<Message>(500, "error", null)
            }
        }.getOrElse {
            insertSendErrorMessage(body.ref)
            Resource.error<Message>(it)
        }

    private fun insertSendErrorMessage(clientId: String) {
        appDataBase.messageDao().sendMessageError(clientId)
    }

    override fun insertNewMessage(token: String, ownerId: String, message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            loadMessageInfo(token = token, message = message)
            message.originMessageId?.takeIf { it.isNotEmpty() }?.run {
                refreshMessage(token = token, ownerId = ownerId,
                    workspaceId = message.workspaceId?:"",
                    channelId = message.channelId?:"",
                    msgId = this)
            }
            appDataBase.insertNewMessage(ownerId, message)
        }
    }

    private suspend fun refreshMessage(token: String, ownerId: String,
                                       workspaceId:String,
                                       channelId: String,
                                       msgId: String) {
        Strings.log("insertNewMessage refreshMessage 1 ")
        kotlin.runCatching {
            val quoteTable = appDataBase.messageDao().getMessage(messageId = msgId)
            if (quoteTable == null || quoteTable.msgSave != SaveType.NEW) {
                messageRestApi.messageDetail(
                    token = token,
                    workspaceId = workspaceId,
                    channelId = channelId,
                    messageId = msgId
                )?.run {
                    appDataBase.refreshMessage(ownerId, quoteTable, this)
                    Strings.log("insertNewMessage refreshMessage 2 ")
                }
            }
        }
        Strings.log("insertNewMessage refreshMessage 3 ")
    }

    override fun deleteMessage(ownerId: String, body: DeleteMessageBody): Flow<Resource<Message>> =
        flow {
            emit(Resource.loading<Message>(null))
            val resource = kotlin.runCatching {
                messageRestApi.deleteMessage(
                    deleteBody = body
                )?.run {
                    insertNewMessage(body.token ?: "", ownerId, this)
                    Resource.success(this)
                } ?: kotlin.run {
                    Resource.error<Message>(500, "errors", null)
                }
            }.getOrElse {
                Resource.error(it)
            }
            emit(resource)
        }.catch {
            Resource.error<Message>(it)
        }.flowOn(Dispatchers.IO)


    private suspend fun loadMessageInfo(token: String, message: Message) {
        //load info for message
        if (message.isInviteMsg()) {
            kotlin.runCatching {
                val inviteResponse = inviteRestApi.inviteInfo(token = token, message.inviteCode())
                inviteResponse?.channel?.let {
                    message.inviteInfo = InviteInfo(inviteResponse)
                }
            }.getOrElse {
                Strings.log("insertMessages loadMessageInfo error ", it)
            }
        }
    }
}
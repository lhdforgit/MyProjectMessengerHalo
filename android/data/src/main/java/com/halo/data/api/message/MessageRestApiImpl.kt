/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.message

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.message.DeleteMessageBody
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.entities.message.Message
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class MessageRestApiImpl @Inject
internal constructor() : MessageRestApi {

    override suspend fun messages(
        token: String,
        workspaceId: String ,
        channelId: String ,
        limit: Int,
        since: String?
    ): ResponsePaging<Message>? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = token
    ).messages(workspaceId,channelId, limit, since)

    override suspend fun editMessage(
        token: String,
        workspaceId: String,
        channelId: String,
        messageId: String,
        body: MessageSendBody.TextBody
    ): Message? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = token
    ).editMessage(workspaceId,channelId, messageId, body)

    override suspend fun createMessage(
        token: String,
        workspaceId: String,
        channelId: String,
        body: MessageSendBody.TextBody
    ): Message? = ServiceGenerator.createMessService(
    serviceClass = MessageService::class.java,
    authorization = token
    ).createMessage(workspaceId,channelId, body)

    override suspend fun createMessageQuote(
        token: String,
        workspaceId: String,
        channelId: String,
        messageId: String,
        body: MessageSendBody.TextBody
    ): Message? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = token
    ).createMessageQuote(workspaceId,channelId, messageId, body)

    override suspend fun createMessage(
        token: String,
        workspaceId: String,
        channelId: String,
        path: RequestBody
    ): Message? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = token
    ).createMessage(workspaceId = workspaceId, channelId = channelId, path = path)

    override suspend fun messageDetail(
        token: String,
        workspaceId: String,
        channelId: String,
        messageId: String
    ): Message? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = token
    ).messageDetail(workspaceId = workspaceId, channelId = channelId, messageId = messageId)

    override suspend fun deleteMessage(body: DeleteMessageBody): Message? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = body.token
    ).run {
        takeIf { body.revoke }?.run {
            revokeMessage( body.workspaceId?:"", body.channelId?:"", body.messageId?:"")
        }?: kotlin.run {
            hideMessage(body.workspaceId?:"", body.channelId?:"", body.messageId?:"")
        }
    }

    override suspend fun crawler(token: String, url: String): Any? = ServiceGenerator.createMessService(
        serviceClass = MessageService::class.java,
        authorization = token
    ).crawler(url)
}
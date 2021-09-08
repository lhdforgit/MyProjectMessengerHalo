/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.message

import com.halo.data.entities.message.DeleteMessageBody
import com.halo.data.entities.message.Message
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.entities.response.ResponsePaging
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @author ndn
 * Created by ndn
 * Created on 8/21/18
 */
interface MessageRestApi {
    suspend fun messages(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        limit: Int,
        since: String?
    ): ResponsePaging<Message>?

    suspend fun editMessage(
        token: String,
        workspaceId: String,
        channelId: String,
        messageId: String,
        body: MessageSendBody.TextBody
    ): Message?

    suspend fun createMessage(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        body: MessageSendBody.TextBody
    ): Message?

    suspend fun createMessageQuote(
        token: String,
        workspaceId: String = "0",
        channelId: String,
        messageId: String,
        body: MessageSendBody.TextBody
    ): Message?

    suspend fun createMessage(
        token: String,
        workspaceId: String,
        channelId: String,
        path: RequestBody
    ): Message?

    suspend fun messageDetail(
        token: String,
        workspaceId: String,
        channelId: String,
        messageId: String
    ): Message?

    suspend fun deleteMessage(deleteBody: DeleteMessageBody): Message?



    suspend fun crawler(
        token: String,
        url: String
    ): Any?
}

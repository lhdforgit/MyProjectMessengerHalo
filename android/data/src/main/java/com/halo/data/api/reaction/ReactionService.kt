/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.reaction

import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import retrofit2.http.*

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface ReactionService {

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/reactions")
    suspend fun reactionMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Body body: ReactionBody.Body
    ): Reactions?

    @DELETE("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/reactions")
    suspend fun deleteReaction(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
        @Body body: ReactionBody.Body
    ): Reactions?

    @POST("/v2/workspaces/{workspaceId}/channels/{channelId}/messages/{messageId}/reactions")
    suspend fun getReactionMessage(
        @Path("workspaceId") workspaceId: String,
        @Path("channelId") channelId: String,
        @Path("messageId") messageId: String,
    ): Message?
}

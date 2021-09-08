/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.reaction

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 9/6/18
 */
@Singleton
class ReactionRestApiImpl @Inject
internal constructor() : ReactionRestApi {
    override suspend fun reactionMessage(
        token: String,
        body: ReactionBody
    ): Reactions? = ServiceGenerator.createMessService(
        serviceClass = ReactionService::class.java,
        authorization = token
    ).reactionMessage(body.workspaceId,body.channelId, body.messageId , body.body)

    override suspend fun deleteReaction(
        token: String,
        body: ReactionBody
    ): Reactions? = ServiceGenerator.createMessService(
        serviceClass = ReactionService::class.java,
        authorization = token
    ).deleteReaction(body.workspaceId,body.channelId, body.messageId , body.body)

    override suspend fun getReactionMessage(
        token: String,
        body: ReactionBody
    ): Message? = ServiceGenerator.createMessService(
        serviceClass = ReactionService::class.java,
        authorization = token
    ).getReactionMessage(body.workspaceId, body.channelId, body.messageId)
}
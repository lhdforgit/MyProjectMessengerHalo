/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.reaction

import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import retrofit2.http.Path

/**
 * @author ndn
 * Created by ndn
 * Created on 8/21/18
 */
interface ReactionRestApi {

    suspend fun reactionMessage(
        token:String,
        body: ReactionBody
    ): Reactions?

    suspend fun deleteReaction(
        token:String,
        body: ReactionBody
    ): Reactions?

    suspend fun getReactionMessage(
        token:String,
        body: ReactionBody
    ): Message?
}

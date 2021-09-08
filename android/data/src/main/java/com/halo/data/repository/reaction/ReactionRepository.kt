package com.halo.data.repository.reaction

import com.halo.data.common.resource.Resource
import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import kotlinx.coroutines.flow.Flow

interface ReactionRepository {

    suspend fun reactionMessage(
        token:String,
        body: ReactionBody
    ): Flow<Resource<Reactions>>

    suspend fun deleteReaction(
        token:String,
        body: ReactionBody
    ): Flow<Resource<Reactions>>

    suspend fun getReactionMessage(
        token:String,
        body: ReactionBody
    ): Flow<Resource<Message>>
}
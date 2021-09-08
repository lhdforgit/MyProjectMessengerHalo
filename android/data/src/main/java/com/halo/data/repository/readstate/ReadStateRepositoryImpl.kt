package com.halo.data.repository.readstate

import com.halo.data.api.reaction.ReactionRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.entities.message.Message
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import com.halo.data.room.dao.MessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadStateRepositoryImpl @Inject constructor(
    private val reactionRestApi: ReactionRestApi,
    private val messageDao: MessageDao
) : ReadStateRepository {

}
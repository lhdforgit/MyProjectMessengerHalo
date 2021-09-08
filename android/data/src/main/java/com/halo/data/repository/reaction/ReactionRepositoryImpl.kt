package com.halo.data.repository.reaction

import com.halo.data.api.reaction.ReactionRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.common.utils.Strings
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
class ReactionRepositoryImpl @Inject constructor(
    private val reactionRestApi: ReactionRestApi,
    private val messageDao: MessageDao
) : ReactionRepository {
    override suspend fun reactionMessage(
        token: String,
        body: ReactionBody
    ): Flow<Resource<Reactions>> = flow {
        emit(Resource.loading<Reactions>(null))
        val resource = kotlin.runCatching {
            val reactions = if (body.delete){
                reactionRestApi.deleteReaction(
                    token = token,
                    body = body
                )
            }else {
                reactionRestApi.reactionMessage(
                    token = token,
                    body = body
                )
            }
            reactions?.run {
                messageDao.updateMessageReaction(
                    body.messageId,
                    this.takeIf { it.haveReaction() }?.toString() ?: ""
                )
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Reactions>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Reactions>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteReaction(
        token: String,
        body: ReactionBody
    ): Flow<Resource<Reactions>> = flow {
        emit(Resource.loading<Reactions>(null))
        val resource = kotlin.runCatching {
            reactionRestApi.deleteReaction(
                token = token,
                body = body
            )?.run {
                messageDao.updateMessageReaction(
                    body.messageId,
                    this.takeIf { it.haveReaction() }?.toString() ?: ""
                )
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Reactions>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Reactions>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun getReactionMessage(
        token: String,
        body: ReactionBody
    ): Flow<Resource<Message>> = flow {
        emit(Resource.loading<Message>(null))
        val resource = kotlin.runCatching {
            reactionRestApi.getReactionMessage(
                token = token,
                body = body,
            )?.run {
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
}
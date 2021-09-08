package com.halo.data.repository.invite

import com.halo.data.api.invite.InviteResApi
import com.halo.data.common.resource.Resource
import com.halo.data.entities.invite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class InviteRepositoryImpl @Inject constructor(private val api: InviteResApi) : InviteRepository {

    override suspend fun createUrlInvite(
        token: String,
        body: CreateInviteBody
    ): CreateInviteResponse? {
        return kotlin.runCatching {
            api.createInvite(token, body)
        }.getOrElse { null }
    }

    override suspend fun sendInviteUser(token: String, body: SendInviteBody): SendInviteResponse? {
        return kotlin.runCatching {
            api.sendInvite(token, body)
        }.getOrElse { null }
    }

    override suspend fun sendInviteFlow(
        token: String,
        body: SendInviteBody
    ): Flow<Resource<SendInviteResponse>>? = flow {
        emit(Resource.loading<SendInviteResponse>(null))
        val resource = kotlin.runCatching {
            api.sendInvite(token, body)?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<SendInviteResponse>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error<SendInviteResponse>(it)
        }
        emit(resource)
    }.catch {
        Resource.error<SendInviteResponse>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun inviteInfo(
        token: String,
        code: String
    ): Flow<Resource<InviteInfoResponse>> = flow {
        emit(Resource.loading<InviteInfoResponse>(null))
        val resource = kotlin.runCatching {
            api.inviteInfo(token, code)?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<InviteInfoResponse>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error<InviteInfoResponse>(it)
        }
        emit(resource)
    }.catch {
        Resource.error<InviteInfoResponse>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun inviteJoin(
        token: String,
        code: String
    ): Flow<Resource<InviteJoinResponse>> = flow {
        emit(Resource.loading<InviteJoinResponse>(null))
        val resource = kotlin.runCatching {
            api.inviteJoin(token, code)?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<InviteJoinResponse>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error<InviteJoinResponse>(it)
        }
        emit(resource)
    }.catch {
        Resource.error<InviteJoinResponse>(it)
    }.flowOn(Dispatchers.IO)
}
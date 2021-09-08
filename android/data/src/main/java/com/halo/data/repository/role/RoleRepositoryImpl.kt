package com.halo.data.repository.role

import com.halo.data.api.role.RoleRestApi
import com.halo.data.common.utils.Strings
import com.halo.data.entities.role.RoleChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepositoryImpl @Inject constructor(private val roleRestApi: RoleRestApi) :
    RoleRepository {

    override suspend fun getRoleChannel(
        token: String,
        workspaceId: String,
        channelId: String
    ): Flow<MutableList<RoleChannel>> = flow {
        val roleList = mutableListOf<RoleChannel>()
        var nextKey: String? = null
        var isHasNextPage = true
        kotlin.runCatching {
            while (isHasNextPage) {
                roleRestApi.getRoleChannel(
                    token = token,
                    workspaceId = workspaceId,
                    channelId = channelId,
                    since = nextKey,
                    limit = 10
                )?.apply {
                    isHasNextPage = this.isHasNextPage
                    elements?.takeIf { it.isNotEmpty() }?.let {
                        roleList.addAll(it)
                        nextKey = it.lastOrNull()?.roleId
                    }
                }
            }
        }.getOrElse {
            emit(mutableListOf<RoleChannel>())
        }
        emit(roleList)
    }.catch {
        emit(mutableListOf<RoleChannel>())
    }.flowOn(Dispatchers.IO)
}
package com.halo.data.repository.notification

import com.halo.data.api.notification.NotificationRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.entities.notification.NotificationDeregisterBody
import com.halo.data.entities.notification.NotificationRegisterBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl
@Inject constructor(
    val restApi: NotificationRestApi
) : NotificationRepository {

    override suspend fun register(
        body: NotificationRegisterBody
    ) = flow {
        emit(Resource.loading<Void>(null))
        val resource = kotlin.runCatching {
            restApi.register(
                body = body
            ).run {
                Resource.success<Void>(null)
            }
        }.getOrElse {
            Resource.error<Void>(it)
        }
        emit(resource)
    }.flowOn(Dispatchers.IO)

    override suspend fun deregister(
        body: NotificationDeregisterBody
    ) = flow {
        emit(Resource.loading<Void>(null))
        val resource = kotlin.runCatching {
            restApi.deregister(
                body = body
            ).run {
                Resource.success<Void>(null)
            }
        }.getOrElse {
            Resource.error<Void>(it)
        }
        emit(resource)
    }.flowOn(Dispatchers.IO)
}
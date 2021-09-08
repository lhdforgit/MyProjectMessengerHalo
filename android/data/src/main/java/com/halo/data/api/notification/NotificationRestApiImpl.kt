package com.halo.data.api.notification

import com.halo.data.api.utils.ServiceGenerator
import com.halo.data.entities.notification.NotificationDeregisterBody
import com.halo.data.entities.notification.NotificationRegisterBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRestApiImpl
@Inject constructor() : NotificationRestApi {

    private val service: NotificationService =
        ServiceGenerator.createService(NotificationService::class.java)

    override suspend fun register(
        body: NotificationRegisterBody
    ) = service.register(body = body.apply {
        Timber.i("Register Firebase Token $this")
    })

    override suspend fun deregister(
        body: NotificationDeregisterBody
    ) = service.deregister(body = body)
}
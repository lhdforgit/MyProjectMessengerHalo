/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.notification

import com.halo.data.entities.notification.NotificationDeregisterBody
import com.halo.data.entities.notification.NotificationRegisterBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

/**
 * @author ndn
 * Created by ndn
 * Created on 5/15/18.
 */
interface NotificationService {

    @POST("/v2/notifications/register")
    suspend fun register(
        @Body body: NotificationRegisterBody
    ): Void

    @DELETE("/v2/notifications/deregister")
    suspend fun deregister(
        @Body body: NotificationDeregisterBody
    ): Void
}
/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.repository.notification

import com.halo.data.common.resource.Resource
import com.halo.data.entities.notification.NotificationDeregisterBody
import com.halo.data.entities.notification.NotificationRegisterBody
import kotlinx.coroutines.flow.Flow

/**
 * @author ngannd
 * Create by ngannd on 24/05/2019
 */
interface NotificationRepository {

    suspend fun register(
        body: NotificationRegisterBody
    ): Flow<Resource<Void>>

    suspend fun deregister(
        body: NotificationDeregisterBody
    ): Flow<Resource<Void>>
}

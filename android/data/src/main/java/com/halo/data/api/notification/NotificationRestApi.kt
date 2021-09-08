/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.api.notification

import com.halo.data.entities.notification.NotificationDeregisterBody
import com.halo.data.entities.notification.NotificationRegisterBody

/**
 * @author ndn
 * Created by ndn
 * Created on 5/15/18.
 */
interface NotificationRestApi {

    suspend fun register(
        body: NotificationRegisterBody
    ): Void

    suspend fun deregister(
        body: NotificationDeregisterBody
    ): Void
}

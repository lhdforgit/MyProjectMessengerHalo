package com.halo.presentation.controller.module

import android.app.Application
import com.hahalolo.notification.controller.NotificationController
import com.halo.presentation.oauthInfo
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/11/21
 * com.halo.presentation.controller.module
 */
class NotificationControllerImpl
@Inject constructor(
    val appContext: Application
) : NotificationController {

    override val userId: String?
        get() = oauthInfo?.idUser
}
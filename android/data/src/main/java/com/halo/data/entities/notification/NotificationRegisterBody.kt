package com.halo.data.entities.notification

import com.google.gson.annotations.SerializedName
import com.halo.constant.HaloConfig.NOTIFICATION_APP_ID

/**
 * Create by ndn
 * Create on 6/23/21
 * com.halo.data.entities.notification
 */
data class NotificationRegisterBody(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("deviceId")
    var deviceId: String? = null,
    @SerializedName("appId")
    var appId: String = NOTIFICATION_APP_ID,
    @SerializedName("deviceToken")
    var deviceToken: String? = null,
    @SerializedName("workspaceChannels")
    var workspaceChannels: WorkspaceChannel? = null
)

data class NotificationDeregisterBody(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("appId")
    var appId: String = NOTIFICATION_APP_ID,
    @SerializedName("deviceToken")
    var deviceToken: String? = null
)

data class WorkspaceChannel(
    @SerializedName("workspaceId")
    var workspaceId: MutableList<String>? = null
)
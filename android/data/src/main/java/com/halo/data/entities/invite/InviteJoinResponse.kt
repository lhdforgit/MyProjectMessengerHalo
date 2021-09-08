package com.halo.data.entities.invite

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class InviteJoinResponse {
    @SerializedName("code")
    @Expose
    var code: String? = null

    @SerializedName("workspaceId")
    @Expose
    var workspaceId: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null

    @SerializedName("expiredAt")
    @Expose
    var expiredAt: String? = null

    @SerializedName("owner")
    @Expose
    var owner: String? = null

    @SerializedName("usageLimits")
    @Expose
    var usageLimits: Int = 0

    @SerializedName("userIds")
    @Expose
    var userIds: Any? = null

    @SerializedName("redirect")
    @Expose
    var redirect: String? = null
}
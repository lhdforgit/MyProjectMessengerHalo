package com.halo.data.entities.invite

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.Member
import com.halo.data.entities.user.User

class InviteInfoResponse {
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

    @SerializedName("channel")
    @Expose
    var channel: Channel? = null

    @SerializedName("memberPopulated")
    @Expose
    var memberPopulated: MutableList<Member>? = null

    @SerializedName("totalMembers")
    @Expose
    var totalMembers: Int = 0

    @SerializedName("joined")
    @Expose
    var joined: Boolean = false
}
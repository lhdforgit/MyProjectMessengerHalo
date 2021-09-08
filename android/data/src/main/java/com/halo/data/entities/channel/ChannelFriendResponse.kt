package com.halo.data.entities.channel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChannelFriendResponse {
    @SerializedName("workspaceId")
    @Expose
    var workspaceId: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("owner")
    @Expose
    var owner: String? = null

    @SerializedName("dmId")
    @Expose
    var dmId: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null
    @SerializedName("recipientId")
    @Expose
    var recipientId: String? = null
}
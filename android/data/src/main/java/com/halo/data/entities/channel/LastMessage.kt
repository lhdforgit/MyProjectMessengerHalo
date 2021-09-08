package com.halo.data.entities.channel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LastMessage {
    @SerializedName("workspaceId")
    @Expose
    var workspaceId: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("messageId")
    @Expose
    var messageId: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("owner")
    @Expose
    var owner: String? = null
}
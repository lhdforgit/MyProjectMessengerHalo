package com.halo.data.entities.channel

data class ChannelCreateResponse(
    var channelId: String? = null,
    var workspaceId: String? = null,
    var name: String? = null,
    var avatar: String? = null,
    var owner: String? = null,
    var createdAt: String? = null
)
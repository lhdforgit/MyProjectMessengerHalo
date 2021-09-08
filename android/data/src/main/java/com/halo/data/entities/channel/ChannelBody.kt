package com.halo.data.entities.channel

data class ChannelBody(
    var name: String? = null,
    var avatar: String? = null,
    var userIds: MutableList<String>? = null
)
package com.halo.data.entities.invite

data class CreateInviteBody(
    val channelId: String,
    val expiresIn: Int,
    val usageLimits: Int,
    val workspaceId: String
)
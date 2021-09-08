package com.halo.data.entities.invite

data class CreateInviteResponse(
    val channelId: String,
    val code: String,
    val createdAt: String,
    val expiredAt: String,
    val owner: String,
    val shortLink: String,
    val usageLimits: Int,
    val userIds: List<String>,
    val workspaceId: String
)
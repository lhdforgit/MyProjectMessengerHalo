package com.halo.data.entities.role

data class RoleChannel(
    val channelId: String,
    val color: Int,
    val mentionable: Boolean,
    val name: String,
    val permissions: String,
    val roleId: String,
    val workspaceId: String
)
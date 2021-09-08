package com.halo.data.entities.member

data class MemberRole(
    val color: Int,
    val mentionable: Boolean,
    val name: String,
    val permissions: String,
    val roleId: String
)
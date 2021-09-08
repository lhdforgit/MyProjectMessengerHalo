package com.halo.data.entities.reaction

class ReactionBody(
    val workspaceId: String,
    val channelId: String,
    val messageId: String,
    val emoji: String?=null,
    val delete: Boolean = false
) {
    val body: Body = Body(emoji?:"")

    class Body(
        val emoji: String = ""
    )
}
package com.halo.data.entities.invite

data class SendInviteResponse(
    val attachmentType: String,
    val attachments: List<Attachment>,
    val bucket: Int,
    val channelId: String,
    val content: String,
    val contentType: String,
    val createdAt: String,
    val embed: List<Any>,
    val messageId: String,
    val messageType: String,
    val metadata: Any,
    val sentAt: String,
    val updatedAt: String,
    val userId: String,
    val workspaceId: String
)
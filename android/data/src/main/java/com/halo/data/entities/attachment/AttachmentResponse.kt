package com.halo.data.entities.attachment

data class AttachmentResponse(
    val messageId: String?,
    val bucket: Int?,
    val channelId: String?,
    val workspaceId: String?,
    val userId: String?,
    val messageType: String?,
    val attachmentType: String?,
    val attachments: List<AttachmentEntity>?,
    val revokedAt: String?,
    val createdAt: String?,
    val sentAt: String?,
    val updatedAt: String?,
)
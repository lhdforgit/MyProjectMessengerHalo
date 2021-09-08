package com.halo.data.entities.attachment

data class AttachmentEntity(
    val fileUrl: String?,
    val originalame: String?,
    val size: Int?,
    val mimetype: String?,
    val type: String?
)
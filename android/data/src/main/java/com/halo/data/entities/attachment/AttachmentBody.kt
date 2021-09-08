package com.halo.data.entities.attachment

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

data class AttachmentBody(
    val token: String,
    var workspaceId: String = "0",
    var channelId: String,
    val ownerId: String,
    private var attachments: MutableList<String>,
    val fromUrl: String? = null,
) {
    val ref: String = UUID.randomUUID().toString()

    fun listAttachment(): MutableList<MultipartBody.Part> {
        return attachments.takeIf { it.isNotEmpty() }?.map { uriPath ->
            val attachmentFile = File(uriPath)
            val requestFile: RequestBody =
                attachmentFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                "attachments",
                attachmentFile.name,
                requestFile
            )
        }?.toMutableList() ?: mutableListOf()
    }

    fun refPart(): MultipartBody.Part {
        return MultipartBody.Part.createFormData("ref", ref)
    }

    fun requestBody(): RequestBody {
        return MultipartBody.Builder().setType(MultipartBody.FORM).apply {
            this.addFormDataPart("ref", ref)
            fromUrl?.takeIf { it.isNotEmpty() }?.let {
                this.addFormDataPart("fromUrl", it)
            }
            attachments.takeIf { it.isNotEmpty() }?.forEach { uriPath ->
                val attachmentFile = File(uriPath)
                val requestFile: RequestBody =
                    attachmentFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                this.addFormDataPart(
                    "attachments",
                    attachmentFile.name,
                    requestFile
                )
            }
        }.build()
    }

    fun attachmentModels(): MutableList<Attachment> {
        return attachments.map {
            Attachment().apply {
                this.url = it
            }
        }.toMutableList()
    }
}
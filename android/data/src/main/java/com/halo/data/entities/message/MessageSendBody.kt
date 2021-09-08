package com.halo.data.entities.message

import com.halo.data.entities.attachment.Attachment
import com.halo.data.room.type.AttachmentType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class MessageSendBody(
    val token: String,
    val workspaceId: String = "0",
    val channelId: String,
    val ownerId: String,
    val content: String? = null,
    val fromUrl: String? = null,
    val mentions: MutableList<String>?=null ,
    val quoteMessageId:String?=null ,
    val editMsgId:String?=null,
    private val attachmentType: String?=null ,
    private val attachments: MutableList<String>? = null
) {
    val ref: String = UUID.randomUUID().toString()

    fun requestBody(): RequestBody? {
        return fromUrl?.takeIf { it.isNotEmpty() }?.let {fromUrl->
            MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                this.addFormDataPart("ref", ref)
                this.addFormDataPart("fromUrl", fromUrl)
            }.build()
        }?: attachments?.takeIf { it.isNotEmpty() }?.let {attachments->
            MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                this.addFormDataPart("ref", ref)
                attachments.forEach { uriPath ->
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
    }

    fun attachmentModels(): MutableList<Attachment>? {
        return attachments?.takeIf { it.isNotEmpty() }?.map {
            Attachment().apply {
                this.url = it
            }
        }?.toMutableList() ?: fromUrl?.takeIf { it.isNotEmpty() }?.run {
            mutableListOf(Attachment().apply {
                this.url = fromUrl
                this.fileUrl = fromUrl
            })
        }
    }

    fun attachmentType():String{
        return attachmentType?.takeIf { it.isNotEmpty() }?: AttachmentType.IMAGE
    }

    val textBody: TextBody? = content?.takeIf { it.isNotEmpty() }?.run {
        TextBody(this, ref, mentions)
    }

    inner class TextBody(
        val content: String,
        val ref: String,
        val mentions:MutableList<String>?=null ,
        val originMessageId:String?=null
    )
}